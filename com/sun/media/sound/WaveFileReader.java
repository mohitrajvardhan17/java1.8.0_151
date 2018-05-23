package com.sun.media.sound;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class WaveFileReader
  extends SunFileReader
{
  private static final int MAX_READ_LENGTH = 12;
  
  public WaveFileReader() {}
  
  public AudioFileFormat getAudioFileFormat(InputStream paramInputStream)
    throws UnsupportedAudioFileException, IOException
  {
    AudioFileFormat localAudioFileFormat = getFMT(paramInputStream, true);
    paramInputStream.reset();
    return localAudioFileFormat;
  }
  
  public AudioFileFormat getAudioFileFormat(URL paramURL)
    throws UnsupportedAudioFileException, IOException
  {
    InputStream localInputStream = paramURL.openStream();
    AudioFileFormat localAudioFileFormat = null;
    try
    {
      localAudioFileFormat = getFMT(localInputStream, false);
    }
    finally
    {
      localInputStream.close();
    }
    return localAudioFileFormat;
  }
  
  public AudioFileFormat getAudioFileFormat(File paramFile)
    throws UnsupportedAudioFileException, IOException
  {
    AudioFileFormat localAudioFileFormat = null;
    FileInputStream localFileInputStream = new FileInputStream(paramFile);
    try
    {
      localAudioFileFormat = getFMT(localFileInputStream, false);
    }
    finally
    {
      localFileInputStream.close();
    }
    return localAudioFileFormat;
  }
  
  public AudioInputStream getAudioInputStream(InputStream paramInputStream)
    throws UnsupportedAudioFileException, IOException
  {
    AudioFileFormat localAudioFileFormat = getFMT(paramInputStream, true);
    return new AudioInputStream(paramInputStream, localAudioFileFormat.getFormat(), localAudioFileFormat.getFrameLength());
  }
  
  public AudioInputStream getAudioInputStream(URL paramURL)
    throws UnsupportedAudioFileException, IOException
  {
    InputStream localInputStream = paramURL.openStream();
    AudioFileFormat localAudioFileFormat = null;
    try
    {
      localAudioFileFormat = getFMT(localInputStream, false);
    }
    finally
    {
      if (localAudioFileFormat == null) {
        localInputStream.close();
      }
    }
    return new AudioInputStream(localInputStream, localAudioFileFormat.getFormat(), localAudioFileFormat.getFrameLength());
  }
  
  public AudioInputStream getAudioInputStream(File paramFile)
    throws UnsupportedAudioFileException, IOException
  {
    FileInputStream localFileInputStream = new FileInputStream(paramFile);
    AudioFileFormat localAudioFileFormat = null;
    try
    {
      localAudioFileFormat = getFMT(localFileInputStream, false);
    }
    finally
    {
      if (localAudioFileFormat == null) {
        localFileInputStream.close();
      }
    }
    return new AudioInputStream(localFileInputStream, localAudioFileFormat.getFormat(), localAudioFileFormat.getFrameLength());
  }
  
  private AudioFileFormat getFMT(InputStream paramInputStream, boolean paramBoolean)
    throws UnsupportedAudioFileException, IOException
  {
    int i = 0;
    int k = 0;
    int m = 0;
    AudioFormat.Encoding localEncoding = null;
    DataInputStream localDataInputStream = new DataInputStream(paramInputStream);
    if (paramBoolean) {
      localDataInputStream.mark(12);
    }
    int i3 = localDataInputStream.readInt();
    int i4 = rllong(localDataInputStream);
    int i5 = localDataInputStream.readInt();
    int i6;
    if (i4 <= 0)
    {
      i4 = -1;
      i6 = -1;
    }
    else
    {
      i6 = i4 + 8;
    }
    if ((i3 != 1380533830) || (i5 != 1463899717))
    {
      if (paramBoolean) {
        localDataInputStream.reset();
      }
      throw new UnsupportedAudioFileException("not a WAVE file");
    }
    try
    {
      for (;;)
      {
        int j = localDataInputStream.readInt();
        i += 4;
        if (j == 1718449184) {
          break;
        }
        k = rllong(localDataInputStream);
        i += 4;
        if (k % 2 > 0) {
          k++;
        }
        i += localDataInputStream.skipBytes(k);
      }
      k = rllong(localDataInputStream);
    }
    catch (EOFException localEOFException1)
    {
      throw new UnsupportedAudioFileException("Not a valid WAV file");
    }
    i += 4;
    int i7 = i + k;
    m = rlshort(localDataInputStream);
    i += 2;
    if (m == 1) {
      localEncoding = AudioFormat.Encoding.PCM_SIGNED;
    } else if (m == 6) {
      localEncoding = AudioFormat.Encoding.ALAW;
    } else if (m == 7) {
      localEncoding = AudioFormat.Encoding.ULAW;
    } else {
      throw new UnsupportedAudioFileException("Not a supported WAV file");
    }
    int n = rlshort(localDataInputStream);
    i += 2;
    if (n <= 0) {
      throw new UnsupportedAudioFileException("Invalid number of channels");
    }
    long l1 = rllong(localDataInputStream);
    i += 4;
    long l2 = rllong(localDataInputStream);
    i += 4;
    int i1 = rlshort(localDataInputStream);
    i += 2;
    int i2 = rlshort(localDataInputStream);
    i += 2;
    if (i2 <= 0) {
      throw new UnsupportedAudioFileException("Invalid bitsPerSample");
    }
    if ((i2 == 8) && (localEncoding.equals(AudioFormat.Encoding.PCM_SIGNED))) {
      localEncoding = AudioFormat.Encoding.PCM_UNSIGNED;
    }
    if (k % 2 != 0) {
      k++;
    }
    if (i7 > i) {
      i += localDataInputStream.skipBytes(i7 - i);
    }
    i = 0;
    try
    {
      for (;;)
      {
        int i8 = localDataInputStream.readInt();
        i += 4;
        if (i8 == 1684108385) {
          break;
        }
        int i10 = rllong(localDataInputStream);
        i += 4;
        if (i10 % 2 > 0) {
          i10++;
        }
        i += localDataInputStream.skipBytes(i10);
      }
      i9 = rllong(localDataInputStream);
    }
    catch (EOFException localEOFException2)
    {
      throw new UnsupportedAudioFileException("Not a valid WAV file");
    }
    int i9;
    i += 4;
    AudioFormat localAudioFormat = new AudioFormat(localEncoding, (float)l1, i2, n, calculatePCMFrameSize(i2, n), (float)l1, false);
    return new WaveFileFormat(AudioFileFormat.Type.WAVE, i6, localAudioFormat, i9 / localAudioFormat.getFrameSize());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\WaveFileReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */