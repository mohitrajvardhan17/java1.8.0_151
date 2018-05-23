package javax.swing.filechooser;

import java.io.File;

public abstract class FileFilter
{
  public FileFilter() {}
  
  public abstract boolean accept(File paramFile);
  
  public abstract String getDescription();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\filechooser\FileFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */