package com.example.project.controller

import com.example.project.dto.AccountDTO
import com.example.project.service.AccountService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spock.lang.Specification

import java.math.BigDecimal
import java.util.UUID

import static org.mockito.Mockito.when

@WebMvcTest(AccountController)
class AccountControllerIntegrationSpec extends Specification {

    @Autowired
    MockMvc mockMvc

    @MockBean
    AccountService accountService

    @Autowired
    ObjectMapper objectMapper

    def setup() {
        // Ensures objectMapper is properly initialized
        assert objectMapper != null
    }

    def "should create an account"() {
        given:
        UUID accountId = UUID.randomUUID()
        AccountDTO accountDTO = new AccountDTO(
                accountId,
                BigDecimal.valueOf(1000),
                1
        )
        when(accountService.createAccount(accountDTO)).thenReturn(accountDTO)

        when:
        def response = mockMvc.perform(MockMvcRequestBuilders.post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath('$.id').value(accountDTO.id.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath('$.balance').value(accountDTO.balance.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath('$.version').value(accountDTO.version))
                .andReturn()

        then:
        response != null
    }
}