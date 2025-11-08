package com.example.user.service;

import com.example.user.dto.LoginUserDTO;
import com.example.user.dto.LoginUserResponseDTO;
import com.example.user.dto.SignupUserDTO;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;        // 자동 주입(AutoWired)
    private final PasswordEncoder passwordEncoder;      // 자동 주입

    @Transactional
    public User signup(SignupUserDTO signupDTO) {
        // 이메일 중복 체크
        if(userRepository.existsByEmail(signupDTO.getEmail())){
            throw new IllegalArgumentException(signupDTO.getEmail() + "는 이미 존재하는 이메일입니다");
        }
        // SignupUserDTO instance를 이용하여 User instance를 생성한다.
        User user = User.fromDTO(signupDTO);
        // 플레인 패스워드를 암호화된 패스워드로 변환
        String encPassword = passwordEncoder.encode(signupDTO.getPassword());
        user.setPassword( encPassword );    // user instance에 암호화된 비밀번호로 다시 설정

        /*
        * insert into `users` (`email`,`password`,`nick_name`) values ('test01@gmail.com', '$2a$10$rnHWO9HsaFlHFfYPBKyLPeiZuaGE14rzx/9mJwqqke1CaZouAAWjy','아이유');
        * */
        return userRepository.save( user ); // JPA에 의해서 자동으로 테이블에 저장딘다. (insert into 쿼리가 자동으로 만들어져서 전달됨)
    }

    public LoginUserResponseDTO login(LoginUserDTO loginUserDTO) {
        User user = userRepository.findByEmail(loginUserDTO.getEmail())
                .orElseThrow(() -> new IllegalArgumentException(loginUserDTO.getEmail() + " 사용자가 존재 하지 않습니다."));
        // 패스워드 인증
        boolean isEqual = passwordEncoder.matches(loginUserDTO.getPassword(), user.getPassword());
        if(!isEqual){
           throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        // User 인스턴스를 LoginUserResponseDTO 인스턴스로 변환하여 반환
        return User.toLoginUserResponse(user);
    }
}
