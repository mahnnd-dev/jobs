package dev.m.obj;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestModal {
    @JsonProperty(value = "user_name")
    String userName;
    @JsonProperty(value = "password")
    String password;
    @JsonProperty(value = "channel")
    String channel;
    @JsonProperty(value = "msisdn")
    String msisdn;
    @JsonProperty(value = "account")
    String account;
    @JsonProperty(value = "sri")
    String sri;
    @JsonProperty(value = "active")
    String active;
    @JsonProperty(value = "token")
    String token;
    @JsonProperty(value = "check_sum")
    String checkSum;
}
