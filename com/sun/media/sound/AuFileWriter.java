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

public final class AuFileWriter
  extends SunFileWriter
{
  public static final int UNKNOWN_SIZE = -1;
  
  public AuFileWriter()
  {
    super(new AudioFileFormat.Type[] { AudioFileFormat.Type.AU });
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
    AuFileFormat localAuFileFormat = (AuFileFormat)getAudioFileFormat(paramType, paramAudioInputStream);
    int i = writeAuFile(paramAudioInputStream, localAuFileFormat, paramOutputStream);
    return i;
  }
  
  public int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, File paramFile)
    throws IOException
  {
    AuFileFormat localAuFileFormat = (AuFileFormat)getAudioFileFormat(paramType, paramAudioInputStream);
    FileOutputStream localFileOutputStream = new FileOutputStream(paramFile);
    BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(localFileOutputStream, 4096);
    int i = writeAuFile(paramAudioInputStream, localAuFileFormat, localBufferedOutputStream);
    localBufferedOutputStream.close();
    if (localAuFileFormat.getByteLength() == -1)
    {
      RandomAccessFile localRandomAccessFile = new RandomAccessFile(paramFile, "rw");
      if (localRandomAccessFile.length() <= 2147483647L)
      {
        localRandomAccessFile.skipBytes(8);
        localRandomAccessFile.writeInt(i - 24);
      }
      localRandomAccessFile.close();
    }
    return i;
  }
  
  private AudioFileFormat getAudioFileFormat(AudioFileFormat.Type paramType, AudioInputStream paramAudioInputStream)
  {
    AudioFormat localAudioFormat1 = null;
    AuFileFormat localAuFileFormat = null;
    Object localObject = AudioFormat.Encoding.PCM_SIGNED;
    AudioFormat localAudioFormat2 = paramAudioInputStream.getFormat();
    AudioFormat.Encoding localEncoding = localAudioFormat2.getEncoding();
    if (!types[0].equals(paramType)) {
      throw new IllegalArgumentException("File type " + paramType + " not supported.");
    }
    int i;
    if ((AudioFormat.Encoding.ALAW.equals(localEncoding)) || (AudioFormat.Encoding.ULAW.equals(localEncoding)))
    {
      localObject = localEncoding;
      i = localAudioFormat2.getSampleSizeInBits();
    }
    else if (localAudioFormat2.getSampleSizeInBits() == 8)
    {
      localObject = AudioFormat.Encoding.PCM_SIGNED;
      i = 8;
    }
    else
    {
      localObject = AudioFormat.Encoding.PCM_SIGNED;
      i = localAudioFormat2.getSampleSizeInBits();
    }
    localAudioFormat1 = new AudioFormat((AudioFormat.Encoding)localObject, localAudioFormat2.getSampleRate(), i, localAudioFormat2.getChannels(), localAudioFormat2.getFrameSize(), localAudioFormat2.getFrameRate(), true);
    int j;
    if (paramAudioInputStream.getFrameLength() != -1L) {
      j = (int)paramAudioInputStream.getFrameLength() * localAudioFormat2.getFrameSize() + 24;
    } else {
      j = -1;
    }
    localAuFileFormat = new AuFileFormat(AudioFileFormat.Type.AU, j, localAudioFormat1, (int)paramAudioInputStream.getFrameLength());
    return localAuFileFormat;
  }
  
  private InputStream getFileStream(AuFileFormat paramAuFileFormat, InputStream paramInputStream)
    throws IOException
  {
    AudioFormat localAudioFormat1 = paramAuFileFormat.getFormat();
    int i = 779316836;
    int j = 24;
    long l1 = paramAuFileFormat.getFrameLength();
    long l2 = l1 == -1L ? -1L : l1 * localAudioFormat1.getFrameSize();
    if (l2 > 2147483647L) {
      l2 = -1L;
    }
    int k = paramAuFileFormat.getAuType();
    int m = (int)localAudioFormat1.getSampleRate();
    int n = localAudioFormat1.getChannels();
    boolean bool = true;
    byte[] arrayOfByte = null;
    ByteArrayInputStream localByteArrayInputStream = null;
    ByteArrayOutputStream localByteArrayOutputStream = null;
    DataOutputStream localDataOutputStream = null;
    SequenceInputStream localSequenceInputStream = null;
    AudioFormat localAudioFormat2 = null;
    AudioFormat.Encoding localEncoding = null;
    Object localObject = paramInputStream;
    localObject = paramInputStream;
    if ((paramInputStream instanceof AudioInputStream))
    {
      localAudioFormat2 = ((AudioInputStream)paramInputStream).getFormat();
      localEncoding = localAudioFormat2.getEncoding();
      if ((AudioFormat.Encoding.PCM_UNSIGNED.equals(localEncoding)) || ((AudioFormat.Encoding.PCM_SIGNED.equals(localEncoding)) && (bool != localAudioFormat2.isBigEndian()))) {
        localObject = AudioSystem.getAudioInputStream(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, localAudioFormat2.getSampleRate(), localAudioFormat2.getSampleSizeInBits(), localAudioFormat2.getChannels(), localAudioFormat2.getFrameSize(), localAudioFormat2.getFrameRate(), bool), (AudioInputStream)paramInputStream);
      }
    }
    localByteArrayOutputStream = new ByteArrayOutputStream();
    localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);
    if (bool)
    {
      localDataOutputStream.writeInt(779316836);
      localDataOutputStream.writeInt(j);
      localDataOutputStream.writeInt((int)l2);
      localDataOutputStream.writeInt(k);
      localDataOutputStream.writeInt(m);
      localDataOutputStream.writeInt(n);
    }
    else
    {
      localDataOutputStream.writeInt(1684960046);
      localDataOutputStream.writeInt(big2little(j));
      localDataOutputStream.writeInt(big2little((int)l2));
      localDataOutputStream.writeInt(big2little(k));
      localDataOutputStream.writeInt(big2little(m));
      localDataOutputStream.writeInt(big2little(n));
    }
    localDataOutputStream.close();
    arrayOfByte = localByteArrayOutputStream.toByteArray();
    localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
    localSequenceInputStream = new SequenceInputStream(localByteArrayInputStream, new SunFileWriter.NoCloseInputStream(this, (InputStream)localObject));
    return localSequenceInputStream;
  }
  
  private int writeAuFile(InputStream paramInputStream, AuFileFormat paramAuFileFormat, OutputStream paramOutputStream)
    throws IOException
  {
    int i = 0;
    int j = 0;
    InputStream localInputStream = getFileStream(paramAuFileFormat, paramInputStream);
    byte[] arrayOfByte = new byte['က'];
    int k = paramAuFileFormat.getByteLength();
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
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\AuFileWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */