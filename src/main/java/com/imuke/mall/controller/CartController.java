package com.imuke.mall.controller;

import com.imuke.mall.consts.MallConsts;
import com.imuke.mall.form.CartAddForm;
import com.imuke.mall.form.CartUpdateForm;
import com.imuke.mall.pojo.User;
import com.imuke.mall.service.ICartService;
import com.imuke.mall.vo.CartVo;
import com.imuke.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * @author guanyun
 * @since 2025/2/24 20:16
 */
@RestController
public class CartController {

    @Autowired
    private ICartService cartService;

    @GetMapping("/carts")
	public ResponseVo<CartVo> list(HttpSession session) {
		User user = (User) session.getAttribute(MallConsts.CURRENT_USER);
		return cartService.list(user.getId());
	}

	@PostMapping("/carts")
	public ResponseVo<CartVo> add(@Valid @RequestBody CartAddForm cartAddForm,
								  HttpSession session) {
		User user = (User) session.getAttribute(MallConsts.CURRENT_USER);
		return cartService.add(user.getId(), cartAddForm);
	}

	@PutMapping("/carts/{productId}")
	public ResponseVo<CartVo> update(@PathVariable Integer productId,
									 @Valid @RequestBody CartUpdateForm form,
									 HttpSession session) {
		User user = (User) session.getAttribute(MallConsts.CURRENT_USER);
		return cartService.update(user.getId(), productId, form);
	}

	@DeleteMapping("/carts/{productId}")
	public ResponseVo<CartVo> delete(@PathVariable Integer productId,
									 HttpSession session) {
		User user = (User) session.getAttribute(MallConsts.CURRENT_USER);
		return cartService.delete(user.getId(), productId);
	}

	@PutMapping("/carts/selectAll")
	public ResponseVo<CartVo> selectAll(HttpSession session) {
		User user = (User) session.getAttribute(MallConsts.CURRENT_USER);
		return cartService.selectAll(user.getId());
	}

	@PutMapping("/carts/unSelectAll")
	public ResponseVo<CartVo> unSelectAll(HttpSession session) {
		User user = (User) session.getAttribute(MallConsts.CURRENT_USER);
		return cartService.unSelectAll(user.getId());
	}

	@GetMapping("/carts/products/sum")
	public ResponseVo<Integer> sum(HttpSession session) {
		User user = (User) session.getAttribute(MallConsts.CURRENT_USER);
		return cartService.sum(user.getId());
	}

}
