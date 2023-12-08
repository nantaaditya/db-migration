package com.nantaaditya.dbmigration.util;

import com.nantaaditya.dbmigration.properties.CredentialProperties;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class RSAUtil {

  private CredentialProperties credentialProperties;

  private PrivateKey privateKey;

  private PublicKey publicKey;

  private KeyFactory keyFactory;

  private Cipher cipher;

  private static final String VALUE_FORMAT = "%s|%s|%s";

  static {
    Security.addProvider(new BouncyCastleProvider());
  }

  public RSAUtil(CredentialProperties credentialProperties) {
    this.credentialProperties = credentialProperties;

    try {
      keyFactory = KeyFactory.getInstance("RSA");
      X509EncodedKeySpec publicKeySpecification = new X509EncodedKeySpec(Base64.getDecoder()
          .decode(this.credentialProperties.getPublicKey().getBytes())
      );
      PKCS8EncodedKeySpec privateKeySpecification = new PKCS8EncodedKeySpec(Base64.getDecoder()
          .decode(this.credentialProperties.getPrivateKey().getBytes())
      );

      this.publicKey = keyFactory.generatePublic(publicKeySpecification);
      this.privateKey = keyFactory.generatePrivate(privateKeySpecification);
      this.cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
    } catch (NoSuchAlgorithmException ex) {
      log.error("#RSA - no such algorithm, ", ex);
    } catch (InvalidKeySpecException ex) {
      log.error("#RSA - invalid key spec, ", ex);
    } catch (NoSuchPaddingException ex) {
      log.error("#RSA - no such padding, ", ex);
    }
  }

  @SneakyThrows
  public String encrypt(String plainValue) {
    cipher.init(Cipher.ENCRYPT_MODE, this.publicKey);
    byte[] bytes = cipher.doFinal(plainValue.getBytes());
    return Base64.getEncoder().encodeToString(bytes);
  }

  @SneakyThrows
  public String encryptPassword(String plainValue) {
    String passwordFormat = String.format(VALUE_FORMAT,
        credentialProperties.getPrefix(), plainValue, credentialProperties.getPostfix());
    return encrypt(passwordFormat);
  }

  @SneakyThrows
  public String decrypt(String encryptedValue) {
    if (!StringUtils.hasLength(encryptedValue)) return encryptedValue;

    cipher.init(Cipher.DECRYPT_MODE, privateKey);
    byte[] encryptedBytes = Base64.getDecoder().decode(encryptedValue.getBytes());
    byte[] bytes = cipher.doFinal(encryptedBytes);
    return new String(bytes);
  }

  public String decryptPassword(String encryptedValue) {
    return decrypt(encryptedValue).split("\\|")[1];
  }
}
