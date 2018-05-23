package javax.sound.sampled.spi;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioInputStream;

public abstract class AudioFileWriter
{
  public AudioFileWriter() {}
  
  public abstract AudioFileFormat.Type[] getAudioFileTypes();
  
  public boolean isFileTypeSupported(AudioFileFormat.Type paramType)
  {
    AudioFileFormat.Type[] arrayOfType = getAudioFileTypes();
    for (int i = 0; i < arrayOfType.length; i++) {
      if (paramType.equals(arrayOfType[i])) {
        return true;
      }
    }
    return false;
  }
  
  public abstract AudioFileFormat.Type[] getAudioFileTypes(AudioInputStream paramAudioInputStream);
  
  public boolean isFileTypeSupported(AudioFileFormat.Type paramType, AudioInputStream paramAudioInputStream)
  {
    AudioFileFormat.Type[] arrayOfType = getAudioFileTypes(paramAudioInputStream);
    for (int i = 0; i < arrayOfType.length; i++) {
      if (paramType.equals(arrayOfType[i])) {
        return true;
      }
    }
    return false;
  }
  
  public abstract int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, OutputStream paramOutputStream)
    throws IOException;
  
  public abstract int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, File paramFile)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\sampled\spi\AudioFileWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */