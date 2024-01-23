package com.project.playlist.repository;

import com.project.playlist.model.PlaylistVideo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistVideoRepository extends JpaRepository<PlaylistVideo, Long> {
    public PlaylistVideo findByPlaylistIdAndVideoId(Long playlistId, Long videoId);
    public List<PlaylistVideo> findByPlaylistId(Long playlistId);
    public List<PlaylistVideo> findByPlaylistIdOrderByOrderNo(Long playlistId);
}
