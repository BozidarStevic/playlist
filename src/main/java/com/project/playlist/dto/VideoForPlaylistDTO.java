package com.project.playlist.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoForPlaylistDTO {
    private Long id;
    private String name;
    private String url;
    private String description;
    private int orderNo;
    private UserDTO user;
}
