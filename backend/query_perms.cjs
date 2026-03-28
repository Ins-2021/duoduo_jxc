const mysql = require('mysql2');
const connection = mysql.createConnection({
  host: '127.0.0.1',
  user: 'root',
  password: 'Root@1234',
  database: 'duoduo_jxc'
});
connection.query("SELECT perms FROM jxc_sys_menu WHERE perms IS NOT NULL AND perms != ''", (err, results) => {
  if (err) console.error(err);
  else {
    const dbPerms = results.map(r => r.perms);
    console.log(dbPerms.join('\n'));
  }
  process.exit();
});
