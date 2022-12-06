package com.sparta.springboard.service;

import com.sparta.springboard.dto.LoginRequestDto;
import com.sparta.springboard.dto.SignupRequestDto;
import com.sparta.springboard.entity.User;
import com.sparta.springboard.entity.UserRoleEnum;
import com.sparta.springboard.jwt.JwtUtil;
import com.sparta.springboard.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class UserService {



    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private static final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    @Transactional
    public void signup(@Valid SignupRequestDto signupRequestDto) {
        String username = signupRequestDto.getUsername();
        String password = signupRequestDto.getPassword();


        Optional<User> found = userRepository.findByUsername(username);

        if (found.isPresent()) {

            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }


        UserRoleEnum role = UserRoleEnum.USER;

        if (signupRequestDto.isAdmin()) {

            if (!signupRequestDto.getAdminToken().equals(ADMIN_TOKEN)) {

                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }

            role = UserRoleEnum.ADMIN;
        }


        User user = new User(username, password, role);
        userRepository.save(user);
    }



    @Transactional(readOnly = true)

    public void login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("등록된 사용자가 없습니다.")
        );

        if(!user.getPassword().equals(password)){
            throw  new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(user.getUsername(), user.getRole()));
    }
}