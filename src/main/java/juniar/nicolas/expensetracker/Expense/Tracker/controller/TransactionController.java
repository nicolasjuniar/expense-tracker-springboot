package juniar.nicolas.expensetracker.Expense.Tracker.controller;

import juniar.nicolas.expensetracker.Expense.Tracker.entity.Transaction;
import juniar.nicolas.expensetracker.Expense.Tracker.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction, Authentication auth) {
        Transaction created = transactionService.createTransaction(auth.getName(), transaction);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions(Authentication auth) {
        return ResponseEntity.ok(transactionService.getAllTransactions(auth.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTransaction(@PathVariable Long id, Authentication auth) {
        transactionService.deleteTransaction(id, auth.getName());
        return ResponseEntity.ok("Transaction deleted");
    }
}
