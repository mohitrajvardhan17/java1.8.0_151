package com.sun.media.sound;

import java.io.IOException;
import java.util.Vector;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;

public final class AlawCodec
  extends SunCodec
{
  private static final byte[] ALAW_TABH = new byte['Ā'];
  private static final byte[] ALAW_TABL = new byte['Ā'];
  private static final AudioFormat.Encoding[] alawEncodings = { AudioFormat.Encoding.ALAW, AudioFormat.Encoding.PCM_SIGNED };
  private static final short[] seg_end = { 255, 511, 1023, 2047, 4095, 8191, 16383, Short.MAX_VALUE };
  
  public AlawCodec()
  {
    super(alawEncodings, alawEncodings);
  }
  
  public AudioFormat.Encoding[] getTargetEncodings(AudioFormat paramAudioFormat)
  {
    AudioFormat.Encoding[] arrayOfEncoding;
    if (paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED))
    {
      if (paramAudioFormat.getSampleSizeInBits() == 16)
      {
        arrayOfEncoding = new AudioFormat.Encoding[1];
        arrayOfEncoding[0] = AudioFormat.Encoding.ALAW;
        return arrayOfEncoding;
      }
      return new AudioFormat.Encoding[0];
    }
    if (paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.ALAW))
    {
      if (paramAudioFormat.getSampleSizeInBits() == 8)
      {
        arrayOfEncoding = new AudioFormat.Encoding[1];
        arrayOfEncoding[0] = AudioFormat.Encoding.PCM_SIGNED;
        return arrayOfEncoding;
      }
      return new AudioFormat.Encoding[0];
    }
    return new AudioFormat.Encoding[0];
  }
  
  public AudioFormat[] getTargetFormats(AudioFormat.Encoding paramEncoding, AudioFormat paramAudioFormat)
  {
    if (((paramEncoding.equals(AudioFormat.Encoding.PCM_SIGNED)) && (paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.ALAW))) || ((paramEncoding.equals(AudioFormat.Encoding.ALAW)) && (paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)))) {
      return getOutputFormats(paramAudioFormat);
    }
    return new AudioFormat[0];
  }
  
  public AudioInputStream getAudioInputStream(AudioFormat.Encoding paramEncoding, AudioInputStream paramAudioInputStream)
  {
    AudioFormat localAudioFormat1 = paramAudioInputStream.getFormat();
    AudioFormat.Encoding localEncoding = localAudioFormat1.getEncoding();
    if (localEncoding.equals(paramEncoding)) {
      return paramAudioInputStream;
    }
    AudioFormat localAudioFormat2 = null;
    if (!isConversionSupported(paramEncoding, paramAudioInputStream.getFormat())) {
      throw new IllegalArgumentException("Unsupported conversion: " + paramAudioInputStream.getFormat().toString() + " to " + paramEncoding.toString());
    }
    if ((localEncoding.equals(AudioFormat.Encoding.ALAW)) && (paramEncoding.equals(AudioFormat.Encoding.PCM_SIGNED))) {
      localAudioFormat2 = new AudioFormat(paramEncoding, localAudioFormat1.getSampleRate(), 16, localAudioFormat1.getChannels(), 2 * localAudioFormat1.getChannels(), localAudioFormat1.getSampleRate(), localAudioFormat1.isBigEndian());
    } else if ((localEncoding.equals(AudioFormat.Encoding.PCM_SIGNED)) && (paramEncoding.equals(AudioFormat.Encoding.ALAW))) {
      localAudioFormat2 = new AudioFormat(paramEncoding, localAudioFormat1.getSampleRate(), 8, localAudioFormat1.getChannels(), localAudioFormat1.getChannels(), localAudioFormat1.getSampleRate(), false);
    } else {
      throw new IllegalArgumentException("Unsupported conversion: " + paramAudioInputStream.getFormat().toString() + " to " + paramEncoding.toString());
    }
    return getAudioInputStream(localAudioFormat2, paramAudioInputStream);
  }
  
  public AudioInputStream getAudioInputStream(AudioFormat paramAudioFormat, AudioInputStream paramAudioInputStream)
  {
    return getConvertedStream(paramAudioFormat, paramAudioInputStream);
  }
  
  private AudioInputStream getConvertedStream(AudioFormat paramAudioFormat, AudioInputStream paramAudioInputStream)
  {
    Object localObject = null;
    AudioFormat localAudioFormat = paramAudioInputStream.getFormat();
    if (localAudioFormat.matches(paramAudioFormat)) {
      localObject = paramAudioInputStream;
    } else {
      localObject = new AlawCodecStream(paramAudioInputStream, paramAudioFormat);
    }
    return (AudioInputStream)localObject;
  }
  
  private AudioFormat[] getOutputFormats(AudioFormat paramAudioFormat)
  {
    Vector localVector = new Vector();
    AudioFormat localAudioFormat;
    if (AudioFormat.Encoding.PCM_SIGNED.equals(paramAudioFormat.getEncoding()))
    {
      localAudioFormat = new AudioFormat(AudioFormat.Encoding.ALAW, paramAudioFormat.getSampleRate(), 8, paramAudioFormat.getChannels(), paramAudioFormat.getChannels(), paramAudioFormat.getSampleRate(), false);
      localVector.addElement(localAudioFormat);
    }
    if (AudioFormat.Encoding.ALAW.equals(paramAudioFormat.getEncoding()))
    {
      localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, paramAudioFormat.getSampleRate(), 16, paramAudioFormat.getChannels(), paramAudioFormat.getChannels() * 2, paramAudioFormat.getSampleRate(), false);
      localVector.addElement(localAudioFormat);
      localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, paramAudioFormat.getSampleRate(), 16, paramAudioFormat.getChannels(), paramAudioFormat.getChannels() * 2, paramAudioFormat.getSampleRate(), true);
      localVector.addElement(localAudioFormat);
    }
    AudioFormat[] arrayOfAudioFormat = new AudioFormat[localVector.size()];
    for (int i = 0; i < arrayOfAudioFormat.length; i++) {
      arrayOfAudioFormat[i] = ((AudioFormat)(AudioFormat)localVector.elementAt(i));
    }
    return arrayOfAudioFormat;
  }
  
  static
  {
    for (int i = 0; i < 256; i++)
    {
      int j = i ^ 0x55;
      int k = (j & 0xF) << 4;
      int m = (j & 0x70) >> 4;
      int n = k + 8;
      if (m >= 1) {
        n += 256;
      }
      if (m > 1) {
        n <<= m - 1;
      }
      if ((j & 0x80) == 0) {
        n = -n;
      }
      ALAW_TABL[i] = ((byte)n);
      ALAW_TABH[i] = ((byte)(n >> 8));
    }
  }
  
  final class AlawCodecStream
    extends AudioInputStream
  {
    private static final int tempBufferSize = 64;
    private byte[] tempBuffer = null;
    boolean encode = false;
    AudioFormat encodeFormat;
    AudioFormat decodeFormat;
    byte[] tabByte1 = null;
    byte[] tabByte2 = null;
    int highByte = 0;
    int lowByte = 1;
    
    AlawCodecStream(AudioInputStream paramAudioInputStream, AudioFormat paramAudioFormat)
    {
      super(paramAudioFormat, -1L);
      AudioFormat localAudioFormat = paramAudioInputStream.getFormat();
      if (!isConversionSupported(paramAudioFormat, localAudioFormat)) {
        throw new IllegalArgumentException("Unsupported conversion: " + localAudioFormat.toString() + " to " + paramAudioFormat.toString());
      }
      boolean bool;
      if (AudioFormat.Encoding.ALAW.equals(localAudioFormat.getEncoding()))
      {
        encode = false;
        encodeFormat = localAudioFormat;
        decodeFormat = paramAudioFormat;
        bool = paramAudioFormat.isBigEndian();
      }
      else
      {
        encode = true;
        encodeFormat = paramAudioFormat;
        decodeFormat = localAudioFormat;
        bool = localAudioFormat.isBigEndian();
        tempBuffer = new byte[64];
      }
      if (bool)
      {
        tabByte1 = AlawCodec.ALAW_TABH;
        tabByte2 = AlawCodec.ALAW_TABL;
        highByte = 0;
        lowByte = 1;
      }
      else
      {
        tabByte1 = AlawCodec.ALAW_TABL;
        tabByte2 = AlawCodec.ALAW_TABH;
        highByte = 1;
        lowByte = 0;
      }
      if ((paramAudioInputStream instanceof AudioInputStream)) {
        frameLength = paramAudioInputStream.getFrameLength();
      }
      framePos = 0L;
      frameSize = localAudioFormat.getFrameSize();
      if (frameSize == -1) {
        frameSize = 1;
      }
    }
    
    private short search(short paramShort1, short[] paramArrayOfShort, short paramShort2)
    {
      for (short s = 0; s < paramShort2; s = (short)(s + 1)) {
        if (paramShort1 <= paramArrayOfShort[s]) {
          return s;
        }
      }
      return paramShort2;
    }
    
    public int read()
      throws IOException
    {
      byte[] arrayOfByte = new byte[1];
      return read(arrayOfByte, 0, arrayOfByte.length);
    }
    
    public int read(byte[] paramArrayOfByte)
      throws IOException
    {
      return read(paramArrayOfByte, 0, paramArrayOfByte.length);
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      if (paramInt2 % frameSize != 0) {
        paramInt2 -= paramInt2 % frameSize;
      }
      if (encode)
      {
        i = 15;
        j = 4;
        int i3 = 0;
        int i4 = paramInt1;
        int i5 = paramInt2 * 2;
        for (int i6 = i5 > 64 ? 64 : i5; (i3 = super.read(tempBuffer, 0, i6)) > 0; i6 = i5 > 64 ? 64 : i5)
        {
          for (int n = 0; n < i3; n += 2)
          {
            int i1 = (short)(tempBuffer[(n + highByte)] << 8 & 0xFF00);
            i1 = (short)(i1 | (short)(tempBuffer[(n + lowByte)] & 0xFF));
            if (i1 >= 0)
            {
              k = 213;
            }
            else
            {
              k = 85;
              i1 = (short)(-i1 - 8);
            }
            m = search(i1, AlawCodec.seg_end, (short)8);
            int i2;
            if (m >= 8)
            {
              i2 = (byte)(0x7F ^ k);
            }
            else
            {
              i2 = (byte)(m << j);
              if (m < 2) {
                i2 = (byte)(i2 | (byte)(i1 >> 4 & i));
              } else {
                i2 = (byte)(i2 | (byte)(i1 >> m + 3 & i));
              }
              i2 = (byte)(i2 ^ k);
            }
            paramArrayOfByte[i4] = i2;
            i4++;
          }
          i5 -= i3;
        }
        if ((i4 == paramInt1) && (i3 < 0)) {
          return i3;
        }
        return i4 - paramInt1;
      }
      int j = paramInt2 / 2;
      int k = paramInt1 + paramInt2 / 2;
      int m = super.read(paramArrayOfByte, k, j);
      for (int i = paramInt1; i < paramInt1 + m * 2; i += 2)
      {
        paramArrayOfByte[i] = tabByte1[(paramArrayOfByte[k] & 0xFF)];
        paramArrayOfByte[(i + 1)] = tabByte2[(paramArrayOfByte[k] & 0xFF)];
        k++;
      }
      if (m < 0) {
        return m;
      }
      return i - paramInt1;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\AlawCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */