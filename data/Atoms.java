package data;
import java.io.*;
import java.util.*;
import tools.*;
import data.*;

/**
 * Atoms class provides variables and methods of the system constructed by atoms.
 *
 * header
 * body
 * footer
 *
 */
public class Atoms {

  //body(one frame)
  public float[][] hmat = new float[3][3];
  public float[][] hmati= new float[3][3];
  public ArrayList<Atom> listAtom= new ArrayList<Atom>();

  //for trajectory
  //public float[][] prev;

  //constructor
  public Atoms(){
  }

  public int getNumAtoms(){
    return listAtom.size();
  }

  public int getNumBonds(){
    int inc=0;
    for( int i=0;i<listAtom.size();i++ ){
      inc += listAtom.get(i).listBond.size();
    }
    return inc;
  }

  public float getVolume(){
    return hmat[0][0] *(hmat[1][1]*hmat[2][2] -hmat[1][2]*hmat[2][1])
      +hmat[0][1] *(hmat[1][2]*hmat[2][0] -hmat[1][0]*hmat[2][2])
      +hmat[0][2] *(hmat[1][0]*hmat[2][1] -hmat[1][1]*hmat[2][0]);
  }

  public Atom getAtom( int i ){
    return listAtom.get(i);
  }

  public void addAtom( Atom a ){
    listAtom.add(a);
  }
  
  public float getMaxBondLength(){
    float maxLength= 0.f;
    for( int i=0; i<getNumAtoms(); i++ ){
      Atom ai= getAtom(i);
      for( int j=0; j<ai.getNumBonds(); j++ ){
        Bond b= ai.getBond(j);
        maxLength= Math.max( maxLength, b.length );
      }
    }
    return maxLength;
  }

  public int getMaxCoordinationNumber(){
    int maxCN= 0;
    for( int i=0; i<getNumAtoms(); i++ ){
      Atom ai= getAtom(i);
      maxCN= Math.max( maxCN, ai.getNumBonds() );
    }
    return maxCN;
  }

  public void clearBonds(){
    for( int i=0; i<getNumAtoms(); i++ ){
      Atom ai= getAtom(i);
      ai.clearBonds();
    }
  }

  public int getSizeByByte(){
    int size= 4 // numAtom
      +4*3*3 // hmat[3][3]
      +4*3*3; // hmati[3][3]
    for( int i=0; i<listAtom.size();i++ ){
      size += listAtom.get(i).getSizeByByte();
    }
    return size;
  }

}
