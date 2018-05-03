DROP TABLE IF EXISTS 'news';
CREATE TABLE 'news'(
	`id` int(11) unsigned NOT NULL AUTO_INCREAMENT,
	`titie` varchar(128) NOT NULL DEFAULT '',
	`link` varchar(256) NOT NULL DEFAULT '',
	`image` varchar(256) NOT NULL DEFAULT '',
	`like_count` int(11) NOT NULL,
	`comment_count` int(11) NOT NULL,
	`create_date` datetime NOT NULL,
	`user_id` int(11) NOT NULL,
	PRIMARY KEY (`id`)
)ENGINE = InnoDB AUTO_INCREAMENT=11 DEFAULT CHARSET=utf8