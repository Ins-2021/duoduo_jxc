const mysql = require('mysql2');
const connection = mysql.createConnection({
  host: '127.0.0.1',
  user: 'root',
  password: 'Root@1234',
  database: 'duoduo_jxc'
});
connection.query('SELECT menu_id, menu_name FROM jxc_sys_menu LIMIT 5', (err, results) => {
  if (err) console.error(err);
  else console.log("RESULTS:", results);
  process.exit();
});
