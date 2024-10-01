package com.example.project.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferResponse(
        String status,
        BigDecimal amount
) {}