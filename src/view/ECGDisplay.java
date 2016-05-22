/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.Controller;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
/**
 *
 * @author connor
 */
public class ECGDisplay implements Initializable {
    public static final String GRAPH_DISPLAY = "/view/ECGGraphs.fxml";
    
    private ArrayList<LineChart> charts;
    private Controller controller;
    private int x = 0;

    // graphs
    @FXML private LineChart lead1;
    @FXML private LineChart lead2;
    @FXML private LineChart lead3;
    @FXML private LineChart lead_avl;
    @FXML private LineChart lead_avf;
    @FXML private LineChart lead_avr;
    
    // buttons
    @FXML private Button start_button;
    @FXML private Button stop_button;
    @FXML private Button save_button;
    
    // inputs
    @FXML private ComboBox serial_name;
    @FXML private TextField baud;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        final String pre = "CHART_COLOR_1: ";
        charts = new ArrayList<>();
        int i = 0;
        XYChart.Series lead1Data = new XYChart.Series();
        lead1.getData().add(lead1Data);
        lead1.setStyle(pre + "cornflowerblue;");
        charts.add(lead1);
        
        XYChart.Series lead2Data = new XYChart.Series();
        lead2.getData().add(lead2Data);
        lead2.setStyle(pre + "darkslateblue;");
        charts.add(lead2);
        
        XYChart.Series lead3Data = new XYChart.Series();
        lead3.getData().add(lead3Data);
        lead3.setStyle(pre + "lightseagreen;");
        charts.add(lead3);
        
        XYChart.Series leadAvlData = new XYChart.Series();
        lead_avl.getData().add(leadAvlData);
        lead_avl.setStyle(pre + "darkseagreen;");
        charts.add(lead_avl);
        
        XYChart.Series leadAvfData = new XYChart.Series();
        lead_avf.getData().add(leadAvfData);
        lead_avf.setStyle(pre + "lightgreen;");
        charts.add(lead_avf);
        
        XYChart.Series leadAvrData = new XYChart.Series();
        lead_avr.getData().add(leadAvrData);
        lead_avr.setStyle(pre + "green;");
        charts.add(lead_avr);
        
        setupBaudTextBox();
        setupStartButton();
        setupStopButton();
        setupSaveButton();
    }
    
    public void udpateGraphs(ArrayList<Integer> yCords) {
        for (int i = 0; i < yCords.size(); ++i) {
            addPoint(charts.get(i), x, yCords.get(i));
        }
        
        
        if (++x >= 2000) {
            x = 0;
        }
    }
    
    private void addPoint(LineChart chart, int x, double y) {
        XYChart.Series series = (XYChart.Series) chart.getData().get(0);
        XYChart.Data point = new XYChart.Data(x, y);
        
        if (series.getData().size() <= x) {
            series.getData().add(point);
        } else {
            series.getData().set(x, point);
        }
    }
    
    private void clearData(LineChart chart) {
        XYChart.Series series = (XYChart.Series) chart.getData().get(0);
        series.getData().clear();
    }
    
    private void setupStartButton() {
        start_button.setOnMouseClicked((e) -> {
            controller.startSerial(
                serial_name.getSelectionModel().getSelectedItem().toString(),
                baud.getText());
            
            start_button.setDisable(true);
            stop_button.setDisable(false);
            save_button.setDisable(true);
        });
    }
    
    private void setupStopButton() {
        stop_button.setOnMouseClicked((e) -> {
            controller.stopSerial();
            start_button.setDisable(false);
            stop_button.setDisable(true);
            save_button.setDisable(false);
        });
    }
    
    private void setupSaveButton() {
        save_button.setOnMouseClicked((e) -> {
            controller.saveECGData();
        });
    }
    
    public void setLogicController(Controller controller) {
        this.controller = controller;
    }
    
    public void setupSerialNameBox(String[] names) {
        serial_name.setItems(FXCollections.observableArrayList(names));
        
        serial_name.valueProperty().addListener((obs, oldVal, newVal) -> {
            changeStartButtonState();
        });
    }
    
    public void setupBaudTextBox() {
        baud.textProperty().addListener((obs, oldVal, newVal) -> {
            // TODO replace with own class
             if (!newVal.matches("\\d*")) {
                baud.setText(newVal.replaceAll("[^\\d]", ""));
            }
             
            changeStartButtonState();
        });
    }
    
    private void changeStartButtonState() {
        if (baud.getText().length() > 2 && !serial_name.getSelectionModel().isEmpty()) {
            start_button.setDisable(false);
        } else {
            start_button.setDisable(true);
        }
    }
}
