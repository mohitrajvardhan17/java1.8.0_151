package com.sun.media.sound;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.spi.AudioFileWriter;

public final class WaveFloatFileWriter
  extends AudioFileWriter
{
  public WaveFloatFileWriter() {}
  
  public AudioFileFormat.Type[] getAudioFileTypes()
  {
    return new AudioFileFormat.Type[] { AudioFileFormat.Type.WAVE };
  }
  
  public AudioFileFormat.Type[] getAudioFileTypes(AudioInputStream paramAudioInputStream)
  {
    if (!paramAudioInputStream.getFormat().getEncoding().equals(AudioFormat.Encoding.PCM_FLOAT)) {
      return new AudioFileFormat.Type[0];
    }
    return new AudioFileFormat.Type[] { AudioFileFormat.Type.WAVE };
  }
  
  private void checkFormat(AudioFileFormat.Type paramType, AudioInputStream paramAudioInputStream)
  {
    if (!AudioFileFormat.Type.WAVE.equals(paramType)) {
      throw new IllegalArgumentException("File type " + paramType + " not supported.");
    }
    if (!paramAudioInputStream.getFormat().getEncoding().equals(AudioFormat.Encoding.PCM_FLOAT)) {
      throw new IllegalArgumentException("File format " + paramAudioInputStream.getFormat() + " not supported.");
    }
  }
  
  public void write(AudioInputStream paramAudioInputStream, RIFFWriter paramRIFFWriter)
    throws IOException
  {
    RIFFWriter localRIFFWriter1 = paramRIFFWriter.writeChunk("fmt ");
    AudioFormat localAudioFormat = paramAudioInputStream.getFormat();
    localRIFFWriter1.writeUnsignedShort(3);
    localRIFFWriter1.writeUnsignedShort(localAudioFormat.getChannels());
    localRIFFWriter1.writeUnsignedInt((int)localAudioFormat.getSampleRate());
    localRIFFWriter1.writeUnsignedInt((int)localAudioFormat.getFrameRate() * localAudioFormat.getFrameSize());
    localRIFFWriter1.writeUnsignedShort(localAudioFormat.getFrameSize());
    localRIFFWriter1.writeUnsignedShort(localAudioFormat.getSampleSizeInBits());
    localRIFFWriter1.close();
    RIFFWriter localRIFFWriter2 = paramRIFFWriter.writeChunk("data");
    byte[] arrayOfByte = new byte['Ѐ'];
    int i;
    while ((i = paramAudioInputStream.read(arrayOfByte, 0, arrayOfByte.length)) != -1) {
      localRIFFWriter2.write(arrayOfByte, 0, i);
    }
    localRIFFWriter2.close();
  }
  
  private AudioInputStream toLittleEndian(AudioInputStream paramAudioInputStream)
  {
    AudioFormat localAudioFormat1 = paramAudioInputStream.getFormat();
    AudioFormat localAudioFormat2 = new AudioFormat(localAudioFormat1.getEncoding(), localAudioFormat1.getSampleRate(), localAudioFormat1.getSampleSizeInBits(), localAudioFormat1.getChannels(), localAudioFormat1.getFrameSize(), localAudioFormat1.getFrameRate(), false);
    return AudioSystem.getAudioInputStream(localAudioFormat2, paramAudioInputStream);
  }
  
  public int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, OutputStream paramOutputStream)
    throws IOException
  {
    checkFormat(paramType, paramAudioInputStream);
    if (paramAudioInputStream.getFormat().isBigEndian()) {
      paramAudioInputStream = toLittleEndian(paramAudioInputStream);
    }
    RIFFWriter localRIFFWriter = new RIFFWriter(new NoCloseOutputStream(paramOutputStream), "WAVE");
    write(paramAudioInputStream, localRIFFWriter);
    int i = (int)localRIFFWriter.getFilePointer();
    localRIFFWriter.close();
    return i;
  }
  
  public int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, File paramFile)
    throws IOException
  {
    checkFormat(paramType, paramAudioInputStream);
    if (paramAudioInputStream.getFormat().isBigEndian()) {
      paramAudioInputStream = toLittleEndian(paramAudioInputStream);
    }
    RIFFWriter localRIFFWriter = new RIFFWriter(paramFile, "WAVE");
    write(paramAudioInputStream, localRIFFWriter);
    int i = (int)localRIFFWriter.getFilePointer();
    localRIFFWriter.close();
    return i;
  }
  
  private static class NoCloseOutputStream
    extends OutputStream
  {
    final OutputStream out;
    
    NoCloseOutputStream(OutputStream paramOutputStream)
    {
      out = paramOutputStream;
    }
    
    public void write(int paramInt)
      throws IOException
    {
      out.write(paramInt);
    }
    
    public void flush()
      throws IOException
    {
      out.flush();
    }
    
    public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      out.write(paramArrayOfByte, paramInt1, paramInt2);
    }
    
    public void write(byte[] paramArrayOfByte)
      throws IOException
    {
      out.write(paramArrayOfByte);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\WaveFloatFileWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */