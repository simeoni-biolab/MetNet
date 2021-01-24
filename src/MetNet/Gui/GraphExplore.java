/*
    MetNet: comparison of Methabolic Networks
*/
package MetNet.gui;

import MetNet.models.Utils;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/*
Graph visualization: single organism
*/
public class GraphExplore {
    
    private ViewPanel view;
    private Viewer viewer;
    
    /*
    Class constructor
    @param id: organismo
    @param matrix: adjacency matrix of the organism
    @righ: visualization position 0: left part of the screen 1: right part of the screen
    */
    // class constructor
    public GraphExplore(String id,int[][] matrix, int right) throws IOException {
        Graph graph = new SingleGraph("Prima Matrice");
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");


        //graph characteristics 
        graph.addAttribute("ui.stylesheet", styleSheet1);
        graph.addAttribute("layout.weight", GraphParameters.GRAPHWEIGHT);
        graph.addAttribute("layout.force", GraphParameters.GRAPHFORCE);
        graph.setAutoCreate(true);
        graph.setStrict(false);

        viewer = new Viewer(graph,Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.enableAutoLayout();
        view = viewer.addDefaultView(false);
        ((ViewPanel) view).resizeFrame(GraphParameters.ORIZZONTALE, GraphParameters.VERTICALE);
        view.getCamera().setAutoFitView(true);
        ((ViewPanel) view).setAutoscrolls(true);

        JLabel isolated_node = new JLabel("Isolated node");
        JTextField isolated= new JTextField("    ");
        JPanel panel=new JPanel();
        isolated.setEditable(false);
        isolated.setBackground(Color.LIGHT_GRAY);
        JFrame frame= new JFrame(id+"-Metabolic network topology");
        frame.setBackground(Color.white);
        panel.add(isolated_node);
        panel.add(isolated);

        GraphEvent event=new GraphEvent(frame,view);
        event.addScrollbarH();
        event.addScrollbarV();
        event.addSliderZoom(panel);

        frame.add(panel, BorderLayout.NORTH);
        frame.add(view, BorderLayout.CENTER);


        ArrayList<String> Name = new ArrayList<>();
        for (int i = 0; i < Utils.pathListSize(); i++) {
            Name.add(Integer.toString(i));
        }

        //graph creation  
        for (int i = 0; i < matrix.length; i++) {
            //if the node is isolated only the name is added
            if (matrix[i][i] == 0) {
                Node n= graph.addNode(Name.get(i));
                n.addAttribute("ui.style", "fill-color: gray; ");
                n.addAttribute("layout.weight", GraphParameters.NODEWEIGHT);
            } else 
                // if the node is not isolated also other attributes are added
                if (matrix[i][i] >= 0) {
                    for (int j = 0; j < i; j++) {
                        if (matrix[i][j] > 0) {
                            Edge e=graph.addEdge(Name.get(i) +"-"+ Name.get(j), Name.get(i), Name.get(j));
                            graph.getNode(Name.get(i)).addAttribute("layout.weight", GraphParameters.NODEWEIGHT);
                            graph.getNode(Name.get(j)).addAttribute("layout.weight", GraphParameters.NODEWEIGHT);
                            e.addAttribute("layout.weight", GraphParameters.EDGEWEIGHT);
                    }
                }
            }
        }


        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");

        
        int i;
        for (Node node : graph) {
            i= Integer.parseInt(node.getId());
            node.addAttribute("ui.label", Utils.getPathName(i));
        }

        event.addclickEvent(graph);

        view.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
            }

            @Override
            public void mouseMoved(MouseEvent e) {

                GraphicElement cur= view.findNodeOrSpriteAt(e.getX(), e.getY());
                if (cur != null) {
                    Node node = graph.getNode(cur.getId());
                    if (node != null){
                        if(node.getDegree() > 0)
                            view.setToolTipText(Utils.getPathDescription(Integer.parseInt(node.getId())) + " Degree: " + node.getDegree());
                        else
                            view.setToolTipText(Utils.getPathDescription(Integer.parseInt(node.getId())));
                    }
                }
                else
                    view.setToolTipText(null);
            }
        });

        frame.setSize(GraphParameters.ORIZZONTALE,GraphParameters.VERTICALE);

        event.positionGraph(right);
        event.searchNode(panel, graph, 0);

        frame.setVisible(true);
    }

    // graph style parameters
    protected String styleSheet1 =
            "node {" +
                    " text-background-mode: rounded-box;"+
                    "size: 30px;" +
                    "fill-color: lightblue;" +
                    "padding: 10px;" +
                    "}" +
            "node.marked {"+
                    " text-background-mode: rounded-box;"+
                    "size: 30px;" +
                    "fill-color: yellow;" +
                    "padding: 10px;" +
                    "}"+
            "node.arrived {"+
                    " text-background-mode: rounded-box;"+
                    "size: 30px;" +
                    "fill-color: #FF8C00;" +
                    "padding: 10px;" +
                    "}"+
            "edge {" +
                    "fill-color : grey;"+
                    "}"+
            "edge.marked {"+
                    "fill-color : #FF8C00;"+
                    "size: 5px;"+
                    "}";

}





