package Util;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class JwtUtil
{
    //expireTime传入的参数为秒
    public static String createToken(Long id,String info,String secret,Integer expireTime)
    {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        String token = JWT.create()
                .withSubject(id.toString())
                .withAudience(info)
                .withExpiresAt(new Date(System.currentTimeMillis()+expireTime*1000))
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .sign(algorithm);
        return token;
    }


    public static boolean verifyToken(String token,String secret)
    {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try
        {
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    public static String getInfo(String token,String secret)
    {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedToken = verifier.verify(token);
        return decodedToken.getAudience().getFirst();
    }
}
