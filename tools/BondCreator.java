package tools;

import java.io.*;
import java.util.*;

import converter.*;
import tools.*;
import data.*;

/**
 * Creating Bonds using pair-list
 */
public class BondCreator{
  final float H_PI = 180.0f/(float)Math.PI;
  static final int maxBondSpecies=100;
  int[] nBond=new int[maxBondSpecies];//# of bonds in each species

  //bond info
  public ArrayList<Integer> atom1List;
  public ArrayList<Integer> atom2List;
  public ArrayList<Float> lengthList;

  //called kvsconv or bondpanel
  boolean normalCreating;
  boolean existBondFile=false;
  String bondfile="bond000";

  public BondCreator(ConvConfig cconf){
    normalCreating=true;
    atom1List=cconf.atom1List;
    atom2List=cconf.atom2List;
    lengthList=cconf.lengthList;
    existBondFile=cconf.existBondFile;
  }
  public BondCreator(){
    normalCreating=false;
    atom1List = new ArrayList<Integer>();
    atom2List = new ArrayList<Integer>();
    lengthList = new ArrayList<Float>();
    existBondFile=false;
  }


  public void create(Atoms atoms,Bonds bonds){
    if(existBondFile){
      this.createWithBondList(atoms,bonds);
    }else{
      this.createWithBondLength(atoms,bonds);
    }
  }
  private void createWithBondLength(Atoms atoms,Bonds bonds){
    System.out.print("\r");
    System.out.print("creating bonds starts");


    float[] dr = new float[3];
    int a1, a2;
    float len, len2;
    float maxLength=-1.f;
    for(int i=0;i<lengthList.size();i++)
      if(maxLength<lengthList.get(i))maxLength=lengthList.get(i);

    float lmax2= maxLength*maxLength;

    // make pair-list
    ArrayList<ArrayList<Integer>> lspr= PairList.makePairList(atoms,maxLength,true,false);


    int inc=0;
    for(int i=0;i<maxBondSpecies;i++)nBond[i]=0;
    // with lspr
    for(int i=0; i<atoms.n; i++){
      int itag = atoms.tag[i];
      if( itag < 0 || itag==Const.VOLUME_DATA_TAG)continue;
      ArrayList<Integer> iList = lspr.get(i);
      //System.out.println(String.format("ilist: %d",iList.size()));
      //System.exit(0);

      for(int k=0; k<iList.size(); k++){
        int j= iList.get(k);// obtain neighbor from lspr
        if( j <= i ) continue;// disable double-count
        int jtag = atoms.tag[j];
        if( jtag < 0 || jtag==Const.VOLUME_DATA_TAG)continue;
        dr[0] = atoms.r[j][0] -atoms.r[i][0];
        dr[1] = atoms.r[j][1] -atoms.r[i][1];
        dr[2] = atoms.r[j][2] -atoms.r[i][2];
        float dr2 =dr[0]*dr[0] +dr[1]*dr[1] +dr[2]*dr[2];

        if( dr2 > lmax2 ) continue;
        for(int p=0; p<lengthList.size(); p++){
          a1= atom1List.get(p);
          a2= atom2List.get(p);
          len= lengthList.get(p);

          len2= len*len;
          if( dr2 > len2 )continue;

          if( (a1==itag && a2==jtag) || (a2==itag && a1==jtag)){
            float[] v = Coordinate.xyz2rtp(dr[0],dr[1],dr[2]);
            //set
            bonds.add(i,j,atoms.r[i],v);
            /*
             * System.out.println(String.format("%d %f %f %f - %d %f %f %f =%f %f %f ",i,
             *                                  atoms.r[i][0],
             *                                  atoms.r[i][1],
             *                                  atoms.r[i][2],
             *                                  j,
             *                                  atoms.r[j][0],
             *                                  atoms.r[j][1],
             *                                  atoms.r[j][2],
             *                                  v[0],v[1],v[2]
             *                                  ));
             */
            nBond[p]++;
            if(bonds.maxBondLength<v[0])bonds.maxBondLength=v[0];
          }//endif
        }//p-loop
      }//k-loop

      //progress bar
      int digit=1;
      if(digit<atoms.n/30)digit=atoms.n/30;
      if(i%digit ==0 ){
        System.out.print("\r");
        System.out.print("creating bonds [");
        for(int jj=0;jj<i/digit;jj++)System.out.print("=");
        System.out.print(">");
        for(int jj=0;jj<30-i/digit;jj++)System.out.print(" ");
        System.out.print("] ");
        System.out.print(String.format("%3.0f %%",i/(float)atoms.n*100));
      }

    }//i-loop

    //finish progress bar
    System.out.print("\r");
    for(int j=0;j<100;j++)System.out.print(" ");
    System.out.print("\r");

    System.out.print(String.format("  |- BONDS        : %8d",bonds.getN()));
    if(lengthList.size()!=0){
      System.out.print(" ( ");
      for( int i=0; i<lengthList.size(); i++ ){
        System.out.print( String.format("%d-%d(length=%.2f): %d, ",
                                        atom1List.get(i),
                                        atom2List.get(i),
                                        lengthList.get(i),
                                        nBond[i]
                                        ));
      }
      System.out.print("\b\b )\n");
    }
  }

  private void createWithBondList(Atoms atoms,Bonds bonds){
    float[] dr = new float[3];
    int a1, a2;
    float len, len2;
    float maxLength=-1.f;
    for(int i=0;i<lengthList.size();i++)
      if(maxLength<lengthList.get(i))maxLength=lengthList.get(i);

    float lmax2= maxLength*maxLength;

    // set pair-list
    ArrayList<ArrayList<Integer>> lspr= PairList.readPairList(bondfile,atoms);


    int inc=0;
    for(int i=0;i<maxBondSpecies;i++)nBond[i]=0;
    // with lspr
    for(int i=0; i<atoms.n; i++){
      int itag = atoms.tag[i];
      if( itag < 0 || itag==Const.VOLUME_DATA_TAG)continue;
      ArrayList<Integer> iList = lspr.get(i);

      for(int k=0; k<iList.size(); k++){
        int j= iList.get(k);// obtain neighbor from lspr
        if( j <= i ) continue;// disable double-count
        int jtag = atoms.tag[j];
        if( jtag < 0 || jtag==Const.VOLUME_DATA_TAG)continue;
        dr[0] = atoms.r[j][0] -atoms.r[i][0];
        dr[1] = atoms.r[j][1] -atoms.r[i][1];
        dr[2] = atoms.r[j][2] -atoms.r[i][2];

        float[] v = Coordinate.xyz2rtp(dr[0],dr[1],dr[2]);
        //set
        bonds.add(i,j,atoms.r[i],v);
        if(bonds.maxBondLength<v[0])bonds.maxBondLength=v[0];
      }//k-loop


    }//i-loop

    System.out.println("");
    System.out.println(String.format("  |- BONDS        : %8d",bonds.getN()));
  }



}
