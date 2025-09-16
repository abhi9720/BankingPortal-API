package com.webapp.bankingportal.service;

import java.util.concurrent.ExecutionException;

public interface IRequestService {

    String getRequestAttr() throws ExecutionException, InterruptedException;
}
