/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.serial;

import controller.Controller;
import java.util.ArrayList;

import javax.comm.SerialPort;
import jssc.SerialNativeInterface;
import jssc.SerialPortList;
import model.files.ECGFileWriter;
import model.files.FileWriter;

/**
 *
 * @author connor
 */
public class ECGSerialData implements SerialPortListener {
    public static final String PORT_NAME = "/dev/ttyACM0";
    public static final String PORT_OWNER = "ECGDisplay";
    public static final int PORT_CONNECT_TIMEOUT = 2000;
    public static final int BAUD_RATE = 115200;
    
    private Thread read;
    private final Controller controller;
    private SerialNativeInterface port;
    private long serialHandle;
    private final FileWriter writer;
    
    public ECGSerialData(Controller controller) {
        this.controller = controller;
        port = new SerialNativeInterface();
        writer = new ECGFileWriter();
    }
    
    @Override
    public void stopListening() {
        if (read != null) {
            try {
                read.interrupt();
                read = null;
                System.out.println("ended thread");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void closePort() {
        try {
            // wait for data to stop reading
            Thread.sleep(200);
            port.closePort(serialHandle);
            writer.close();
            System.out.println("closed resources");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void startListening(String port, int baud) {
        setupPort(port, baud);
        
        try {
            read = new Thread() {
                @Override
                public void run() {
                    try {
                        startReading();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            
            read.start();
        } catch (Exception io) {
            io.printStackTrace();
        }
    }
    
    @Override
    public boolean saveData(String absPath) {
        return writer.saveFile(absPath);
    }
    
    private void startReading() throws Exception {
        byte[] b;
        StringBuilder graphData = new StringBuilder();
        
        while (!Thread.currentThread().isInterrupted()) {
            b = port.readBytes(serialHandle, 1);
            String s = new String(b);
            if (s.equals("\n")) {
                String data = graphData.toString();
                if (isCorrectFormat(data)) {
                    controller.sendNewData(dataToArrayList(data));
                    writer.writeLine(data);
                }
                
                graphData = new StringBuilder();
            } else {
                graphData.append(s);
            }
        }
    }
    
    // checks to see that string is composed of only numbers and spaces
    // and has 6 space separated parts
    private boolean isCorrectFormat(String data) {
        int numValues = data.split(" ").length;
        return numValues == 6 && data.matches("^([0-9]+\\s)+");
    }
    
    private ArrayList<Integer> dataToArrayList(String data) {
        ArrayList<Integer> toInt = new ArrayList<>();
        String[] dataArr = data.split(" ");
        for (String str : dataArr) {
            toInt.add(Integer.parseInt(str));
        } 
        
        return toInt;
    }
    
    private void setupPort(String portName, int baudRate) { 
        serialHandle = port.openPort(portName, false);
        boolean success = port.setParams(serialHandle, baudRate, SerialPort.DATABITS_8, 
                SerialPort.STOPBITS_1, SerialPort.PARITY_NONE, false, false, 0);
        
        if (serialHandle < 0 || !success) {
            System.err.println("Error: Could not open serial port " + portName);
            System.exit(0);
        }
    }
    
    @Override
    public String[] getSerialPorts() {
        return SerialPortList.getPortNames();
    }
}
