package com.sun.media.sound;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Soundbank;
import javax.sound.midi.spi.SoundbankReader;

public final class DLSSoundbankReader
  extends SoundbankReader
{
  public DLSSoundbankReader() {}
  
  public Soundbank getSoundbank(URL paramURL)
    throws InvalidMidiDataException, IOException
  {
    try
    {
      return new DLSSoundbank(paramURL);
    }
    catch (RIFFInvalidFormatException localRIFFInvalidFormatException)
    {
      return null;
    }
    catch (IOException localIOException) {}
    return null;
  }
  
  public Soundbank getSoundbank(InputStream paramInputStream)
    throws InvalidMidiDataException, IOException
  {
    try
    {
      paramInputStream.mark(512);
      return new DLSSoundbank(paramInputStream);
    }
    catch (RIFFInvalidFormatException localRIFFInvalidFormatException)
    {
      paramInputStream.reset();
    }
    return null;
  }
  
  public Soundbank getSoundbank(File paramFile)
    throws InvalidMidiDataException, IOException
  {
    try
    {
      return new DLSSoundbank(paramFile);
    }
    catch (RIFFInvalidFormatException localRIFFInvalidFormatException) {}
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\DLSSoundbankReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */