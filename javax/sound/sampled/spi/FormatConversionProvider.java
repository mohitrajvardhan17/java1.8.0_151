package javax.sound.sampled.spi;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;

public abstract class FormatConversionProvider
{
  public FormatConversionProvider() {}
  
  public abstract AudioFormat.Encoding[] getSourceEncodings();
  
  public abstract AudioFormat.Encoding[] getTargetEncodings();
  
  public boolean isSourceEncodingSupported(AudioFormat.Encoding paramEncoding)
  {
    AudioFormat.Encoding[] arrayOfEncoding = getSourceEncodings();
    for (int i = 0; i < arrayOfEncoding.length; i++) {
      if (paramEncoding.equals(arrayOfEncoding[i])) {
        return true;
      }
    }
    return false;
  }
  
  public boolean isTargetEncodingSupported(AudioFormat.Encoding paramEncoding)
  {
    AudioFormat.Encoding[] arrayOfEncoding = getTargetEncodings();
    for (int i = 0; i < arrayOfEncoding.length; i++) {
      if (paramEncoding.equals(arrayOfEncoding[i])) {
        return true;
      }
    }
    return false;
  }
  
  public abstract AudioFormat.Encoding[] getTargetEncodings(AudioFormat paramAudioFormat);
  
  public boolean isConversionSupported(AudioFormat.Encoding paramEncoding, AudioFormat paramAudioFormat)
  {
    AudioFormat.Encoding[] arrayOfEncoding = getTargetEncodings(paramAudioFormat);
    for (int i = 0; i < arrayOfEncoding.length; i++) {
      if (paramEncoding.equals(arrayOfEncoding[i])) {
        return true;
      }
    }
    return false;
  }
  
  public abstract AudioFormat[] getTargetFormats(AudioFormat.Encoding paramEncoding, AudioFormat paramAudioFormat);
  
  public boolean isConversionSupported(AudioFormat paramAudioFormat1, AudioFormat paramAudioFormat2)
  {
    AudioFormat[] arrayOfAudioFormat = getTargetFormats(paramAudioFormat1.getEncoding(), paramAudioFormat2);
    for (int i = 0; i < arrayOfAudioFormat.length; i++) {
      if (paramAudioFormat1.matches(arrayOfAudioFormat[i])) {
        return true;
      }
    }
    return false;
  }
  
  public abstract AudioInputStream getAudioInputStream(AudioFormat.Encoding paramEncoding, AudioInputStream paramAudioInputStream);
  
  public abstract AudioInputStream getAudioInputStream(AudioFormat paramAudioFormat, AudioInputStream paramAudioInputStream);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\sampled\spi\FormatConversionProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */