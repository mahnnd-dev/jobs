package dev.m.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class DetectService {
    private final ExportDLLService exportDLLService;
    private final GenerateDataService generateDataService;
    private final DatabaseStatsTool databaseStatsTool;
    @Value("${detection.module.type-code}")
    private String type;

    @Autowired
    public DetectService(ExportDLLService exportDLLService, GenerateDataService generateDataService, DatabaseStatsTool databaseStatsTool) {
        this.exportDLLService = exportDLLService;
        this.generateDataService = generateDataService;
        this.databaseStatsTool = databaseStatsTool;
    }


    @PostConstruct
    public void init() {
        log.info("#Init DetectService type: {}", type);
        if (type.equals("DLL"))
            exportDLLService.processExport();
        if (type.equals("DATA"))
            generateDataService.generateTable();
        if (type.equals("TOOL"))
            databaseStatsTool.run();
    }
}
