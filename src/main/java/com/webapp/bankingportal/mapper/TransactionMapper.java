package com.webapp.bankingportal.mapper;

import org.springframework.stereotype.Component;

import com.webapp.bankingportal.dto.TransactionDTO;
import com.webapp.bankingportal.entity.Transaction;

/**
 * Mapper component that converts {@link Transaction} entities into
 * {@link TransactionDTO} data transfer objects.
 */
@Component
public class TransactionMapper {

    /**
     * Converts a {@link Transaction} entity to a {@link TransactionDTO}.
     *
     * @param transaction the source transaction entity
     * @return a {@link TransactionDTO} populated from the given entity
     */
    public TransactionDTO toDto(Transaction transaction) {
        return new TransactionDTO(transaction);
    }

}
