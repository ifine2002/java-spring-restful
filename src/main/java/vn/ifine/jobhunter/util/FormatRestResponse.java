package vn.ifine.jobhunter.util;

import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import jakarta.servlet.http.HttpServletResponse;
import vn.ifine.jobhunter.domain.response.RestResponse;
import vn.ifine.jobhunter.util.annotation.ApiMessage;

@ControllerAdvice
public class FormatRestResponse implements ResponseBodyAdvice<Object> {
    // 1. Phương thức kiểm tra có support format hay không
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    // 2. Phương thức chính để format response
    @Override
    @Nullable
    public Object beforeBodyWrite(
            Object body, // Dữ liệu gốc từ controller
            MethodParameter returnType, // Thông tin về method trả về
            MediaType selectedContentType, // Kiểu content (JSON, XML,...)
            Class selectedConverterType, // Class converter được chọn
            ServerHttpRequest request, // Request information
            ServerHttpResponse response) // Response information
    {
        // 2.1 Lấy HTTP status code
        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int status = servletResponse.getStatus();

        // 2.2 Tạo đối tượng response chuẩn
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(status);

        // 2.3 Xử lý các trường hợp đặc biệt
        if (body instanceof String || body instanceof Resource) {
            return body; // Không format nếu response là String
        }
        // swagger
        String path = request.getURI().getPath();
        if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui")) {
            return body;
        }

        // 2.4 Xử lý response theo status code
        if (status >= 400) {
            // Trường hợp lỗi: trả về body gốc
            return body;

        } else {
            // Trường hợp thành công:
            res.setData(body);// Set data

            // Set message từ annotation hoặc mặc định
            ApiMessage message = returnType.getMethodAnnotation(ApiMessage.class);
            res.setMessage(message != null ? message.value() : "CALL API SUCCESS");
        }
        return res;
    }

}
