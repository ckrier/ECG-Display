/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.serial;

import controller.ECGController;
import java.util.ArrayList;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 *
 * @author connor
 */
public class FakeSerial implements SerialPortListener {
    private static final int FRAMES = 50;
    private static final double INCR = Math.PI * 2 / 500; 
    private double num = 0;
    private final ECGController controller;
    
    public FakeSerial(ECGController controller) {
        this.controller = controller;
    }
    
    private ArrayList<Integer> getSerialData() {    
        ArrayList<Integer> vals = new ArrayList<>();
        
        vals.add((int)((Math.sin(num) + 1) * 50));
        vals.add((int)((Math.cos(num) + 1) * 50));
        vals.add((int)((Math.sin(num / 2) + 1) * 40));
        vals.add((int)((Math.sin(num) * Math.cos(num) + 1) * 50));
        vals.add((int)((Math.sin(num / 2) * Math.cos(num / 4) + 1) * 50));
        vals.add((int)((Math.cos(num / 4) + 1) * 30));

        num += INCR;
        
        if (num >= Math.PI * 2) {
            num = 0;
        }

        return vals;
    }
    
    public void startListening(String port, int baud) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(FRAMES), ev -> {
            for (int i = 0; i < Math.floor(FRAMES) / 2; ++i) {
                controller.sendNewData(getSerialData());
            }
        }));
        
        System.out.println(port);
        System.out.println(baud);
        
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
    
    public void stopListening() {
        
    }
    
    public boolean saveData(String fileName) {
        return true;
    }
    
    public void closePort() {
        
    }
    
    public String[] getSerialPorts() {
        String[] arr ={"/dev/ttyAMC0", "/dev/ttyUSB0", "/dev/ttySRP0"};
        return arr;
    }
}
