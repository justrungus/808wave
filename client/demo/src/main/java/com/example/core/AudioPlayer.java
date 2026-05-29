package com.example.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.models.TrackDTO;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class AudioPlayer {
    private static AudioPlayer instance;

    private MediaPlayer mediaPlayer;
    private List<TrackDTO> queue = new ArrayList<>();
    private List<Integer> shuffleOrder = new ArrayList<>();
    private int currentIndex = 0;

    private final ObjectProperty<TrackDTO> currentTrack = new SimpleObjectProperty<>();
    private final BooleanProperty playing = new SimpleBooleanProperty(false);
    private final BooleanProperty shuffle = new SimpleBooleanProperty(false);
    private final DoubleProperty volume = new SimpleDoubleProperty(0.7);

    private AudioPlayer() {}

    public static AudioPlayer getInstance() {
        if (instance == null) instance = new AudioPlayer();
        return instance;
    }

    public void play(TrackDTO track, List<TrackDTO> contextQueue) {
        Platform.runLater(() -> {
            if (contextQueue != null && !contextQueue.isEmpty()) {
                queue = new ArrayList<>(contextQueue);
                currentIndex = queue.indexOf(track);
                if (currentIndex == -1) { queue.add(0, track); currentIndex = 0; }
            } else {
                queue = new ArrayList<>();
                queue.add(track);
                currentIndex = 0;
            }
            if (shuffle.get()) buildShuffleOrder();
            loadAndPlay(track);
        });
    }

    public void playPause() {
        if (mediaPlayer == null) return;
        if (playing.get()) {
            mediaPlayer.pause();
            playing.set(false);
        } else {
            mediaPlayer.play();
            playing.set(true);
        }
    }

    public void next() {
        if (queue.isEmpty()) return;
        Platform.runLater(() -> {
            if (shuffle.get()) {
                int pos = shuffleOrder.indexOf(currentIndex);
                currentIndex = pos < shuffleOrder.size() - 1
                        ? shuffleOrder.get(pos + 1) : shuffleOrder.get(0);
            } else {
                currentIndex = (currentIndex + 1) % queue.size();
            }
            loadAndPlay(queue.get(currentIndex));
        });
    }

    public void previous() {
        if (queue.isEmpty()) return;
        Platform.runLater(() -> {
            if (mediaPlayer != null && mediaPlayer.getCurrentTime().toSeconds() > 3) {
                mediaPlayer.seek(Duration.ZERO);
                return;
            }
            if (shuffle.get()) {
                int pos = shuffleOrder.indexOf(currentIndex);
                currentIndex = pos > 0 ? shuffleOrder.get(pos - 1) : shuffleOrder.get(shuffleOrder.size() - 1);
            } else {
                currentIndex = (currentIndex - 1 + queue.size()) % queue.size();
            }
            loadAndPlay(queue.get(currentIndex));
        });
    }

    public void toggleShuffle() {
        boolean newVal = !shuffle.get();
        shuffle.set(newVal);
        if (newVal) buildShuffleOrder();
    }

    public void seek(double percent) {
        if (mediaPlayer == null) return;
        Duration total = mediaPlayer.getTotalDuration();
        if (total != null && !total.isUnknown())
            mediaPlayer.seek(total.multiply(percent / 100.0));
    }

    private void loadAndPlay(TrackDTO track) {
        if (mediaPlayer != null) { mediaPlayer.stop(); mediaPlayer.dispose(); mediaPlayer = null; }
        currentTrack.set(track);
        playing.set(false);

        String url = "http://localhost:8080/api/tracks/" + track.getId() + "/stream";
        try {
            Media media = new Media(url);
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(volume.get());
            mediaPlayer.setOnEndOfMedia(this::next);
            mediaPlayer.setOnReady(() -> { mediaPlayer.play(); playing.set(true); });
            mediaPlayer.setOnError(() -> System.err.println("MediaPlayer error: " + mediaPlayer.getError()));
            volume.addListener((obs, o, n) -> { if (mediaPlayer != null) mediaPlayer.setVolume(n.doubleValue()); });
        } catch (Exception e) {
            System.err.println("Error loading media: " + e.getMessage());
        }
    }

    private void buildShuffleOrder() {
        shuffleOrder = new ArrayList<>();
        for (int i = 0; i < queue.size(); i++) shuffleOrder.add(i);
        shuffleOrder.remove(Integer.valueOf(currentIndex));
        Collections.shuffle(shuffleOrder);
        shuffleOrder.add(0, currentIndex);
    }

    public ObjectProperty<TrackDTO> currentTrackProperty() { return currentTrack; }
    public BooleanProperty playingProperty() { return playing; }
    public BooleanProperty shuffleProperty() { return shuffle; }
    public DoubleProperty volumeProperty() { return volume; }
    public MediaPlayer getMediaPlayer() { return mediaPlayer; }
    public TrackDTO getCurrentTrack() { return currentTrack.get(); }
    public boolean isPlaying() { return playing.get(); }
}