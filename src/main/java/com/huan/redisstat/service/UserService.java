package com.huan.redisstat.service;

import com.huan.redisstat.persistence.entities.User;

public interface UserService {

	User getByUserId(String userId);

}
