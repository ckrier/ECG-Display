/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.serial;

/**
 *
 * @author connor
 */
public interface SerialPortListener {
    public void startListening(String port, int baud);
    public void stopListening();
    public boolean saveData(String fileName);
    public void closePort();
    public String[] getSerialPorts();
}
