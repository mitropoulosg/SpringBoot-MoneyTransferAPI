package com.example.project.controller;

import com.example.project.dto.AccountDTO;
import com.example.project.dto.TransactionDTO;
import com.example.project.dto.TransferResponse;
import com.example.project.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing accounts and transferring money.
 * Provides endpoints for creating, retrieving, updating, deleting accounts, and transferring funds between accounts.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Account Management", description = "Operations related to account management")
public class AccountController {

    private final AccountService accountService;

    /**
     * Constructs a new AccountController with the specified AccountService.
     *
     * @param accountService the account service to be used by this controller
     */
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Creates a new account with the provided details.
     *
     * @param accountDTO the details of the account to be created
     * @return a ResponseEntity containing the created account details
     */
    @PostMapping("/accounts")
    @Operation(summary = "Create a new account", description = "Creates a new account with the provided details.")
    @ApiResponse(responseCode = "201", description = "Account created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<AccountDTO> createAccount(
            @Valid @RequestBody @Parameter(description = "Account details to create") AccountDTO accountDTO) {
        AccountDTO createdAccount = accountService.createAccount(accountDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }

    /**
     * Retrieves a list of all accounts.
     *
     * @return a ResponseEntity containing the list of all accounts
     */
    @GetMapping("/accounts")
    @Operation(summary = "Retrieve all accounts", description = "Fetches a list of all accounts.")
    @ApiResponse(responseCode = "200", description = "Accounts retrieved successfully")
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        List<AccountDTO> accountDTOs = accountService.getAllAccounts();
        return ResponseEntity.ok(accountDTOs);
    }

    /**
     * Retrieves the details of an account by its ID.
     *
     * @param id the ID of the account to retrieve
     * @return a ResponseEntity containing the account details or a 404 status if not found
     */
    @GetMapping("/accounts/{id}")
    @Operation(summary = "Retrieve an account by ID", description = "Fetches details of an account by its ID.")
    @ApiResponse(responseCode = "200", description = "Account retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Account not found")
    public ResponseEntity<AccountDTO> getAccount(
            @PathVariable @Parameter(description = "ID of the account to retrieve") UUID id) {
        AccountDTO accountDTO = accountService.getAccount(id);
        return ResponseEntity.ok(accountDTO);
    }

    /**
     * Updates an existing account with new details.
     *
     * @param id         the ID of the account to update
     * @param accountDTO the updated account details
     * @return a ResponseEntity with a success message or an error message if the update fails
     */
    @PutMapping("/accounts/{id}")
    @Operation(summary = "Update an account", description = "Updates an existing account with new details.")
    @ApiResponse(responseCode = "200", description = "Account updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input or account not found")
    public ResponseEntity<String> updateAccount(
            @PathVariable @Parameter(description = "ID of the account to update") UUID id,
            @Valid @RequestBody @Parameter(description = "Updated account details") AccountDTO accountDTO) {
        accountService.updateAccount(id, accountDTO);
        return ResponseEntity.ok("Account updated");

    }

    /**
     * Deletes an account by its ID.
     *
     * @param id the ID of the account to delete
     * @return a ResponseEntity with a success message or an error message if the deletion fails
     */
    @DeleteMapping("/accounts/{id}")
    @Operation(summary = "Delete an account", description = "Deletes an account by its ID.")
    @ApiResponse(responseCode = "200", description = "Account deleted successfully")
    @ApiResponse(responseCode = "400", description = "Invalid ID or account not found")
    public ResponseEntity<String> deleteAccount(
            @PathVariable @Parameter(description = "ID of the account to delete") UUID id) {
        accountService.deleteAccount(id);
        return ResponseEntity.ok("Account deleted");
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transfer money between accounts", description = "Transfers money from one account to another.")
    @ApiResponse(responseCode = "200", description = "Transfer successful")
    @ApiResponse(responseCode = "400", description = "Invalid request details")
    @ApiResponse(responseCode = "404", description = "Account not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<TransferResponse> transferMoney(
            @Valid @RequestBody @Parameter(description = "Details of the transfer request") TransactionDTO request) {
        TransferResponse response = accountService.transferMoney(request);
        return ResponseEntity.ok(response);
    }
}
