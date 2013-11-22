package tools;

import java.io.*;
import java.util.*;

import converter.*;
import tools.*;
import data.*;

/**
 * Searchs neighbors and stores bond info to each atom.
 */
public class BondCreator{
  final float H_PI = 180.0f/(float)Math.PI;
  static final int maxBondSpecies=100;
  int[] numBonds=new int[maxBondSpecies];//# of bonds in each species

  final boolean PBC_ON= true;

  //bond info
  public ArrayList<Integer> atom1List;
  public ArrayList<Integer> atom2List;
  public ArrayList<Float> lengthList;

  public BondCreator(ConvConfig cconf){
    atom1List=cconf.atom1List;
    atom2List=cconf.atom2List;
    lengthList=cconf.lengthList;
  }
  public BondCreator(){
    atom1List = new ArrayList<Integer>();
    atom2List = new ArrayList<Integer>();
    lengthList = new ArrayList<Float>();
  }


  public void createWithBondLength( Atoms atoms ){
    System.out.print("\r");
    System.out.print("creating bonds starts");

    float[] dh = new float[3];
    float[] dr = new float[3];
    int a1, a2;
    float len, len2;
    float maxLength=-1.f;
    int natm= atoms.getNumAtoms();
    for(int i=0;i<lengthList.size();i++)
      if( maxLength<lengthList.get(i) ) maxLength=lengthList.get(i);

    float lmax2= maxLength*maxLength;

    // make pair-list
    ArrayList<ArrayList<Integer>> lspr= PairList.makePairList(atoms,maxLength,true,PBC_ON);


    int inc=0;
    for(int i=0;i<maxBondSpecies;i++)numBonds[i]=0;
    // with lspr
    for( int i=0; i<natm; i++ ){
      //int itag = atoms.tag[i];
      Atom atomi= atoms.getAtom(i);
      int itag= (int)(atomi.tag);
      if( itag < 0 || itag==Const.VOLUME_DATA_TAG) continue;
      ArrayList<Integer> iList = lspr.get(i);
      //System.out.println(String.format("ilist: %d",iList.size()));
      //System.exit(0);
      float[] xi= MDMath.mulH( atoms.hmati, atomi.pos );

      for(int k=0; k<iList.size(); k++){
        int j= iList.get(k);  // obtain neighbor from lspr
        if( j <= i ) continue;  // avoid double-counting
        Atom atomj= atoms.getAtom(j);
        //int jtag = atoms.tag[j];
        int jtag = (int)(atomj.tag);
        if( jtag < 0 || jtag==Const.VOLUME_DATA_TAG ) continue;
        float[] xj= MDMath.mulH( atoms.hmati, atomj.pos );
        dh[0]= xj[0] -xi[0];
        dh[1]= xj[1] -xi[1];
        dh[2]= xj[2] -xi[2];
        if( PBC_ON ){
          for(int l=0; l<3; l++){
            if(dh[l] > 0.5f) dh[l]=dh[l]-1.0f;
            if(dh[l] <-0.5f) dh[l]=dh[l]+1.0f;
          }
        }
        dr= MDMath.mulH( atoms.hmat, dh );
        float dr2 =dr[0]*dr[0] +dr[1]*dr[1] +dr[2]*dr[2];

        if( dr2 > lmax2 ) continue;
        for(int p=0; p<lengthList.size(); p++){
          a1= atom1List.get(p);
          a2= atom2List.get(p);
          len= lengthList.get(p);

          len2= len*len;
          if( dr2 > len2 ) continue;

          if( (a1==itag && a2==jtag) || (a2==itag && a1==jtag)){
            float[] v = Coordinate.xyz2rtp(dr[0],dr[1],dr[2]);
            atomi.listBond.add(new Bond(v[0],v[1],v[2]));
            v= Coordinate.xyz2rtp(-dr[0],-dr[1],-dr[2]);
            atomj.listBond.add(new Bond(v[0],v[1],v[2]));
            //set
            //bonds.add(i,j,atoms.r[i],v);
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
            numBonds[p]++;
            //if( atoms.maxBondLength < v[0] )
            //  atoms.maxBondLength= v[0];
          }//endif
        }//p-loop
      }//k-loop

      //progress bar
      int digit=1;
      if( digit < atoms.getNumAtoms()/30 ) digit= atoms.getNumAtoms()/30;
      if( i%digit ==0 ){
        System.out.print("\r");
        System.out.print("creating bonds [");
        for(int jj=0;jj<i/digit;jj++)System.out.print("=");
        System.out.print(">");
        for(int jj=0;jj<30-i/digit;jj++)System.out.print(" ");
        System.out.print("] ");
        System.out.print(String.format("%3.0f %%",i/(float)atoms.getNumAtoms()*100));
      }

    }//i-loop

    //finish progress bar
    System.out.print("\r");
    for( int j=0;j<100;j++ ) System.out.print(" ");
    System.out.print("\r");

    System.out.print(String.format("  |- BONDS        : %8d", atoms.getNumBonds()));
    if(lengthList.size()!=0){
      System.out.print(" ( ");
      for( int i=0; i<lengthList.size(); i++ ){
        System.out.print( String.format("%d-%d(length=%.2f): %d, ",
                                        atom1List.get(i),
                                        atom2List.get(i),
                                        lengthList.get(i),
                                        numBonds[i]
                                        ));
      }
      System.out.print("\b\b )\n");
    }
  }

  public void createWithBondList( Atoms atoms, ConvConfig cconf,int itarget, int ifrm ){
    float[] dh = new float[3];
    float[] dr = new float[3];
    int a1, a2;
    float len, len2;
    float maxLength=-1.f;
    for(int i=0;i<lengthList.size();i++)
      if(maxLength<lengthList.get(i))maxLength=lengthList.get(i);

    String readFile=String.format(cconf.readFilePath.get(itarget)+"/"
                                  +cconf.bondFile,ifrm);

    // set pair-list
    ArrayList<ArrayList<Integer>> lspr= PairList.readPairList(readFile,atoms);

    int inc=0;
    for(int i=0;i<maxBondSpecies;i++)numBonds[i]=0;
    // with lspr
    for(int i=0; i<atoms.getNumAtoms(); i++){
      Atom atomi= atoms.listAtom.get(i);
      int itag = (int)(atomi.tag);
      if( itag < 0 || itag==Const.VOLUME_DATA_TAG)continue;
      ArrayList<Integer> iList = lspr.get(i);
      float[] xi= MDMath.mulH( atoms.hmati, atomi.pos );

      for(int jj=0; jj<iList.size(); jj++){
        int j= iList.get(jj);// obtain neighbor from lspr
        if(j<0)continue;
        //if( j <= i ) continue;// disable double-count
        Atom atomj= atoms.listAtom.get(j);

        int jtag = (int)(atomj.tag);
        if( jtag < 0 || jtag==Const.VOLUME_DATA_TAG)continue;
        float[] xj= MDMath.mulH( atoms.hmati, atomj.pos );
        dh[0]= xj[0] -xi[0];
        dh[1]= xj[1] -xi[1];
        dh[2]= xj[2] -xi[2];
        if( PBC_ON ){
          for(int l=0; l<3; l++){
            if(dh[l] > 0.5f) dh[l]=dh[l]-1.0f;
            if(dh[l] <-0.5f) dh[l]=dh[l]+1.0f;
          }
        }
        dr= MDMath.mulH( atoms.hmat, dh );

        float[] v = Coordinate.xyz2rtp(dr[0],dr[1],dr[2]);
        atomi.listBond.add(new Bond(v[0],v[1],v[2]));
        v= Coordinate.xyz2rtp(-dr[0],-dr[1],-dr[2]);
        atomj.listBond.add(new Bond(v[0],v[1],v[2]));
        //set
        //bonds.add(i,j,atoms.r[i],v);
        //if(bonds.maxBondLength<v[0])bonds.maxBondLength=v[0];
      }//k-loop


    }//i-loop

    System.out.println(String.format("  |- BONDS        : %8d", atoms.getNumBonds() ));
  }



}
