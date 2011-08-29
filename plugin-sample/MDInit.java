package plugin;
import java.io.*;
import viewer.viewConfigPanel.plugin.ExportPluginInterface;

public class MDInit implements ExportPluginInterface {
  public void exec(String dir, int fn,
                   float[][] h,
                   float[][] hinv,
                   int n,
                   float[][] r,
                   byte[] tag,
                   int[] vtag
                   ){
    String filePath=String.format(dir+"/%04d.init.d",fn);

    FileWriter fw;
    BufferedWriter bw;
    PrintWriter pw;
    String str;

    // open
    try{
      fw = new FileWriter( filePath );
      bw = new BufferedWriter( fw );
      pw = new PrintWriter( bw );



      int nv=0;
      for(int i=0;i<n;i++){
        if(vtag[i]<0)continue;
        nv++;
      }
      System.out.println(String.format("output Natom: %d",nv));

      pw.println(String.format("%d",nv));
      pw.println(String.format("%e %e %e",h[0][0],h[0][1],h[0][2]));
      pw.println(String.format("%e %e %e",h[1][0],h[1][1],h[1][2]));
      pw.println(String.format("%e %e %e",h[2][0],h[2][1],h[2][2]));

      //shpere as atom
      for(int i=0;i<n;i++){
        //skip
        if(vtag[i]<0)continue;

        float[] out = new float[3];
        for(int k=0; k<3; k++) out[k] =
                                 hinv[k][0]*r[i][0]+
                                 hinv[k][1]*r[i][1]+
                                 hinv[k][2]*r[i][2];
        pw.println(String.format("%e %e %e %e %e %e %e %e %e %e"
                                 ,(float)tag[i],out[0],out[1],out[2]
                                 ,0e0,0e0,0e0
                                 ,out[0],out[1],out[2]
                                 ));
      }

      pw.close();
      bw.close();
      fw.close();
    }catch( IOException e ){
      System.out.println("---> Failed to write MD init file");
      //System.out.println(e.getMessage());
    }
  }
}
