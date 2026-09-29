package vn.iotstar.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.User;
import vn.iotstar.model.LoginUserModel;
import vn.iotstar.model.RegisterUserModel;
import vn.iotstar.repository.UserRepository;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public User signup(RegisterUserModel input) {

        if (userRepository.existsByEmail(input.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        User user = new User();

        user.setEmail(input.getEmail());
        user.setFullName(input.getFullName());

        user.setPassword(
            passwordEncoder.encode(input.getPassword())
        );

        return userRepository.save(user);
    }

    public User authenticate(LoginUserModel input) {

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                input.getEmail(),
                input.getPassword()
            )
        );

        return userRepository
            .findByEmail(input.getEmail())
            .orElseThrow(
                () -> new IllegalArgumentException(
                    "Không tìm thấy người dùng"
                )
            );
    }
}