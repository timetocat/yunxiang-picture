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
