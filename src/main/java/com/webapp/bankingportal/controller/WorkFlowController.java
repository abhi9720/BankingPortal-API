package com.webapp.bankingportal.controller;

import com.webapp.bankingportal.workflow.graph.GraphBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workflow")
public class WorkFlowController {

    private final GraphBuilder graphBuilder;

    public WorkFlowController(GraphBuilder graphBuilder) {
        this.graphBuilder = graphBuilder;
    }

    /**
     * Run the reconciliation workflow end-to-end.
     * Example: GET /api/workflow/run
     */
    @GetMapping("/run")
    public ResponseEntity<String> runWorkflow() {
        try {
            graphBuilder.runGraph();
            return ResponseEntity.ok("Workflow executed successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Workflow execution failed: " + e.getMessage());
        }
    }
}
