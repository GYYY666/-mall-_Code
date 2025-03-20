package com.imuke.mall.listener;

import com.google.gson.Gson;
import com.imuke.mall.pojo.PayInfo;
import com.imuke.mall.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author guanyun
 * @since 2025/3/8 10:56
 */
@Component
@RabbitListener(queues = "payNotify")
@Slf4j
public class PayMsgListener {
    @Autowired
	private IOrderService orderService;

	@RabbitHandler
	public void process(String msg) {
		log.info("【接收到消息】=> {}", msg);

		PayInfo payInfo = new Gson().fromJson(msg, PayInfo.class);
		if (payInfo.getPlatformStatus().equals("SUCCESS")) {
			//修改订单里的状态
			orderService.paid(payInfo.getOrderNo());
		}
	}

}
