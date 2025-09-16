package com.webapp.bankingportal.config;

import com.alibaba.ttl.threadpool.TtlExecutors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class TaskExecutorConfig {

    @Bean
    public Executor taskTaskExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                5,5,60, TimeUnit.SECONDS,new ArrayBlockingQueue<>(100)
        );
        //pre-init
        executor.prestartAllCoreThreads();
        TtlExecutors TtlExecutors = null;
        return TtlExecutors.getTtlExecutor(executor);
        //return executor;
    }

}
