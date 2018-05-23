package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;

public class FileKey
{
  private long dwVolumeSerialNumber;
  private long nFileIndexHigh;
  private long nFileIndexLow;
  
  private FileKey() {}
  
  public static FileKey create(FileDescriptor paramFileDescriptor)
  {
    FileKey localFileKey = new FileKey();
    try
    {
      localFileKey.init(paramFileDescriptor);
    }
    catch (IOException localIOException)
    {
      throw new Error(localIOException);
    }
    return localFileKey;
  }
  
  public int hashCode()
  {
    return (int)(dwVolumeSerialNumber ^ dwVolumeSerialNumber >>> 32) + (int)(nFileIndexHigh ^ nFileIndexHigh >>> 32) + (int)(nFileIndexLow ^ nFileIndexHigh >>> 32);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof FileKey)) {
      return false;
    }
    FileKey localFileKey = (FileKey)paramObject;
    return (dwVolumeSerialNumber == dwVolumeSerialNumber) && (nFileIndexHigh == nFileIndexHigh) && (nFileIndexLow == nFileIndexLow);
  }
  
  private native void init(FileDescriptor paramFileDescriptor)
    throws IOException;
  
  private static native void initIDs();
  
  static
  {
    IOUtil.load();
    initIDs();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\FileKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */