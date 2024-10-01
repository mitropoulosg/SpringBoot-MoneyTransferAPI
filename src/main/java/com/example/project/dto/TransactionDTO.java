package com.example.project.dto;

import com.example.project.enums.Currency;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;


public record TransactionDTO(
        @NotNull UUID sourceAccountId,
        @NotNull UUID targetAccountId,
        @NotNull BigDecimal amount,
        Currency currency,
        int version
){}