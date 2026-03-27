INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, component, icon, menu_type, visible, status, perms, deleted) VALUES
  (601, 600, '应收账款', 1, '/finance/receivable', 'FinanceReceivable', NULL, 'C', 1, 1, 'finance:receivable:view', 0),
  (602, 600, '应付账款', 2, '/finance/payable', 'FinancePayable', NULL, 'C', 1, 1, 'finance:payable:view', 0),
  (603, 600, '收款单', 3, '/finance/receipt', 'FinanceReceipt', NULL, 'C', 1, 1, 'finance:receipt:view', 0),
  (604, 600, '付款单', 4, '/finance/payment', 'FinancePayment', NULL, 'C', 1, 1, 'finance:payment:view', 0),
  (605, 600, '资金流水', 5, '/finance/transaction', 'FinanceTransaction', NULL, 'C', 1, 1, 'finance:transaction:view', 0),
  (606, 600, '核销单', 6, '/finance/write-off', 'FinanceWriteOff', NULL, 'C', 1, 1, 'finance:write-off:view', 0),
  (607, 600, '收支记录', 7, '/finance/income-expense', 'FinanceIncomeExpense', NULL, 'C', 1, 1, 'finance:income-expense:view', 0),
  (708, 700, '销售日报查看', 1, NULL, NULL, NULL, 'F', 1, 1, 'report:sales:view', 0),
  (809, 800, '商品查看', 1, NULL, NULL, NULL, 'F', 1, 1, 'data:product:view', 0),
  (909, 906, '字段设置编辑', 1, NULL, NULL, NULL, 'F', 1, 1, 'settings:field-settings:edit', 0),
  (910, 907, '单号规则新增', 1, NULL, NULL, NULL, 'F', 1, 1, 'settings:doc-no-rule:add', 0),
  (911, 907, '单号规则编辑', 2, NULL, NULL, NULL, 'F', 1, 1, 'settings:doc-no-rule:edit', 0),
  (912, 907, '单号规则删除', 3, NULL, NULL, NULL, 'F', 1, 1, 'settings:doc-no-rule:delete', 0),
  (913, 606, '核销单新增', 1, NULL, NULL, NULL, 'F', 1, 1, 'finance:write-off:add', 0),
  (914, 606, '核销单删除', 2, NULL, NULL, NULL, 'F', 1, 1, 'finance:write-off:delete', 0),
  (915, 607, '收支记录新增', 1, NULL, NULL, NULL, 'F', 1, 1, 'finance:income-expense:add', 0),
  (916, 607, '收支记录编辑', 2, NULL, NULL, NULL, 'F', 1, 1, 'finance:income-expense:edit', 0),
  (917, 607, '收支记录删除', 3, NULL, NULL, NULL, 'F', 1, 1, 'finance:income-expense:delete', 0),
  (918, 607, '收支记录审核', 4, NULL, NULL, NULL, 'F', 1, 1, 'finance:income-expense:approve', 0);

INSERT IGNORE INTO jxc_sys_role_menu (role_id, menu_id) VALUES
  (1, 601), (1, 602), (1, 603), (1, 604), (1, 605), (1, 606), (1, 607),
  (1, 708), (1, 809), (1, 909), (1, 910), (1, 911), (1, 912), (1, 913),
  (1, 914), (1, 915), (1, 916), (1, 917), (1, 918);
