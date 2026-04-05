package com.webapp.bankingportal.workflow.nodes;

import com.webapp.bankingportal.service.EmailService;
import com.webapp.bankingportal.workflow.state.ReconState;
import org.bsc.langgraph4j.action.NodeAction;

import java.util.*;

// --- Node 3: Mail Report ---
public class MailReportNode implements NodeAction<ReconState> {

    private final EmailService emailService;

    // Constructor injection (instead of @Autowired for clarity in workflow wiring)
    public MailReportNode(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> apply(ReconState state) {
        String report = state.report().orElse("No report generated");
        String account = state.accountNumber().orElse("UNKNOWN");

        // Because USER_KEY uses an appender channel, unwrap the list and take the latest map
        List<Map<String, String>> userList = (List<Map<String, String>>) state.value(ReconState.USER_KEY)
                .orElseThrow(() -> new IllegalStateException("User info not found in state"));

        Map<String, String> userMap = userList.get(userList.size() - 1);

        String email = userMap.getOrDefault("email", "unknown@example.com");
        String name  = userMap.getOrDefault("name", "Customer");

        // Build email body using template
        String emailBody = emailService.getReconciliationReportTemplate(
                name,
                "Here is your latest reconciliation report for account " + account + ".",
                report
        );

        // Send email
        emailService.sendEmail(email, "Your Reconciliation Report - OneStopBank", emailBody);

        System.out.println("Mailing report for account " + account + " to " + email);

        return Map.of();
    }
}
