package com.project.playlist.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VideoForPlaylistDTO {
    private Long id;
    private String name;
    private String url;
    private String description;
    private int orderNo;
    private UserDTO user;
}
