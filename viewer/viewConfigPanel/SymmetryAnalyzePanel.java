package viewer.viewConfigPanel;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.jogamp.opengl.util.*;

import com.jogamp.opengl.util.gl2.*;
import javax.media.opengl.awt.*;

import data.*;
import tools.*;
import viewer.*;
import viewer.renderer.*;
import jspglib.JSpgLib;

public class SymmetryAnalyzePanel extends JPanel implements ActionListener{

  private Controller ctrl;
  private ViewConfig vconf;
  public SymmetryAnalyzePanel(Controller ctrl){
    this.ctrl=ctrl;
    this.vconf=ctrl.vconf;
    createPanel();
  }

  private JButton getPrimitiveButton,getSymmetryButton,getSymbolButton,refineCellButton,getMultiplicityButton;

  private void createPanel(){
    getPrimitiveButton=new JButton("Get Primitive");
    getPrimitiveButton.setFocusable(false);
    getPrimitiveButton.addActionListener( this );

    getSymmetryButton=new JButton("Get Symmetry");
    getSymmetryButton.setFocusable(false);
    getSymmetryButton.addActionListener( this );

    getSymbolButton=new JButton("Get Symbol");
    getSymbolButton.setFocusable(false);
    getSymbolButton.addActionListener( this );

    refineCellButton=new JButton("Refine Cell");
    refineCellButton.setFocusable(false);
    refineCellButton.addActionListener( this );

    getMultiplicityButton=new JButton("Get Multiplicity");
    getMultiplicityButton.setFocusable(false);
    getMultiplicityButton.addActionListener( this );

    this.setLayout(new GridLayout(0,5));

    add(getPrimitiveButton);
    add(getSymmetryButton);
    add(getSymbolButton);
    add(refineCellButton);
    add(getMultiplicityButton);
    requestFocusInWindow();
  }


  public void actionPerformed( ActionEvent e ){
    if(ctrl.getActiveRW()==null)return;
    viewer.renderer.Atoms atoms =ctrl.getActiveRW().getAtoms();

    double[] lattice =new double[9];
    for(int i=0;i<3;i++){
      for(int j=0;j<3;j++){
        lattice[3*i+j]=atoms.h[i][j];
      }
    }
    double[] position =new double[atoms.n*3];
    int[] types = new int[atoms.n];
    for(int i=0;i<atoms.n;i++){
      types[i]=atoms.tag[i];
      for(int j=0;j<3;j++){
        position[3*i+j]=atoms.r[i][j];
      }
    }
    int num_atom = atoms.n;
    double symprec = 1e-5;

    if( e.getSource() == getPrimitiveButton){
      findPrimitive(lattice,position,types,num_atom,symprec);
    }else if( e.getSource() == getSymmetryButton){
      getSymmetry(lattice,position,types,num_atom,symprec);
    }else if( e.getSource() == getSymbolButton){
      getSymbol(lattice,position,types,num_atom,symprec);
    }else if( e.getSource() == refineCellButton){
      refineCell(lattice,position,types,num_atom,symprec);
    }else if( e.getSource() == getMultiplicityButton){
      getMultiplicity(lattice,position,types,num_atom,symprec);
    }
  }


  private void findPrimitive(double[] lattice, double[] position, int[] types, int num_atom, double symprec){

    JSpgLib jspg = new JSpgLib();

    System.out.println("Find primitive");
    int num_primitive_atom=jspg.findPrimitive(lattice,position,types,num_atom,symprec);
    for(int i=0;i<3;i++)
      System.out.println(String.format("%f %f %f", lattice[0+3*i], lattice[1+3*i], lattice[2+3*i]));

    System.out.println("Atomic positions:");
    for (int i=0; i<num_primitive_atom; i++) {
      System.out.println(String.format("%d: %f %f %f", types[i], position[i*3+0], position[i*3+1],position[i*3+2]));
    }
    jspg=null;
  }


  private void getSymbol(double[] lattice, double[] position, int[] types, int num_atom, double symprec){

    JSpgLib spg = new JSpgLib();
    System.out.println("*** Symbol of InterNational ***");
    String in=spg.getInterNational(lattice,position,types,num_atom,symprec);
    System.out.println(in);

    System.out.println("*** Symbol of Shoenflies ***");
    String sh=spg.getShoenflies(lattice,position,types,num_atom,symprec);
    System.out.println(sh);

    System.out.println("*** Database***");
    spg.getDataset(lattice,position,types,num_atom,symprec);
    spg=null;
  }


  private void refineCell(double[] lattice, double[] position, int[] types, int num_atom, double symprec){
    System.out.println("*** Refine Cell ***");

    JSpgLib spg = new JSpgLib();
    int num_atom_bravais=spg.refineCell(lattice,position,types,num_atom,symprec);

    System.out.println("Lattice parameter:");
    for (int i = 0; i < 3; i++ ) {
      System.out.println(String.format("%f %f %f", lattice[0+3*i], lattice[1+3*i], lattice[2+3*i]));
    }
    System.out.println("Atomic positions:");
    for (int  i = 0; i<num_atom_bravais; i++ ) {
      System.out.println(String.format("%d: %f %f %f", types[i], position[i*3+0], position[i*3+1],position[i*3+2]));
    }
    spg=null;
  }


  private void getSymmetry(double[] lattice, double[] position, int[] types, int num_atom, double symprec){
    int max_size = 50;
    int[] rotation=new int[max_size*3*3];
    double[] translation=new double[max_size*3];

    JSpgLib spg = new JSpgLib();
    System.out.println("*** Symmetry ***");
    int size=spg.getSymmetry(rotation,translation,max_size,
                             lattice,position,types,num_atom,symprec);

    for (int i = 0; i < size; i++) {
      System.out.println(String.format("--- %d ---", i + 1));
      for (int j = 0; j < 3; j++)
        System.out.println(String.format("%2d %2d %2d", rotation[i*3*3+j*3+0], rotation[i*3*3+j*3+1], rotation[i*3*3+j*3+2]));
      System.out.println(String.format("%f %f %f", translation[i*3+0], translation[i*3+1],translation[i*3+2]));
    }
    spg=null;
  }

  private void getMultiplicity(double[] lattice, double[] position, int[] types, int num_atom, double symprec){
    JSpgLib spg = new JSpgLib();
    int size=spg.getMultiplicity(lattice,position,types,num_atom,symprec);
    System.out.println("*** Get Multiplicity ***");
    System.out.println(String.format("Number of symmetry operations: %d", size));
    spg=null;
  }





}
