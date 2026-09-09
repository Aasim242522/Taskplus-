package com.taskmonitor.module.auth.service;

import com.taskmonitor.common.exception.BusinessException;
import com.taskmonitor.common.exception.ErrorCode;
import com.taskmonitor.module.auth.dto.AuthResponse;
import com.taskmonitor.module.auth.dto.LoginRequest;
import com.taskmonitor.module.auth.dto.RegisterRequest;
import com.taskmonitor.module.gamification.domain.Streak;
import com.taskmonitor.module.gamification.repository.StreakRepository;
import com.taskmonitor.module.user.domain.Role;
import com.taskmonitor.module.user.domain.User;
import com.taskmonitor.module.user.domain.UserPreference;
import com.taskmonitor.module.user.repository.UserPreferenceRepository;
import com.taskmonitor.module.user.repository.UserRepository;
import com.taskmonitor.security.JwtTokenProvider;
import com.taskmonitor.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final StreakRepository streakRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository,
                       UserPreferenceRepository userPreferenceRepository,
                       StreakRepository streakRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.userPreferenceRepository = userPreferenceRepository;
        this.streakRepository = streakRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }

        User user = User.builder()
                .email(request.getEmail().toLowerCase().trim())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName().trim())
                .timezone(request.getTimezone() != null ? request.getTimezone() : "UTC")
                .role(Role.ROLE_USER)
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);

        // Initialize user preferences
        UserPreference preference = UserPreference.builder()
                .user(savedUser)
                .darkMode(false)
                .emailNotifications(true)
                .pushNotifications(true)
                .dailyGoalTasks(5)
                .weekStartDay("MONDAY")
                .build();
        userPreferenceRepository.save(preference);

        // Initialize streak record
        Streak streak = Streak.builder()
                .user(savedUser)
                .currentStreak(0)
                .longestStreak(0)
                .freezeCredits(1)
                .build();
        streakRepository.save(streak);

        log.info("Registered new user with email: {}", savedUser.getEmail());

        UserPrincipal principal = UserPrincipal.create(savedUser);
        String accessToken = tokenProvider.generateAccessToken(principal);
        String refreshToken = tokenProvider.generateRefreshToken(principal);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(AuthResponse.UserResponse.builder()
                        .id(savedUser.getId())
                        .email(savedUser.getEmail())
                        .fullName(savedUser.getFullName())
                        .timezone(savedUser.getTimezone())
                        .role(savedUser.getRole().name())
                        .build())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().toLowerCase().trim(),
                        request.getPassword()
                )
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String accessToken = tokenProvider.generateAccessToken(principal);
        String refreshToken = tokenProvider.generateRefreshToken(principal);

        log.info("User successfully logged in: {}", principal.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(AuthResponse.UserResponse.builder()
                        .id(principal.getId())
                        .email(principal.getEmail())
                        .fullName(principal.getFullName())
                        .timezone(principal.getTimezone())
                        .role(principal.getAuthorities().iterator().next().getAuthority())
                        .build())
                .build();
    }
}
