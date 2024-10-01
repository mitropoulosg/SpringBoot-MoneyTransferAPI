package com.example.project.mapper;

import com.example.project.dto.TransactionDTO;
import com.example.project.entity.Transaction;
import com.example.project.enums.Currency;
import java.math.BigDecimal;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-09-19T03:12:20+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.11 (Eclipse Adoptium)"
)
@Component
public class TransactionMapperImpl implements TransactionMapper {

    @Override
    public TransactionDTO toDto(Transaction transactionEntity) {
        if ( transactionEntity == null ) {
            return null;
        }

        UUID sourceAccountId = null;
        UUID targetAccountId = null;
        BigDecimal amount = null;
        Currency currency = null;
        int version = 0;

        sourceAccountId = transactionEntity.getSourceAccountId();
        targetAccountId = transactionEntity.getTargetAccountId();
        amount = transactionEntity.getAmount();
        currency = transactionEntity.getCurrency();
        version = transactionEntity.getVersion();

        TransactionDTO transactionDTO = new TransactionDTO( sourceAccountId, targetAccountId, amount, currency, version );

        return transactionDTO;
    }

    @Override
    public Transaction toEntity(TransactionDTO transactionDTO) {
        if ( transactionDTO == null ) {
            return null;
        }

        Transaction transaction = new Transaction();

        transaction.setSourceAccountId( transactionDTO.sourceAccountId() );
        transaction.setTargetAccountId( transactionDTO.targetAccountId() );
        transaction.setAmount( transactionDTO.amount() );
        transaction.setVersion( transactionDTO.version() );

        return transaction;
    }
}
