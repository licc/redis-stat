package com.huan.redisstat.persistence.entities;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@Table(name = "t_node")
public class Node {
    @Id
    private Long id;

    private Long clusterId;

    private String name;

    private String host;

    private Integer port;

    private Integer dataState;

    private Date createTime;

    private Date updateTime;
}