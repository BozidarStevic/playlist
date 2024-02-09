package com.project.playlist.mapper;

import com.project.playlist.dto.PlaylistDTO;
import com.project.playlist.model.Playlist;

public interface PlaylistCustomMapper {
    PlaylistDTO toDTO(Playlist playlist);
}
