package sun.audio;

import com.sun.media.sound.DataPusher;
import com.sun.media.sound.Toolkit;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Vector;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiFileFormat;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class AudioDevice
{
  private boolean DEBUG = false;
  private Hashtable clipStreams = new Hashtable();
  private Vector infos = new Vector();
  private boolean playing = false;
  private Mixer mixer = null;
  public static final AudioDevice device = new AudioDevice();
  
  private AudioDevice() {}
  
  private synchronized void startSampled(AudioInputStream paramAudioInputStream, InputStream paramInputStream)
    throws UnsupportedAudioFileException, LineUnavailableException
  {
    Info localInfo = null;
    DataPusher localDataPusher = null;
    DataLine.Info localInfo1 = null;
    SourceDataLine localSourceDataLine = null;
    paramAudioInputStream = Toolkit.getPCMConvertedAudioInputStream(paramAudioInputStream);
    if (paramAudioInputStream == null) {
      return;
    }
    localInfo1 = new DataLine.Info(SourceDataLine.class, paramAudioInputStream.getFormat());
    if (!AudioSystem.isLineSupported(localInfo1)) {
      return;
    }
    localSourceDataLine = (SourceDataLine)AudioSystem.getLine(localInfo1);
    localDataPusher = new DataPusher(localSourceDataLine, paramAudioInputStream);
    localInfo = new Info(null, paramInputStream, localDataPusher);
    infos.addElement(localInfo);
    localDataPusher.start();
  }
  
  private synchronized void startMidi(InputStream paramInputStream1, InputStream paramInputStream2)
    throws InvalidMidiDataException, MidiUnavailableException
  {
    Sequencer localSequencer = null;
    Info localInfo = null;
    localSequencer = MidiSystem.getSequencer();
    localSequencer.open();
    try
    {
      localSequencer.setSequence(paramInputStream1);
    }
    catch (IOException localIOException)
    {
      throw new InvalidMidiDataException(localIOException.getMessage());
    }
    localInfo = new Info(localSequencer, paramInputStream2, null);
    infos.addElement(localInfo);
    localSequencer.addMetaEventListener(localInfo);
    localSequencer.start();
  }
  
  public synchronized void openChannel(InputStream paramInputStream)
  {
    if (DEBUG)
    {
      System.out.println("AudioDevice: openChannel");
      System.out.println("input stream =" + paramInputStream);
    }
    Info localInfo = null;
    for (int i = 0; i < infos.size(); i++)
    {
      localInfo = (Info)infos.elementAt(i);
      if (in == paramInputStream) {
        return;
      }
    }
    AudioInputStream localAudioInputStream1 = null;
    if ((paramInputStream instanceof AudioStream))
    {
      if (midiformat != null) {
        try
        {
          startMidi(stream, paramInputStream);
        }
        catch (Exception localException1)
        {
          return;
        }
      } else if (ais != null) {
        try
        {
          startSampled(ais, paramInputStream);
        }
        catch (Exception localException2)
        {
          return;
        }
      }
    }
    else if ((paramInputStream instanceof AudioDataStream))
    {
      if ((paramInputStream instanceof ContinuousAudioDataStream)) {
        try
        {
          AudioInputStream localAudioInputStream2 = new AudioInputStream(paramInputStream, getAudioDataformat, -1L);
          startSampled(localAudioInputStream2, paramInputStream);
        }
        catch (Exception localException3)
        {
          return;
        }
      } else {
        try
        {
          AudioInputStream localAudioInputStream3 = new AudioInputStream(paramInputStream, getAudioDataformat, getAudioDatabuffer.length);
          startSampled(localAudioInputStream3, paramInputStream);
        }
        catch (Exception localException4)
        {
          return;
        }
      }
    }
    else
    {
      BufferedInputStream localBufferedInputStream = new BufferedInputStream(paramInputStream, 1024);
      try
      {
        try
        {
          localAudioInputStream1 = AudioSystem.getAudioInputStream(localBufferedInputStream);
        }
        catch (IOException localIOException1)
        {
          return;
        }
        startSampled(localAudioInputStream1, paramInputStream);
      }
      catch (UnsupportedAudioFileException localUnsupportedAudioFileException1)
      {
        try
        {
          try
          {
            MidiFileFormat localMidiFileFormat = MidiSystem.getMidiFileFormat(localBufferedInputStream);
          }
          catch (IOException localIOException2)
          {
            return;
          }
          startMidi(localBufferedInputStream, paramInputStream);
        }
        catch (InvalidMidiDataException localInvalidMidiDataException)
        {
          AudioFormat localAudioFormat = new AudioFormat(AudioFormat.Encoding.ULAW, 8000.0F, 8, 1, 1, 8000.0F, true);
          try
          {
            AudioInputStream localAudioInputStream4 = new AudioInputStream(localBufferedInputStream, localAudioFormat, -1L);
            startSampled(localAudioInputStream4, paramInputStream);
          }
          catch (UnsupportedAudioFileException localUnsupportedAudioFileException2)
          {
            return;
          }
          catch (LineUnavailableException localLineUnavailableException2)
          {
            return;
          }
        }
        catch (MidiUnavailableException localMidiUnavailableException)
        {
          return;
        }
      }
      catch (LineUnavailableException localLineUnavailableException1)
      {
        return;
      }
    }
    notify();
  }
  
  public synchronized void closeChannel(InputStream paramInputStream)
  {
    if (DEBUG) {
      System.out.println("AudioDevice.closeChannel");
    }
    if (paramInputStream == null) {
      return;
    }
    for (int i = 0; i < infos.size(); i++)
    {
      Info localInfo = (Info)infos.elementAt(i);
      if (in == paramInputStream) {
        if (sequencer != null)
        {
          sequencer.stop();
          infos.removeElement(localInfo);
        }
        else if (datapusher != null)
        {
          datapusher.stop();
          infos.removeElement(localInfo);
        }
      }
    }
    notify();
  }
  
  public synchronized void open() {}
  
  public synchronized void close() {}
  
  public void play()
  {
    if (DEBUG) {
      System.out.println("exiting play()");
    }
  }
  
  public synchronized void closeStreams()
  {
    for (int i = 0; i < infos.size(); i++)
    {
      Info localInfo = (Info)infos.elementAt(i);
      if (sequencer != null)
      {
        sequencer.stop();
        sequencer.close();
        infos.removeElement(localInfo);
      }
      else if (datapusher != null)
      {
        datapusher.stop();
        infos.removeElement(localInfo);
      }
    }
    if (DEBUG) {
      System.err.println("Audio Device: Streams all closed.");
    }
    clipStreams = new Hashtable();
    infos = new Vector();
  }
  
  public int openChannels()
  {
    return infos.size();
  }
  
  void setVerbose(boolean paramBoolean)
  {
    DEBUG = paramBoolean;
  }
  
  final class Info
    implements MetaEventListener
  {
    final Sequencer sequencer;
    final InputStream in;
    final DataPusher datapusher;
    
    Info(Sequencer paramSequencer, InputStream paramInputStream, DataPusher paramDataPusher)
    {
      sequencer = paramSequencer;
      in = paramInputStream;
      datapusher = paramDataPusher;
    }
    
    public void meta(MetaMessage paramMetaMessage)
    {
      if ((paramMetaMessage.getType() == 47) && (sequencer != null)) {
        sequencer.close();
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\audio\AudioDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */