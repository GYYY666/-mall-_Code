package com.imuke.mall.exception;

import com.imuke.mall.enums.ResponseEnum;
import com.imuke.mall.vo.ResponseVo;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Objects;

/**
 * 捕获运行时异常，用于返回格式化错误信息‘
 *
 * @author guanyun
 * @since 2025/2/14 15:04
 */
@ControllerAdvice
public class RuntimeExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody  //将java对象转为json格式的数据。
    public ResponseVo handle(RuntimeException e) {

        return ResponseVo.error(ResponseEnum.ERROR, e.getMessage());
    }

    @ExceptionHandler(UserLoginException.class)
    @ResponseBody
    public ResponseVo UserLoginException() {
        return ResponseVo.error(ResponseEnum.NEED_LOGIN);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseBody
	public ResponseVo notValidExceptionHandle(MethodArgumentNotValidException e) {
		BindingResult bindingResult = e.getBindingResult();
		Objects.requireNonNull(bindingResult.getFieldError());
		return ResponseVo.error(ResponseEnum.PARAM_ERROR,
				bindingResult.getFieldError().getField() + " "
                        + bindingResult.getFieldError().getDefaultMessage());
	}
}
