package tools;
import java.io.File;
import javax.swing.filechooser.FileFilter;


/**
 * file chooser filter
 */
public class MyFilter extends FileFilter{
  private String filterString;
  public MyFilter(String filterString){
    this.filterString=filterString;
  }

  public boolean accept(File f){
    //always show directory
    if(f.isDirectory()) return true;
    String filename = f.getName();
    //if starting with...
    if(filename.endsWith(filterString))
      return true;
    else
      return false;

  }

  /**
   * override description
   */
  public String getDescription(){
    return filterString;
  }

}
