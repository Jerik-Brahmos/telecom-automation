package com.samsung.telecom_automation.artifact;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.samsung.telecom_automation.event.ArtifactUpdateEvent;

@RestController
@RequestMapping("/api/artifacts")
public class ArtifactController {
    private final ArtifactService artifactService;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public ArtifactController(ArtifactService artifactService, ApplicationEventPublisher eventPublisher) {
        this.artifactService = artifactService;
        this.eventPublisher = eventPublisher;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadArtifact(@RequestParam String artifactName, @RequestBody String content) {
        try {
            artifactService.storeArtifact(artifactName, content);
            eventPublisher.publishEvent(new ArtifactUpdateEvent(this, artifactName));
            return ResponseEntity.ok("Artifact uploaded: " + artifactName);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error uploading artifact: " + e.getMessage());
        }
    }

    @GetMapping("/{name}")
    public ResponseEntity<String> getArtifact(@PathVariable String name) {
        try {
            String content = artifactService.getArtifact(name);
            return ResponseEntity.ok(content);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error retrieving artifact: " + e.getMessage());
        }
    }
}