package digitalwallet;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Mahesh Imaduwa
 */
public class Antifraud {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        String batch,stream,output_file_location;
        
        batch = "paymo_input/batch_payment.txt";
        stream= "paymo_input/stream_payment.txt";
        output_file_location= "paymo_output";
        
        
        Relationship rel = new Relationship(batch,stream,output_file_location);
        rel.getCustomersRelations();
        
    }
    
}
