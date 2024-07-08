package sopt.univoice.infra.common.dto;

import sopt.univoice.infra.common.exception.message.ErrorMessage;

import java.util.Map;

public record SuccessStatusResponse<T>(
    int status,
    String message,
    T data
) {
    public static <T> SuccessStatusResponse<T> of(SuccessMessage successMessage, T data) {
        return new SuccessStatusResponse<>(successMessage.getStatus(), successMessage.getMessage(), data);
    }
    public static SuccessStatusResponse<Void> of(SuccessMessage successMessage) {
        return new SuccessStatusResponse<>(successMessage.getStatus(), successMessage.getMessage(), null);
    }

    public static <T> SuccessStatusResponse<Map<String, T>> of(SuccessMessage successMessage, String key, T data) {
        return new SuccessStatusResponse<>(successMessage.getStatus(), successMessage.getMessage(), Map.of(key, data));
    }

    public static <T> SuccessStatusResponse<T> of(ErrorMessage errorMessage, T data) {
        return new SuccessStatusResponse<>(errorMessage.getStatus(), errorMessage.getMessage(), data);
    }

    public static <T> SuccessStatusResponse<Void> of(ErrorMessage errorMessage) {
        return new SuccessStatusResponse<>(errorMessage.getStatus(), errorMessage.getMessage(), null);
    }
}