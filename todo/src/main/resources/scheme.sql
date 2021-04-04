-- DROP TABLE
DROP TABLE IF EXISTS `todo`;

-- CREATE TABLE
create table IF not exists `todo`
(
   `id`        INT(20) AUTO_INCREMENT,
   `title`     VARCHAR(50) NOT NULL,
   `content`   VARCHAR(500) NOT NULL,
   `archived`  BOOLEAN,
   `completed` BOOLEAN,
   `created_date_time` Datetime DEFAULT NULL,
   `updated_date_time` Datetime DEFUALT NULL,
       PRIMARY KEY('id')
) DEFAULT_CHARSET=utf8 COLLATE=utf8_bin;