package com.expense.service.controller;

import com.expense.service.dto.ExpenseDto;
import com.expense.service.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/expense/v1")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping("/expenses")
    public ResponseEntity<List<ExpenseDto>> getExpenses(@RequestParam("user_id") @NonNull String userId) {
        try {
            log.info("Fetching expenses for user_id={}", userId);
            List<ExpenseDto> expenseDtoList = expenseService.getExpenses(userId);
            return ResponseEntity.ok(expenseDtoList);
        } catch (Exception ex) {
            log.error("Failed to fetch expenses for user_id={}", userId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/expenses")
    public ResponseEntity<Boolean> addExpense(
            @RequestHeader("X-User-Id") @NonNull String userId,
            @RequestBody ExpenseDto expenseDto) {
        try {
            expenseDto.setUserId(userId);
            log.info("Creating expense for user_id={}, data={}", userId, expenseDto);
            boolean result = expenseService.createExpense(expenseDto);
            return ResponseEntity.ok(result);
        } catch (Exception ex) {
            log.error("Failed to create expense for user_id={}, data={}", userId, expenseDto, ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }
}