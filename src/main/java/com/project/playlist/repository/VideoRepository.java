package com.project.playlist.repository;

import com.project.playlist.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    public Video findByUrl(String url);
}
