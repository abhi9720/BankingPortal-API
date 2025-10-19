package com.webapp.bankingportal.workflow.graph;

import com.webapp.bankingportal.workflow.nodes.MailReportNode;
import com.webapp.bankingportal.workflow.nodes.ReportGenerationNode;
import com.webapp.bankingportal.workflow.nodes.TransactionFetchNode;
import com.webapp.bankingportal.workflow.state.ReconState;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.CompiledGraph;

import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

import java.util.Map;

import com.webapp.bankingportal.service.DashboardService;
import com.webapp.bankingportal.service.EmailService;
import com.webapp.bankingportal.service.TransactionService;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.stereotype.Component;

@Component
public class GraphBuilder {

    private final TransactionService transactionService;
    private final DashboardService dashboardService;
    private final EmailService emailService;
    private final OpenAiChatModel chatModel;

    public GraphBuilder(TransactionService transactionService,
                        DashboardService dashboardService,
                        EmailService emailService,
                        OpenAiChatModel chatModel) {
        this.transactionService = transactionService;
        this.dashboardService = dashboardService;
        this.emailService = emailService;
        this.chatModel = chatModel;
    }

    /** Build and return the compiled graph */
    public CompiledGraph<ReconState> buildGraph() throws GraphStateException {
        var fetchNode = new TransactionFetchNode(transactionService, dashboardService);
        var reportNode = new ReportGenerationNode(chatModel);
        var mailNode = new MailReportNode(emailService);

        var stateGraph = new StateGraph<>(
                ReconState.SCHEMA,
                ReconState::new
        )
                .addNode("fetchTransactions", node_async(fetchNode))
                .addNode("generateReport", node_async(reportNode))
                .addNode("mailReport", node_async(mailNode))
                .addEdge(START, "fetchTransactions")
                .addEdge("fetchTransactions", "generateReport")
                .addEdge("generateReport", "mailReport")
                .addEdge("mailReport", END);

        return stateGraph.compile();
    }

    /** Run the compiled graph */
    public void runGraph() throws GraphStateException {
        var compiled = buildGraph();
        for (var item : compiled.stream(Map.of())) {
            System.out.println("State after step: " + item);
        }
    }
}
