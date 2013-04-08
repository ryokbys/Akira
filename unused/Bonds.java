package data;
import java.io.*;
import java.util.*;
import tools.*;
import data.*;

  /**
   * This class defines bonds info.
   */
public class Bonds {

  //body
  ArrayList<Bond> bonds= new ArrayList<Bond>();
  public HashMap<Integer,Integer> CN = new HashMap<Integer,Integer>();
  public float maxBondLength=0.f;
  public int maxCN=0;

  //constructor
  public Bonds(){
  }

  public void clear(){
    bonds.clear();
    CN.clear();
  }

  public int getCNN(){
    Set set = CN.keySet();
    Iterator iterator = set.iterator();
    Integer object;
    int n=0;
    while(iterator.hasNext()){
      object = (Integer)iterator.next();
      if(maxCN<CN.get(object)) maxCN=CN.get(object);
      n++;
    }
    return n;
  }
  private void countCoordinationNum(int i){
    if(CN.containsKey(i)){
      int inc=CN.get(i);
      inc++;
      CN.put(i,inc);
    }else{
      int inc=1;
      CN.put(i,inc);
    }
  }
  public void add(int i, int j, float[] org, float[] v){
    bonds.add(new Bond(i, j, org, v[0],v[1],v[2]));
    countCoordinationNum(i);
    countCoordinationNum(j);
  }
  public void add(int i, int j, float[] org, float l, float t, float p){
    bonds.add(new Bond(i, j, org, l, t, p));
    countCoordinationNum(i);
    countCoordinationNum(j);
  }

  public Bond get(int i){
    return bonds.get(i);
  }
  public int getN(){
    return bonds.size();
  }

  public int getSkipByte(){
    return
      4//nBonds
      +4//CNN
      +4//maxlength
      +4//maxCN
      +(4//i
        +4//j
        +4*3//origin
        +4//length
        +4//theta
        +4//phi
        )*getN()
      +(4+4)*getCNN();
  }

}
