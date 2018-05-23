package com.sun.media.sound;

import java.io.IOException;
import java.util.Vector;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;

public final class PCMtoPCMCodec
  extends SunCodec
{
  private static final AudioFormat.Encoding[] inputEncodings = { AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED };
  private static final AudioFormat.Encoding[] outputEncodings = { AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED };
  private static final int tempBufferSize = 64;
  private byte[] tempBuffer = null;
  
  public PCMtoPCMCodec()
  {
    super(inputEncodings, outputEncodings);
  }
  
  public AudioFormat.Encoding[] getTargetEncodings(AudioFormat paramAudioFormat)
  {
    if ((paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) || (paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)))
    {
      AudioFormat.Encoding[] arrayOfEncoding = new AudioFormat.Encoding[2];
      arrayOfEncoding[0] = AudioFormat.Encoding.PCM_SIGNED;
      arrayOfEncoding[1] = AudioFormat.Encoding.PCM_UNSIGNED;
      return arrayOfEncoding;
    }
    return new AudioFormat.Encoding[0];
  }
  
  public AudioFormat[] getTargetFormats(AudioFormat.Encoding paramEncoding, AudioFormat paramAudioFormat)
  {
    AudioFormat[] arrayOfAudioFormat1 = getOutputFormats(paramAudioFormat);
    Vector localVector = new Vector();
    for (int i = 0; i < arrayOfAudioFormat1.length; i++) {
      if (arrayOfAudioFormat1[i].getEncoding().equals(paramEncoding)) {
        localVector.addElement(arrayOfAudioFormat1[i]);
      }
    }
    AudioFormat[] arrayOfAudioFormat2 = new AudioFormat[localVector.size()];
    for (int j = 0; j < arrayOfAudioFormat2.length; j++) {
      arrayOfAudioFormat2[j] = ((AudioFormat)(AudioFormat)localVector.elementAt(j));
    }
    return arrayOfAudioFormat2;
  }
  
  public AudioInputStream getAudioInputStream(AudioFormat.Encoding paramEncoding, AudioInputStream paramAudioInputStream)
  {
    if (isConversionSupported(paramEncoding, paramAudioInputStream.getFormat()))
    {
      AudioFormat localAudioFormat1 = paramAudioInputStream.getFormat();
      AudioFormat localAudioFormat2 = new AudioFormat(paramEncoding, localAudioFormat1.getSampleRate(), localAudioFormat1.getSampleSizeInBits(), localAudioFormat1.getChannels(), localAudioFormat1.getFrameSize(), localAudioFormat1.getFrameRate(), localAudioFormat1.isBigEndian());
      return getAudioInputStream(localAudioFormat2, paramAudioInputStream);
    }
    throw new IllegalArgumentException("Unsupported conversion: " + paramAudioInputStream.getFormat().toString() + " to " + paramEncoding.toString());
  }
  
  public AudioInputStream getAudioInputStream(AudioFormat paramAudioFormat, AudioInputStream paramAudioInputStream)
  {
    return getConvertedStream(paramAudioFormat, paramAudioInputStream);
  }
  
  private AudioInputStream getConvertedStream(AudioFormat paramAudioFormat, AudioInputStream paramAudioInputStream)
  {
    Object localObject = null;
    AudioFormat localAudioFormat = paramAudioInputStream.getFormat();
    if (localAudioFormat.matches(paramAudioFormat))
    {
      localObject = paramAudioInputStream;
    }
    else
    {
      localObject = new PCMtoPCMCodecStream(paramAudioInputStream, paramAudioFormat);
      tempBuffer = new byte[64];
    }
    return (AudioInputStream)localObject;
  }
  
  private AudioFormat[] getOutputFormats(AudioFormat paramAudioFormat)
  {
    Vector localVector = new Vector();
    int i = paramAudioFormat.getSampleSizeInBits();
    boolean bool = paramAudioFormat.isBigEndian();
    AudioFormat localAudioFormat;
    if (i == 8)
    {
      if (AudioFormat.Encoding.PCM_SIGNED.equals(paramAudioFormat.getEncoding()))
      {
        localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), false);
        localVector.addElement(localAudioFormat);
      }
      if (AudioFormat.Encoding.PCM_UNSIGNED.equals(paramAudioFormat.getEncoding()))
      {
        localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), false);
        localVector.addElement(localAudioFormat);
      }
    }
    else if (i == 16)
    {
      if ((AudioFormat.Encoding.PCM_SIGNED.equals(paramAudioFormat.getEncoding())) && (bool))
      {
        localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), true);
        localVector.addElement(localAudioFormat);
        localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), false);
        localVector.addElement(localAudioFormat);
        localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), false);
        localVector.addElement(localAudioFormat);
      }
      if ((AudioFormat.Encoding.PCM_UNSIGNED.equals(paramAudioFormat.getEncoding())) && (bool))
      {
        localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), true);
        localVector.addElement(localAudioFormat);
        localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), false);
        localVector.addElement(localAudioFormat);
        localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), false);
        localVector.addElement(localAudioFormat);
      }
      if ((AudioFormat.Encoding.PCM_SIGNED.equals(paramAudioFormat.getEncoding())) && (!bool))
      {
        localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), false);
        localVector.addElement(localAudioFormat);
        localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), true);
        localVector.addElement(localAudioFormat);
        localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), true);
        localVector.addElement(localAudioFormat);
      }
      if ((AudioFormat.Encoding.PCM_UNSIGNED.equals(paramAudioFormat.getEncoding())) && (!bool))
      {
        localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), false);
        localVector.addElement(localAudioFormat);
        localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), true);
        localVector.addElement(localAudioFormat);
        localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), true);
        localVector.addElement(localAudioFormat);
      }
    }
    AudioFormat[] arrayOfAudioFormat;
    synchronized (localVector)
    {
      arrayOfAudioFormat = new AudioFormat[localVector.size()];
      for (int j = 0; j < arrayOfAudioFormat.length; j++) {
        arrayOfAudioFormat[j] = ((AudioFormat)(AudioFormat)localVector.elementAt(j));
      }
    }
    return arrayOfAudioFormat;
  }
  
  class PCMtoPCMCodecStream
    extends AudioInputStream
  {
    private final int PCM_SWITCH_SIGNED_8BIT = 1;
    private final int PCM_SWITCH_ENDIAN = 2;
    private final int PCM_SWITCH_SIGNED_LE = 3;
    private final int PCM_SWITCH_SIGNED_BE = 4;
    private final int PCM_UNSIGNED_LE2SIGNED_BE = 5;
    private final int PCM_SIGNED_LE2UNSIGNED_BE = 6;
    private final int PCM_UNSIGNED_BE2SIGNED_LE = 7;
    private final int PCM_SIGNED_BE2UNSIGNED_LE = 8;
    private final int sampleSizeInBytes;
    private int conversionType = 0;
    
    PCMtoPCMCodecStream(AudioInputStream paramAudioInputStream, AudioFormat paramAudioFormat)
    {
      super(paramAudioFormat, -1L);
      int i = 0;
      AudioFormat.Encoding localEncoding1 = null;
      AudioFormat.Encoding localEncoding2 = null;
      AudioFormat localAudioFormat = paramAudioInputStream.getFormat();
      if (!isConversionSupported(localAudioFormat, paramAudioFormat)) {
        throw new IllegalArgumentException("Unsupported conversion: " + localAudioFormat.toString() + " to " + paramAudioFormat.toString());
      }
      localEncoding1 = localAudioFormat.getEncoding();
      localEncoding2 = paramAudioFormat.getEncoding();
      boolean bool1 = localAudioFormat.isBigEndian();
      boolean bool2 = paramAudioFormat.isBigEndian();
      i = localAudioFormat.getSampleSizeInBits();
      sampleSizeInBytes = (i / 8);
      if (i == 8)
      {
        if ((AudioFormat.Encoding.PCM_UNSIGNED.equals(localEncoding1)) && (AudioFormat.Encoding.PCM_SIGNED.equals(localEncoding2))) {
          conversionType = 1;
        } else if ((AudioFormat.Encoding.PCM_SIGNED.equals(localEncoding1)) && (AudioFormat.Encoding.PCM_UNSIGNED.equals(localEncoding2))) {
          conversionType = 1;
        }
      }
      else if ((localEncoding1.equals(localEncoding2)) && (bool1 != bool2)) {
        conversionType = 2;
      } else if ((AudioFormat.Encoding.PCM_UNSIGNED.equals(localEncoding1)) && (!bool1) && (AudioFormat.Encoding.PCM_SIGNED.equals(localEncoding2)) && (bool2)) {
        conversionType = 5;
      } else if ((AudioFormat.Encoding.PCM_SIGNED.equals(localEncoding1)) && (!bool1) && (AudioFormat.Encoding.PCM_UNSIGNED.equals(localEncoding2)) && (bool2)) {
        conversionType = 6;
      } else if ((AudioFormat.Encoding.PCM_UNSIGNED.equals(localEncoding1)) && (bool1) && (AudioFormat.Encoding.PCM_SIGNED.equals(localEncoding2)) && (!bool2)) {
        conversionType = 7;
      } else if ((AudioFormat.Encoding.PCM_SIGNED.equals(localEncoding1)) && (bool1) && (AudioFormat.Encoding.PCM_UNSIGNED.equals(localEncoding2)) && (!bool2)) {
        conversionType = 8;
      }
      frameSize = localAudioFormat.getFrameSize();
      if (frameSize == -1) {
        frameSize = 1;
      }
      if ((paramAudioInputStream instanceof AudioInputStream)) {
        frameLength = paramAudioInputStream.getFrameLength();
      } else {
        frameLength = -1L;
      }
      framePos = 0L;
    }
    
    public int read()
      throws IOException
    {
      if (frameSize == 1)
      {
        if (conversionType == 1)
        {
          int i = super.read();
          if (i < 0) {
            return i;
          }
          int j = (byte)(i & 0xF);
          j = j >= 0 ? (byte)(0x80 | j) : (byte)(0x7F & j);
          i = j & 0xF;
          return i;
        }
        throw new IOException("cannot read a single byte if frame size > 1");
      }
      throw new IOException("cannot read a single byte if frame size > 1");
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
      if ((frameLength != -1L) && (paramInt2 / frameSize > frameLength - framePos)) {
        paramInt2 = (int)(frameLength - framePos) * frameSize;
      }
      int i = super.read(paramArrayOfByte, paramInt1, paramInt2);
      if (i < 0) {
        return i;
      }
      switch (conversionType)
      {
      case 1: 
        switchSigned8bit(paramArrayOfByte, paramInt1, paramInt2, i);
        break;
      case 2: 
        switchEndian(paramArrayOfByte, paramInt1, paramInt2, i);
        break;
      case 3: 
        switchSignedLE(paramArrayOfByte, paramInt1, paramInt2, i);
        break;
      case 4: 
        switchSignedBE(paramArrayOfByte, paramInt1, paramInt2, i);
        break;
      case 5: 
      case 6: 
        switchSignedLE(paramArrayOfByte, paramInt1, paramInt2, i);
        switchEndian(paramArrayOfByte, paramInt1, paramInt2, i);
        break;
      case 7: 
      case 8: 
        switchSignedBE(paramArrayOfByte, paramInt1, paramInt2, i);
        switchEndian(paramArrayOfByte, paramInt1, paramInt2, i);
        break;
      }
      return i;
    }
    
    private void switchSigned8bit(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
    {
      for (int i = paramInt1; i < paramInt1 + paramInt3; i++) {
        paramArrayOfByte[i] = (paramArrayOfByte[i] >= 0 ? (byte)(0x80 | paramArrayOfByte[i]) : (byte)(0x7F & paramArrayOfByte[i]));
      }
    }
    
    private void switchSignedBE(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
    {
      int i = paramInt1;
      while (i < paramInt1 + paramInt3)
      {
        paramArrayOfByte[i] = (paramArrayOfByte[i] >= 0 ? (byte)(0x80 | paramArrayOfByte[i]) : (byte)(0x7F & paramArrayOfByte[i]));
        i += sampleSizeInBytes;
      }
    }
    
    private void switchSignedLE(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
    {
      int i = paramInt1 + sampleSizeInBytes - 1;
      while (i < paramInt1 + paramInt3)
      {
        paramArrayOfByte[i] = (paramArrayOfByte[i] >= 0 ? (byte)(0x80 | paramArrayOfByte[i]) : (byte)(0x7F & paramArrayOfByte[i]));
        i += sampleSizeInBytes;
      }
    }
    
    private void switchEndian(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
    {
      if (sampleSizeInBytes == 2)
      {
        int i = paramInt1;
        while (i < paramInt1 + paramInt3)
        {
          int j = paramArrayOfByte[i];
          paramArrayOfByte[i] = paramArrayOfByte[(i + 1)];
          paramArrayOfByte[(i + 1)] = j;
          i += sampleSizeInBytes;
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\PCMtoPCMCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */