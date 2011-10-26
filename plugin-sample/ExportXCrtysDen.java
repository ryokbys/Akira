package plugin;
import java.io.*;
import viewer.viewConfigPanel.plugin.ExportPluginInterface;

public class ExportXCrtysDen implements ExportPluginInterface {
  public String getSaveFileName(){
    return "hoge.xsf";
  }
  public String getPluginName(){
    return "XCrysDen format";
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

    // open
    try{
      fw = new FileWriter( saveFile);
      bw = new BufferedWriter( fw );
      pw = new PrintWriter( bw );


      double bohr=0.529177;

      int nv=0;
      for(int i=0;i<n;i++){
        if(vtag[i]<0)continue;
        nv++;
      }
      System.out.println(String.format("output Natom: %d",nv));

      //NOTE: fortran program is transpose for fast reading!
      pw.println("PRIMVEC");
      pw.println(String.format("%e %e %e",h[0][0]*bohr,h[1][0]*bohr,h[2][0]*bohr));
      pw.println(String.format("%e %e %e",h[0][1]*bohr,h[1][1]*bohr,h[2][1]*bohr));
      pw.println(String.format("%e %e %e",h[0][2]*bohr,h[1][2]*bohr,h[2][2]*bohr));

      pw.println("PRIMCOORD");
      pw.println(String.format("%d",nv));
      //shpere as atom
      for(int i=0;i<n;i++){
        //skip
        if(vtag[i]<0)continue;

        pw.println(String.format("%d %e %e %e %e %e %e",
                                 (int)tag[i],
                                 r[i][0],r[i][1],r[i][2],
                                 0.0,0.0,0.0));
      }

      pw.close();
      bw.close();
      fw.close();
    }catch( IOException e ){
      System.out.println("---> Failed to write mts file");
      //System.out.println(e.getMessage());
    }
  }
}
