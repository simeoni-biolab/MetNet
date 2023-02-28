/*
    MetNet: comparison of Methabolic Networks
*/ 
package MetNet.models;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;


/*
    class managing the thread that parses all KGML files of an organism
*/
public class ParsingThread extends SwingWorker<Void, Void> {
    private String organism;
    private Map<String, Pathway> pathway;
    private MainModel parModel;
    
    /*
    class constructor
    @param organism: organism to be parsed
    @param pathway: list of pathways of the organism
    @pm: pointer to the caller
    */
    public ParsingThread(String organism, Map<String, Pathway> pathway, MainModel pm){
        this.organism = organism;
        this.pathway = pathway;
        this.parModel = pm;
    }
          
    /*
        For each KGML file of the specific organism create an object that parses the files
    */
    @Override
    public Void doInBackground() {
        try{
            Files.walk(Paths.get(Utils.ORGANISMS_FOLDER + organism + "/")).forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    SAXParserFactory parserFactor = SAXParserFactory.newInstance();
                    try {
                        SAXParser saxParser = parserFactor.newSAXParser();
                        SAXHandler handler = new SAXHandler(pathway);
                        saxParser.parse(filePath.toString(), handler);
                    } catch (ParserConfigurationException | SAXException | IOException ex) {
                        Logger.getLogger(ParsingThread.class.getName()).log(Level.SEVERE, "Parser, I/O or internet connection problems", ex);
                        System.exit(1);
                    }
                }
            });
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, "I/O problems:: check internet connection", ex);    
            System.exit(1); 
        }
        
       return null;
    }
    
    
    /*
    Remove the thread from the list of threads.
    If the list becomes empty the download phase is terminated 
    */
    @Override
    public void done() {
        String[] tmp = this.toString().split("@");
        if (ThreadList.notifyThread(tmp[tmp.length-1])){
            parModel.endParsing();
        }
    }
}