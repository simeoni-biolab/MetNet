/*
    MetNet: comparison of Methabolic Networks
*/
package MetNet.models;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import java.util.HashMap;
import java.util.Map;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * KGML file Parsing - Metabolic network comparison - results presentation  - model part
 */
public class MainModel {
    private List<PropertyChangeListener> listener = new ArrayList<PropertyChangeListener>();    
    private MainResults results;  // data structure to store the comparison results
    private String org1;
    private String org2;
    private String pathwayMethod;
    private  Map<String, Integer> unionReactions; 
    private  Map<String, Pathway> pathwayListOrg1;
    private  Map<String, Pathway> pathwayListOrg2;
    private int[][] matrixC1; // "adjacency" matrix of the first organism 
    private int[][] matrixC2; // "adjacency" matrix of the second organism

    
    // class constructor
    public MainModel(String org1, String org2) {
        this.org1 = org1;
        this.org2 = org2;
        this.results = null; // connection with the view's results set as starting the comparison

        // local variables initialization
        pathwayMethod = null; // set by the user through the form
        unionReactions = new HashMap<>();
        pathwayListOrg1 = new HashMap<>();
        pathwayListOrg2 = new HashMap<>();               
    }

    
    public void parsingAndComparing(String pathwaymethod, MainResults results) {
        this.pathwayMethod = pathwaymethod;
        this.results = results; // connection with the caller results 

        
        // Start the timer in order to calculate the computation time
        ExecutionTimer.startTest();
        System.out.println("Comparison started: " + org1 + " - " + org2);

        // Launch two threads for executing the parsing of the KGML files of both organisms
        ParsingThread o1 = new ParsingThread(org1, pathwayListOrg1, this);
        o1.execute();
        ParsingThread o2 = new ParsingThread(org2, pathwayListOrg2, this);
        o2.execute();
            
        String[] tmp1 = o1.toString().split("@");
        String[] tmp2 = o2.toString().split("@");
        
        // Add the last two threads to a list of threads
        ThreadList.addThread(tmp1[tmp1.length-1]);
        ThreadList.addThread(tmp2[tmp2.length-1]);
    }

    // parsing threads ended up: starting the comparison
    public void endParsing() {
       startComparison();          
    }

    public void parsingAndComparingCommandLine(String pathwaymethod, MainResults results) {
        this.results = results; // link to the results to be shown
        this.pathwayMethod = pathwaymethod;
 
        // Start the timer in order to calculate the computation time
        ExecutionTimer.startTest();
        System.out.println("Comparison started: " + org1 + " - " + org2);
        
        Utils.parsingPathways(org1, pathwayListOrg1);
        Utils.parsingPathways(org2, pathwayListOrg2);

        startComparison();
    }
    
    
    /*
        Starting point of the comparison. Different procedures are called 
        wrt. the user selection
    */
    public void startComparison() {
        Map<String, Double> risultatiPathway = new HashMap<>();
        Map<String, Double> risultatiNetwork = new HashMap<>();
        // Launching the right procedure for comparing pathways according to the selection of the user
        switch(pathwayMethod){
            case "set": 
                risultatiPathway = pathwaySetMethod();
                break;
            case "multiset": 
                risultatiPathway = pathwayMultiSetMethod();
                break;
            default: 
                System.out.println("No method selected at pathway level.");
                break;
        }
        
        risultatiNetwork = networkCompoundIntersectionMethod();
        
        
        // results calculation, storing and exporting in .xls format
        calculateResults(risultatiPathway, risultatiNetwork);
        notifyListeners(this, "status", null, "stop");  // notify the view: results can be shown
        exportResults(risultatiPathway, risultatiNetwork);
        ExecutionTimer.finishTest();   // stop timer
        
    }
    
    /*
        Procedure for metabolic pathways comparison based on Sets.
        It implements the computation of the Pathway Similarity Index SimP_i and 
        manage three main cases:
        0 -> if a pathway is missing in one of the two organisms
        1 -> if a pathway is present in both the organisms but there are no reaction to compare
        Jaccard Index -> otherwise.
        
        In the last case we calculate the ratio between the common reactions and 
        the union of the reactions between the two organisms.
    */
    private Map<String, Double> pathwaySetMethod(){
        Map<String, Double> results = new HashMap();
        // Starting from the list of all the pathways we consider the pathways 
        // that are present in the organisms.
        for (int i=0; i<Utils.PATHWAYLIST.length; i++) {
            String pathway = Utils.PATHWAYLIST[i].getName();
            Pathway p1 = pathwayListOrg1.get(pathway);
            Pathway p2 = pathwayListOrg2.get(pathway);
            
            // First case
            if((p1==null && p2!=null)||(p1!=null && p2==null)){
                results.put(pathway, 0.0);
                // If the pathway is missing in the first organism we consider
                // only the reaction of the pathway in the other one and viceversa.
                if(p1==null){
                    Set<String> set = new HashSet<String>();
                    set.addAll(p2.getGeneList().values());
                    unionReactions.put(pathway, set.size());
                }else{
                    Set<String> set = new HashSet<String>();
                    set.addAll(p1.getGeneList().values());
                    unionReactions.put(pathway, set.size());                
                }
                
            // Second and third cases are managed in SetCompare procedure    
            }else if(p1!=null && p2!=null){
                results.put(pathway, SetCompare(p1.getGeneList(), p2.getGeneList()));
                unionReactions.put(pathway, union(p1.getGeneList(), p2.getGeneList()).size());
            }        
        }
        return results;
    }

    /*
        It manages the three cases defined previously in pathwaySetMethod. 
        Essentially we return different values in three cases:
        0 -> if a pathway is missing in one of the two organisms
        1 -> if a pathway is present in both the organisms but there are no reaction to compare
        Jaccard Index -> otherwise.
    */

    private double SetCompare(Map<String, String> list1, Map<String, String> list2){
       if(list1.isEmpty() && list2.isEmpty()){
           return 1.0;
       }
        //If there are reactions in both pathways
       if(!list1.isEmpty() && !list2.isEmpty()){
            return (((double)intersection(list1, list2).size())/((double)union(list1, list2).size()));
       }else{
           //Return 0 if the files exist but only one of them contains reactions
           return 0.0;
       }
       
    }


    
    /*
        Procedure for metabolic pathways comparison based on Multisets.
        It implements the computation of the Pathway Similarity Index SimP_i and 
        manage three main cases:
        0 -> if a pathway is missing in one of the two organisms
        1 -> if a pathway is present in both the organisms but there are no reaction to compare
        Jaccard Index -> otherwise.
        
    NOTE: with this method we take into account the occurrences of each single 
    reaction in each pathway.
    */
    private Map<String, Double> pathwayMultiSetMethod(){
        Map<String, Double> results = new HashMap();
         
        for (int i=0; i<Utils.PATHWAYLIST.length; i++) {
            String pathway = Utils.PATHWAYLIST[i].getName();
            Pathway p1 = pathwayListOrg1.get(pathway);
            Pathway p2 = pathwayListOrg2.get(pathway);
            
            if((p1==null && p2!=null)||(p1!=null && p2==null)){
                results.put(pathway, 0.0);     
                 if(p1==null){
                    Multiset<String> set = HashMultiset.create(p2.getGeneList().values());
                    unionReactions.put(pathway, set.size());
                }else{
                    Multiset<String> set = HashMultiset.create(p1.getGeneList().values());
                    unionReactions.put(pathway, set.size());                
                }
            }else if(p1!=null && p2!=null){
                results.put(pathway, MultiSetCompare(pathwayListOrg1.get(pathway).getGeneList(), pathwayListOrg2.get(pathway).getGeneList()));
                //Marta: unionReaction serve per il calcolo dell'indice globale
                //weighted functional index: si fa l'unione delle reazioni come insieme
                //indipendentemente dal tipo di confronto di pathway utilizzato 
                unionReactions.put(pathway, union(p1.getGeneList(), p2.getGeneList()).size());
                
            }        
        }
        return results;
    }

    /*
        Comparison of metabolic network using compound intersection method
        Implement the SimS index.
        Some convention are used in order to produce a custom adiacency matrix:
        Main diagonal: Cell i,i
        -1 -> represents a pathway which is not present in the metabolism of the 
              organism.
        0 -> represents an isolated pathway (do not share compounds with any other pathway)
        >0 -> the value represents the number of connections of that pathway 
        Cell i,j = Cell j,i (i<>j)
        cardinality of common compounds between pathway i and pathway j (symmetric value)
    */
    private Map<String, Double> networkCompoundIntersectionMethod() {
        Map<String, Double> networkresults = new HashMap();
        double setunion;
        double setintersection;
        
        // costruisco la struttura rispetto ai compound
        // usate matrici globali per la connessione con la parte di visualizzazione della struttura
        matrixC1 = matrixCompoundIntersection(pathwayListOrg1);
        matrixC2 = matrixCompoundIntersection(pathwayListOrg2);
        printMatrix(matrixC1, org1);
        printMatrix(matrixC2, org2);
        
        // We consider the list of all the pathways defined in the home model
        for (int i=0; i<Utils.PATHWAYLIST.length; i++) {
            String id = Utils.PATHWAYLIST[i].getName();

            //The pathway doesn't exist in one of the two organisms
            if((matrixC1[i][i]==-1 && matrixC2[i][i]!=-1) || (matrixC1[i][i]!=-1 && matrixC2[i][i]==-1)){
                networkresults.put(id, 0.0);
                //If the pathway exists
            }else if(matrixC1[i][i]!=-1 && matrixC2[i][i]!=-1){
                // We manage the case where the pathways is isolated in both organism
                if(matrixC1[i][i]==0 && matrixC2[i][i]==0)
                    networkresults.put(id, 1.0);
                
                //We manage the case where one of the pathway is isolated and the other is connected
                if(matrixC1[i][i] == 0 && matrixC2[i][i] > 0)
                    networkresults.put(id, (double)(1/(1+ matrixC2[i][i])));            
                if(matrixC1[i][i] > 0 && matrixC2[i][i] == 0)
                    networkresults.put(id, (double)(1/(1+ matrixC1[i][i])));
                    
                    
                if(matrixC1[i][i]>0 && matrixC2[i][i]>0){
                    // We manage the case where the pathway are present and connected
                    setunion = 0.0;
                    setintersection = 0.0;
                    for(int j=0; j<Utils.PATHWAYLIST.length;j++){
                        if (i!=j) { // salto la diagonale (che indica altro) 
                        if ((matrixC1[i][j] > 0) || (matrixC2[i][j] > 0))
                            setunion++;
                        if ((matrixC1[i][j] > 0) && (matrixC2[i][j] > 0))
                            setintersection++;
                        }
                    }
                    networkresults.put(id, (setintersection/setunion));
                }
            }
        }
           
        return networkresults;
    }
    
    /*
        Return matrix is such that the value of each cell i,i is:
        -1 -> represents a pathway which is not present in the metabolism of the 
              organism.
        0 -> represents an isolated pathway (do not share compounds with any other pathway)
        >0 -> the value represents the number of connections of that pathway (degree)
        ciascuna cella i,j = j,i (i<>j)
        cardinality of the set containing the common compounds between pathway i and pathway j (symmetric value)

    */    
    private int[][] matrixCompoundIntersection(Map<String, Pathway> pathwayListOrg) {
        int size = Utils.PATHWAYLIST.length;
        int[][] m = new int[size][size];
        
        // inizializzazione matrice
        for (int i = 0; i< size;i++)
            for (int j = 0; j < size; j++) {
                if (i==j)
                    m[i][i] = -1; // inizializzo la diagonale con pathway non esistente
                else
                    m[i][j] = 0;  // inizializzo il numero di compound in comune tra i due pathway i e j
            }

        
        
        // We consider the list of all the pathways defined in the home model
        for (int i1=0; i1<Utils.PATHWAYLIST.length; i1++) {
            for (int i2=0; i2<Utils.PATHWAYLIST.length; i2++) {
                int card; // cardinalità intersezione pathway
                String id1 = Utils.PATHWAYLIST[i1].getName();
                String id2 = Utils.PATHWAYLIST[i2].getName();
                boolean b1 = pathwayListOrg.containsKey(id1); // esiste il pathway id1 per l'organismo?
                boolean b2 = pathwayListOrg.containsKey(id2); // esiste il pathway id2 per l'organismo?
                                
                if (b1 & m[i1][i1] == -1) // se il pathway esiste ma è ancora segnalato come non esistente
                    m[i1][i1] = 0; // pathway esiste e, al momento, è isolato
                
                if (b2 & m[i2][i2] == -1) // se il pathway esiste ma è ancora segnalato come non esistente
                    m[i2][i2] = 0; // pathway esiste e, al momento, è isolato
                
                // se un pathway esiste e uno no, la matrice già è stata inizializzata
                // col valore zero su m[i1][i2] e m[i2][i1]
                
                // faccio il lavoro su metà matrice e copio per simmetria
                if (i1 < i2 && b1 && b2) { // se entrambi i pathway esistono
                    card = cardinality(pathwayListOrg.get(id1).getCompoundList(), pathwayListOrg.get(id2).getCompoundList());
                    m[i1][i2] = m[i2][i1] = card; 
                    if (card > 0) { // i due pathway hanno compound in comune
                        m[i1][i1]++; // i1 non è isolato, incremento le sue connessioni
                        m[i2][i2]++; // i2 non è isolato, incremento le sue connessioni
                    }    
                }
            }
        }
        
        return m;     
    }

    private int cardinality(Set<String> S, Set<String> T) {
        Set R = new HashSet<>();
        
      
        S.stream().forEach((entry) -> {
            if(T.contains(entry))
              R.add(entry);
        });
      
      return R.size();      
    }

    /*
        Essentially perform the same operation that the setCompare procedure.
    */
    private double MultiSetCompare(Map<String, String> list1, Map<String, String> list2){
        if(list1.isEmpty() && list2.isEmpty()){
           return 1.0;
        }
        //If there are reactions in both pathways
        if(!list1.isEmpty() && !list2.isEmpty()){ 
           return (((double)Multiintersection(list1, list2).size())/((double)Multiunion(list1, list2).size()));
        }else{
           //Return 0 if the files exist but only one of them contains reactions
           return 0.0;
        }
    }
    
    
    /*
        Union between sets of chemical reactions
    */
    private <T,Z> Set<Z> union(Map<T,Z> list1, Map<T,Z> list2) {
        Set<Z> set = new HashSet<Z>();
        set.addAll(list1.values());
        set.addAll(list2.values());
        return new HashSet<Z>(set);
    }

    /*
        Intersection between sets of chemical reactions
    */
    private <T,Z> Set<Z> intersection(Map<T,Z> list1, Map<T,Z> list2) {
        Set<Z> set = new HashSet<Z>();
        list1.entrySet().stream().forEach((entry) -> {
            if(list2.containsValue(entry.getValue())) {
                set.add(entry.getValue());
            }
        });
        return new HashSet<Z>(set);
    }
    
    /*
        Union between multisets of chemical reactions
    */
    private <T,Z> Multiset<Z> Multiunion(Map<T,Z> list1, Map<T,Z> list2) {
        Multiset<Z> set1 = HashMultiset.create(list1.values());
        Multiset<Z> set2 = HashMultiset.create(list2.values());
        return Multisets.union(set1,set2);
    }
    
    /*
        Intersection between multisets of chemical reactions
    */
    private <T,Z> Multiset<Z> Multiintersection(Map<T,Z> list1, Map<T,Z> list2) {
        Multiset<Z> set1 = HashMultiset.create(list1.values());
        Multiset<Z> set2 = HashMultiset.create(list2.values());
        return Multisets.intersection(set1,set2);
    }
    
    // global and local indexes calculation
    // storing indexes and adjacency matrices in the results data structure
    private void calculateResults(Map <String, Double> risultatiP, Map <String, Double> risultatiN) {
        double somma = 0;
        double sommanetwork = 0;
        double sommapathway = 0;
        double sommapathwayweighted = 0;
        int totalweight = 0;
        
        // storing the adjacency matrices
        results.setMatrixOrg1(matrixC1);
        results.setMatrixOrg2(matrixC2);
        
        // Computation of the global similarity indexes
        for (String id: risultatiN.keySet()){
            // Combined Similarity Index: CI
            somma += risultatiN.get(id) * risultatiP.get(id);
            // Topological Similarity Index: SimS
            sommanetwork = sommanetwork + risultatiN.get(id);
            // Functional Similarity Index: SimPA
            sommapathway+= risultatiP.get(id);
            // Functional Similarity Index: SimPW
            sommapathwayweighted+= risultatiP.get(id)*unionReactions.get(id);
            totalweight+= unionReactions.get(id);
        }

        /* global index formulae  
           functionalSimIndex = sommapathway/risultatiP.size();
           weightedFunctionalSimIndex = sommapathwayweighted/totalweight;
           structuralSimIndex = sommanetwork/risultatiN.size();
           combinedSimIndex = somma/risultatiN.size();
        */
        
        // calculating and storing the global indexes
        results.setPathwaySimIndex(sommapathway/risultatiP.size());
        results.setWeightedPathwaySimIndex(sommapathwayweighted/totalweight);
        results.setStructuralSimIndex(sommanetwork/risultatiN.size());
        results.setCombinedSimIndex(somma/risultatiN.size());
        
        // filling the table of the local comparison results
        for (int i = 0; i< Utils.PATHWAYLIST.length; i++) {
            String nomepathway = Utils.PATHWAYLIST[i].getName();
            String descrizione = Utils.PATHWAYLIST[i].getDescription();
            // se almeno uno dei due organismi ha il pathway corrente 
            if (pathwayListOrg1.get(nomepathway) != null || pathwayListOrg2.get(nomepathway) != null) {
               Double valuePathway = risultatiP.get(nomepathway);
               Double valueNetwork = risultatiN.get(nomepathway);
               results.addToTable(new PathwayResults(nomepathway, descrizione, valueNetwork, valuePathway));
             }
        }

        
    }


    
    /*
        Export the results as an excel file
    */
    private void exportResults(Map <String, Double> risultatiP, Map <String, Double> risultatiN) {
        
        try{
                
            // Initialize the file
            String filename = "results-" + org1 + "-" + org2 + "C" + (pathwayMethod.equals("set")? "S":"M") + ".xls" ;
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("FirstSheet");
            int l=0;


            // intestazione file con i dati degli indici globali
            HSSFRow intesta = sheet.createRow(l++);
            intesta.createCell(0).setCellValue("Pathway method");
            intesta.createCell(3).setCellValue(pathwayMethod);
            intesta = sheet.createRow(l++);
            intesta.createCell(0).setCellValue("Network method");
            intesta.createCell(3).setCellValue("compound intersection");
            intesta = sheet.createRow(l++);
            intesta.createCell(0).setCellValue("Combined similarity index");
            intesta.createCell(3).setCellValue(results.getCombinedSimIndex());
            intesta = sheet.createRow(l++);
            intesta.createCell(0).setCellValue("Structure similarity index");
            intesta.createCell(3).setCellValue(results.getStructuralSimIndex());
            intesta = sheet.createRow(l++);
            intesta.createCell(0).setCellValue("Pathway similarity index");
            intesta.createCell(3).setCellValue(results.getPathwaySimIndex());
            intesta = sheet.createRow(l++);
            intesta.createCell(0).setCellValue("Weighted Pathway sim. index");
            intesta.createCell(3).setCellValue(results.getWeightedPathwaySimIndex());
            intesta = sheet.createRow(l++);    

            // caricamento risultati del confronto di ciascun pathway
            // nel file excel

        
            // Set the columns of the excel file  
            HSSFRow rowhead = sheet.createRow(l++);
            rowhead.createCell(0).setCellValue("Number");
            rowhead.createCell(1).setCellValue("Name");
            rowhead.createCell(2).setCellValue("Network Sim");
            rowhead.createCell(3).setCellValue("Pathway Sim");

            // scrivo i risultati di tutti i pathway
            for (int i = 0; i< Utils.PATHWAYLIST.length; i++) {
                String nomepathway = Utils.PATHWAYLIST[i].getName();
                String descrizione = Utils.PATHWAYLIST[i].getDescription();
                // se almeno uno dei due organismi ha il pathway corrente 
                if (pathwayListOrg1.get(nomepathway) != null || pathwayListOrg2.get(nomepathway) != null) {
                   Double valuePathway = risultatiP.get(nomepathway);
                   Double valueNetwork = risultatiN.get(nomepathway);
                   // aggiungo una riga al file excel         
                   HSSFRow row = sheet.createRow(l++);
                   row.createCell(0).setCellValue(nomepathway);
                row.createCell(1).setCellValue(descrizione);
                row.createCell(2).setCellValue(valueNetwork);
                row.createCell(3).setCellValue(valuePathway);
            }
        }

        
            int lastRow = sheet.getLastRowNum();
        
            // Write on file 
            FileOutputStream fileOut = new FileOutputStream(filename);
        
            workbook.write(fileOut); 
            fileOut.close();
            System.out.println("Your excel file has been generated.");
        
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);    
        }

    }



    /*  matrix: adjacency matrix of organism org
        org: organism 
        Write on an external file the matrix in input.
        Useful for debug.
    */
    private void printMatrix(int[][] matrix, String org) {
        try{
            FileOutputStream prova = new FileOutputStream("adjmatrix-"+org+".txt");
            PrintStream scrivi = new PrintStream(prova);
            for(int i=0; i< matrix.length; i++){
                scrivi.println("");
                for(int j=0; j<matrix.length;j++){
                    scrivi.print(matrix[i][j]+" ");
                }
            }
            scrivi.close();
        }
        catch(IOException ex){
            Logger.getLogger(MainModel.class.getName()).log(Level.SEVERE, null, ex);    
        }                
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
