package br.com.curso.udemy.productapi.modules.jwt;

import br.com.curso.udemy.productapi.config.exception.AuthorizationException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class JwtService {

    private static final String BEARER = "bearer";
    private static final String SPACE = " ";

    @Value("${app-config.secrets.api-secret}")
    private String apiSecret;

    public void validateAuthorization(String token){
        var accessToken = extractToken(token);
        try{
            System.out.println(apiSecret);
            var claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(apiSecret.getBytes()))
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
            var user = JwtResponse.getUser(claims);
            if(ObjectUtils.isEmpty(user) || ObjectUtils.isEmpty(user.getId())){
                throw new AuthorizationException("The user is not valid");
            }
        }catch (Exception exception){
            exception.printStackTrace();
            throw new AuthorizationException("Error while trying to proccess the Access Token");
        }
    }

    private String extractToken(String token){
        if(ObjectUtils.isEmpty(token)){
            throw new AuthorizationException("The access token was not informed.");
        }
        if(token.toLowerCase().contains(BEARER)){
            token = token.split(SPACE)[1];
        }
        return token;
    }
}
