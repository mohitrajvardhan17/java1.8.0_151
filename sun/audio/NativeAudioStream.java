package sun.audio;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class NativeAudioStream
  extends FilterInputStream
{
  public NativeAudioStream(InputStream paramInputStream)
    throws IOException
  {
    super(paramInputStream);
  }
  
  public int getLength()
  {
    return 0;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\audio\NativeAudioStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */