/*
    MetNet: comparison of Methabolic Networks
*/

package MetNet.Models;
import java.util.ArrayList;

/**
 *
 * Data structure for storing the comparison results
 */
public class MainResults {
    // global indexes
    private double pathwaySimIndex = 0.0;
    private double weightedPathwaySimIndex = 0.0;
    private double structuralSimIndex = 0.0;
    private double combinedSimIndex = 0.0;
    // local indexes
    private ArrayList<PathwayResults> table = new ArrayList();
    // adjacency matrices of the two organisms  
    private int[][] matrixOrg1 = null;
    private int[][] matrixOrg2 = null;    


    public void setPathwaySimIndex(double d) {
        pathwaySimIndex = d;
    }
    
    public void setWeightedPathwaySimIndex(double d) {
        weightedPathwaySimIndex = d;
    }

    public void setStructuralSimIndex(double d) {
        structuralSimIndex = d;
    }
 
    public void setCombinedSimIndex(double d) {
        combinedSimIndex = d;
    }
    
    public void setMatrixOrg1(int[][] m) {
        matrixOrg1 = m;
    }
    
    public void setMatrixOrg2(int[][] m) {
        matrixOrg2 = m;
    }
    
    public int[][] getMatrixOrg1() {
        return matrixOrg1;
    }
    
    public int[][] getMatrixOrg2() {
        return matrixOrg2;
    }    
    
    public double getPathwaySimIndex() {
        return pathwaySimIndex;
    }
    
    public double getWeightedPathwaySimIndex( ) {
        return weightedPathwaySimIndex;
    }

    public double getStructuralSimIndex() {
        return structuralSimIndex;
    }
 
    public double getCombinedSimIndex() {
        return combinedSimIndex;
    }

    public void addToTable(PathwayResults tr) {
        table.add(tr);
    }
    
    public String getNameRow(int i) {
        if (i >= 0 && i < table.size())
            return table.get(i).getName();
        else 
            return null;
    }

    public String getDescriptionRow(int i) {
        if (i >= 0 && i < table.size())
            return table.get(i).getDescription();
        else 
            return null;
    }

    public Double getvalueNetworkRow(int i) {
        if (i >= 0 && i < table.size())
            return table.get(i).getValueNetwork();
        else 
            return null;
    }
    
    public Double getvaluePathwayRow(int i) {
        if (i >= 0 && i < table.size())
            return table.get(i).getValuePathway();
        else 
            return null;
    }


    public int tableSize() {
        return table.size();
    }
}
