package com.projectn.projectn.service;

import com.projectn.projectn.common.Constant;
import com.projectn.projectn.common.enums.RoleEnum;
import com.projectn.projectn.common.enums.Status;
import com.projectn.projectn.config.MessageBuilder;
import com.projectn.projectn.exception.AppException;
import com.projectn.projectn.model.Role;
import com.projectn.projectn.model.User;
import com.projectn.projectn.payload.request.LoginRequest;
import com.projectn.projectn.payload.request.RegisterRequest;
import com.projectn.projectn.payload.response.LoginResponse;
import com.projectn.projectn.payload.response.RespMessage;
import com.projectn.projectn.payload.response.UserDTO;
import com.projectn.projectn.repository.RoleRepository;
import com.projectn.projectn.repository.UserRepository;
import com.projectn.projectn.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MessageBuilder messageBuilder;

    // Login method
    public RespMessage login(LoginRequest loginRequest) {
        checkLoginRequest(loginRequest);
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        LoginResponse response = jwtTokenProvider.generateToken(authentication);
        return messageBuilder.buildSuccessMessage(response);
    }

    private void checkLoginRequest(LoginRequest loginRequest) {
        if (loginRequest.getEmail() == null || loginRequest.getEmail().isEmpty()) {
            throw new AppException(Constant.FIELD_NOT_NULL, new Object[]{"LoginRequest.Email"}, "Email must be not null");
        }
        if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            throw new AppException(Constant.FIELD_NOT_NULL, new Object[]{"LoginRequest.Password"}, "Password must be not null");
        }
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
        if (userOptional.isEmpty()) {
            throw new AppException(Constant.FIELD_NOT_VALID, new Object[]{"LoginRequest.Email"}, "Email not found");
        }
        if (!passwordEncoder.matches(loginRequest.getPassword(), userOptional.get().getPassword())) {
            throw new AppException(Constant.FIELD_NOT_VALID, new Object[]{"LoginRequest.Password"}, "Password not correct");
        }
        if (!userOptional.get().isEnabled()) {
            throw new AppException(Constant.FIELD_NOT_VALID, new Object[]{"LoginRequest.Email"}, "User is disabled");
        }
    }

    // Register method
    @Transactional
    public RespMessage register(RegisterRequest registerRequest) {
        checkRegisterRequest(registerRequest);
        String emailDto = registerRequest.getEmail();
        String nameDto = registerRequest.getFullName();
        String passwordDto = registerRequest.getPassword();

        Role role = roleRepository.getRoleByName(RoleEnum.ROLE_USER)
                .orElseThrow(() -> new AppException(Constant.FIELD_NOT_FOUND, new Object[]{"Role"}, "Role not found"));
        User user = User.builder()
                .email(emailDto)
                .name(nameDto)
                .password(passwordEncoder.encode(passwordDto))
                .role(role)
                .status(Status.ACTIVE)
                .build();
        userRepository.save(user);
        return messageBuilder.buildSuccessMessage("Register successfully");
    }

    public void checkRegisterRequest(RegisterRequest registerRequest) {
        if (registerRequest.getEmail() == null || registerRequest.getEmail().isEmpty()) {
            throw new AppException(Constant.FIELD_NOT_NULL, new Object[]{"RegisterRequest.Email"}, "Email must be not null");
        }
        if (registerRequest.getPassword() == null || registerRequest.getPassword().isEmpty()) {
            throw new AppException(Constant.FIELD_NOT_NULL, new Object[]{"RegisterRequest.Password"}, "Password must be not null");
        }
        if (registerRequest.getConfirmPassword() == null || registerRequest.getConfirmPassword().isEmpty()) {
            throw new AppException(Constant.FIELD_NOT_NULL, new Object[]{"RegisterRequest.ConfirmPassword"}, "ConfirmPassword must be not null");
        }

        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new AppException(Constant.FIELD_NOT_VALID, new Object[]{"RegisterRequest.Password"}, "Password and RegisterRequest.ConfirmPassword must be the same");
        }

        if (userRepository.existsUserByEmail(registerRequest.getEmail())) {
            throw new AppException(Constant.FIELD_EXISTED, new Object[]{"RegisterRequest.Email"}, "Email is existed");
        }
    }

    public RespMessage getProfileByToken() {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String email = userDetails.getUsername();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(Constant.FIELD_NOT_FOUND, new Object[]{"User"}, "User not found when get profile by token"));

            UserDTO userDTO = UserDTO.builder()
                    .name(user.getName())
                    .email(user.getEmail())
                    .roleName(user.getRole().getName().name())
                    .profile_img(user.getAvatar())
                    .status(user.getStatus().name())
                    .id(user.getId())
                    .wallet(user.getWallet())
                    .build();
            return messageBuilder.buildSuccessMessage(userDTO);

        }
        catch(Exception e) {
            throw new AppException(Constant.FIELD_NOT_FOUND, new Object[]{"Token"}, "Token is null or not valid");
        }
    }

    public RespMessage refreshAccessToken(String refreshToken) {

        if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {
            throw new AppException(Constant.FIELD_NOT_VALID, new Object[]{"RefreshToken"}, "Refresh token is missing or invalid");
        }

        refreshToken = refreshToken.substring(7);

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new AppException(Constant.FIELD_NOT_VALID, new Object[]{"RefreshToken"}, "Invalid refresh token");
        }

        String username = jwtTokenProvider.getUsername(refreshToken);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new AppException(Constant.FIELD_NOT_FOUND, new Object[]{"User"}, "User not found for provided refresh token"));

        String newAccessToken = jwtTokenProvider.generateAccessToken(username);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);

        return RespMessage.builder()
                .respCode("000")
                .respDesc("Access token refreshed successfully")
                .data(Map.of(
                        "accessToken", newAccessToken,
                        "refreshToken", newRefreshToken
                ))
                .build();
    }

}
