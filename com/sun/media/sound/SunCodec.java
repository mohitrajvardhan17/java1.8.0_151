package com.sun.media.sound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.spi.FormatConversionProvider;

abstract class SunCodec
  extends FormatConversionProvider
{
  private final AudioFormat.Encoding[] inputEncodings;
  private final AudioFormat.Encoding[] outputEncodings;
  
  SunCodec(AudioFormat.Encoding[] paramArrayOfEncoding1, AudioFormat.Encoding[] paramArrayOfEncoding2)
  {
    inputEncodings = paramArrayOfEncoding1;
    outputEncodings = paramArrayOfEncoding2;
  }
  
  public final AudioFormat.Encoding[] getSourceEncodings()
  {
    AudioFormat.Encoding[] arrayOfEncoding = new AudioFormat.Encoding[inputEncodings.length];
    System.arraycopy(inputEncodings, 0, arrayOfEncoding, 0, inputEncodings.length);
    return arrayOfEncoding;
  }
  
  public final AudioFormat.Encoding[] getTargetEncodings()
  {
    AudioFormat.Encoding[] arrayOfEncoding = new AudioFormat.Encoding[outputEncodings.length];
    System.arraycopy(outputEncodings, 0, arrayOfEncoding, 0, outputEncodings.length);
    return arrayOfEncoding;
  }
  
  public abstract AudioFormat.Encoding[] getTargetEncodings(AudioFormat paramAudioFormat);
  
  public abstract AudioFormat[] getTargetFormats(AudioFormat.Encoding paramEncoding, AudioFormat paramAudioFormat);
  
  public abstract AudioInputStream getAudioInputStream(AudioFormat.Encoding paramEncoding, AudioInputStream paramAudioInputStream);
  
  public abstract AudioInputStream getAudioInputStream(AudioFormat paramAudioFormat, AudioInputStream paramAudioInputStream);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SunCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */