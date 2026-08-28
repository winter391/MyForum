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



create table user_subscriber
(
    id bigint primary key comment '这张表的主键，无实际意义',
    subscriber_id bigint comment '关注者，关注了别人的人',
    subscribed_id bigint comment '被关注的人',

    constraint subscriber_foreign_key  foreign key (subscriber_id) references user(id) on update cascade on delete  cascade
) engine=InnoDB charset = utf8mb4 comment '这张表记录了关注关系，即一个人关注另一个人的关系'