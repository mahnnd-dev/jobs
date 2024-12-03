package dev.m.obj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiAccountUpdate {
    String account;
    String accPartner;
    String password;
    String listIp;
    String sysId;
    String apiSyncId;
    String privateKey;
    String token;
    int isReport = 0;
    int maxLength = 0;
    int tps = 0;
}
