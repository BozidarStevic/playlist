package com.project.playlist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

@SpringBootApplication
@ComponentScans({@ComponentScan("com.project.playlist.mapper"),
		@ComponentScan("com.project.playlist.controller"),
		@ComponentScan("com.project.playlist.dto"),
		@ComponentScan("com.project.playlist.repository"),
		@ComponentScan("com.project.playlist.service"),
		@ComponentScan("com.project.playlist.config"),
		@ComponentScan("com.project.playlist.model")})
public class PlaylistApplication {
	public static void main(String[] args) {
		SpringApplication.run(PlaylistApplication.class, args);
	}

}
