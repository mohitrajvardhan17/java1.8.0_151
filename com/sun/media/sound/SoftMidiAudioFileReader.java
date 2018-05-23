package com.sun.media.sound;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.spi.AudioFileReader;

public final class SoftMidiAudioFileReader
  extends AudioFileReader
{
  public static final AudioFileFormat.Type MIDI = new AudioFileFormat.Type("MIDI", "mid");
  private static AudioFormat format = new AudioFormat(44100.0F, 16, 2, true, false);
  
  public SoftMidiAudioFileReader() {}
  
  public AudioFileFormat getAudioFileFormat(Sequence paramSequence)
    throws UnsupportedAudioFileException, IOException
  {
    long l1 = paramSequence.getMicrosecondLength() / 1000000L;
    long l2 = (format.getFrameRate() * (float)(l1 + 4L));
    return new AudioFileFormat(MIDI, format, (int)l2);
  }
  
  public AudioInputStream getAudioInputStream(Sequence paramSequence)
    throws UnsupportedAudioFileException, IOException
  {
    SoftSynthesizer localSoftSynthesizer = new SoftSynthesizer();
    Receiver localReceiver;
    try
    {
      localAudioInputStream = localSoftSynthesizer.openStream(format, null);
      localReceiver = localSoftSynthesizer.getReceiver();
    }
    catch (MidiUnavailableException localMidiUnavailableException)
    {
      throw new IOException(localMidiUnavailableException.toString());
    }
    float f = paramSequence.getDivisionType();
    Track[] arrayOfTrack = paramSequence.getTracks();
    int[] arrayOfInt = new int[arrayOfTrack.length];
    int i = 500000;
    int j = paramSequence.getResolution();
    long l1 = 0L;
    long l2 = 0L;
    for (;;)
    {
      Object localObject1 = null;
      int k = -1;
      Object localObject3;
      for (int m = 0; m < arrayOfTrack.length; m++)
      {
        int n = arrayOfInt[m];
        localObject2 = arrayOfTrack[m];
        if (n < ((Track)localObject2).size())
        {
          localObject3 = ((Track)localObject2).get(n);
          if ((localObject1 == null) || (((MidiEvent)localObject3).getTick() < ((MidiEvent)localObject1).getTick()))
          {
            localObject1 = localObject3;
            k = m;
          }
        }
      }
      if (k == -1) {
        break;
      }
      arrayOfInt[k] += 1;
      l4 = ((MidiEvent)localObject1).getTick();
      if (f == 0.0F) {
        l2 += (l4 - l1) * i / j;
      } else {
        l2 = (l4 * 1000000.0D * f / j);
      }
      l1 = l4;
      Object localObject2 = ((MidiEvent)localObject1).getMessage();
      if ((localObject2 instanceof MetaMessage))
      {
        if ((f == 0.0F) && (((MetaMessage)localObject2).getType() == 81))
        {
          localObject3 = ((MetaMessage)localObject2).getData();
          if (localObject3.length < 3) {
            throw new UnsupportedAudioFileException();
          }
          i = (localObject3[0] & 0xFF) << 16 | (localObject3[1] & 0xFF) << 8 | localObject3[2] & 0xFF;
        }
      }
      else {
        localReceiver.send((MidiMessage)localObject2, l2);
      }
    }
    long l3 = l2 / 1000000L;
    long l4 = (localAudioInputStream.getFormat().getFrameRate() * (float)(l3 + 4L));
    AudioInputStream localAudioInputStream = new AudioInputStream(localAudioInputStream, localAudioInputStream.getFormat(), l4);
    return localAudioInputStream;
  }
  
  public AudioInputStream getAudioInputStream(InputStream paramInputStream)
    throws UnsupportedAudioFileException, IOException
  {
    paramInputStream.mark(200);
    Sequence localSequence;
    try
    {
      localSequence = MidiSystem.getSequence(paramInputStream);
    }
    catch (InvalidMidiDataException localInvalidMidiDataException)
    {
      paramInputStream.reset();
      throw new UnsupportedAudioFileException();
    }
    catch (IOException localIOException)
    {
      paramInputStream.reset();
      throw new UnsupportedAudioFileException();
    }
    return getAudioInputStream(localSequence);
  }
  
  public AudioFileFormat getAudioFileFormat(URL paramURL)
    throws UnsupportedAudioFileException, IOException
  {
    Sequence localSequence;
    try
    {
      localSequence = MidiSystem.getSequence(paramURL);
    }
    catch (InvalidMidiDataException localInvalidMidiDataException)
    {
      throw new UnsupportedAudioFileException();
    }
    catch (IOException localIOException)
    {
      throw new UnsupportedAudioFileException();
    }
    return getAudioFileFormat(localSequence);
  }
  
  public AudioFileFormat getAudioFileFormat(File paramFile)
    throws UnsupportedAudioFileException, IOException
  {
    Sequence localSequence;
    try
    {
      localSequence = MidiSystem.getSequence(paramFile);
    }
    catch (InvalidMidiDataException localInvalidMidiDataException)
    {
      throw new UnsupportedAudioFileException();
    }
    catch (IOException localIOException)
    {
      throw new UnsupportedAudioFileException();
    }
    return getAudioFileFormat(localSequence);
  }
  
  public AudioInputStream getAudioInputStream(URL paramURL)
    throws UnsupportedAudioFileException, IOException
  {
    Sequence localSequence;
    try
    {
      localSequence = MidiSystem.getSequence(paramURL);
    }
    catch (InvalidMidiDataException localInvalidMidiDataException)
    {
      throw new UnsupportedAudioFileException();
    }
    catch (IOException localIOException)
    {
      throw new UnsupportedAudioFileException();
    }
    return getAudioInputStream(localSequence);
  }
  
  public AudioInputStream getAudioInputStream(File paramFile)
    throws UnsupportedAudioFileException, IOException
  {
    if (!paramFile.getName().toLowerCase().endsWith(".mid")) {
      throw new UnsupportedAudioFileException();
    }
    Sequence localSequence;
    try
    {
      localSequence = MidiSystem.getSequence(paramFile);
    }
    catch (InvalidMidiDataException localInvalidMidiDataException)
    {
      throw new UnsupportedAudioFileException();
    }
    catch (IOException localIOException)
    {
      throw new UnsupportedAudioFileException();
    }
    return getAudioInputStream(localSequence);
  }
  
  public AudioFileFormat getAudioFileFormat(InputStream paramInputStream)
    throws UnsupportedAudioFileException, IOException
  {
    paramInputStream.mark(200);
    Sequence localSequence;
    try
    {
      localSequence = MidiSystem.getSequence(paramInputStream);
    }
    catch (InvalidMidiDataException localInvalidMidiDataException)
    {
      paramInputStream.reset();
      throw new UnsupportedAudioFileException();
    }
    catch (IOException localIOException)
    {
      paramInputStream.reset();
      throw new UnsupportedAudioFileException();
    }
    return getAudioFileFormat(localSequence);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SoftMidiAudioFileReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */