package com.dorohedoro.util;

import com.dorohedoro.constant.AuthConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import sun.misc.BASE64Decoder;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class JWTUtil {
    public static String generateToken(Integer expire, String data) throws Exception {
        if (expire <= 0) {
            expire = AuthConstant.DEFAULT_EXPIRE_DAY;
        }
        
        ZonedDateTime zonedDateTime = LocalDate.now().plus(expire, ChronoUnit.DAYS)
                .atStartOfDay(ZoneId.systemDefault());
        Date expireDate = Date.from(zonedDateTime.toInstant());

        return Jwts.builder()
                .claim(AuthConstant.KEY, data)
                .setId(UUID.randomUUID().toString())
                .setExpiration(expireDate)
                .signWith(getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    public static Claims decryptData(String token) throws Exception {
        if (token == null) {
            return null;
        }

        Claims data = Jwts.parser().
                setSigningKey(getPublicKey()).
                parseClaimsJws(token).
                getBody();

        if (data.getExpiration().before(new Date())) {
            return null;
        }

        return data;
    }
    
    public static PrivateKey getPrivateKey() throws Exception {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(
                new BASE64Decoder().decodeBuffer(AuthConstant.PRIVATE_KEY)
        );
        return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    }
    
    public static PublicKey getPublicKey() throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(
                new BASE64Decoder().decodeBuffer(AuthConstant.PUBLIC_KEY)
        );
        return KeyFactory.getInstance("RSA").generatePublic(keySpec);
    }
}
