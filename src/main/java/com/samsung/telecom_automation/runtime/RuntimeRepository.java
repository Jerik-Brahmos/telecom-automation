package com.samsung.telecom_automation.runtime;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Repository
public class RuntimeRepository {
    private final String repoUrl;
    private final String username;
    private final String token;

    public RuntimeRepository(
            @Value("${runtime.repo.url}") String repoUrl,
            @Value("${runtime.repo.username}") String username,
            @Value("${runtime.repo.token}") String token) {
        this.repoUrl = repoUrl;
        this.username = username;
        this.token = token;
    }

    public void saveDesiredState(String fileName, String yamlContent) throws Exception {
        File localRepoDir = new File(System.getProperty("java.io.tmpdir"), "runtime-repo-" + UUID.randomUUID().toString());
        try {
            synchronized (this) {
                Files.createDirectories(localRepoDir.toPath());
                try {
                    Git.cloneRepository()
                            .setURI(repoUrl)
                            .setDirectory(localRepoDir)
                            .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, token))
                            .call();
                } catch (GitAPIException e) {
                    throw new Exception("Failed to clone repository: " + repoUrl + ". Check URL, credentials, or repository access.", e);
                }

                Git git = Git.open(localRepoDir);
                Path filePath = Paths.get(localRepoDir.getPath(), fileName);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, yamlContent.getBytes(StandardCharsets.UTF_8));

                git.add().addFilepattern(fileName).call();
                git.commit().setMessage("Update desired state: " + fileName).call();
                git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, token)).call();
            }
        } catch (GitAPIException e) {
            throw new Exception("Git operation failed: " + e.getMessage(), e);
        } finally {
            if (localRepoDir.exists()) {
                deleteDirectory(localRepoDir);
            }
        }
    }

    public String getDesiredState(String fileName) throws Exception {
        File localRepoDir = new File(System.getProperty("java.io.tmpdir"), "runtime-repo-" + UUID.randomUUID().toString());
        try {
            synchronized (this) {
                Files.createDirectories(localRepoDir.toPath());
                try {
                    Git.cloneRepository()
                            .setURI(repoUrl)
                            .setDirectory(localRepoDir)
                            .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, token))
                            .call();
                } catch (GitAPIException e) {
                    throw new Exception("Failed to clone repository: " + repoUrl + ". Check URL, credentials, or repository access.", e);
                }

                Path filePath = Paths.get(localRepoDir.getPath(), fileName);
                if (!Files.exists(filePath)) {
                    return null;
                }
                return Files.readString(filePath, StandardCharsets.UTF_8);
            }
        } catch (GitAPIException e) {
            throw new Exception("Git operation failed: " + e.getMessage(), e);
        } finally {
            if (localRepoDir.exists()) {
                deleteDirectory(localRepoDir);
            }
        }
    }

    private void deleteDirectory(File directory) {
        try {
            if (directory.isDirectory()) {
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        deleteDirectory(file);
                    }
                }
            }
            Files.deleteIfExists(directory.toPath());
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }
}