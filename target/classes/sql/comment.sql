drop table if exists comment;

create table comment (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) not null,
  `entity_id` int(11) not null,
  `entity_type` int(2) not null,
  `status` int(2) not null,
  `content` varchar(64) not null,
  `created_date` datetime not null,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8