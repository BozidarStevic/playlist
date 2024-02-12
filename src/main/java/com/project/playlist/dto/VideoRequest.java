package com.project.playlist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoRequest {
    @NotBlank(message = "Name must not be blank")
    private String name;
    @NotBlank(message = "URL must not be blank")
    @URL(message = "Invalid URL")
    private String url;
    private String description;
    @NotNull(message = "User id must not be empty")
    private Long userId;
}
