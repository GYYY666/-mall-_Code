package com.imuke.mall.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author guanyun
 * @since 2025/2/14 11:35
 *
 * 用户接收到的信息
 */
@Data
public class UserLoginForm {

	@NotBlank
	private String username;

	@NotBlank
	private String password;

}
