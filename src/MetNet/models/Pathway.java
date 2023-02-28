/*
MetNet: comparison of Metabolic Networks
 */


package MetNet.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 *
 * data structure to store information about one single pathway
 */
public class Pathway {
    private Map<String, String> geneList;
    private Map<Integer, Integer> ecrelList;
    private Map<Integer, String> maplinkList;
    private Set<String> compoundList;
    private String name;
    private String title;
    private String org;
    private String num;
    private String linkMapImage;
    private String linkKEGGPathway;

    
   /*
    * class constructor
    */
    public Pathway(String name, String title, String org, String num, String linkMapImage, String linkKEGGPathway){
        this.name = name;
        this.title = title;
        this.org = org;
        this.num = num;
        this.linkMapImage = linkMapImage;
        this.linkKEGGPathway = linkKEGGPathway; 
        ecrelList = new HashMap<>();
        maplinkList = new HashMap<>();
        geneList = new HashMap<>();
        compoundList = new HashSet<>();
    }
    
    
    /*
     * get methods for the object's attributes
    */
    
    protected String getNumber(){
        return this.num;
    }
    
    protected String getOrg(){
        return this.org;
    }
    
    protected String getName(){
        return this.name;
    }
    
    protected String getTitle(){
        return this.title;
    }
    
    protected Map<Integer, Integer> getEcrelList(){
        return this.ecrelList;
    }
    
    protected Map<Integer, String> getMapLinkList(){
        return this.maplinkList;
    }
    
    protected Map<String, String> getGeneList(){
        return this.geneList;
    }

    protected Set<String> getCompoundList(){
        return this.compoundList;
    }
}
