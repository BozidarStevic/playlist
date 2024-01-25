package com.project.playlist.controller;

import com.project.playlist.dto.PlaylistDTO;
import com.project.playlist.dto.VideoDTO;
import com.project.playlist.dto.VideoRequest;
import com.project.playlist.exceptions.DuplicateVideoUrlForUserException;
import com.project.playlist.exceptions.PlaylistNotFoundException;
import com.project.playlist.exceptions.UserNotFoundException;
import com.project.playlist.exceptions.VideoNotFoundException;
import com.project.playlist.mapper.PlaylistMapper;
import com.project.playlist.mapper.VideoMapper;
import com.project.playlist.model.Playlist;
import com.project.playlist.model.Video;
import com.project.playlist.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("api/videos")
public class VideoController {
    @Autowired
    private VideoService videoService;

    @PostMapping()
    public  ResponseEntity<VideoDTO> createVideo(@RequestBody VideoRequest videoRequest) {
        try {
            Video video = videoService.createVideo(videoRequest);
            VideoDTO videoDTO = VideoMapper.INSTANCE.toDTO(video);
            return ResponseEntity.created(URI.create("api/videos/" + video.getId())).body(videoDTO);
        } catch (UserNotFoundException | DuplicateVideoUrlForUserException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping()
    public ResponseEntity<List<VideoDTO>> getAllVideos() {
        try {
            List<Video> allVideos = videoService.getAllVideos();
            List<VideoDTO> allVideosDTO = VideoMapper.INSTANCE.toDTOList(allVideos);
            return ResponseEntity.ok(allVideosDTO);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{videoId}")
    public ResponseEntity<VideoDTO> getVideoById(@PathVariable Long videoId) {
        try {
            Video video = videoService.getVideoById(videoId);
            VideoDTO videoDTO = VideoMapper.INSTANCE.toDTO(video);
            return ResponseEntity.ok(videoDTO);
        } catch (VideoNotFoundException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

}
