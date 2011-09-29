package viewer.viewConfigPanel;

import java.io.*;
import java.nio.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.jogamp.opengl.util.*;

import com.jogamp.opengl.util.gl2.*;
import javax.media.opengl.awt.*;

import viewer.*;
import viewer.renderer.*;
import data.*;
import tools.*;

public class TrajectoryPanel extends JPanel
  implements ChangeListener,ActionListener{

  /*accesser starts */
  public void setTrjMode(){
    cbTrjMode.setSelected(vconf.isTrjMode);
  }

  /*accesser ends */

  public void stateChanged(ChangeEvent e){
    if( e.getSource() == cbTrjMode ){
      vconf.isTrjMode=cbTrjMode.isSelected();
   }
  }

  public void actionPerformed( ActionEvent ae ){
    if( ae.getSource() == refreshButton ){
      ctrl.RWinRefresh();
    }else if( ae.getSource() == jbDel ){
      int r = jtable.getSelectedRow();
      if( r < 0 ) delRow();
      else delRow( r );
      reset();
    }else if( ae.getSource() == jbAddAll ){
      setAllatoms();
      reset();
    }else if( ae.getSource() == jbClear ){
      clearTable();
      reset();
    }else if( ae.getSource() == writeButton ){
      writeFile();
    }else if( ae.getSource() == revertButton ){
      readFile();
    }else if( ae.getSource() == editButton ){
      SimpleEditor se=new SimpleEditor(trjFile,this);
    }
    requestFocus();
  }



  Controller ctrl;
  ViewConfig vconf;
  String trjFile;

  //constructor
  public TrajectoryPanel(Controller ctrl){
    this.ctrl=ctrl;
    this.vconf=ctrl.vconf;
    trjFile = ctrl.vconf.configDir+File.separator+"trajectory";
    makePanel();
    readFile();
  }


  public JTable jtable;
  JCheckBox cbTrjMode;
  JButton jbDel;
  JButton jbAddAll;
  JButton jbClear;
  JButton writeButton,revertButton,editButton;
  MyTableModel tableModel;
  JScrollPane sp;
  JButton refreshButton;

  static final String[] colNames = { "Atom", "Type", "Radius","Color"};
  void makePanel(){

    cbTrjMode =new JCheckBox("Trj. Mode",vconf.isTrjMode);
    cbTrjMode.setFocusable(false);
    cbTrjMode.addChangeListener(this);

    jbDel = new JButton( "Del. Row" );
    refreshButton = new JButton( "Repaint" );
    jbDel.setFocusable( false );
    refreshButton.setFocusable( false );
    jbDel.addActionListener( this );
    refreshButton.addActionListener( this );

    jbAddAll = new JButton( "Add All" );
    jbClear = new JButton( "Clear" );
    jbAddAll.setFocusable( false );
    jbClear.setFocusable( false );
    jbAddAll.addActionListener( this );
    jbClear.addActionListener( this );


    writeButton= new JButton( "Save Table");
    writeButton.setFocusable( false );
    writeButton.addActionListener( this );

    revertButton= new JButton( "Revert" );
    revertButton.setFocusable( false );
    revertButton.addActionListener( this );


    editButton= new JButton( "Edit" );
    editButton.setFocusable( false );
    editButton.addActionListener( this );

    tableModel = new MyTableModel( colNames, 0 );
    jtable = new JTable( tableModel );
    jtable.setColumnSelectionAllowed( true );
    jtable.setRowHeight( 25 );
    jtable.setIntercellSpacing( new Dimension(5,5) );

    sp = new JScrollPane( jtable );
    sp.setWheelScrollingEnabled( true );
    //sp.setPreferredSize( new Dimension(500,200) );

    jtable.setFocusable( false );
    jtable.setDefaultRenderer( Color.class, new MyColorRenderer(true) );
    jtable.setDefaultEditor(   Color.class, new MyColorEditor() );

    DefaultTableColumnModel columnModel
      = (DefaultTableColumnModel)jtable.getColumnModel();
    columnModel.getColumn(0).setPreferredWidth(12);
    columnModel.getColumn(1).setPreferredWidth(8);
    columnModel.getColumn(2).setPreferredWidth(3);
    columnModel.getColumn(3).setPreferredWidth(5);



    TableColumn typeColumn = jtable.getColumn( "Type" );
    JComboBox cb = new JComboBox();
    cb.addItem( "LinesPoint" );
    cb.addItem( "Lines" );
    cb.addItem( "WireSphere" );
    cb.addItem( "SolidSphere" );
    typeColumn.setCellEditor( new DefaultCellEditor(cb) );



    SpringLayout layout = new SpringLayout();
    this.setLayout(layout);


    layout.putConstraint(SpringLayout.WEST, cbTrjMode, 10, SpringLayout.WEST, this);
    layout.putConstraint(SpringLayout.NORTH, cbTrjMode, 10, SpringLayout.NORTH, this);

    layout.putConstraint(SpringLayout.WEST, jbDel, 0, SpringLayout.EAST, cbTrjMode);
    layout.putConstraint(SpringLayout.NORTH, jbDel, 0, SpringLayout.NORTH, cbTrjMode);

    layout.putConstraint(SpringLayout.WEST, jbAddAll, 0, SpringLayout.EAST, jbDel);
    layout.putConstraint(SpringLayout.NORTH, jbAddAll, 0, SpringLayout.NORTH, jbDel);

    layout.putConstraint(SpringLayout.WEST, jbClear, 0, SpringLayout.EAST, jbAddAll);
    layout.putConstraint(SpringLayout.NORTH,jbClear, 0, SpringLayout.NORTH, jbAddAll);

    layout.putConstraint(SpringLayout.WEST, refreshButton, 0, SpringLayout.EAST, jbClear);
    layout.putConstraint(SpringLayout.NORTH,refreshButton, 0, SpringLayout.NORTH, jbClear);


    layout.putConstraint(SpringLayout.NORTH, writeButton, 0, SpringLayout.NORTH, refreshButton);
    layout.putConstraint(SpringLayout.WEST, writeButton, 0, SpringLayout.EAST, refreshButton);

    layout.putConstraint(SpringLayout.WEST, revertButton, 0, SpringLayout.EAST, writeButton);
    layout.putConstraint(SpringLayout.NORTH, revertButton, 0, SpringLayout.NORTH,writeButton);

    layout.putConstraint(SpringLayout.WEST, editButton, 0, SpringLayout.EAST, revertButton);
    layout.putConstraint(SpringLayout.NORTH, editButton, 0, SpringLayout.NORTH,revertButton);


    layout.putConstraint(SpringLayout.EAST, sp, -10, SpringLayout.EAST, this);
    layout.putConstraint(SpringLayout.WEST, sp, 10, SpringLayout.WEST, this);
    layout.putConstraint(SpringLayout.SOUTH, sp, -10, SpringLayout.SOUTH, this);
    layout.putConstraint(SpringLayout.NORTH, sp, 10, SpringLayout.SOUTH, writeButton);

    this.add( cbTrjMode);
    this.add( jbDel );
    this.add( jbAddAll );
    this.add( jbClear );
    this.add( refreshButton );
    this.add( writeButton );
    this.add( revertButton );
    this.add( editButton );
    this.add( sp );
  }

  class MyTableModel extends DefaultTableModel {
    public MyTableModel( String[] columnNames, int rowNum ){
      super( columnNames, rowNum );
    }
    public Class getColumnClass(int c){
      return getValueAt(0, c).getClass();
    }
  }


  public void addRow(){
    Object[] o = { new Integer(-1), new String("LinesPoint"),
                   new Float(4.0f),
                   new Color(1f, 1f, 0f, 1.0f) };
    tableModel.addRow( o );
  }
  public void delRow(){
    int p = tableModel.getRowCount();
    if( p > 0 ) tableModel.removeRow( p-1 );
  }
  public void delRow( int p ){
    tableModel.removeRow( p );
  }


  public void setPickedID( int id ){
    if(id<0) return;

    int r;
    boolean exist = false;
    r = jtable.getRowCount();
    for( int i=0; i<r; i++ ){
      int d = ((Integer)jtable.getValueAt( i, 0 )).intValue();
      if( id == d ){
        delRow( i );
        exist = true;
        break;
      }
    }
    if( exist == false ){
      addRow();
      r = jtable.getRowCount();
      tableModel.setValueAt( new Integer(id), r-1, 0 );
    }
    reset();
  }

  public void setAllatoms(){
    RenderingWindow rw=ctrl.getActiveRW();
    for(int id=0;id<rw.atoms.n;id++){
      addRow();
      int r = jtable.getRowCount();
      tableModel.setValueAt( id, r-1, 0 );
    }
  }

  public void clearTable(){
    int p=jtable.getRowCount();
    for(int i=0;i<p;i++) delRow();
  }

  void reset(){
    for( int t=0; t<tableList.size(); t++ ){
      ArrayList<Integer> list = tableList.get(t);
      list.clear();
    }
    tableList.clear();
    for( int i=0; i<jtable.getRowCount(); i++ ){
      ArrayList<Integer> a = new ArrayList<Integer>();
      tableList.add( a );
    }
    if(ctrl.activeRWinID>=0 && ctrl.RWin[ctrl.activeRWinID] != null)
      ctrl.RWin[ctrl.activeRWinID].atoms.resetTrjDList();

  }

  public ArrayList<ArrayList<Integer>> tableList
    = new ArrayList<ArrayList<Integer>>();


  //// file reader
  public void writeFile(){
    try {
      FileWriter fw = new FileWriter( trjFile );
      BufferedWriter bw = new BufferedWriter( fw );
      PrintWriter pw = new PrintWriter( bw );

      for(int i=0;i<tableModel.getRowCount();i++){
        int d = ((Integer)jtable.getValueAt( i, 0 )).intValue();
        pw.println( String.format("%d", d) );
      }

      pw.close();
      bw.close();
      fw.close();
      //System.out.println("saved slice position");
    }
    catch ( IOException ioe ){
    }
  }
  public void readFile(){
    try {
      FileReader fr = new FileReader(trjFile);
      BufferedReader br = new BufferedReader( fr );
      String line;
      Scanner sc;

      //clear table
      clearTable();

      //read
      line = br.readLine();
      while(line!=null){
        //parse
        sc = new Scanner( line );
        int id = sc.nextInt();

        //add
        addRow();
        int r = jtable.getRowCount();
        tableModel.setValueAt( id, r-1, 0 );

        //read next
        line = br.readLine();
      }


      br.close();
      fr.close();
      //System.out.println("read: "+trjFile);
    }
    catch ( IOException e ){
      System.out.println("no read: "+trjFile);
    }
  }


}

//local use
class SimpleEditor extends JFrame implements ActionListener{
  String filePath;
  TrajectoryPanel trj;
  public SimpleEditor(String filePath, TrajectoryPanel trj){
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setTitle("Edit Tracking ID");
    setBounds(new Rectangle(40,40,700,700));
    this.filePath=filePath;
    this.trj=trj;

    add(createPanel());
    setVisible(true);
  }

  public void actionPerformed( ActionEvent e ){
    if( e.getSource() == btnSave ){
      setVisible(false);
      save();
      trj.readFile();
    }
  }

  //save file
  void save(){
    try{
      FileWriter fw = new FileWriter(filePath);
      fw.write(textArea.getText());
      fw.close();
    } catch (FileNotFoundException ex){
      ex.printStackTrace();
    } catch (IOException ex){
      ex.printStackTrace();
    }

  }

  JTextArea textArea;
  JButton btnSave;

  //
  JPanel createPanel(){
    //text area
    textArea = new JTextArea();
    textArea.setFont(new Font("courier",Font.PLAIN,14));

    //set text area
    try{
      FileReader fr = new FileReader(filePath);
      BufferedReader br =new BufferedReader(fr);
      String line;
      while ((line = br.readLine()) != null){
        textArea.append(line + "\n");
      }
      br.close();
    } catch (IOException ex){
      ex.printStackTrace();
    }

    //scroller
    JScrollPane sp = new JScrollPane( textArea );
    sp.setFocusable(false);

    //button
    btnSave=new JButton("save");
    btnSave.addActionListener(this);


    //jpanel
    JPanel jpanel=new JPanel();
    jpanel.setLayout(new BorderLayout(0,0));
    jpanel.add(btnSave,BorderLayout.NORTH);
    jpanel.add(sp,BorderLayout.CENTER);

    return jpanel;
  }
}
