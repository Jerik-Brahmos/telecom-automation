package com.samsung.telecom_automation.runtime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RuntimeService {
    private final RuntimeRepository runtimeRepository;

    @Autowired
    public RuntimeService(RuntimeRepository runtimeRepository) {
        this.runtimeRepository = runtimeRepository;
    }

    public void updateDesiredState(String fileName, String yamlContent) throws Exception {
        runtimeRepository.saveDesiredState(fileName, yamlContent);
    }

    public String getDesiredState(String fileName) throws Exception {
        return runtimeRepository.getDesiredState(fileName);
    }
}