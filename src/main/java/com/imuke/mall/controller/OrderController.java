package com.imuke.mall.controller;

import com.github.pagehelper.PageInfo;
import com.imuke.mall.consts.MallConsts;
import com.imuke.mall.form.OrderCreateForm;
import com.imuke.mall.pojo.User;
import com.imuke.mall.service.IOrderService;
import com.imuke.mall.vo.OrderVo;
import com.imuke.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * @author guanyun
 * @since 2025/3/6 22:00
 */
public class OrderController {
    @Autowired
	private IOrderService orderService;

	@PostMapping("/orders")
	public ResponseVo<OrderVo> create(@Valid @RequestBody OrderCreateForm form,
									  HttpSession session) {
		User user = (User) session.getAttribute(MallConsts.CURRENT_USER);
		return orderService.create(user.getId(), form.getShippingId());
	}

	@GetMapping("/orders")
	public ResponseVo<PageInfo> list(@RequestParam Integer pageNum,
                                     @RequestParam Integer pageSize,
                                     HttpSession session) {
		User user = (User) session.getAttribute(MallConsts.CURRENT_USER);
		return orderService.list(user.getId(), pageNum, pageSize);
	}

	@GetMapping("/orders/{orderNo}")
	public ResponseVo<OrderVo> detail(@PathVariable Long orderNo,
                                      HttpSession session) {
		User user = (User) session.getAttribute(MallConsts.CURRENT_USER);
		return orderService.detail(user.getId(), orderNo);
	}

	@PutMapping("/orders/{orderNo}")
	public ResponseVo cancel(@PathVariable Long orderNo,
                             HttpSession session) {
		User user = (User) session.getAttribute(MallConsts.CURRENT_USER);
		return orderService.cancel(user.getId(), orderNo);
	}

}
