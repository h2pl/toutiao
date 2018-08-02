drop table if exists message;
create table message (
 `id` int(11) NOT NULL AUTO_INCREMENT,
 `from_id` int(11) not null ,
 `to_id` int(11) not null,
 `content` varchar(64) not null,
 `has_read` int(2) not null,
 `conversation_id` int(11) not null,
 `created_date` datetime not null,
 PRIMARY KEY (`id`)
)ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8