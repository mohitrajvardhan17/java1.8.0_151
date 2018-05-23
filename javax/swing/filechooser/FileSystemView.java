package javax.swing.filechooser;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import sun.awt.shell.ShellFolder;

public abstract class FileSystemView
{
  static FileSystemView windowsFileSystemView = null;
  static FileSystemView unixFileSystemView = null;
  static FileSystemView genericFileSystemView = null;
  private boolean useSystemExtensionHiding = UIManager.getDefaults().getBoolean("FileChooser.useSystemExtensionHiding");
  
  public static FileSystemView getFileSystemView()
  {
    if (File.separatorChar == '\\')
    {
      if (windowsFileSystemView == null) {
        windowsFileSystemView = new WindowsFileSystemView();
      }
      return windowsFileSystemView;
    }
    if (File.separatorChar == '/')
    {
      if (unixFileSystemView == null) {
        unixFileSystemView = new UnixFileSystemView();
      }
      return unixFileSystemView;
    }
    if (genericFileSystemView == null) {
      genericFileSystemView = new GenericFileSystemView();
    }
    return genericFileSystemView;
  }
  
  public FileSystemView()
  {
    final WeakReference localWeakReference = new WeakReference(this);
    UIManager.addPropertyChangeListener(new PropertyChangeListener()
    {
      public void propertyChange(PropertyChangeEvent paramAnonymousPropertyChangeEvent)
      {
        FileSystemView localFileSystemView = (FileSystemView)localWeakReference.get();
        if (localFileSystemView == null) {
          UIManager.removePropertyChangeListener(this);
        } else if (paramAnonymousPropertyChangeEvent.getPropertyName().equals("lookAndFeel")) {
          useSystemExtensionHiding = UIManager.getDefaults().getBoolean("FileChooser.useSystemExtensionHiding");
        }
      }
    });
  }
  
  public boolean isRoot(File paramFile)
  {
    if ((paramFile == null) || (!paramFile.isAbsolute())) {
      return false;
    }
    File[] arrayOfFile1 = getRoots();
    for (File localFile : arrayOfFile1) {
      if (localFile.equals(paramFile)) {
        return true;
      }
    }
    return false;
  }
  
  public Boolean isTraversable(File paramFile)
  {
    return Boolean.valueOf(paramFile.isDirectory());
  }
  
  public String getSystemDisplayName(File paramFile)
  {
    if (paramFile == null) {
      return null;
    }
    String str = paramFile.getName();
    if ((!str.equals("..")) && (!str.equals(".")) && ((useSystemExtensionHiding) || (!isFileSystem(paramFile)) || (isFileSystemRoot(paramFile))) && (((paramFile instanceof ShellFolder)) || (paramFile.exists())))
    {
      try
      {
        str = getShellFolder(paramFile).getDisplayName();
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        return null;
      }
      if ((str == null) || (str.length() == 0)) {
        str = paramFile.getPath();
      }
    }
    return str;
  }
  
  public String getSystemTypeDescription(File paramFile)
  {
    return null;
  }
  
  public Icon getSystemIcon(File paramFile)
  {
    if (paramFile == null) {
      return null;
    }
    ShellFolder localShellFolder;
    try
    {
      localShellFolder = getShellFolder(paramFile);
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      return null;
    }
    Image localImage = localShellFolder.getIcon(false);
    if (localImage != null) {
      return new ImageIcon(localImage, localShellFolder.getFolderType());
    }
    return UIManager.getIcon(paramFile.isDirectory() ? "FileView.directoryIcon" : "FileView.fileIcon");
  }
  
  public boolean isParent(File paramFile1, File paramFile2)
  {
    if ((paramFile1 == null) || (paramFile2 == null)) {
      return false;
    }
    if ((paramFile1 instanceof ShellFolder))
    {
      File localFile1 = paramFile2.getParentFile();
      if ((localFile1 != null) && (localFile1.equals(paramFile1))) {
        return true;
      }
      File[] arrayOfFile1 = getFiles(paramFile1, false);
      for (File localFile2 : arrayOfFile1) {
        if (paramFile2.equals(localFile2)) {
          return true;
        }
      }
      return false;
    }
    return paramFile1.equals(paramFile2.getParentFile());
  }
  
  public File getChild(File paramFile, String paramString)
  {
    if ((paramFile instanceof ShellFolder))
    {
      File[] arrayOfFile1 = getFiles(paramFile, false);
      for (File localFile : arrayOfFile1) {
        if (localFile.getName().equals(paramString)) {
          return localFile;
        }
      }
    }
    return createFileObject(paramFile, paramString);
  }
  
  public boolean isFileSystem(File paramFile)
  {
    if ((paramFile instanceof ShellFolder))
    {
      ShellFolder localShellFolder = (ShellFolder)paramFile;
      return (localShellFolder.isFileSystem()) && ((!localShellFolder.isLink()) || (!localShellFolder.isDirectory()));
    }
    return true;
  }
  
  public abstract File createNewFolder(File paramFile)
    throws IOException;
  
  public boolean isHiddenFile(File paramFile)
  {
    return paramFile.isHidden();
  }
  
  public boolean isFileSystemRoot(File paramFile)
  {
    return ShellFolder.isFileSystemRoot(paramFile);
  }
  
  public boolean isDrive(File paramFile)
  {
    return false;
  }
  
  public boolean isFloppyDrive(File paramFile)
  {
    return false;
  }
  
  public boolean isComputerNode(File paramFile)
  {
    return ShellFolder.isComputerNode(paramFile);
  }
  
  public File[] getRoots()
  {
    File[] arrayOfFile = (File[])ShellFolder.get("roots");
    for (int i = 0; i < arrayOfFile.length; i++) {
      if (isFileSystemRoot(arrayOfFile[i])) {
        arrayOfFile[i] = createFileSystemRoot(arrayOfFile[i]);
      }
    }
    return arrayOfFile;
  }
  
  public File getHomeDirectory()
  {
    return createFileObject(System.getProperty("user.home"));
  }
  
  public File getDefaultDirectory()
  {
    File localFile = (File)ShellFolder.get("fileChooserDefaultFolder");
    if (isFileSystemRoot(localFile)) {
      localFile = createFileSystemRoot(localFile);
    }
    return localFile;
  }
  
  public File createFileObject(File paramFile, String paramString)
  {
    if (paramFile == null) {
      return new File(paramString);
    }
    return new File(paramFile, paramString);
  }
  
  public File createFileObject(String paramString)
  {
    File localFile = new File(paramString);
    if (isFileSystemRoot(localFile)) {
      localFile = createFileSystemRoot(localFile);
    }
    return localFile;
  }
  
  public File[] getFiles(File paramFile, boolean paramBoolean)
  {
    ArrayList localArrayList = new ArrayList();
    if (!(paramFile instanceof ShellFolder)) {
      try
      {
        paramFile = getShellFolder(paramFile);
      }
      catch (FileNotFoundException localFileNotFoundException1)
      {
        return new File[0];
      }
    }
    File[] arrayOfFile1 = ((ShellFolder)paramFile).listFiles(!paramBoolean);
    if (arrayOfFile1 == null) {
      return new File[0];
    }
    for (Object localObject : arrayOfFile1)
    {
      if (Thread.currentThread().isInterrupted()) {
        break;
      }
      if (!(localObject instanceof ShellFolder))
      {
        if (isFileSystemRoot((File)localObject)) {
          localObject = createFileSystemRoot((File)localObject);
        }
        try
        {
          localObject = ShellFolder.getShellFolder((File)localObject);
        }
        catch (FileNotFoundException localFileNotFoundException2)
        {
          continue;
        }
        catch (InternalError localInternalError)
        {
          continue;
        }
      }
      if ((!paramBoolean) || (!isHiddenFile((File)localObject))) {
        localArrayList.add(localObject);
      }
    }
    return (File[])localArrayList.toArray(new File[localArrayList.size()]);
  }
  
  public File getParentDirectory(File paramFile)
  {
    if ((paramFile == null) || (!paramFile.exists())) {
      return null;
    }
    ShellFolder localShellFolder;
    try
    {
      localShellFolder = getShellFolder(paramFile);
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      return null;
    }
    File localFile1 = localShellFolder.getParentFile();
    if (localFile1 == null) {
      return null;
    }
    if (isFileSystem(localFile1))
    {
      File localFile2 = localFile1;
      if (!localFile2.exists())
      {
        File localFile3 = localFile1.getParentFile();
        if ((localFile3 == null) || (!isFileSystem(localFile3))) {
          localFile2 = createFileSystemRoot(localFile2);
        }
      }
      return localFile2;
    }
    return localFile1;
  }
  
  ShellFolder getShellFolder(File paramFile)
    throws FileNotFoundException
  {
    if ((!(paramFile instanceof ShellFolder)) && (!(paramFile instanceof FileSystemRoot)) && (isFileSystemRoot(paramFile))) {
      paramFile = createFileSystemRoot(paramFile);
    }
    try
    {
      return ShellFolder.getShellFolder(paramFile);
    }
    catch (InternalError localInternalError)
    {
      System.err.println("FileSystemView.getShellFolder: f=" + paramFile);
      localInternalError.printStackTrace();
    }
    return null;
  }
  
  protected File createFileSystemRoot(File paramFile)
  {
    return new FileSystemRoot(paramFile);
  }
  
  static class FileSystemRoot
    extends File
  {
    public FileSystemRoot(File paramFile)
    {
      super("");
    }
    
    public FileSystemRoot(String paramString)
    {
      super();
    }
    
    public boolean isDirectory()
    {
      return true;
    }
    
    public String getName()
    {
      return getPath();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\filechooser\FileSystemView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */