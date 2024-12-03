package dev.m.obj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiMsisdnApp {
    String msisdn = "";
    int type = 0;
    int sysId = 0;
    int sri = 0;
    String imsi = "";
    int apiSyncId = 0;
    int active = 0;
    String insertDate = "";
    String editUser = "";
    String editDate = "";
}

