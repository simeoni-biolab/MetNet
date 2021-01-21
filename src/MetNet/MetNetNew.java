/*
MetNet: comparison of Metabolic Networks
 */
package MetNet;

import java.util.logging.Level;
import java.util.logging.Logger;
import MetNet.Models.Utils;
import MetNet.Models.CommandLineExecution;
import MetNet.Gui.HomeView;

/*
MetNet's main class.
MetNet can run as standalone GUI application or as a command-line program. 
 */
public class MetNetNew {

    /*
     @param args the command line arguments:
     1. first organism
     2. second organism
     3. pathway method to be used for the comparison: "set" or "multiset"
     */
    public static void main(String[] args) {
         CommandLineExecution cle;

        if (args.length == 0) {// no parameters -- GUI execution
            try{
                HomeView homeView = new HomeView();
            } catch (Exception ex) {
                Logger.getLogger(MetNetNew.class.getName()).log(Level.SEVERE, "Execution failure", ex);
            }
        }
        else            
            if (args.length < 3) 
                System.out.println("Wrong number of parameters");
            else { // command line execution
                 // parameters checking
                 boolean exec = true;
                 
                 if (!Utils.searchOrganism(args[0])) {
                     System.out.println("Organism " + args[0] + " not found"); 
                     exec = false;
                 }
                 if (!Utils.searchOrganism(args[1])) {
                     System.out.println("Organism " + args[1] + " not found");
                     exec = false;
                 }
                 
                 if (!Utils.searchPathwayMethod(args[2])) {
                     System.out.println("Wrong pathway comparison method: " + args[2]);
                     exec = false;
                 }
                 if (exec) { // parameters ok: execution  
                    try {                       
                        cle = new CommandLineExecution(args[0], args[1], args[2]);
                        cle.exe();
                    }catch (Exception ex) {               
                        Logger.getLogger(MetNetNew.class.getName()).log(Level.SEVERE, "Execution failure", ex);
                    }
                 }
            } // end command line execution
        
    } // end main           

} // end class
