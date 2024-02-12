package com.project.playlist.mapper;

import com.project.playlist.dto.VideoDTO;
import com.project.playlist.dto.VideoForPlaylistDTO;
import com.project.playlist.dto.VideoRequest;
import com.project.playlist.model.Video;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface VideoMapper {
    VideoMapper INSTANCE = Mappers.getMapper(VideoMapper.class);

    VideoDTO toDTO(Video video);
    Video fromRequest(VideoRequest videoRequest);

    @Mapping(target = "orderNo", source = "orderNo")
    VideoForPlaylistDTO videoToVideoForPlaylistDTO(Video video, int orderNo);

//    @Mapping(target = "id", source = "pv.video.id")
//    @Mapping(target = "name", source = "pv.video.name")
//    @Mapping(target = "url", source = "pv.video.url")
//    @Mapping(target = "description", source = "pv.video.description")
//    @Mapping(target = "orderNo", source = "pv.orderNo")
//    @Mapping(target = "user", source = "pv.video.user")
//    VideoForPlaylistDTO playlistVideoToVideoForPlaylistDTO(PlaylistVideo pv);

    List<VideoDTO> toDTOList(List<Video> videoList);
//    List<VideoForPlaylistDTO> toVideoForPlaylistDTOList(List<PlaylistVideo> playlistVideoList);
}
