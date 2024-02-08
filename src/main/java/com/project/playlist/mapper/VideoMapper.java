package com.project.playlist.mapper;

import com.project.playlist.dto.VideoDTO;
import com.project.playlist.dto.VideoRequest;
import com.project.playlist.model.Video;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface VideoMapper {
    VideoMapper INSTANCE = Mappers.getMapper(VideoMapper.class);

    VideoDTO toDTO(Video video);
    Video fromRequest(VideoRequest videoRequest);

    List<VideoDTO> toDTOList(List<Video> itemCollection);
}
