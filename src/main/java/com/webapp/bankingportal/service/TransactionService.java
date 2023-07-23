package com.webapp.bankingportal.service;

import java.util.List;

import com.webapp.bankingportal.dto.TransactionDTO;

public interface TransactionService {

	List<TransactionDTO> getAllTransactionsByAccountNumber(String accountNumber);

}
