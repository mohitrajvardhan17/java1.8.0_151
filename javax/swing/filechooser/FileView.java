package javax.swing.filechooser;

import java.io.File;
import javax.swing.Icon;

public abstract class FileView
{
  public FileView() {}
  
  public String getName(File paramFile)
  {
    return null;
  }
  
  public String getDescription(File paramFile)
  {
    return null;
  }
  
  public String getTypeDescription(File paramFile)
  {
    return null;
  }
  
  public Icon getIcon(File paramFile)
  {
    return null;
  }
  
  public Boolean isTraversable(File paramFile)
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\filechooser\FileView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */