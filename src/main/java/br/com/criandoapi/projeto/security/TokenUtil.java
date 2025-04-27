package br.com.criandoapi.projeto.security;

import br.com.criandoapi.projeto.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;
import java.util.Date;

public class TokenUtil {

    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer "; // <-- ESPAÇO AQUI!
    private static final long EXPIRATION = 12 * 60 * 60 * 1000;
    private static final String SECRET_KEY = "MyK3Yt0T0k3nP4r@S3CuRiTY@Sp3c14L";
    private static final String EMISSOR = "DevNice";

    public static String createToken(Usuario usuario) {
        Key secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        String token = Jwts.builder()
                .setSubject(usuario.getNome())
                .setIssuer(EMISSOR)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(secretKey, SignatureAlgorithm.HS256) // <-- HS256 correto aqui
                .compact();
        return PREFIX + token; // adiciona Bearer + espaço
    }

    private static boolean isExpirationValid(Date expiration) {
        return expiration.after(new Date(System.currentTimeMillis()));
    }

    private static boolean isEmissorValid(String emissor) {
        return EMISSOR.equals(emissor);
    }

    private static boolean isSubjectValid(String username) {
        return username != null && !username.isEmpty();
    }

    public static Authentication validate(HttpServletRequest request) {
        try {
            String token = request.getHeader(HEADER);

            if (token == null || !token.startsWith(PREFIX)) {
                return null;
            }

            token = token.replace(PREFIX, ""); // remover "Bearer "

            Jws<Claims> jwsClaims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token);

            String username = jwsClaims.getBody().getSubject();
            String issuer = jwsClaims.getBody().getIssuer();
            Date expiration = jwsClaims.getBody().getExpiration();

            if (isSubjectValid(username) && isEmissorValid(issuer) && isExpirationValid(expiration)) {
                return new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
            }

        } catch (Exception e) {
            return null; // token inválido, apenas ignora
        }

        return null;
    }
}
