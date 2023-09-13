package com.osamamo.prayerfirst.data.network;
import com.osamamo.prayerfirst.data.model.PrayerTimes;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.json.JSONObject;

import java.util.logging.Logger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


import javax.swing.*;



import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.IOException;



public class WebService {
  private static final LocalDate currentDate = LocalDate.now();
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

  private static final Logger LOG = Logger.getLogger(WebService.class.getName());

  public PrayerTimes getPrayerTimes(String country, String city) {
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      String PRAYER_TIMES_END_POINT = ("http://api.aladhan.com/v1/timingsByCity/" + currentDate.format(formatter) + "?city="+city+"&country="+country+"&method=5").replace(" ","-");
      HttpGet httpGet = new HttpGet(PRAYER_TIMES_END_POINT);
      CloseableHttpResponse response = httpClient.execute(httpGet);
      int statusCode = response.getStatusLine().getStatusCode();

      if (statusCode == 200) {
        String responseBody = EntityUtils.toString(response.getEntity());
        JSONObject jsonRoot = new JSONObject(responseBody);

        if (jsonRoot.has("data")) {
          JSONObject timings = jsonRoot.getJSONObject("data").getJSONObject("timings");
          return PrayerTimes.builder()
            .fajr(timings.getString("Fajr"))
            .sunrise(timings.getString("Sunrise"))
            .dhuhr(timings.getString("Dhuhr"))
            .asr(timings.getString("Asr"))
            .maghrib(timings.getString("Maghrib"))
            .isha(timings.getString("Isha"))
            .build();
        }
      } else {
        // Handle HTTP errors here, e.g., show an error message
        JOptionPane.showMessageDialog(null, "Failed to retrieve prayer times. HTTP Status Code: " + statusCode);
      }
    } catch (IOException e) {
      LOG.warning(e.getMessage());
      // Handle other exceptions here
    }

    // Show a confirmation dialog
    int result = JOptionPane.showConfirmDialog(null, "Failed to retrieve prayer times due to a network issue. Do you want to retry?", "Error", JOptionPane.YES_NO_OPTION);

    if (result == JOptionPane.YES_OPTION) {
      return getPrayerTimes(country, city);
    } else {
      JOptionPane.showMessageDialog(null, "Manual date entry is activated.");
      return null;
    }
  }
//import javafx.scene.control.DialogPane;
//import javafx.scene.control.Dialog;
//import javafx.scene.control.ButtonType;
//    System.out.println("No error");
//    dbConnectionError.setVisible(true);
//    Dialog<ButtonType> dialog = new Dialog<>();
//    dialog.setDialogPane(dbConnectionError);
//
//    dialog.setResultConverter(dialogButton -> {
//      dialog.showAndWait();
//      if (dialogButton == dbConnectionError.getButtonTypes().get(0) ) {
//        getPrayerTimes(country, city);
//
//      }
//        tglEnableManualAzan.setSelected(true);
//        JOptionPane.showMessageDialog(null, "تم تفعيل التاريخ اليدوي");
//      return null;
//    });
//    System.out.println("What is your name");


  private PrayerTimes getDefaultPrayerTimes() {
    String defaultTime = "--:--";
    return PrayerTimes.builder()
      .fajr(defaultTime)
      .sunrise(defaultTime)
      .dhuhr(defaultTime)
      .asr(defaultTime)
      .maghrib(defaultTime)
      .isha(defaultTime)
      .build();
  }
}
