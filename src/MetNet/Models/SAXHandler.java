/*
MetNet: comparison of Metabolic Networks
 */
package MetNet.models;

import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/*
Parsing of KGML files
 */

public class SAXHandler extends DefaultHandler {
private Map<String, Pathway> plist;
private Pathway p;
  
  /*
  class constructor
  @param plist: pathways to be parsed
  */
  public SAXHandler(Map<String, Pathway> plist){
      this.plist = plist;
  }

  /*
    Method triggered when a start tag is found.
    It performs the parsing of a single tag and saves all useful information.
  */
  @Override
  public void startElement(String uri, String localName, 
                           String qName, Attributes attributes) 
                           throws SAXException {
    
    // Pathway info
    String name = attributes.getValue("name");
    String title = attributes.getValue("title");
    String org = attributes.getValue("org");
    String number = attributes.getValue("number");
    String image = attributes.getValue("image");
    String link = attributes.getValue("link");
    // KGML Entry info 
    String id = attributes.getValue("id");  
    String type = attributes.getValue("type");
    String reaction = attributes.getValue("reaction");
    // KGML Relations info
    String entry1 = attributes.getValue("entry1");
    String entry2 = attributes.getValue("entry2");
    
    
    // auxiliary arrays to store all reactions substrates and products names and ids 
    String[] arraycpd; 
    String[] arrayid;  

    switch(qName){
      //Action performed when pathway tag is found: it is the first one in a KGML file
      case "pathway":
        p = new Pathway(name, title, org, number, image, link);
        break;
      //Action performed when entry tag is found
      case "entry":
        //If a reaction is founded add it to the array
        if(type.equals("gene") && reaction!=null){
            p.getGeneList().put(id, reaction);
        } 
        //If a map is found add it to the corresponding array
        if(type.equals("map")){
            p.getMapLinkList().put(Integer.parseInt(id), name.substring(8));
         }  
        break;
      // Action performed when substrate tag is found
      case "substrate":
          arraycpd = name.split(" ");
          for (int i=0; i<arraycpd.length; i++) {
             p.getCompoundList().add(arraycpd[i]);
          }
          break;
       // Action performed when substrate tag is found
       case "product":
          arraycpd = name.split(" ");
          for (int i=0; i<arraycpd.length; i++) {
             p.getCompoundList().add(arraycpd[i]);
          }
          break;
      //Action performed when realtion tag is found
      case "relation":
        if(type.equals("ECrel")){
            p.getEcrelList().put(Integer.parseInt(entry1), Integer.parseInt(entry2));
        } 
        break;
    }
  }

  /*
    Method triggered when an end tag is found.
    At the end of the pathway tag all pathway information are stored
  */
  @Override
  public void endElement(String uri, String localName, 
                         String qName) throws SAXException {
   switch(qName){
       case "pathway":
           plist.put(p.getNumber(), p);
           break;
   }
  }
}

