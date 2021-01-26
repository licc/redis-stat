insert into t_cluster(id, name, data_state,create_time)
values (1,
        '本地',
        1,
        now());


insert into t_node(cluster_id, name, host, port,data_state, create_time, update_time)
values (1, 'master', '127.0.0.1', 6379,1, now(), now());

