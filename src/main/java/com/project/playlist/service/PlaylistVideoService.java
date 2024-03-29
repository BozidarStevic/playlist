package com.project.playlist.service;

import com.project.playlist.model.PlaylistVideo;

import java.util.List;

public interface PlaylistVideoService {
    PlaylistVideo addVideoToPlaylist(Long playlistId, Long videoId);
    void removeVideoFromPlaylist(Long playlistId, Long videoId);
    void changeVideoOrder(Long playlistId, int fromOrderNo, int toOrderNo);
    List<PlaylistVideo> getSortedPlaylistVideosForPlaylist(Long playlistId);
}
