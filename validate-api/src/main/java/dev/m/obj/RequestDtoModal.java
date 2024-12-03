package dev.m.obj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDtoModal {
    @NotBlank
    @Size(min = 2, max = 30)
    private String username;

    @Email
    @NotBlank (message = "mèo méo meo mèo meo")
    private String email;
}
