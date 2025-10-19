package com.webapp.bankingportal.workflow.nodes;

import com.webapp.bankingportal.workflow.state.ReconState;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.bsc.langgraph4j.action.NodeAction;

import java.util.*;

// --- Node 2: Report Generation with LLM ---
public class ReportGenerationNode implements NodeAction<ReconState> {
    private final OpenAiChatModel model;

    public ReportGenerationNode(OpenAiChatModel model) {
        this.model = model;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> apply(ReconState state) {
        // Transactions are stored as List<String>
        List<String> txns = (List<String>) state.value(ReconState.TXNS_KEY)
                .orElse(List.of());
        String account = state.accountNumber().orElse("UNKNOWN");

        // Join the stringified transactions into one block
        String txnSummary = String.join("\n", txns);

        String prompt = """
You are a financial reconciliation assistant. 
Generate a clear, easy-to-read reconciliation report for account %s based on the following transactions:

%s

Rules for formatting and tone:
1. Do NOT use markdown symbols like **, ##, or bullet markers (*, -). 
   Use plain text headings such as "Balance Check", "Narrative", "Alerts", "Trends", "Summary".
2. Do NOT include technical details like transaction IDs, codes, or field names. 
   Just describe deposits and withdrawals in plain, everyday language.
3. Balance Check:
   - Show opening balance (if available), total deposits, total withdrawals, and closing balance.
   - If the math doesn’t add up, clearly flag it.
4. Narrative:
   - Tell the story of the day in natural language, like a diary. 
   Example: "You added ₹10,000 in the morning, withdrew ₹2,000 at lunch, and sent ₹5,000 in the evening. That left you with ₹53,700."
5. Alerts:
   - If any withdrawal is larger than ₹10,000, add: "Heads up, you withdrew a large amount today."
   - If balance drops below ₹1,000, add: "Low balance warning."
6. Trends:
   - Provide weekly or monthly summaries in plain language. 
   Example: "This week you deposited ₹25,000 and withdrew ₹18,000. Net savings: ₹7,000."
7. Overall Summary:
   - End with a simple, encouraging statement about the account’s health and spending habits.
8. Make the tone friendly, professional, and easy for a non-technical person to understand.
""".formatted(account, txnSummary);



        // Call LLM
        String report = model.generate(prompt);

        System.out.println("Generated report:\n" + report);

        return Map.of(ReconState.REPORT_KEY, report);
    }
}
