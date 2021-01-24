
/*
    MetNet: comparison of Methabolic Networks
*/
package MetNet.models;

import java.io.IOException;
import java.net.URL;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;


/*
Starting model of the GUI execution: allows for updating the local list of KEGG organisms
*/
public class HomeModel extends Thread{
    private List<PropertyChangeListener> listener = new ArrayList<PropertyChangeListener>();
    
    
     /*
        The thread performs the following operations:
        1. download of KEGG organisms  
        2. save organisms in the static array organismList 
        3. write organisms in the local file organismLsit.txt 
    */
    @Override
    public void run() {
        ArrayList<String> A = new ArrayList();
        BufferedReader in;
        int id =0; // organisms counter  
        notifyListeners(this, "status", null, "start");
        
        try {
            // preparing the local file for writing
            FileOutputStream lista = new FileOutputStream("organismList.txt");
            PrintStream scrivi = new PrintStream(lista);

            //Connection to the KEGG API
            URL url = new URL("http://rest.kegg.jp/list/organism");
            in = new BufferedReader(new InputStreamReader(url.openStream()));

            //Parsing the KEGG organisms file 
            String line = in.readLine();
            while (line != null){
                String[] split = line.split("\t");
                String charset = "'"; String regex = "";
                String acronym = escapeSpecialChars(split[1], charset, regex);
                String name = escapeSpecialChars(split[2], charset, regex);
                if (acronym.length() == 3) // stesso spazio tra acronimo e nome
                    acronym = acronym + " ";
                String riga = acronym + "  " + name;
                A.add(riga); // adding to the local array 
                scrivi.println(riga); // writing in the local file  
                id++;
                line = in.readLine(); // reading the next line of the KEGG file  
            }
            scrivi.close(); // closing the local file 
        } catch (IOException ex) {
            Logger.getLogger(HomeModel.class.getName()).log(Level.SEVERE, null, ex);
        }    
           
        Utils.organismList = A.toArray(new String[id]); // update of the local data structure 
        notifyListeners(this, "status", "start", "end");
        
    }
    
   
    // special characters: checking and replacing
    private static String escapeSpecialChars(String s, String charset, String regex){
         return s.replaceAll(charset, regex); // possible special charset "(?=[]\\[+&|!(){}^\"~*?:\\\\-])"
    }
    
    
    private void notifyListeners(Object object, String property, String oldValue, String newValue) {
        for (PropertyChangeListener name : listener) {
            name.propertyChange(new PropertyChangeEvent(this, property, oldValue, newValue));
        }
    }

    public void addChangeListener(PropertyChangeListener newListener) {
        listener.add(newListener);
    }    
    

}
