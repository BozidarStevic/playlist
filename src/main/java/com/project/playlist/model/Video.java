package com.project.playlist.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "video")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String url;
    private String description;
    @ManyToOne
    private User user;
    @OneToMany(mappedBy = "video", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PlaylistVideo> playlistVideos = new ArrayList<>();

    public Video(Long id, String name, String url, String description) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Video video = (Video) o;
        return Objects.equals(id, video.id) && Objects.equals(name, video.name) && Objects.equals(url, video.url) && Objects.equals(description, video.description) && Objects.equals(user.getId(), video.user.getId()) && Objects.equals(playlistVideos, video.playlistVideos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, url, description, user.getId(), playlistVideos);
    }

    @Override
    public String toString() {
        if (user != null) {
            return "Video{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", url='" + url + '\'' +
                    ", description='" + description + '\'' +
                    ", user=" + user.getId() +
                    ", playlistVideos=" + playlistVideos +
                    '}';
        } else {
            return "Video{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", url='" + url + '\'' +
                    ", description='" + description + '\'' +
                    ", playlistVideos=" + playlistVideos +
                    '}';
        }
    }
}
