package com.project.playlist.service.impl;

import com.project.playlist.dto.VideoRequest;
import com.project.playlist.mapper.VideoMapper;
import com.project.playlist.model.User;
import com.project.playlist.model.Video;
import com.project.playlist.repository.VideoRepository;
import com.project.playlist.service.UserService;
import com.project.playlist.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoServiceImpl implements VideoService {
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private UserService userService;

    @Override
    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }

    @Override
    public Video createVideo(VideoRequest videoRequest) {
        User user = userService.getUserById(videoRequest.getUserId());
        Video existingVideo = videoRepository.findByUrl(videoRequest.getUrl());
        if (existingVideo == null) {
            Video video = VideoMapper.INSTANCE.fromRequest(videoRequest);
            video.setUser(user);
            return videoRepository.save(video);
        } else {
            return null;
        }
    }
}
