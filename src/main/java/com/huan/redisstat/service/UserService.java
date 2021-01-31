package com.huan.redisstat.service;

import com.huan.redisstat.persistence.entities.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {

	User getByUserId(String userId);

    UserDetails getAccessableResource(String name, String ldapNo);
}
