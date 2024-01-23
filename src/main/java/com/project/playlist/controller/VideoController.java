package com.project.playlist.controller;

import com.project.playlist.dto.VideoDTO;
import com.project.playlist.dto.VideoRequest;
import com.project.playlist.mapper.VideoMapper;
import com.project.playlist.model.Video;
import com.project.playlist.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/videos")
public class VideoController {
    @Autowired
    private VideoService videoService;

    @PostMapping()
    public  ResponseEntity<VideoDTO> createVideo(@RequestBody VideoRequest videoRequest) {
        Video video = videoService.createVideo(videoRequest);
        if (video == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        VideoDTO videoDTO = VideoMapper.INSTANCE.toDTO(video);
        return ResponseEntity.created(null).body(videoDTO);
    }

    @GetMapping()
    public ResponseEntity<List<VideoDTO>> getAllMovies() {
        List<Video> allVideos = videoService.getAllVideos();
        if (allVideos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<VideoDTO> allVideosDTO = VideoMapper.INSTANCE.toDTOList(allVideos);
        return ResponseEntity.ok(allVideosDTO);
    }


}
