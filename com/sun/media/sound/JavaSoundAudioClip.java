package com.sun.media.sound;

import java.applet.AudioClip;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiFileFormat;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class JavaSoundAudioClip
  implements AudioClip, MetaEventListener, LineListener
{
  private static final boolean DEBUG = false;
  private static final int BUFFER_SIZE = 16384;
  private long lastPlayCall = 0L;
  private static final int MINIMUM_PLAY_DELAY = 30;
  private byte[] loadedAudio = null;
  private int loadedAudioByteLength = 0;
  private AudioFormat loadedAudioFormat = null;
  private AutoClosingClip clip = null;
  private boolean clipLooping = false;
  private DataPusher datapusher = null;
  private Sequencer sequencer = null;
  private Sequence sequence = null;
  private boolean sequencerloop = false;
  private static final long CLIP_THRESHOLD = 1048576L;
  private static final int STREAM_BUFFER_SIZE = 1024;
  
  public JavaSoundAudioClip(InputStream paramInputStream)
    throws IOException
  {
    BufferedInputStream localBufferedInputStream = new BufferedInputStream(paramInputStream, 1024);
    localBufferedInputStream.mark(1024);
    boolean bool = false;
    try
    {
      AudioInputStream localAudioInputStream = AudioSystem.getAudioInputStream(localBufferedInputStream);
      bool = loadAudioData(localAudioInputStream);
      if (bool)
      {
        bool = false;
        if (loadedAudioByteLength < 1048576L) {
          bool = createClip();
        }
        if (!bool) {
          bool = createSourceDataLine();
        }
      }
    }
    catch (UnsupportedAudioFileException localUnsupportedAudioFileException)
    {
      try
      {
        MidiFileFormat localMidiFileFormat = MidiSystem.getMidiFileFormat(localBufferedInputStream);
        bool = createSequencer(localBufferedInputStream);
      }
      catch (InvalidMidiDataException localInvalidMidiDataException)
      {
        bool = false;
      }
    }
    if (!bool) {
      throw new IOException("Unable to create AudioClip from input stream");
    }
  }
  
  public synchronized void play()
  {
    startImpl(false);
  }
  
  public synchronized void loop()
  {
    startImpl(true);
  }
  
  private synchronized void startImpl(boolean paramBoolean)
  {
    long l1 = System.currentTimeMillis();
    long l2 = l1 - lastPlayCall;
    if (l2 < 30L) {
      return;
    }
    lastPlayCall = l1;
    try
    {
      if (clip != null)
      {
        if (!clip.isOpen())
        {
          clip.open(loadedAudioFormat, loadedAudio, 0, loadedAudioByteLength);
        }
        else
        {
          clip.flush();
          if (paramBoolean != clipLooping) {
            clip.stop();
          }
        }
        clip.setFramePosition(0);
        if (paramBoolean) {
          clip.loop(-1);
        } else {
          clip.start();
        }
        clipLooping = paramBoolean;
      }
      else if (datapusher != null)
      {
        datapusher.start(paramBoolean);
      }
      else if (sequencer != null)
      {
        sequencerloop = paramBoolean;
        if (sequencer.isRunning()) {
          sequencer.setMicrosecondPosition(0L);
        }
        if (!sequencer.isOpen()) {
          try
          {
            sequencer.open();
            sequencer.setSequence(sequence);
          }
          catch (InvalidMidiDataException localInvalidMidiDataException) {}catch (MidiUnavailableException localMidiUnavailableException) {}
        }
        sequencer.addMetaEventListener(this);
        try
        {
          sequencer.start();
        }
        catch (Exception localException1) {}
      }
    }
    catch (Exception localException2) {}
  }
  
  public synchronized void stop()
  {
    lastPlayCall = 0L;
    if (clip != null)
    {
      try
      {
        clip.flush();
      }
      catch (Exception localException1) {}
      try
      {
        clip.stop();
      }
      catch (Exception localException2) {}
    }
    else if (datapusher != null)
    {
      datapusher.stop();
    }
    else if (sequencer != null)
    {
      try
      {
        sequencerloop = false;
        sequencer.addMetaEventListener(this);
        sequencer.stop();
      }
      catch (Exception localException3) {}
      try
      {
        sequencer.close();
      }
      catch (Exception localException4) {}
    }
  }
  
  public synchronized void update(LineEvent paramLineEvent) {}
  
  public synchronized void meta(MetaMessage paramMetaMessage)
  {
    if (paramMetaMessage.getType() == 47) {
      if (sequencerloop)
      {
        sequencer.setMicrosecondPosition(0L);
        loop();
      }
      else
      {
        stop();
      }
    }
  }
  
  public String toString()
  {
    return getClass().toString();
  }
  
  protected void finalize()
  {
    if (clip != null) {
      clip.close();
    }
    if (datapusher != null) {
      datapusher.close();
    }
    if (sequencer != null) {
      sequencer.close();
    }
  }
  
  private boolean loadAudioData(AudioInputStream paramAudioInputStream)
    throws IOException, UnsupportedAudioFileException
  {
    paramAudioInputStream = Toolkit.getPCMConvertedAudioInputStream(paramAudioInputStream);
    if (paramAudioInputStream == null) {
      return false;
    }
    loadedAudioFormat = paramAudioInputStream.getFormat();
    long l1 = paramAudioInputStream.getFrameLength();
    int i = loadedAudioFormat.getFrameSize();
    long l2 = -1L;
    if ((l1 != -1L) && (l1 > 0L) && (i != -1) && (i > 0)) {
      l2 = l1 * i;
    }
    if (l2 != -1L) {
      readStream(paramAudioInputStream, l2);
    } else {
      readStream(paramAudioInputStream);
    }
    return true;
  }
  
  private void readStream(AudioInputStream paramAudioInputStream, long paramLong)
    throws IOException
  {
    int i;
    if (paramLong > 2147483647L) {
      i = Integer.MAX_VALUE;
    } else {
      i = (int)paramLong;
    }
    loadedAudio = new byte[i];
    int j;
    for (loadedAudioByteLength = 0;; loadedAudioByteLength += j)
    {
      j = paramAudioInputStream.read(loadedAudio, loadedAudioByteLength, i - loadedAudioByteLength);
      if (j <= 0)
      {
        paramAudioInputStream.close();
        break;
      }
    }
  }
  
  private void readStream(AudioInputStream paramAudioInputStream)
    throws IOException
  {
    DirectBAOS localDirectBAOS = new DirectBAOS();
    byte[] arrayOfByte = new byte['䀀'];
    int i = 0;
    int j = 0;
    for (;;)
    {
      i = paramAudioInputStream.read(arrayOfByte, 0, arrayOfByte.length);
      if (i <= 0)
      {
        paramAudioInputStream.close();
        break;
      }
      j += i;
      localDirectBAOS.write(arrayOfByte, 0, i);
    }
    loadedAudio = localDirectBAOS.getInternalBuffer();
    loadedAudioByteLength = j;
  }
  
  private boolean createClip()
  {
    try
    {
      DataLine.Info localInfo = new DataLine.Info(Clip.class, loadedAudioFormat);
      if (!AudioSystem.isLineSupported(localInfo)) {
        return false;
      }
      Line localLine = AudioSystem.getLine(localInfo);
      if (!(localLine instanceof AutoClosingClip)) {
        return false;
      }
      clip = ((AutoClosingClip)localLine);
      clip.setAutoClosing(true);
    }
    catch (Exception localException)
    {
      return false;
    }
    return clip != null;
  }
  
  private boolean createSourceDataLine()
  {
    try
    {
      DataLine.Info localInfo = new DataLine.Info(SourceDataLine.class, loadedAudioFormat);
      if (!AudioSystem.isLineSupported(localInfo)) {
        return false;
      }
      SourceDataLine localSourceDataLine = (SourceDataLine)AudioSystem.getLine(localInfo);
      datapusher = new DataPusher(localSourceDataLine, loadedAudioFormat, loadedAudio, loadedAudioByteLength);
    }
    catch (Exception localException)
    {
      return false;
    }
    return datapusher != null;
  }
  
  private boolean createSequencer(BufferedInputStream paramBufferedInputStream)
    throws IOException
  {
    try
    {
      sequencer = MidiSystem.getSequencer();
    }
    catch (MidiUnavailableException localMidiUnavailableException)
    {
      return false;
    }
    if (sequencer == null) {
      return false;
    }
    try
    {
      sequence = MidiSystem.getSequence(paramBufferedInputStream);
      if (sequence == null) {
        return false;
      }
    }
    catch (InvalidMidiDataException localInvalidMidiDataException)
    {
      return false;
    }
    return true;
  }
  
  private static class DirectBAOS
    extends ByteArrayOutputStream
  {
    DirectBAOS() {}
    
    public byte[] getInternalBuffer()
    {
      return buf;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\JavaSoundAudioClip.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */