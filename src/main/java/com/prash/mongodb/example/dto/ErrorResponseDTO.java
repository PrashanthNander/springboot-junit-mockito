package com.prash.mongodb.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponseDTO {

    private HttpStatus errorCode;

    private  String errorMessage;

    private LocalDateTime errorTime;
}
