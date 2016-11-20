/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package digitalwallet;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;

/**
 *
 * @author Mahesh Imaduwa
 */
public class Dataminer {
    
    private String csvfile;
    private String[] custIDone;
    private String[] custIDtwo;
    private String[] custID;
    
    
  
    public Dataminer(String file){
        
        this.csvfile=file;
        
    }
    
    private void createCustList(){
        
        ArrayList<String> iddata,custIdONE,custIdTWO;
        iddata = new ArrayList<>();
        custIdONE = new ArrayList<>();
        custIdTWO = new ArrayList<>();
        
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        int i=0;
        try { 
            br = new BufferedReader(new FileReader(this.csvfile));
         
            String headerLine = br.readLine();
            
            while ((line = br.readLine()) != null) 
            {               
                String[] rowdata = line.split(cvsSplitBy);
                i++;
                iddata.add(rowdata[1].trim());
                iddata.add(rowdata[2].trim());
                custIdONE.add(rowdata[1].trim());
                custIdTWO.add(rowdata[2].trim());
                
            }
              
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
     LinkedHashSet<String> uniqueData = new LinkedHashSet<String>();
     uniqueData .addAll(iddata);
     iddata.clear();
     iddata.addAll(uniqueData );
    
     
     this.custID = new String[iddata.size()];
     this.custID=iddata.toArray(this.custID);
     
     this.custIDone = new String[custIdONE.size()];
     this.custIDone = custIdONE.toArray(custIDone);
     
     this.custIDtwo = new String[custIdTWO.size()];
     this.custIDtwo = custIdTWO.toArray(custIDtwo);
     
}
    public int getMaxCusID(){
        
        int[] c= new int[this.custID.length];  
        for(int i=0;i < c.length;i++){
            c[i]=Integer.parseInt(this.custID[i]);
        }
        Arrays.sort(c);
        return c[c.length-1];
    }
    
     
    public String[] getCustomerList(){
        
        createCustList();
        return this.custID;
    }
    
   
    public String[] getDataField(int colIndex){
        
        String [] col = null;
        switch (colIndex){
            case 1: col=this.custIDone;
                break;
            case 2: col=this.custIDtwo;
                break;
            default: col=col;
                break;     
        }
        return col;
    }
                
    
    
   
   
    
}

