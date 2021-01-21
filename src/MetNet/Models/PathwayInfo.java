/*
    MetNet: comparison of Methabolic Networks
*/
package MetNet.Models;

/**
 *
 * Pathway basic information: name and description
 */
public class PathwayInfo {
    private String name;
    private String description;
    
    // class constructor
    public PathwayInfo(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    // get methods
    
    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

}
    

