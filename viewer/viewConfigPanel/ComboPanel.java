package viewer.viewConfigPanel;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;

import com.sun.opengl.util.gl2.*;
import javax.media.opengl.awt.*;

import tools.*;
import viewer.*;

public class ComboPanel extends JPanel implements ActionListener{
  Controller ctrl;
  //constructor
  public ComboPanel(Controller ctrl){
    this.ctrl=ctrl;
    jfc = new JFileChooser( new File( System.getProperty("user.dir") ) );
    jfc.addChoosableFileFilter(new MyFilter(".AkiraCmb"));
    jfc.setDialogTitle("Choose \"***.AkiraCmb\"");

    this.createPanel();
  }

  private JButton startButton;
  private JButton stopButton;
  private JButton resetButton;
  private JButton addRowButton;
  private JButton delRowButton;
  private JButton writeButton,loadButton,loadDefaultButton;
  private DefaultTableModel deftableModel;
  private JTable jtable;
  private JScrollPane sp;
  private JComboBox cb;
  private JProgressBar progressBar;

  private static final String[] colNames = { "Step", "Command", "Value" };

  private ArrayList<String> operationStr = new ArrayList<String>();
  private ArrayList<Float>  operationVal = new ArrayList<Float>();

  static final String NEXT_STEP    = "NEXT_STEP";
  static final String OBJ_CENTER_X = "OBJ_CENTER_X";
  static final String OBJ_CENTER_Y = "OBJ_CENTER_Y";
  static final String OBJ_CENTER_Z = "OBJ_CENTER_Z";
  static final String OBJ_SCALE    = "OBJ_SCALE";
  static final String ROTATE_X     = "ROTATE_X";
  static final String ROTATE_Y     = "ROTATE_Y";
  static final String ROTATE_Z     = "ROTATE_Z";
  static final String SAVE_IMAGE   = "SAVE_IMAGE";
  static final String HOME         = "HOME";


  private void createPanel(){
    startButton = new JButton( "Start" );
    startButton.addActionListener( this );
    startButton.setFocusable(false);

    stopButton  = new JButton( "Stop" );
    stopButton.addActionListener( this );
    stopButton.setFocusable(false);

    resetButton = new JButton( "Reset" );
    resetButton.addActionListener( this );
    resetButton.setFocusable(false);

    addRowButton = new JButton( "Add Row" );
    addRowButton.setFocusable(false);
    addRowButton.addActionListener( this );

    delRowButton = new JButton( "Del Row" );
    delRowButton.setFocusable(false);
    delRowButton.addActionListener( this );

    writeButton = new JButton( "Write File" );
    writeButton.addActionListener( this );
    writeButton.setFocusable(false);

    loadButton = new JButton( "Load File" );
    loadButton.addActionListener( this );
    loadButton.setFocusable(false);

    loadDefaultButton = new JButton( "Load Default File" );
    loadDefaultButton.addActionListener( this );
    loadDefaultButton.setFocusable(false);

    deftableModel = new DefaultTableModel( colNames, 0 );
    jtable = new JTable( deftableModel );
    jtable.setFocusable(false);

    jtable.setColumnSelectionAllowed( true );
    DefaultTableColumnModel columnModel
      = (DefaultTableColumnModel)jtable.getColumnModel();
    columnModel.getColumn(0).setPreferredWidth(30);
    columnModel.getColumn(1).setPreferredWidth(120);
    columnModel.getColumn(2).setPreferredWidth(50);

    cb = new JComboBox();
    cb.addItem( NEXT_STEP );
    cb.addItem( OBJ_CENTER_X );
    cb.addItem( OBJ_CENTER_Y );
    cb.addItem( OBJ_CENTER_Z );
    cb.addItem( OBJ_SCALE );
    cb.addItem( ROTATE_X );
    cb.addItem( ROTATE_Y );
    cb.addItem( ROTATE_Z );
    cb.addItem( SAVE_IMAGE );
    cb.addItem( HOME );

    TableColumn table_com = jtable.getColumn( new String("Command") );
    table_com.setCellEditor( new DefaultCellEditor(cb) );

    sp = new JScrollPane( jtable );
    sp.setWheelScrollingEnabled( true );
    sp.setFocusable(false);

    progressBar = new JProgressBar();
    progressBar.setFocusable(false);
    progressBar.setStringPainted( true );
    progressBar.setMinimum(0);


    SpringLayout layout = new SpringLayout();
    this.setLayout( layout );


    layout.putConstraint( SpringLayout.NORTH, startButton, 10,
                          SpringLayout.NORTH, this );
    layout.putConstraint( SpringLayout.WEST, startButton, 10,
                          SpringLayout.WEST, this );

    layout.putConstraint( SpringLayout.NORTH, stopButton, 0,
                          SpringLayout.NORTH, startButton );
    layout.putConstraint( SpringLayout.WEST, stopButton, 2,
                          SpringLayout.EAST, startButton );


    layout.putConstraint( SpringLayout.NORTH, addRowButton, 0,
                          SpringLayout.NORTH, stopButton );
    layout.putConstraint( SpringLayout.WEST, addRowButton, 2,
                          SpringLayout.EAST, stopButton );

    layout.putConstraint( SpringLayout.NORTH, delRowButton, 0,
                          SpringLayout.NORTH, addRowButton );
    layout.putConstraint( SpringLayout.WEST, delRowButton, 2,
                          SpringLayout.EAST, addRowButton );

    layout.putConstraint( SpringLayout.NORTH, resetButton, 0,SpringLayout.NORTH, delRowButton );
    layout.putConstraint( SpringLayout.WEST, resetButton, 2,SpringLayout.EAST, delRowButton);

    layout.putConstraint( SpringLayout.NORTH, writeButton, 0,
                          SpringLayout.NORTH, resetButton );
    layout.putConstraint( SpringLayout.WEST, writeButton, 2,
                          SpringLayout.EAST, resetButton );

    layout.putConstraint( SpringLayout.NORTH, loadButton, 0,
                          SpringLayout.NORTH, writeButton );
    layout.putConstraint( SpringLayout.WEST, loadButton, 0,
                          SpringLayout.EAST, writeButton );

    layout.putConstraint( SpringLayout.NORTH, loadDefaultButton, 0,
                          SpringLayout.NORTH, loadButton );
    layout.putConstraint( SpringLayout.WEST, loadDefaultButton, 0,
                          SpringLayout.EAST, loadButton );

    layout.putConstraint( SpringLayout.EAST, progressBar, -10,SpringLayout.EAST, this );
    layout.putConstraint( SpringLayout.WEST, progressBar, 10,SpringLayout.EAST, loadDefaultButton);
    layout.putConstraint( SpringLayout.NORTH, progressBar, 0,SpringLayout.NORTH, startButton);
    layout.putConstraint( SpringLayout.SOUTH, progressBar, 0,SpringLayout.SOUTH, startButton);

    layout.putConstraint( SpringLayout.SOUTH, sp, -10,SpringLayout.SOUTH, this);
    layout.putConstraint( SpringLayout.EAST, sp, -10,SpringLayout.EAST, this );
    layout.putConstraint( SpringLayout.WEST, sp, 10,SpringLayout.WEST, this );
    layout.putConstraint( SpringLayout.NORTH, sp, 10,SpringLayout.SOUTH, startButton);




    this.add( startButton );
    this.add( stopButton );
    this.add( resetButton );

    this.add( addRowButton );
    this.add( delRowButton );

    this.add( writeButton );
    this.add( loadButton );
    this.add( loadDefaultButton );

    this.add( sp );
    this.add( progressBar );

  }


  private void clearArrayList(){
    operationStr.clear();
    operationVal.clear();
  }

  private void setArrayList(){
    int s = jtable.getRowCount();
    String str;
    for( int i=0; i<s; i++ ){
      str = (String)jtable.getValueAt(i,1);
      if( str != null ){
        operationStr.add( str );
        operationVal.add( Float.valueOf((String)jtable.getValueAt(i,2)).floatValue() );
      }
    }
  }

  private void addTable(){
    int size = operationStr.size();
    String[] s = new String[3];
    for( int i=0; i<size; i++ ){
      s[0] = String.valueOf( i );
      s[1] = new String( operationStr.get(i) );
      s[2] = String.valueOf( operationVal.get(i) );
      deftableModel.addRow( s );
    }
  }
  private void addTable( int p ){
    int size = operationStr.size();
    int ps = deftableModel.getRowCount();
    String[] s = new String[3];
    for( int i=0; i<size; i++ ){
      s[0] = String.valueOf( i+p );
      s[1] = new String( operationStr.get(i) );
      s[2] = String.valueOf( operationVal.get(i) );
      deftableModel.insertRow( i+p, s );
    }
    int np = deftableModel.getRowCount();
    int sp = np - ( ps - p );
    for( int i=sp; i<np; i++ ){
      s[0] = String.valueOf( i );
      deftableModel.setValueAt( s[0], i, 0 );
    }
  }

  private void clearTable(){
    int s = deftableModel.getRowCount();
    for( int i=0; i<s; i++ ){
      deftableModel.removeRow( 0 );
    }
  }

  private void addRow(){
    int p = deftableModel.getRowCount();
    String[] s = new String[3];
    s[0] = String.valueOf( p );
    s[1] = null;
    s[2] = String.valueOf( 1.0 );
    deftableModel.addRow( s );
  }
  private void addRow( int p ){
    int ps = deftableModel.getRowCount();
    String[] s = new String[3];
    s[0] = String.valueOf( p );
    s[1] = null;
    s[2] = String.valueOf( 1.0 );
    deftableModel.insertRow( p, s );
    int np = deftableModel.getRowCount();
    int sp = np - ( ps - p );
    for( int i=sp; i<np; i++ ){
      s[0] = String.valueOf( i );
      deftableModel.setValueAt( s[0], i, 0 );
    }
  }

  private void delRow(){
    int ps = deftableModel.getRowCount();
    if( ps > 0 ){
      deftableModel.removeRow( ps-1 );
    }
  }

  private void delRow( int p ){
    int ps = deftableModel.getRowCount();
    deftableModel.removeRow( p );
    for( int i=p; i<ps-1; i++ ){
      String s = String.valueOf( i );
      deftableModel.setValueAt( s, i, 0 );
    }
  }


  public void actionPerformed( ActionEvent ae ){
    if( ae.getSource() == addRowButton ){
      int r = jtable.getSelectedRow();
      if( r < 0 ) addRow();
      else addRow( r );
    }else if( ae.getSource() == delRowButton ){
      int r = jtable.getSelectedRow();
      if( r < 0 ){
        delRow();
      }
      else {
        delRow( r );
      }
    }else if( ae.getSource() == startButton ){
      if( stopButton.getText().equals("restart") ){
        stopButton.setText("stop");
      }
      start();
    }
    else if( ae.getSource() == stopButton ){
      if( stopButton.getText().equals("stop") ){
        if( isRunning == true ){
          stop();
          stopButton.setText("restart");
        }
      }
      else if( stopButton.getText().equals("restart") ){
        restart();
        stopButton.setText("stop");
      }
    }else if( ae.getSource() == resetButton ){
      clearTable();
      reset();
    }else if( ae.getSource() == writeButton ){
      String path=getSaveFilename();
      writeFile(path);
    }else if( ae.getSource() == loadButton ){
      String path=getOpenFilename();
      read(path);
    }else if( ae.getSource() == loadDefaultButton ){
      read(null);
    }

  }

  private void setSelect( int step ){
    jtable.setRowSelectionInterval( step, step );
  }

  private String getCommand( int step ){
    String s = null;
    if( operationStr.size() > 0  ){
      s = operationStr.get( step );
    }
    else {
      s = new String("NONE");
    }
    return s;
  }
  private float getValue( int step ){
    float f = 0.0f;
    if( operationVal.size() > 0 ){
      f = operationVal.get( step );
    }
    return f;
  }

  private int step;
  private int end;
  private boolean isRunning = false;
  private void start(){
    clearArrayList();
    setArrayList();
    step = 0;
    end = operationStr.size();
    if( end != 0 ){
      progressBar.setMaximum( end-1 );
      isRunning = true;
      ctrl.RWinRefresh();
    }
  }
  private void restart(){
    clearArrayList();
    setArrayList();
    if( step < operationStr.size() ){
      end = operationStr.size();
      progressBar.setMaximum( end-1 );
      isRunning = true;
      ctrl.RWinRefresh();
    }else {
      start();
    }
  }
  private void processProgressBar(){
    jtable.setRowSelectionInterval( step, step );
    progressBar.setValue( step );
    step += 1;
    if( step >= end ) reset();
  }

  private void stop(){
    isRunning = false;
  }


  private void reset(){
    clearArrayList();
    step = 0;
    end = 0;
    progressBar.setMaximum(1);
    progressBar.setValue(0);
    isRunning = false;
  }


  public void running(RenderingWindow RW){
    if( isRunning ){
      String s = getCommand( step );
      float val = getValue( step );
      //System.out.println("val= " + val );

        if( s.equals( NEXT_STEP ) ){
          RW.incrementFrame((int)val);
        }else if( s.equals( OBJ_CENTER_X ) ){
          RW.setObjectCenter(val, 0.f,0.f);
        }else if( s.equals( OBJ_CENTER_Y ) ){
          RW.setObjectCenter(0.f,val,0.f);
        }else if( s.equals( OBJ_CENTER_Z ) ){
          RW.setObjectCenter(0.f,0.f,val);
        }else if( s.equals( OBJ_SCALE ) ){
          RW.keyZoom((int)val);
        }else if( s.equals( ROTATE_X ) ){
          RW.setObjectRotate(val, 0.f, 0.f);
        }else if( s.equals( ROTATE_Y ) ){
          RW.setObjectRotate(0.f,val, 0.f);
        }else if( s.equals( ROTATE_Z ) ){
          RW.setObjectRotate(0.f,0.f,val);
        }else if( s.equals( SAVE_IMAGE ) ){
          RW.writeImage();
        }else if( s.equals( HOME ) ){
          RW.setVPHome();
        }else {
        }
        processProgressBar();
        RW.refresh();
    }//isRunning
  }

    ////////file operation
    JFileChooser jfc;
    private void setFilePath( String path ){
      jfc.setCurrentDirectory( new File(path) );
    }

    private String getOpenFilename(){
      String str = null;
      int s = jfc.showOpenDialog( null );
      if( s == JFileChooser.APPROVE_OPTION ){
        File file = jfc.getSelectedFile();
        str = file.getAbsolutePath();
      }
      return str;
    }

    private String getSaveFilename(){
      String str = null;
      jfc.setSelectedFile(new File("test.AkiraCmb"));
      int s = jfc.showSaveDialog( null );
      if( s == JFileChooser.APPROVE_OPTION ){
        str = jfc.getSelectedFile().getAbsolutePath();
        if(str.endsWith(".AkiraCmb") ==false)str+=".AkiraCmb";
      }else if( s == JFileChooser.ERROR_OPTION ){
      }
      return str;
    }


    private void writeFile(String path){
      try {
        FileWriter fw = new FileWriter( path );
        BufferedWriter bw = new BufferedWriter( fw );
        PrintWriter pw = new PrintWriter( bw );

        setArrayList();
        for(int i=0;i<operationStr.size();i++){
          String str=operationStr.get(i);
          float val=operationVal.get(i);
          pw.println( String.format("%-20s %f", str,val) );
        }

        pw.close();
        bw.close();
        fw.close();
      }catch ( IOException ioe ){
      }
    }
    private void read(String path){
      try {
        BufferedReader br;
        if(path==null){
          br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/viewer/sample.AkiraCmb")));
        }else{
          br = new BufferedReader( new FileReader(path) );
        }
        String line;
        Scanner sc;

        //read
        clearArrayList();
        line = br.readLine();
        while(line !=null){
          sc = new Scanner( line );
          operationStr.add(sc.next());
          operationVal.add(sc.nextFloat());
          line = br.readLine();
        }

        //add table
        clearTable();
        for(int i=0;i<operationStr.size();i++){
          int p = deftableModel.getRowCount();
          String[] s = new String[3];
          s[0] = String.valueOf( i );
          s[1] = operationStr.get(i);
          s[2] = String.valueOf(operationVal.get(i));
          deftableModel.addRow( s );
        }

        br.close();
      }catch ( IOException e ){
        System.out.println("no read: "+path);
      }
    }


  }
