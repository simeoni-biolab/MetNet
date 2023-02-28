/*
    MetNet: comparison of Methabolic Networks
*/
package MetNet.gui;

import MetNet.models.Utils;
import MetNet.models.OrganismsModel;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JList;
import javax.swing.JOptionPane;




/**
 *
 * Form for the organisms selection and download
 */
public class OrganismsView extends javax.swing.JFrame implements PropertyChangeListener {
    private HomeView homeView;
    private OrganismsModel orgModel;
    private MainView parsingView;
    private int riferimento = 0;
    private String org1 = null;
    private String org2 = null;

    /**
     * constructor method: form creation and initialization 
     * @param homeView: launcher (father) of this form
     */
    //   
    public OrganismsView(HomeView homeView) {
        this.homeView = homeView;
        orgModel = new OrganismsModel();
        orgModel.addChangeListener(this);

        // window initialization
        initComponents();        
        organism1.setEditable(false);
        organism2.setEditable(false);
        organism1.setFont(new Font("Courier", Font.PLAIN, 14));
        organism2.setFont(new Font("Courier", Font.PLAIN, 14));
        filesBar.setVisible(false);
        populateOrganismList();
        AddDoubleClick();
        setVisible(true);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }
    
    /*
    Values checked for evt:
    "start": sent when starting the download
    "progress": percentage of the work already done, used to set the download bar
    "stop": sent when the download is finished
    */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getNewValue().equals("start")) {
           disableAll();
           setFilesBarVisible(true);
        }              
        if (evt.getNewValue().equals("progress")) {
            int progress = Integer.parseInt((String)evt.getOldValue());
            setFilesBarValue(progress);
        }
        if (evt.getNewValue().equals("stop")) {
           enableAll();
           setFilesBarValue(0);
           setFilesBarVisible(false);
           setVisible(false);
           parsingView = new MainView(this, org1, org2);
           
        }              
        
    }

    // form normal view
    protected void normalView() {
        organism1.setEditable(false);
        organism2.setEditable(false);
        filesBar.setVisible(false);
        setVisible(true);
        
        
    }
    
    // selecting organisms throud double click on the table's row
    private void AddDoubleClick() {
        organismList.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent evt) {
                JList list = (JList) evt.getSource();
                if (evt.getClickCount() == 2) {
                    int index = list.locationToIndex(evt.getPoint());
                    AddOrg(index);
                }
            }
        });
    }

    // adding the selected organism for the comparison (if possible)
    private void AddOrg(int index) {        
        if (organism1.getText().isEmpty()) 
             organism1.setText(Utils.getOrganism(index).substring(0,Math.min(Utils.getOrganism(index).length(),30)));        
        else if (organism2.getText().isEmpty()) 
             organism2.setText(Utils.getOrganism(index).substring(0,Math.min(Utils.getOrganism(index).length(),30)));
        
        
    }
    
    // the form's table is populated with the organisms list
    private void populateOrganismList() {
        organismList.setFont(new Font("Courier", Font.PLAIN, 14));
        organismList.setListData(Utils.getOrganismList());        
    }

    private void setFilesBarVisible(boolean b){
        this.filesBar.setVisible(b);
    }
    
    private void setFilesBarValue(int progress){
        this.filesBar.setValue(progress);
    }
    

    // disabling all fields and buttons     
    private void disableAll(){
    
        this.nextWindows.setEnabled(false);
        this.organism1.setEnabled(false);
        this.organism2.setEnabled(false);
        this.download1.setEnabled(false);
        this.download2.setEnabled(false);
        this.searchNext.setEnabled(false);
        this.clean1.setEnabled(false);
        this.clean2.setEnabled(false);
        this.organismList.setEnabled(false);
        this.organismo.setEnabled(false);
        this.previous.setEnabled(false);
        this.search.setEnabled(false);            
    }
    
    
    //  enabling all fields and buttons 
    private void enableAll(){
        this.nextWindows.setEnabled(true);
        this.organism1.setEnabled(true);
        this.organism2.setEnabled(true);
        this.download1.setEnabled(true);
        this.download2.setEnabled(true);
        this.searchNext.setEnabled(true);
        this.clean1.setEnabled(true);
        this.clean2.setEnabled(true);
        this.organismList.setEnabled(true);
        this.organismo.setEnabled(true);
        this.previous.setEnabled(true);
        this.search.setEnabled(true);
    }     
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        organismo = new javax.swing.JTextField();
        search = new javax.swing.JButton();
        searchNext = new javax.swing.JButton();
        nextWindows = new javax.swing.JButton();
        filesBar = new javax.swing.JProgressBar();
        jPanel1 = new javax.swing.JPanel();
        organism1 = new javax.swing.JTextField();
        download1 = new javax.swing.JCheckBox();
        clean1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        clean2 = new javax.swing.JButton();
        download2 = new javax.swing.JCheckBox();
        organism2 = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        organismList = new javax.swing.JList<>();
        previous = new javax.swing.JButton();

        jLabel1.setText("jLabel1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel6.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel6.setText("MetNet - Comparison of Metabolic Networks");
        jLabel6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        organismo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                organismoActionPerformed(evt);
            }
        });

        search.setText("Search First");
        search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchActionPerformed(evt);
            }
        });

        searchNext.setText("Search Next");
        searchNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchNextActionPerformed(evt);
            }
        });

        nextWindows.setText("Next");
        nextWindows.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextWindowsActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("First organism"));

        download1.setText("download even if locally present");
        download1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                download1ActionPerformed(evt);
            }
        });

        clean1.setText("Clean");
        clean1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clean1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(organism1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clean1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(download1)
                        .addGap(0, 103, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(organism1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(clean1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(download1)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Second organism"));

        clean2.setText("Clean");
        clean2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clean2ActionPerformed(evt);
            }
        });

        download2.setText("download even if locally present");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(organism2, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clean2, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(download2))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(organism2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(clean2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(download2)
                .addContainerGap())
        );

        organismList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(organismList);

        previous.setText("Previous");
        previous.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(100, 100, 100)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 586, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(filesBar, javax.swing.GroupLayout.PREFERRED_SIZE, 580, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(previous)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(nextWindows))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(176, 176, 176)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 422, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(31, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(organismo, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(search, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(searchNext, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(207, 207, 207))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jPanel1, jPanel2});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {nextWindows, previous});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {filesBar, jScrollPane2});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(organismo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(search)
                    .addComponent(searchNext))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(filesBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nextWindows)
                    .addComponent(previous))
                .addGap(20, 20, 20))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jPanel1, jPanel2});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void clean1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clean1ActionPerformed
        organism1.setText("");
    }//GEN-LAST:event_clean1ActionPerformed

    
    private void organismoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_organismoActionPerformed
    }//GEN-LAST:event_organismoActionPerformed

    private void nextWindowsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextWindowsActionPerformed
        if (organism1.getText().isEmpty() || organism2.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select two organisms to compare");             
        }
        else {
            String app1[] = organism1.getText().split(" ");
            String app2[] = organism2.getText().split(" ");
            org1 = app1[0]; // organism code is the first word in the text
            org2 = app2[0]; 
            // organisms download with the GUI execution
            orgModel.checkFiles(app1[0],app2[0],download1.isSelected(), download2.isSelected());
        }
    }//GEN-LAST:event_nextWindowsActionPerformed

    private void download1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_download1ActionPerformed
    }//GEN-LAST:event_download1ActionPerformed

    private void previousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousActionPerformed
        setVisible(false); 
        homeView.normalView();
    }//GEN-LAST:event_previousActionPerformed

    private void clean2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clean2ActionPerformed
        organism2.setText("");
    }//GEN-LAST:event_clean2ActionPerformed

    private void searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchActionPerformed

        if (!organismo.getText().isEmpty()) {
            for (int i=0; i < Utils.orgListSize();i++) {
                if (Utils.getOrganism(i).contains(organismo.getText())) {
                    organismList.setSelectedIndex(i);
                    organismList.ensureIndexIsVisible(i);
                    riferimento = i;
                    break;
                }      
            }
            
        }            
    }//GEN-LAST:event_searchActionPerformed

    private void searchNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchNextActionPerformed
        if (!organismo.getText().isEmpty()) {
            for (int i=riferimento + 1; i < Utils.orgListSize();i++) {
                if (Utils.getOrganism(i).contains(organismo.getText())) {
                    organismList.setSelectedIndex(i);
                    organismList.ensureIndexIsVisible(i);
                    riferimento = i;
                    break;
                }      
            }
            
        }            
    }//GEN-LAST:event_searchNextActionPerformed

        



    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton clean1;
    private javax.swing.JButton clean2;
    private javax.swing.JCheckBox download1;
    private javax.swing.JCheckBox download2;
    private javax.swing.JProgressBar filesBar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton nextWindows;
    private javax.swing.JTextField organism1;
    private javax.swing.JTextField organism2;
    private javax.swing.JList<String> organismList;
    private javax.swing.JTextField organismo;
    private javax.swing.JButton previous;
    private javax.swing.JButton search;
    private javax.swing.JButton searchNext;
    // End of variables declaration//GEN-END:variables
}
