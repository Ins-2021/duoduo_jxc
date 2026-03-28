const mysql = require('mysql2');
const connection = mysql.createConnection({
  host: '127.0.0.1',
  user: 'root',
  password: 'Root@1234',
  database: 'duoduo_jxc'
});
connection.query("SELECT menu_id, menu_name, perms FROM jxc_sys_menu WHERE menu_name LIKE '%账户%' OR menu_name LIKE '%资金%'", (err, results) => {
  if (err) console.error(err);
  else console.log("RESULTS:", results);
  process.exit();
});
