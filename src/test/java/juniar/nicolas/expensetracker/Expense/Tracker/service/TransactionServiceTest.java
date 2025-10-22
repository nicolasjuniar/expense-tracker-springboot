package juniar.nicolas.expensetracker.Expense.Tracker.service;

import juniar.nicolas.expensetracker.Expense.Tracker.entity.Transaction;
import juniar.nicolas.expensetracker.Expense.Tracker.entity.User;
import juniar.nicolas.expensetracker.Expense.Tracker.repository.TransactionRepository;
import juniar.nicolas.expensetracker.Expense.Tracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    private User user;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setTitle("Groceries");
        transaction.setAmount(100.0);
        transaction.setCategory("Food");
        transaction.setUser(user);
        transaction.setDate(LocalDateTime.now());
    }

    @Test
    void testCreateTransaction_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction t = invocation.getArgument(0);
            t.setId(1L);
            return t;
        });

        Transaction input = new Transaction();
        input.setTitle("Groceries");
        input.setAmount(100.0);
        input.setCategory("Food");

        Transaction result = transactionService.createTransaction("testuser", input);

        assertNotNull(result);
        assertEquals("Groceries", result.getTitle());
        assertEquals(100.0, result.getAmount());
        assertEquals("Food", result.getCategory());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testCreateTransaction_UserNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        Transaction input = new Transaction();
        assertThrows(RuntimeException.class, () -> transactionService.createTransaction("testuser", input));
    }

    @Test
    void testGetAllTransactions_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(transactionRepository.findByUser(user)).thenReturn(List.of(transaction));

        List<Transaction> result = transactionService.getAllTransactions("testuser");

        assertEquals(1, result.size());
        assertEquals("Groceries", result.get(0).getTitle());
    }

    @Test
    void testDeleteTransaction_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        transactionService.deleteTransaction(1L, "testuser");

        verify(transactionRepository, times(1)).delete(transaction);
    }

    @Test
    void testDeleteTransaction_Unauthorized() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        transaction.setUser(anotherUser);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> transactionService.deleteTransaction(1L, "testuser"));
        assertEquals("Unauthorized access", ex.getMessage());
    }

    @Test
    void testDeleteTransaction_NotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> transactionService.deleteTransaction(1L, "testuser"));
        assertEquals("Transaction not found", ex.getMessage());
    }
}
