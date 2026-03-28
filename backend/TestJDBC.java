import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TestJDBC {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://127.0.0.1:3306/duoduo_jxc?useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai";
        String user = "root";
        String password = "Root@1234";
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected to database");
            try (PreparedStatement pstmt = conn.prepareStatement("UPDATE jxc_sys_menu SET menu_name = ? WHERE menu_id = 100")) {
                pstmt.setString(1, "销售(JDBC)");
                pstmt.executeUpdate();
            }
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT menu_name FROM jxc_sys_menu WHERE menu_id = 100")) {
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Read back: " + rs.getString("menu_name"));
                    }
                }
            }
        }
    }
}
