const mysql = require('mysql2');
const connection = mysql.createConnection({
  host: '127.0.0.1',
  user: 'duoduo',
  password: 'Duoduo@1234',
  database: 'duoduo_jxc'
});
connection.query('SHOW CREATE TABLE jxc_sys_menu', (err, results) => {
  if (err) console.error(err);
  else console.log(results[0]['Create Table']);
  process.exit();
});
