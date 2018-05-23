package sun.audio;

import java.io.IOException;
import java.io.InputStream;

public final class AudioTranslatorStream
  extends NativeAudioStream
{
  private final int length = 0;
  
  public AudioTranslatorStream(InputStream paramInputStream)
    throws IOException
  {
    super(paramInputStream);
    throw new InvalidAudioFormatException();
  }
  
  public int getLength()
  {
    return 0;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\audio\AudioTranslatorStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */