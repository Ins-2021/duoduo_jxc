# 数据库脚本执行说明

本目录用于存放临时数据库迁移脚本，脚本执行后应删除。

## 执行方式

```bash
# 使用Docker执行（推荐）
cat script.sql | docker exec -i duoduo-mysql mysql -uroot -p'Duoduo@1234' --default-character-set=utf8mb4 duoduo_jxc

# 或进入MySQL后执行
docker exec -it duoduo-mysql mysql -uroot -p'Duoduo@1234' --default-character-set=utf8mb4 duoduo_jxc
source /path/to/script.sql;
```

## 注意事项

1. 所有脚本使用 UTF-8 编码
2. 脚本执行后请删除，避免混淆
3. 建议在脚本中添加 `INSERT IGNORE` 或 `ON DUPLICATE KEY UPDATE` 以支持重复执行
