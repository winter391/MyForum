create schema if not exists MyForum;

use MyForum;

create table User(
                     id bigint primary key comment '主键，用户id',
                     user_name varchar(255) comment '用户名',
                     password varchar(255) comment '用户密码',
                     head_image varchar(255) default '' comment '用户头像url',
                     head_image_thumb varchar(255) default '' comment '用户头像缩略图url',
                     nick_name varchar(255) comment '用户昵称',
                     sex tinyint default 0 comment '用户性别，0：男，1：女',
                     is_banned tinyint default 0 comment '用户是否被封禁，0：未被封禁，1：已被封禁',
                     type tinyint default 0 comment '用户类型，0：为普通用户，1：为管理员用户',
                     create_time datetime default current_timestamp comment '用户创建时间',
                     login_time datetime comment '用户最后登录时间'
) engine=InnoDB charset = utf8mb4 comment '用户表，存储了用户的信息'