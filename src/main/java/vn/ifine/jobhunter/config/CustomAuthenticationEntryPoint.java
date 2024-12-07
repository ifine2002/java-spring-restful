package vn.ifine.jobhunter.config;

import java.io.IOException;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.ifine.jobhunter.domain.RestResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    // Sử dụng BearerTokenAuthenticationEntryPoint mặc định của Spring
    private final AuthenticationEntryPoint delegate = new BearerTokenAuthenticationEntryPoint();

    // ObjectMapper để chuyển đổi object thành JSON
    private final ObjectMapper mapper;

    // Constructor injection
    public CustomAuthenticationEntryPoint(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        // Constructor injection
        this.delegate.commence(request, response, authException);

        // 2. Set response type là JSON
        response.setContentType("application/json;charset=UTF-8");

        // 3. Tạo custom response
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.UNAUTHORIZED.value());

        // 4. Lấy message lỗi chi tiết
        String errorMessage = Optional.ofNullable((authException.getCause()))
                .map(Throwable::getMessage)
                .orElse(authException.getMessage());
        res.setError(errorMessage);

        // 5. Set message lỗi người dùng
        res.setMessage("Token không hợp lệ (hết hạn, không đúng định dạng, hoặc không truyền JWT ở header)...");

        // 6. Chuyển response thành JSON và gửi về client
        mapper.writeValue(response.getWriter(), res);
    }
}
