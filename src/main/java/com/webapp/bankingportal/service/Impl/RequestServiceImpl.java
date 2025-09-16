package com.webapp.bankingportal.service.Impl;

import com.webapp.bankingportal.service.IRequestService;
import com.webapp.bankingportal.util.CustomRequestContextHolder;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

@Service
public class RequestServiceImpl implements IRequestService {

    @Resource
    private Executor taskTaskExecutor;
    @Override
    public String getRequestAttr() throws ExecutionException, InterruptedException {
        //return requestAttr();

        CompletableFuture<String> future = CompletableFuture.supplyAsync(this::requestAttr, taskTaskExecutor);
        return future.get();
    }

    private String requestAttr() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) CustomRequestContextHolder.getRequestAttributes();
        if(Objects.isNull(attributes)) {
            return "null";
        }
        HttpServletRequest request = attributes.getRequest();
        return request.getHeader("username");
    }
}
