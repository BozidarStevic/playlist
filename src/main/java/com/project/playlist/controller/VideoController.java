package com.project.playlist.controller;

import com.project.playlist.dto.VideoDTO;
import com.project.playlist.dto.VideoRequest;
import com.project.playlist.mapper.VideoMapper;
import com.project.playlist.model.Video;
import com.project.playlist.service.VideoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping()
    @Validated
    public VideoDTO createVideo(@RequestBody @Valid VideoRequest videoRequest) {
        Video video = videoService.createVideo(videoRequest);
        return VideoMapper.INSTANCE.toDTO(video);
    }

    @GetMapping()
    public List<VideoDTO> getAllVideos() {
        List<Video> allVideos = videoService.getAllVideos();
        return VideoMapper.INSTANCE.toDTOList(allVideos);
    }

    @GetMapping("/{videoId}")
    public VideoDTO getVideoById(@PathVariable @NotNull Long videoId) {
        Video video = videoService.getVideoById(videoId);
        return VideoMapper.INSTANCE.toDTO(video);
    }

}
