const mysql = require('mysql2');
const connection = mysql.createConnection({
  host: '127.0.0.1',
  user: 'root',
  password: '', // wait, DB_PASSWORD from env? Let me check docker compose if it exists
  database: 'duoduo_jxc'
});
connection.query('SELECT menu_id, menu_name, perms FROM jxc_sys_menu WHERE parent_id = 301', (err, results) => {
  if (err) console.log(err);
  else console.log(results);
  process.exit();
});
