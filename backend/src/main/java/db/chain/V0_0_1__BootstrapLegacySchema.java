package db.chain;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import java.sql.Connection;
import java.util.List;

public class V0_0_1__BootstrapLegacySchema extends BaseJavaMigration {

    private static final List<String> SCRIPTS = List.of(
            "sql/workflow_schema.sql",
            "sql/rbac_schema.sql",
            "sql/rbac_migration.sql",
            "sql/rbac_seed.sql",
            "sql/oauth2_schema.sql",
            "sql/sales_return_schema.sql",
            "sql/inventory_finance_schema.sql",
            "sql/doc_no_rule_schema.sql",
            "sql/product_spu_alter.sql",
            "sql/customer_level_migration.sql",
            "sql/product_select_indexes.sql"
    );

    @Override
    public void migrate(Context context) {
        execute(context.getConnection(), SCRIPTS);
    }

    private void execute(Connection connection, List<String> scripts) {
        SingleConnectionDataSource dataSource = new SingleConnectionDataSource(connection, true);
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        scripts.forEach(path -> populator.addScript(new ClassPathResource(path)));
        populator.execute(dataSource);
    }
}
