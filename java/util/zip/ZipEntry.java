package java.util.zip;

import java.nio.file.attribute.FileTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ZipEntry
  implements ZipConstants, Cloneable
{
  String name;
  long xdostime = -1L;
  FileTime mtime;
  FileTime atime;
  FileTime ctime;
  long crc = -1L;
  long size = -1L;
  long csize = -1L;
  int method = -1;
  int flag = 0;
  byte[] extra;
  String comment;
  public static final int STORED = 0;
  public static final int DEFLATED = 8;
  static final long DOSTIME_BEFORE_1980 = 2162688L;
  private static final long UPPER_DOSTIME_BOUND = 4036608000000L;
  
  public ZipEntry(String paramString)
  {
    Objects.requireNonNull(paramString, "name");
    if (paramString.length() > 65535) {
      throw new IllegalArgumentException("entry name too long");
    }
    name = paramString;
  }
  
  public ZipEntry(ZipEntry paramZipEntry)
  {
    Objects.requireNonNull(paramZipEntry, "entry");
    name = name;
    xdostime = xdostime;
    mtime = mtime;
    atime = atime;
    ctime = ctime;
    crc = crc;
    size = size;
    csize = csize;
    method = method;
    flag = flag;
    extra = extra;
    comment = comment;
  }
  
  ZipEntry() {}
  
  public String getName()
  {
    return name;
  }
  
  public void setTime(long paramLong)
  {
    xdostime = ZipUtils.javaToExtendedDosTime(paramLong);
    if ((xdostime != 2162688L) && (paramLong <= 4036608000000L)) {
      mtime = null;
    } else {
      mtime = FileTime.from(paramLong, TimeUnit.MILLISECONDS);
    }
  }
  
  public long getTime()
  {
    if (mtime != null) {
      return mtime.toMillis();
    }
    return xdostime != -1L ? ZipUtils.extendedDosToJavaTime(xdostime) : -1L;
  }
  
  public ZipEntry setLastModifiedTime(FileTime paramFileTime)
  {
    mtime = ((FileTime)Objects.requireNonNull(paramFileTime, "lastModifiedTime"));
    xdostime = ZipUtils.javaToExtendedDosTime(paramFileTime.to(TimeUnit.MILLISECONDS));
    return this;
  }
  
  public FileTime getLastModifiedTime()
  {
    if (mtime != null) {
      return mtime;
    }
    if (xdostime == -1L) {
      return null;
    }
    return FileTime.from(getTime(), TimeUnit.MILLISECONDS);
  }
  
  public ZipEntry setLastAccessTime(FileTime paramFileTime)
  {
    atime = ((FileTime)Objects.requireNonNull(paramFileTime, "lastAccessTime"));
    return this;
  }
  
  public FileTime getLastAccessTime()
  {
    return atime;
  }
  
  public ZipEntry setCreationTime(FileTime paramFileTime)
  {
    ctime = ((FileTime)Objects.requireNonNull(paramFileTime, "creationTime"));
    return this;
  }
  
  public FileTime getCreationTime()
  {
    return ctime;
  }
  
  public void setSize(long paramLong)
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("invalid entry size");
    }
    size = paramLong;
  }
  
  public long getSize()
  {
    return size;
  }
  
  public long getCompressedSize()
  {
    return csize;
  }
  
  public void setCompressedSize(long paramLong)
  {
    csize = paramLong;
  }
  
  public void setCrc(long paramLong)
  {
    if ((paramLong < 0L) || (paramLong > 4294967295L)) {
      throw new IllegalArgumentException("invalid entry crc-32");
    }
    crc = paramLong;
  }
  
  public long getCrc()
  {
    return crc;
  }
  
  public void setMethod(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 8)) {
      throw new IllegalArgumentException("invalid compression method");
    }
    method = paramInt;
  }
  
  public int getMethod()
  {
    return method;
  }
  
  public void setExtra(byte[] paramArrayOfByte)
  {
    setExtra0(paramArrayOfByte, false);
  }
  
  void setExtra0(byte[] paramArrayOfByte, boolean paramBoolean)
  {
    if (paramArrayOfByte != null)
    {
      if (paramArrayOfByte.length > 65535) {
        throw new IllegalArgumentException("invalid extra field length");
      }
      int i = 0;
      int j = paramArrayOfByte.length;
      while (i + 4 < j)
      {
        int k = ZipUtils.get16(paramArrayOfByte, i);
        int m = ZipUtils.get16(paramArrayOfByte, i + 2);
        i += 4;
        if (i + m > j) {
          break;
        }
        switch (k)
        {
        case 1: 
          if ((paramBoolean) && (m >= 16))
          {
            size = ZipUtils.get64(paramArrayOfByte, i);
            csize = ZipUtils.get64(paramArrayOfByte, i + 8);
          }
          break;
        case 10: 
          if (m >= 32)
          {
            int n = i + 4;
            if ((ZipUtils.get16(paramArrayOfByte, n) == 1) && (ZipUtils.get16(paramArrayOfByte, n + 2) == 24))
            {
              mtime = ZipUtils.winTimeToFileTime(ZipUtils.get64(paramArrayOfByte, n + 4));
              atime = ZipUtils.winTimeToFileTime(ZipUtils.get64(paramArrayOfByte, n + 12));
              ctime = ZipUtils.winTimeToFileTime(ZipUtils.get64(paramArrayOfByte, n + 20));
            }
          }
          break;
        case 21589: 
          int i1 = Byte.toUnsignedInt(paramArrayOfByte[i]);
          int i2 = 1;
          if (((i1 & 0x1) != 0) && (i2 + 4 <= m))
          {
            mtime = ZipUtils.unixTimeToFileTime(ZipUtils.get32(paramArrayOfByte, i + i2));
            i2 += 4;
          }
          if (((i1 & 0x2) != 0) && (i2 + 4 <= m))
          {
            atime = ZipUtils.unixTimeToFileTime(ZipUtils.get32(paramArrayOfByte, i + i2));
            i2 += 4;
          }
          if (((i1 & 0x4) != 0) && (i2 + 4 <= m))
          {
            ctime = ZipUtils.unixTimeToFileTime(ZipUtils.get32(paramArrayOfByte, i + i2));
            i2 += 4;
          }
          break;
        }
        i += m;
      }
    }
    extra = paramArrayOfByte;
  }
  
  public byte[] getExtra()
  {
    return extra;
  }
  
  public void setComment(String paramString)
  {
    comment = paramString;
  }
  
  public String getComment()
  {
    return comment;
  }
  
  public boolean isDirectory()
  {
    return name.endsWith("/");
  }
  
  public String toString()
  {
    return getName();
  }
  
  public int hashCode()
  {
    return name.hashCode();
  }
  
  public Object clone()
  {
    try
    {
      ZipEntry localZipEntry = (ZipEntry)super.clone();
      extra = (extra == null ? null : (byte[])extra.clone());
      return localZipEntry;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\zip\ZipEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */