package com.leyou.auth.client;

import com.leyou.user.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author 26747
 * @description UserClient
 * @date 2020/6/5 15:22
 */
@FeignClient(value = "user-service")
public interface UserClient extends UserApi {
}
