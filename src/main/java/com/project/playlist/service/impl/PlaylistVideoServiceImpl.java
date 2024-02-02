package com.project.playlist.service.impl;


import com.project.playlist.exceptions.VideoAlreadyInPlaylistException;
import com.project.playlist.model.Playlist;
import com.project.playlist.model.PlaylistVideo;
import com.project.playlist.model.Video;
import com.project.playlist.repository.PlaylistVideoRepository;
import com.project.playlist.service.PlaylistService;
import com.project.playlist.service.PlaylistVideoService;
import com.project.playlist.service.VideoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PlaylistVideoServiceImpl implements PlaylistVideoService {

    private final VideoService videoService;

    private final PlaylistService playlistService;

    private final PlaylistVideoRepository playlistVideoRepository;

    public PlaylistVideoServiceImpl(VideoService videoService, PlaylistService playlistService, PlaylistVideoRepository playlistVideoRepository) {
        this.videoService = videoService;
        this.playlistService = playlistService;
        this.playlistVideoRepository = playlistVideoRepository;
    }

    @Override
    public PlaylistVideo addVideoToPlaylist(Long playlistId, Long videoId) {
        Playlist playlist = playlistService.getPlaylistById(playlistId);
        Video video = videoService.getVideoById(videoId);
        List<PlaylistVideo> playlistVideoList = playlist.getPlaylistVideos();
        if (playlistVideoList.stream()
                .anyMatch(pv -> pv.getVideo().getId().equals(videoId))) {
            throw new VideoAlreadyInPlaylistException(videoId, playlistId);
        }
        PlaylistVideo playlistVideo = new PlaylistVideo();
        playlistVideo.setPlaylist(playlist);
        playlistVideo.setVideo(video);
        int orderNo = playlistVideoList.size() + 1;
        playlistVideo.setOrderNo(orderNo);
        return playlistVideoRepository.save(playlistVideo);
    }

    @Override
    public void removeVideoFromPlaylist(Long playlistId, Long videoId) {
        playlistService.getPlaylistById(playlistId);
        videoService.getVideoById(videoId);
        PlaylistVideo playlistVideo = getPlaylistVideo(playlistId, videoId);
        int removedOrderNo = playlistVideo.getOrderNo();
        playlistVideoRepository.delete(playlistVideo);
        List<PlaylistVideo> playlistVideoList = playlistVideoRepository.findByPlaylistId(playlistId);
        playlistVideoList.stream()
                .filter(pv -> pv.getOrderNo() > removedOrderNo)
                .forEach(pv -> {
                    pv.setOrderNo(pv.getOrderNo() - 1);
                    playlistVideoRepository.save(pv);
                });
    }

    private PlaylistVideo getPlaylistVideo(Long playlistId, Long videoId) {
        return playlistVideoRepository.findByPlaylistIdAndVideoId(playlistId, videoId)
                .orElseThrow(() -> new IllegalArgumentException("Video with id " + videoId + " is not in playlist with id " + playlistId));
    }

    @Override
    public void changeVideoOrder(Long playlistId, int fromOrderNo, int toOrderNo) {
        playlistService.getPlaylistById(playlistId);
        List<PlaylistVideo> playlistVideoList = playlistVideoRepository.findByPlaylistId(playlistId);
        if (playlistVideoList.isEmpty()) {
            throw new IllegalArgumentException("Playlist with id " + playlistId + " doesn't have any video!");
        }
        if (areOrderNumbersIncorrect(fromOrderNo, toOrderNo, playlistVideoList)) {
            throw new IllegalArgumentException("Invalid order numbers!");
        }
        String msg = "The playlist does not have a video with order number: ";
        PlaylistVideo fromPlaylistVideo = playlistVideoList.stream()
                .filter(pv -> pv.getOrderNo() == fromOrderNo).findFirst()
                .orElseThrow(() -> new IllegalArgumentException(msg + fromOrderNo));
        playlistVideoList.stream()
                .filter(pv -> (pv.getOrderNo() == toOrderNo)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException(msg + toOrderNo));
        changeOrderTransactional(fromPlaylistVideo, playlistVideoList, fromOrderNo, toOrderNo);
    }

    private static boolean areOrderNumbersIncorrect(int fromOrderNo, int toOrderNo, List<PlaylistVideo> playlistVideoList) {
        return fromOrderNo == toOrderNo || fromOrderNo < 1 || toOrderNo < 1 || fromOrderNo > playlistVideoList.size() || toOrderNo > playlistVideoList.size();
    }

    private void changeOrderTransactional(PlaylistVideo fromPlaylistVideo, List<PlaylistVideo> playlistVideoList, int fromOrderNo, int toOrderNo) {
        int direction = (fromOrderNo > toOrderNo) ? 1 : -1;
        playlistVideoList.stream()
                .filter(pv -> shouldTheVideoBeMoved(fromOrderNo, toOrderNo, pv, direction))
                .forEach(pv -> {
                    pv.setOrderNo(pv.getOrderNo() + direction);
                    playlistVideoRepository.save(pv);
                });
        fromPlaylistVideo.setOrderNo(toOrderNo);
        playlistVideoRepository.save(fromPlaylistVideo);
    }

    private static boolean shouldTheVideoBeMoved(int fromOrderNo, int toOrderNo, PlaylistVideo pv, int direction) {
        return (direction == 1 && pv.getOrderNo() >= toOrderNo && pv.getOrderNo() < fromOrderNo) ||
                (direction == -1 && pv.getOrderNo() > fromOrderNo && pv.getOrderNo() <= toOrderNo);
    }

    @Override
    public List<Video> getSortedVideosForPlaylist(Long playlistId) {
        playlistService.getPlaylistById(playlistId);
        return playlistVideoRepository.findByPlaylistIdOrderByOrderNo(playlistId).stream()
                .map(PlaylistVideo::getVideo).collect(Collectors.toList());
    }
}
