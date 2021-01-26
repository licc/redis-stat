package com.huan.redisstat.persistence.entities;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@Table(name = "t_cluster")
public class Cluster {
    @Id
    private Long id;

    private String name;

    private Date createTime;

    private Date updateTime;

    private Integer dataState;

    @Transient
    private List<Node> nodes;

}