package com.project.playlist.dto;

import com.project.playlist.model.PlaylistVideo;
import com.project.playlist.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistDTO {
    private Long id;
    private String name;
    private User user;
    private Collection<PlaylistVideo> playlistVideos;
}
