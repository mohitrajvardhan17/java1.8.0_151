package com.sun.media.sound;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Soundbank;
import javax.sound.midi.spi.SoundbankReader;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class AudioFileSoundbankReader
  extends SoundbankReader
{
  public AudioFileSoundbankReader() {}
  
  public Soundbank getSoundbank(URL paramURL)
    throws InvalidMidiDataException, IOException
  {
    try
    {
      AudioInputStream localAudioInputStream = AudioSystem.getAudioInputStream(paramURL);
      Soundbank localSoundbank = getSoundbank(localAudioInputStream);
      localAudioInputStream.close();
      return localSoundbank;
    }
    catch (UnsupportedAudioFileException localUnsupportedAudioFileException)
    {
      return null;
    }
    catch (IOException localIOException) {}
    return null;
  }
  
  public Soundbank getSoundbank(InputStream paramInputStream)
    throws InvalidMidiDataException, IOException
  {
    paramInputStream.mark(512);
    try
    {
      AudioInputStream localAudioInputStream = AudioSystem.getAudioInputStream(paramInputStream);
      Soundbank localSoundbank = getSoundbank(localAudioInputStream);
      if (localSoundbank != null) {
        return localSoundbank;
      }
    }
    catch (UnsupportedAudioFileException localUnsupportedAudioFileException) {}catch (IOException localIOException) {}
    paramInputStream.reset();
    return null;
  }
  
  public Soundbank getSoundbank(AudioInputStream paramAudioInputStream)
    throws InvalidMidiDataException, IOException
  {
    try
    {
      byte[] arrayOfByte;
      if (paramAudioInputStream.getFrameLength() == -1L)
      {
        localObject1 = new ByteArrayOutputStream();
        localObject2 = new byte[1024 - 1024 % paramAudioInputStream.getFormat().getFrameSize()];
        int i;
        while ((i = paramAudioInputStream.read((byte[])localObject2)) != -1) {
          ((ByteArrayOutputStream)localObject1).write((byte[])localObject2, 0, i);
        }
        paramAudioInputStream.close();
        arrayOfByte = ((ByteArrayOutputStream)localObject1).toByteArray();
      }
      else
      {
        arrayOfByte = new byte[(int)(paramAudioInputStream.getFrameLength() * paramAudioInputStream.getFormat().getFrameSize())];
        new DataInputStream(paramAudioInputStream).readFully(arrayOfByte);
      }
      Object localObject1 = new ModelByteBufferWavetable(new ModelByteBuffer(arrayOfByte), paramAudioInputStream.getFormat(), -4800.0F);
      Object localObject2 = new ModelPerformer();
      ((ModelPerformer)localObject2).getOscillators().add(localObject1);
      SimpleSoundbank localSimpleSoundbank = new SimpleSoundbank();
      SimpleInstrument localSimpleInstrument = new SimpleInstrument();
      localSimpleInstrument.add((ModelPerformer)localObject2);
      localSimpleSoundbank.addInstrument(localSimpleInstrument);
      return localSimpleSoundbank;
    }
    catch (Exception localException) {}
    return null;
  }
  
  public Soundbank getSoundbank(File paramFile)
    throws InvalidMidiDataException, IOException
  {
    try
    {
      AudioInputStream localAudioInputStream = AudioSystem.getAudioInputStream(paramFile);
      localAudioInputStream.close();
      ModelByteBufferWavetable localModelByteBufferWavetable = new ModelByteBufferWavetable(new ModelByteBuffer(paramFile, 0L, paramFile.length()), -4800.0F);
      ModelPerformer localModelPerformer = new ModelPerformer();
      localModelPerformer.getOscillators().add(localModelByteBufferWavetable);
      SimpleSoundbank localSimpleSoundbank = new SimpleSoundbank();
      SimpleInstrument localSimpleInstrument = new SimpleInstrument();
      localSimpleInstrument.add(localModelPerformer);
      localSimpleSoundbank.addInstrument(localSimpleInstrument);
      return localSimpleSoundbank;
    }
    catch (UnsupportedAudioFileException localUnsupportedAudioFileException)
    {
      return null;
    }
    catch (IOException localIOException) {}
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\AudioFileSoundbankReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */