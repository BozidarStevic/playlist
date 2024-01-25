package com.project.playlist.repository;

import com.project.playlist.model.PlaylistVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaylistVideoRepository extends JpaRepository<PlaylistVideo, Long> {
    Optional<PlaylistVideo> findByPlaylistIdAndVideoId(Long playlistId, Long videoId);
    List<PlaylistVideo> findByPlaylistId(Long playlistId);
    List<PlaylistVideo> findByPlaylistIdOrderByOrderNo(Long playlistId);
}
