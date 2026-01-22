-- 菜单表 DDL
-- 用于存储商家的菜单/菜品信息

CREATE TABLE IF NOT EXISTS `menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `shop_id` bigint NOT NULL COMMENT '商家ID',
  `name` varchar(100) NOT NULL COMMENT '菜品名称',
  `description` text COMMENT '菜品描述',
  `price` decimal(10,2) DEFAULT NULL COMMENT '价格',
  `image` varchar(500) DEFAULT NULL COMMENT '图片URL',
  `is_recommended` tinyint DEFAULT '0' COMMENT '是否推荐：1是，0否',
  `sort_order` int DEFAULT '0' COMMENT '排序顺序（数字越小越靠前）',
  `status` tinyint DEFAULT '1' COMMENT '状态：1正常，0下线',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_menu_shop` (`shop_id`),
  KEY `idx_menu_recommended` (`shop_id`, `is_recommended`, `status`),
  CONSTRAINT `fk_menu_shop` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单表';

