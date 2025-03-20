package com.imuke.mall.service.impl;

import com.google.gson.Gson;
import com.imuke.mall.dao.ProductMapper;
import com.imuke.mall.enums.ProductStatusEnum;
import com.imuke.mall.enums.ResponseEnum;
import com.imuke.mall.form.CartAddForm;
import com.imuke.mall.form.CartUpdateForm;
import com.imuke.mall.pojo.Cart;
import com.imuke.mall.pojo.Product;
import com.imuke.mall.service.ICartService;
import com.imuke.mall.vo.CartProductVo;
import com.imuke.mall.vo.CartVo;
import com.imuke.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author guanyun
 * @since 2025/2/24 21:06
 */
@Service
public class CartServiceImpl implements ICartService {

    private static final String CART_REDIS_KEY_TEMPLATE = "cart_%d";  //格式化

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private Gson gson = new Gson();

    @Override
    public ResponseVo<CartVo> add(Integer uid, CartAddForm form) {

        Integer quantity = 1;

        Product product = productMapper.selectByPrimaryKey(form.getProductId());

        //判断商品是否存在
        if(product == null){
            return ResponseVo.error(ResponseEnum.PRODUCT_NOT_EXIST);
        }

        //判断商品是否上架
////        if (!product.getStatus().equals(ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE.getCode())){
        if (!product.getStatus().equals(ProductStatusEnum.ON_SALE.getCode())){
            return ResponseVo.error(ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE);
        }

        //判断商品库存
        if(product.getStock() <= 0){
            return ResponseVo.error(ResponseEnum.PROODUCT_STOCK_ERROR);
        }

        //写入到redis
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey  = String.format(CART_REDIS_KEY_TEMPLATE, uid);

        Cart cart;  // 存入radis中的对象
		String value = opsForHash.get(redisKey, String.valueOf(product.getId()));
        if (StringUtils.isEmpty(value)) {
			//没有该商品, 新增
			cart = new Cart(product.getId(), quantity, form.getSelected());
		}else {
			//已经有了，数量+1
			cart = gson.fromJson(value, Cart.class);
			cart.setQuantity(cart.getQuantity() + quantity);
		}

        opsForHash.put(redisKey,
                String.valueOf(product.getId()),
                gson.toJson(cart));

        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> list(Integer uid) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey  = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        Map<String, String> entries = opsForHash.entries(redisKey);

        boolean selectAll = true;
        Integer cartTotalQuantity = 0;
        BigDecimal cartTotalPrice = BigDecimal.ZERO;
        CartVo cartVo = new CartVo();
        List<CartProductVo> cartProductVoList = new ArrayList<>();
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            Integer productId = Integer.valueOf(entry.getKey());
            Cart cart = gson.fromJson(entry.getValue(), Cart.class);

            //TODO 需要优化，使用mysql里的in
			Product product = productMapper.selectByPrimaryKey(productId);
			if (product != null) {
				CartProductVo cartProductVo = new CartProductVo(productId,
						cart.getQuantity(),
						product.getName(),
						product.getSubtitle(),
						product.getMainImage(),
						product.getPrice(),
						product.getStatus(),
						product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())),
						product.getStock(),
						cart.getProduceSelected()
				        );
                cartProductVoList.add(cartProductVo);

                if (!cart.getProduceSelected()){
                    selectAll = false;
                }

                //累加  计算总价（只计算选中的价格）
                if(cart.getProduceSelected()){
                    cartTotalPrice = cartTotalPrice.add(cartProductVo.getProductTotalPrice());
                }

            }

            cartTotalQuantity += cart.getQuantity();
        }
        cartVo.setSelectedAll(selectAll);
        cartVo.setCartTotalQuantity(cartTotalQuantity);
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        return ResponseVo.sucess(cartVo);
    }

    @Override
    public ResponseVo<CartVo> update(Integer uid, Integer productId, CartUpdateForm form) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey  = String.format(CART_REDIS_KEY_TEMPLATE, uid);

		String value = opsForHash.get(redisKey, String.valueOf(productId));
        if (StringUtils.isEmpty(value)) {
			//没有该商品, 报错
            return ResponseVo.error(ResponseEnum.PRODUCT_NOT_EXIST);
		}
        //已经有了，修改内容
        Cart cart = gson.fromJson(value, Cart.class);
        if (form.getQuantity() != null
                && form.getQuantity() >= 0) {
            cart.setQuantity(form.getQuantity());
        }

        if (form.getSelected() != null) {
            cart.setProduceSelected(form.getSelected());
        }

        opsForHash.put(redisKey,
                String.valueOf(productId),
                gson.toJson(cart));
        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> delete(Integer uid, Integer productId) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey  = String.format(CART_REDIS_KEY_TEMPLATE, uid);

		String value = opsForHash.get(redisKey, String.valueOf(productId));
        if (StringUtils.isEmpty(value)) {
			//没有该商品, 报错
            return ResponseVo.error(ResponseEnum.PRODUCT_NOT_EXIST);
		}
        opsForHash.delete(redisKey,
                String.valueOf(productId));
        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> selectAll(Integer uid) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey  = String.format(CART_REDIS_KEY_TEMPLATE, uid);

        for (Cart cart : listForCart(uid)) {
            cart.setProduceSelected(true);
            opsForHash.put(redisKey,
                    String.valueOf(cart.getProductId()),
                    gson.toJson(cart));
        }
        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> unSelectAll(Integer uid) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey  = String.format(CART_REDIS_KEY_TEMPLATE, uid);

        for (Cart cart : listForCart(uid)) {
            cart.setProduceSelected(false);
            opsForHash.put(redisKey,
                    String.valueOf(cart.getProductId()),
                    gson.toJson(cart));
        }
        return list(uid);
    }

    @Override
    public ResponseVo<Integer> sum(Integer uid) {
        Integer sum = listForCart(uid).stream()
				.map(Cart::getQuantity)
				.reduce(0, Integer::sum);
		return ResponseVo.sucess(sum);
    }

    public List<Cart> listForCart(Integer uid) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey  = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        Map<String, String> entries = opsForHash.entries(redisKey);

        List<Cart> cartList = new ArrayList<>();
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            cartList.add(gson.fromJson(entry.getValue(), Cart.class));
        }
        return cartList;
    }
}
