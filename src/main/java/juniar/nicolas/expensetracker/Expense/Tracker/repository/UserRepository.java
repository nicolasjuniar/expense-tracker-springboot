package juniar.nicolas.expensetracker.Expense.Tracker.repository;

import juniar.nicolas.expensetracker.Expense.Tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
