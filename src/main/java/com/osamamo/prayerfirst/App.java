package com.osamamo.prayerfirst;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

import com.osamamo.prayerfirst.controller.PrayerTimesController;
public class App extends Application {

  public static Stage stage;
  private static final Logger LOG = Logger.getLogger(App.class.getName());

  @Override
  public void start(Stage stage) {

    try {
      Parent root = FXMLLoader.load(getClass().getResource("/views/PrayerFirst.fxml"));
      stage.setScene(new Scene(root));
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }

    // Make icon to the app
    stage.getIcons().add(new Image("/images/icon-app-64px.png"));
    stage.initStyle(StageStyle.UNDECORATED);
    App.stage = stage;

    // instructs the javafx system not to exit implicitly when the last application window is shut.
    Platform.setImplicitExit(false);

    // sets up the tray icon (using awt code run on the swing thread).
    javax.swing.SwingUtilities.invokeLater(this::addAppToTray);

    showStage();
  }

  // Sets up a system tray icon for the application.
  private void addAppToTray() {
    try {
      // ensure awt toolkit is initialized.
      java.awt.Toolkit.getDefaultToolkit();

      // app requires system tray support, just exit if there is no support.
      if (!java.awt.SystemTray.isSupported()) {
        System.out.println("No system tray support, application exiting.");
        Platform.exit();
      }

      // set up a system tray icon.
      java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();

      java.awt.Image image = ImageIO.read(App.class.getResourceAsStream("/images/icon-app-16px.png"));

      java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(image);
      trayIcon.setToolTip("PrayerFirst");

      // if the user clicks on the tray icon, show the main app stage.
      trayIcon.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          if (e.getClickCount() == 1) {
            Platform.runLater(App.this::showStage);
          }
        }
      });

      // if the user selects the default menu item (which includes the app name),
      // show the main app stage.
      java.awt.MenuItem openItem = new java.awt.MenuItem("Open");
      openItem.addActionListener(event -> Platform.runLater(this::showStage));

      // hide the main app stage.
      java.awt.MenuItem hideItem = new java.awt.MenuItem("Hide");
      hideItem.addActionListener(event -> Platform.runLater(this::hideStage));

      // the convention for tray icons seems to be to set the default icon for opening
      // the application stage in a bold font.
      java.awt.Font defaultFont = java.awt.Font.decode(null);
      java.awt.Font boldFont = defaultFont.deriveFont(java.awt.Font.BOLD);
      openItem.setFont(boldFont);

      // to really exit the application, the user must go to the system tray icon
      // and select the exit option, this will shutdown JavaFX and remove the
      // tray icon (removing the tray icon will also shut down AWT).
      java.awt.MenuItem exitItem = new java.awt.MenuItem("Exit");
      exitItem.addActionListener(event -> {
        Platform.exit();
        tray.remove(trayIcon);
      });

      // setup the popup menu for the application.
      final java.awt.PopupMenu popup = new java.awt.PopupMenu();
      popup.add(openItem);
      popup.add(hideItem);
      popup.addSeparator();
      popup.add(exitItem);
      trayIcon.setPopupMenu(popup);

      // add the application tray icon to the system tray.
      tray.add(trayIcon);
    } catch (java.awt.AWTException | IOException e) {
      LOG.warning("Unable to init system tray");
      e.printStackTrace();
    }
  }

  private void showStage() {
    Optional.ofNullable(stage).ifPresent(stg -> {
      stage.show();
      stage.toFront();
    });
  }

  private void hideStage() {
    Optional.ofNullable(stage).ifPresent(stg -> {
      stage.hide();
    });
  }

  public static void main(String[] args) {
    launch(args);
  }
}
