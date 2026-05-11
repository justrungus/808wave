package com.wave808.server.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "tracks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Track {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long trackId;

    @Column(nullable = false)
    private String title;

    private String genre;
    private String album;
    private Integer bpm;

    @Column(name = "musical_key")
    private String musicalKey;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable=false)
    private User uploader;

    @Column(name = "uploaded_at")
    private java.time.LocalDateTime uploadedAt = java.time.LocalDateTime.now();
    


}
