package com.example.project.mapper;

import com.example.project.dto.AccountDTO;
import com.example.project.dto.TransactionDTO;
import com.example.project.entity.Account;
import com.example.project.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    TransactionDTO toDto(Transaction transactionEntity);
    Transaction toEntity(TransactionDTO transactionDTO);
}