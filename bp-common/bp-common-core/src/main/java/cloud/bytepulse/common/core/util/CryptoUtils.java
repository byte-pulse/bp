package cloud.bytepulse.common.core.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * 通用加密工具类，支持 RSA 和 AES 加解密，用于接口通信
 * 支持密钥生成、加密解密，全部使用 Base64 编码格式
 */
public class CryptoUtils {

    private static final int DEFAULT_RSA_KEY_SIZE = 2048; // 默认 RSA 密钥长度
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8; // 默认字符集

    // ========== RSA ==========

    /**
     * 生成 RSA 密钥对
     */
    public static KeyPair generateRSAKeyPair() throws Exception {
        return generateRSAKeyPair(DEFAULT_RSA_KEY_SIZE);
    }

    /**
     * 生成指定长度的 RSA 密钥对
     */
    public static KeyPair generateRSAKeyPair(int keySize) throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(keySize);
        return keyGen.generateKeyPair();
    }

    /**
     * 获取 RSA 公钥的 Base64 字符串
     */
    public static String getBase64PublicKey(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    /**
     * 获取 RSA 私钥的 Base64 字符串
     */
    public static String getBase64PrivateKey(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    /**
     * 使用 RSA 公钥加密字符串
     */
    public static String encryptWithRSA(String plainText, String base64PublicKey) throws Exception {
        PublicKey publicKey = getPublicKeyFromBase64(base64PublicKey);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encrypted = cipher.doFinal(plainText.getBytes(DEFAULT_CHARSET));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * 使用 RSA 私钥解密字符串
     */
    public static String decryptWithRSA(String cipherText, String base64PrivateKey) throws Exception {
        PrivateKey privateKey = getPrivateKeyFromBase64(base64PrivateKey);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(decrypted, DEFAULT_CHARSET);
    }

    private static PublicKey getPublicKeyFromBase64(String base64PublicKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
}
