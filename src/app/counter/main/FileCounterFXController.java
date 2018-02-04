package app.counter.main;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class FileCounterFXController extends Application implements Initializable {

	@FXML
	private TextField tfPath;
	@FXML
	private TextField tfHrsFr;
	@FXML
	private TextField tfMinFr;
	@FXML
	private TextField tfHrsTo;
	@FXML
	private TextField tfMinTo;
	@FXML
	private Label lblSum;
	@FXML
	private Button btnFileChooser;
	@FXML
	private DatePicker fileDatePickerFr;
	@FXML
	private DatePicker fileDatePickerTo;

	private static Stage primaryStage;

	private void setPrimaryStage(Stage stage) {
		FileCounterFXController.primaryStage = stage;
	}

	static public Stage getPrimaryStage() {
		return primaryStage;
	}

	@FXML
	protected void handleOnTfHrsReleasedAction(KeyEvent event) {

		TextField tf = (TextField) event.getSource();
		String t = tf.getText();
		if (t.matches("\\d+") && Integer.valueOf(t) >= 0 && Integer.valueOf(t) <= 23) {

		} else {
			tf.setText("0");
		}
	}

	@FXML
	protected void handleOnTfMinReleasedAction(KeyEvent event) {
		TextField tf = (TextField) event.getSource();
		String t = tf.getText();
		if (t.matches("\\d+") && Integer.valueOf(t) >= 0 && Integer.valueOf(t) <= 59) {

		} else {
			tf.setText("0");
		}
	}

	@FXML
	protected void handleOnMouseEnteredAction(MouseEvent event) {
		lblSum.setScaleX(2.2);
		lblSum.setScaleY(2.2);
	}

	@FXML
	protected void handleOnMouseExitedAction(MouseEvent event) {
		lblSum.setScaleX(1.0);
		lblSum.setScaleY(1.0);
	}

	@FXML
	protected void handlePathButtonAction(ActionEvent event) {
		DirectoryChooser chooser = new DirectoryChooser();
		File chosenDir = chooser.showDialog(primaryStage);
		if (chosenDir != null) {
			System.out.println(chosenDir.getAbsolutePath());
			tfPath.setText(chosenDir.getAbsolutePath());
		} else {
			System.out.print("no directory chosen");
		}

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						String path = tfPath.getText();
						File f = new File(path);
						if (f.isDirectory() && f.list().length > 0) {

							FileFilter ff = new FileFilter() {

								@Override
								public boolean accept(File file) {

									// get file modified instant
									long m = file.lastModified();
									Calendar fileDate = Calendar.getInstance();
									fileDate.setTimeInMillis(m);
									Instant istFile = fileDate.toInstant();

									// get input from instant
									Integer iptHrsFr = Integer.valueOf(tfHrsFr.getText());
									Integer iptMinFr = Integer.valueOf(tfMinFr.getText());
									LocalTime iptTimeFr = LocalTime.of(iptHrsFr, iptMinFr);
									LocalDate iptDateFr = fileDatePickerFr.getValue();
									LocalDateTime iptLdtFr = iptDateFr.atTime(iptTimeFr);
									Instant istInputFr = iptLdtFr.toInstant(ZoneOffset.ofHours(8));

									// get input to instant
									Integer iptHrsTo = Integer.valueOf(tfHrsTo.getText());
									Integer iptMinTo = Integer.valueOf(tfMinTo.getText());
									LocalTime iptTimeTo = LocalTime.of(iptHrsTo, iptMinTo);
									LocalDate iptDateTo = fileDatePickerTo.getValue();
									LocalDateTime iptLdtTo = iptDateTo.atTime(iptTimeTo);
									Instant istInputTo = iptLdtTo.toInstant(ZoneOffset.ofHours(8));

									// count files which not be hidden and older than input date&time
									boolean b = !file.isHidden() && (istFile.compareTo(istInputFr) >= 0)
											&& (istFile.compareTo(istInputTo) <= 0);
									file.exists();
									return b;
								}
							};

							int cnt = f.listFiles(ff).length;
							System.out.println(cnt);
							lblSum.setText(String.valueOf(cnt));
							if (cnt >= 180 || cnt == 60 || cnt == 120) {
								lblSum.getStyleClass().remove("normal");
								lblSum.getStyleClass().add("alert");
							} else {
								lblSum.getStyleClass().remove("alert");
								lblSum.getStyleClass().add("normal");
							}
							f.exists();
						} else {
							System.out.println("not a directory");
						}
					}

				});
			}
		}, 0, 800);
	}

	@Override
	public void start(Stage stage) throws IOException {

		setPrimaryStage(stage);
		Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("GridFxml.fxml"));
		Scene scene = new Scene(root, 250, 450);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setScene(scene);
		stage.setTitle("FilesCounterFX");
		stage.setAlwaysOnTop(true);

		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				System.exit(0);
			}
		});
		stage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		LocalDateTime ldtNow = LocalDateTime.now();
		tfHrsFr.setText(String.valueOf(ldtNow.getHour()));
		tfMinFr.setText(String.valueOf(ldtNow.getMinute()));
		tfHrsTo.setText(String.valueOf(ldtNow.getHour()));
		tfMinTo.setText(String.valueOf(ldtNow.getMinute()));

		fileDatePickerFr.setValue(LocalDate.now());
		fileDatePickerTo.setValue(LocalDate.now());

	}

}
