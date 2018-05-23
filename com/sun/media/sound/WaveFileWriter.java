package com.sun.media.sound;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.SequenceInputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public final class WaveFileWriter
  extends SunFileWriter
{
  static final int RIFF_MAGIC = 1380533830;
  static final int WAVE_MAGIC = 1463899717;
  static final int FMT_MAGIC = 1718449184;
  static final int DATA_MAGIC = 1684108385;
  static final int WAVE_FORMAT_UNKNOWN = 0;
  static final int WAVE_FORMAT_PCM = 1;
  static final int WAVE_FORMAT_ADPCM = 2;
  static final int WAVE_FORMAT_ALAW = 6;
  static final int WAVE_FORMAT_MULAW = 7;
  static final int WAVE_FORMAT_OKI_ADPCM = 16;
  static final int WAVE_FORMAT_DIGISTD = 21;
  static final int WAVE_FORMAT_DIGIFIX = 22;
  static final int WAVE_IBM_FORMAT_MULAW = 257;
  static final int WAVE_IBM_FORMAT_ALAW = 258;
  static final int WAVE_IBM_FORMAT_ADPCM = 259;
  static final int WAVE_FORMAT_DVI_ADPCM = 17;
  static final int WAVE_FORMAT_SX7383 = 7175;
  
  public WaveFileWriter()
  {
    super(new AudioFileFormat.Type[] { AudioFileFormat.Type.WAVE });
  }
  
  public AudioFileFormat.Type[] getAudioFileTypes(AudioInputStream paramAudioInputStream)
  {
    AudioFileFormat.Type[] arrayOfType = new AudioFileFormat.Type[types.length];
    System.arraycopy(types, 0, arrayOfType, 0, types.length);
    AudioFormat localAudioFormat = paramAudioInputStream.getFormat();
    AudioFormat.Encoding localEncoding = localAudioFormat.getEncoding();
    if ((AudioFormat.Encoding.ALAW.equals(localEncoding)) || (AudioFormat.Encoding.ULAW.equals(localEncoding)) || (AudioFormat.Encoding.PCM_SIGNED.equals(localEncoding)) || (AudioFormat.Encoding.PCM_UNSIGNED.equals(localEncoding))) {
      return arrayOfType;
    }
    return new AudioFileFormat.Type[0];
  }
  
  public int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, OutputStream paramOutputStream)
    throws IOException
  {
    WaveFileFormat localWaveFileFormat = (WaveFileFormat)getAudioFileFormat(paramType, paramAudioInputStream);
    if (paramAudioInputStream.getFrameLength() == -1L) {
      throw new IOException("stream length not specified");
    }
    int i = writeWaveFile(paramAudioInputStream, localWaveFileFormat, paramOutputStream);
    return i;
  }
  
  public int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, File paramFile)
    throws IOException
  {
    WaveFileFormat localWaveFileFormat = (WaveFileFormat)getAudioFileFormat(paramType, paramAudioInputStream);
    FileOutputStream localFileOutputStream = new FileOutputStream(paramFile);
    BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(localFileOutputStream, 4096);
    int i = writeWaveFile(paramAudioInputStream, localWaveFileFormat, localBufferedOutputStream);
    localBufferedOutputStream.close();
    if (localWaveFileFormat.getByteLength() == -1)
    {
      int j = i - localWaveFileFormat.getHeaderSize();
      int k = j + localWaveFileFormat.getHeaderSize() - 8;
      RandomAccessFile localRandomAccessFile = new RandomAccessFile(paramFile, "rw");
      localRandomAccessFile.skipBytes(4);
      localRandomAccessFile.writeInt(big2little(k));
      localRandomAccessFile.skipBytes(12 + WaveFileFormat.getFmtChunkSize(localWaveFileFormat.getWaveType()) + 4);
      localRandomAccessFile.writeInt(big2little(j));
      localRandomAccessFile.close();
    }
    return i;
  }
  
  private AudioFileFormat getAudioFileFormat(AudioFileFormat.Type paramType, AudioInputStream paramAudioInputStream)
  {
    AudioFormat localAudioFormat1 = null;
    WaveFileFormat localWaveFileFormat = null;
    Object localObject = AudioFormat.Encoding.PCM_SIGNED;
    AudioFormat localAudioFormat2 = paramAudioInputStream.getFormat();
    AudioFormat.Encoding localEncoding = localAudioFormat2.getEncoding();
    if (!types[0].equals(paramType)) {
      throw new IllegalArgumentException("File type " + paramType + " not supported.");
    }
    int k = 1;
    int i;
    if ((AudioFormat.Encoding.ALAW.equals(localEncoding)) || (AudioFormat.Encoding.ULAW.equals(localEncoding)))
    {
      localObject = localEncoding;
      i = localAudioFormat2.getSampleSizeInBits();
      if (localEncoding.equals(AudioFormat.Encoding.ALAW)) {
        k = 6;
      } else {
        k = 7;
      }
    }
    else if (localAudioFormat2.getSampleSizeInBits() == 8)
    {
      localObject = AudioFormat.Encoding.PCM_UNSIGNED;
      i = 8;
    }
    else
    {
      localObject = AudioFormat.Encoding.PCM_SIGNED;
      i = localAudioFormat2.getSampleSizeInBits();
    }
    localAudioFormat1 = new AudioFormat((AudioFormat.Encoding)localObject, localAudioFormat2.getSampleRate(), i, localAudioFormat2.getChannels(), localAudioFormat2.getFrameSize(), localAudioFormat2.getFrameRate(), false);
    int j;
    if (paramAudioInputStream.getFrameLength() != -1L) {
      j = (int)paramAudioInputStream.getFrameLength() * localAudioFormat2.getFrameSize() + WaveFileFormat.getHeaderSize(k);
    } else {
      j = -1;
    }
    localWaveFileFormat = new WaveFileFormat(AudioFileFormat.Type.WAVE, j, localAudioFormat1, (int)paramAudioInputStream.getFrameLength());
    return localWaveFileFormat;
  }
  
  private int writeWaveFile(InputStream paramInputStream, WaveFileFormat paramWaveFileFormat, OutputStream paramOutputStream)
    throws IOException
  {
    int i = 0;
    int j = 0;
    InputStream localInputStream = getFileStream(paramWaveFileFormat, paramInputStream);
    byte[] arrayOfByte = new byte['က'];
    int k = paramWaveFileFormat.getByteLength();
    while ((i = localInputStream.read(arrayOfByte)) >= 0) {
      if (k > 0)
      {
        if (i < k)
        {
          paramOutputStream.write(arrayOfByte, 0, i);
          j += i;
          k -= i;
        }
        else
        {
          paramOutputStream.write(arrayOfByte, 0, k);
          j += k;
          k = 0;
          break;
        }
      }
      else
      {
        paramOutputStream.write(arrayOfByte, 0, i);
        j += i;
      }
    }
    return j;
  }
  
  private InputStream getFileStream(WaveFileFormat paramWaveFileFormat, InputStream paramInputStream)
    throws IOException
  {
    AudioFormat localAudioFormat1 = paramWaveFileFormat.getFormat();
    int i = paramWaveFileFormat.getHeaderSize();
    int j = 1380533830;
    int k = 1463899717;
    int m = 1718449184;
    int n = WaveFileFormat.getFmtChunkSize(paramWaveFileFormat.getWaveType());
    short s1 = (short)paramWaveFileFormat.getWaveType();
    int i1 = (short)localAudioFormat1.getChannels();
    int i2 = (short)localAudioFormat1.getSampleSizeInBits();
    int i3 = (int)localAudioFormat1.getSampleRate();
    int i4 = localAudioFormat1.getFrameSize();
    int i5 = (int)localAudioFormat1.getFrameRate();
    int i6 = i1 * i2 * i3 / 8;
    short s2 = (short)(i2 / 8 * i1);
    int i7 = 1684108385;
    int i8 = paramWaveFileFormat.getFrameLength() * i4;
    int i9 = paramWaveFileFormat.getByteLength();
    int i10 = i8 + i - 8;
    byte[] arrayOfByte = null;
    ByteArrayInputStream localByteArrayInputStream = null;
    ByteArrayOutputStream localByteArrayOutputStream = null;
    DataOutputStream localDataOutputStream = null;
    SequenceInputStream localSequenceInputStream = null;
    AudioFormat localAudioFormat2 = null;
    AudioFormat.Encoding localEncoding = null;
    Object localObject = paramInputStream;
    if ((paramInputStream instanceof AudioInputStream))
    {
      localAudioFormat2 = ((AudioInputStream)paramInputStream).getFormat();
      localEncoding = localAudioFormat2.getEncoding();
      if ((AudioFormat.Encoding.PCM_SIGNED.equals(localEncoding)) && (i2 == 8))
      {
        s1 = 1;
        localObject = AudioSystem.getAudioInputStream(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, localAudioFormat2.getSampleRate(), localAudioFormat2.getSampleSizeInBits(), localAudioFormat2.getChannels(), localAudioFormat2.getFrameSize(), localAudioFormat2.getFrameRate(), false), (AudioInputStream)paramInputStream);
      }
      if (((AudioFormat.Encoding.PCM_SIGNED.equals(localEncoding)) && (localAudioFormat2.isBigEndian())) || ((AudioFormat.Encoding.PCM_UNSIGNED.equals(localEncoding)) && (!localAudioFormat2.isBigEndian())) || ((AudioFormat.Encoding.PCM_UNSIGNED.equals(localEncoding)) && (localAudioFormat2.isBigEndian()) && (i2 != 8)))
      {
        s1 = 1;
        localObject = AudioSystem.getAudioInputStream(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, localAudioFormat2.getSampleRate(), localAudioFormat2.getSampleSizeInBits(), localAudioFormat2.getChannels(), localAudioFormat2.getFrameSize(), localAudioFormat2.getFrameRate(), false), (AudioInputStream)paramInputStream);
      }
    }
    localByteArrayOutputStream = new ByteArrayOutputStream();
    localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);
    localDataOutputStream.writeInt(j);
    localDataOutputStream.writeInt(big2little(i10));
    localDataOutputStream.writeInt(k);
    localDataOutputStream.writeInt(m);
    localDataOutputStream.writeInt(big2little(n));
    localDataOutputStream.writeShort(big2littleShort(s1));
    localDataOutputStream.writeShort(big2littleShort(i1));
    localDataOutputStream.writeInt(big2little(i3));
    localDataOutputStream.writeInt(big2little(i6));
    localDataOutputStream.writeShort(big2littleShort(s2));
    localDataOutputStream.writeShort(big2littleShort(i2));
    if (s1 != 1) {
      localDataOutputStream.writeShort(0);
    }
    localDataOutputStream.writeInt(i7);
    localDataOutputStream.writeInt(big2little(i8));
    localDataOutputStream.close();
    arrayOfByte = localByteArrayOutputStream.toByteArray();
    localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
    localSequenceInputStream = new SequenceInputStream(localByteArrayInputStream, new SunFileWriter.NoCloseInputStream(this, (InputStream)localObject));
    return localSequenceInputStream;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\WaveFileWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */