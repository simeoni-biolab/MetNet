/*
    MetNet: comparison of Methabolic Networks
*/
package MetNet.models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;

/**
This class contains auxiliary static constants, variables, data structures and methods
 */
public class Utils {
    
    // constant values
    protected static final String ORGANISMLIST_FILENAME = "organismList.txt";
    protected static final String PATHWAYLIST_FILENAME = "pathwayList.txt";
    protected static final String ORGANISMS_FOLDER = "organisms/";
    protected static final String[] PATHWAYMETHOD = {"set", "multiset"};


    // data structures for pathways and organisms
    protected static final PathwayInfo[] PATHWAYLIST = readPathwayList(); // list of pathways to be considered
    protected static String[] organismList = readOrganismList();  // list of organisms to be considered: can be updated by the user
    


    // reading organisms from local file ORGANISMLIST_FILENAME 
    // and saving data on the static array organismList  
    // ATTENTION: the user may ask for updating the list of organisms through HomeView.
    // In that case the array organismList is updated correspondingly
    private static String[] readOrganismList() {
        ArrayList<String> A = new ArrayList();
        BufferedReader b;
        int id =0; // number of inserted organisms

        try {
            // preparing to read from local file  
            FileReader f = new FileReader(ORGANISMLIST_FILENAME);
            b = new BufferedReader(f);
            String s = b.readLine();

            while (s != null){
                A.add(s); // writing on local array  
                id++;
                s = b.readLine(); // reading the next line
            }

        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }    
           
        return A.toArray(new String[id]);
        
    }


    /*
    Reads from local file PATHWAYLIST_FILENAME the list of pathways to be considered
    */
    private static PathwayInfo[] readPathwayList() {
        ArrayList<PathwayInfo> A = new ArrayList();
        BufferedReader b;
        int id =0; // number of inserted pathways

        try {
            // preparing the read from local file
            FileReader f = new FileReader(PATHWAYLIST_FILENAME);
            b = new BufferedReader(f);
            String s = b.readLine();

            while (s != null){
             if (!s.isEmpty() && !s.startsWith("//")) {
                PathwayInfo ip = new PathwayInfo(s.substring(0,5), s.substring(6)); 
                A.add(ip); // writing on the local array
                id++;
             }
                s = b.readLine(); // reading the next line  
            }

        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }    
           
        return A.toArray(new PathwayInfo[id]);
        
    }
    
    // return the size of the pathway array
    public static int pathListSize() {
        return PATHWAYLIST.length;
    }
    
    /*
    return the name of the i-th pathway
    @param i = index of the pathway name to be retrieved
    */
    public static String getPathName(int i) {
        if (i>=0 && i < PATHWAYLIST.length)
            return PATHWAYLIST[i].getName();
        else
            return null;
    }
    
    /*
    return the description of the i-th pathway
    @param i = index of the pathway name to be retrieved
    */
    public static String getPathDescription(int i) {
        if (i>=0 && i < PATHWAYLIST.length)
            return PATHWAYLIST[i].getDescription();
        else
            return null;
    }

    // return the size of the organisms array    
    public static int orgListSize() {
        return organismList.length;
    }

    /*
    return true if the organism org is present in the organisms array
    @param org = KEGG code of the organism to be retrieved. Must be not null.
    */    
    public static boolean searchOrganism(String org) {
        int i = 0;
        boolean found = false;
        
        while ((i< organismList.length) && (!found)) {
            String app[] = organismList[i].split(" ");
            found = app[0].equals(org);
            i++;
        }
        return found;
    }  
    
    /*
    return the i-th organism
    @param i = index of the organism to be retrieved
    */
    public static String getOrganism(int i) {
        if (i>=0 && i < organismList.length)
            return organismList[i];
        else
            return null;
    }
    
    // return the organismList array
    public static String[] getOrganismList() {
        return organismList;
    }
    
    // return the PATHWAYMETHOD array
    public static String[] getPathwayMethod() {
        return PATHWAYMETHOD;
    }

    
    /*
    return true if the specified pathway method is present in the PATHWAYMETHOD array
    @param method = pathway method to be retrieved. Must be not null.
    */    
    public static boolean searchPathwayMethod(String method) {
      int i;
      for (i=0; (i< PATHWAYMETHOD.length) && (!PATHWAYMETHOD[i].equals(method)); i++);
      return (i<PATHWAYMETHOD.length);
  }
     
    
//-----------------------------------------------------------------   
// Utilities for command line execution
    
    /*
    pathways download for the organim org: command-line case
    @param org = KEGG code of the organism to be retrieved. Must be not null.
    */
    public static void downloadOrganism(String org) {
        int totalpathway = 0;
        int totalnotfound = 0;
        
        //Create the organisms folder if it doesn't exist 
        new File(ORGANISMS_FOLDER).mkdir();
        // deletion and re-creation of the organism folder 
        String Dir = ORGANISMS_FOLDER + org;
        File f = new File(Dir);
        deleteFolder(f);
        f.mkdir();
        //Download the KGML files exploiting the public KEGG API
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
        }
        System.out.println("number of pathways " + org + ": " + totalpathway);
        
        
    }


    
    // file/directory deletion 
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

    // metodo che fa il parsing dei pathway di un organismo nel caso commandLine
    /*
    parsing the pathways  of the organim org: command-line case
    @param org = KEGG code of the organism to be retrieved. Must be not null.
    @param p = pathway list 
    */
    public static void parsingPathways(String organism, Map<String, Pathway> p) {
        try{
            Files.walk(Paths.get("organisms/" + organism + "/")).forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    SAXParserFactory parserFactor = SAXParserFactory.newInstance();
                    try {
                        SAXParser saxParser = parserFactor.newSAXParser();
                        SAXHandler handler = new SAXHandler(p);
                        saxParser.parse(filePath.toString(), handler);
                    } catch (ParserConfigurationException | SAXException | IOException ex) {
                        Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, "I/O problems: check internet connection", ex);
                        System.exit(1);
                    }
                }
            });
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);    
        }
  
    }
     
    
}
