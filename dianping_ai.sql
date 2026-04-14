/*
 Navicat Premium Dump SQL

 Source Server         : 本地库
 Source Server Type    : MySQL
 Source Server Version : 80039 (8.0.39)
 Source Host           : localhost:3306
 Source Schema         : dianping_ai

 Target Server Type    : MySQL
 Target Server Version : 80039 (8.0.39)
 File Encoding         : 65001

 Date: 28/01/2026 20:43:24
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ai_call_log
-- ----------------------------
DROP TABLE IF EXISTS `ai_call_log`;
CREATE TABLE `ai_call_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NULL DEFAULT NULL COMMENT '触发用户ID，可空（系统调用为空）',
  `type` enum('generate','recommend') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调用类型',
  `prompt` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请求Prompt',
  `response_ref` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '响应引用/存储位置',
  `latency_ms` int NULL DEFAULT NULL COMMENT '耗时ms',
  `status` int NULL DEFAULT 1 COMMENT '状态：1成功，0失败',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '大模型调用日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_call_log
-- ----------------------------
INSERT INTO `ai_call_log` VALUES (1, 3, 'generate', '请基于以下信息生成一条用户点评：\n- 用户ID: 3\n- 商家ID: 1\n- 用户偏好: 偏辣，别太咸\n- 历史消费: 无历史消费记录\n要求：口吻真实简洁，50~120字，避免夸大或虚假表述。\n', 'mock llm response for prompt: 请基于以下信息生成一条用户点评：\n- 用户ID: 3\n- 商家ID: 1\n- 用户偏好: 偏辣，别太咸\n- 历史消费: 无历史消费记录\n要求：口吻真实简洁，50~120字，避免夸大或虚假表述。\n', 11, 1, '2025-12-15 16:42:56');
INSERT INTO `ai_call_log` VALUES (2, 3, 'generate', '请基于以下信息生成一条用户点评：\n- 用户ID: 3\n- 商家ID: 1\n- 用户偏好: null\n- 历史消费: 无历史消费记录\n要求：口吻真实简洁，50~120字，避免夸大或虚假表述。\n', 'mock llm response for prompt: 请基于以下信息生成一条用户点评：\n- 用户ID: 3\n- 商家ID: 1\n- 用户偏好: null\n- 历史消费: 无历史消费记录\n要求：口吻真实简洁，50~120字，避免夸大或虚假表述。\n', 2, 1, '2025-12-15 16:44:11');
INSERT INTO `ai_call_log` VALUES (3, 3, 'generate', '请基于以下信息生成一条用户点评：\n- 用户ID: 3\n- 商家ID: 1\n- 用户偏好: 偏辣，别太咸\n- 历史消费: 无历史消费记录\n要求：口吻真实简洁，50~120字，避免夸大或虚假表述。\n', '【mock响应】请基于以下信息生成一条用户点评：\n- 用户ID: 3\n- 商家ID: 1\n- 用户偏好: 偏辣，别太咸\n- 历史消费: 无历史消费记录\n要求：口吻真实简洁，50~120字，避免夸大或虚假表述。\n', 210, 1, '2025-12-15 23:47:25');
INSERT INTO `ai_call_log` VALUES (4, 3, 'generate', '请基于以下信息生成一条用户点评：\n- 用户ID: 3\n- 商家ID: 1\n- 用户偏好: 偏辣，别太咸\n- 历史消费: 无历史消费记录\n要求：口吻真实简洁，50~120字，避免夸大或虚假表述。\n', '第一次来这家店，点了招牌辣子鸡，辣度很够，正合我口味！就是稍微有点咸，下次点单时得备注少盐。整体味道不错，分量也足，会再来试试其他菜。', 3309, 1, '2025-12-16 19:28:54');
INSERT INTO `ai_call_log` VALUES (5, 7, 'generate', '请基于以下信息生成一条用户点评：\n- 用户ID: 7\n- 商家ID: 1\n- 用户自由输入的偏好: \n- 用户标签画像: 无\n- 商家标签特征: 无\n- 历史消费: 无历史消费记录\n要求：结合上述信息，用中文生成一条真实、具体的用餐点评，50~120字，避免夸大或虚假表述。\n', '第一次来这家店，整体体验还不错。点了招牌菜和一份主食，分量适中，口味清淡合我胃口。服务员态度也挺友好，上菜速度正常。虽然没什么特别惊艳的地方，但作为附近用餐的选择，算是踏实可靠的一餐。', 3943, 1, '2026-01-13 19:26:49');
INSERT INTO `ai_call_log` VALUES (6, 7, 'generate', '请基于以下信息生成一条用户点评：\n- 用户ID: 7\n- 商家ID: 2\n- 用户自由输入的偏好: \n- 用户标签画像: 无\n- 商家标签特征: 无\n- 历史消费: 无历史消费记录\n要求：结合上述信息，用中文生成一条真实、具体的用餐点评，50~120字，避免夸大或虚假表述。\n', '第一次来这家店，整体体验还不错。点了招牌菜和一份主食，分量适中，味道中规中矩，服务态度也挺友好。虽然没什么特别惊艳的地方，但作为附近用餐的选择还算靠谱，以后可能会再来试试其他菜品。', 2735, 1, '2026-01-13 19:58:46');

-- ----------------------------
-- Table structure for menu
-- ----------------------------
DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `shop_id` bigint NOT NULL COMMENT '商家ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜品名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '菜品描述',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '价格',
  `image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片URL',
  `is_recommended` tinyint NULL DEFAULT 0 COMMENT '是否推荐：1是，0否',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序顺序（数字越小越靠前）',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1正常，0下线',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_menu_shop`(`shop_id` ASC) USING BTREE,
  INDEX `idx_menu_recommended`(`shop_id` ASC, `is_recommended` ASC, `status` ASC) USING BTREE,
  CONSTRAINT `fk_menu_shop` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of menu
-- ----------------------------
INSERT INTO `menu` VALUES (1, 2, '焦糖玛奇朵', '经典热销咖啡，香甜焦糖配上顺滑牛奶', 32.00, NULL, 1, 1, 1, '2026-01-23 18:49:01', '2026-01-23 18:49:01');
INSERT INTO `menu` VALUES (2, 2, '冰摇柠檬茶', '夏日清爽必点，酸甜适口解腻', 26.00, NULL, 1, 2, 1, '2026-01-23 18:49:01', '2026-01-23 18:49:01');
INSERT INTO `menu` VALUES (3, 2, '提拉米苏蛋糕', '咖啡风味甜点，搭配饮品更满足', 36.00, NULL, 1, 3, 1, '2026-01-23 18:49:01', '2026-01-23 18:49:01');
INSERT INTO `menu` VALUES (4, 1, '经典番茄锅底', '酸甜番茄锅，适合全家口味，儿童也能接受', 68.00, NULL, 1, 1, 1, '2026-01-23 18:49:01', '2026-01-23 18:49:01');
INSERT INTO `menu` VALUES (5, 1, '招牌牛肉卷', '精选肥牛，适合涮 7 秒入口即化', 58.00, NULL, 1, 2, 1, '2026-01-23 18:49:01', '2026-01-23 18:49:01');
INSERT INTO `menu` VALUES (6, 1, '虾滑拼盘', '手工虾滑，Q 弹爽口，海鲜爱好者推荐', 72.00, NULL, 1, 3, 1, '2026-01-23 18:49:01', '2026-01-23 18:49:01');
INSERT INTO `menu` VALUES (7, 3, '招牌招待茶', '简单清香的热茶，用于测试环境占位', 5.00, NULL, 1, 1, 1, '2026-01-23 18:49:01', '2026-01-23 18:49:01');
INSERT INTO `menu` VALUES (8, 3, '测试小吃拼盘', '多种小吃组合，用于界面展示与联调', 18.00, NULL, 1, 2, 1, '2026-01-23 18:49:01', '2026-01-23 18:49:01');

-- ----------------------------
-- Table structure for order_record
-- ----------------------------
DROP TABLE IF EXISTS `order_record`;
CREATE TABLE `order_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `shop_id` bigint NOT NULL COMMENT '商家ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `order_no` varchar(50) NULL DEFAULT NULL COMMENT '订单号',
  `amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '消费金额，保留两位小数',
  `pay_status` tinyint NULL DEFAULT 0 COMMENT '支付状态：0待支付，1已支付，2已取消',
  `pay_method` varchar(30) NULL DEFAULT NULL COMMENT '支付方式（模拟）',
  `pay_txn_no` varchar(80) NULL DEFAULT NULL COMMENT '模拟交易号',
  `paid_at` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `visit_time` datetime NULL DEFAULT NULL COMMENT '到店时间',
  `items` json NULL COMMENT '消费项明细JSON',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_shop`(`shop_id` ASC) USING BTREE,
  INDEX `idx_order_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_order_pay_status`(`pay_status` ASC) USING BTREE,
  CONSTRAINT `fk_order_shop` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '消费记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_record
-- ----------------------------

-- ----------------------------
-- Table structure for review
-- ----------------------------
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `shop_id` bigint NOT NULL COMMENT '商家ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `rating` int NULL DEFAULT NULL COMMENT '评分1-5',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '点评正文',
  `images` json NULL COMMENT '图片列表JSON',
  `is_ai_generated` bit(1) NULL DEFAULT b'0' COMMENT '是否AI生成：0否1是',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `status` int NULL DEFAULT 1 COMMENT '状态：1正常，0屏蔽/审核中',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_review_shop`(`shop_id` ASC) USING BTREE,
  INDEX `idx_review_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_review_shop` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_review_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '点评表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of review
-- ----------------------------
INSERT INTO `review` VALUES (1, 1, 3, 5, '好吃', '[\"https://xx/1.jpg\"]', b'0', 0, 1, '2025-12-15 16:41:04');
INSERT INTO `review` VALUES (2, 1, 3, 5, '这家火锅很辣很过瘾，服务也不错', '[]', b'0', 2, 1, '2025-12-15 21:45:18');
INSERT INTO `review` VALUES (3, 1, 3, 5, '这家火锅很辣很过瘾，服务也不错', NULL, b'0', 0, 1, '2025-12-15 21:45:55');
INSERT INTO `review` VALUES (4, 2, 7, 5, '第一次来这家店，整体体验还不错。点了招牌菜和一份主食，分量适中，味道中规中矩，服务态度也挺友好。虽然没什么特别惊艳的地方，但作为附近用餐的选择还算靠谱，以后可能会再来试试其他菜品。', NULL, b'0', 0, 1, '2026-01-13 20:19:31');
INSERT INTO `review` VALUES (5, 2, 7, 5, '真不错', '[\"blob:http://localhost:5173/3deb90c3-b272-4168-bc8d-13ce7aab91ff\"]', b'0', 0, 1, '2026-01-26 22:40:27');

-- ----------------------------
-- Table structure for review_keyword
-- ----------------------------
DROP TABLE IF EXISTS `review_keyword`;
CREATE TABLE `review_keyword`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `review_id` bigint NOT NULL COMMENT '点评ID',
  `keyword` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '关键词',
  `weight` double NULL DEFAULT 1 COMMENT '权重',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_review_keyword_review`(`review_id` ASC) USING BTREE,
  CONSTRAINT `fk_review_keyword_review` FOREIGN KEY (`review_id`) REFERENCES `review` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '点评关键词表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of review_keyword
-- ----------------------------
INSERT INTO `review_keyword` VALUES (1, 2, '辣', 1);
INSERT INTO `review_keyword` VALUES (2, 2, '服务', 1);
INSERT INTO `review_keyword` VALUES (3, 3, '辣', 1);
INSERT INTO `review_keyword` VALUES (4, 3, '服务', 1);
INSERT INTO `review_keyword` VALUES (5, 4, '态度', 1);
INSERT INTO `review_keyword` VALUES (6, 4, '服务', 1);
INSERT INTO `review_keyword` VALUES (7, 4, '分量', 1);

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色编码，英文标识，如 ADMIN / MERCHANT / USER',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称，如 管理员 / 商家 / 普通用户',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_code`(`code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, 'ADMIN', '运营管理员', '平台运营/运维人员，管理所有商家和数据', '2026-01-27 22:02:56');
INSERT INTO `role` VALUES (2, 'MERCHANT', '商家', '商家账号，只能管理自己店铺', '2026-01-27 22:02:56');
INSERT INTO `role` VALUES (3, 'USER', '普通用户', '前台普通用户/点评用户', '2026-01-27 22:02:56');

-- ----------------------------
-- Table structure for shop
-- ----------------------------
DROP TABLE IF EXISTS `shop`;
CREATE TABLE `shop`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商家名称',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '品类',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地址',
  `lng` double NULL DEFAULT NULL COMMENT '经度',
  `lat` double NULL DEFAULT NULL COMMENT '纬度',
  `avg_price` double NULL DEFAULT NULL COMMENT '人均价格',
  `avg_score` double NULL DEFAULT NULL COMMENT '平均评分',
  `status` int NULL DEFAULT 1 COMMENT '状态：1正常，0下线',
  `owner_user_id` bigint NULL DEFAULT NULL COMMENT '归属商家用户ID（商家账号）',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_shop_owner_user`(`owner_user_id` ASC) USING BTREE,
  CONSTRAINT `fk_shop_owner_user` FOREIGN KEY (`owner_user_id`) REFERENCES `user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商家表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shop
-- ----------------------------
INSERT INTO `shop` VALUES (1, '海底捞火锅（升级版）', '火锅', '北京市朝阳区新地址xxx', 116.397128, 39.916527, 180, 3.8, 1, NULL, '2025-12-15 15:14:09', '2025-12-25 19:48:00');
INSERT INTO `shop` VALUES (2, '星巴克咖啡', '咖啡', '北京市海淀区xxx', NULL, NULL, 25, 4.2, 1, NULL, '2025-12-15 15:21:35', '2025-12-25 23:46:48');
INSERT INTO `shop` VALUES (3, '测试新增商家', '其他', '广东省', NULL, NULL, 295, 0, 1, NULL, '2025-12-25 21:38:55', '2025-12-25 21:38:55');

-- ----------------------------
-- Table structure for shop_tag
-- ----------------------------
DROP TABLE IF EXISTS `shop_tag`;
CREATE TABLE `shop_tag`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `shop_id` bigint NOT NULL COMMENT '商家ID',
  `tag_id` bigint NOT NULL COMMENT '标签ID',
  `weight` double NULL DEFAULT 1 COMMENT '权重',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_shop_tag`(`shop_id` ASC, `tag_id` ASC) USING BTREE,
  INDEX `idx_shop_tag_tag`(`tag_id` ASC) USING BTREE,
  CONSTRAINT `fk_shop_tag_shop` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_shop_tag_tag` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商家与标签关联' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shop_tag
-- ----------------------------

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签名',
  `type` enum('user','shop','review') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签类型',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '标签表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tag
-- ----------------------------
INSERT INTO `tag` VALUES (1, '微辣', 'user', '2026-01-19 21:53:09');
INSERT INTO `tag` VALUES (2, '微辣', 'user', '2026-01-19 22:05:40');
INSERT INTO `tag` VALUES (3, '服务好', 'user', '2026-01-20 22:38:52');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号，唯一可空',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱，唯一可空',
  `password_hash` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码哈希',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `status` int NULL DEFAULT 1 COMMENT '状态：1正常，0禁用',
  `balance` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '账户余额',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `mobile`(`mobile` ASC) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (3, NULL, 'test@example.com', '$2a$10$e2kTr31d9I2M8UFdo8Q5Wuf007ufzX.LnUO9Met8KvvS3f6TTixm6', '威震天', NULL, 1, 300.00, NULL, '2026-01-17 22:58:21');
INSERT INTO `user` VALUES (4, '123456', NULL, '$2a$10$7qRXdjwXJbhWyIGhp/XzlOvuNy4rIkZazCZFlbptyPJVb2l3iij.i', NULL, NULL, 1, 0.00, NULL, NULL);
INSERT INTO `user` VALUES (5, '1234567', NULL, '$2a$10$nm.4qBvnKnI69qa.f9dJrO/Apaf8stLYkwGYx1nktDSLlZ.j5Lm52', NULL, NULL, 1, 0.00, '2025-12-13 22:09:49', '2025-12-13 22:09:49');
INSERT INTO `user` VALUES (6, '12345678', NULL, '$2a$10$epklKD2qV/jvKlCNp0x.EOWin0wuhKfNG9wABlQSgicI6smWYgzlC', NULL, NULL, 1, 0.00, '2025-12-13 23:53:01', '2025-12-13 23:53:01');
INSERT INTO `user` VALUES (7, '12345678910', NULL, '$2a$10$OFwV1UEJMi6ziTPj5w6/n.4uvfE7iTnnLBL0JVzcYIsYbEkoVt/ai', '威震天1', NULL, 1, 500.00, '2025-12-24 20:39:34', '2026-01-17 23:11:39');

-- ----------------------------
-- Table structure for wallet_txn
-- ----------------------------
DROP TABLE IF EXISTS `wallet_txn`;
CREATE TABLE `wallet_txn`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '流水类型：RECHARGE/WITHDRAW/PAY',
  `amount` decimal(10, 2) NOT NULL COMMENT '变动金额，正数入账，负数出账',
  `balance_before` decimal(10, 2) NOT NULL COMMENT '变更前余额',
  `balance_after` decimal(10, 2) NOT NULL COMMENT '变更后余额',
  `biz_no` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '业务单号（订单号/交易号）',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_wallet_txn_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_wallet_txn_created`(`created_at` ASC) USING BTREE,
  CONSTRAINT `fk_wallet_txn_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '账户流水表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_role`(`user_id` ASC, `role_id` ASC) USING BTREE,
  INDEX `idx_user_role_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_user_role_role`(`role_id` ASC) USING BTREE,
  CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户与角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_role
-- ----------------------------

-- ----------------------------
-- Table structure for user_tag
-- ----------------------------
DROP TABLE IF EXISTS `user_tag`;
CREATE TABLE `user_tag`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `tag_id` bigint NOT NULL COMMENT '标签ID',
  `weight` double NULL DEFAULT 1 COMMENT '权重',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_tag`(`user_id` ASC, `tag_id` ASC) USING BTREE,
  INDEX `idx_user_tag_tag`(`tag_id` ASC) USING BTREE,
  CONSTRAINT `fk_user_tag_tag` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_tag_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户与标签关联' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_tag
-- ----------------------------
INSERT INTO `user_tag` VALUES (5, 7, 3, 1, '2026-01-20 22:38:54');

-- ----------------------------
-- Table structure for user_review_like
-- ----------------------------
DROP TABLE IF EXISTS `user_review_like`;
CREATE TABLE `user_review_like`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `review_id` bigint NOT NULL COMMENT '点评ID',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_review_like`(`user_id` ASC, `review_id` ASC) USING BTREE,
  INDEX `idx_user_review_like_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_user_review_like_review`(`review_id` ASC) USING BTREE,
  CONSTRAINT `fk_user_review_like_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_review_like_review` FOREIGN KEY (`review_id`) REFERENCES `review` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户点赞点评关系表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
