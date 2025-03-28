package com.imuke.mall.controller;

import com.imuke.mall.consts.MallConsts;
import com.imuke.mall.form.ShippingForm;
import com.imuke.mall.pojo.User;
import com.imuke.mall.service.IShippingService;
import com.imuke.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * @author guanyun
 * @since 2025/3/1 15:43
 */
@Controller
public class ShippingController {

    @Autowired
	private IShippingService shippingService;

	@PostMapping("/shippings")
	public ResponseVo add(@Valid @RequestBody ShippingForm form,
						  HttpSession session) {
		User user = (User) session.getAttribute(MallConsts.CURRENT_USER);
		return shippingService.add(user.getId(), form);
	}

	@DeleteMapping("/shippings/{shippingId}")
	public ResponseVo delete(@PathVariable Integer shippingId,
                             HttpSession session) {
		User user = (User) session.getAttribute(MallConsts.CURRENT_USER);
		return shippingService.delete(user.getId(), shippingId);
	}

	@PutMapping("/shippings/{shippingId}")
	public ResponseVo update(@PathVariable Integer shippingId,
							 @Valid @RequestBody ShippingForm form,
							 HttpSession session) {
		User user = (User) session.getAttribute(MallConsts.CURRENT_USER);
		return shippingService.update(user.getId(), shippingId, form);
	}

	@GetMapping("/shippings")
	public ResponseVo list(@RequestParam(required = false, defaultValue = "1") Integer pageNum,
						   @RequestParam(required = false, defaultValue = "10") Integer pageSize,
						   HttpSession session) {
		User user = (User) session.getAttribute(MallConsts.CURRENT_USER);
		return shippingService.list(user.getId(), pageNum, pageSize);
	}

}
