create table t_cluster
(
    id          bigint auto_increment not null primary key,
    name        varchar(255),
    data_state        int,
    create_time TIMESTAMP(6),
    update_time TIMESTAMP(6)


);


create table t_node
(
    id          bigint auto_increment not null primary key,
    cluster_id      bigint,
    name        varchar(255),
    host        varchar(128),
    port        int,
    data_state        int,
    create_time TIMESTAMP(6),
    update_time TIMESTAMP(6)


);

