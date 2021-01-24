/*
    MetNet: comparison of Methabolic Networks
*/
package MetNet.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
 


/**
 *
 * Organisms download - model part
 */
public class OrganismsModel {
    private List<PropertyChangeListener> listener = new ArrayList<PropertyChangeListener>();
    private String org1;
    private String org2;
    private boolean download1 = false;
    private boolean download2 = false;
    private static int contatore = 0; // total number of downloaded pathways
    private int numThread = 0; // number of threads
    
    
    
    
    /*
        The procedure checks the presence of the pathway files and creates the threads
        for download if necessary
     * @param org1
     * @param org2
     * @param download1 // download required even if the first organism is already locally present  
     * @param download2 // download required even if the second organism is already locally present
     * @param commandLine  // true if MetNet runs as a command line program
    */

    public void checkFiles(String org1, String org2, boolean download1, boolean download2)  {
        boolean o1;
        boolean o2;
        String s = "";
         
        this.org1 = org1;
        this.org2 = org2;

        // o1 è vero se è richiesto il download oppure l'organismo non è già presente localmente 
        o1 = (download1 || !Files.isDirectory(Paths.get("organisms/"+org1)));
        // o2 è vero se è richiesto il download oppure l'organismo non è già presente localmente 
        o2 = (download2 || !Files.isDirectory(Paths.get("organisms/"+org2)));
        
        
        if (o1 || o2) {// se almeno uno dei due organismi deve essere scaricato
            if (o1 && o2) 
                createThread(org1, org2); // download di entrambi
            else if (o1) 
                    createThread(org1, null); // solo org1
                 else 
                    createThread(null, org2); // solo org2
        }
        else {
            // download non necessario
            notifyListeners(this, "status", null, "stop"); // notifica fine
        }
        
            
    } 

    /*
        This function create two threads that download the files of the two organisms
        selected
    */
    private void createThread(String org1, String org2){
        contatore = 0;
        numThread = 0;
        DownloadFilesThread th1 = null;  
        DownloadFilesThread th2 = null; 
        notifyListeners(this, "status", null, "start"); // start of download notified
     
        //Create the first thread with the first organism
        if (org1 != null) {
            numThread++;
            th1 = new DownloadFilesThread(org1, this);
            th1.execute();
            String[] tmp1 = th1.toString().split("@"); //Save the id of the thread in an array
            ThreadList.addThread(tmp1[tmp1.length-1]);
        }
       
        //Create the second thread with the second organism
        if (org2 != null) {
           numThread++;
           th2 = new DownloadFilesThread(org2, this);
           th2.execute();        
           String[] tmp2 = th2.toString().split("@"); //Save the id of the thread in an array   
           ThreadList.addThread(tmp2[tmp2.length-1]);
        }
      
        
     }
    
    //    Counts the number of downloaded files. 
    //    It is synchronized since each thread can access to this method. 
    //    At each call the "percentage" of downloaded pathway is notified
    public synchronized void incrementCount(){
        contatore++;
        int val = contatore* 100/(numThread * Utils.PATHWAYLIST.length);
        notifyListeners(this, "status",""+val,"progress");  
    }
    
    // all threads ended up: the end of download is notified
    public void threadsEnd() {
       notifyListeners(this, "status", "progress", "stop");          
    }
    

    private void notifyListeners(Object object, String property, String oldValue, String newValue) {
        for (PropertyChangeListener name : listener) {
            name.propertyChange(new PropertyChangeEvent(this, property, oldValue, newValue));
        }
    }

    public void addChangeListener(PropertyChangeListener newListener) {
        listener.add(newListener);
    }    

   /*
        download of KGML files in the command line case
    */
    public void checkFilesCommandLine(String org1, String org2) {
         
        this.org1 = org1; 
        this.org2 = org2;
        
        if (!Files.isDirectory(Paths.get(Utils.ORGANISMS_FOLDER +org1))) { // org1 non presente
            System.out.println("Download organism " + org1);
            Utils.downloadOrganism(org1); // scarico org1
        }
        
        if (!Files.isDirectory(Paths.get(Utils.ORGANISMS_FOLDER +org2))) { // org2 non presente
            System.out.println("Download organism " + org2);
            Utils.downloadOrganism(org2); // scarico org2
        }              
    }  

 }
