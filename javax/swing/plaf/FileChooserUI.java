package javax.swing.plaf;

import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;

public abstract class FileChooserUI
  extends ComponentUI
{
  public FileChooserUI() {}
  
  public abstract FileFilter getAcceptAllFileFilter(JFileChooser paramJFileChooser);
  
  public abstract FileView getFileView(JFileChooser paramJFileChooser);
  
  public abstract String getApproveButtonText(JFileChooser paramJFileChooser);
  
  public abstract String getDialogTitle(JFileChooser paramJFileChooser);
  
  public abstract void rescanCurrentDirectory(JFileChooser paramJFileChooser);
  
  public abstract void ensureFileIsVisible(JFileChooser paramJFileChooser, File paramFile);
  
  public JButton getDefaultButton(JFileChooser paramJFileChooser)
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\FileChooserUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */