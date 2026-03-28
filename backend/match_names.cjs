const fs = require('fs');
const path = require('path');
const mysql = require('mysql2');

const viewsDir = path.join(__dirname, '../frontend/src/views');
const routerFile = path.join(__dirname, '../frontend/src/router/index.ts');

const frontendMap = new Map();

// 1. Extract from router
const routerContent = fs.readFileSync(routerFile, 'utf-8');
const metaRegex = /meta:\s*\{[^}]*title:\s*['"]([^'"]+)['"][^}]*perm:\s*['"]([^'"]+)['"][^}]*\}/g;
let m;
while ((m = metaRegex.exec(routerContent)) !== null) {
  frontendMap.set(m[2], m[1]);
}

// 2. Extract from Vue views (v-perm)
function scanVueFiles(dir, fileList = []) {
  const files = fs.readdirSync(dir);
  for (const file of files) {
    const fullPath = path.join(dir, file);
    if (fs.statSync(fullPath).isDirectory()) {
      scanVueFiles(fullPath, fileList);
    } else if (fullPath.endsWith('.vue')) {
      fileList.push(fullPath);
    }
  }
  return fileList;
}

const vueFiles = scanVueFiles(viewsDir);
vueFiles.forEach(file => {
  const content = fs.readFileSync(file, 'utf-8');
  const btnRegex = /<[^>]*v-perm\s*=\s*['"](?:\[['"])?([^'"\]]+)(?:['"]\])?['"][^>]*>\s*([^<]+)\s*</g;
  let match;
  while ((match = btnRegex.exec(content)) !== null) {
    const perm = match[1];
    let text = match[2].trim();
    if (text && !text.includes('{{') && !text.includes('\n')) {
      frontendMap.set(perm, text);
    }
  }
});

// Manual additions for some tricky buttons with icons or variables
const manual = {
  'settings:op-log:clear': '清空',
  'system:user:resetpwd': '重置密码',
  'system:user:grant': '分配角色',
  'system:role:grant': '分配权限',
  'data:customer:add': '新增',
  'data:customer:edit': '编辑',
  'data:supplier:add': '新增',
  'data:supplier:edit': '编辑',
  'data:warehouse:add': '新增',
  'data:warehouse:edit': '编辑',
  'sales:return:add': '退货',
  'sales:return:audit': '审核',
  'sales:return:view': '详情',
  'sales:return:delete': '删除',
  'sales:order:convert': '转销售',
  'sales:order:delete': '删除',
  'sales:order:add': '新增销售单',
  'purchase:order:add': '新增进货单',
  'purchase:return:add': '新增退货单',
  'inventory:stock-in:add': '新增其他入库单',
  'inventory:stock-out:add': '新增其他出库单',
  'inventory:transfer:add': '新增调拨单',
  'inventory:assembly:add': '新增组装单',
  'inventory:check:add': '新增盘点单',
  'settings:doc-no-rule:add': '新增规则',
  'settings:doc-no-rule:edit': '修改',
  'system:user:add': '新增',
  'system:user:edit': '修改',
  'system:user:delete': '删除',
  'system:role:add': '新增',
  'system:role:edit': '修改',
  'system:role:delete': '删除',
  'system:dept:add': '新增',
  'system:dept:edit': '修改',
  'system:dept:delete': '删除',
  'system:post:add': '新增',
  'system:post:edit': '修改',
  'system:post:delete': '删除',
  'system:menu:add': '新增',
  'system:menu:edit': '修改',
  'system:menu:delete': '删除',
  'finance:payable:add': '新增',
  'finance:payable:edit': '修改',
  'finance:payable:delete': '删除',
  'finance:receivable:add': '新增',
  'finance:receivable:edit': '修改',
  'finance:receivable:delete': '删除',
  'finance:receipt:add': '新增',
  'finance:receipt:edit': '修改',
  'finance:receipt:delete': '删除',
  'finance:payment:add': '新增',
  'finance:payment:edit': '修改',
  'finance:payment:delete': '删除',
  'finance:transaction:add': '新增',
  'finance:transaction:delete': '删除',
  'finance:write-off:add': '新增',
  'finance:write-off:edit': '修改',
  'finance:write-off:delete': '删除',
  'finance:income-expense:add': '新增',
  'finance:income-expense:edit': '修改',
  'finance:income-expense:delete': '删除'
};

for (const [k, v] of Object.entries(manual)) {
  if (!frontendMap.has(k)) {
    frontendMap.set(k, v);
  }
}

const connection = mysql.createConnection({
  host: '127.0.0.1',
  user: 'root',
  password: 'Root@1234',
  database: 'duoduo_jxc'
});

connection.query("SELECT menu_id, menu_name, perms FROM jxc_sys_menu WHERE perms IS NOT NULL AND perms != ''", (err, results) => {
  if (err) throw err;
  
  const updates = [];
  const deletes = [];
  
  results.forEach(row => {
    const perm = row.perms;
    const dbName = row.menu_name;
    const frontName = frontendMap.get(perm);
    
    if (!frontName) {
      deletes.push(row.menu_id);
    } else if (dbName !== frontName) {
      updates.push(`UPDATE jxc_sys_menu SET menu_name = '${frontName}' WHERE menu_id = ${row.menu_id};`);
    }
  });
  
  if (deletes.length > 0) {
    console.log(`-- Deleting ${deletes.length} unused menus`);
    console.log(`DELETE FROM jxc_sys_role_menu WHERE menu_id IN (${deletes.join(',')});`);
    console.log(`DELETE FROM jxc_sys_menu WHERE menu_id IN (${deletes.join(',')});`);
  }
  
  if (updates.length > 0) {
    console.log(`-- Updating ${updates.length} menu names`);
    console.log(updates.join('\n'));
  }
  
  process.exit();
});
