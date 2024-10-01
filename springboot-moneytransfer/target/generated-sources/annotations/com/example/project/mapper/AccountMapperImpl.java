package com.example.project.mapper;

import com.example.project.dto.AccountDTO;
import com.example.project.entity.Account;
import java.math.BigDecimal;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-09-19T03:12:19+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.11 (Eclipse Adoptium)"
)
@Component
public class AccountMapperImpl implements AccountMapper {

    @Override
    public AccountDTO toDto(Account accountEntity) {
        if ( accountEntity == null ) {
            return null;
        }

        UUID id = null;
        BigDecimal balance = null;
        int version = 0;

        id = accountEntity.getId();
        balance = accountEntity.getBalance();
        version = accountEntity.getVersion();

        AccountDTO accountDTO = new AccountDTO( id, balance, version );

        return accountDTO;
    }

    @Override
    public Account toEntity(AccountDTO accountDTO) {
        if ( accountDTO == null ) {
            return null;
        }

        Account account = new Account();

        account.setId( accountDTO.id() );
        account.setBalance( accountDTO.balance() );
        account.setVersion( accountDTO.version() );

        return account;
    }
}
