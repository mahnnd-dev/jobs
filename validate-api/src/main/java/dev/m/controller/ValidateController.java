package dev.m.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Log4j2
@Validated
@RestController
@RequiredArgsConstructor
public class ValidateController {

    @PostMapping("/api/example")
    public String example(
            @RequestParam @NotNull(message = "ID is required") Long id,
            @RequestParam @Min(value = 1, message = "The value must be greater than or equal to 1") int quantity) {
        return "ID: " + id + ", Quantity: " + quantity;
    }

    @PostMapping("/api/validateParams")
    public String validateParams(
            @RequestParam @NotBlank (message = "mèo méo meo mèo meo") String name,
            @RequestParam @Min(1) @Max(100) int age) {
        return "Name: " + name + ", Age: " + age;
    }
}
