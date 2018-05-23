package sun.audio;

import java.io.ByteArrayInputStream;

public class AudioDataStream
  extends ByteArrayInputStream
{
  private final AudioData ad;
  
  public AudioDataStream(AudioData paramAudioData)
  {
    super(buffer);
    ad = paramAudioData;
  }
  
  final AudioData getAudioData()
  {
    return ad;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\audio\AudioDataStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */