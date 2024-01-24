package com.project.playlist.service;

import com.project.playlist.model.PlaylistVideo;
import com.project.playlist.model.Video;

import java.util.List;

public interface PlaylistVideoService {
    void addVideoToPlaylist(Long playlistId, Long videoId);
    void removeVideoFromPlaylist(Long playlistId, Long videoId);
    void changeVideoOrder(Long playlistId, int fromOrderNo, int toOrderNo);
    List<Video> getSortedVideosForPlaylist(Long playlistId);
}
