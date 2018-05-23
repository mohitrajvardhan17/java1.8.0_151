package com.sun.media.sound;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.spi.AudioFileReader;

public final class WaveExtensibleFileReader
  extends AudioFileReader
{
  private static final String[] channelnames = { "FL", "FR", "FC", "LF", "BL", "BR", "FLC", "FLR", "BC", "SL", "SR", "TC", "TFL", "TFC", "TFR", "TBL", "TBC", "TBR" };
  private static final String[] allchannelnames = { "w1", "w2", "w3", "w4", "w5", "w6", "w7", "w8", "w9", "w10", "w11", "w12", "w13", "w14", "w15", "w16", "w17", "w18", "w19", "w20", "w21", "w22", "w23", "w24", "w25", "w26", "w27", "w28", "w29", "w30", "w31", "w32", "w33", "w34", "w35", "w36", "w37", "w38", "w39", "w40", "w41", "w42", "w43", "w44", "w45", "w46", "w47", "w48", "w49", "w50", "w51", "w52", "w53", "w54", "w55", "w56", "w57", "w58", "w59", "w60", "w61", "w62", "w63", "w64" };
  private static final GUID SUBTYPE_PCM = new GUID(1L, 0, 16, 128, 0, 0, 170, 0, 56, 155, 113);
  private static final GUID SUBTYPE_IEEE_FLOAT = new GUID(3L, 0, 16, 128, 0, 0, 170, 0, 56, 155, 113);
  
  public WaveExtensibleFileReader() {}
  
  private String decodeChannelMask(long paramLong)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    long l = 1L;
    for (int i = 0; i < allchannelnames.length; i++)
    {
      if ((paramLong & l) != 0L) {
        if (i < channelnames.length) {
          localStringBuffer.append(channelnames[i] + " ");
        } else {
          localStringBuffer.append(allchannelnames[i] + " ");
        }
      }
      l *= 2L;
    }
    if (localStringBuffer.length() == 0) {
      return null;
    }
    return localStringBuffer.substring(0, localStringBuffer.length() - 1);
  }
  
  /* Error */
  public AudioFileFormat getAudioFileFormat(InputStream paramInputStream)
    throws UnsupportedAudioFileException, IOException
  {
    // Byte code:
    //   0: aload_1
    //   1: sipush 200
    //   4: invokevirtual 374	java/io/InputStream:mark	(I)V
    //   7: aload_0
    //   8: aload_1
    //   9: invokespecial 365	com/sun/media/sound/WaveExtensibleFileReader:internal_getAudioFileFormat	(Ljava/io/InputStream;)Ljavax/sound/sampled/AudioFileFormat;
    //   12: astore_2
    //   13: aload_1
    //   14: invokevirtual 373	java/io/InputStream:reset	()V
    //   17: goto +10 -> 27
    //   20: astore_3
    //   21: aload_1
    //   22: invokevirtual 373	java/io/InputStream:reset	()V
    //   25: aload_3
    //   26: athrow
    //   27: aload_2
    //   28: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	29	0	this	WaveExtensibleFileReader
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
    long l1 = 1L;
    int m = 1;
    int n = 1;
    int i1 = 1;
    long l2 = 0L;
    GUID localGUID = null;
    while (localRIFFReader.hasNextChunk())
    {
      localObject = localRIFFReader.nextChunk();
      if (((RIFFReader)localObject).getFormat().equals("fmt "))
      {
        i = 1;
        int i2 = ((RIFFReader)localObject).readUnsignedShort();
        if (i2 != 65534) {
          throw new UnsupportedAudioFileException();
        }
        k = ((RIFFReader)localObject).readUnsignedShort();
        l1 = ((RIFFReader)localObject).readUnsignedInt();
        ((RIFFReader)localObject).readUnsignedInt();
        m = ((RIFFReader)localObject).readUnsignedShort();
        n = ((RIFFReader)localObject).readUnsignedShort();
        int i3 = ((RIFFReader)localObject).readUnsignedShort();
        if (i3 != 22) {
          throw new UnsupportedAudioFileException();
        }
        i1 = ((RIFFReader)localObject).readUnsignedShort();
        if (i1 > n) {
          throw new UnsupportedAudioFileException();
        }
        l2 = ((RIFFReader)localObject).readUnsignedInt();
        localGUID = GUID.read((RIFFReader)localObject);
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
    Object localObject = new HashMap();
    String str = decodeChannelMask(l2);
    if (str != null) {
      ((Map)localObject).put("channelOrder", str);
    }
    if (l2 != 0L) {
      ((Map)localObject).put("channelMask", Long.valueOf(l2));
    }
    ((Map)localObject).put("validBitsPerSample", Integer.valueOf(i1));
    AudioFormat localAudioFormat = null;
    if (localGUID.equals(SUBTYPE_PCM))
    {
      if (n == 8) {
        localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, (float)l1, n, k, m, (float)l1, false, (Map)localObject);
      } else {
        localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, (float)l1, n, k, m, (float)l1, false, (Map)localObject);
      }
    }
    else if (localGUID.equals(SUBTYPE_IEEE_FLOAT)) {
      localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, (float)l1, n, k, m, (float)l1, false, (Map)localObject);
    } else {
      throw new UnsupportedAudioFileException();
    }
    AudioFileFormat localAudioFileFormat = new AudioFileFormat(AudioFileFormat.Type.WAVE, localAudioFormat, -1);
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
  
  private static class GUID
  {
    long i1;
    int s1;
    int s2;
    int x1;
    int x2;
    int x3;
    int x4;
    int x5;
    int x6;
    int x7;
    int x8;
    
    private GUID() {}
    
    GUID(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10)
    {
      i1 = paramLong;
      s1 = paramInt1;
      s2 = paramInt2;
      x1 = paramInt3;
      x2 = paramInt4;
      x3 = paramInt5;
      x4 = paramInt6;
      x5 = paramInt7;
      x6 = paramInt8;
      x7 = paramInt9;
      x8 = paramInt10;
    }
    
    public static GUID read(RIFFReader paramRIFFReader)
      throws IOException
    {
      GUID localGUID = new GUID();
      i1 = paramRIFFReader.readUnsignedInt();
      s1 = paramRIFFReader.readUnsignedShort();
      s2 = paramRIFFReader.readUnsignedShort();
      x1 = paramRIFFReader.readUnsignedByte();
      x2 = paramRIFFReader.readUnsignedByte();
      x3 = paramRIFFReader.readUnsignedByte();
      x4 = paramRIFFReader.readUnsignedByte();
      x5 = paramRIFFReader.readUnsignedByte();
      x6 = paramRIFFReader.readUnsignedByte();
      x7 = paramRIFFReader.readUnsignedByte();
      x8 = paramRIFFReader.readUnsignedByte();
      return localGUID;
    }
    
    public int hashCode()
    {
      return (int)i1;
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof GUID)) {
        return false;
      }
      GUID localGUID = (GUID)paramObject;
      if (i1 != i1) {
        return false;
      }
      if (s1 != s1) {
        return false;
      }
      if (s2 != s2) {
        return false;
      }
      if (x1 != x1) {
        return false;
      }
      if (x2 != x2) {
        return false;
      }
      if (x3 != x3) {
        return false;
      }
      if (x4 != x4) {
        return false;
      }
      if (x5 != x5) {
        return false;
      }
      if (x6 != x6) {
        return false;
      }
      if (x7 != x7) {
        return false;
      }
      return x8 == x8;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\WaveExtensibleFileReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */