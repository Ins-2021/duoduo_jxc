package com.duoduo.jxc.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class JwtKeyConfig {

    @Value("${jxc.security.jwt.key-dir:./data/keys}")
    private String jwtKeyDir;

    @Bean
    public KeyPair jwtKeyPair() throws Exception {
        Path dir = Path.of(jwtKeyDir);
        Path privateKeyPath = dir.resolve("jwt-private.pem");
        Path publicKeyPath = dir.resolve("jwt-public.pem");

        if (Files.exists(privateKeyPath) && Files.exists(publicKeyPath)) {
            PrivateKey privateKey = readPrivateKey(privateKeyPath);
            PublicKey publicKey = readPublicKey(publicKeyPath);
            return new KeyPair(publicKey, privateKey);
        }

        Files.createDirectories(dir);
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        KeyPair keyPair = gen.generateKeyPair();
        writePem(privateKeyPath, "PRIVATE KEY", keyPair.getPrivate().getEncoded());
        writePem(publicKeyPath, "PUBLIC KEY", keyPair.getPublic().getEncoded());
        
        // 设置私钥文件仅所有者可读写 (600)
        privateKeyPath.toFile().setReadable(false, false);
        privateKeyPath.toFile().setReadable(true, true);
        privateKeyPath.toFile().setWritable(false, false);
        privateKeyPath.toFile().setWritable(true, true);
        
        return keyPair;
    }

    @Bean
    public RSAKey rsaKey(KeyPair jwtKeyPair) {
        RSAPublicKey publicKey = (RSAPublicKey) jwtKeyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) jwtKeyPair.getPrivate();
        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(computeKid(publicKey.getEncoded()))
                .build();
    }

    @Bean
    public JWKSet jwkSet(RSAKey rsaKey) {
        return new JWKSet(rsaKey.toPublicJWK());
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource(RSAKey rsaKey) {
        return new ImmutableJWKSet<>(new JWKSet(rsaKey));
    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtDecoder jwtDecoder(KeyPair jwtKeyPair, JwtBlacklistValidator jwtBlacklistValidator) {
        RSAPublicKey publicKey = (RSAPublicKey) jwtKeyPair.getPublic();
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(publicKey).build();
        decoder.setJwtValidator(jwtBlacklistValidator);
        return decoder;
    }

    private static PrivateKey readPrivateKey(Path path) throws Exception {
        String pem = Files.readString(path, StandardCharsets.UTF_8);
        String body = pem.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] der = Base64.getDecoder().decode(body);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(new PKCS8EncodedKeySpec(der));
    }

    private static PublicKey readPublicKey(Path path) throws Exception {
        String pem = Files.readString(path, StandardCharsets.UTF_8);
        String body = pem.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] der = Base64.getDecoder().decode(body);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(new X509EncodedKeySpec(der));
    }

    private static void writePem(Path path, String type, byte[] derBytes) throws Exception {
        String b64 = Base64.getEncoder().encodeToString(derBytes);
        StringBuilder sb = new StringBuilder();
        sb.append("-----BEGIN ").append(type).append("-----\n");
        for (int i = 0; i < b64.length(); i += 64) {
            sb.append(b64, i, Math.min(i + 64, b64.length())).append("\n");
        }
        sb.append("-----END ").append(type).append("-----\n");
        Files.writeString(path, sb.toString(), StandardCharsets.UTF_8);
    }

    private static String computeKid(byte[] publicKeyDer) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(publicKeyDer);
            byte[] first = new byte[16];
            System.arraycopy(digest, 0, first, 0, 16);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(first);
        } catch (Exception e) {
            throw new RuntimeException("计算 KID 失败", e);
        }
    }
}
