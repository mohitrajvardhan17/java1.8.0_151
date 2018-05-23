package sun.nio.fs;

import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;
import java.security.AccessController;
import java.util.concurrent.TimeUnit;
import sun.misc.Unsafe;
import sun.security.action.GetPropertyAction;

class WindowsFileAttributes
  implements DosFileAttributes
{
  private static final Unsafe unsafe = ;
  private static final short SIZEOF_FILE_INFORMATION = 52;
  private static final short OFFSETOF_FILE_INFORMATION_ATTRIBUTES = 0;
  private static final short OFFSETOF_FILE_INFORMATION_CREATETIME = 4;
  private static final short OFFSETOF_FILE_INFORMATION_LASTACCESSTIME = 12;
  private static final short OFFSETOF_FILE_INFORMATION_LASTWRITETIME = 20;
  private static final short OFFSETOF_FILE_INFORMATION_VOLSERIALNUM = 28;
  private static final short OFFSETOF_FILE_INFORMATION_SIZEHIGH = 32;
  private static final short OFFSETOF_FILE_INFORMATION_SIZELOW = 36;
  private static final short OFFSETOF_FILE_INFORMATION_INDEXHIGH = 44;
  private static final short OFFSETOF_FILE_INFORMATION_INDEXLOW = 48;
  private static final short SIZEOF_FILE_ATTRIBUTE_DATA = 36;
  private static final short OFFSETOF_FILE_ATTRIBUTE_DATA_ATTRIBUTES = 0;
  private static final short OFFSETOF_FILE_ATTRIBUTE_DATA_CREATETIME = 4;
  private static final short OFFSETOF_FILE_ATTRIBUTE_DATA_LASTACCESSTIME = 12;
  private static final short OFFSETOF_FILE_ATTRIBUTE_DATA_LASTWRITETIME = 20;
  private static final short OFFSETOF_FILE_ATTRIBUTE_DATA_SIZEHIGH = 28;
  private static final short OFFSETOF_FILE_ATTRIBUTE_DATA_SIZELOW = 32;
  private static final short SIZEOF_FIND_DATA = 592;
  private static final short OFFSETOF_FIND_DATA_ATTRIBUTES = 0;
  private static final short OFFSETOF_FIND_DATA_CREATETIME = 4;
  private static final short OFFSETOF_FIND_DATA_LASTACCESSTIME = 12;
  private static final short OFFSETOF_FIND_DATA_LASTWRITETIME = 20;
  private static final short OFFSETOF_FIND_DATA_SIZEHIGH = 28;
  private static final short OFFSETOF_FIND_DATA_SIZELOW = 32;
  private static final short OFFSETOF_FIND_DATA_RESERVED0 = 36;
  private static final long WINDOWS_EPOCH_IN_MICROSECONDS = -11644473600000000L;
  private static final boolean ensureAccurateMetadata;
  private final int fileAttrs;
  private final long creationTime;
  private final long lastAccessTime;
  private final long lastWriteTime;
  private final long size;
  private final int reparseTag;
  private final int volSerialNumber;
  private final int fileIndexHigh;
  private final int fileIndexLow;
  
  static FileTime toFileTime(long paramLong)
  {
    paramLong /= 10L;
    paramLong += -11644473600000000L;
    return FileTime.from(paramLong, TimeUnit.MICROSECONDS);
  }
  
  static long toWindowsTime(FileTime paramFileTime)
  {
    long l = paramFileTime.to(TimeUnit.MICROSECONDS);
    l -= -11644473600000000L;
    l *= 10L;
    return l;
  }
  
  private WindowsFileAttributes(int paramInt1, long paramLong1, long paramLong2, long paramLong3, long paramLong4, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    fileAttrs = paramInt1;
    creationTime = paramLong1;
    lastAccessTime = paramLong2;
    lastWriteTime = paramLong3;
    size = paramLong4;
    reparseTag = paramInt2;
    volSerialNumber = paramInt3;
    fileIndexHigh = paramInt4;
    fileIndexLow = paramInt5;
  }
  
  private static WindowsFileAttributes fromFileInformation(long paramLong, int paramInt)
  {
    int i = unsafe.getInt(paramLong + 0L);
    long l1 = unsafe.getLong(paramLong + 4L);
    long l2 = unsafe.getLong(paramLong + 12L);
    long l3 = unsafe.getLong(paramLong + 20L);
    long l4 = (unsafe.getInt(paramLong + 32L) << 32) + (unsafe.getInt(paramLong + 36L) & 0xFFFFFFFF);
    int j = unsafe.getInt(paramLong + 28L);
    int k = unsafe.getInt(paramLong + 44L);
    int m = unsafe.getInt(paramLong + 48L);
    return new WindowsFileAttributes(i, l1, l2, l3, l4, paramInt, j, k, m);
  }
  
  private static WindowsFileAttributes fromFileAttributeData(long paramLong, int paramInt)
  {
    int i = unsafe.getInt(paramLong + 0L);
    long l1 = unsafe.getLong(paramLong + 4L);
    long l2 = unsafe.getLong(paramLong + 12L);
    long l3 = unsafe.getLong(paramLong + 20L);
    long l4 = (unsafe.getInt(paramLong + 28L) << 32) + (unsafe.getInt(paramLong + 32L) & 0xFFFFFFFF);
    return new WindowsFileAttributes(i, l1, l2, l3, l4, paramInt, 0, 0, 0);
  }
  
  static NativeBuffer getBufferForFindData()
  {
    return NativeBuffers.getNativeBuffer(592);
  }
  
  static WindowsFileAttributes fromFindData(long paramLong)
  {
    int i = unsafe.getInt(paramLong + 0L);
    long l1 = unsafe.getLong(paramLong + 4L);
    long l2 = unsafe.getLong(paramLong + 12L);
    long l3 = unsafe.getLong(paramLong + 20L);
    long l4 = (unsafe.getInt(paramLong + 28L) << 32) + (unsafe.getInt(paramLong + 32L) & 0xFFFFFFFF);
    int j = isReparsePoint(i) ? unsafe.getInt(paramLong + 36L) : 0;
    return new WindowsFileAttributes(i, l1, l2, l3, l4, j, 0, 0, 0);
  }
  
  static WindowsFileAttributes readAttributes(long paramLong)
    throws WindowsException
  {
    NativeBuffer localNativeBuffer1 = NativeBuffers.getNativeBuffer(52);
    try
    {
      long l = localNativeBuffer1.address();
      WindowsNativeDispatcher.GetFileInformationByHandle(paramLong, l);
      int i = 0;
      int j = unsafe.getInt(l + 0L);
      if (isReparsePoint(j))
      {
        int k = 16384;
        NativeBuffer localNativeBuffer2 = NativeBuffers.getNativeBuffer(k);
        try
        {
          WindowsNativeDispatcher.DeviceIoControlGetReparsePoint(paramLong, localNativeBuffer2.address(), k);
          i = (int)unsafe.getLong(localNativeBuffer2.address());
        }
        finally {}
      }
      WindowsFileAttributes localWindowsFileAttributes = fromFileInformation(l, i);
      return localWindowsFileAttributes;
    }
    finally
    {
      localNativeBuffer1.release();
    }
  }
  
  static WindowsFileAttributes get(WindowsPath paramWindowsPath, boolean paramBoolean)
    throws WindowsException
  {
    Object localObject2;
    if (!ensureAccurateMetadata)
    {
      Object localObject1 = null;
      NativeBuffer localNativeBuffer = NativeBuffers.getNativeBuffer(36);
      try
      {
        long l2 = localNativeBuffer.address();
        WindowsNativeDispatcher.GetFileAttributesEx(paramWindowsPath.getPathForWin32Calls(), l2);
        int j = unsafe.getInt(l2 + 0L);
        if (!isReparsePoint(j))
        {
          WindowsFileAttributes localWindowsFileAttributes1 = fromFileAttributeData(l2, 0);
          return localWindowsFileAttributes1;
        }
      }
      catch (WindowsException localWindowsException1)
      {
        if (localWindowsException1.lastError() != 32) {
          throw localWindowsException1;
        }
        localObject1 = localWindowsException1;
      }
      finally
      {
        localNativeBuffer.release();
      }
      if (localObject1 != null)
      {
        localObject2 = paramWindowsPath.getPathForWin32Calls();
        int i = ((String)localObject2).charAt(((String)localObject2).length() - 1);
        if ((i == 58) || (i == 92)) {
          throw ((Throwable)localObject1);
        }
        localNativeBuffer = getBufferForFindData();
        try
        {
          long l3 = WindowsNativeDispatcher.FindFirstFile((String)localObject2, localNativeBuffer.address());
          WindowsNativeDispatcher.FindClose(l3);
          WindowsFileAttributes localWindowsFileAttributes2 = fromFindData(localNativeBuffer.address());
          if (localWindowsFileAttributes2.isReparsePoint()) {
            throw ((Throwable)localObject1);
          }
          WindowsFileAttributes localWindowsFileAttributes3 = localWindowsFileAttributes2;
          return localWindowsFileAttributes3;
        }
        catch (WindowsException localWindowsException2)
        {
          throw ((Throwable)localObject1);
        }
        finally
        {
          localNativeBuffer.release();
        }
      }
    }
    long l1 = paramWindowsPath.openForReadAttributeAccess(paramBoolean);
    try
    {
      localObject2 = readAttributes(l1);
      return (WindowsFileAttributes)localObject2;
    }
    finally
    {
      WindowsNativeDispatcher.CloseHandle(l1);
    }
  }
  
  static boolean isSameFile(WindowsFileAttributes paramWindowsFileAttributes1, WindowsFileAttributes paramWindowsFileAttributes2)
  {
    return (volSerialNumber == volSerialNumber) && (fileIndexHigh == fileIndexHigh) && (fileIndexLow == fileIndexLow);
  }
  
  static boolean isReparsePoint(int paramInt)
  {
    return (paramInt & 0x400) != 0;
  }
  
  int attributes()
  {
    return fileAttrs;
  }
  
  int volSerialNumber()
  {
    return volSerialNumber;
  }
  
  int fileIndexHigh()
  {
    return fileIndexHigh;
  }
  
  int fileIndexLow()
  {
    return fileIndexLow;
  }
  
  public long size()
  {
    return size;
  }
  
  public FileTime lastModifiedTime()
  {
    return toFileTime(lastWriteTime);
  }
  
  public FileTime lastAccessTime()
  {
    return toFileTime(lastAccessTime);
  }
  
  public FileTime creationTime()
  {
    return toFileTime(creationTime);
  }
  
  public Object fileKey()
  {
    return null;
  }
  
  boolean isReparsePoint()
  {
    return isReparsePoint(fileAttrs);
  }
  
  boolean isDirectoryLink()
  {
    return (isSymbolicLink()) && ((fileAttrs & 0x10) != 0);
  }
  
  public boolean isSymbolicLink()
  {
    return reparseTag == -1610612724;
  }
  
  public boolean isDirectory()
  {
    if (isSymbolicLink()) {
      return false;
    }
    return (fileAttrs & 0x10) != 0;
  }
  
  public boolean isOther()
  {
    if (isSymbolicLink()) {
      return false;
    }
    return (fileAttrs & 0x440) != 0;
  }
  
  public boolean isRegularFile()
  {
    return (!isSymbolicLink()) && (!isDirectory()) && (!isOther());
  }
  
  public boolean isReadOnly()
  {
    return (fileAttrs & 0x1) != 0;
  }
  
  public boolean isHidden()
  {
    return (fileAttrs & 0x2) != 0;
  }
  
  public boolean isArchive()
  {
    return (fileAttrs & 0x20) != 0;
  }
  
  public boolean isSystem()
  {
    return (fileAttrs & 0x4) != 0;
  }
  
  static
  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.nio.fs.ensureAccurateMetadata", "false"));
    ensureAccurateMetadata = str.length() == 0 ? true : Boolean.valueOf(str).booleanValue();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\fs\WindowsFileAttributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */