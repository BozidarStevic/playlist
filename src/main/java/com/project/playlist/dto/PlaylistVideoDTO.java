package com.project.playlist.dto;

import com.project.playlist.model.User;
import com.project.playlist.model.Video;

import java.util.Collection;

public class PlaylistVideoDTO {
    private Long id;
    private String name;
    private User user;
    private Collection<VideoDTO> videos;
}
