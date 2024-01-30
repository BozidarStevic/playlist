package com.project.playlist.service.impl;

import com.project.playlist.dto.VideoRequest;
import com.project.playlist.exceptions.DuplicateVideoUrlForUserException;
import com.project.playlist.exceptions.VideoNotFoundException;
import com.project.playlist.mapper.VideoMapper;
import com.project.playlist.model.User;
import com.project.playlist.model.Video;
import com.project.playlist.repository.VideoRepository;
import com.project.playlist.service.UserService;
import com.project.playlist.service.VideoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;

    private final UserService userService;

    public VideoServiceImpl(VideoRepository videoRepository, UserService userService) {
        this.videoRepository = videoRepository;
        this.userService = userService;
    }

    @Override
    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }

    @Override
    public Video createVideo(VideoRequest videoRequest) {
        User user = userService.getUserById(videoRequest.getUserId());
        videoRepository.findByUrlAndUserId(videoRequest.getUrl(), user.getId())
                .orElseThrow(() -> new DuplicateVideoUrlForUserException(videoRequest.getUrl(), user.getId()));
        Video video = VideoMapper.INSTANCE.fromRequest(videoRequest);
        video.setUser(user);
        return videoRepository.save(video);
    }

    @Override
    public Video getVideoById(Long videoId) {
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new VideoNotFoundException(videoId));
    }
}
