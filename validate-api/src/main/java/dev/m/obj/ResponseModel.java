package dev.m.obj;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Full ResponseApi trả về cho client
 * Code được ánh xạ trong bảng: HttpStatus
 * Trường total thể hiện tổng số bản ghi của lần truy vấn
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseModel {
    @JsonProperty(value = "status")
    private String status;
    @JsonProperty(value = "message")
    private String message;
    @JsonProperty(value = "req_id")
    private String requestId;
}
