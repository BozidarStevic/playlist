package com.project.playlist.dto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoRequest {
    private String name;
    private String url;
    private String description;
    private Long userId;
}
