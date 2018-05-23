package javax.sound.midi.spi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiFileFormat;
import javax.sound.midi.Sequence;

public abstract class MidiFileReader
{
  public MidiFileReader() {}
  
  public abstract MidiFileFormat getMidiFileFormat(InputStream paramInputStream)
    throws InvalidMidiDataException, IOException;
  
  public abstract MidiFileFormat getMidiFileFormat(URL paramURL)
    throws InvalidMidiDataException, IOException;
  
  public abstract MidiFileFormat getMidiFileFormat(File paramFile)
    throws InvalidMidiDataException, IOException;
  
  public abstract Sequence getSequence(InputStream paramInputStream)
    throws InvalidMidiDataException, IOException;
  
  public abstract Sequence getSequence(URL paramURL)
    throws InvalidMidiDataException, IOException;
  
  public abstract Sequence getSequence(File paramFile)
    throws InvalidMidiDataException, IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\midi\spi\MidiFileReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */