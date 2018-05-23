package sun.awt.shell;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.Callable;

class ShellFolderManager
{
  ShellFolderManager() {}
  
  public ShellFolder createShellFolder(File paramFile)
    throws FileNotFoundException
  {
    return new DefaultShellFolder(null, paramFile);
  }
  
  public Object get(String paramString)
  {
    if (paramString.equals("fileChooserDefaultFolder"))
    {
      File localFile = new File(System.getProperty("user.home"));
      try
      {
        return createShellFolder(localFile);
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        return localFile;
      }
    }
    if (paramString.equals("roots")) {
      return File.listRoots();
    }
    if (paramString.equals("fileChooserComboBoxFolders")) {
      return get("roots");
    }
    if (paramString.equals("fileChooserShortcutPanelFolders")) {
      return new File[] { (File)get("fileChooserDefaultFolder") };
    }
    return null;
  }
  
  public boolean isComputerNode(File paramFile)
  {
    return false;
  }
  
  public boolean isFileSystemRoot(File paramFile)
  {
    if (((paramFile instanceof ShellFolder)) && (!((ShellFolder)paramFile).isFileSystem())) {
      return false;
    }
    return paramFile.getParentFile() == null;
  }
  
  protected ShellFolder.Invoker createInvoker()
  {
    return new DirectInvoker(null);
  }
  
  private static class DirectInvoker
    implements ShellFolder.Invoker
  {
    private DirectInvoker() {}
    
    public <T> T invoke(Callable<T> paramCallable)
      throws Exception
    {
      return (T)paramCallable.call();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\shell\ShellFolderManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */