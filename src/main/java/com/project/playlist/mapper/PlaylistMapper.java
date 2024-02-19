package com.project.playlist.mapper;

import com.project.playlist.dto.PlaylistDTO;
import com.project.playlist.dto.VideoForPlaylistDTO;
import com.project.playlist.model.Playlist;
import com.project.playlist.model.PlaylistVideo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface PlaylistMapper {
    PlaylistMapper INSTANCE = Mappers.getMapper(PlaylistMapper.class);

    @Mapping(target = "videos", source = "playlist.playlistVideos")
    PlaylistDTO toDTO(Playlist playlist);

    @Mapping(target = "id", source = "pv.video.id")
    @Mapping(target = "name", source = "pv.video.name")
    @Mapping(target = "url", source = "pv.video.url")
    @Mapping(target = "description", source = "pv.video.description")
    @Mapping(target = "orderNo", source = "pv.orderNo")
    @Mapping(target = "user", source = "pv.video.user")
    VideoForPlaylistDTO playlistVideoToVideoForPlaylistDTO(PlaylistVideo pv);
}
