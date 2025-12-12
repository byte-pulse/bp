package cloud.bytepulse.bp.common.utils;

import cloud.bytepulse.bp.framework.exception.BytePulseException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.crypto.SecretKey;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * jwt工具类
 *
 * @author jiejiebiezheyang
 * @since 2023-04-03 16:29
 */
public class JWTUtils {

    protected static final byte[] TOKEN_SIGNER = """
            WfayArJun#bW8#cxvs$b2wMZU=w#t3rMHkh@dgBcgSBtEL=&Kpv^Uj3rU4nVWJH&JZWEQxt
            jQuvG^Yf$vBPU8Kq6kYU9By&shsSxLjSTZ2k**5=eS@nfJ*v^DrPrPm7$ngd3S2duBuUJga
            eZ=Y6Jpex*f2jc4axNK!##9x@%p%L^evjbX86af=E&aY6!g%MyT3BcM5QNPqA9^3eC7k9wf
            E4YU&L!qLHS9N4RECnfH#c2A#VyaSn6*vy%F$4*J!mM
            """.getBytes();

    public static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(TOKEN_SIGNER);


    /**
     * 创建token<br>
     * 根据用户id创建token
     */
    public static String createToken(Payload payload, int seconds) {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.SECOND, seconds);
        Date newTime = calendar.getTime();

        return Jwts.builder()
                .issuedAt(now)// 签发时间
                .expiration(newTime)// 过期时间
                .notBefore(now)// 生效时间
                .signWith(SECRET_KEY) // 签名
                .claims(payload) // 内容
                .compact(); // 生成
    }

    /**
     * 解析token<br>
     * 根据传入的key的获取值
     */
    public static String parseToken(String token, String key, boolean verifyTime) throws RuntimeException {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .verifyWith(SECRET_KEY) // 解密
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (Exception e) {
            throw new BytePulseException("token非法");
        }
        if (verifyTime) { // 是否验证过期时间
            // 获取当前时间
            long nowTime = new Date().getTime();
            // 获取过期时间
            Date expiration = claims.getExpiration();
            long l = expiration.getTime() - nowTime;
            if (l <= 0) {
                throw new BytePulseException("token已过期");
            }
        }
        return claims.get(key, String.class);
    }

    /**
     * 解析token<br>
     * 根据传入的key的获取值
     */
    public static String parseToken(String token, String key) {
        return parseToken(token, key, true);
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class Payload extends HashMap<String, String> {
        // 链式添加方法
        public Payload with(String key, String value) {
            this.put(key, value);
            return this;
        }
    }
}
