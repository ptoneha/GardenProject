-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('种植决策方案', '1061', '1', 'result', 'agriculture/result/index', 1, 0, 'C', '0', '0', 'agriculture:result:list', '#', 'admin', sysdate(), '', null, '种植决策方案菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('种植决策方案查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'agriculture:result:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('种植决策方案新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'agriculture:result:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('种植决策方案修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'agriculture:result:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('种植决策方案删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'agriculture:result:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('种植决策方案导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'agriculture:result:export',       '#', 'admin', sysdate(), '', null, '');