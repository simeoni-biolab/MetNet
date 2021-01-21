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
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

/*
class for the visualization of the combined topology of the two selected organisms
*/

public class MergeGraphMoreColor {

    // colored representation of the two graphs
    private char[][] color = new char[Utils.pathListSize()][Utils.pathListSize()]; 
    private Viewer viewer;
    private ViewPanel view;
    private Node press;

    /*
    class constructor
    @param a: first organism
    @param b: second organism
    @param matrix1: adjacency matrix of the first organism
    @param matrix2: adjacency matrix of the second organism
    */ 
    public MergeGraphMoreColor(String a, String b, int[][] matrix1, int[][] matrix2) throws IOException {

        Graph graphMerge = new SingleGraph(a + "-" + b + " topology comparison");

        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");


        graphMerge.addAttribute("ui.stylesheet", styleSheet);
        graphMerge.addAttribute("layout.weight", GraphParameters.GRAPHWEIGHT);
        graphMerge.addAttribute("layout.force", GraphParameters.GRAPHFORCE); 
        graphMerge.setAutoCreate(true);
        graphMerge.setStrict(false);
        //visualizzazione grafo
        viewer = new Viewer(graphMerge, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.enableAutoLayout();
        view = viewer.addDefaultView(false);
        ((ViewPanel) view).resizeFrame(800, 800);
        view.getCamera().setAutoFitView(true);
        ((ViewPanel) view).setAutoscrolls(true);

        JLabel red_label= new JLabel(b);
        JTextField red_text = new JTextField("   ");
        red_text.setEditable(false);
        red_text.setBackground(Color.decode("#FF8300")); //Color.orange
        
 

        JLabel green_label= new JLabel("shared");
        JTextField green_text = new JTextField("   ");
        green_text.setEditable(false);
        green_text.setBackground(Color.decode("#00DA00")); //Color.green
 
        JLabel blue_label= new JLabel(a);
        JTextField blue_text = new JTextField("   ");
        blue_text.setEditable(false);
        blue_text.setBackground(Color.decode("#0096FF")); //Color.blue

        JPanel panel=new JPanel();
        panel.add(red_label);
        panel.add(red_text);
        panel.add(blue_label);
        panel.add(blue_text);
        panel.add(green_label);
        panel.add(green_text);

        JFrame frame = new JFrame(a + "-" + b + " topology comparison");

        frame.add(panel, BorderLayout.NORTH);
        frame.add(view, BorderLayout.CENTER);


        ArrayList<String> Name = new ArrayList<>();
        for (int i = 0; i < Utils.pathListSize(); i++) {
            Name.add(Integer.toString(i));
        }


        //crazione nodi
        for (int i = 0; i < matrix1.length; i++) {
            if ((matrix1[i][i] >= 0 && matrix2[i][i] == -1) || (matrix1[i][i] == -1 && matrix2[i][i] >= 0)) {
                //uno dei due nodi non esiste
                Node node = graphMerge.addNode(Name.get(i));
                node.addAttribute("layout.weight", GraphParameters.NODEWEIGHT);
                if (matrix1[i][i] >= 0){
                    color[i][i]='B';
                    node.addAttribute("ui.class", "matrix1");
                }
                else{
                    color[i][i]='R';
                    node.addAttribute("ui.class", "matrix2");
                }
            }
            if ((matrix1[i][i] >= 0 && matrix2[i][i] >= 0)) {
                Node node = graphMerge.addNode(Name.get(i));
                node.addAttribute("layout.weight", GraphParameters.NODEWEIGHT);
                color[i][i]='V';
            }
        }

        //creazione archi
        for (int i = 0; i < matrix1.length; i++)
            for (int j = 0; j < i; j++) {
                if (matrix1[i][j] > 0 && matrix2[i][j] > 0) {
                    Edge e = graphMerge.addEdge(Name.get(i) +"-"+ Name.get(j), Name.get(i), Name.get(j));
                    e.addAttribute("layout.weight", GraphParameters.EDGEWEIGHT);
                    color[i][j]='V';
                } else if (((matrix1[i][j] == 0 && matrix2[i][j] > 0) || (matrix1[i][j] > 0 && matrix2[i][j] == 0))) {
                    Edge e = graphMerge.addEdge(Name.get(i) +"-"+ Name.get(j), Name.get(i), Name.get(j));
                    if (matrix1[i][j] > 0){
                        e.addAttribute("ui.class", "matrix1");
                        color[i][j]='B';
                    }
                    else{
                        e.addAttribute("ui.class", "matrix2");
                        color[i][j]='R';
                    }
                    e.addAttribute("layout.weight", GraphParameters.EDGEWEIGHT);
                    graphMerge.getNode(Name.get(i)).addAttribute("layout.weight", GraphParameters.NODEWEIGHT);
                    graphMerge.getNode(Name.get(j)).addAttribute("layout.weight", GraphParameters.NODEWEIGHT);
                }
            }

        graphMerge.addAttribute("ui.antialias");

        for (Node node : graphMerge) {
            int i = Integer.parseInt(node.getId());
            node.addAttribute("ui.label", Utils.getPathName(i));
        }

        GraphEvent event=new GraphEvent(frame, view, color, a, b);

        event.addclickEvent(graphMerge, 3);
        event.addScrollbarH();
        event.addScrollbarV();
        event.addSliderZoom(panel);
        event.searchNode(panel, graphMerge, 1);

        view.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                GraphicElement cur= view.findNodeOrSpriteAt(e.getX(), e.getY());
                if (cur != null) {
                    Node node = graphMerge.getNode(cur.getId());
                    if (node != null){
                        if(node.getDegree()>0)
                            view.setToolTipText(Utils.getPathDescription(Integer.parseInt(node.getId())) + " Degree: " + node.getDegree());
                        else
                            view.setToolTipText(Utils.getPathDescription(Integer.parseInt(node.getId())));
                    }
                }
            }
        });


        frame.setSize(800,750);
        frame.setVisible(true);


    }

    //colore per diversi tipi di nodi e archi
    protected String styleSheet =
            "node {" +
                    " text-background-mode: rounded-box;"+
                    "size: 30px;" +
                    "fill-color:#00DA00;" + //green
                    "padding: 10px;" +
                    "}" +
            "edge {" +
                    "fill-color : green;" +
                    "padding: 10px;" +
                    "}" +
            "node.matrix1 {"+
                    "size: 30px;" +
                    " text-background-mode: rounded-box;"+
                    "fill-color: #0096FF;" + //blue
                    "padding: 10px;" +
                    "}"+
            "edge.matrix1 {" +
                    "fill-color : #00BAFF;"+ //light blue
                    "padding: 10px;" +
                    "}"+
            "node.matrix2 {"+
                    " text-background-mode: rounded-box;"+
                    "size: 30px;" +
                    "fill-color: #FF8300;" + //orange
                    "padding: 10px;" +
                    "}"+
            "edge.matrix2 {" +
                    "fill-color : orange;"+
                    "padding: 10px;" +
                    "}"+
            "node.pressed {"+
                    " text-background-mode: rounded-box;"+
                    "size: 30px;" +
                    "fill-color: #006400;" +
                    "}"+
            "edge.pressed {" +
                    "size: 5px;"+
                    "fill-color : #006400;"+
                    "}"+
            "node.matrix1Pressed {"+
                    " text-background-mode: rounded-box;"+
                    "size: 30px;" +
                    "fill-color: #00008B;" +
                    "}"+
            "edge.matrix1Pressed {" +
                    "size: 5px;"+
                    "fill-color : #00008B;"+
                    "}"+
            "node.matrix2Pressed {"+
                    " text-background-mode: rounded-box;"+
                    "size: 30px;" +
                    "fill-color: #8B0000;" +
                    "}"+
            "edge.matrix2Pressed {" +
                    "size: 5px;"+
                    "fill-color : #8B0000;"+
                    "}";

}
