ALTER TABLE bus_optimization_task
    ADD COLUMN crop_whitelist varchar(500) DEFAULT NULL COMMENT '作物白名单，逗号分隔的 crop_id 列表',
    ADD COLUMN crop_blacklist varchar(500) DEFAULT NULL COMMENT '作物黑名单，逗号分隔的 crop_id 列表',
    ADD COLUMN min_pulse_ratio decimal(5,4) DEFAULT '0.0000' COMMENT '豆类最小占比，取值范围 [0,1]',
    ADD COLUMN task_season varchar(20) DEFAULT NULL COMMENT '任务季节',
    ADD COLUMN owner_user_id bigint(20) DEFAULT NULL COMMENT '任务归属用户ID';

ALTER TABLE bus_optimization_task
    ADD INDEX idx_bus_opt_task_owner (owner_user_id);
