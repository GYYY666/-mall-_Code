package com.imuke.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imuke.mall.dao.OrderItemMapper;
import com.imuke.mall.dao.OrderMapper;
import com.imuke.mall.dao.ProductMapper;
import com.imuke.mall.dao.ShippingMapper;
import com.imuke.mall.enums.OrderStatusEnum;
import com.imuke.mall.enums.PaymentTypeEnum;
import com.imuke.mall.enums.ProductStatusEnum;
import com.imuke.mall.enums.ResponseEnum;
import com.imuke.mall.pojo.*;
import com.imuke.mall.service.ICartService;
import com.imuke.mall.service.IOrderService;
import com.imuke.mall.vo.OrderItemVo;
import com.imuke.mall.vo.OrderVo;
import com.imuke.mall.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author guanyun
 * @since 2025/3/3 20:06
 */
@Service
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private ShippingMapper shippingMapper; //收获地址

    @Autowired
    private ICartService cartService;   //购物车

    @Autowired
    private ProductMapper productMapper; //商品

    @Autowired
    private OrderMapper orderMapper; //订单

    @Autowired
    private OrderItemMapper orderItemMapper; //订单项

    @Override
    @Transactional
    public ResponseVo<OrderVo> create(Integer uid, Integer shippingId) {
        //收货地址校验（需要查出来）
        Shipping shipping = shippingMapper.selectByUidAndShippingId(uid, shippingId);
        if (shipping == null){
            return ResponseVo.error(ResponseEnum.SHIPPING_NOT_EXIST);
        }

        //获取购物车，校验购物车（是否有商品、库存）
        List<Cart> cartList = cartService.listForCart(uid).stream()
                //只有当 getProduceSelected 返回 true 的购物车项才会被保留，其他项会被过滤掉。
                .filter(Cart::getProduceSelected)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(cartList)){
            return ResponseVo.error(ResponseEnum.CART_SELECTED_IS_EMPTY);
        }

        //获取购物车中选中的商品ID
        Set<Integer> productIdSet = cartList.stream()
                .map(Cart::getProductId)
                .collect(Collectors.toSet());
        List<Product> productList = productMapper.selectByProductIdSet(productIdSet);
        Map<Integer, Product> map = productList.stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        List<OrderItem> orderItemList = new ArrayList<>();
        Long orderNo = generateOrderNo();
        for(Cart cart : cartList){
            //根据商品id查询商品
            Product product = map.get(cart.getProductId());

            //是否有商品
            if (product == null){
                return ResponseVo.error(ResponseEnum.PRODUCT_NOT_EXIST,
                        "商品不存在  ProductId = "+ cart.getProductId());
            }
            //商品是否上架
            if (!ProductStatusEnum.ON_SALE.getCode() .equals(product.getStatus())){
                return ResponseVo.error(ResponseEnum.PROODUCT_STOCK_ERROR,
                        "商品不是在售状态  ProductId = "+ product.getName());
            }

            //库存是否充足
            if (product.getStock() < cart.getQuantity()){
                return ResponseVo.error(ResponseEnum.PROODUCT_STOCK_ERROR,
                        "库存不足  ProductId = "+ product.getName());
            }

            OrderItem orderItem = buildOrderItem(uid, orderNo, cart.getQuantity(), product);
            orderItemList.add(orderItem);

            //减少库存
            product.setStock(product.getStock() - cart.getQuantity());
            int row = productMapper.updateByPrimaryKeySelective(product);
            if(row <= 0){
                return ResponseVo.error(ResponseEnum.ERROR);
            }
        }
        //计算价格 总价、只计算被选中的商品
        //生成订单 入库 order表 order_item表
        Order order = buildOrder(uid, orderNo, shippingId, orderItemList);//创建订单

        int rowForOrder = orderMapper.insertSelective(order);
        if (rowForOrder <= 0){
            return ResponseVo.error(ResponseEnum.ERROR);
        }

        int rowForOrderItem = orderItemMapper.batchInsert(orderItemList);
        if (rowForOrderItem <= 0){
            return ResponseVo.error(ResponseEnum.ERROR);
        }



        //将选中的商品从购物车中删除
        //Redis有事务打包命令。不能回滚
        for(Cart cart : cartList){
            cartService.delete(uid, cart.getProductId());
        }

        //构造ordervo对象
        OrderVo orderVo = buildOrderVo(order, orderItemList, shipping);
        return ResponseVo.sucess(orderVo);

    }

    @Override
    public ResponseVo<PageInfo> list(Integer uid, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectByUid(uid);

        Set<Long> orderNoSet = orderList.stream()
				.map(Order::getOrderNo)
				.collect(Collectors.toSet());
		List<OrderItem> orderItemList = orderItemMapper.selectByOrderNoSet(orderNoSet);
		Map<Long, List<OrderItem>> orderItemMap = orderItemList.stream()
				.collect(Collectors.groupingBy(OrderItem::getOrderNo));

        //查收货地址id
		Set<Integer> shippingIdSet = orderList.stream()
				.map(Order::getShippingId)
				.collect(Collectors.toSet());
		List<Shipping> shippingList = shippingMapper.selectByIdSet(shippingIdSet);
		Map<Integer, Shipping> shippingMap = shippingList.stream()
				.collect(Collectors.toMap(Shipping::getId, shipping -> shipping));

		List<OrderVo> orderVoList = new ArrayList<>();
		for (Order order : orderList) {
			OrderVo orderVo = buildOrderVo(order,
					orderItemMap.get(order.getOrderNo()),
					shippingMap.get(order.getShippingId()));
			orderVoList.add(orderVo);
		}
		PageInfo pageInfo = new PageInfo<>(orderList);
		pageInfo.setList(orderVoList);

		return ResponseVo.sucess(pageInfo);

    }

    @Override
    public ResponseVo<OrderVo> detail(Integer uid, Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
		if (order == null || !order.getUserId().equals(uid)) {
			return ResponseVo.error(ResponseEnum.ORDER_NOT_EXIST);
		}
		Set<Long> orderNoSet = new HashSet<>(); //为了用orderNoSet
		orderNoSet.add(order.getOrderNo());
		List<OrderItem> orderItemList = orderItemMapper.selectByOrderNoSet(orderNoSet);

		Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId()); //查地址
		OrderVo orderVo = buildOrderVo(order, orderItemList, shipping);
		return ResponseVo.sucess(orderVo);

    }

    @Override
    public ResponseVo cancel(Integer uid, Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
		if (order == null || !order.getUserId().equals(uid)) {
			return ResponseVo.error(ResponseEnum.ORDER_NOT_EXIST);
		}
        //只有未付款菜呢呢取消
        if( !OrderStatusEnum.NO_PAY.getCode().equals(order.getStatus())){
            return ResponseVo.error(ResponseEnum.ORDER_STATUS_ERROR);
        }

        order.setStatus(OrderStatusEnum.CANCELED.getCode());
        order.setCloseTime(new Date());
        int row = orderMapper.updateByPrimaryKeySelective(order);
        if (row <= 0){
            return ResponseVo.error(ResponseEnum.ERROR);
        }

        return ResponseVo.sucess();
    }

    @Override
    public void paid(Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
		if (order == null) {
			throw new RuntimeException(ResponseEnum.ORDER_NOT_EXIST.getDesc()+"订单id" + orderNo);
		}
        //只有未付款订单可以变成已付款
        if( !OrderStatusEnum.NO_PAY.getCode().equals(order.getStatus())){
            throw new RuntimeException(ResponseEnum.ORDER_STATUS_ERROR.getDesc()+"订单id" + orderNo);
        }

        order.setStatus(OrderStatusEnum.PAID.getCode());
        order.setPaymentTime(new Date());
        int row = orderMapper.updateByPrimaryKeySelective(order);
        if (row <= 0){
            throw new RuntimeException("将订单更新为已支付状态失败"+ "订单ID" + orderNo);
        }
    }

    private OrderVo buildOrderVo(Order order, List<OrderItem> orderItemList, Shipping shipping) {
        OrderVo orderVo = new OrderVo();
		BeanUtils.copyProperties(order, orderVo);

		List<OrderItemVo> OrderItemVoList = orderItemList.stream().map(e -> {
			OrderItemVo orderItemVo = new OrderItemVo();
			BeanUtils.copyProperties(e, orderItemVo);
			return orderItemVo;
		}).collect(Collectors.toList());
		orderVo.setOrderItemVoList(OrderItemVoList);

		if (shipping != null) {
			orderVo.setShippingId(shipping.getId());
			orderVo.setShippingVo(shipping);
		}

		return orderVo;
    }

    private Order buildOrder(Integer uid,
							 Long orderNo,
							 Integer shippingId,
							 List<OrderItem> orderItemList
							 ) {
		BigDecimal payment = orderItemList.stream()
				.map(OrderItem::getTotalPrice)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		Order order = new Order();
		order.setOrderNo(orderNo);
		order.setUserId(uid);
		order.setShippingId(shippingId);
		order.setPayment(payment);
		order.setPaymentType(PaymentTypeEnum.PAY_ONLINE.getCode());
		order.setPostage(0);
		order.setStatus(OrderStatusEnum.NO_PAY.getCode());
		return order;
	}


    private Long generateOrderNo() {
		return System.currentTimeMillis() + new Random().nextInt(999);
	}
    private OrderItem buildOrderItem(Integer uid, Long orderNo, Integer quantity, Product product) {
		OrderItem item = new OrderItem();
		item.setUserId(uid);
		item.setOrderNo(orderNo);
		item.setProductId(product.getId());
		item.setProductName(product.getName());
		item.setProductImage(product.getMainImage());
		item.setCurrentUnitPrice(product.getPrice());
		item.setQuantity(quantity);
		item.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
		return item;
	}
}
