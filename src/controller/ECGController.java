/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.File;
import java.util.ArrayList;
import view.ECGDisplay;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.serial.ECGSerialData;
import model.serial.FakeSerial;
import model.serial.SerialPortListener;

/**
 *
 * @author connor
 */
public class ECGController extends Application implements Controller {
    private Stage stage;
    private ECGDisplay view;
    private SerialPortListener serial;
    
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        serial = new ECGSerialData(this);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass()
                .getResource(ECGDisplay.GRAPH_DISPLAY));
        
        // load the view and grab the controller
        Parent root = fxmlLoader.load();
        view = (ECGDisplay) fxmlLoader.getController();
        view.setLogicController(this);
        String[] arr = serial.getSerialPorts();
        System.out.println(arr);
        view.setupSerialNameBox(arr);

        // set the viw of the app
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    @Override
    public void stop() {
        serial.stopListening();
        serial.closePort();
        
        // hack to force the application to close (why won't the damn this close?)
        System.exit(0);
    }
    
    @Override
    public void sendNewData(ArrayList<Integer> yCords) {
        Platform.runLater(() -> {
            view.udpateGraphs(yCords);
        });
    }
    
    @Override
    public void startSerial(String port, String baud) {
        serial.startListening(port, Integer.parseInt(baud));
    }
    
    @Override
    public void stopSerial() {
        serial.stopListening();
    }
    
    @Override
    public void saveECGData() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save ECG Data as CSV");
        
        File file = chooser.showSaveDialog(stage);
        if (file != null) {
            serial.saveData(file.getAbsolutePath());
        }
    }
    
    @Override
    public String[] getComboNamesData() {
        return serial.getSerialPorts();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
