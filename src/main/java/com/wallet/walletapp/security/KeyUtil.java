package com.wallet.walletapp.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class KeyUtil {

    private final ResourceLoader resourceLoader;

    // generate private_key.pem command: [openssl genpkey -algorithm RSA -out private_key.pem -pkeyopt rsa_keygen_bits:2048]
    @Value("${application.security.jwt.public-key.path}")
    private String PUBLIC_KEY_PATH;
    // generate public_key.pem command: [openssl rsa -in private_key.pem -pubout -out public_key.pem]
    @Value("${application.security.jwt.private-key.path}")
    private String PRIVATE_KEY_PATH;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public PrivateKey getPrivateKey() {
        if (privateKey == null) {
            try {
                Resource resource = resourceLoader.getResource(PRIVATE_KEY_PATH);
                String keyContent = readKeyContent(resource);
                String privateKeyPEM = keyContent
                        .replace("-----BEGIN PRIVATE KEY-----", "")
                        .replace("-----END PRIVATE KEY-----", "")
                        .replaceAll("\\s+", "");

                byte[] decodedKey = Base64.getDecoder().decode(privateKeyPEM);
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                privateKey = keyFactory.generatePrivate(keySpec);
            } catch (Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }
        return privateKey;
    }

    public PublicKey getPublicKey() {
        if (publicKey == null) {
            try {
                Resource resource = resourceLoader.getResource(PUBLIC_KEY_PATH);
                String keyContent = readKeyContent(resource);
                String publicKeyPEM = keyContent
                        .replace("-----BEGIN PUBLIC KEY-----", "")
                        .replace("-----END PUBLIC KEY-----", "")
                        .replaceAll("\\s+", "");

                byte[] decodedKey = Base64.getDecoder().decode(publicKeyPEM);
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                publicKey = keyFactory.generatePublic(keySpec);
            } catch (Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }
        return publicKey;
    }

    private String readKeyContent(Resource resource) {
        try (InputStream inputStream = resource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}