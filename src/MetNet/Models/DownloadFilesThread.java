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
        for (int i=0; i<Utils.PATHWAYLIST.length; i++) {
            String path = Utils.PATHWAYLIST[i].getName();
            try {
                URL url = new URL("http://rest.kegg.jp/get/" + org + path + "/kgml");
                Path targetPath = new File(Dir + "/" + path + ".xml").toPath();
                Files.copy(url.openStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                totalpathway++;
            } catch (UnknownHostException ex) {
                Logger.getLogger(DownloadFilesThread.class.getName()).log(Level.SEVERE, "Host not found: check your internet connection", ex);
                System.exit(1);
            } catch (MalformedURLException ex) {
                Logger.getLogger(DownloadFilesThread.class.getName()).log(Level.SEVERE, "URL not found", ex);
                System.exit(1);
            } catch (IOException ex) {
                totalnotfound++;
            }
        orgModel.incrementCount();  // incrementing the total number of downloaded pathways
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


}
