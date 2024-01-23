package com.project.playlist.mapper;

import com.project.playlist.dto.PlaylistDTO;
import com.project.playlist.dto.PlaylistRequest;
import com.project.playlist.model.Playlist;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface PlaylistMapper {
    PlaylistMapper INSTANCE = Mappers.getMapper(PlaylistMapper.class);

    public PlaylistDTO toDTO(Playlist playlist);
    public Playlist fromRequest(PlaylistRequest playlistRequest);

//    public List<PlaylistDTO> toDTOList(List<Playlist> itemCollection);
//    public List<Playlist> fromDTOList(List<PlaylistDTO> itemDTOCollection);
}
