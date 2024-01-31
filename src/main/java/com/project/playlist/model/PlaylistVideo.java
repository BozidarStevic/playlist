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
        return orderNo == that.orderNo && Objects.equals(id, that.id) && Objects.equals(video, that.video) && Objects.equals(playlist, that.playlist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, video, playlist, orderNo);
    }

}
