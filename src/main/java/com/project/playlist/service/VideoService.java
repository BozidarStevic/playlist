package com.project.playlist.service;

import com.project.playlist.dto.VideoRequest;
import com.project.playlist.model.User;
import com.project.playlist.model.Video;

import java.util.List;

public interface VideoService {
    List<Video> getAllVideos();
    Video createVideo(VideoRequest videoRequest);

    Video getVideoByUrlAndUserId(String url, User user);

    Video getVideoById(Long videoId);
}
