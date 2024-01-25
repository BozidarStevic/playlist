package com.project.playlist.service.impl;


import com.project.playlist.model.PlaylistVideo;
import com.project.playlist.model.Playlist;
import com.project.playlist.model.Video;
import com.project.playlist.repository.PlaylistVideoRepository;
import com.project.playlist.service.PlaylistService;
import com.project.playlist.service.PlaylistVideoService;
import com.project.playlist.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlaylistVideoServiceImpl implements PlaylistVideoService {
    @Autowired
    private VideoService videoService;
    @Autowired
    private PlaylistService playlistService;
    @Autowired
    private PlaylistVideoRepository playlistVideoRepository;

    @Override
    public void addVideoToPlaylist(Long playlistId, Long videoId) {
        Playlist playlist = playlistService.getPlaylistById(playlistId);
        Video video = videoService.getVideoById(videoId);

        List<PlaylistVideo> playlistVideoList = playlist.getPlaylistVideos();
        for (PlaylistVideo pv : playlistVideoList) {
            if (pv.getVideo().getId().longValue() == videoId.longValue()) {
                System.out.println("Video with id " + videoId + " already exist in playlist.");
                return;
            }
        }
        PlaylistVideo playlistVideo = new PlaylistVideo();
        playlistVideo.setPlaylist(playlist);
        playlistVideo.setVideo(video);
        int orderNo = playlistVideoList.size() + 1;
        playlistVideo.setOrderNo(orderNo);
        playlistVideoRepository.save(playlistVideo);
    }

    @Override
    @Transactional
    public void removeVideoFromPlaylist(Long playlistId, Long videoId) {
        playlistService.getPlaylistById(playlistId);
        videoService.getVideoById(videoId);

        Optional<PlaylistVideo> playlistVideoOptional = playlistVideoRepository.findByPlaylistIdAndVideoId(playlistId, videoId);
        if (playlistVideoOptional.isPresent()) {
            PlaylistVideo playlistVideo = playlistVideoOptional.get();
            int removedOrderNo = playlistVideo.getOrderNo();
            playlistVideoRepository.delete(playlistVideo);

            List<PlaylistVideo> playlistVideoList = playlistVideoRepository.findByPlaylistId(playlistId);
            for (PlaylistVideo pv : playlistVideoList) {
                if (pv.getOrderNo() > removedOrderNo) {
                    pv.setOrderNo(pv.getOrderNo() - 1);
                    playlistVideoRepository.save(pv);
                }
            }
        } else {
            String msg = "Video with id " + videoId + " is not in playlist with id " + playlistId;
            throw new IllegalArgumentException(msg);
        }
    }

    @Override
    public void changeVideoOrder(Long playlistId, int fromOrderNo, int toOrderNo) {
        playlistService.getPlaylistById(playlistId);

        List<PlaylistVideo> playlistVideoList = playlistVideoRepository.findByPlaylistId(playlistId);
        if (playlistVideoList.isEmpty()) {
            String msg = "Playlist with id " + playlistId + " doesn't have any video!";
            throw new IllegalArgumentException(msg);
        }
        if (fromOrderNo == toOrderNo || fromOrderNo < 1 || toOrderNo < 1 || fromOrderNo > playlistVideoList.size() || toOrderNo > playlistVideoList.size()) {
            String msg = "Invalid order numbers!";
            throw new IllegalArgumentException(msg);
        }

        PlaylistVideo fromPlaylistVideo = null;
        PlaylistVideo toPlaylistVideo = null;
        for (PlaylistVideo pv : playlistVideoList) {
            if (pv.getOrderNo() == fromOrderNo) {
                fromPlaylistVideo = pv;
            } else if (pv.getOrderNo() == toOrderNo) {
                toPlaylistVideo = pv;
            }
        }
        if (fromPlaylistVideo != null && toPlaylistVideo != null) {
            changeOrderTransactional(fromPlaylistVideo, playlistVideoList, fromOrderNo, toOrderNo);
        } else {
            String msg = "The playlist does not have a video with one of the provided order numbers!";
            throw new IllegalArgumentException(msg);
        }
    }
    @Transactional
    private void changeOrderTransactional(PlaylistVideo fromPlaylistVideo, List<PlaylistVideo> playlistVideoList, int fromOrderNo, int toOrderNo) {
        int direction = (fromOrderNo > toOrderNo) ? 1 : -1;
        for (PlaylistVideo pv : playlistVideoList) {
            if ((direction == 1 && pv.getOrderNo() >= toOrderNo && pv.getOrderNo() < fromOrderNo) ||
                    (direction == -1 && pv.getOrderNo() > fromOrderNo && pv.getOrderNo() <= toOrderNo)) {
                pv.setOrderNo(pv.getOrderNo() + direction);
                playlistVideoRepository.save(pv);
            }
        }
        fromPlaylistVideo.setOrderNo(toOrderNo);
        playlistVideoRepository.save(fromPlaylistVideo);
    }

    @Override
    public List<Video> getSortedVideosForPlaylist(Long playlistId) {
        playlistService.getPlaylistById(playlistId);
        List<PlaylistVideo> playlistVideoList = playlistVideoRepository.findByPlaylistIdOrderByOrderNo(playlistId);
        List<Video> videos = new ArrayList<>();
        for (PlaylistVideo pv : playlistVideoList) {
            videos.add(pv.getVideo());
        }
        return videos;
    }
}
