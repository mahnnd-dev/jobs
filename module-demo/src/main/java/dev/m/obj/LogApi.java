package dev.m.obj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogApi {
    String request;
    String response;
    int status = 0;
    String msg;
    String channel;
    String logDate;
    String accPartner;
    long reqId = 0;
    long timeProcess = 0;
    String msisdn;
    String accChange;
}
