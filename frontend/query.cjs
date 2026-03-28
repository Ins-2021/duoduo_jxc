const mysql = require('mysql2');
const fs = require('fs');
const connection = mysql.createConnection({
  host: '127.0.0.1',
  user: 'root',
  password: 'Duoduo@1234',
  database: 'duoduo_jxc'
});
connection.query('SELECT menu_id, menu_name, perms FROM jxc_sys_menu WHERE parent_id = 301', (err, results) => {
  if (err) {
    fs.writeFileSync('output.txt', err.toString());
  } else {
    fs.writeFileSync('output.txt', JSON.stringify(results, null, 2));
  }
  connection.end();
});
