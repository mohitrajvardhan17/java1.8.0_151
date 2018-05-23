package sun.awt.shell;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.net.URI;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;

public abstract class ShellFolder
  extends File
{
  private static final String COLUMN_NAME = "FileChooser.fileNameHeaderText";
  private static final String COLUMN_SIZE = "FileChooser.fileSizeHeaderText";
  private static final String COLUMN_DATE = "FileChooser.fileDateHeaderText";
  protected ShellFolder parent;
  private static final ShellFolderManager shellFolderManager;
  private static final Invoker invoker = shellFolderManager.createInvoker();
  private static final Comparator DEFAULT_COMPARATOR = new Comparator()
  {
    public int compare(Object paramAnonymousObject1, Object paramAnonymousObject2)
    {
      int i;
      if ((paramAnonymousObject1 == null) && (paramAnonymousObject2 == null)) {
        i = 0;
      } else if ((paramAnonymousObject1 != null) && (paramAnonymousObject2 == null)) {
        i = 1;
      } else if ((paramAnonymousObject1 == null) && (paramAnonymousObject2 != null)) {
        i = -1;
      } else if ((paramAnonymousObject1 instanceof Comparable)) {
        i = ((Comparable)paramAnonymousObject1).compareTo(paramAnonymousObject2);
      } else {
        i = 0;
      }
      return i;
    }
  };
  private static final Comparator<File> FILE_COMPARATOR = new Comparator()
  {
    public int compare(File paramAnonymousFile1, File paramAnonymousFile2)
    {
      ShellFolder localShellFolder1 = null;
      ShellFolder localShellFolder2 = null;
      if ((paramAnonymousFile1 instanceof ShellFolder))
      {
        localShellFolder1 = (ShellFolder)paramAnonymousFile1;
        if (localShellFolder1.isFileSystem()) {
          localShellFolder1 = null;
        }
      }
      if ((paramAnonymousFile2 instanceof ShellFolder))
      {
        localShellFolder2 = (ShellFolder)paramAnonymousFile2;
        if (localShellFolder2.isFileSystem()) {
          localShellFolder2 = null;
        }
      }
      if ((localShellFolder1 != null) && (localShellFolder2 != null)) {
        return localShellFolder1.compareTo(localShellFolder2);
      }
      if (localShellFolder1 != null) {
        return -1;
      }
      if (localShellFolder2 != null) {
        return 1;
      }
      String str1 = paramAnonymousFile1.getName();
      String str2 = paramAnonymousFile2.getName();
      int i = str1.compareToIgnoreCase(str2);
      if (i != 0) {
        return i;
      }
      return str1.compareTo(str2);
    }
  };
  
  ShellFolder(ShellFolder paramShellFolder, String paramString)
  {
    super(paramString != null ? paramString : "ShellFolder");
    parent = paramShellFolder;
  }
  
  public boolean isFileSystem()
  {
    return !getPath().startsWith("ShellFolder");
  }
  
  protected abstract Object writeReplace()
    throws ObjectStreamException;
  
  public String getParent()
  {
    if ((parent == null) && (isFileSystem())) {
      return super.getParent();
    }
    if (parent != null) {
      return parent.getPath();
    }
    return null;
  }
  
  public File getParentFile()
  {
    if (parent != null) {
      return parent;
    }
    if (isFileSystem()) {
      return super.getParentFile();
    }
    return null;
  }
  
  public File[] listFiles()
  {
    return listFiles(true);
  }
  
  public File[] listFiles(boolean paramBoolean)
  {
    File[] arrayOfFile = super.listFiles();
    if (!paramBoolean)
    {
      Vector localVector = new Vector();
      int i = arrayOfFile == null ? 0 : arrayOfFile.length;
      for (int j = 0; j < i; j++) {
        if (!arrayOfFile[j].isHidden()) {
          localVector.addElement(arrayOfFile[j]);
        }
      }
      arrayOfFile = (File[])localVector.toArray(new File[localVector.size()]);
    }
    return arrayOfFile;
  }
  
  public abstract boolean isLink();
  
  public abstract ShellFolder getLinkLocation()
    throws FileNotFoundException;
  
  public abstract String getDisplayName();
  
  public abstract String getFolderType();
  
  public abstract String getExecutableType();
  
  public int compareTo(File paramFile)
  {
    if ((paramFile == null) || (!(paramFile instanceof ShellFolder)) || (((paramFile instanceof ShellFolder)) && (((ShellFolder)paramFile).isFileSystem())))
    {
      if (isFileSystem()) {
        return super.compareTo(paramFile);
      }
      return -1;
    }
    if (isFileSystem()) {
      return 1;
    }
    return getName().compareTo(paramFile.getName());
  }
  
  public Image getIcon(boolean paramBoolean)
  {
    return null;
  }
  
  public static ShellFolder getShellFolder(File paramFile)
    throws FileNotFoundException
  {
    if ((paramFile instanceof ShellFolder)) {
      return (ShellFolder)paramFile;
    }
    if (!paramFile.exists()) {
      throw new FileNotFoundException();
    }
    return shellFolderManager.createShellFolder(paramFile);
  }
  
  public static Object get(String paramString)
  {
    return shellFolderManager.get(paramString);
  }
  
  public static boolean isComputerNode(File paramFile)
  {
    return shellFolderManager.isComputerNode(paramFile);
  }
  
  public static boolean isFileSystemRoot(File paramFile)
  {
    return shellFolderManager.isFileSystemRoot(paramFile);
  }
  
  public static File getNormalizedFile(File paramFile)
    throws IOException
  {
    File localFile = paramFile.getCanonicalFile();
    if (paramFile.equals(localFile)) {
      return localFile;
    }
    return new File(paramFile.toURI().normalize());
  }
  
  public static void sort(List<? extends File> paramList)
  {
    if ((paramList == null) || (paramList.size() <= 1)) {
      return;
    }
    invoke(new Callable()
    {
      public Void call()
      {
        Object localObject = null;
        Iterator localIterator = val$files.iterator();
        while (localIterator.hasNext())
        {
          File localFile1 = (File)localIterator.next();
          File localFile2 = localFile1.getParentFile();
          if ((localFile2 == null) || (!(localFile1 instanceof ShellFolder)))
          {
            localObject = null;
            break;
          }
          if (localObject == null)
          {
            localObject = localFile2;
          }
          else if ((localObject != localFile2) && (!((File)localObject).equals(localFile2)))
          {
            localObject = null;
            break;
          }
        }
        if ((localObject instanceof ShellFolder)) {
          ((ShellFolder)localObject).sortChildren(val$files);
        } else {
          Collections.sort(val$files, ShellFolder.FILE_COMPARATOR);
        }
        return null;
      }
    });
  }
  
  public void sortChildren(final List<? extends File> paramList)
  {
    invoke(new Callable()
    {
      public Void call()
      {
        Collections.sort(paramList, ShellFolder.FILE_COMPARATOR);
        return null;
      }
    });
  }
  
  public boolean isAbsolute()
  {
    return (!isFileSystem()) || (super.isAbsolute());
  }
  
  public File getAbsoluteFile()
  {
    return isFileSystem() ? super.getAbsoluteFile() : this;
  }
  
  public boolean canRead()
  {
    return isFileSystem() ? super.canRead() : true;
  }
  
  public boolean canWrite()
  {
    return isFileSystem() ? super.canWrite() : false;
  }
  
  public boolean exists()
  {
    return (!isFileSystem()) || (isFileSystemRoot(this)) || (super.exists());
  }
  
  public boolean isDirectory()
  {
    return isFileSystem() ? super.isDirectory() : true;
  }
  
  public boolean isFile()
  {
    return !isDirectory() ? true : isFileSystem() ? super.isFile() : false;
  }
  
  public long lastModified()
  {
    return isFileSystem() ? super.lastModified() : 0L;
  }
  
  public long length()
  {
    return isFileSystem() ? super.length() : 0L;
  }
  
  public boolean createNewFile()
    throws IOException
  {
    return isFileSystem() ? super.createNewFile() : false;
  }
  
  public boolean delete()
  {
    return isFileSystem() ? super.delete() : false;
  }
  
  public void deleteOnExit()
  {
    if (isFileSystem()) {
      super.deleteOnExit();
    }
  }
  
  public boolean mkdir()
  {
    return isFileSystem() ? super.mkdir() : false;
  }
  
  public boolean mkdirs()
  {
    return isFileSystem() ? super.mkdirs() : false;
  }
  
  public boolean renameTo(File paramFile)
  {
    return isFileSystem() ? super.renameTo(paramFile) : false;
  }
  
  public boolean setLastModified(long paramLong)
  {
    return isFileSystem() ? super.setLastModified(paramLong) : false;
  }
  
  public boolean setReadOnly()
  {
    return isFileSystem() ? super.setReadOnly() : false;
  }
  
  public String toString()
  {
    return isFileSystem() ? super.toString() : getDisplayName();
  }
  
  public static ShellFolderColumnInfo[] getFolderColumns(File paramFile)
  {
    ShellFolderColumnInfo[] arrayOfShellFolderColumnInfo = null;
    if ((paramFile instanceof ShellFolder)) {
      arrayOfShellFolderColumnInfo = ((ShellFolder)paramFile).getFolderColumns();
    }
    if (arrayOfShellFolderColumnInfo == null) {
      arrayOfShellFolderColumnInfo = new ShellFolderColumnInfo[] { new ShellFolderColumnInfo("FileChooser.fileNameHeaderText", Integer.valueOf(150), Integer.valueOf(10), true, null, FILE_COMPARATOR), new ShellFolderColumnInfo("FileChooser.fileSizeHeaderText", Integer.valueOf(75), Integer.valueOf(4), true, null, DEFAULT_COMPARATOR, true), new ShellFolderColumnInfo("FileChooser.fileDateHeaderText", Integer.valueOf(130), Integer.valueOf(10), true, null, DEFAULT_COMPARATOR, true) };
    }
    return arrayOfShellFolderColumnInfo;
  }
  
  public ShellFolderColumnInfo[] getFolderColumns()
  {
    return null;
  }
  
  public static Object getFolderColumnValue(File paramFile, int paramInt)
  {
    if ((paramFile instanceof ShellFolder))
    {
      Object localObject = ((ShellFolder)paramFile).getFolderColumnValue(paramInt);
      if (localObject != null) {
        return localObject;
      }
    }
    if ((paramFile == null) || (!paramFile.exists())) {
      return null;
    }
    switch (paramInt)
    {
    case 0: 
      return paramFile;
    case 1: 
      return paramFile.isDirectory() ? null : Long.valueOf(paramFile.length());
    case 2: 
      if (isFileSystemRoot(paramFile)) {
        return null;
      }
      long l = paramFile.lastModified();
      return l == 0L ? null : new Date(l);
    }
    return null;
  }
  
  public Object getFolderColumnValue(int paramInt)
  {
    return null;
  }
  
  public static <T> T invoke(Callable<T> paramCallable)
  {
    try
    {
      return (T)invoke(paramCallable, RuntimeException.class);
    }
    catch (InterruptedException localInterruptedException) {}
    return null;
  }
  
  public static <T, E extends Throwable> T invoke(Callable<T> paramCallable, Class<E> paramClass)
    throws InterruptedException, Throwable
  {
    try
    {
      return (T)invoker.invoke(paramCallable);
    }
    catch (Exception localException)
    {
      if ((localException instanceof RuntimeException)) {
        throw ((RuntimeException)localException);
      }
      if ((localException instanceof InterruptedException))
      {
        Thread.currentThread().interrupt();
        throw ((InterruptedException)localException);
      }
      if (paramClass.isInstance(localException)) {
        throw ((Throwable)paramClass.cast(localException));
      }
      throw new RuntimeException("Unexpected error", localException);
    }
  }
  
  static
  {
    String str = (String)Toolkit.getDefaultToolkit().getDesktopProperty("Shell.shellFolderManager");
    Class localClass = null;
    try
    {
      localClass = Class.forName(str, false, null);
      if (!ShellFolderManager.class.isAssignableFrom(localClass)) {
        localClass = null;
      }
    }
    catch (ClassNotFoundException localClassNotFoundException) {}catch (NullPointerException localNullPointerException) {}catch (SecurityException localSecurityException) {}
    if (localClass == null) {
      localClass = ShellFolderManager.class;
    }
    try
    {
      shellFolderManager = (ShellFolderManager)localClass.newInstance();
    }
    catch (InstantiationException localInstantiationException)
    {
      throw new Error("Could not instantiate Shell Folder Manager: " + localClass.getName());
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new Error("Could not access Shell Folder Manager: " + localClass.getName());
    }
  }
  
  public static abstract interface Invoker
  {
    public abstract <T> T invoke(Callable<T> paramCallable)
      throws Exception;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\shell\ShellFolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */