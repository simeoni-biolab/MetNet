/*
MetNet: comparison of Metabolic Networks
 */
package MetNet.Models;


/**
 class for executing MetNet as a command line program
 */
public class CommandLineExecution  {
    String org1;
    String org2;
    String method;
    OrganismsModel orgModel;
    MainModel mainModel;
    MainResults mResults = new MainResults();

    
    public CommandLineExecution(String org1, String org2, String method) {
        this.org1 = org1;
        this.org2 = org2;
        this.method = method;
    }
    
    public void exe() {

        orgModel = new OrganismsModel();

        // KEGG files download is performed only if needed; 
        // last argument allows for the command line execution
        orgModel.checkFilesCommandLine(org1, org2);
        
        // 
        mResults = new MainResults();
        mainModel = new MainModel(org1, org2);          
        mainModel.parsingAndComparingCommandLine(method, mResults);
               
    }

 
}
