package com.osamamo.prayerfirst.controller;

import com.jfoenix.controls.*;
import com.osamamo.prayerfirst.App;
import com.osamamo.prayerfirst.data.model.PrayerTimes;
import com.osamamo.prayerfirst.service.AdhanService;
import com.osamamo.prayerfirst.util.FileUtils;
import com.osamamo.prayerfirst.data.network.WebService;

import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import javax.swing.*;

import org.json.JSONArray;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import org.json.JSONObject;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.net.URL;
import java.text.DateFormat;
import java.time.chrono.HijrahChronology;
import java.time.chrono.HijrahDate;
import java.time.LocalDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.awt.TrayIcon.MessageType;
import java.util.*;
import java.util.List;
import java.util.Timer;


public class PrayerTimesController implements Initializable {

  @FXML
  private StackPane menuBar;
  @FXML
  private JFXHamburger hamburgerMenu;
  @FXML
  private JFXComboBox<String> comboCities, comboCountry;

  @FXML
  private JFXSlider sliderAzkarTime;

  @FXML
  private Label lblDate, lblDateHijri;
  @FXML
  private Label lblTimeH, lblTimeSeparator, lblTimeM, lblTimeSeparator2, lblTimeS;
  @FXML
  private Label lblPraeryName, lblPrayerFajr, lblPrayerSunrise, lblPrayerDhuhr, lblPrayerAsr, lblPrayerMaghrib, lblPrayerIsha;

  @FXML
  private  TextField tfFajrH, tfFajrM, tfSunriseH,tfSunriseM, tfDhuhrH,tfDhuhrM, tfAsrH, tfAsrM, tfMaghribH, tfMaghribM, tfIshaH, tfIshaM ;
  /* Adhan part */
  @FXML
  private StackPane alarmView;
  @FXML
  private Text txtAlarmPrayer, txtAlarmCity;

  /* Settings part */
  @FXML
  private VBox settingsView;
  @FXML
  private JFXToggleButton tglRunAdhan, tglRunAzkar, tglEnableManualAzan ;
  @FXML
  private JFXComboBox<String> comboAdhan;

  @FXML
  private Button iconPlayAdhan;

  private HamburgerBasicCloseTransition hamburgerTransition;



  // Used to make stage draggable
  private double xOffset = 0;
  private double yOffset = 0;

  private final WebService webService;
  private final AdhanService adhanService;
  public static final String jsonString;

  public String remain_adhan_time = "";

  static {
      // Use the class loader to load the JSON file as a resource
      ClassLoader classLoader = PrayerTimesController.class.getClassLoader();
      InputStream inputStream = classLoader.getResourceAsStream("config/country_city.json");

      // Read the JSON content from the input stream
      try {
          jsonString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
          // Close the input stream when done
         inputStream.close();
      } catch (IOException e) {
          throw new RuntimeException(e);
      }


  }

  public static final String [] AZKAR = {"أسْتَغْفِرُ اللهَ وَأتُوبُ إلَيْهِ","سُبْحـانَ اللهِ وَبِحَمْـدِهِ عَدَدَ خَلْـقِه ، وَرِضـا نَفْسِـه ، وَزِنَـةَ عَـرْشِـه ، وَمِـدادَ كَلِمـاتِـه","اللّهُـمَّ بِكَ أَصْـبَحْنا وَبِكَ أَمْسَـينا ، وَبِكَ نَحْـيا وَبِكَ نَمُـوتُ وَإِلَـيْكَ النُّـشُور","لَا إلَه إلّا اللهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءِ قَدِيرِ","أَعُوذُ بِكَلِمَاتِ اللهِ التَّامَّةِ مِنْ شَرِّ مَا خَلَقَ"," بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ","اللهم صل وسلم وبارك على سيدنا محمد ﷺ "," حَسْبِيَ اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ عَلَيْهِ تَوَكَّلْتُ وَهُوَ رَبُّ الْعَرْشِ الْعَظِيمِ","لا إلهَ إلاّ اللّهُ وَحْـدَهُ لا شَـريكَ له، لهُ المُلـكُ ولهُ الحَمـد، وهوَ على كلّ شيءٍ قدير"," سُـبْحانَ اللهِ","الحمْـدُ لله ","لا إلهَ إلاّ اللهُ واللهُ أكبَر","َلا حَولَ وَلا قوّة إلاّ باللّهِ العليّ العظيم","رَبِّ اغْفرْ لي","اللهم صل وسلم وبارك على سيدنا محمد ﷺ "};

  public static final JSONObject jsonObject = new JSONObject(jsonString);
  public static final String[] Country = jsonObject.keySet().toArray(new String[0]);

  public PrayerTimesController() {
    this.webService = new WebService();
    this.adhanService = new AdhanService();
  }


  /* ========================= INITIALIZE ========================== */

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initMenu();
    initDateAndClock();
    initComboCountry();
    initComboCities();
    loadSettingsLog(); // load saved app stat
    initAdhan();

    /* Make stage draggable */
    menuBar.setOnMousePressed(event -> {
      xOffset = event.getSceneX();
      yOffset = event.getSceneY();
    });
    menuBar.setOnMouseDragged(event -> {
      App.stage.setX(event.getScreenX() - xOffset);
      App.stage.setY(event.getScreenY() - yOffset);
      App.stage.setOpacity(0.7f);
    });
    menuBar.setOnDragDone(e -> App.stage.setOpacity(1.0f));
    menuBar.setOnMouseReleased(e -> App.stage.setOpacity(1.0f));
  }


  /* Data And Clock */
  private void initDateAndClock() {

    /* Init clock (date & time) of prayer times */
    KeyFrame clockKeyFrame = new KeyFrame(Duration.ZERO, e -> {

      Date currentDate = new Date();

      /*                                Date Part                      */
      // Display the Georgy date
      DateFormat dayFormat = new SimpleDateFormat("dd.MMMM.yyyy");
      lblDate.setText(dayFormat.format(currentDate));

      // Display the Hijri date
      Calendar cl = Calendar.getInstance();
      cl.setTime(currentDate);
      HijrahDate islamyDate = HijrahChronology.INSTANCE.date(LocalDate.of(cl.get(Calendar.YEAR), cl.get(Calendar.MONTH) + 1, cl.get(Calendar.DATE)));
      lblDateHijri.setText(islamyDate.toString().split(" ")[2]);


      /*                                Time Part                      */
      // Check if time of Adhan come
      DateFormat timeFormat = new SimpleDateFormat("HH:mm");

      Date cuttentDate = null;
      try { cuttentDate = timeFormat.parse(timeFormat.format(currentDate)); } catch (ParseException ex) {}
      Date nextAdhan = nextAdhan(cuttentDate);

      // Display remainder Time
      int hour = nextAdhan.getHours() - currentDate.getHours();

      int min  = nextAdhan.getMinutes() - currentDate.getMinutes();
      String[] time = {""+(((hour*60)+min)/60),""+((((hour*60)+min)%60)),""+(59 - currentDate.getSeconds())};
      lblTimeS.setText((time[2].length()==2)?time[2]:"0"+time[2]);
      lblTimeM.setText((time[1].length()==2)?time[1]:"0"+time[1]);
      lblTimeH.setText((time[0].length()==2)?time[0]:"0"+time[0]);



      // Is it new day? => change the prayer times
      if (timeFormat.format(currentDate).equals("00:00:00") && comboCities.getSelectionModel() != null) {
        setPrayerTimes(comboCountry.getSelectionModel().getSelectedItem(),
                       comboCities.getSelectionModel().getSelectedItem());
      }

      // Play Azkar
      if ( currentDate.getMinutes() % (60 / sliderAzkarTime.getValue()) == 0 && currentDate.getSeconds() == 0)
        try {
          Random random = new Random();
          displayNotification("PrayerFirst", "أذكر الله", AZKAR[random.nextInt(AZKAR.length)]);
        } catch (AWTException ex) {
          System.out.println(ex);
        }

      // Play Reminder of Prayer
      if (tglRunAzkar.isSelected() && time[0].equals("0") && time[1].equals("10") && time[2].equals("1")){
        System.out.println("Adhan Git Close");
        adhanService.setAdhan("adan999.mp3");
        adhanService.play();
        initAdhan();
      }
    });




    Timeline clock = new Timeline(clockKeyFrame, new KeyFrame(Duration.seconds(1)));
    clock.setCycleCount(Animation.INDEFINITE);
    clock.play();

    // Show/Hide animation for time separator
    KeyFrame clockSeparatorKeyFrame = new KeyFrame(Duration.ZERO, e -> {
      if (lblTimeSeparator.isVisible()) {
        lblTimeSeparator.setVisible(false);
        lblTimeSeparator2.setVisible(false);
      } else {
        lblTimeSeparator.setVisible(true);
        lblTimeSeparator2.setVisible(true);
      }
    });

    Timeline clockSeparator = new Timeline(clockSeparatorKeyFrame, new KeyFrame(Duration.millis(500)));
    clockSeparator.setCycleCount(Animation.INDEFINITE);
    clockSeparator.play();

  }

  private void initComboCities() {
    // Get Saved Country
    ResourceBundle bundle = ResourceBundle.getBundle("config.settings");
    int country_num = Integer.parseInt(toUTF(bundle.getString("country")));

    // Get cities array
    JSONArray jsonArray = jsonObject.getJSONArray(Country[country_num]);
    String[] normalArray = new String[jsonArray.length()];
    for (int i = 0; i < jsonArray.length(); i++) {
      normalArray[i] = jsonArray.getString(i);
    }

    comboCities.getItems().setAll(normalArray);
    comboCities.setOnAction(e -> setPrayerTimes(comboCountry.getSelectionModel().getSelectedItem(),
                                                comboCities.getSelectionModel().getSelectedItem()));
  }

  private void initComboCountry() {
    Arrays.sort(Country);
    comboCountry.getItems().setAll(Country);
  }

  private void initAdhan() {
    adhanService.setAdhan(comboAdhan.getSelectionModel().getSelectedItem());
  }

  private void setPrayerTimes(String country, String city ) {
    // Get prayer times from web service
    if (tglEnableManualAzan.isSelected() ) {

      // Get Values from settings file
      ResourceBundle bundle = ResourceBundle.getBundle("config.settings");
      // load the Azan Time Values
      Label lbl_p [] = {lblPrayerFajr, lblPrayerSunrise, lblPrayerDhuhr, lblPrayerAsr, lblPrayerMaghrib, lblPrayerIsha};
      String prayer_name [] = {"Fajr","Sunrise","Dhuhr","Asr","Maghrib","Isha"};
      for (int i = 0 ; i<6; i++) {
        lbl_p[i].setText(toUTF((bundle.getString(prayer_name[i]+"H"))) +":"+ toUTF((bundle.getString(prayer_name[i]+"M"))) );
      }
    }else{
      PrayerTimes prayerTimes = webService.getPrayerTimes(country, city);
      if (prayerTimes != null) {
        lblPrayerFajr.setText(prayerTimes.getFajr());
        lblPrayerSunrise.setText(prayerTimes.getSunrise());
        lblPrayerDhuhr.setText(prayerTimes.getDhuhr());
        lblPrayerAsr.setText(prayerTimes.getAsr());
        lblPrayerMaghrib.setText(prayerTimes.getMaghrib());
        lblPrayerIsha.setText(prayerTimes.getIsha());
      }else{
        tglEnableManualAzan.setSelected(true);
        setPrayerTimes(country,city );
      }
    }
  }

  @FXML
  private void onClose() {
    saveSettingsLog();
    // Platform.exit();
    App.stage.hide();
  }

  @FXML
  private void onHide() {
    App.stage.setIconified(true);
  }







  /* ================================ ALARM (Adhan) part ========================= */


  public Date nextAdhan(Date timeNow) {


    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

    // map Adhan name by it's label
    LinkedHashMap<String, String> prayerTimes = new LinkedHashMap<>();
    prayerTimes.put("الفجر", lblPrayerFajr.getText());
    prayerTimes.put("الظهر", lblPrayerDhuhr.getText());
    prayerTimes.put("العصر", lblPrayerAsr.getText());
    prayerTimes.put("المغرب",lblPrayerMaghrib.getText());
    prayerTimes.put("العشاء", lblPrayerIsha.getText());

    try {
      for (Map.Entry<String, String> entry : prayerTimes.entrySet())  {
        Date prayerTime = dateFormat.parse(entry.getValue());

        if (prayerTime.after(timeNow)) {
          lblPraeryName.setText(entry.getKey()); // update next prayer
          return prayerTime;
        } else if (prayerTime.equals(timeNow)){
          checkPlayAdhan(entry.getKey());       // play adhan when next prayer come
        }
      }
      lblPraeryName.setText("قيام الليل");
      // add 24 hours cause fajer is in next day
      return dateFormat.parse("23:59");

    }catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  private void checkPlayAdhan(String prayerName) { // Check if Azan not play then play

    if (adhanService.isCanPlay() && !adhanService.isPlaying() && tglRunAdhan.isSelected()) {
      adhanService.setCanPlay(false);
      txtAlarmCity.setText(comboCities.getSelectionModel().getSelectedItem());
      txtAlarmPrayer.setText(prayerName);
      setShowView(true, alarmView);
      adhanService.play();

      int wait_time  = (prayerName == "المغرب") ? 6 : 12;

      /*                   Get PC go to Sleep After 5 min                */
      Timer timer = new Timer();
      // Schedule the code to execute after 5 minutes (300,000 milliseconds)
      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          // Display a warning dialog with a message
          JOptionPane.showMessageDialog(null, "سوف يتم اغلاق الجهاز بعد " + wait_time/2 + " دقائق", "PrayerFirst", JOptionPane.WARNING_MESSAGE);

        }
      }, (wait_time/2) * 60 * 1000); // 2 minutes in milliseconds

      // Task 2: Make the PC sleep after 5 minutes
      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          try {
            Runtime.getRuntime().exec("rundll32.exe powrprof.dll,SetSuspendState 0,1,0");
            System.out.println("PC is sleeping.");
          } catch (IOException ioe) {
            ioe.printStackTrace();
          }
        }
      }, wait_time * 60 * 1000); // 5 minutes in millisecondss





    }
  }


  @FXML
  public void onCloseAlarm() {
    setShowView(false, alarmView);
    adhanService.pause();
    adhanService.launchPeriodStop();
  }

















  /* ======================== SETTINGS =========================== */

  private void initMenu() { // Init settings
    /* Init show/hide menu */
    hamburgerTransition = new HamburgerBasicCloseTransition(hamburgerMenu);
    hamburgerTransition.setRate(-1);
    hamburgerMenu.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
      if (hamburgerTransition.getRate() == -1) {
        hamburgerTransition.setRate(1);
        setShowView(true, settingsView);
      } else {
        hamburgerTransition.setRate(-1);
        setShowView(false, settingsView);
        adhanService.pause();
      }
      hamburgerTransition.play();
    });

    /* Init Adan combobox */

    List<String> adhanFilesName = Arrays.asList("adan1.mp3", "adan2.mp3", "adan3.mp3", "adan4.mp3", "adan5.mp3");
    comboAdhan.getItems().addAll(Optional.ofNullable(adhanFilesName).get());
    comboAdhan.setOnAction(e -> {
      adhanService.pause();
      initAdhan();
      if (iconPlayAdhan.getText().equals("||"))
        adhanService.play();
    });


  }
  @FXML
  private void onIconPlayAdhanClicked()
  {
    if (iconPlayAdhan.getText().equals(">")){
      adhanService.play();
      iconPlayAdhan.setText("||");
  }else {
      adhanService.pause();
      iconPlayAdhan.setText(">");
    }
    {

  }
  }
  @FXML
  private void onCloseMenu() {
    setShowView(false, settingsView);
    hamburgerTransition.setRate(-1);
    hamburgerTransition.play();

    adhanService.pause();
  }

  private void setShowView(boolean show, Parent view) {
    ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(500), view);
    if (show) {
      view.setVisible(true);
      scaleTransition.setFromX(0);
      scaleTransition.setFromY(0);
      scaleTransition.setToX(1);
      scaleTransition.setToY(1);
    } else {
      scaleTransition.setFromX(1);
      scaleTransition.setFromY(1);
      scaleTransition.setToX(0);
      scaleTransition.setToY(0);
      scaleTransition.setOnFinished(e -> view.setVisible(false));
    }
    scaleTransition.play();
  }

  private void loadSettingsLog() {
    // Get Values from settings file
    ResourceBundle bundle = ResourceBundle.getBundle("config.settings");

    // load country value
    int contry = Integer.parseInt(toUTF(bundle.getString("country")));
    comboCountry.getSelectionModel().select(contry);


    // load city value
    int city = Integer.parseInt(toUTF(bundle.getString("city")));
    if (jsonObject.getJSONArray(Country[contry]).length() < city) {
      city = 0;
    } // if city not in conty return 0

    tglEnableManualAzan.setSelected(Boolean.valueOf(toUTF((bundle.getString("enableManualAzan")))));
    comboCities.getSelectionModel().select(city);
    setPrayerTimes(comboCountry.getSelectionModel().getSelectedItem(),
      comboCities.getSelectionModel().getSelectedItem());

    // load adhan values
    tglRunAdhan.setSelected(Boolean.valueOf(toUTF((bundle.getString("enableAdhan")))));
    comboAdhan.getSelectionModel().select(Integer.parseInt(toUTF((bundle.getString("adhan")))));

    // load Azkar value
    tglRunAzkar.setSelected(Boolean.valueOf(toUTF((bundle.getString("enableAzkar")))));
    sliderAzkarTime.setValue(Double.parseDouble(toUTF((bundle.getString("azkarTimes")))));

    // load the Azan Time Values
    TextField tf [] = {tfFajrH, tfSunriseH, tfDhuhrH, tfAsrH, tfMaghribH, tfIshaH,tfFajrM, tfSunriseM, tfDhuhrM, tfAsrM, tfMaghribM, tfIshaM};
    String prayer_name [] = {"FajrH","SunriseH","DhuhrH","AsrH","MaghribH","IshaH","FajrM","SunriseM","DhuhrM","AsrM","MaghribM","IshaM"};
    for (int i = 0 ; i<12; i++) {
      tf[i].setText(toUTF((bundle.getString(prayer_name[i]))));
    }



  }

  private void saveSettingsLog() {
    Properties prop = new Properties();
    OutputStream output = null;
    try {
      URL resourceURL = getClass().getClassLoader().getResource("config");

      output = new FileOutputStream(resourceURL.toString().split("e:")[1] + "/settings.properties");

      // Set the properties value
      prop.setProperty("country", String.valueOf(comboCountry.getSelectionModel().getSelectedIndex()));
      prop.setProperty("city", String.valueOf(comboCities.getSelectionModel().getSelectedIndex()));
      prop.setProperty("enableAdhan", String.valueOf(tglRunAdhan.isSelected()));
      prop.setProperty("adhan", String.valueOf(comboAdhan.getSelectionModel().getSelectedIndex()));
      prop.setProperty("enableAzkar", String.valueOf(tglRunAzkar.isSelected()));
      prop.setProperty("azkarTimes",String.valueOf(sliderAzkarTime.getValue()));

      // Set the Azan Time Values
      TextField tfh [] = {tfFajrH, tfSunriseH, tfDhuhrH, tfAsrH, tfMaghribH, tfIshaH};
      TextField tfm [] = {tfFajrM, tfSunriseM, tfDhuhrM, tfAsrM, tfMaghribM, tfIshaM};
      String prayer_name [] = {"Fajr","Sunrise","Dhuhr","Asr","Maghrib","Isha"};


      for (int i = 0; i < 6; i++) {
        String formattedHour,formattedMinute;
        try {
          int hourValue = Integer.parseInt(tfh[i].getText());
          int minuteValue = Integer.parseInt(tfm[i].getText());

          // Ensure hour is between 0 and 23
           formattedHour = (hourValue >= 0 && hourValue <= 23)
            ? String.format("%02d", hourValue)
            : "00";

          // Ensure minute is between 0 and 59
          formattedMinute = (minuteValue >= 0 && minuteValue <= 59)
            ? String.format("%02d", minuteValue)
            : "00";
        } catch (Exception e) {
          formattedHour ="00";
          formattedMinute = "00" ;
        }

        // Set properties
        prop.setProperty(prayer_name[i] + "H", formattedHour);
        prop.setProperty(prayer_name[i] + "M", formattedMinute);
      }


      prop.setProperty("enableManualAzan", String.valueOf(tglEnableManualAzan.isSelected()));

      // Save properties to project root folder
      prop.store(output, null);
    } catch (IOException io) {
      io.printStackTrace();
    } finally {
      if (output != null) {
        try {
          output.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }



  /*============================ SYSTEM ============================*/
  private String toUTF(String val) {
    try {
      return new String(val.getBytes("ISO-8859-1"), "UTF-8");
    } catch (Exception e) {
      throw new IllegalArgumentException(e.getMessage());
    }
  }

  public void displayNotification(String tip, String caption, String text) throws AWTException {
    // Obtain only one instance of the SystemTray object
    SystemTray tray = SystemTray.getSystemTray();
    // Obtain tryicon
    TrayIcon trayIcon = new TrayIcon(
      Toolkit.getDefaultToolkit().createImage("src/main/resources/images/icon-app-16px.png"));

    // Set tooltip text for the tray icon (optional)
    trayIcon.setToolTip(tip);

    // Show Notification
    tray.add(trayIcon);
    trayIcon.displayMessage(caption, text, MessageType.INFO);
    tray.remove(trayIcon);
  }

}
