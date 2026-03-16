-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('任务资源关联', '1061', '1', 'taskLand', 'agriculture/taskLand/index', 1, 0, 'C', '0', '0', 'agriculture:taskLand:list', '#', 'admin', sysdate(), '', null, '任务资源关联菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('任务资源关联查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'agriculture:taskLand:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('任务资源关联新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'agriculture:taskLand:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('任务资源关联修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'agriculture:taskLand:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('任务资源关联删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'agriculture:taskLand:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('任务资源关联导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'agriculture:taskLand:export',       '#', 'admin', sysdate(), '', null, '');