package com.webapp.bankingportal.workflow.state;

import com.webapp.bankingportal.dto.UserResponse;

import org.bsc.langgraph4j.state.AgentState;
import org.bsc.langgraph4j.state.Channel;
import org.bsc.langgraph4j.state.Channels;

import java.util.*;

// --- State ---
public class ReconState extends AgentState {
    public static final String ACCOUNT_KEY = "accountNumber";
    public static final String TXNS_KEY = "transactions";
    public static final String REPORT_KEY = "report";
    public static final String USER_KEY = "userResponse";

    // Using appender for all keys, then we pick the last value in getters
    public static final Map<String, Channel<?>> SCHEMA = Map.of(
            ACCOUNT_KEY, Channels.appender(ArrayList::new),
            TXNS_KEY, Channels.appender(ArrayList::new),
            REPORT_KEY, Channels.appender(ArrayList::new),
            USER_KEY, Channels.appender(ArrayList::new)
    );

    public ReconState(Map<String, Object> initData) {
        super(initData);
    }

    public Optional<String> accountNumber() {
        return this.<List<String>>value(ACCOUNT_KEY)
                .flatMap(list -> list.isEmpty() ? Optional.empty() : Optional.of(list.get(list.size() - 1)));
    }

    @SuppressWarnings("unchecked")
    public Optional<List<String>> transactions() {
        return this.value(TXNS_KEY);
    }

    public Optional<String> report() {
        return this.<List<String>>value(REPORT_KEY)
                .flatMap(list -> list.isEmpty() ? Optional.empty() : Optional.of(list.get(list.size() - 1)));
    }

    public Optional<UserResponse> userResponse() {
        return this.<List<UserResponse>>value(USER_KEY)
                .flatMap(list -> list.isEmpty() ? Optional.empty() : Optional.of(list.get(list.size() - 1)));
    }
}




