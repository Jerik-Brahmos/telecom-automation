package com.samsung.telecom_automation.artifact;

import org.apache.commons.io.IOUtils;
import org.jfrog.artifactory.client.Artifactory;
import org.jfrog.artifactory.client.ArtifactoryClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Service
public class ArtifactService {
    private final Artifactory artifactory;
    private final String repository;

    public ArtifactService(
            @Value("${artifactory.url}") String artifactoryUrl,
            @Value("${artifactory.username}") String username,
            @Value("${artifactory.apiKey}") String apiKey,
            @Value("${artifactory.repository}") String repository) {
        this.artifactory = ArtifactoryClientBuilder.create()
                .setUrl(artifactoryUrl)
                .setUsername(username)
                .setPassword(apiKey) // Use API key as password
                .build();
        this.repository = repository;
    }

    public void storeArtifact(String name, String content) throws Exception {
        try {
            ByteArrayInputStream contentStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
            artifactory.repository(this.repository)
                    .upload(name, contentStream)
                    .doUpload();
        } catch (Exception e) {
            throw new Exception("Failed to store artifact in Artifactory: " + name, e);
        }
    }

    public String getArtifact(String name) throws Exception {
        try {
            return IOUtils.toString(
                    artifactory.repository(this.repository).download(name).doDownload(),
                    StandardCharsets.UTF_8
            );
        } catch (Exception e) {
            throw new Exception("Failed to retrieve artifact from Artifactory: " + name, e);
        }
    }
}