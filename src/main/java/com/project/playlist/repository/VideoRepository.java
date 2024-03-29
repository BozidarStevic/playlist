package com.project.playlist.repository;

import com.project.playlist.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
//    Optional<Video> findByUrlAndUserId(String url, Long id);
    boolean existsByUrlAndUserId(String url, Long id);
}
