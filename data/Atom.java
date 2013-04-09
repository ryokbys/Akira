package data;
import java.util.*;

public class Atom{
  public byte tag;
  public float[] pos= new float[3];
  
  public ArrayList<Bond> listBond= new ArrayList<Bond>();

  // Auxiliary data per atom
  public static final byte MAX_NUM_DATA= 9;
  public float[] auxData= new float[MAX_NUM_DATA];

  // visualize
  public boolean isVisible= true;

  // Constructor
  public Atom(){
  }
  public Atom( byte tag ){
    this.tag= tag;
  }
  public Atom( byte tag, float x, float y, float z ){
    this.tag= tag;
    this.pos[0]= x;
    this.pos[1]= y;
    this.pos[2]= z;
  }

  public int getNumBonds(){
    return listBond.size();
  }
  public Bond getBond( int i ){
    return listBond.get(i);
  }
  public void clearBonds(){
    listBond.clear();
  }

  public int getSizeByByte(){
    int size;
    size=  1 // tag
      +4*3 // pos
      +4*MAX_NUM_DATA // auxData
      +4; //numBonds
    for( int i=0; i<listBond.size();i++ ){
      size += listBond.get(i).getSizeByByte();
    }
    return size;
  }

}
