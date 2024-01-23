package com.project.playlist.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "playlist")
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne
    private User user;
    @OneToMany(mappedBy = "playlist", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnore
    private List<PlaylistVideo> playlistVideos;

    public Playlist(Long id, String name, User user) {
        this.id = id;
        this.name = name;
        this.user = user;
    }
}
