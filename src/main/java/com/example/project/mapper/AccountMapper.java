package com.example.project.mapper;


import com.example.project.dto.AccountDTO;
import com.example.project.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountDTO toDto(Account accountEntity);
    Account toEntity(AccountDTO accountDTO);
}