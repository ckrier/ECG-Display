/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.ArrayList;

/**
 *
 * @author connor
 */
public interface Controller {
    // methods used by the UI
    public void startSerial(String port, String baud);
    public void stopSerial();
    public void saveECGData();
    public String[] getComboNamesData();
    
    // methods used by the model
    public void sendNewData(ArrayList<Integer> yCords);
}
