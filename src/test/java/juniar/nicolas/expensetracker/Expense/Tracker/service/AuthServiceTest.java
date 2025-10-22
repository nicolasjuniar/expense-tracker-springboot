package juniar.nicolas.expensetracker.Expense.Tracker.service;

import juniar.nicolas.expensetracker.Expense.Tracker.dto.AuthRequest;
import juniar.nicolas.expensetracker.Expense.Tracker.dto.AuthResponse;
import juniar.nicolas.expensetracker.Expense.Tracker.entity.User;
import juniar.nicolas.expensetracker.Expense.Tracker.repository.UserRepository;
import juniar.nicolas.expensetracker.Expense.Tracker.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private AuthRequest request;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // AuthRequest: gunakan no-arg constructor + setters (sesuai class yang kamu pakai)
        request = new AuthRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        user = User.builder()
                .id(1L)
                .username("testuser")
                .password("encodedpassword")
                .build();
    }

    @Test
    void testRegister_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedpassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        String response = authService.register(request);

        assertEquals("User registered successfully", response);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegister_Fail_UsernameExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(request));
        assertEquals("Username already exists", ex.getMessage());
    }

    @Test
    void testLogin_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedpassword")).thenReturn(true);
        when(jwtUtil.generateToken("testuser")).thenReturn("fake-jwt-token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.getToken());
        verify(jwtUtil, times(1)).generateToken("testuser");
    }

    @Test
    void testLogin_Fail_InvalidPassword() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedpassword")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertEquals("Invalid credentials", ex.getMessage());
    }

    @Test
    void testLogin_Fail_UserNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertEquals("User not found", ex.getMessage());
    }
}