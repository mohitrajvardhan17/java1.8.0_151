package com.sun.media.sound;

import java.io.BufferedInputStream;
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
import javax.sound.sampled.spi.AudioFileReader;

public final class WaveFloatFileReader
  extends AudioFileReader
{
  public WaveFloatFileReader() {}
  
  /* Error */
  public AudioFileFormat getAudioFileFormat(InputStream paramInputStream)
    throws UnsupportedAudioFileException, IOException
  {
    // Byte code:
    //   0: aload_1
    //   1: sipush 200
    //   4: invokevirtual 130	java/io/InputStream:mark	(I)V
    //   7: aload_0
    //   8: aload_1
    //   9: invokespecial 124	com/sun/media/sound/WaveFloatFileReader:internal_getAudioFileFormat	(Ljava/io/InputStream;)Ljavax/sound/sampled/AudioFileFormat;
    //   12: astore_2
    //   13: aload_1
    //   14: invokevirtual 129	java/io/InputStream:reset	()V
    //   17: goto +10 -> 27
    //   20: astore_3
    //   21: aload_1
    //   22: invokevirtual 129	java/io/InputStream:reset	()V
    //   25: aload_3
    //   26: athrow
    //   27: aload_2
    //   28: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	29	0	this	WaveFloatFileReader
    //   0	29	1	paramInputStream	InputStream
    //   12	16	2	localAudioFileFormat	AudioFileFormat
    //   20	6	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	20	finally
  }
  
  private AudioFileFormat internal_getAudioFileFormat(InputStream paramInputStream)
    throws UnsupportedAudioFileException, IOException
  {
    RIFFReader localRIFFReader = new RIFFReader(paramInputStream);
    if (!localRIFFReader.getFormat().equals("RIFF")) {
      throw new UnsupportedAudioFileException();
    }
    if (!localRIFFReader.getType().equals("WAVE")) {
      throw new UnsupportedAudioFileException();
    }
    int i = 0;
    int j = 0;
    int k = 1;
    long l = 1L;
    int m = 1;
    int n = 1;
    while (localRIFFReader.hasNextChunk())
    {
      localObject = localRIFFReader.nextChunk();
      if (((RIFFReader)localObject).getFormat().equals("fmt "))
      {
        i = 1;
        int i1 = ((RIFFReader)localObject).readUnsignedShort();
        if (i1 != 3) {
          throw new UnsupportedAudioFileException();
        }
        k = ((RIFFReader)localObject).readUnsignedShort();
        l = ((RIFFReader)localObject).readUnsignedInt();
        ((RIFFReader)localObject).readUnsignedInt();
        m = ((RIFFReader)localObject).readUnsignedShort();
        n = ((RIFFReader)localObject).readUnsignedShort();
      }
      if (((RIFFReader)localObject).getFormat().equals("data"))
      {
        j = 1;
        break;
      }
    }
    if (i == 0) {
      throw new UnsupportedAudioFileException();
    }
    if (j == 0) {
      throw new UnsupportedAudioFileException();
    }
    Object localObject = new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, (float)l, n, k, m, (float)l, false);
    AudioFileFormat localAudioFileFormat = new AudioFileFormat(AudioFileFormat.Type.WAVE, (AudioFormat)localObject, -1);
    return localAudioFileFormat;
  }
  
  public AudioInputStream getAudioInputStream(InputStream paramInputStream)
    throws UnsupportedAudioFileException, IOException
  {
    AudioFileFormat localAudioFileFormat = getAudioFileFormat(paramInputStream);
    RIFFReader localRIFFReader1 = new RIFFReader(paramInputStream);
    if (!localRIFFReader1.getFormat().equals("RIFF")) {
      throw new UnsupportedAudioFileException();
    }
    if (!localRIFFReader1.getType().equals("WAVE")) {
      throw new UnsupportedAudioFileException();
    }
    while (localRIFFReader1.hasNextChunk())
    {
      RIFFReader localRIFFReader2 = localRIFFReader1.nextChunk();
      if (localRIFFReader2.getFormat().equals("data")) {
        return new AudioInputStream(localRIFFReader2, localAudioFileFormat.getFormat(), localRIFFReader2.getSize());
      }
    }
    throw new UnsupportedAudioFileException();
  }
  
  public AudioFileFormat getAudioFileFormat(URL paramURL)
    throws UnsupportedAudioFileException, IOException
  {
    InputStream localInputStream = paramURL.openStream();
    AudioFileFormat localAudioFileFormat;
    try
    {
      localAudioFileFormat = getAudioFileFormat(new BufferedInputStream(localInputStream));
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
    FileInputStream localFileInputStream = new FileInputStream(paramFile);
    AudioFileFormat localAudioFileFormat;
    try
    {
      localAudioFileFormat = getAudioFileFormat(new BufferedInputStream(localFileInputStream));
    }
    finally
    {
      localFileInputStream.close();
    }
    return localAudioFileFormat;
  }
  
  public AudioInputStream getAudioInputStream(URL paramURL)
    throws UnsupportedAudioFileException, IOException
  {
    return getAudioInputStream(new BufferedInputStream(paramURL.openStream()));
  }
  
  public AudioInputStream getAudioInputStream(File paramFile)
    throws UnsupportedAudioFileException, IOException
  {
    return getAudioInputStream(new BufferedInputStream(new FileInputStream(paramFile)));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\WaveFloatFileReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */