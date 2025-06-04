package com.samsung.telecom_automation.event;

import org.springframework.context.ApplicationEvent;

public class ArtifactUpdateEvent extends ApplicationEvent {
    private final String artifactName;

    public ArtifactUpdateEvent(Object source, String artifactName) {
        super(source);
        this.artifactName = artifactName;
    }

    public String getArtifactName() {
        return artifactName;
    }
}
