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

    private static PrivateKey getPrivateKeyFromBase64(String base64PrivateKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64PrivateKey);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    // ========== AES ==========

    /**
     * 生成 AES 密钥（Base64 编码）
     */
    public static String generateAESKey(int keySize) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(keySize);
        SecretKey secretKey = keyGen.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    /**
     * 从 SecretKey 获取 Base64 字符串
     */
    public static String getBase64AESKey(SecretKey secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    /**
     * 从 Base64 获取 AES SecretKey 对象
     */
    public static SecretKey getAESKeyFromBase64(String base64Key) {
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    /**
     * 随机生成安全的 16 字节 IV
     */
    public static IvParameterSpec generateRandomIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    /**
     * 使用 AES 密钥加密明文，传入 IV 参数
     * 返回 Base64 编码密文
     */
    public static String encryptWithAES(String plainText, String base64AESKey, IvParameterSpec ivSpec) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(base64AESKey), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] encrypted = cipher.doFinal(plainText.getBytes(DEFAULT_CHARSET));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * 使用 AES 密钥解密密文，传入 IV 参数
     */
    public static String decryptWithAES(String cipherText, String base64AESKey, IvParameterSpec ivSpec) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(base64AESKey), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(decrypted, DEFAULT_CHARSET);
    }

    /**
     * 将 Base64 编码的字符串转换为 IvParameterSpec 对象
     */
    public static IvParameterSpec base64ToIV(String base64IV) {
        byte[] decodedIV = Base64.getDecoder().decode(base64IV);  // 解码 Base64 字符串
        return new IvParameterSpec(decodedIV);  // 使用解码后的字节数组创建 IvParameterSpec
    }

    /**
     * 将 IvParameterSpec 转换为 Base64 编码的字符串
     */
    public static String IVToBase64(IvParameterSpec ivSpec) {
        byte[] ivBytes = ivSpec.getIV();  // 获取 IvParameterSpec 中的字节数组
        return Base64.getEncoder().encodeToString(ivBytes);  // 编码为 Base64 字符串
    }

    // ========== SHA ==========

    /**
     * 获取 SHA-256 哈希值
     */
    public static String getSHA256(String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");  // 创建 SHA-256 的 MessageDigest 实例
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));  // 获取哈希值
        return Base64.getEncoder().encodeToString(hash);  // 返回 Base64 编码的哈希值
    }

    /**
     * 获取 SHA-256 哈希值 十六进制
     */
    public static String getSHA256Hex(String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));  // 转换为 16 进制
        }
        return hexString.toString();
    }

    /**
     *
     * 获取 Hmac SHA256
     *
     */
    public static String signHmacSHA256(String input, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hmacBytes = mac.doFinal(input.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hmacBytes);
    }

    /**
     *
     * 获取 Hmac SHA256 HEX
     */
    public static String signHmacSHA256Hex(String input, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hmacBytes = mac.doFinal(input.getBytes(StandardCharsets.UTF_8));
        return java.util.HexFormat.of().formatHex(hmacBytes);
    }
}