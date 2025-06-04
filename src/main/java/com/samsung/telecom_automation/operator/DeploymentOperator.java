package com.samsung.telecom_automation.operator;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.util.Yaml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.samsung.telecom_automation.runtime.RuntimeService;

@Component
public class DeploymentOperator {
    private final RuntimeService runtimeService;
    private final AppsV1Api appsV1Api;

    @Autowired
    public DeploymentOperator(RuntimeService runtimeService, ApiClient apiClient) {
        this.runtimeService = runtimeService;
        this.appsV1Api = new AppsV1Api(apiClient);
    }

    @Scheduled(fixedRate = 60000)
    public void reconcile() {
        try {
            String yamlContent = runtimeService.getDesiredState("desired-state.yaml");
            if (yamlContent == null) {
                System.out.println("No desired state found, skipping reconciliation");
                return;
            }
            V1Deployment deployment = (V1Deployment) Yaml.load(yamlContent);
            String deploymentName = deployment.getMetadata().getName();
            System.out.println("Reconciling deployment: " + deploymentName);
            try {
                V1Deployment existing = appsV1Api.readNamespacedDeployment(deploymentName, "default").execute();
                System.out.println("Updating deployment: " + deploymentName + ", current replicas: " + existing.getSpec().getReplicas());
                appsV1Api.replaceNamespacedDeployment(deploymentName, "default", deployment);
                System.out.println("Updated deployment: " + deploymentName);
            } catch (ApiException e) {
                if (e.getCode() == 404) {
                    System.out.println("Creating deployment: " + deploymentName);
                    V1Deployment created = appsV1Api.createNamespacedDeployment("default", deployment).execute();
                    System.out.println("Created deployment: " + deploymentName + ", replicas: " + created.getSpec().getReplicas());
                } else {
                    System.out.println("Kubernetes API error: code=" + e.getCode() + ", body=" + e.getResponseBody());
                    throw e;
                }
            }
            System.out.println("Deployment successfully synced deployment: " + deploymentName);
        } catch (Exception e) {
            System.out.println("Deployment sync failed : " + e.getMessage());
        }
    }
}