package sun.audio;

import java.io.IOException;

final class InvalidAudioFormatException
  extends IOException
{
  InvalidAudioFormatException() {}
  
  InvalidAudioFormatException(String paramString)
  {
    super(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\audio\InvalidAudioFormatException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */