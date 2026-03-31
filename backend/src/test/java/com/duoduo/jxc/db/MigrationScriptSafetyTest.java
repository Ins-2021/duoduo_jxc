package com.duoduo.jxc.db;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MigrationScriptSafetyTest {

    private static final List<Path> MIGRATION_DIRECTORIES = List.of(
            Path.of("src/main/resources/sql"),
            Path.of("src/main/resources/db/migration")
    );

    private static final Map<String, Pattern> FORBIDDEN_PATTERNS = new LinkedHashMap<>();
    private static final List<Pattern> UNSAFE_UPDATE_WHERE_PATTERNS = List.of(
            Pattern.compile("(?is)^\\(*\\s*1\\s*=\\s*1\\s*\\)*$"),
            Pattern.compile("(?is)^\\(*\\s*0\\s*=\\s*0\\s*\\)*$"),
            Pattern.compile("(?is)^\\(*\\s*true\\s*\\)*$"),
            Pattern.compile("(?is)^\\(*\\s*1\\s*(<>|!=)\\s*0\\s*\\)*$")
    );

    static {
        FORBIDDEN_PATTERNS.put("DROP TABLE", Pattern.compile("(?is)\\bdrop\\s+table\\b"));
        FORBIDDEN_PATTERNS.put("TRUNCATE TABLE", Pattern.compile("(?is)\\btruncate\\s+table\\b"));
        FORBIDDEN_PATTERNS.put("DROP DATABASE", Pattern.compile("(?is)\\bdrop\\s+database\\b"));
        FORBIDDEN_PATTERNS.put("DELETE FROM", Pattern.compile("(?is)\\bdelete\\s+from\\b"));
        FORBIDDEN_PATTERNS.put("ALTER TABLE DROP COLUMN", Pattern.compile("(?is)\\balter\\s+table\\b[\\s\\S]*?\\bdrop\\s+column\\b"));
    }

    @Test
    void currentMigrationScriptsShouldNotContainDestructiveStatements() throws IOException {
        for (Path directory : MIGRATION_DIRECTORIES) {
            assertTrue(Files.exists(directory), "目录不存在: " + directory);
            try (Stream<Path> stream = Files.walk(directory)) {
                for (Path script : stream.filter(path -> path.toString().endsWith(".sql")).toList()) {
                    String content = stripComments(Files.readString(script, StandardCharsets.UTF_8));
                    for (Map.Entry<String, Pattern> entry : FORBIDDEN_PATTERNS.entrySet()) {
                        assertFalse(
                                entry.getValue().matcher(content).find(),
                                () -> "迁移脚本包含危险语句 [" + entry.getKey() + "]: " + script
                        );
                    }
                    assertNoUnsafeFullTableUpdate(script, content);
                }
            }
        }
    }

    @Test
    void detectorShouldCatchDestructiveStatements() {
        String dangerousSql = """
                CREATE TABLE demo(id BIGINT);
                DELETE FROM demo;
                ALTER TABLE demo DROP COLUMN id;
                """;
        String content = stripComments(dangerousSql);
        assertTrue(FORBIDDEN_PATTERNS.get("DELETE FROM").matcher(content).find());
        assertTrue(FORBIDDEN_PATTERNS.get("ALTER TABLE DROP COLUMN").matcher(content).find());
    }

    @Test
    void detectorShouldCatchUpdateWithoutWhere() {
        String dangerousSql = """
                UPDATE demo SET status = 0;
                """;
        IllegalStateException exception = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalStateException.class,
                () -> assertNoUnsafeFullTableUpdate(Path.of("demo.sql"), stripComments(dangerousSql))
        );
        assertTrue(exception.getMessage().contains("UPDATE"));
    }

    @Test
    void detectorShouldCatchUpdateWithPseudoWhere() {
        String dangerousSql = """
                UPDATE demo SET status = 0 WHERE 1 = 1;
                UPDATE demo SET status = 1 WHERE TRUE;
                """;
        IllegalStateException exception = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalStateException.class,
                () -> assertNoUnsafeFullTableUpdate(Path.of("demo.sql"), stripComments(dangerousSql))
        );
        assertTrue(exception.getMessage().contains("伪条件"));
    }

    private String stripComments(String sql) {
        String withoutBlockComments = sql.replaceAll("(?is)/\\*.*?\\*/", " ");
        return withoutBlockComments.replaceAll("(?m)^\\s*--.*$", " ");
    }

    private void assertNoUnsafeFullTableUpdate(Path script, String content) {
        Arrays.stream(content.split(";"))
                .map(String::trim)
                .filter(statement -> !statement.isEmpty())
                .filter(statement -> statement.matches("(?is)^update\\b[\\s\\S]*"))
                .findFirst()
                .ifPresent(statement -> {
                    String whereClause = extractWhereClause(statement);
                    if (whereClause == null) {
                        throw new IllegalStateException("迁移脚本包含无条件 UPDATE: " + script + " -> " + statement);
                    }
                    if (isUnsafePseudoWhere(whereClause)) {
                        throw new IllegalStateException("迁移脚本包含伪条件全表 UPDATE: " + script + " -> " + statement);
                    }
                });
    }

    private String extractWhereClause(String statement) {
        java.util.regex.Matcher matcher = Pattern.compile("(?is)^update\\b[\\s\\S]*?\\bwhere\\b([\\s\\S]*)$").matcher(statement);
        if (!matcher.matches()) {
            return null;
        }
        return matcher.group(1).trim();
    }

    private boolean isUnsafePseudoWhere(String whereClause) {
        return UNSAFE_UPDATE_WHERE_PATTERNS.stream()
                .anyMatch(pattern -> pattern.matcher(whereClause).matches());
    }
}
