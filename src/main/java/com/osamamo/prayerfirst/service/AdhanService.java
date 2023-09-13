package com.osamamo.prayerfirst.service;

import com.osamamo.prayerfirst.util.FileUtils;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.net.URL;

public class AdhanService {

  private MediaPlayer adhanPlayer;

  @Setter
  @Getter
  private boolean canPlay = true;

  public void setAdhan(String adhanName) {
    String resourcePath = "adhan/" + adhanName; // Assuming adhanName includes the file extension (e.g., "adhan.mp3")
    System.out.println(resourcePath);
    // Use the class loader to load the resource
    URL resourceURL = getClass().getClassLoader().getResource(resourcePath);
    Media media = new Media(resourceURL.toExternalForm());
    System.out.println(resourceURL.toExternalForm());
    adhanPlayer = new MediaPlayer(media);
  }

  public void play() {
    adhanPlayer.play();
  }

  public void pause() {
    adhanPlayer.pause();
    adhanPlayer.seek(adhanPlayer.getStartTime());
  }

  public boolean isPlaying() {
    return adhanPlayer.getStatus().equals(MediaPlayer.Status.PLAYING);
  }

  public void launchPeriodStop() { // If I stop Adhan don't play it again until the next prayer
    canPlay = false;
    new Thread(() -> {
      try {
        Thread.sleep(60000L); // Sleep 1 min
      } catch (InterruptedException ie) {
        ie.printStackTrace();
      }
      canPlay = true;
    }).start();
  }
}
