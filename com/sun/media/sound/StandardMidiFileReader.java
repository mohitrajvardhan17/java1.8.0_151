package com.sun.media.sound;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiFileFormat;
import javax.sound.midi.Sequence;
import javax.sound.midi.spi.MidiFileReader;

public final class StandardMidiFileReader
  extends MidiFileReader
{
  private static final int MThd_MAGIC = 1297377380;
  private static final int bisBufferSize = 1024;
  
  public StandardMidiFileReader() {}
  
  public MidiFileFormat getMidiFileFormat(InputStream paramInputStream)
    throws InvalidMidiDataException, IOException
  {
    return getMidiFileFormatFromStream(paramInputStream, -1, null);
  }
  
  private MidiFileFormat getMidiFileFormatFromStream(InputStream paramInputStream, int paramInt, SMFParser paramSMFParser)
    throws InvalidMidiDataException, IOException
  {
    int i = 16;
    int j = -1;
    DataInputStream localDataInputStream;
    if ((paramInputStream instanceof DataInputStream)) {
      localDataInputStream = (DataInputStream)paramInputStream;
    } else {
      localDataInputStream = new DataInputStream(paramInputStream);
    }
    if (paramSMFParser == null) {
      localDataInputStream.mark(i);
    } else {
      stream = localDataInputStream;
    }
    int k;
    float f;
    int n;
    try
    {
      int i1 = localDataInputStream.readInt();
      if (i1 != 1297377380) {
        throw new InvalidMidiDataException("not a valid MIDI file");
      }
      int i2 = localDataInputStream.readInt() - 6;
      k = localDataInputStream.readShort();
      int m = localDataInputStream.readShort();
      int i3 = localDataInputStream.readShort();
      if (i3 > 0)
      {
        f = 0.0F;
        n = i3;
      }
      else
      {
        int i4 = -1 * (i3 >> 8);
        switch (i4)
        {
        case 24: 
          f = 24.0F;
          break;
        case 25: 
          f = 25.0F;
          break;
        case 29: 
          f = 29.97F;
          break;
        case 30: 
          f = 30.0F;
          break;
        case 26: 
        case 27: 
        case 28: 
        default: 
          throw new InvalidMidiDataException("Unknown frame code: " + i4);
        }
        n = i3 & 0xFF;
      }
      if (paramSMFParser != null)
      {
        localDataInputStream.skip(i2);
        tracks = m;
      }
    }
    finally
    {
      if (paramSMFParser == null) {
        localDataInputStream.reset();
      }
    }
    MidiFileFormat localMidiFileFormat = new MidiFileFormat(k, f, n, paramInt, j);
    return localMidiFileFormat;
  }
  
  public MidiFileFormat getMidiFileFormat(URL paramURL)
    throws InvalidMidiDataException, IOException
  {
    InputStream localInputStream = paramURL.openStream();
    BufferedInputStream localBufferedInputStream = new BufferedInputStream(localInputStream, 1024);
    MidiFileFormat localMidiFileFormat = null;
    try
    {
      localMidiFileFormat = getMidiFileFormat(localBufferedInputStream);
    }
    finally
    {
      localBufferedInputStream.close();
    }
    return localMidiFileFormat;
  }
  
  public MidiFileFormat getMidiFileFormat(File paramFile)
    throws InvalidMidiDataException, IOException
  {
    FileInputStream localFileInputStream = new FileInputStream(paramFile);
    BufferedInputStream localBufferedInputStream = new BufferedInputStream(localFileInputStream, 1024);
    long l = paramFile.length();
    if (l > 2147483647L) {
      l = -1L;
    }
    MidiFileFormat localMidiFileFormat = null;
    try
    {
      localMidiFileFormat = getMidiFileFormatFromStream(localBufferedInputStream, (int)l, null);
    }
    finally
    {
      localBufferedInputStream.close();
    }
    return localMidiFileFormat;
  }
  
  public Sequence getSequence(InputStream paramInputStream)
    throws InvalidMidiDataException, IOException
  {
    SMFParser localSMFParser = new SMFParser();
    MidiFileFormat localMidiFileFormat = getMidiFileFormatFromStream(paramInputStream, -1, localSMFParser);
    if ((localMidiFileFormat.getType() != 0) && (localMidiFileFormat.getType() != 1)) {
      throw new InvalidMidiDataException("Invalid or unsupported file type: " + localMidiFileFormat.getType());
    }
    Sequence localSequence = new Sequence(localMidiFileFormat.getDivisionType(), localMidiFileFormat.getResolution());
    for (int i = 0; (i < tracks) && (localSMFParser.nextTrack()); i++) {
      localSMFParser.readTrack(localSequence.createTrack());
    }
    return localSequence;
  }
  
  public Sequence getSequence(URL paramURL)
    throws InvalidMidiDataException, IOException
  {
    Object localObject1 = paramURL.openStream();
    localObject1 = new BufferedInputStream((InputStream)localObject1, 1024);
    Sequence localSequence = null;
    try
    {
      localSequence = getSequence((InputStream)localObject1);
    }
    finally
    {
      ((InputStream)localObject1).close();
    }
    return localSequence;
  }
  
  public Sequence getSequence(File paramFile)
    throws InvalidMidiDataException, IOException
  {
    Object localObject1 = new FileInputStream(paramFile);
    localObject1 = new BufferedInputStream((InputStream)localObject1, 1024);
    Sequence localSequence = null;
    try
    {
      localSequence = getSequence((InputStream)localObject1);
    }
    finally
    {
      ((InputStream)localObject1).close();
    }
    return localSequence;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\StandardMidiFileReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */