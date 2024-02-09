package com.project.playlist.mapper;

import com.project.playlist.dto.PlaylistDTO;
import com.project.playlist.dto.UserDTO;
import com.project.playlist.dto.VideoForPlaylistDTO;
import com.project.playlist.model.Playlist;
import com.project.playlist.model.User;
import com.project.playlist.model.Video;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PlaylistCustomMapperImpl implements PlaylistCustomMapper {
    @Override
    public PlaylistDTO toDTO(Playlist playlist) {

        PlaylistDTO playlistDTO = new PlaylistDTO();

        if ( playlist != null ) {
            playlistDTO.setId( playlist.getId() );
            playlistDTO.setName( playlist.getName() );
            playlistDTO.setUser( userToUserDTO( playlist.getUser() ) );

            List<VideoForPlaylistDTO> videos = playlist.getPlaylistVideos().stream()
                    .map(pv -> videoToVideoForPlaylistDTO(pv.getVideo(), pv.getOrderNo()))
                    .collect(Collectors.toList());
            playlistDTO.setVideos(videos);
        }

        return playlistDTO;
    }

    protected UserDTO userToUserDTO(User user) {

        UserDTO userDTO = new UserDTO();

        if ( user != null ) {
            userDTO.setId( user.getId() );
            userDTO.setUsername( user.getUsername() );
            userDTO.setEmail( user.getEmail() );
        }

        return userDTO;
    }

    protected VideoForPlaylistDTO videoToVideoForPlaylistDTO(Video video, int orderNo) {

        VideoForPlaylistDTO videoForPlaylistDTO = new VideoForPlaylistDTO();

        if ( video != null ) {
            videoForPlaylistDTO.setId( video.getId() );
            videoForPlaylistDTO.setName( video.getName() );
            videoForPlaylistDTO.setUrl( video.getUrl() );
            videoForPlaylistDTO.setDescription( video.getDescription() );
            videoForPlaylistDTO.setUser( userToUserDTO( video.getUser() ) );
            videoForPlaylistDTO.setOrderNo(orderNo);
        }

        return videoForPlaylistDTO;
    }
}
