package com.project.playlist.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;


import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    @Builder.Default
    private int errorCode = ErrorCodes.GENERAL_ERROR;
    @Builder.Default
    private HttpStatus status = HttpStatus.BAD_REQUEST;
    @Builder.Default
    private ErrorType type = ErrorType.GENERAL_ERROR;
    @Builder.Default
    private String message = "Error";
    private String path;
    private Long id;
    @Builder.Default
    private List<String> errors = Collections.emptyList();
}
