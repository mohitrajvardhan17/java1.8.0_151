package com.sun.media.sound;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.spi.FormatConversionProvider;

public final class AudioFloatFormatConverter
  extends FormatConversionProvider
{
  private final AudioFormat.Encoding[] formats = { AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED, AudioFormat.Encoding.PCM_FLOAT };
  
  public AudioFloatFormatConverter() {}
  
  public AudioInputStream getAudioInputStream(AudioFormat.Encoding paramEncoding, AudioInputStream paramAudioInputStream)
  {
    if (paramAudioInputStream.getFormat().getEncoding().equals(paramEncoding)) {
      return paramAudioInputStream;
    }
    AudioFormat localAudioFormat1 = paramAudioInputStream.getFormat();
    int i = localAudioFormat1.getChannels();
    AudioFormat.Encoding localEncoding = paramEncoding;
    float f = localAudioFormat1.getSampleRate();
    int j = localAudioFormat1.getSampleSizeInBits();
    boolean bool = localAudioFormat1.isBigEndian();
    if (paramEncoding.equals(AudioFormat.Encoding.PCM_FLOAT)) {
      j = 32;
    }
    AudioFormat localAudioFormat2 = new AudioFormat(localEncoding, f, j, i, i * j / 8, f, bool);
    return getAudioInputStream(localAudioFormat2, paramAudioInputStream);
  }
  
  public AudioInputStream getAudioInputStream(AudioFormat paramAudioFormat, AudioInputStream paramAudioInputStream)
  {
    if (!isConversionSupported(paramAudioFormat, paramAudioInputStream.getFormat())) {
      throw new IllegalArgumentException("Unsupported conversion: " + paramAudioInputStream.getFormat().toString() + " to " + paramAudioFormat.toString());
    }
    return getAudioInputStream(paramAudioFormat, AudioFloatInputStream.getInputStream(paramAudioInputStream));
  }
  
  public AudioInputStream getAudioInputStream(AudioFormat paramAudioFormat, AudioFloatInputStream paramAudioFloatInputStream)
  {
    if (!isConversionSupported(paramAudioFormat, paramAudioFloatInputStream.getFormat())) {
      throw new IllegalArgumentException("Unsupported conversion: " + paramAudioFloatInputStream.getFormat().toString() + " to " + paramAudioFormat.toString());
    }
    if (paramAudioFormat.getChannels() != paramAudioFloatInputStream.getFormat().getChannels()) {
      paramAudioFloatInputStream = new AudioFloatInputStreamChannelMixer(paramAudioFloatInputStream, paramAudioFormat.getChannels());
    }
    if (Math.abs(paramAudioFormat.getSampleRate() - paramAudioFloatInputStream.getFormat().getSampleRate()) > 1.0E-6D) {
      paramAudioFloatInputStream = new AudioFloatInputStreamResampler(paramAudioFloatInputStream, paramAudioFormat);
    }
    return new AudioInputStream(new AudioFloatFormatConverterInputStream(paramAudioFormat, paramAudioFloatInputStream), paramAudioFormat, paramAudioFloatInputStream.getFrameLength());
  }
  
  public AudioFormat.Encoding[] getSourceEncodings()
  {
    return new AudioFormat.Encoding[] { AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED, AudioFormat.Encoding.PCM_FLOAT };
  }
  
  public AudioFormat.Encoding[] getTargetEncodings()
  {
    return new AudioFormat.Encoding[] { AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED, AudioFormat.Encoding.PCM_FLOAT };
  }
  
  public AudioFormat.Encoding[] getTargetEncodings(AudioFormat paramAudioFormat)
  {
    if (AudioFloatConverter.getConverter(paramAudioFormat) == null) {
      return new AudioFormat.Encoding[0];
    }
    return new AudioFormat.Encoding[] { AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED, AudioFormat.Encoding.PCM_FLOAT };
  }
  
  public AudioFormat[] getTargetFormats(AudioFormat.Encoding paramEncoding, AudioFormat paramAudioFormat)
  {
    if (AudioFloatConverter.getConverter(paramAudioFormat) == null) {
      return new AudioFormat[0];
    }
    int i = paramAudioFormat.getChannels();
    ArrayList localArrayList = new ArrayList();
    if (paramEncoding.equals(AudioFormat.Encoding.PCM_SIGNED)) {
      localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0F, 8, i, i, -1.0F, false));
    }
    if (paramEncoding.equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
      localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, -1.0F, 8, i, i, -1.0F, false));
    }
    for (int j = 16; j < 32; j += 8)
    {
      if (paramEncoding.equals(AudioFormat.Encoding.PCM_SIGNED))
      {
        localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0F, j, i, i * j / 8, -1.0F, false));
        localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0F, j, i, i * j / 8, -1.0F, true));
      }
      if (paramEncoding.equals(AudioFormat.Encoding.PCM_UNSIGNED))
      {
        localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, -1.0F, j, i, i * j / 8, -1.0F, true));
        localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, -1.0F, j, i, i * j / 8, -1.0F, false));
      }
    }
    if (paramEncoding.equals(AudioFormat.Encoding.PCM_FLOAT))
    {
      localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0F, 32, i, i * 4, -1.0F, false));
      localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0F, 32, i, i * 4, -1.0F, true));
      localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0F, 64, i, i * 8, -1.0F, false));
      localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0F, 64, i, i * 8, -1.0F, true));
    }
    return (AudioFormat[])localArrayList.toArray(new AudioFormat[localArrayList.size()]);
  }
  
  public boolean isConversionSupported(AudioFormat paramAudioFormat1, AudioFormat paramAudioFormat2)
  {
    if (AudioFloatConverter.getConverter(paramAudioFormat2) == null) {
      return false;
    }
    if (AudioFloatConverter.getConverter(paramAudioFormat1) == null) {
      return false;
    }
    if (paramAudioFormat2.getChannels() <= 0) {
      return false;
    }
    return paramAudioFormat1.getChannels() > 0;
  }
  
  public boolean isConversionSupported(AudioFormat.Encoding paramEncoding, AudioFormat paramAudioFormat)
  {
    if (AudioFloatConverter.getConverter(paramAudioFormat) == null) {
      return false;
    }
    for (int i = 0; i < formats.length; i++) {
      if (paramEncoding.equals(formats[i])) {
        return true;
      }
    }
    return false;
  }
  
  private static class AudioFloatFormatConverterInputStream
    extends InputStream
  {
    private final AudioFloatConverter converter;
    private final AudioFloatInputStream stream;
    private float[] readfloatbuffer;
    private final int fsize;
    
    AudioFloatFormatConverterInputStream(AudioFormat paramAudioFormat, AudioFloatInputStream paramAudioFloatInputStream)
    {
      stream = paramAudioFloatInputStream;
      converter = AudioFloatConverter.getConverter(paramAudioFormat);
      fsize = ((paramAudioFormat.getSampleSizeInBits() + 7) / 8);
    }
    
    public int read()
      throws IOException
    {
      byte[] arrayOfByte = new byte[1];
      int i = read(arrayOfByte);
      if (i < 0) {
        return i;
      }
      return arrayOfByte[0] & 0xFF;
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      int i = paramInt2 / fsize;
      if ((readfloatbuffer == null) || (readfloatbuffer.length < i)) {
        readfloatbuffer = new float[i];
      }
      int j = stream.read(readfloatbuffer, 0, i);
      if (j < 0) {
        return j;
      }
      converter.toByteArray(readfloatbuffer, 0, j, paramArrayOfByte, paramInt1);
      return j * fsize;
    }
    
    public int available()
      throws IOException
    {
      int i = stream.available();
      if (i < 0) {
        return i;
      }
      return i * fsize;
    }
    
    public void close()
      throws IOException
    {
      stream.close();
    }
    
    public synchronized void mark(int paramInt)
    {
      stream.mark(paramInt * fsize);
    }
    
    public boolean markSupported()
    {
      return stream.markSupported();
    }
    
    public synchronized void reset()
      throws IOException
    {
      stream.reset();
    }
    
    public long skip(long paramLong)
      throws IOException
    {
      long l = stream.skip(paramLong / fsize);
      if (l < 0L) {
        return l;
      }
      return l * fsize;
    }
  }
  
  private static class AudioFloatInputStreamChannelMixer
    extends AudioFloatInputStream
  {
    private final int targetChannels;
    private final int sourceChannels;
    private final AudioFloatInputStream ais;
    private final AudioFormat targetFormat;
    private float[] conversion_buffer;
    
    AudioFloatInputStreamChannelMixer(AudioFloatInputStream paramAudioFloatInputStream, int paramInt)
    {
      sourceChannels = paramAudioFloatInputStream.getFormat().getChannels();
      targetChannels = paramInt;
      ais = paramAudioFloatInputStream;
      AudioFormat localAudioFormat = paramAudioFloatInputStream.getFormat();
      targetFormat = new AudioFormat(localAudioFormat.getEncoding(), localAudioFormat.getSampleRate(), localAudioFormat.getSampleSizeInBits(), paramInt, localAudioFormat.getFrameSize() / sourceChannels * paramInt, localAudioFormat.getFrameRate(), localAudioFormat.isBigEndian());
    }
    
    public int available()
      throws IOException
    {
      return ais.available() / sourceChannels * targetChannels;
    }
    
    public void close()
      throws IOException
    {
      ais.close();
    }
    
    public AudioFormat getFormat()
    {
      return targetFormat;
    }
    
    public long getFrameLength()
    {
      return ais.getFrameLength();
    }
    
    public void mark(int paramInt)
    {
      ais.mark(paramInt / targetChannels * sourceChannels);
    }
    
    public boolean markSupported()
    {
      return ais.markSupported();
    }
    
    public int read(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
      throws IOException
    {
      int i = paramInt2 / targetChannels * sourceChannels;
      if ((conversion_buffer == null) || (conversion_buffer.length < i)) {
        conversion_buffer = new float[i];
      }
      int j = ais.read(conversion_buffer, 0, i);
      if (j < 0) {
        return j;
      }
      int k;
      int m;
      int i1;
      int i2;
      if (sourceChannels == 1)
      {
        k = targetChannels;
        for (m = 0; m < targetChannels; m++)
        {
          i1 = 0;
          i2 = paramInt1 + m;
          while (i1 < i)
          {
            paramArrayOfFloat[i2] = conversion_buffer[i1];
            i1++;
            i2 += k;
          }
        }
      }
      else if (targetChannels == 1)
      {
        k = sourceChannels;
        m = 0;
        for (i1 = paramInt1; m < i; i1++)
        {
          paramArrayOfFloat[i1] = conversion_buffer[m];
          m += k;
        }
        for (m = 1; m < sourceChannels; m++)
        {
          i1 = m;
          for (i2 = paramInt1; i1 < i; i2++)
          {
            paramArrayOfFloat[i2] += conversion_buffer[i1];
            i1 += k;
          }
        }
        float f = 1.0F / sourceChannels;
        i1 = 0;
        for (i2 = paramInt1; i1 < i; i2++)
        {
          paramArrayOfFloat[i2] *= f;
          i1 += k;
        }
      }
      else
      {
        k = Math.min(sourceChannels, targetChannels);
        int n = paramInt1 + paramInt2;
        i1 = targetChannels;
        i2 = sourceChannels;
        int i4;
        for (int i3 = 0; i3 < k; i3++)
        {
          i4 = paramInt1 + i3;
          int i5 = i3;
          while (i4 < n)
          {
            paramArrayOfFloat[i4] = conversion_buffer[i5];
            i4 += i1;
            i5 += i2;
          }
        }
        for (i3 = k; i3 < targetChannels; i3++)
        {
          i4 = paramInt1 + i3;
          while (i4 < n)
          {
            paramArrayOfFloat[i4] = 0.0F;
            i4 += i1;
          }
        }
      }
      return j / sourceChannels * targetChannels;
    }
    
    public void reset()
      throws IOException
    {
      ais.reset();
    }
    
    public long skip(long paramLong)
      throws IOException
    {
      long l = ais.skip(paramLong / targetChannels * sourceChannels);
      if (l < 0L) {
        return l;
      }
      return l / sourceChannels * targetChannels;
    }
  }
  
  private static class AudioFloatInputStreamResampler
    extends AudioFloatInputStream
  {
    private final AudioFloatInputStream ais;
    private final AudioFormat targetFormat;
    private float[] skipbuffer;
    private SoftAbstractResampler resampler;
    private final float[] pitch = new float[1];
    private final float[] ibuffer2;
    private final float[][] ibuffer;
    private float ibuffer_index = 0.0F;
    private int ibuffer_len = 0;
    private final int nrofchannels;
    private float[][] cbuffer;
    private final int buffer_len = 512;
    private final int pad;
    private final int pad2;
    private final float[] ix = new float[1];
    private final int[] ox = new int[1];
    private float[][] mark_ibuffer = (float[][])null;
    private float mark_ibuffer_index = 0.0F;
    private int mark_ibuffer_len = 0;
    
    AudioFloatInputStreamResampler(AudioFloatInputStream paramAudioFloatInputStream, AudioFormat paramAudioFormat)
    {
      ais = paramAudioFloatInputStream;
      AudioFormat localAudioFormat = paramAudioFloatInputStream.getFormat();
      targetFormat = new AudioFormat(localAudioFormat.getEncoding(), paramAudioFormat.getSampleRate(), localAudioFormat.getSampleSizeInBits(), localAudioFormat.getChannels(), localAudioFormat.getFrameSize(), paramAudioFormat.getSampleRate(), localAudioFormat.isBigEndian());
      nrofchannels = targetFormat.getChannels();
      Object localObject = paramAudioFormat.getProperty("interpolation");
      if ((localObject != null) && ((localObject instanceof String)))
      {
        String str = (String)localObject;
        if (str.equalsIgnoreCase("point")) {
          resampler = new SoftPointResampler();
        }
        if (str.equalsIgnoreCase("linear")) {
          resampler = new SoftLinearResampler2();
        }
        if (str.equalsIgnoreCase("linear1")) {
          resampler = new SoftLinearResampler();
        }
        if (str.equalsIgnoreCase("linear2")) {
          resampler = new SoftLinearResampler2();
        }
        if (str.equalsIgnoreCase("cubic")) {
          resampler = new SoftCubicResampler();
        }
        if (str.equalsIgnoreCase("lanczos")) {
          resampler = new SoftLanczosResampler();
        }
        if (str.equalsIgnoreCase("sinc")) {
          resampler = new SoftSincResampler();
        }
      }
      if (resampler == null) {
        resampler = new SoftLinearResampler2();
      }
      pitch[0] = (localAudioFormat.getSampleRate() / paramAudioFormat.getSampleRate());
      pad = resampler.getPadding();
      pad2 = (pad * 2);
      ibuffer = new float[nrofchannels][512 + pad2];
      ibuffer2 = new float[nrofchannels * 512];
      ibuffer_index = (512 + pad);
      ibuffer_len = 512;
    }
    
    public int available()
      throws IOException
    {
      return 0;
    }
    
    public void close()
      throws IOException
    {
      ais.close();
    }
    
    public AudioFormat getFormat()
    {
      return targetFormat;
    }
    
    public long getFrameLength()
    {
      return -1L;
    }
    
    public void mark(int paramInt)
    {
      ais.mark((int)(paramInt * pitch[0]));
      mark_ibuffer_index = ibuffer_index;
      mark_ibuffer_len = ibuffer_len;
      if (mark_ibuffer == null) {
        mark_ibuffer = new float[ibuffer.length][ibuffer[0].length];
      }
      for (int i = 0; i < ibuffer.length; i++)
      {
        float[] arrayOfFloat1 = ibuffer[i];
        float[] arrayOfFloat2 = mark_ibuffer[i];
        for (int j = 0; j < arrayOfFloat2.length; j++) {
          arrayOfFloat2[j] = arrayOfFloat1[j];
        }
      }
    }
    
    public boolean markSupported()
    {
      return ais.markSupported();
    }
    
    private void readNextBuffer()
      throws IOException
    {
      if (ibuffer_len == -1) {
        return;
      }
      int m;
      int n;
      for (int i = 0; i < nrofchannels; i++)
      {
        float[] arrayOfFloat1 = ibuffer[i];
        int k = ibuffer_len + pad2;
        m = ibuffer_len;
        for (n = 0; m < k; n++)
        {
          arrayOfFloat1[n] = arrayOfFloat1[m];
          m++;
        }
      }
      ibuffer_index -= ibuffer_len;
      ibuffer_len = ais.read(ibuffer2);
      if (ibuffer_len >= 0)
      {
        while (ibuffer_len < ibuffer2.length)
        {
          i = ais.read(ibuffer2, ibuffer_len, ibuffer2.length - ibuffer_len);
          if (i == -1) {
            break;
          }
          ibuffer_len += i;
        }
        Arrays.fill(ibuffer2, ibuffer_len, ibuffer2.length, 0.0F);
        ibuffer_len /= nrofchannels;
      }
      else
      {
        Arrays.fill(ibuffer2, 0, ibuffer2.length, 0.0F);
      }
      i = ibuffer2.length;
      for (int j = 0; j < nrofchannels; j++)
      {
        float[] arrayOfFloat2 = ibuffer[j];
        m = j;
        for (n = pad2; m < i; n++)
        {
          arrayOfFloat2[n] = ibuffer2[m];
          m += nrofchannels;
        }
      }
    }
    
    public int read(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
      throws IOException
    {
      if ((cbuffer == null) || (cbuffer[0].length < paramInt2 / nrofchannels)) {
        cbuffer = new float[nrofchannels][paramInt2 / nrofchannels];
      }
      if (ibuffer_len == -1) {
        return -1;
      }
      if (paramInt2 < 0) {
        return 0;
      }
      int i = paramInt1 + paramInt2;
      int j = paramInt2 / nrofchannels;
      int k = 0;
      int m = ibuffer_len;
      int i1;
      float[] arrayOfFloat;
      while (j > 0)
      {
        if (ibuffer_len >= 0)
        {
          if (ibuffer_index >= ibuffer_len + pad) {
            readNextBuffer();
          }
          m = ibuffer_len + pad;
        }
        if (ibuffer_len < 0)
        {
          m = pad2;
          if (ibuffer_index >= m) {
            break;
          }
        }
        if (ibuffer_index < 0.0F) {
          break;
        }
        n = k;
        for (i1 = 0; i1 < nrofchannels; i1++)
        {
          ix[0] = ibuffer_index;
          ox[0] = k;
          arrayOfFloat = ibuffer[i1];
          resampler.interpolate(arrayOfFloat, ix, m, pitch, 0.0F, cbuffer[i1], ox, paramInt2 / nrofchannels);
        }
        ibuffer_index = ix[0];
        k = ox[0];
        j -= k - n;
      }
      for (int n = 0; n < nrofchannels; n++)
      {
        i1 = 0;
        arrayOfFloat = cbuffer[n];
        int i2 = n + paramInt1;
        while (i2 < i)
        {
          paramArrayOfFloat[i2] = arrayOfFloat[(i1++)];
          i2 += nrofchannels;
        }
      }
      return paramInt2 - j * nrofchannels;
    }
    
    public void reset()
      throws IOException
    {
      ais.reset();
      if (mark_ibuffer == null) {
        return;
      }
      ibuffer_index = mark_ibuffer_index;
      ibuffer_len = mark_ibuffer_len;
      for (int i = 0; i < ibuffer.length; i++)
      {
        float[] arrayOfFloat1 = mark_ibuffer[i];
        float[] arrayOfFloat2 = ibuffer[i];
        for (int j = 0; j < arrayOfFloat2.length; j++) {
          arrayOfFloat2[j] = arrayOfFloat1[j];
        }
      }
    }
    
    public long skip(long paramLong)
      throws IOException
    {
      if (paramLong < 0L) {
        return 0L;
      }
      if (skipbuffer == null) {
        skipbuffer = new float[1024 * targetFormat.getFrameSize()];
      }
      float[] arrayOfFloat = skipbuffer;
      int i;
      for (long l = paramLong; l > 0L; l -= i)
      {
        i = read(arrayOfFloat, 0, (int)Math.min(l, skipbuffer.length));
        if (i < 0)
        {
          if (l != paramLong) {
            break;
          }
          return i;
        }
      }
      return paramLong - l;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\AudioFloatFormatConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */