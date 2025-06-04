package com.samsung.telecom_automation.config;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileReader;
import java.io.IOException;

@Configuration
public class KubernetesClientConfig {
    @Value("${kubernetes.kubeconfig:}")
    private String kubeconfigPath;

    @Value("${kubernetes.apiServer:}")
    private String apiServer;

    @Bean
    public ApiClient kubernetesApiClient() throws IOException {
        if (!kubeconfigPath.isEmpty()) {
            // Load from explicit kubeconfig file
            try (FileReader reader = new FileReader(kubeconfigPath)) {
                return ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(reader)).build();
            }
        } else if (!apiServer.isEmpty()) {
            // Use API server URL directly (insecure, for testing)
            return new ClientBuilder().setBasePath(apiServer).build();
        } else {
            // Fallback to default kubeconfig (~/.kube/config)
            return ClientBuilder.standard().build();
        }
    }
}