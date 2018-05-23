package javax.swing.filechooser;

import java.io.File;
import java.io.IOException;
import javax.swing.UIManager;

class GenericFileSystemView
  extends FileSystemView
{
  private static final String newFolderString = UIManager.getString("FileChooser.other.newFolder");
  
  GenericFileSystemView() {}
  
  public File createNewFolder(File paramFile)
    throws IOException
  {
    if (paramFile == null) {
      throw new IOException("Containing directory is null:");
    }
    File localFile = createFileObject(paramFile, newFolderString);
    if (localFile.exists()) {
      throw new IOException("Directory already exists:" + localFile.getAbsolutePath());
    }
    localFile.mkdirs();
    return localFile;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\filechooser\GenericFileSystemView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */