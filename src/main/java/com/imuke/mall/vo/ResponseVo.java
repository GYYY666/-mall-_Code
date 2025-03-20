package com.imuke.mall.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.imuke.mall.enums.ResponseEnum;
import lombok.Data;
import org.springframework.validation.BindingResult;

/**
 * @author guanyun
 * @since 2025/2/13 19:58
 */
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ResponseVo<T> {

    private Integer status;

    private String msg;

    private T data;

    private ResponseVo(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }
    private ResponseVo(Integer status, T data) {
        this.status = status;
        this.data = data;
    }
    public static <T> ResponseVo<T> sucessByMsg(String msg) {

        return new ResponseVo<>(ResponseEnum.SUCCESS.getCode(), msg);
    }
    public static <T> ResponseVo<T> sucess(T data) {

        return new ResponseVo<>(ResponseEnum.SUCCESS.getCode(), data);
    }

    

    public static <T> ResponseVo<T> sucess() {
        return new ResponseVo<>(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getDesc());
    }

    public static <T> ResponseVo<T> error(ResponseEnum responseEnum) {
        return new ResponseVo<>(responseEnum.getCode(), responseEnum.getDesc());
    }

    public static <T> ResponseVo<T> error(ResponseEnum responseEnum, String msg) {
        return new ResponseVo<>(responseEnum.getCode(), msg);
    }
    public static <T> ResponseVo<T> error(ResponseEnum responseEnum, BindingResult bindingResult) {
        return new ResponseVo<>(responseEnum.getCode(), bindingResult.getFieldError().getField()+ " " + bindingResult.getFieldError().getDefaultMessage());
    }
}
