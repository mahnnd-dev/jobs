package dev.m.controller;

import dev.m.anotation.ValidateParams;
import dev.m.obj.RequestModal;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Log4j2
@RestController
@RequiredArgsConstructor
public class AopValidController {

    @ValidateParams
    @PostMapping("/param")
    public ResponseEntity<?> param(@RequestParam() Map<String, String> map) {
        // xử lý logic sau khi validate
        return ResponseEntity.ok("Success: " + map);
    }

    @ValidateParams
    @PostMapping("/body")
    public ResponseEntity<?> body(@RequestBody RequestModal requestModal) {
        // xử lý logic sau khi validate
        return ResponseEntity.ok("Success: " + requestModal);
    }
}
