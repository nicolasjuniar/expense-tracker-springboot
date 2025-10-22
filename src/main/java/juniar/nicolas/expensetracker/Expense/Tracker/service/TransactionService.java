package juniar.nicolas.expensetracker.Expense.Tracker.service;

import juniar.nicolas.expensetracker.Expense.Tracker.entity.Transaction;
import juniar.nicolas.expensetracker.Expense.Tracker.entity.User;
import juniar.nicolas.expensetracker.Expense.Tracker.repository.TransactionRepository;
import juniar.nicolas.expensetracker.Expense.Tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    public Transaction createTransaction(String username, Transaction transaction) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        transaction.setUser(user);
        transaction.setDate(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return transactionRepository.findByUser(user);
    }

    public void deleteTransaction(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }

        transactionRepository.delete(transaction);
    }
}