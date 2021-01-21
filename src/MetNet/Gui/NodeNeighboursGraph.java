/*
    MetNet: comparison of Methabolic Networks
*/
package MetNet.Gui;


import MetNet.Models.Utils;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

/*
Visualization of a selected node and its neighbours on a separate window
*/
public class NodeNeighboursGraph {

    Graph graph;
    Node node;
    int i;
    public char[][] color=new char[Utils.pathListSize()][Utils.pathListSize()];

    private Viewer viewer;
    private ViewPanel view;
    JFrame frame;

    public NodeNeighboursGraph(Node node){
        this.node=node;
        graph= new SingleGraph(node.getId());
        i=Integer.parseInt(node.getId());
        ArrayList<String> Name = new ArrayList<>();
        for (int i = 0; i < Utils.pathListSize(); i++) {
            Name.add(Integer.toString(i));
        }

        //graph creation
        Node n=graph.addNode(Name.get(i));
        if(node.getDegree()>0){
            for(Edge edge: node.getEachEdge()){
                if(edge.getNode1().equals(node)){
                    n=graph.addNode(edge.getNode0().getId());
                }
                else{
                    n=graph.addNode(edge.getNode1().getId());
                }
                graph.addEdge(edge.getId(), edge.getNode0().getId(), edge.getNode1().getId());
            }
        }


        //window creation
        viewer = new Viewer(graph,Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");


        viewer.enableAutoLayout();
        view = viewer.addDefaultView(false);

        frame= new JFrame(Utils.getPathName(Integer.parseInt(graph.getId())) + " - " + Utils.getPathDescription(Integer.parseInt(graph.getId())));
        frame.add(view, BorderLayout.CENTER);
        frame.setSize(600,500);

        //right screen
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
        int x = (int) rect.getMaxX() - frame.getWidth();
        int y = 0;
        frame.setLocation(x, y);
        view.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
            }

            @Override
            public void mouseMoved(MouseEvent e) {               
                GraphicElement cur= view.findNodeOrSpriteAt(e.getX(), e.getY());
                if (cur != null) {
                    Node node = graph.getNode(cur.getId());
                    if (node != null) {
                        if(node.getDegree() > 1)
                            view.setToolTipText(Utils.getPathDescription(Integer.parseInt(node.getId())) + " Degree: " + node.getDegree());
                        else
                            view.setToolTipText(Utils.getPathDescription(Integer.parseInt(node.getId())));
                    }
                }
                else
                    view.setToolTipText(null); 
            }
        });
    }

    // display single graph
    public void Display(){
        for(Node node: graph.getEachNode()){
            i=Integer.parseInt(node.getId());
            node.addAttribute("ui.label", Utils.getPathName(i));
        }
        graph.addAttribute("ui.stylesheet", styleSheet);
        frame.setVisible(true);
    }

    // display merged graph
    public void Display(char[][] color, String a, String b, int n_color){

        JLabel red_label= new JLabel();
        JLabel green_label= new JLabel("shared");
        JTextField red_text = new JTextField("   ");
        red_text.setEditable(false);
        red_text.setBackground(Color.decode("#FF8300")); // Color.orange
        JTextField green_text = new JTextField("   ");
        green_text.setEditable(false);
        green_text.setBackground(Color.decode("#00DA00")); //Color.green
        JLabel blue_label= new JLabel();
        JTextField blue_text = new JTextField("   ");
        blue_text.setEditable(false);        
        blue_text.setBackground(Color.decode("#0096FF")); // Color.blue
        JPanel panel=new JPanel();
        panel.add(green_label);
        panel.add(green_text);
        panel.add(red_label);
        panel.add(red_text);

        if(n_color==2){
            red_label.setText("not shared");
        }
        else{
            red_label.setText(b);
            blue_label.setText(a);
            panel.add(blue_label);
            panel.add(blue_text);
        }


        this.color=color;
        graph.addAttribute("ui.stylesheet", styleSheetMerge);

        for(Node n: graph.getEachNode()){
            if(color[Integer.parseInt(n.getId())][Integer.parseInt(n.getId())]=='R'){
                n.addAttribute("ui.class", "redNode");
            }
            if(color[Integer.parseInt(n.getId())][Integer.parseInt(n.getId())]=='B'){
                n.addAttribute("ui.class", "blueNode");
            }
        }

        for (int j = 0; j < Utils.pathListSize(); j++) {
            if (color[i][j] != '\0') {
                Edge e = graph.getEdge(node.getId() +"-"+ j);
                if (e != null) {
                    if (color[i][j] == 'R')
                        e.addAttribute("ui.class", "redEdge");
                    if (color[i][j] == 'B')
                        e.addAttribute("ui.class", "blueEdge");
                }
            }
            else{
                Edge e = graph.getEdge(j+"-"+node.getId());
                if (e != null) {
                    if (color[j][i] == 'R')
                        e.addAttribute("ui.class", "redEdge");
                    if (color[j][i] == 'B')
                        e.addAttribute("ui.class", "blueEdge");
                }
            }
        }

        for(Node n: graph.getEachNode()){
            i=Integer.parseInt(n.getId());
            n.addAttribute("ui.label", Utils.getPathName(i));
        }

        frame.add(panel, BorderLayout.NORTH);
        frame.setVisible(true);


    }

    protected String styleSheet =
            "node {" +
                    " text-background-mode: rounded-box;"+
                    "	fill-color: lightblue;" +
                    "   size: 30px; " +
                    "}"+
            "edge {" +
                    "shape: cubic-curve;"+
                    "fill-color : grey;"+
                    "}";

    protected String styleSheetMerge =
            "node {" +
                    " text-background-mode: rounded-box;"+
                    " fill-color: #00DA00;" + //green
                    "   size: 30px; " +
                    "}"+
            "node.redNode {" +
                    " text-background-mode: rounded-box;"+
                    " fill-color: #FF8300;" + //orange
                    "   size: 30px; " +
                    "}"+
            "node.blueNode {" +
                    " text-background-mode: rounded-box;"+
                    " fill-color: #0096FF;" + // blue
                    "   size: 30px; " +
                    "}"+
            "edge {" +
                    "shape: cubic-curve;"+
                    "fill-color : green;"+
                    "}"+
            "edge.redEdge {" +
                    "shape: cubic-curve;"+
                    "fill-color : orange;"+
                    "}"+
            "edge.blueEdge {" +
                    "shape: cubic-curve;"+
                    "fill-color : #00BAFF;"+ //light blue
                    "}";
}


