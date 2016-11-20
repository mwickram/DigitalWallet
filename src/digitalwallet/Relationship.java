/*
 * Mahesh Imaduwa
 */
package digitalwallet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;


public class Relationship {
    
   private String batchcsvFile,streamcsvFile,outputfileLocation;
   private String[] custIdONE,custIdTWO;
   private String[] pastcustomers,pastcustIdONE,pastcustIdTWO;

  
    
    public Relationship(String batchFile, String streamFile,String location){
        
        this.batchcsvFile=batchFile;
        this.streamcsvFile=streamFile;
        this.outputfileLocation = location;
        
    }
    
    
    private String[] getPastCustomers(){
        
        Dataminer dmPast=new Dataminer(this.batchcsvFile);
        this.pastcustomers= dmPast.getCustomerList();
        this.pastcustIdONE=dmPast.getDataField(1);
        this.pastcustIdTWO=dmPast.getDataField(2);
       
        
        return this.pastcustomers;
        
    }
    
    
    private String[] getCurrentCustomers(){
        
        Dataminer dmCurr=new Dataminer(this.streamcsvFile);
        String[] currentCustomers=dmCurr.getCustomerList();
        this.custIdONE=dmCurr.getDataField(1);//stream
        this.custIdTWO=dmCurr.getDataField(2);//stream
        
        return currentCustomers;
        
    }
    
    
    private ArrayList findCustomerStatus(int colIndex){
        
        String[] PastCustomer,CurrCustomer;
        ArrayList<Boolean> trustStatus= new ArrayList<>();
        CurrCustomer=getCurrentCustomers();
        PastCustomer=getPastCustomers();
        String[] col=new String[this.custIdONE.length];
        
        if(colIndex==1)
            col=this.custIdONE;//stream
        if(colIndex==2)
            col=this.custIdTWO;//stream
        
        for(int i=0;i < col.length;i++){   
            boolean pastCust=false;
            for(int j=0; j < PastCustomer.length;j++){
                if(col[i].equals(PastCustomer[j])){
                    trustStatus.add(true);
                    pastCust=true;
                }    
            } 
            if(!pastCust){
                trustStatus.add(false);
            }
        }
        
        return trustStatus;
    }
    
    private void CreateStatusList(){
        
        ArrayList idone=findCustomerStatus(1);//stream
        ArrayList idtwo=findCustomerStatus(2);//stream
        ArrayList output1, output2, output3;
        output1=new ArrayList<>();
        output2=new ArrayList<>();
        output3=new ArrayList<>();
        
        Map<String,Integer> cusNet = new TreeMap<>();
        
        for(int col = 0; col < this.pastcustIdONE.length;col++){
           //col++;
           String ky_1to2=this.pastcustIdONE[col]+","+this.pastcustIdTWO[col];
           String ky_2to1=this.pastcustIdTWO[col]+","+this.pastcustIdONE[col];
          
           cusNet.putIfAbsent(ky_1to2,1);
           cusNet.putIfAbsent(ky_2to1,1);
           
        }
       
        //System.out.println(cusNet.keySet());
        for(int i=0;i < idone.size();i++){
            //new transactions
            if(idone.get(i).equals(false) || idtwo.get(i).equals(false)){
                output1.add("Unverified");
                output2.add("Unverified");
                output3.add("Unverified");
                System.out.println(this.custIdONE[i]+" unverified "+this.custIdTWO[i]);
            }else{//1-else
               
                boolean oneD;
                oneD=(cusNet.containsKey(custIdONE[i]+","+custIdTWO[i])|| 
                              cusNet.containsKey(custIdTWO[i]+","+custIdONE[i]));
                if(oneD){
                output1.add("trusted");
                output2.add("trusted");
                output3.add("trusted");
                System.out.println(this.custIdONE[i]+" trust(1D) "+this.custIdTWO[i]);
                }
                else if(!oneD) {//2-else, this else without "if" is meaningful 
                output1.add("Unverified");//end writing feature 1 for all non-oneD
                //2-for
                boolean twoDeg=false;
                int maxLength=this.pastcustomers.length;
                twoDegree:
                for(int k=0;k<maxLength;k++){
                    
                     String str_k=this.pastcustomers[k];
                     
                     boolean twoD;
                     
                     twoD=((cusNet.containsKey(str_k+","+custIdONE[i])||
                                cusNet.containsKey(custIdONE[i]+","+str_k))&&
                           (cusNet.containsKey(str_k+","+custIdTWO[i])||
                                cusNet.containsKey(custIdTWO[i]+","+str_k)));
   
                     if(twoD){
                         output2.add("trusted");
                         output3.add("trusted");
                         twoDeg=true;
                         System.out.println(this.custIdONE[i]+" trust(2D) "+this.custIdTWO[i]);
                         break;
                    }//end of if in 2-D selection
                    
                }//end of 2-for
                
                boolean threeDeg=false;
                
                 //3-for: 3-degree selection
              if(!twoDeg){
                    output2.add("unverified");//end wrting 
                threeDegree:
                for(int k=0;k<maxLength;k++){
                        for(int j=0;j<maxLength;j++){
                             
                             String str_k=this.pastcustomers[k];
                             String str_j=this.pastcustomers[j];
                            
                            boolean threeD;
                          
                              threeD=
                            ((cusNet.containsKey(str_k+","+custIdONE[i])||
                                        cusNet.containsKey(custIdONE[i]+","+str_k))&&
                            (cusNet.containsKey(str_k+","+str_j)||
                                        cusNet.containsKey(str_j+","+str_k))&&
                            (cusNet.containsKey(custIdTWO[i]+","+str_j)||
                                        cusNet.containsKey(str_j+","+custIdTWO[i])));
                            
                                if(threeD){
                                   threeDeg=true;
                                   output3.add("trusted");
                                   System.out.println(this.custIdONE[i]+" trust(3D) "+this.custIdTWO[i]);
                                   break;
                                }
                                
                        }
                }//end of 3-for threeDegree
             } 
             
            boolean fourDeg = false; 
            
            
             if(!threeDeg && !twoDeg){
              fourdegree:
                for(int k=0;k<maxLength;k++){
                   for(int j=0;j<maxLength;j++){
                       for(int q=0;q<maxLength;q++){
                           
                           String str_k, str_j,str_q;
                           str_k=this.pastcustomers[k];
                           str_j=this.pastcustomers[j];
                           str_q=this.pastcustomers[q];
                           
                           boolean fourD;
                           
                            fourD=
                            (cusNet.containsKey(str_k+","+this.custIdONE[i])||
                                    cusNet.containsKey(this.custIdONE[i]+","+str_k))&&
                            (cusNet.containsKey(str_j+","+str_k)||
                                    cusNet.containsKey(str_k+","+str_j)) &&
                            (cusNet.containsKey(str_q+","+str_j)||
                                    cusNet.containsKey(str_j+","+str_q))&&
                            (cusNet.containsKey(this.custIdTWO[i]+","+str_q)||
                                    cusNet.containsKey(str_q+","+this.custIdTWO[i]));
                            
                               if(fourD){
                                  System.out.println(this.custIdONE[i]+" trust(4D) "+this.custIdTWO[i]);
                                  output3.add("trusted");
                                  fourDeg=true;
                                  break;
                              }   
                        }
                    }
                }//end of 4-for threeDegree 
                 
             }
             
             //Over 4-degree is not trusted
             if(!twoDeg && !threeDeg && !fourDeg){
                 
                 System.out.println(this.custIdONE[i]+" unverified "+this.custIdTWO[i]);
                 output3.add("unverified");
             }
             
         }//end of 2-else
                
        }//end of 1-else
           
      } //end of 1-for
        writeToFile(output1,"output1");  
        writeToFile(output2,"output2");   
        writeToFile(output3,"output3");   
    }
    
    public void getCustomersRelations() { 
        CreateStatusList(); 
    }
    
    private void writeToFile(ArrayList content,String filename){
       
        File file;
          
        try {
            file= new File(this.outputfileLocation+"/"+filename+".txt");  
            file.delete();
            if (!file.exists()) {
		file.createNewFile();
            }
                FileWriter fileWriter = new FileWriter(file,true);
                PrintWriter out = new PrintWriter(fileWriter);
                
                for(int i=0;i < content.size();i++)
                {
                 out.print(content.get(i)+"\n");
                }
                out.close();
                  
        } catch (IOException e) {
		e.printStackTrace();
	} 
        
    }
    
    
    
    
}

