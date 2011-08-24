package plugin;
import java.io.*;

import viewer.viewConfigPanel.MyPluginInterface;

public class SiO2Qmclst implements MyPluginInterface {
  //public void do(float[][] h, int n, float[][] r){
  public void exec(){
    System.out.println("qmclst");
  }

  public void exec(String dir, int fn,
                   float[][] h,
                   float[][] hinv,
                   int n,
                   float[][] r,
                   byte[] tag,
                   int[] vtag
                   ){

    String filePath=String.format(dir+"/%04d.qmclst",fn);

    FileWriter fw;
    BufferedWriter bw;
    PrintWriter pw;
    String str;

    //viewer.renderer.Atoms atoms=ctrl.getActiveRW().atoms;
    // open
    try{
      fw = new FileWriter( filePath );
      bw = new BufferedWriter( fw );
      pw = new PrintWriter( bw );


      double bond=1.6*1.3/0.529;
      double bond2=bond*bond;

      //-----cal coordinatio num
      int[] icoord=new int[n];
      for(int i=0;i<n;i++)icoord[i]=0;
      for(int i=0;i<n-1;i++){
        if(vtag[i]<0)continue;
        for(int j=i+1;j<n;j++){
          if(vtag[j]<0)continue;
          float dr2=0;
          for(int k=0; k<3; k++)
            dr2+=(r[i][k]-r[j][k])*(r[i][k]-r[j][k]);
          if(dr2< bond2){
            icoord[i]++;
            icoord[j]++;
          }
        }
      }
      //-----delete isolated Si
      for(int i=0;i<n;i++){
        if(vtag[i]<0)continue;
        if(tag[i]==1 && icoord[i]<4)vtag[i]=-1;
      }
      //-----recal coordinatio num
      for(int i=0;i<n;i++)icoord[i]=0;
      for(int i=0;i<n-1;i++){
        if(vtag[i]<0)continue;
        for(int j=i+1;j<n;j++){
          if(vtag[j]<0)continue;
          float dr2=0;
          for(int k=0; k<3; k++)
            dr2+=(r[i][k]-r[j][k])*(r[i][k]-r[j][k]);
          if(dr2< bond2){
            icoord[i]++;
            icoord[j]++;
          }
        }
      }
      //delete O
      for(int i=0;i<n;i++){
        if(vtag[i]<0)continue;
        if(tag[i]==2 && icoord[i]<=1)vtag[i]=-1;
      }


      int nn=0;
      for(int i=0;i<n;i++){
        if(vtag[i]<0)continue;
        nn++;
      }
      System.out.println(String.format("output Natom: %d",nn));
      pw.println(String.format("%d",nn));

      for(int i=0;i<n;i++){
        if(vtag[i]<0)continue;

        float[] out = new float[3];
        for(int k=0; k<3; k++) out[k] = hinv[k][0]*r[i][0]
                                 + hinv[k][1]*r[i][1]
                                 + hinv[k][2]*r[i][2];
        pw.println(String.format("%e %e %e",out[0],out[1],out[2]));
      }

      pw.close();
      bw.close();
      fw.close();
    }catch( IOException e ){
      System.out.println("---> Failed to write SiO2 qmclst file");
      //System.out.println(e.getMessage());
    }

  }

}
