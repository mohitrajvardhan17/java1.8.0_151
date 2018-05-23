package sun.awt.windows;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

final class WDropTargetContextPeerFileStream
  extends FileInputStream
{
  private long stgmedium;
  
  WDropTargetContextPeerFileStream(String paramString, long paramLong)
    throws FileNotFoundException
  {
    super(paramString);
    stgmedium = paramLong;
  }
  
  public void close()
    throws IOException
  {
    if (stgmedium != 0L)
    {
      super.close();
      freeStgMedium(stgmedium);
      stgmedium = 0L;
    }
  }
  
  private native void freeStgMedium(long paramLong);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WDropTargetContextPeerFileStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */