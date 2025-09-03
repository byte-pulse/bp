package cloud.bytepulse.common.core.util;

import cloud.bytepulse.common.core.exception.BytePulseException;
import cloud.bytepulse.common.core.exception.enums.AuthErrorCode;
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
}
