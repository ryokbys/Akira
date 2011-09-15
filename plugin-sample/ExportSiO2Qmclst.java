package plugin;
import java.io.*;

import viewer.viewConfigPanel.plugin.ExportPluginInterface;

public class ExportSiO2Qmclst implements ExportPluginInterface {
  public String getName(){
    return "sio2-qmclst00";
  }
  public void exec(String saveFile,
                   float[][] h,
                   float[][] hinv,
                   int n,
                   float[][] r,
                   byte[] tag,
                   int[] vtag
                   ){


    FileWriter fw;
    BufferedWriter bw;
    PrintWriter pw;
    String str;

    //viewer.renderer.Atoms atoms=ctrl.getActiveRW().atoms;
    // open
    try{
      fw = new FileWriter( saveFile );
      bw = new BufferedWriter( fw );
      pw = new PrintWriter( bw );

      //set maximum bond length in Ang
      double bond=1.6*1.3;

      //convert Ang to a.u. and square
      double bond2=(bond/0.529)*(bond/0.529);

      int nv=0;
      int[] icoord=new int[n];
      for(int i=0;i<n;i++){
        icoord[i]=0;
        if(vtag[i]<0)continue;
        nv++;
      }
      System.out.println(String.format("input atoms: %d",nv));

      for(int i=0;i<n-1;i++){
        if(vtag[i]<0)continue;
        for(int j=i+1;j<n;j++){
          if(vtag[j]<0)continue;
          float dr2=0;
          for(int k=0; k<3; k++)dr2+=(r[i][k]-r[j][k])*(r[i][k]-r[j][k]);
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
          for(int k=0; k<3; k++)dr2+=(r[i][k]-r[j][k])*(r[i][k]-r[j][k]);
          if(dr2< bond2){
            icoord[i]++;
            icoord[j]++;
          }
        }
      }
      //----delete isolated O
      for(int i=0;i<n;i++){
        if(vtag[i]<0)continue;
        if(tag[i]==2 && icoord[i]<=1)vtag[i]=-1;
      }


      int nn=0;
      double[] xt={0.,0.,0.};
      for(int i=0;i<n;i++){
        if(vtag[i]<0)continue;
        nn++;
        for(int k=0; k<3; k++)xt[k] += hinv[k][0]*r[i][0]+hinv[k][1]*r[i][1]+hinv[k][2]*r[i][2];
      }
      System.out.println("Terminated by Si");
      System.out.println(String.format("output Natom: %d",nn));

      pw.println(String.format("%e %e %e",xt[0]/nn,xt[1]/nn,xt[2]/nn));//xtarget
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
