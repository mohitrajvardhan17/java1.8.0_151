package java.awt;

import java.awt.peer.FileDialogPeer;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.FileDialogAccessor;

public class FileDialog
  extends Dialog
{
  public static final int LOAD = 0;
  public static final int SAVE = 1;
  int mode;
  String dir;
  String file;
  private File[] files;
  private boolean multipleMode = false;
  FilenameFilter filter;
  private static final String base = "filedlg";
  private static int nameCounter = 0;
  private static final long serialVersionUID = 5035145889651310422L;
  
  private static native void initIDs();
  
  public FileDialog(Frame paramFrame)
  {
    this(paramFrame, "", 0);
  }
  
  public FileDialog(Frame paramFrame, String paramString)
  {
    this(paramFrame, paramString, 0);
  }
  
  public FileDialog(Frame paramFrame, String paramString, int paramInt)
  {
    super(paramFrame, paramString, true);
    setMode(paramInt);
    setLayout(null);
  }
  
  public FileDialog(Dialog paramDialog)
  {
    this(paramDialog, "", 0);
  }
  
  public FileDialog(Dialog paramDialog, String paramString)
  {
    this(paramDialog, paramString, 0);
  }
  
  public FileDialog(Dialog paramDialog, String paramString, int paramInt)
  {
    super(paramDialog, paramString, true);
    setMode(paramInt);
    setLayout(null);
  }
  
  /* Error */
  String constructComponentName()
  {
    // Byte code:
    //   0: ldc 8
    //   2: dup
    //   3: astore_1
    //   4: monitorenter
    //   5: new 121	java/lang/StringBuilder
    //   8: dup
    //   9: invokespecial 219	java/lang/StringBuilder:<init>	()V
    //   12: ldc 6
    //   14: invokevirtual 222	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   17: getstatic 188	java/awt/FileDialog:nameCounter	I
    //   20: dup
    //   21: iconst_1
    //   22: iadd
    //   23: putstatic 188	java/awt/FileDialog:nameCounter	I
    //   26: invokevirtual 221	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   29: invokevirtual 220	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   32: aload_1
    //   33: monitorexit
    //   34: areturn
    //   35: astore_2
    //   36: aload_1
    //   37: monitorexit
    //   38: aload_2
    //   39: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	40	0	this	FileDialog
    //   3	34	1	Ljava/lang/Object;	Object
    //   35	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   5	34	35	finally
    //   35	38	35	finally
  }
  
  public void addNotify()
  {
    synchronized (getTreeLock())
    {
      if ((parent != null) && (parent.getPeer() == null)) {
        parent.addNotify();
      }
      if (peer == null) {
        peer = getToolkit().createFileDialog(this);
      }
      super.addNotify();
    }
  }
  
  public int getMode()
  {
    return mode;
  }
  
  public void setMode(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
    case 1: 
      mode = paramInt;
      break;
    default: 
      throw new IllegalArgumentException("illegal file dialog mode");
    }
  }
  
  public String getDirectory()
  {
    return dir;
  }
  
  public void setDirectory(String paramString)
  {
    dir = ((paramString != null) && (paramString.equals("")) ? null : paramString);
    FileDialogPeer localFileDialogPeer = (FileDialogPeer)peer;
    if (localFileDialogPeer != null) {
      localFileDialogPeer.setDirectory(dir);
    }
  }
  
  public String getFile()
  {
    return file;
  }
  
  public File[] getFiles()
  {
    synchronized (getObjectLock())
    {
      if (files != null) {
        return (File[])files.clone();
      }
      return new File[0];
    }
  }
  
  private void setFiles(File[] paramArrayOfFile)
  {
    synchronized (getObjectLock())
    {
      files = paramArrayOfFile;
    }
  }
  
  public void setFile(String paramString)
  {
    file = ((paramString != null) && (paramString.equals("")) ? null : paramString);
    FileDialogPeer localFileDialogPeer = (FileDialogPeer)peer;
    if (localFileDialogPeer != null) {
      localFileDialogPeer.setFile(file);
    }
  }
  
  public void setMultipleMode(boolean paramBoolean)
  {
    synchronized (getObjectLock())
    {
      multipleMode = paramBoolean;
    }
  }
  
  /* Error */
  public boolean isMultipleMode()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 208	java/awt/FileDialog:getObjectLock	()Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 189	java/awt/FileDialog:multipleMode	Z
    //   11: aload_1
    //   12: monitorexit
    //   13: ireturn
    //   14: astore_2
    //   15: aload_1
    //   16: monitorexit
    //   17: aload_2
    //   18: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19	0	this	FileDialog
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  public FilenameFilter getFilenameFilter()
  {
    return filter;
  }
  
  public synchronized void setFilenameFilter(FilenameFilter paramFilenameFilter)
  {
    filter = paramFilenameFilter;
    FileDialogPeer localFileDialogPeer = (FileDialogPeer)peer;
    if (localFileDialogPeer != null) {
      localFileDialogPeer.setFilenameFilter(paramFilenameFilter);
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException
  {
    paramObjectInputStream.defaultReadObject();
    if ((dir != null) && (dir.equals(""))) {
      dir = null;
    }
    if ((file != null) && (file.equals(""))) {
      file = null;
    }
  }
  
  protected String paramString()
  {
    String str = super.paramString();
    str = str + ",dir= " + dir;
    str = str + ",file= " + file;
    return str + (mode == 0 ? ",load" : ",save");
  }
  
  boolean postsOldMouseEvents()
  {
    return false;
  }
  
  static
  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless()) {
      initIDs();
    }
    AWTAccessor.setFileDialogAccessor(new AWTAccessor.FileDialogAccessor()
    {
      public void setFiles(FileDialog paramAnonymousFileDialog, File[] paramAnonymousArrayOfFile)
      {
        paramAnonymousFileDialog.setFiles(paramAnonymousArrayOfFile);
      }
      
      public void setFile(FileDialog paramAnonymousFileDialog, String paramAnonymousString)
      {
        file = ("".equals(paramAnonymousString) ? null : paramAnonymousString);
      }
      
      public void setDirectory(FileDialog paramAnonymousFileDialog, String paramAnonymousString)
      {
        dir = ("".equals(paramAnonymousString) ? null : paramAnonymousString);
      }
      
      /* Error */
      public boolean isMultipleMode(FileDialog paramAnonymousFileDialog)
      {
        // Byte code:
        //   0: aload_1
        //   1: invokevirtual 49	java/awt/FileDialog:getObjectLock	()Ljava/lang/Object;
        //   4: dup
        //   5: astore_2
        //   6: monitorenter
        //   7: aload_1
        //   8: invokestatic 48	java/awt/FileDialog:access$100	(Ljava/awt/FileDialog;)Z
        //   11: aload_2
        //   12: monitorexit
        //   13: ireturn
        //   14: astore_3
        //   15: aload_2
        //   16: monitorexit
        //   17: aload_3
        //   18: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	19	0	this	1
        //   0	19	1	paramAnonymousFileDialog	FileDialog
        //   5	11	2	Ljava/lang/Object;	Object
        //   14	4	3	localObject1	Object
        // Exception table:
        //   from	to	target	type
        //   7	13	14	finally
        //   14	17	14	finally
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\FileDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */