package com.expense.service.service;

import com.expense.service.dto.ExpenseDto;
import com.expense.service.entities.Expense;
import com.expense.service.repository.ExpenseRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public boolean createExpense(ExpenseDto expenseDto) {
        setCurrency(expenseDto);
        try {
            log.info("Saving new expense for user_id={}, external_id={}", expenseDto.getUserId(), expenseDto.getExternalId());
            expenseRepository.save(objectMapper.convertValue(expenseDto, Expense.class));
            return true;
        } catch (Exception ex) {
            log.error("Failed to save expense: {}", expenseDto, ex);
            return false;
        }
    }

    public boolean updateExpense(ExpenseDto expenseDto) {
        setCurrency(expenseDto);
        try {
            Optional<Expense> expenseFoundOpt = expenseRepository.findByUserIdAndExternalId(expenseDto.getUserId(), expenseDto.getExternalId());
            if (expenseFoundOpt.isEmpty()) {
                log.warn("Expense not found for update: user_id={}, external_id={}", expenseDto.getUserId(), expenseDto.getExternalId());
                return false;
            }

            Expense expense = expenseFoundOpt.get();
            expense.setAmount(expenseDto.getAmount());
            expense.setMerchant(Strings.isNotBlank(expenseDto.getMerchant()) ? expenseDto.getMerchant() : expense.getMerchant());
            expense.setCurrency(Strings.isNotBlank(expenseDto.getCurrency()) ? expenseDto.getCurrency() : expense.getCurrency());

            expenseRepository.save(expense);
            log.info("Expense updated: user_id={}, external_id={}", expenseDto.getUserId(), expenseDto.getExternalId());
            return true;
        } catch (Exception ex) {
            log.error("Failed to update expense: {}", expenseDto, ex);
            return false;
        }
    }

    public List<ExpenseDto> getExpenses(String userId) {
        try {
            List<Expense> expenses = expenseRepository.findByUserId(userId);
            log.info("Fetched {} expenses for user_id={}", expenses.size(), userId);
            return objectMapper.convertValue(expenses, new TypeReference<List<ExpenseDto>>() {});
        } catch (Exception ex) {
            log.error("Failed to fetch expenses for user_id={}", userId, ex);
            throw ex;
        }
    }

    private void setCurrency(ExpenseDto expenseDto) {
        if (Objects.isNull(expenseDto.getCurrency())) {
            expenseDto.setCurrency("INR");
        }
    }
}