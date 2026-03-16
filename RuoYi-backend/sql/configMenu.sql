-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('作物指标配置', '1061', '1', 'config', 'agriculture/config/index', 1, 0, 'C', '0', '0', 'agriculture:config:list', '#', 'admin', sysdate(), '', null, '作物指标配置菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('作物指标配置查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'agriculture:config:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('作物指标配置新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'agriculture:config:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('作物指标配置修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'agriculture:config:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('作物指标配置删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'agriculture:config:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('作物指标配置导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'agriculture:config:export',       '#', 'admin', sysdate(), '', null, '');