package sopt.univoice.infra.common.dto;

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
}