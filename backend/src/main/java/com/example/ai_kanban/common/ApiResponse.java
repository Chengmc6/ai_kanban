package com.example.ai_kanban.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    private int code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "Success", null);
    }

    public static<T> ApiResponse<T>  success(String message){
        return new ApiResponse<>(200,message,null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "Success", data);
    }

    public static <T> ApiResponse<T> fail(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(500, message, null);
    }

    public static <T> ApiResponse<T> fail(ResultCode result) {
        return new ApiResponse<>(result.getCode(), result.getMessage(), null);
    }

    // ======= 统一 Builder（推荐使用） =======
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    @Data
    public static class Builder<T> {
        private int code = 200;
        private String message = "Success";
        private T data;

        public Builder<T> code(int code) {
            this.code = code;
            return this;
        }

        public Builder<T> message(String message) {
            this.message = message;
            return this;
        }

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public ApiResponse<T> build() {
            return new ApiResponse<>(code, message, data);
        }
    }
}
