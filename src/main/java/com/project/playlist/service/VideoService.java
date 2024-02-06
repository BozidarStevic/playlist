package com.project.playlist.service;

import com.project.playlist.dto.VideoRequest;
import com.project.playlist.model.User;
import com.project.playlist.model.Video;

import java.util.List;
import java.util.Optional;

public interface VideoService {
    List<Video> getAllVideos();
    Video createVideo(VideoRequest videoRequest);

    Optional<Video> getVideoByUrlAndUserId(String url, User user);

    Video getVideoById(Long videoId);
}
