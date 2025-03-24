ALTER TABLE picture
    -- 添加新列
    ADD COLUMN review_status INT DEFAULT 0 NOT NULL COMMENT '审核状态：0-待审核; 1-通过; 2-拒绝',
    ADD COLUMN review_message VARCHAR(512) NULL COMMENT '审核信息',
    ADD COLUMN reviewer_id BIGINT NULL COMMENT '审核人 ID',
    ADD COLUMN review_time DATETIME NULL COMMENT '审核时间';

-- 创建基于 reviewStatus 列的索引
CREATE INDEX idx_reviewStatus ON picture (review_status);


ALTER TABLE picture
    -- 添加新列
    ADD COLUMN thumbnail_url varchar(512) NULL COMMENT '缩略图 url';

-- 添加新列
ALTER TABLE picture
    ADD COLUMN space_id bigint  null comment '空间 id（为空表示公共空间）';

-- 创建索引
CREATE INDEX idx_spaceId ON picture (space_id);

-- 添加新列
ALTER TABLE picture
    ADD COLUMN pic_color varchar(16) null comment '图片主色调';

ALTER TABLE space
    ADD COLUMN space_type int default 0 not null comment '空间类型：0-私有 1-团队';

-- 扩展用户表：新增会员功能
ALTER TABLE user
    ADD COLUMN vip_expire_time datetime NULL COMMENT '会员过期时间',
    ADD COLUMN vip_code        char(8)  NULL COMMENT '会员兑换码',
    ADD COLUMN vip_number      bigint   NULL COMMENT '会员编号';

ALTER TABLE user
    ADD email varchar(256) NULL COMMENT '用户邮箱';

CREATE UNIQUE INDEX idx_email ON user (email, id);