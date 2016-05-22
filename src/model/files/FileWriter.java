/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.files;

/**
 *
 * @author connor
 */
public interface FileWriter {
    public void writeLine(String line);
    public boolean saveFile(String absPath);
    public void close();
}
