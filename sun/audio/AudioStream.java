package sun.audio;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiFileFormat;
import javax.sound.midi.MidiSystem;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class AudioStream
  extends FilterInputStream
{
  AudioInputStream ais = null;
  AudioFormat format = null;
  MidiFileFormat midiformat = null;
  InputStream stream = null;
  
  public AudioStream(InputStream paramInputStream)
    throws IOException
  {
    super(paramInputStream);
    stream = paramInputStream;
    if (!paramInputStream.markSupported()) {
      stream = new BufferedInputStream(paramInputStream, 1024);
    }
    try
    {
      ais = AudioSystem.getAudioInputStream(stream);
      format = ais.getFormat();
      in = ais;
    }
    catch (UnsupportedAudioFileException localUnsupportedAudioFileException)
    {
      try
      {
        midiformat = MidiSystem.getMidiFileFormat(stream);
      }
      catch (InvalidMidiDataException localInvalidMidiDataException)
      {
        throw new IOException("could not create audio stream from input stream");
      }
    }
  }
  
  public AudioData getData()
    throws IOException
  {
    int i = getLength();
    if (i < 1048576)
    {
      byte[] arrayOfByte = new byte[i];
      try
      {
        ais.read(arrayOfByte, 0, i);
      }
      catch (IOException localIOException)
      {
        throw new IOException("Could not create AudioData Object");
      }
      return new AudioData(format, arrayOfByte);
    }
    throw new IOException("could not create AudioData object");
  }
  
  public int getLength()
  {
    if ((ais != null) && (format != null)) {
      return (int)(ais.getFrameLength() * ais.getFormat().getFrameSize());
    }
    if (midiformat != null) {
      return midiformat.getByteLength();
    }
    return -1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\audio\AudioStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */