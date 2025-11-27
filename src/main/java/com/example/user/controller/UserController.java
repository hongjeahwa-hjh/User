package com.example.user.controller;

import com.example.user.dto.LoginUserDTO;
import com.example.user.dto.LoginUserResponseDTO;
import com.example.user.dto.SignupUserDTO;
import com.example.user.dto.UserApiResponse;
import com.example.user.entity.User;
import com.example.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// http://localhost:8095/api/user/

// 리퀘스트의 종류 : GET, POST(Form), PUT(입력), DELETE(삭제), UPDATE(수정), ...
// method : GET, POST(Form), PUT(입력), DELETE(삭제), UPDATE(수정), ...

@RestController                     // 이 클래스를 RestController로 만듬 -> Response를 JSON구조로 전달
@RequiredArgsConstructor            // private final로 선언된 멤버 필드를 자동 주입
@RequestMapping("/api/user")        // http://127.0.0.1:8095/api/user
public class UserController {
    private final UserService userService;

    // healthCheck : 서버가 동작하는지 여부를 확인하기 위한 리퀘스트 처리부

    // http://localhost:8095/api/user/health

    /**
     * 서버가 동작하는지 여부를 확인하기 위한 리퀘스트 처리부
     *
     * @return ResponseEntity : message에 동작중임을 알리는 내용이 전달된다.
     */
    @GetMapping("/health")
    public ResponseEntity<UserApiResponse<String>> healthCheck() {
        String res = "User 서비스가 동작중 입니다";
        UserApiResponse<String> apiResponse = new UserApiResponse<>(
                "success",
                res,
                null);
        return ResponseEntity.ok(apiResponse);  // response code : 200
    }

    /**
     * 회원가입 요청(request) 처리
     * param: SignupUserDTO signupDTO
     * 아래와 같이 JSON형태로 전달한다.
     * {
     *   "email": "test@gmail.com",
     *   "password": "1234",
     *   "nick_name": "홍길동"
     * }
     **/
    @PostMapping("/signup")     // http://localhost:8095/api/user/signup
    public ResponseEntity<UserApiResponse<String>> signup(@RequestBody SignupUserDTO signupDTO) {
        try {
            System.out.println("*** /signup 엔드포인트 호출됨 ***");
            // 이메일 정보가 누락되었다면 에러 코드를 반환한다.
            if (signupDTO.getEmail() == null || signupDTO.getEmail().isEmpty()) {
                UserApiResponse<String> response = new UserApiResponse<>(
                        "error",
                        "이메일은 필수 필드 입니다",
                        null
                );
                return ResponseEntity.badRequest().body(response);
            }

            // 비밀번호가 누락되었다면 에러코드를 변환
            if (signupDTO.getPassword() == null || signupDTO.getPassword().isEmpty()) {
                UserApiResponse<String> response = new UserApiResponse<>(
                        "error",
                        "비밀번호는 필수 필드 입니다",
                        null
                );
                return ResponseEntity.badRequest().body(response);
            }

            // 닉네임이 누락되었다면 에러코드를 변환
            if (signupDTO.getNick_name() == null || signupDTO.getNick_name().isEmpty()) {
                UserApiResponse<String> response = new UserApiResponse<>(
                        "error",
                        "닉네임은 필수 필드 입니다",
                        null
                );
                return ResponseEntity.badRequest().body(response);
            }

            // 누락된 정보가 없다면 회원가입 처리를 userService에게 처리하게 하고 결과를 반환한다.
            User user = userService.signup(signupDTO);
            if (user.getEmail() != null && user.getNickName() != null) {
                UserApiResponse<String> response = new UserApiResponse<>(
                        "success",
                        "회원가입 성공",
                        null
                );
                return ResponseEntity.ok(response);
            }

            UserApiResponse<String> response = new UserApiResponse<>(
                    "error",
                    "회원가입 실패",
                    null
            );
            return ResponseEntity.internalServerError().body(response);

        } catch (IllegalArgumentException e) {
            UserApiResponse<String> response = new UserApiResponse<>(
                    "error",
                    e.getMessage(),
                    null
            );
            return ResponseEntity.internalServerError().body(response);
        } catch (Exception e) {
            UserApiResponse<String> response = new UserApiResponse<>(
                    "error",
                    "데이터베이스 저장중에 알 수 없는 에러가 발생했습니다.",
                    null
            );
            return ResponseEntity.internalServerError().body(response);

        }


    }
    /**
     * 로그인(request) 처리
     * param: LoginUserDTO loginUserDTO
     * 아래와 같이 JSON형태로 전달한다.
     * {
     *     "status": "success",
     *     "message": "로그인 성공",
     *     "data": {
     *         "id": 1,
     *         "email": "test@gmail.com",
     *         "nick_name": "홍길동"
     *        }
     * }
     **/
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginUserDTO loginUserDTO, HttpSession session) {
        try {
            // loginUserDTO에 누락된 정보가 있는지 검사
            if ( loginUserDTO.getEmail() == null || loginUserDTO.getEmail().isEmpty() ){
                UserApiResponse<String> response = new UserApiResponse<>(
                        "error",
                        "이메일은 필수 필드 입니다",
                        null
                );
                return ResponseEntity.badRequest().body(response);
            }
            if ( loginUserDTO.getPassword() == null || loginUserDTO.getPassword().isEmpty()) {
                UserApiResponse<String> response = new UserApiResponse<>(
                        "error",
                        "비밀번호는 필수 필드 입니다",
                        null
                );
                return ResponseEntity.badRequest().body(response);
            }

            LoginUserResponseDTO dto = userService.login(loginUserDTO);

            // Session 객체를 만들어서 서버와 클라이언트가 갖음
            session.setAttribute("loginUser", dto);     // 로그인 처리

            UserApiResponse<LoginUserResponseDTO> response = new UserApiResponse<>(
                    "success",
                    "로그인 성공",
                    dto
            );
            return ResponseEntity.ok( response );
            /*
            * {
            *   "id":
            *   "email":
            *   "nick_name":
            * }
            * */

        } catch (IllegalArgumentException e){
            UserApiResponse<String> response = new UserApiResponse<>(
                    "error",
                    e.getMessage(),
                    null
            );
            return ResponseEntity.internalServerError().body(response);
        } catch (Exception e){
            UserApiResponse<String> response = new UserApiResponse<>(
                    "error",
                    e.getMessage(),
                    null
            );
            return ResponseEntity.badRequest().body(response);

        }
    }

    /**
     * 로그아웃 요청을 처리하는 메서드.
     *
     * <p>현재 사용자의 세션을 무효화하여(초기화하여) 로그아웃을 수행한다.
     * 세션이 종료되면 인증 정보 및 사용자 관련 세션 데이터가 모두 삭제된다.</p>
     *
     * @param session 현재 사용자 세션 객체(HttpSession)
     * @return 로그아웃 성공 응답을 포함한 ResponseEntity
     */
    // logout : 로그아웃 리퀘스트 처리부
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();       // 현재 사용자(스레드) session 클리어
        return ResponseEntity.ok(new UserApiResponse<>("success", "로그아웃되었습니다.", null));
    }

    /**
     * 현재 사용자 세션 상태를 확인하는 메서드.
     *
     * <p>세션은 상태(Stateful)를 유지하는 객체로, 로그인 여부를 확인하기 위해
     * 세션에 저장된 {@code loginUser} 정보를 조회한다.</p>
     *
     * <p>세션에 로그인 정보가 존재하지 않으면 HTTP 401(Unauthorized) 상태 코드와 함께
     * "로그인이 필요합니다." 메시지를 반환한다.
     * 로그인되어 있을 경우, 사용자 정보를 포함한 성공 응답을 반환한다.</p>
     *
     * @param session 현재 사용자 세션(HttpSession)
     * @return 세션 상태에 따른 응답(ResponseEntity)
     */
    // session은 상태(state) 객체이다  <===> stateless
    // session : 세션 확인(로그인 확인) 처리부
    @GetMapping("/session")
    public ResponseEntity<?> checkSession(HttpSession session){
        LoginUserResponseDTO userDTO = (LoginUserResponseDTO)session.getAttribute("loginUser");
        if( userDTO == null ) {
            // Http Error 401 = Unauthorized
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        // 로그인 사용자
        // String data = "사용자 데이터 : " + userDTO.getNick_name() + "님, 반갑습니다.";
        return ResponseEntity.ok(new UserApiResponse<LoginUserResponseDTO>("success", "로그인 사용자", userDTO));
    }


    // 로그인한 사용자만 접근 가능한 리퀘스트 테스트

}
