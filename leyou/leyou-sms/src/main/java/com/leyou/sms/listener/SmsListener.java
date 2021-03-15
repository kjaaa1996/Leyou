package com.leyou.sms.listener;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.leyou.sms.config.SmsProperties;
import com.leyou.sms.utils.SmsUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author 26747
 * @description SmsListener
 * @date 2020/6/3 11:15
 */
@Component("smsListener")
public class SmsListener {

    @Resource(name = "smsUtils")
    private SmsUtils smsUtils;

    @Resource
    private SmsProperties smsProperties;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "LEYOU.SMS.QUEUE", durable = "true"),
            exchange = @Exchange(value = "LEYOU.SMS.EXCHANGE", type = ExchangeTypes.TOPIC, ignoreDeclarationExceptions = "true"),
            key = {"sms.verify.code"}
    ))
    public void ListenerSms(Map<String, String> msg) throws ClientException {
        if (msg == null || msg.size() <= 0) {
            //放弃处理
            return;
        }
        String phone = msg.get("phone");
        String code = msg.get("code");
        if (StringUtils.isBlank(phone) || StringUtils.isBlank(code)) {
            //放弃处理
            return;
        }
        //发送消息
        SendSmsResponse response = this.smsUtils.sendSms(phone, code, smsProperties.getSignName(), smsProperties.getVerifyCodeTemplate());

    }
}
