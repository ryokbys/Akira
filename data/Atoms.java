package data;
import java.io.*;
import java.util.*;
import tools.*;
import data.*;

/**
 * This class defines atoms info.
 *
 * header
 * body
 * footer
 *
 */
public class Atoms {

  /* member */
  //header
  public String convDate;
  public int totalFrame;
  public float startTime;
  public float timeInterval;

  //body(one frame)
  //public int skipByte;
  public int n=0;
  public int nData=0;
  public float[][] h= new float[3][3];
  public float[][] hinv= new float[3][3];
  public byte[]    tag;//-2^7 ~ 2^7-1
  public float[][] r;
  public float[][] data;

  //footer
  public int nTagMax=0;
  public int[] involvedTags;
  public float[][] hMax = new float[3][3];
  public float hMinLength;
  public float[][] originalDataRange = new float[Const.DATA][2];
  public int maxNatom=0;

  //for trajectory
  public float[][] prev;


  //constructor
  public Atoms(){
  }
  public void allocate(int n){
    if(maxNatom==0 || maxNatom<n){
      this.maxNatom=n;
      tag  = null;
      tag  = new byte[n];
      r   = null;
      r   = new float[n][3];
      data = null;
      data = new float[n][Const.DATA];
    }
  }
  public int getSkipByte(){
    return
      4//nAtoms
      +4//nData
      +4*3*3//h[3][3]
      +4*3*3//hinv[3][3]
      +(1//tag(byte)
        +4*3//ra
        +4*nData//data
        )*n;
  }

}
