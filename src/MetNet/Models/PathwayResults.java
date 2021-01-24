/*
    MetNet: comparison of Methabolic Networks
*/
package MetNet.models;

/**
 * data structure to store the comparison results of a single pathway 
 * at the structural and functional levels
 */
public class PathwayResults {
    private String name;
    private String description;
    private Double valueNetwork;
    private Double valuePathway;
    
    public PathwayResults(String name, String description, Double valueNetwork, Double valuePathway) {   
        this.name = name;
        this.description = description;
        this.valueNetwork = valueNetwork;
        this.valuePathway = valuePathway;
    }

    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Double getValueNetwork() {
        return valueNetwork;
    }

    public Double getValuePathway() {
        return valuePathway;
    }
}
