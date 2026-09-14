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
) engine=InnoDB charset = utf8mb4 comment '用户表，存储了用户的信息';



create table user_subscriber
(
    id bigint primary key comment '这张表的主键，无实际意义',
    subscriber_id bigint comment '关注者，关注了别人的人',
    subscribed_id bigint comment '被关注的人',

    constraint subscriber_foreign_key  foreign key (subscriber_id) references user(id) on update cascade on delete  cascade
) engine=InnoDB charset = utf8mb4 comment '这张表记录了关注关系，即一个人关注另一个人的关系';



create table private_message
(
    id bigint primary key,
    sender_id bigint comment '发送者id',
    receiver_id bigint comment '接收者id',
    message varchar(255) comment '消息本体，如果是文件那么存储的就是url',
    type tinyint comment '文件类型，0：文字，1：文件',
    chat_id int comment '会话内id，标识这是两个人之间发的第几条消息'
)engine = InnoDB charset  = utf8mb4 comment '这张表存储了私聊消息';



create table post_published
(
    id bigint primary key comment '帖子的主键，无实际意义',
    title varchar(255) comment '帖子的标题',
    content text comment '帖子的内容',
    publisher_nickname varchar(255) comment '帖子发布者的昵称',
    publisher_id bigint comment '发布者的id',
    bar_name varchar(255) comment '帖子属于的贴吧名字',
    bar_id bigint comment '这个帖子所属的贴吧的id',
    pin tinyint default 0 comment '这个帖子在它所属的贴吧中是否置顶',
    view_count int default 0 comment'这个帖子的浏览量',
    like_count int default 0 comment '这个帖子的点赞数',
    comment_count int default 0 comment '这个帖子的评论数',
    create_time datetime comment '这个帖子的发布时间'

) engine=InnoDB charset = utf8mb4 comment '这张表存储了已经发布的帖子的信息';


create table post_unpublished
(
    id bigint primary key comment '帖子的主键，无实际意义',
    title varchar(255) comment '帖子的标题',
    content text comment '帖子的内容',
    publisher_nickname varchar(255) comment '帖子发布者的昵称',
    publisher_id bigint comment '发布者的id',
    bar_name varchar(255) comment '帖子属于的贴吧名字',
    bar_id bigint comment '这个帖子所属的贴吧的id'

) engine=InnoDB charset = utf8mb4 comment '这张表存储了还没有发布帖子的信息';





create table bar
(
    id bigint primary key comment '本张表的主键，无实际意义',
    name varchar(255) comment '贴吧的名字',
    description varchar(255) default '' comment '贴吧的简介',
    cover_image varchar(255) default '' comment '贴吧头像的url地址',
    member_count int default 0 comment '贴吧的成员数',
    post_count int default 0 comment '贴吧里帖子的数量',
    master_id bigint comment '吧主的id',
    master_nickname varchar(255) comment '吧主的昵称',
    is_banned tinyint default 0 comment '贴吧是否被封禁',
    post_permission tinyint default 0 comment '发帖权限，0：所有人可发帖，1：仅成员可发帖'
)engine=InnoDB charset = utf8mb4 comment '本张表储存了贴吧的相关信息';







create table bar_member
(
    id bigint primary key comment '这张表的主键，无实际意义',
    bar_id bigint comment '用户属于哪个贴吧',
    user_id bigint comment '属于该贴吧的成员',
    identity int comment '该用户在该贴吧的身份，0：普通用户，1：管理员，2：吧主',
    is_banned tinyint comment '该用户在该贴吧中是否被封禁',
    unique key bar_member_unique (bar_id, user_id)
)engine=InnoDB charset = utf8mb4 comment '本张表为了储存各个用户在各个贴吧中的状态';