package com.huan.redisstat.persistence.entities;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
public class User implements Serializable {

	private String userId;

	private String userName;


	private String password;


	private String sex;


	private Date registerTime;


	private String registerIp;




}
