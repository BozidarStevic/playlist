package com.project.playlist.service;

import com.project.playlist.dto.VideoRequest;
import com.project.playlist.model.Video;

import java.util.List;

public interface VideoService {
    List<Video> getAllVideos();
    Video createVideo(VideoRequest videoRequest);
}
