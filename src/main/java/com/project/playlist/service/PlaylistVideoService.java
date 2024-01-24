package com.project.playlist.service;

import com.project.playlist.exceptions.PlaylistNotFoundException;
import com.project.playlist.exceptions.VideoNotFoundException;
import com.project.playlist.model.PlaylistVideo;
import com.project.playlist.model.Playlist;
import com.project.playlist.model.Video;
import com.project.playlist.repository.PlaylistVideoRepository;
import com.project.playlist.repository.PlaylistRepository;
import com.project.playlist.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlaylistVideoService {
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private PlaylistVideoRepository playlistVideoRepository;


    public void addVideoToPlaylist(Long playlistId, Long videoId) {
        Optional<Playlist> playlistOptional = playlistRepository.findById(playlistId);
        if (playlistOptional.isEmpty()) {
            throw new PlaylistNotFoundException(playlistId);
        }
        Playlist playlist =  playlistOptional.get();

        Optional<Video> videoOptional = videoRepository.findById(videoId);
        if (videoOptional.isEmpty()) {
            throw new VideoNotFoundException(videoId);
        }
        Video video = videoOptional.get();

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

        try {
            playlistVideoRepository.save(playlistVideo);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            throw new IllegalArgumentException();
        } catch (OptimisticLockingFailureException e) {
            System.out.println(e.getMessage());
            String msg = "OptimisticLockingFailureException";
            throw new OptimisticLockingFailureException(msg);
        }
    }

    @Transactional
    public void removeVideoFromPlaylist(Long playlistId, Long videoId) {
        PlaylistVideo playlistVideo = playlistVideoRepository.findByPlaylistIdAndVideoId(playlistId, videoId);
        if (playlistVideo != null) {
            int removedOrderNo = playlistVideo.getOrderNo();
            try {
                playlistVideoRepository.delete(playlistVideo);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                throw new IllegalArgumentException();
            } catch (OptimisticLockingFailureException e) {
                System.out.println(e.getMessage());
                String msg = "OptimisticLockingFailureException";
                throw new OptimisticLockingFailureException(msg);
            }
            List<PlaylistVideo> playlistVideoList = playlistVideoRepository.findByPlaylistId(playlistId);
            for (PlaylistVideo pv : playlistVideoList) {
                if (pv.getOrderNo() > removedOrderNo) {
                    pv.setOrderNo(pv.getOrderNo() - 1);
                    try {
                        playlistVideoRepository.save(pv);
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                        throw new IllegalArgumentException();
                    } catch (OptimisticLockingFailureException e) {
                        System.out.println(e.getMessage());
                        String msg = "OptimisticLockingFailureException";
                        throw new OptimisticLockingFailureException(msg);
                    }
                }
            }
        } else {
            String msg = "Video with id " + videoId + " is not in playlist with id " + playlistId;
            System.out.println(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    public void changeVideoOrder(Long playlistId, int fromOrderNo, int toOrderNo) {
        Optional<Playlist> playlistOptional = playlistRepository.findById(playlistId);
        if (playlistOptional.isEmpty()) {
            throw new PlaylistNotFoundException(playlistId);
        }
        Playlist playlist =  playlistOptional.get();

        List<PlaylistVideo> playlistVideoList = playlistVideoRepository.findByPlaylistId(playlistId);
        if (playlistVideoList == null) {
            String msg = "Playlist with id " + playlistId + " doesn't have any video!";
            System.out.println(msg);
            throw new IllegalArgumentException(msg);
        }

        if (fromOrderNo == toOrderNo || fromOrderNo < 1 || toOrderNo < 1 || fromOrderNo > playlistVideoList.size() || toOrderNo > playlistVideoList.size()) {
            String msg = "Invalid order numbers";
            System.out.println(msg);
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
            if (fromOrderNo > toOrderNo) {
                changeOrderTransactional1(fromPlaylistVideo, playlistVideoList, fromOrderNo, toOrderNo);
            } else {
                changeOrderTransactional2(fromPlaylistVideo, playlistVideoList, fromOrderNo, toOrderNo);
            }
        } else {
            String msg = "The playlist does not have a video at one of the provided order numbers!";
            System.out.println(msg);
            throw new IllegalArgumentException(msg);
        }
    }
    @Transactional
    private void changeOrderTransactional1(PlaylistVideo fromPlaylistVideo, List<PlaylistVideo> playlistVideoList, int fromOrderNo, int toOrderNo) {
        for (PlaylistVideo pv : playlistVideoList) {
            if (pv.getOrderNo() >= toOrderNo && pv.getOrderNo() < fromOrderNo) {
                pv.setOrderNo(pv.getOrderNo() + 1);
                try {
                    playlistVideoRepository.save(pv);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        fromPlaylistVideo.setOrderNo(toOrderNo);
        playlistVideoRepository.save(fromPlaylistVideo);
    }

    @Transactional
    private void changeOrderTransactional2(PlaylistVideo fromPlaylistVideo, List<PlaylistVideo> playlistVideoList, int fromOrderNo, int toOrderNo) {
        for (PlaylistVideo pv : playlistVideoList) {
            if (pv.getOrderNo() > fromOrderNo && pv.getOrderNo() <= toOrderNo) {
                pv.setOrderNo(pv.getOrderNo() - 1);
                try {
                    playlistVideoRepository.save(pv);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        fromPlaylistVideo.setOrderNo(toOrderNo);
        playlistVideoRepository.save(fromPlaylistVideo);
    }


    public List<Video> getSortedVideosForPlaylist(Long playlistId) {
        List<PlaylistVideo> playlistVideoList = playlistVideoRepository.findByPlaylistIdOrderByOrderNo(playlistId);
        if (playlistVideoList != null && !playlistVideoList.isEmpty()) {
            List<Video> videos = new ArrayList<>();
            for (PlaylistVideo pv : playlistVideoList) {
                videos.add(pv.getVideo());
            }
            return videos;
        }
        return null;
    }
}
