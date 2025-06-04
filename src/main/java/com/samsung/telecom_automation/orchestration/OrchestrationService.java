package com.samsung.telecom_automation.orchestration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.samsung.telecom_automation.runtime.RuntimeService;

@Service
public class OrchestrationService {
    private final RuntimeService runtimeService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public OrchestrationService(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    public void updateDesiredState(String serviceIntent) throws Exception {
        // Parse JSON input
        JsonNode intent = objectMapper.readTree(serviceIntent);
        int replicas = intent.has("replicas") ? intent.get("replicas").asInt(2) : 2;
        String image = intent.has("image") ? intent.get("image").asText("nginx:latest") : "nginx:latest";
        String appName = intent.has("appName") ? intent.get("appName").asText("example") : "example";

        // Generate dynamic YAML
        String yaml = """
                apiVersion: apps/v1
                kind: Deployment
                metadata:
                  name: %s-deployment
                spec:
                  replicas: %d
                  selector:
                    matchLabels:
                      app: %s
                  template:
                    metadata:
                      labels:
                        app: %s
                    spec:
                      containers:
                      - name: %s-container
                        image: %s
                """.formatted(appName, replicas, appName, appName, appName, image);
        runtimeService.updateDesiredState("desired-state.yaml", yaml);
    }
}