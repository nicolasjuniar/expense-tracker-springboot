package juniar.nicolas.expensetracker.Expense.Tracker.repository;

import juniar.nicolas.expensetracker.Expense.Tracker.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserId(Long userId);
}