-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('种植优化任务', '1061', '1', 'task', 'agriculture/task/index', 1, 0, 'C', '0', '0', 'agriculture:task:list', '#', 'admin', sysdate(), '', null, '种植优化任务菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('种植优化任务查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'agriculture:task:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('种植优化任务新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'agriculture:task:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('种植优化任务修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'agriculture:task:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('种植优化任务删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'agriculture:task:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('种植优化任务导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'agriculture:task:export',       '#', 'admin', sysdate(), '', null, '');