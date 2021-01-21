/*
    MetNet: comparison of Methabolic Networks
*/
package MetNet.Gui;

import MetNet.Models.Utils;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.view.Camera;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.util.DefaultMouseManager;
import org.graphstream.ui.view.util.MouseManager;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;


/*
Class for graph events and functionalities
*/
public class GraphEvent {
    private JFrame frame;
    private View view;
    private Camera camera;
    private char[][] color; // adjacency matrix
    private String org1;
    private String org2;

    private Scrollbar scroll_Horizontal=new Scrollbar(Scrollbar.HORIZONTAL,0,GraphParameters.ORIZZONTALE,0,GraphParameters.ORIZZONTALE);;
    private int pos_scroll_H;  // saving the scroll position
    private int visible_amount_H;  // saving the visible amount of the window
    
    private Scrollbar scroll_Vertical=new Scrollbar(Scrollbar.VERTICAL, 0, GraphParameters.VERTICALE, 0, GraphParameters.VERTICALE);;
    private int pos_scroll_V;  // saving the scroll position
    private int visible_amount_V; // saving the visible amount of the window 

    private JSlider slider;    // to set the zoom
    private Point3 newcenter;  // keeps track of the center
    private Node press = null; // highlighted node

    
    /*
    class constructor: single graph
    */
    public GraphEvent(JFrame frame, View view){
        this.frame=frame;
        this.view=view;
        camera=view.getCamera();
        newcenter = camera.getViewCenter();
    }

    /*
    class constructor: merged graph
    */
    public GraphEvent(JFrame frame, View view, char[][] color, String a, String b){
        this.frame=frame;
        this.view=view;
        camera=view.getCamera();
        newcenter = camera.getViewCenter();
        this.color=color;
        org1=a; org2=b;
    }
    
    //orizontal scrollbar 
    public void addScrollbarH(){

        pos_scroll_H=scroll_Horizontal.getValue(); // initial position
        visible_amount_H = scroll_Horizontal.getVisibleAmount();
        scroll_Horizontal.setBackground(Color.darkGray);

        scroll_Horizontal.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {

                int actualPosition = scroll_Horizontal.getValue();
                double zoom = (1 + slider.getValue()/100D) * 1.2;
                double diffx = ((double)(actualPosition - pos_scroll_H) / (double)(GraphParameters.ORIZZONTALE - visible_amount_H)) * zoom;

                camera.setViewCenter(newcenter.x + diffx, newcenter.y,newcenter.z);
                pos_scroll_H = actualPosition;
            }
        });

        frame.add(scroll_Horizontal, BorderLayout.SOUTH);
    }


    //vertical scrollbar 
    public void addScrollbarV(){

        pos_scroll_V=scroll_Vertical.getValue(); // initial position
        visible_amount_V = scroll_Vertical.getVisibleAmount();
        scroll_Vertical.setBackground(Color.darkGray);

        scroll_Vertical.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                            
                int actualPosition = scroll_Vertical.getValue();
                double zoom = (1 + slider.getValue()/100D) * 1.2;

                double diffy = ((double)(pos_scroll_V - actualPosition) / (double)(GraphParameters.VERTICALE - visible_amount_V)) * zoom;                             
                camera.setViewCenter(newcenter.x,newcenter.y + diffy,newcenter.z);                
                pos_scroll_V = actualPosition;
            }

        });

        frame.add(scroll_Vertical, BorderLayout.LINE_END);

    }

    //zoom slider 
    public void addSliderZoom(JPanel panel){
        slider=new JSlider(JSlider.HORIZONTAL,0, 50, 0);

        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {

                if(slider.getValue()==0){
                    camera.resetView();
                    newcenter = camera.getViewCenter();
                    scroll_Horizontal.setVisibleAmount(GraphParameters.ORIZZONTALE);
                    visible_amount_H = GraphParameters.ORIZZONTALE;
                    pos_scroll_H = 0;
                    scroll_Vertical.setVisibleAmount(GraphParameters.VERTICALE);
                    visible_amount_V = GraphParameters.VERTICALE;
                    pos_scroll_V = 0;
                }
                else{
                    float zoom = slider.getValue()/100F;
                    camera.setViewPercent(1-zoom);
                    newcenter = camera.getViewCenter();
                    // visible graph portion calculation 
                    int new_visible_amount_H=((100-slider.getValue())*GraphParameters.ORIZZONTALE)/100;
                    int new_visible_amount_V = ((100-slider.getValue())*GraphParameters.VERTICALE)/100;
                    
                    
                    // positioning the horizontalbar
                    //(pos_scroll_H + metaVisibileH) = (nuova_posH + nuovametaVisibileH)
                    int nuova_posH = pos_scroll_H + (visible_amount_H - new_visible_amount_H)/2;     
                     
                    
                    scroll_Horizontal.setVisibleAmount(new_visible_amount_H);
                    // ATTENTION: nuova_posH can be out of bounds: setValue adjusts it!
                    scroll_Horizontal.setValue(nuova_posH); // setting new bar position
                    visible_amount_H = new_visible_amount_H; // update visible dimension
                    pos_scroll_H = scroll_Horizontal.getValue(); // keeping the right value back

                    // positioning the vertical scrollbar                                        
                    int nuova_posV = pos_scroll_V + (visible_amount_V - new_visible_amount_V)/2;                   
 
                   
                    scroll_Vertical.setVisibleAmount(new_visible_amount_V);
                    // ATTENTION: nuova_posV can be out of bounds: setValue adjusts it!                    
                    scroll_Vertical.setValue(nuova_posV); // setting the new bar position
                    visible_amount_V = new_visible_amount_V; // update visible dimension
                    pos_scroll_V = scroll_Vertical.getValue(); // update position; keeping the ritht value
 
                }
            }
        });
        panel.add(slider);

    }

    //window positioning and visualization  
    public void positionGraph(int right){

        frame.setLocationRelativeTo(null);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();

        if(right==1){
            int x = (int) rect.getMaxX() - frame.getWidth();
            int y = 0;
            frame.setLocation(x, y);
        }
        else{
            int x = (int) rect.getMinX();
            int y = 0;
            frame.setLocation(x, y);
        }
    }


    //zoom call on click event
    private void nodeNeighbours(Graph graph, int x, int y, int type){
        GraphicElement curElement = view.findNodeOrSpriteAt(x, y);
        if (curElement != null) {
            Node node = graph.getNode(curElement.getId());
            if(node != null) {
                NodeNeighboursGraph nngraph= new NodeNeighboursGraph(node);
                if(type==1)
                    nngraph.Display();
                else
                    nngraph.Display(color, org1, org2, type);
            }
        }
    }

    //single graph case: managing click, zoom and highlighting
    public void addclickEvent(Graph graph){
        MouseManager manager = new DefaultMouseManager() {

            @Override
            public void mouseClicked(MouseEvent event) {
                Node p = null;
                
                super.mouseClicked(event);
                curElement = view.findNodeOrSpriteAt(event.getX(), event.getY());
                if(curElement != null) {// clicked on a node   
                  p = graph.getNode(curElement.getId());
                  if (p != null) {// it is actually a node
                     if(press == null) { // no other node clicked, highligh this node
                       press = p;
                       addColor(p); 
                     }
                     else // another node clicked: if it is the same node, remove highlighting                                                                   
                         if(press.getId()==p.getId() && event.getClickCount()==1){ //node already selected 
                            removeColor(p);
                            press = null;
                         }                                    
                     if (event.getClickCount()==2) // doppio click                    
                         nodeNeighbours(graph, event.getX(), event.getY(), 1);
                
                  }
                }
            }
        };
        view.setMouseManager(manager);
    }
    
    // managing click, zoom and highlighting on a merged graph
    public void addclickEvent(Graph graph, int type){
        MouseManager manager = new DefaultMouseManager() {
            @Override
            public void mouseClicked(MouseEvent event) {
                Node p = null;
                
                super.mouseClicked(event);
                curElement = view.findNodeOrSpriteAt(event.getX(), event.getY());
                if(curElement != null) {// clicked on a node   
                  p = graph.getNode(curElement.getId());
                  if (p != null) {// it is actually a node
                     if(press == null) { // no other node clicked, highligh this node
                       press = p;
                       int i = Integer.parseInt(p.getId());
                       for (int j = 0; j < Utils.pathListSize(); j++) {
                           Edge e;
                           if (color[i][j] != '\0') {
                              e = graph.getEdge(p.getId() + "-"+ Integer.toString(j));
                              addColor(e, i, j);
                           } else {                              
                               e = graph.getEdge(Integer.toString(j) + "-"+p.getId());
                               addColor(e, j, i);
                           }
                       }
                       if (color[Integer.parseInt(p.getId())][Integer.parseInt(p.getId())] == 'R')                                
                           p.addAttribute("ui.class", "matrix2Pressed");
                       else if(color[Integer.parseInt(p.getId())][Integer.parseInt(p.getId())] == 'B')
                           p.addAttribute("ui.class", "matrix1Pressed");
                       else
                           p.addAttribute("ui.class", "pressed");
                     }

                     else // another node clicked: if it is the same node, remove highlighting                                                                   
                         if(press.getId()==p.getId() && event.getClickCount()==1){ //node already selected 
                            int i = Integer.parseInt(press.getId());
                            for (int j = 0; j < Utils.pathListSize(); j++) {
                                if (graph.getEdge(press.getId() +"-"+ j)!= null) {
                                    removeColor(graph.getEdge(press.getId() +"-"+ j), i, j);
                                }
                                if(graph.getEdge(j+"-"+press.getId())!= null){
                                    removeColor(graph.getEdge(j+"-"+press.getId()), j, i);
                                }
                            }
                            if (color[Integer.parseInt(press.getId())][Integer.parseInt(press.getId())] == 'R') {
                                press.addAttribute("ui.class", "matrix2");
                            } else if (color[Integer.parseInt(press.getId())][Integer.parseInt(press.getId())] == 'B') {
                                press.addAttribute("ui.class", "matrix1");
                            }else{
                                press.removeAttribute("ui.class");
                            }
                            press = null;
                         }                                    
                     
                     if (event.getClickCount()==2) // doppio click                    
                         nodeNeighbours(graph, event.getX(), event.getY(), type);
                
                  }
                }                
            }
        };
        
        view.setMouseManager(manager);
    }
            
    
    public void searchNode(JPanel panel, Graph graph, int number){
        JLabel search=new JLabel("Search node");
        JTextField text=new JTextField("00000");
        panel.add(search);
        panel.add(text);

        text.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input= text.getText();
                if(input!=null){
                    Node s=search(input,graph);
                    if (s == null) {
                        JOptionPane.showMessageDialog(panel, "Node " + input + " not found");
                        return ;
                    }
                    if (press != null) // there is a node already selected  
                        removeColor(press); // remove highlighting
                    press = s; // highlight the searched one
                        
                    if(number==0){
                        addColor(s);
                    }
                    else{
                        if (color[Integer.parseInt(s.getId())][Integer.parseInt(s.getId())] == 'R') {
                            s.addAttribute("ui.class", "matrix2Pressed");
                        } else if (color[Integer.parseInt(s.getId())][Integer.parseInt(s.getId())] == 'B') {
                            s.addAttribute("ui.class", "matrix1Pressed");
                        }else{
                            s.addAttribute("ui.class", "pressed");
                        }
                        for(Edge edge: s.getEachEdge()){
                            int i=Integer.parseInt(edge.getNode1().getId());
                            int j=Integer.parseInt(edge.getNode0().getId());
                            if(color[i][j]!='\0')
                                addColor(edge, i, j);
                            if(color[j][i]!='\0')
                                addColor(edge,j,i);
                        }
                    }
                }
                text.setText("00000");
            }
        });
    }
    
    // searching a node in the graph
    private Node search(String input,Graph graph){
        Node search=null;
        int j=0;
        while (j<Utils.pathListSize() && !Utils.getPathName(j).equals(input))
            j++;
        if (j<Utils.pathListSize()) // trovato
           search=graph.getNode(Integer.toString(j));
        return search;
    }

    
    private void addColor(Edge e, int i, int j){
        if (e != null) {
            if (color[i][j] == 'R')
                e.addAttribute("ui.class", "matrix2Pressed");
            else if (color[i][j] == 'B')
                e.addAttribute("ui.class", "matrix1Pressed");
            else 
                e.addAttribute("ui.class", "pressed");
        }
    }

    private void removeColor(Edge e, int i, int j){
        if (e != null) {
            if (color[i][j] != '\0' && color[i][j] == 'R') {
                e.addAttribute("ui.class", "matrix2");
            } else if(color[i][j] != '\0' && color[i][j] == 'B'){
                e.addAttribute("ui.class", "matrix1");
            }
            else
                e.removeAttribute("ui.class");
        }
    }

    private void removeColor(Node p){
        if(p != null && p.getDegree()>0){
            for(Edge edge: p.getEachEdge()){
                edge.removeAttribute("ui.class");
                edge.getNode1().removeAttribute("ui.class");
                edge.getNode0().removeAttribute("ui.class");
            }
        }
        p.removeAttribute("ui.class");
    }

    private void addColor(Node p){
        if (p!= null) {
            p.addAttribute("ui.class", "arrived");
            if(p.getDegree()>0){
                for(Edge edge: p.getEachEdge()){
                    edge.addAttribute("ui.class", "marked");
                    edge.getNode1().addAttribute("ui.class", "arrived");
                    edge.getNode0().addAttribute("ui.class", "arrived");
                }
            }
        }
    }

}

