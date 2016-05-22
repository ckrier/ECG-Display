/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.files;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 *
 * @author connor
 */
public class ECGFileWriter implements FileWriter {
    private File tempFile;
    private PrintWriter csvWriter;
    
    public ECGFileWriter() {
        try {
            tempFile = createTempFile();
            csvWriter = new PrintWriter(tempFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void writeLine(String line) {
        csvWriter.println(formatToCSV(line));
        csvWriter.flush();
    }
    
    @Override
    public boolean saveFile(String absPath) {
        boolean success = true;
        Path savePath = Paths.get(absPath);
        Path tempPath = Paths.get(tempFile.getAbsolutePath());
        
        try {
            Files.copy(tempPath, savePath, StandardCopyOption.REPLACE_EXISTING);
            tempFile.delete();
            csvWriter.close();
            
            tempFile = createTempFile();
            csvWriter = new PrintWriter(tempFile);
        } catch (IOException e) {
            success = false;
            e.printStackTrace();
        }
        
        return success;
    }
    
    @Override
    public void close() {
        csvWriter.flush();
        csvWriter.close();
    }
    
    private String formatToCSV(String old) {
        String[] values = old.split(" ");
        String csv = "";
        for (int i = 0; i < values.length; ++i) {
            csv += values[i];
            if (i != values.length - 1) {
                csv += ", ";
            }
        }
        
        return csv;
    }
    
    private File createTempFile() throws IOException {
        File temp = File.createTempFile("SerialData", "csv");
        temp.deleteOnExit();
        return temp;
    }
}
