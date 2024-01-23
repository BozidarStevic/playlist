package com.project.playlist.dto;

import com.project.playlist.model.PlaylistVideo;

import java.util.Collection;

import com.project.playlist.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VideoDTO {
    private Long id;
    private String name;
    private String url;
    private String description;
    private UserDTO user;
//    private Collection<PlaylistVideo> playlistVideos;
}
