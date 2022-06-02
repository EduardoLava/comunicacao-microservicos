package br.com.curso.udemy.productapi.config.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

import static br.com.curso.udemy.productapi.config.RequestUtil.*;

@Component
public class FeignClientAuthInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION = "Authorization";
    private static final String TRANSACTION_ID = "transactionid";

    @Override
    public void apply(RequestTemplate template) {
        var currentRequest = getCurrentRequest();
        System.out.println(currentRequest.getHeader(AUTHORIZATION));
        template.header(AUTHORIZATION, currentRequest.getHeader(AUTHORIZATION));
        template.header(TRANSACTION_ID, currentRequest.getHeader(TRANSACTION_ID));
    }

}
