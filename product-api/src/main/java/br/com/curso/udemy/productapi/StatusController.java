package br.com.curso.udemy.productapi;

import feign.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class StatusController {

    @GetMapping("status")
    public ResponseEntity<Map<String, Object>> getApiStatus(){
        var response = new HashMap<String, Object>();

        response.put("service", "Product-api");
        response.put("status", "up");

        return ResponseEntity.ok(response);

    }

}
