import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestBcrypt {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("Hash matches admin123: " + encoder.matches("admin123", "$2a$10$N.zmdr9k7uOCQb3ZQMKT.eX7mI9v0g6nLXB0Z0w0qA0z6w0gJf0Ca"));
        System.out.println("New hash for admin123: " + encoder.encode("admin123"));
    }
}
