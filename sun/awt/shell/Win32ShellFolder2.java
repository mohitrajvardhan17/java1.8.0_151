package sun.awt.shell;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;

final class Win32ShellFolder2
  extends ShellFolder
{
  public static final int DESKTOP = 0;
  public static final int INTERNET = 1;
  public static final int PROGRAMS = 2;
  public static final int CONTROLS = 3;
  public static final int PRINTERS = 4;
  public static final int PERSONAL = 5;
  public static final int FAVORITES = 6;
  public static final int STARTUP = 7;
  public static final int RECENT = 8;
  public static final int SENDTO = 9;
  public static final int BITBUCKET = 10;
  public static final int STARTMENU = 11;
  public static final int DESKTOPDIRECTORY = 16;
  public static final int DRIVES = 17;
  public static final int NETWORK = 18;
  public static final int NETHOOD = 19;
  public static final int FONTS = 20;
  public static final int TEMPLATES = 21;
  public static final int COMMON_STARTMENU = 22;
  public static final int COMMON_PROGRAMS = 23;
  public static final int COMMON_STARTUP = 24;
  public static final int COMMON_DESKTOPDIRECTORY = 25;
  public static final int APPDATA = 26;
  public static final int PRINTHOOD = 27;
  public static final int ALTSTARTUP = 29;
  public static final int COMMON_ALTSTARTUP = 30;
  public static final int COMMON_FAVORITES = 31;
  public static final int INTERNET_CACHE = 32;
  public static final int COOKIES = 33;
  public static final int HISTORY = 34;
  public static final int ATTRIB_CANCOPY = 1;
  public static final int ATTRIB_CANMOVE = 2;
  public static final int ATTRIB_CANLINK = 4;
  public static final int ATTRIB_CANRENAME = 16;
  public static final int ATTRIB_CANDELETE = 32;
  public static final int ATTRIB_HASPROPSHEET = 64;
  public static final int ATTRIB_DROPTARGET = 256;
  public static final int ATTRIB_LINK = 65536;
  public static final int ATTRIB_SHARE = 131072;
  public static final int ATTRIB_READONLY = 262144;
  public static final int ATTRIB_GHOSTED = 524288;
  public static final int ATTRIB_HIDDEN = 524288;
  public static final int ATTRIB_FILESYSANCESTOR = 268435456;
  public static final int ATTRIB_FOLDER = 536870912;
  public static final int ATTRIB_FILESYSTEM = 1073741824;
  public static final int ATTRIB_HASSUBFOLDER = Integer.MIN_VALUE;
  public static final int ATTRIB_VALIDATE = 16777216;
  public static final int ATTRIB_REMOVABLE = 33554432;
  public static final int ATTRIB_COMPRESSED = 67108864;
  public static final int ATTRIB_BROWSABLE = 134217728;
  public static final int ATTRIB_NONENUMERATED = 1048576;
  public static final int ATTRIB_NEWCONTENT = 2097152;
  public static final int SHGDN_NORMAL = 0;
  public static final int SHGDN_INFOLDER = 1;
  public static final int SHGDN_INCLUDE_NONFILESYS = 8192;
  public static final int SHGDN_FORADDRESSBAR = 16384;
  public static final int SHGDN_FORPARSING = 32768;
  FolderDisposer disposer = new FolderDisposer();
  private long pIShellIcon = -1L;
  private String folderType = null;
  private String displayName = null;
  private Image smallIcon = null;
  private Image largeIcon = null;
  private Boolean isDir = null;
  private boolean isPersonal;
  private volatile Boolean cachedIsFileSystem;
  private volatile Boolean cachedIsLink;
  private static Map smallSystemImages = new HashMap();
  private static Map largeSystemImages = new HashMap();
  private static Map smallLinkedSystemImages = new HashMap();
  private static Map largeLinkedSystemImages = new HashMap();
  private static final int LVCFMT_LEFT = 0;
  private static final int LVCFMT_RIGHT = 1;
  private static final int LVCFMT_CENTER = 2;
  
  private static native void initIDs();
  
  private void setIShellFolder(long paramLong)
  {
    disposer.pIShellFolder = paramLong;
  }
  
  private void setRelativePIDL(long paramLong)
  {
    disposer.relativePIDL = paramLong;
  }
  
  private static String composePathForCsidl(int paramInt)
    throws IOException, InterruptedException
  {
    String str = getFileSystemPath(paramInt);
    return str == null ? "ShellFolder: 0x" + Integer.toHexString(paramInt) : str;
  }
  
  Win32ShellFolder2(final int paramInt)
    throws IOException, InterruptedException
  {
    super(null, composePathForCsidl(paramInt));
    invoke(new Callable()
    {
      public Void call()
        throws InterruptedException
      {
        if (paramInt == 0)
        {
          Win32ShellFolder2.this.initDesktop();
        }
        else
        {
          Win32ShellFolder2.this.initSpecial(Win32ShellFolder2.access$200(getDesktop()), paramInt);
          long l1 = disposer.relativePIDL;
          parent = getDesktop();
          while (l1 != 0L)
          {
            long l2 = Win32ShellFolder2.copyFirstPIDLEntry(l1);
            if (l2 == 0L) {
              break;
            }
            l1 = Win32ShellFolder2.getNextPIDLEntry(l1);
            if (l1 != 0L) {
              parent = new Win32ShellFolder2((Win32ShellFolder2)parent, l2);
            } else {
              disposer.relativePIDL = l2;
            }
          }
        }
        return null;
      }
    }, InterruptedException.class);
    Disposer.addRecord(this, disposer);
  }
  
  Win32ShellFolder2(Win32ShellFolder2 paramWin32ShellFolder2, long paramLong1, long paramLong2, String paramString)
  {
    super(paramWin32ShellFolder2, paramString != null ? paramString : "ShellFolder: ");
    disposer.pIShellFolder = paramLong1;
    disposer.relativePIDL = paramLong2;
    Disposer.addRecord(this, disposer);
  }
  
  Win32ShellFolder2(Win32ShellFolder2 paramWin32ShellFolder2, final long paramLong)
    throws InterruptedException
  {
    super(paramWin32ShellFolder2, (String)invoke(new Callable()
    {
      public String call()
      {
        return Win32ShellFolder2.getFileSystemPath(Win32ShellFolder2.access$200(Win32ShellFolder2.this), paramLong);
      }
    }, RuntimeException.class));
    disposer.relativePIDL = paramLong;
    Disposer.addRecord(this, disposer);
  }
  
  private native void initDesktop();
  
  private native void initSpecial(long paramLong, int paramInt);
  
  public void setIsPersonal()
  {
    isPersonal = true;
  }
  
  protected Object writeReplace()
    throws ObjectStreamException
  {
    invoke(new Callable()
    {
      public File call()
      {
        if (isFileSystem()) {
          return new File(getPath());
        }
        Win32ShellFolder2 localWin32ShellFolder21 = Win32ShellFolderManager2.getDrives();
        if (localWin32ShellFolder21 != null)
        {
          File[] arrayOfFile = localWin32ShellFolder21.listFiles();
          if (arrayOfFile != null) {
            for (int i = 0; i < arrayOfFile.length; i++) {
              if ((arrayOfFile[i] instanceof Win32ShellFolder2))
              {
                Win32ShellFolder2 localWin32ShellFolder22 = (Win32ShellFolder2)arrayOfFile[i];
                if ((localWin32ShellFolder22.isFileSystem()) && (!localWin32ShellFolder22.hasAttribute(33554432))) {
                  return new File(localWin32ShellFolder22.getPath());
                }
              }
            }
          }
        }
        return new File("C:\\");
      }
    });
  }
  
  protected void dispose()
  {
    disposer.dispose();
  }
  
  static native long getNextPIDLEntry(long paramLong);
  
  static native long copyFirstPIDLEntry(long paramLong);
  
  private static native long combinePIDLs(long paramLong1, long paramLong2);
  
  static native void releasePIDL(long paramLong);
  
  private static native void releaseIShellFolder(long paramLong);
  
  private long getIShellFolder()
  {
    if (disposer.pIShellFolder == 0L) {
      try
      {
        disposer.pIShellFolder = ((Long)invoke(new Callable()
        {
          public Long call()
          {
            assert (isDirectory());
            assert (parent != null);
            long l1 = getParentIShellFolder();
            if (l1 == 0L) {
              throw new InternalError("Parent IShellFolder was null for " + getAbsolutePath());
            }
            long l2 = Win32ShellFolder2.bindToObject(l1, disposer.relativePIDL);
            if (l2 == 0L) {
              throw new InternalError("Unable to bind " + getAbsolutePath() + " to parent");
            }
            return Long.valueOf(l2);
          }
        }, RuntimeException.class)).longValue();
      }
      catch (InterruptedException localInterruptedException) {}
    }
    return disposer.pIShellFolder;
  }
  
  public long getParentIShellFolder()
  {
    Win32ShellFolder2 localWin32ShellFolder2 = (Win32ShellFolder2)getParentFile();
    if (localWin32ShellFolder2 == null) {
      return getIShellFolder();
    }
    return localWin32ShellFolder2.getIShellFolder();
  }
  
  public long getRelativePIDL()
  {
    if (disposer.relativePIDL == 0L) {
      throw new InternalError("Should always have a relative PIDL");
    }
    return disposer.relativePIDL;
  }
  
  private long getAbsolutePIDL()
  {
    if (parent == null) {
      return getRelativePIDL();
    }
    if (disposer.absolutePIDL == 0L) {
      disposer.absolutePIDL = combinePIDLs(((Win32ShellFolder2)parent).getAbsolutePIDL(), getRelativePIDL());
    }
    return disposer.absolutePIDL;
  }
  
  public Win32ShellFolder2 getDesktop()
  {
    return Win32ShellFolderManager2.getDesktop();
  }
  
  public long getDesktopIShellFolder()
  {
    return getDesktop().getIShellFolder();
  }
  
  private static boolean pathsEqual(String paramString1, String paramString2)
  {
    return paramString1.equalsIgnoreCase(paramString2);
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject == null) || (!(paramObject instanceof Win32ShellFolder2)))
    {
      if (!(paramObject instanceof File)) {
        return super.equals(paramObject);
      }
      return pathsEqual(getPath(), ((File)paramObject).getPath());
    }
    Win32ShellFolder2 localWin32ShellFolder2 = (Win32ShellFolder2)paramObject;
    if (((parent == null) && (parent != null)) || ((parent != null) && (parent == null))) {
      return false;
    }
    if ((isFileSystem()) && (localWin32ShellFolder2.isFileSystem())) {
      return (pathsEqual(getPath(), localWin32ShellFolder2.getPath())) && ((parent == parent) || (parent.equals(parent)));
    }
    if ((parent == parent) || (parent.equals(parent))) {
      try
      {
        return pidlsEqual(getParentIShellFolder(), disposer.relativePIDL, disposer.relativePIDL);
      }
      catch (InterruptedException localInterruptedException)
      {
        return false;
      }
    }
    return false;
  }
  
  private static boolean pidlsEqual(long paramLong1, long paramLong2, final long paramLong3)
    throws InterruptedException
  {
    ((Boolean)invoke(new Callable()
    {
      public Boolean call()
      {
        return Boolean.valueOf(Win32ShellFolder2.compareIDs(val$pIShellFolder, paramLong3, val$pidl2) == 0);
      }
    }, RuntimeException.class)).booleanValue();
  }
  
  private static native int compareIDs(long paramLong1, long paramLong2, long paramLong3);
  
  public boolean isFileSystem()
  {
    if (cachedIsFileSystem == null) {
      cachedIsFileSystem = Boolean.valueOf(hasAttribute(1073741824));
    }
    return cachedIsFileSystem.booleanValue();
  }
  
  public boolean hasAttribute(final int paramInt)
  {
    Boolean localBoolean = (Boolean)invoke(new Callable()
    {
      public Boolean call()
      {
        return Boolean.valueOf((Win32ShellFolder2.getAttributes0(getParentIShellFolder(), getRelativePIDL(), paramInt) & paramInt) != 0);
      }
    });
    return (localBoolean != null) && (localBoolean.booleanValue());
  }
  
  private static native int getAttributes0(long paramLong1, long paramLong2, int paramInt);
  
  private static String getFileSystemPath(long paramLong1, long paramLong2)
  {
    int i = 536936448;
    if ((paramLong1 == Win32ShellFolderManager2.getNetwork().getIShellFolder()) && (getAttributes0(paramLong1, paramLong2, i) == i))
    {
      String str = getFileSystemPath(Win32ShellFolderManager2.getDesktop().getIShellFolder(), getLinkLocation(paramLong1, paramLong2, false));
      if ((str != null) && (str.startsWith("\\\\"))) {
        return str;
      }
    }
    return getDisplayNameOf(paramLong1, paramLong2, 32768);
  }
  
  static String getFileSystemPath(int paramInt)
    throws IOException, InterruptedException
  {
    String str = (String)invoke(new Callable()
    {
      public String call()
        throws IOException
      {
        return Win32ShellFolder2.getFileSystemPath0(val$csidl);
      }
    }, IOException.class);
    if (str != null)
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null) {
        localSecurityManager.checkRead(str);
      }
    }
    return str;
  }
  
  private static native String getFileSystemPath0(int paramInt)
    throws IOException;
  
  private static boolean isNetworkRoot(String paramString)
  {
    return (paramString.equals("\\\\")) || (paramString.equals("\\")) || (paramString.equals("//")) || (paramString.equals("/"));
  }
  
  public File getParentFile()
  {
    return parent;
  }
  
  public boolean isDirectory()
  {
    if (isDir == null) {
      if ((hasAttribute(536870912)) && (!hasAttribute(134217728)))
      {
        isDir = Boolean.TRUE;
      }
      else if (isLink())
      {
        ShellFolder localShellFolder = getLinkLocation(false);
        isDir = Boolean.valueOf((localShellFolder != null) && (localShellFolder.isDirectory()));
      }
      else
      {
        isDir = Boolean.FALSE;
      }
    }
    return isDir.booleanValue();
  }
  
  private long getEnumObjects(final boolean paramBoolean)
    throws InterruptedException
  {
    ((Long)invoke(new Callable()
    {
      public Long call()
      {
        boolean bool = disposer.pIShellFolder == getDesktopIShellFolder();
        return Long.valueOf(Win32ShellFolder2.this.getEnumObjects(disposer.pIShellFolder, bool, paramBoolean));
      }
    }, RuntimeException.class)).longValue();
  }
  
  private native long getEnumObjects(long paramLong, boolean paramBoolean1, boolean paramBoolean2);
  
  private native long getNextChild(long paramLong);
  
  private native void releaseEnumObjects(long paramLong);
  
  private static native long bindToObject(long paramLong1, long paramLong2);
  
  public File[] listFiles(final boolean paramBoolean)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkRead(getPath());
    }
    try
    {
      (File[])invoke(new Callable()
      {
        public File[] call()
          throws InterruptedException
        {
          if (!isDirectory()) {
            return null;
          }
          if ((isLink()) && (!hasAttribute(536870912))) {
            return new File[0];
          }
          Win32ShellFolder2 localWin32ShellFolder21 = Win32ShellFolderManager2.getDesktop();
          Win32ShellFolder2 localWin32ShellFolder22 = Win32ShellFolderManager2.getPersonal();
          long l1 = Win32ShellFolder2.this.getIShellFolder();
          ArrayList localArrayList = new ArrayList();
          long l2 = Win32ShellFolder2.this.getEnumObjects(paramBoolean);
          if (l2 != 0L) {
            try
            {
              int i = 1342177280;
              do
              {
                long l3 = Win32ShellFolder2.this.getNextChild(l2);
                int j = 1;
                if ((l3 != 0L) && ((Win32ShellFolder2.getAttributes0(l1, l3, i) & i) != 0))
                {
                  Win32ShellFolder2 localWin32ShellFolder23;
                  if ((equals(localWin32ShellFolder21)) && (localWin32ShellFolder22 != null) && (Win32ShellFolder2.pidlsEqual(l1, l3, disposer.relativePIDL)))
                  {
                    localWin32ShellFolder23 = localWin32ShellFolder22;
                  }
                  else
                  {
                    localWin32ShellFolder23 = new Win32ShellFolder2(Win32ShellFolder2.this, l3);
                    j = 0;
                  }
                  localArrayList.add(localWin32ShellFolder23);
                }
                if (j != 0) {
                  Win32ShellFolder2.releasePIDL(l3);
                }
                if (l3 == 0L) {
                  break;
                }
              } while (!Thread.currentThread().isInterrupted());
            }
            finally
            {
              Win32ShellFolder2.this.releaseEnumObjects(l2);
            }
          }
          return Thread.currentThread().isInterrupted() ? new File[0] : (File[])localArrayList.toArray(new ShellFolder[localArrayList.size()]);
        }
      }, InterruptedException.class);
    }
    catch (InterruptedException localInterruptedException) {}
    return new File[0];
  }
  
  Win32ShellFolder2 getChildByPath(final String paramString)
    throws InterruptedException
  {
    (Win32ShellFolder2)invoke(new Callable()
    {
      public Win32ShellFolder2 call()
        throws InterruptedException
      {
        long l1 = Win32ShellFolder2.this.getIShellFolder();
        long l2 = Win32ShellFolder2.this.getEnumObjects(true);
        Win32ShellFolder2 localWin32ShellFolder2 = null;
        long l3;
        while ((l3 = Win32ShellFolder2.this.getNextChild(l2)) != 0L)
        {
          if (Win32ShellFolder2.getAttributes0(l1, l3, 1073741824) != 0)
          {
            String str = Win32ShellFolder2.getFileSystemPath(l1, l3);
            if ((str != null) && (str.equalsIgnoreCase(paramString)))
            {
              long l4 = Win32ShellFolder2.bindToObject(l1, l3);
              localWin32ShellFolder2 = new Win32ShellFolder2(Win32ShellFolder2.this, l4, l3, str);
              break;
            }
          }
          Win32ShellFolder2.releasePIDL(l3);
        }
        Win32ShellFolder2.this.releaseEnumObjects(l2);
        return localWin32ShellFolder2;
      }
    }, InterruptedException.class);
  }
  
  public boolean isLink()
  {
    if (cachedIsLink == null) {
      cachedIsLink = Boolean.valueOf(hasAttribute(65536));
    }
    return cachedIsLink.booleanValue();
  }
  
  public boolean isHidden()
  {
    return hasAttribute(524288);
  }
  
  private static native long getLinkLocation(long paramLong1, long paramLong2, boolean paramBoolean);
  
  public ShellFolder getLinkLocation()
  {
    return getLinkLocation(true);
  }
  
  private ShellFolder getLinkLocation(final boolean paramBoolean)
  {
    (ShellFolder)invoke(new Callable()
    {
      public ShellFolder call()
      {
        if (!isLink()) {
          return null;
        }
        Win32ShellFolder2 localWin32ShellFolder2 = null;
        long l = Win32ShellFolder2.getLinkLocation(getParentIShellFolder(), getRelativePIDL(), paramBoolean);
        if (l != 0L) {
          try
          {
            localWin32ShellFolder2 = Win32ShellFolderManager2.createShellFolderFromRelativePIDL(getDesktop(), l);
          }
          catch (InterruptedException localInterruptedException) {}catch (InternalError localInternalError) {}
        }
        return localWin32ShellFolder2;
      }
    });
  }
  
  long parseDisplayName(final String paramString)
    throws IOException, InterruptedException
  {
    ((Long)invoke(new Callable()
    {
      public Long call()
        throws IOException
      {
        return Long.valueOf(Win32ShellFolder2.parseDisplayName0(Win32ShellFolder2.access$200(Win32ShellFolder2.this), paramString));
      }
    }, IOException.class)).longValue();
  }
  
  private static native long parseDisplayName0(long paramLong, String paramString)
    throws IOException;
  
  private static native String getDisplayNameOf(long paramLong1, long paramLong2, int paramInt);
  
  public String getDisplayName()
  {
    if (displayName == null) {
      displayName = ((String)invoke(new Callable()
      {
        public String call()
        {
          return Win32ShellFolder2.getDisplayNameOf(getParentIShellFolder(), getRelativePIDL(), 0);
        }
      }));
    }
    return displayName;
  }
  
  private static native String getFolderType(long paramLong);
  
  public String getFolderType()
  {
    if (folderType == null)
    {
      final long l = getAbsolutePIDL();
      folderType = ((String)invoke(new Callable()
      {
        public String call()
        {
          return Win32ShellFolder2.getFolderType(l);
        }
      }));
    }
    return folderType;
  }
  
  private native String getExecutableType(String paramString);
  
  public String getExecutableType()
  {
    if (!isFileSystem()) {
      return null;
    }
    return getExecutableType(getAbsolutePath());
  }
  
  private static native long getIShellIcon(long paramLong);
  
  private static native int getIconIndex(long paramLong1, long paramLong2);
  
  private static native long getIcon(String paramString, boolean paramBoolean);
  
  private static native long extractIcon(long paramLong1, long paramLong2, boolean paramBoolean);
  
  private static native long getSystemIcon(int paramInt);
  
  private static native long getIconResource(String paramString, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean);
  
  private static native int[] getIconBits(long paramLong, int paramInt);
  
  private static native void disposeIcon(long paramLong);
  
  static native int[] getStandardViewButton0(int paramInt);
  
  private long getIShellIcon()
  {
    if (pIShellIcon == -1L) {
      pIShellIcon = getIShellIcon(getIShellFolder());
    }
    return pIShellIcon;
  }
  
  private static Image makeIcon(long paramLong, boolean paramBoolean)
  {
    if ((paramLong != 0L) && (paramLong != -1L))
    {
      int i = paramBoolean ? 32 : 16;
      int[] arrayOfInt = getIconBits(paramLong, i);
      if (arrayOfInt != null)
      {
        BufferedImage localBufferedImage = new BufferedImage(i, i, 2);
        localBufferedImage.setRGB(0, 0, i, i, arrayOfInt, 0, i);
        return localBufferedImage;
      }
    }
    return null;
  }
  
  public Image getIcon(final boolean paramBoolean)
  {
    Image localImage = paramBoolean ? largeIcon : smallIcon;
    if (localImage == null)
    {
      localImage = (Image)invoke(new Callable()
      {
        public Image call()
        {
          Image localImage = null;
          long l1;
          if (isFileSystem())
          {
            l1 = parent != null ? ((Win32ShellFolder2)parent).getIShellIcon() : 0L;
            long l2 = getRelativePIDL();
            int i = Win32ShellFolder2.getIconIndex(l1, l2);
            if (i > 0)
            {
              Map localMap;
              if (isLink()) {
                localMap = paramBoolean ? Win32ShellFolder2.largeLinkedSystemImages : Win32ShellFolder2.smallLinkedSystemImages;
              } else {
                localMap = paramBoolean ? Win32ShellFolder2.largeSystemImages : Win32ShellFolder2.smallSystemImages;
              }
              localImage = (Image)localMap.get(Integer.valueOf(i));
              if (localImage == null)
              {
                long l3 = Win32ShellFolder2.getIcon(getAbsolutePath(), paramBoolean);
                localImage = Win32ShellFolder2.makeIcon(l3, paramBoolean);
                Win32ShellFolder2.disposeIcon(l3);
                if (localImage != null) {
                  localMap.put(Integer.valueOf(i), localImage);
                }
              }
            }
          }
          if (localImage == null)
          {
            l1 = Win32ShellFolder2.extractIcon(getParentIShellFolder(), getRelativePIDL(), paramBoolean);
            localImage = Win32ShellFolder2.makeIcon(l1, paramBoolean);
            Win32ShellFolder2.disposeIcon(l1);
          }
          if (localImage == null) {
            localImage = Win32ShellFolder2.this.getIcon(paramBoolean);
          }
          return localImage;
        }
      });
      if (paramBoolean) {
        largeIcon = localImage;
      } else {
        smallIcon = localImage;
      }
    }
    return localImage;
  }
  
  static Image getSystemIcon(SystemIcon paramSystemIcon)
  {
    long l = getSystemIcon(paramSystemIcon.getIconID());
    Image localImage = makeIcon(l, true);
    disposeIcon(l);
    return localImage;
  }
  
  static Image getShell32Icon(int paramInt, boolean paramBoolean)
  {
    boolean bool = true;
    int i = paramBoolean ? 32 : 16;
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    String str = (String)localToolkit.getDesktopProperty("win.icon.shellIconBPP");
    if (str != null) {
      bool = str.equals("4");
    }
    long l = getIconResource("shell32.dll", paramInt, i, i, bool);
    if (l != 0L)
    {
      Image localImage = makeIcon(l, paramBoolean);
      disposeIcon(l);
      return localImage;
    }
    return null;
  }
  
  public File getCanonicalFile()
    throws IOException
  {
    return this;
  }
  
  public boolean isSpecial()
  {
    return (isPersonal) || (!isFileSystem()) || (this == getDesktop());
  }
  
  public int compareTo(File paramFile)
  {
    if (!(paramFile instanceof Win32ShellFolder2))
    {
      if ((isFileSystem()) && (!isSpecial())) {
        return super.compareTo(paramFile);
      }
      return -1;
    }
    return Win32ShellFolderManager2.compareShellFolders(this, (Win32ShellFolder2)paramFile);
  }
  
  public ShellFolderColumnInfo[] getFolderColumns()
  {
    (ShellFolderColumnInfo[])invoke(new Callable()
    {
      public ShellFolderColumnInfo[] call()
      {
        ShellFolderColumnInfo[] arrayOfShellFolderColumnInfo = Win32ShellFolder2.this.doGetColumnInfo(Win32ShellFolder2.access$200(Win32ShellFolder2.this));
        if (arrayOfShellFolderColumnInfo != null)
        {
          ArrayList localArrayList = new ArrayList();
          for (int i = 0; i < arrayOfShellFolderColumnInfo.length; i++)
          {
            ShellFolderColumnInfo localShellFolderColumnInfo = arrayOfShellFolderColumnInfo[i];
            if (localShellFolderColumnInfo != null)
            {
              localShellFolderColumnInfo.setAlignment(Integer.valueOf(localShellFolderColumnInfo.getAlignment().intValue() == 2 ? 0 : localShellFolderColumnInfo.getAlignment().intValue() == 1 ? 4 : 10));
              localShellFolderColumnInfo.setComparator(new Win32ShellFolder2.ColumnComparator(Win32ShellFolder2.this, i));
              localArrayList.add(localShellFolderColumnInfo);
            }
          }
          arrayOfShellFolderColumnInfo = new ShellFolderColumnInfo[localArrayList.size()];
          localArrayList.toArray(arrayOfShellFolderColumnInfo);
        }
        return arrayOfShellFolderColumnInfo;
      }
    });
  }
  
  public Object getFolderColumnValue(final int paramInt)
  {
    invoke(new Callable()
    {
      public Object call()
      {
        return Win32ShellFolder2.this.doGetColumnValue(getParentIShellFolder(), getRelativePIDL(), paramInt);
      }
    });
  }
  
  private native ShellFolderColumnInfo[] doGetColumnInfo(long paramLong);
  
  private native Object doGetColumnValue(long paramLong1, long paramLong2, int paramInt);
  
  private static native int compareIDsByColumn(long paramLong1, long paramLong2, long paramLong3, int paramInt);
  
  public void sortChildren(final List<? extends File> paramList)
  {
    invoke(new Callable()
    {
      public Void call()
      {
        Collections.sort(paramList, new Win32ShellFolder2.ColumnComparator(Win32ShellFolder2.this, 0));
        return null;
      }
    });
  }
  
  static {}
  
  private static class ColumnComparator
    implements Comparator<File>
  {
    private final Win32ShellFolder2 shellFolder;
    private final int columnIdx;
    
    public ColumnComparator(Win32ShellFolder2 paramWin32ShellFolder2, int paramInt)
    {
      shellFolder = paramWin32ShellFolder2;
      columnIdx = paramInt;
    }
    
    public int compare(final File paramFile1, final File paramFile2)
    {
      Integer localInteger = (Integer)ShellFolder.invoke(new Callable()
      {
        public Integer call()
        {
          if (((paramFile1 instanceof Win32ShellFolder2)) && ((paramFile2 instanceof Win32ShellFolder2))) {
            return Integer.valueOf(Win32ShellFolder2.compareIDsByColumn(Win32ShellFolder2.access$200(Win32ShellFolder2.this), ((Win32ShellFolder2)paramFile1).getRelativePIDL(), ((Win32ShellFolder2)paramFile2).getRelativePIDL(), columnIdx));
          }
          return Integer.valueOf(0);
        }
      });
      return localInteger == null ? 0 : localInteger.intValue();
    }
  }
  
  static class FolderDisposer
    implements DisposerRecord
  {
    long absolutePIDL;
    long pIShellFolder;
    long relativePIDL;
    boolean disposed;
    
    FolderDisposer() {}
    
    public void dispose()
    {
      if (disposed) {
        return;
      }
      ShellFolder.invoke(new Callable()
      {
        public Void call()
        {
          if (relativePIDL != 0L) {
            Win32ShellFolder2.releasePIDL(relativePIDL);
          }
          if (absolutePIDL != 0L) {
            Win32ShellFolder2.releasePIDL(absolutePIDL);
          }
          if (pIShellFolder != 0L) {
            Win32ShellFolder2.releaseIShellFolder(pIShellFolder);
          }
          return null;
        }
      });
      disposed = true;
    }
  }
  
  public static enum SystemIcon
  {
    IDI_APPLICATION(32512),  IDI_HAND(32513),  IDI_ERROR(32513),  IDI_QUESTION(32514),  IDI_EXCLAMATION(32515),  IDI_WARNING(32515),  IDI_ASTERISK(32516),  IDI_INFORMATION(32516),  IDI_WINLOGO(32517);
    
    private final int iconID;
    
    private SystemIcon(int paramInt)
    {
      iconID = paramInt;
    }
    
    public int getIconID()
    {
      return iconID;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\shell\Win32ShellFolder2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */