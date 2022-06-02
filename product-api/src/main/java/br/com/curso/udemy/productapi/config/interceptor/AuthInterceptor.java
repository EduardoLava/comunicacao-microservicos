package br.com.curso.udemy.productapi.config.interceptor;

import br.com.curso.udemy.productapi.config.exception.ValidationException;
import br.com.curso.udemy.productapi.modules.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.UUID;

import static org.springframework.util.ObjectUtils.*;

public class AuthInterceptor implements HandlerInterceptor {

    private static final String AUTHORIZATION = "Authorization";
    private static final String TRANSACTION_ID = "transactionid";
    private static final String SERVICE_ID = "serviceid";

    @Autowired
    private JwtService jwtService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(isOptions(request)){
            return true;
        }

        if(isEmpty(request.getHeader(TRANSACTION_ID))){
            throw new ValidationException("The transactionid header is required");
        }

        var authorization = request.getHeader(AUTHORIZATION);
        jwtService.validateAuthorization(authorization);
        request.setAttribute(SERVICE_ID, UUID.randomUUID().toString());
        return true;
    }

    private boolean isOptions(HttpServletRequest request){
        return HttpMethod.OPTIONS.equals(request.getMethod());
    }
}
