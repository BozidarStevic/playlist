package com.project.playlist.dto;

import com.project.playlist.model.User;
import java.util.Collection;

public class PlaylistVideoDTO {
    private Long id;
    private String name;
    private User user;
    private Collection<VideoDTO> videos;
}
