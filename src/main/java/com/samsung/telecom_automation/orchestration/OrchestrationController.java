package com.samsung.telecom_automation.orchestration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orchestration")
public class OrchestrationController {
    private final OrchestrationService orchestrationService;

    @Autowired
    public OrchestrationController(OrchestrationService orchestrationService) {
        this.orchestrationService = orchestrationService;
    }

    @PostMapping("/desired-state")
    public ResponseEntity<String> updateDesiredState(@RequestBody String serviceIntent) {
        try {
            orchestrationService.updateDesiredState(serviceIntent);
            return ResponseEntity.ok("Desired state updated");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating desired state: " + e.getMessage());
        }
    }
}
