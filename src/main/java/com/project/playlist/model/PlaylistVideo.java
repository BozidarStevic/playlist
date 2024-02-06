package com.project.playlist.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "playlist_video")
public class PlaylistVideo {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Video video;
    @ManyToOne
    private Playlist playlist;
    private int orderNo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaylistVideo that = (PlaylistVideo) o;
        return orderNo == that.orderNo && Objects.equals(id, that.id) && Objects.equals(video.getId(), that.video.getId()) && Objects.equals(playlist.getId(), that.playlist.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, video.getId(), playlist.getId(), orderNo);
    }

    @Override
    public String toString() {
        if (video != null && playlist != null) {
            return "PlaylistVideo{" +
                    "id=" + id +
                    ", video=" + video.getId() +
                    ", playlist=" + playlist.getId() +
                    ", orderNo=" + orderNo +
                    '}';
        } else if (video == null && playlist != null) {
            return "PlaylistVideo{" +
                    "id=" + id +
                    ", playlist=" + playlist.getId() +
                    ", orderNo=" + orderNo +
                    '}';
        } else if (video != null) {
            return "PlaylistVideo{" +
                    "id=" + id +
                    ", video=" + video.getId() +
                    ", orderNo=" + orderNo +
                    '}';
        } else {
            return "PlaylistVideo{" +
                    "id=" + id +
                    ", orderNo=" + orderNo +
                    '}';

        }

    }
}
