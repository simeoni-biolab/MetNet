/*
    MetNet: comparison of Methabolic Networks
*/
package MetNet.models;

import java.io.File;
import javax.swing.SwingWorker;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;
import java.util.Random;

/*
allows the download of KGML files using SwingWorker threads
Used for the GUI execution
*/ 
public class DownloadFilesThread extends SwingWorker<Void, Void> {

    private OrganismsModel orgModel;  
    private String org;

    /*
    class Constructor 
    @param org: KEGG code of the organism whose pathways need to be downloaded
    @param orgModel: pointer to the caller
     */
    public DownloadFilesThread(String org, OrganismsModel orgModel) {
        this.orgModel = orgModel;
        this.org = org;
    }


    @Override
    public Void doInBackground() {
        int totalpathway = 0;
        int totalnotfound = 0;
        //Create the "organisms" folder if it doesn't exist 
        new File(Utils.ORGANISMS_FOLDER).mkdir();
        // Delete and re-create the organism folder 
        String Dir = Utils.ORGANISMS_FOLDER + org;
        File f = new File(Dir);
        deleteFolder(f);
        f.mkdir();
        //KGML files download by exploiting the public KEGG API
        int i = 0;
        boolean proceed = true;
        while (i < Utils.PATHWAYLIST.length) {
            String path = Utils.PATHWAYLIST[i].getName();
            //String filename = Dir + "/" + path + ".xml";
            try {
                URL url = new URL("https://rest.kegg.jp/get/" + org + path + "/kgml");
                Path targetPath = new File(Dir + "/" + path + ".xml").toPath();
                Files.copy(url.openStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                // copyFile(filename, url);
                totalpathway++;
                proceed = true;

            } catch (UnknownHostException ex) {
                Logger.getLogger(DownloadFilesThread.class.getName()).log(Level.SEVERE, "Host not found: check your internet connection", ex);
                System.exit(1);
            } catch (MalformedURLException ex) {
                Logger.getLogger(DownloadFilesThread.class.getName()).log(Level.SEVERE, "URL not found", ex);
                System.exit(1);
            } catch (IOException ex) {
                //System.out.println(ex.toString());
                if (ex.toString().contains(" 403 ")) { // forbidden access case
                    proceed = false;
                    wait(500); // wait for a random number between [5,10] seconds
                }                    
                else { // file not found case
                    totalnotfound++;
                    proceed = true;
                }
            }
        
            //System.out.println("index i = " + i + " filename = " + filename + " proceed = " + proceed);
            if (proceed) {
                orgModel.incrementCount();  // incrementing the total number of downloaded pathways
                i++;       
            }
        }
        
        System.out.println("total number of pathways " + org + ": " + totalpathway); 
        return null;
    }
    

    /*
    Remove the thread from the list of threads.
    If the list of threads is empty the download phase is terminated 
    */
    @Override
    public void done() {
        String[] tmp = this.toString().split("@");
            if (ThreadList.notifyThread(tmp[tmp.length - 1])) {             
                orgModel.threadsEnd();
            }
    }

    // the specified file/directory is deleted
    private static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    
    private static void copyFile(final String filename, final URL url)
        throws MalformedURLException, IOException {
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {
            in = new BufferedInputStream(url.openStream());
            fout = new FileOutputStream(filename);

            final byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (fout != null) {
                fout.close();
            }
        }
    }

    // required k>0 
    // waiting time in milliseconds: k + random(0,k): 
    private static void wait(int k) {
        Random random = new Random();
        try{
            Thread.sleep(random.nextInt(k) + k);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}
