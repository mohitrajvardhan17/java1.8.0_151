package sun.audio;

import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Enumeration;

public final class AudioStreamSequence
  extends SequenceInputStream
{
  Enumeration e;
  InputStream in;
  
  public AudioStreamSequence(Enumeration paramEnumeration)
  {
    super(paramEnumeration);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\audio\AudioStreamSequence.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */