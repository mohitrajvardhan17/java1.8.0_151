package sun.nio.fs;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystemException;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;
import java.nio.file.attribute.UserDefinedFileAttributeView;

class WindowsFileStore
  extends FileStore
{
  private final String root;
  private final WindowsNativeDispatcher.VolumeInformation volInfo;
  private final int volType;
  private final String displayName;
  
  private WindowsFileStore(String paramString)
    throws WindowsException
  {
    assert (paramString.charAt(paramString.length() - 1) == '\\');
    root = paramString;
    volInfo = WindowsNativeDispatcher.GetVolumeInformation(paramString);
    volType = WindowsNativeDispatcher.GetDriveType(paramString);
    String str = volInfo.volumeName();
    if (str.length() > 0) {
      displayName = str;
    } else {
      displayName = (volType == 2 ? "Removable Disk" : "");
    }
  }
  
  static WindowsFileStore create(String paramString, boolean paramBoolean)
    throws IOException
  {
    try
    {
      return new WindowsFileStore(paramString);
    }
    catch (WindowsException localWindowsException)
    {
      if ((paramBoolean) && (localWindowsException.lastError() == 21)) {
        return null;
      }
      localWindowsException.rethrowAsIOException(paramString);
    }
    return null;
  }
  
  static WindowsFileStore create(WindowsPath paramWindowsPath)
    throws IOException
  {
    try
    {
      String str;
      if (paramWindowsPath.getFileSystem().supportsLinks())
      {
        str = WindowsLinkSupport.getFinalPath(paramWindowsPath, true);
      }
      else
      {
        WindowsFileAttributes.get(paramWindowsPath, true);
        str = paramWindowsPath.getPathForWin32Calls();
      }
      try
      {
        return createFromPath(str);
      }
      catch (WindowsException localWindowsException2)
      {
        if (localWindowsException2.lastError() != 144) {
          throw localWindowsException2;
        }
        str = WindowsLinkSupport.getFinalPath(paramWindowsPath);
        if (str == null) {
          throw new FileSystemException(paramWindowsPath.getPathForExceptionMessage(), null, "Couldn't resolve path");
        }
        return createFromPath(str);
      }
      return null;
    }
    catch (WindowsException localWindowsException1)
    {
      localWindowsException1.rethrowAsIOException(paramWindowsPath);
    }
  }
  
  private static WindowsFileStore createFromPath(String paramString)
    throws WindowsException
  {
    String str = WindowsNativeDispatcher.GetVolumePathName(paramString);
    return new WindowsFileStore(str);
  }
  
  WindowsNativeDispatcher.VolumeInformation volumeInformation()
  {
    return volInfo;
  }
  
  int volumeType()
  {
    return volType;
  }
  
  public String name()
  {
    return volInfo.volumeName();
  }
  
  public String type()
  {
    return volInfo.fileSystemName();
  }
  
  public boolean isReadOnly()
  {
    return (volInfo.flags() & 0x80000) != 0;
  }
  
  private WindowsNativeDispatcher.DiskFreeSpace readDiskFreeSpace()
    throws IOException
  {
    try
    {
      return WindowsNativeDispatcher.GetDiskFreeSpaceEx(root);
    }
    catch (WindowsException localWindowsException)
    {
      localWindowsException.rethrowAsIOException(root);
    }
    return null;
  }
  
  public long getTotalSpace()
    throws IOException
  {
    return readDiskFreeSpace().totalNumberOfBytes();
  }
  
  public long getUsableSpace()
    throws IOException
  {
    return readDiskFreeSpace().freeBytesAvailable();
  }
  
  public long getUnallocatedSpace()
    throws IOException
  {
    return readDiskFreeSpace().freeBytesAvailable();
  }
  
  public <V extends FileStoreAttributeView> V getFileStoreAttributeView(Class<V> paramClass)
  {
    if (paramClass == null) {
      throw new NullPointerException();
    }
    return (FileStoreAttributeView)null;
  }
  
  public Object getAttribute(String paramString)
    throws IOException
  {
    if (paramString.equals("totalSpace")) {
      return Long.valueOf(getTotalSpace());
    }
    if (paramString.equals("usableSpace")) {
      return Long.valueOf(getUsableSpace());
    }
    if (paramString.equals("unallocatedSpace")) {
      return Long.valueOf(getUnallocatedSpace());
    }
    if (paramString.equals("volume:vsn")) {
      return Integer.valueOf(volInfo.volumeSerialNumber());
    }
    if (paramString.equals("volume:isRemovable")) {
      return Boolean.valueOf(volType == 2);
    }
    if (paramString.equals("volume:isCdrom")) {
      return Boolean.valueOf(volType == 5);
    }
    throw new UnsupportedOperationException("'" + paramString + "' not recognized");
  }
  
  public boolean supportsFileAttributeView(Class<? extends FileAttributeView> paramClass)
  {
    if (paramClass == null) {
      throw new NullPointerException();
    }
    if ((paramClass == BasicFileAttributeView.class) || (paramClass == DosFileAttributeView.class)) {
      return true;
    }
    if ((paramClass == AclFileAttributeView.class) || (paramClass == FileOwnerAttributeView.class)) {
      return (volInfo.flags() & 0x8) != 0;
    }
    if (paramClass == UserDefinedFileAttributeView.class) {
      return (volInfo.flags() & 0x40000) != 0;
    }
    return false;
  }
  
  public boolean supportsFileAttributeView(String paramString)
  {
    if ((paramString.equals("basic")) || (paramString.equals("dos"))) {
      return true;
    }
    if (paramString.equals("acl")) {
      return supportsFileAttributeView(AclFileAttributeView.class);
    }
    if (paramString.equals("owner")) {
      return supportsFileAttributeView(FileOwnerAttributeView.class);
    }
    if (paramString.equals("user")) {
      return supportsFileAttributeView(UserDefinedFileAttributeView.class);
    }
    return false;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof WindowsFileStore)) {
      return false;
    }
    WindowsFileStore localWindowsFileStore = (WindowsFileStore)paramObject;
    return root.equals(root);
  }
  
  public int hashCode()
  {
    return root.hashCode();
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(displayName);
    if (localStringBuilder.length() > 0) {
      localStringBuilder.append(" ");
    }
    localStringBuilder.append("(");
    localStringBuilder.append(root.subSequence(0, root.length() - 1));
    localStringBuilder.append(")");
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\fs\WindowsFileStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */