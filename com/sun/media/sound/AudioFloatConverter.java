package com.sun.media.sound;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;

public abstract class AudioFloatConverter
{
  private AudioFormat format;
  
  public AudioFloatConverter() {}
  
  public static AudioFloatConverter getConverter(AudioFormat paramAudioFormat)
  {
    Object localObject = null;
    if (paramAudioFormat.getFrameSize() == 0) {
      return null;
    }
    if (paramAudioFormat.getFrameSize() != (paramAudioFormat.getSampleSizeInBits() + 7) / 8 * paramAudioFormat.getChannels()) {
      return null;
    }
    if (paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED))
    {
      if (paramAudioFormat.isBigEndian())
      {
        if (paramAudioFormat.getSampleSizeInBits() <= 8) {
          localObject = new AudioFloatConversion8S(null);
        } else if ((paramAudioFormat.getSampleSizeInBits() > 8) && (paramAudioFormat.getSampleSizeInBits() <= 16)) {
          localObject = new AudioFloatConversion16SB(null);
        } else if ((paramAudioFormat.getSampleSizeInBits() > 16) && (paramAudioFormat.getSampleSizeInBits() <= 24)) {
          localObject = new AudioFloatConversion24SB(null);
        } else if ((paramAudioFormat.getSampleSizeInBits() > 24) && (paramAudioFormat.getSampleSizeInBits() <= 32)) {
          localObject = new AudioFloatConversion32SB(null);
        } else if (paramAudioFormat.getSampleSizeInBits() > 32) {
          localObject = new AudioFloatConversion32xSB((paramAudioFormat.getSampleSizeInBits() + 7) / 8 - 4);
        }
      }
      else if (paramAudioFormat.getSampleSizeInBits() <= 8) {
        localObject = new AudioFloatConversion8S(null);
      } else if ((paramAudioFormat.getSampleSizeInBits() > 8) && (paramAudioFormat.getSampleSizeInBits() <= 16)) {
        localObject = new AudioFloatConversion16SL(null);
      } else if ((paramAudioFormat.getSampleSizeInBits() > 16) && (paramAudioFormat.getSampleSizeInBits() <= 24)) {
        localObject = new AudioFloatConversion24SL(null);
      } else if ((paramAudioFormat.getSampleSizeInBits() > 24) && (paramAudioFormat.getSampleSizeInBits() <= 32)) {
        localObject = new AudioFloatConversion32SL(null);
      } else if (paramAudioFormat.getSampleSizeInBits() > 32) {
        localObject = new AudioFloatConversion32xSL((paramAudioFormat.getSampleSizeInBits() + 7) / 8 - 4);
      }
    }
    else if (paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED))
    {
      if (paramAudioFormat.isBigEndian())
      {
        if (paramAudioFormat.getSampleSizeInBits() <= 8) {
          localObject = new AudioFloatConversion8U(null);
        } else if ((paramAudioFormat.getSampleSizeInBits() > 8) && (paramAudioFormat.getSampleSizeInBits() <= 16)) {
          localObject = new AudioFloatConversion16UB(null);
        } else if ((paramAudioFormat.getSampleSizeInBits() > 16) && (paramAudioFormat.getSampleSizeInBits() <= 24)) {
          localObject = new AudioFloatConversion24UB(null);
        } else if ((paramAudioFormat.getSampleSizeInBits() > 24) && (paramAudioFormat.getSampleSizeInBits() <= 32)) {
          localObject = new AudioFloatConversion32UB(null);
        } else if (paramAudioFormat.getSampleSizeInBits() > 32) {
          localObject = new AudioFloatConversion32xUB((paramAudioFormat.getSampleSizeInBits() + 7) / 8 - 4);
        }
      }
      else if (paramAudioFormat.getSampleSizeInBits() <= 8) {
        localObject = new AudioFloatConversion8U(null);
      } else if ((paramAudioFormat.getSampleSizeInBits() > 8) && (paramAudioFormat.getSampleSizeInBits() <= 16)) {
        localObject = new AudioFloatConversion16UL(null);
      } else if ((paramAudioFormat.getSampleSizeInBits() > 16) && (paramAudioFormat.getSampleSizeInBits() <= 24)) {
        localObject = new AudioFloatConversion24UL(null);
      } else if ((paramAudioFormat.getSampleSizeInBits() > 24) && (paramAudioFormat.getSampleSizeInBits() <= 32)) {
        localObject = new AudioFloatConversion32UL(null);
      } else if (paramAudioFormat.getSampleSizeInBits() > 32) {
        localObject = new AudioFloatConversion32xUL((paramAudioFormat.getSampleSizeInBits() + 7) / 8 - 4);
      }
    }
    else if (paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_FLOAT)) {
      if (paramAudioFormat.getSampleSizeInBits() == 32)
      {
        if (paramAudioFormat.isBigEndian()) {
          localObject = new AudioFloatConversion32B(null);
        } else {
          localObject = new AudioFloatConversion32L(null);
        }
      }
      else if (paramAudioFormat.getSampleSizeInBits() == 64) {
        if (paramAudioFormat.isBigEndian()) {
          localObject = new AudioFloatConversion64B(null);
        } else {
          localObject = new AudioFloatConversion64L(null);
        }
      }
    }
    if (((paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) || (paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED))) && (paramAudioFormat.getSampleSizeInBits() % 8 != 0)) {
      localObject = new AudioFloatLSBFilter((AudioFloatConverter)localObject, paramAudioFormat);
    }
    if (localObject != null) {
      format = paramAudioFormat;
    }
    return (AudioFloatConverter)localObject;
  }
  
  public final AudioFormat getFormat()
  {
    return format;
  }
  
  public abstract float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3);
  
  public final float[] toFloatArray(byte[] paramArrayOfByte, float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    return toFloatArray(paramArrayOfByte, 0, paramArrayOfFloat, paramInt1, paramInt2);
  }
  
  public final float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2)
  {
    return toFloatArray(paramArrayOfByte, paramInt1, paramArrayOfFloat, 0, paramInt2);
  }
  
  public final float[] toFloatArray(byte[] paramArrayOfByte, float[] paramArrayOfFloat, int paramInt)
  {
    return toFloatArray(paramArrayOfByte, 0, paramArrayOfFloat, 0, paramInt);
  }
  
  public final float[] toFloatArray(byte[] paramArrayOfByte, float[] paramArrayOfFloat)
  {
    return toFloatArray(paramArrayOfByte, 0, paramArrayOfFloat, 0, paramArrayOfFloat.length);
  }
  
  public abstract byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3);
  
  public final byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, byte[] paramArrayOfByte, int paramInt2)
  {
    return toByteArray(paramArrayOfFloat, 0, paramInt1, paramArrayOfByte, paramInt2);
  }
  
  public final byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte)
  {
    return toByteArray(paramArrayOfFloat, paramInt1, paramInt2, paramArrayOfByte, 0);
  }
  
  public final byte[] toByteArray(float[] paramArrayOfFloat, int paramInt, byte[] paramArrayOfByte)
  {
    return toByteArray(paramArrayOfFloat, 0, paramInt, paramArrayOfByte, 0);
  }
  
  public final byte[] toByteArray(float[] paramArrayOfFloat, byte[] paramArrayOfByte)
  {
    return toByteArray(paramArrayOfFloat, 0, paramArrayOfFloat.length, paramArrayOfByte, 0);
  }
  
  private static class AudioFloatConversion16SB
    extends AudioFloatConverter
  {
    private AudioFloatConversion16SB() {}
    
    public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt2;
      for (int k = 0; k < paramInt3; k++) {
        paramArrayOfFloat[(j++)] = ((short)(paramArrayOfByte[(i++)] << 8 | paramArrayOfByte[(i++)] & 0xFF) * 3.051851E-5F);
      }
      return paramArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt3;
      for (int k = 0; k < paramInt2; k++)
      {
        int m = (int)(paramArrayOfFloat[(i++)] * 32767.0D);
        paramArrayOfByte[(j++)] = ((byte)(m >>> 8));
        paramArrayOfByte[(j++)] = ((byte)m);
      }
      return paramArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion16SL
    extends AudioFloatConverter
  {
    private AudioFloatConversion16SL() {}
    
    public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt2 + paramInt3;
      for (int k = paramInt2; k < j; k++) {
        paramArrayOfFloat[k] = ((short)(paramArrayOfByte[(i++)] & 0xFF | paramArrayOfByte[(i++)] << 8) * 3.051851E-5F);
      }
      return paramArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
    {
      int i = paramInt3;
      int j = paramInt1 + paramInt2;
      for (int k = paramInt1; k < j; k++)
      {
        int m = (int)(paramArrayOfFloat[k] * 32767.0D);
        paramArrayOfByte[(i++)] = ((byte)m);
        paramArrayOfByte[(i++)] = ((byte)(m >>> 8));
      }
      return paramArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion16UB
    extends AudioFloatConverter
  {
    private AudioFloatConversion16UB() {}
    
    public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt2;
      for (int k = 0; k < paramInt3; k++)
      {
        int m = (paramArrayOfByte[(i++)] & 0xFF) << 8 | paramArrayOfByte[(i++)] & 0xFF;
        paramArrayOfFloat[(j++)] = ((m - 32767) * 3.051851E-5F);
      }
      return paramArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt3;
      for (int k = 0; k < paramInt2; k++)
      {
        int m = 32767 + (int)(paramArrayOfFloat[(i++)] * 32767.0D);
        paramArrayOfByte[(j++)] = ((byte)(m >>> 8));
        paramArrayOfByte[(j++)] = ((byte)m);
      }
      return paramArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion16UL
    extends AudioFloatConverter
  {
    private AudioFloatConversion16UL() {}
    
    public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt2;
      for (int k = 0; k < paramInt3; k++)
      {
        int m = paramArrayOfByte[(i++)] & 0xFF | (paramArrayOfByte[(i++)] & 0xFF) << 8;
        paramArrayOfFloat[(j++)] = ((m - 32767) * 3.051851E-5F);
      }
      return paramArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt3;
      for (int k = 0; k < paramInt2; k++)
      {
        int m = 32767 + (int)(paramArrayOfFloat[(i++)] * 32767.0D);
        paramArrayOfByte[(j++)] = ((byte)m);
        paramArrayOfByte[(j++)] = ((byte)(m >>> 8));
      }
      return paramArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion24SB
    extends AudioFloatConverter
  {
    private AudioFloatConversion24SB() {}
    
    public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt2;
      for (int k = 0; k < paramInt3; k++)
      {
        int m = (paramArrayOfByte[(i++)] & 0xFF) << 16 | (paramArrayOfByte[(i++)] & 0xFF) << 8 | paramArrayOfByte[(i++)] & 0xFF;
        if (m > 8388607) {
          m -= 16777216;
        }
        paramArrayOfFloat[(j++)] = (m * 1.192093E-7F);
      }
      return paramArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt3;
      for (int k = 0; k < paramInt2; k++)
      {
        int m = (int)(paramArrayOfFloat[(i++)] * 8388607.0F);
        if (m < 0) {
          m += 16777216;
        }
        paramArrayOfByte[(j++)] = ((byte)(m >>> 16));
        paramArrayOfByte[(j++)] = ((byte)(m >>> 8));
        paramArrayOfByte[(j++)] = ((byte)m);
      }
      return paramArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion24SL
    extends AudioFloatConverter
  {
    private AudioFloatConversion24SL() {}
    
    public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt2;
      for (int k = 0; k < paramInt3; k++)
      {
        int m = paramArrayOfByte[(i++)] & 0xFF | (paramArrayOfByte[(i++)] & 0xFF) << 8 | (paramArrayOfByte[(i++)] & 0xFF) << 16;
        if (m > 8388607) {
          m -= 16777216;
        }
        paramArrayOfFloat[(j++)] = (m * 1.192093E-7F);
      }
      return paramArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt3;
      for (int k = 0; k < paramInt2; k++)
      {
        int m = (int)(paramArrayOfFloat[(i++)] * 8388607.0F);
        if (m < 0) {
          m += 16777216;
        }
        paramArrayOfByte[(j++)] = ((byte)m);
        paramArrayOfByte[(j++)] = ((byte)(m >>> 8));
        paramArrayOfByte[(j++)] = ((byte)(m >>> 16));
      }
      return paramArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion24UB
    extends AudioFloatConverter
  {
    private AudioFloatConversion24UB() {}
    
    public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt2;
      for (int k = 0; k < paramInt3; k++)
      {
        int m = (paramArrayOfByte[(i++)] & 0xFF) << 16 | (paramArrayOfByte[(i++)] & 0xFF) << 8 | paramArrayOfByte[(i++)] & 0xFF;
        m -= 8388607;
        paramArrayOfFloat[(j++)] = (m * 1.192093E-7F);
      }
      return paramArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt3;
      for (int k = 0; k < paramInt2; k++)
      {
        int m = (int)(paramArrayOfFloat[(i++)] * 8388607.0F);
        m += 8388607;
        paramArrayOfByte[(j++)] = ((byte)(m >>> 16));
        paramArrayOfByte[(j++)] = ((byte)(m >>> 8));
        paramArrayOfByte[(j++)] = ((byte)m);
      }
      return paramArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion24UL
    extends AudioFloatConverter
  {
    private AudioFloatConversion24UL() {}
    
    public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt2;
      for (int k = 0; k < paramInt3; k++)
      {
        int m = paramArrayOfByte[(i++)] & 0xFF | (paramArrayOfByte[(i++)] & 0xFF) << 8 | (paramArrayOfByte[(i++)] & 0xFF) << 16;
        m -= 8388607;
        paramArrayOfFloat[(j++)] = (m * 1.192093E-7F);
      }
      return paramArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt3;
      for (int k = 0; k < paramInt2; k++)
      {
        int m = (int)(paramArrayOfFloat[(i++)] * 8388607.0F);
        m += 8388607;
        paramArrayOfByte[(j++)] = ((byte)m);
        paramArrayOfByte[(j++)] = ((byte)(m >>> 8));
        paramArrayOfByte[(j++)] = ((byte)(m >>> 16));
      }
      return paramArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion32B
    extends AudioFloatConverter
  {
    ByteBuffer bytebuffer = null;
    FloatBuffer floatbuffer = null;
    
    private AudioFloatConversion32B() {}
    
    public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
    {
      int i = paramInt3 * 4;
      if ((bytebuffer == null) || (bytebuffer.capacity() < i))
      {
        bytebuffer = ByteBuffer.allocate(i).order(ByteOrder.BIG_ENDIAN);
        floatbuffer = bytebuffer.asFloatBuffer();
      }
      bytebuffer.position(0);
      floatbuffer.position(0);
      bytebuffer.put(paramArrayOfByte, paramInt1, i);
      floatbuffer.get(paramArrayOfFloat, paramInt2, paramInt3);
      return paramArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
    {
      int i = paramInt2 * 4;
      if ((bytebuffer == null) || (bytebuffer.capacity() < i))
      {
        bytebuffer = ByteBuffer.allocate(i).order(ByteOrder.BIG_ENDIAN);
        floatbuffer = bytebuffer.asFloatBuffer();
      }
      floatbuffer.position(0);
      bytebuffer.position(0);
      floatbuffer.put(paramArrayOfFloat, paramInt1, paramInt2);
      bytebuffer.get(paramArrayOfByte, paramInt3, i);
      return paramArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion32L
    extends AudioFloatConverter
  {
    ByteBuffer bytebuffer = null;
    FloatBuffer floatbuffer = null;
    
    private AudioFloatConversion32L() {}
    
    public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
    {
      int i = paramInt3 * 4;
      if ((bytebuffer == null) || (bytebuffer.capacity() < i))
      {
        bytebuffer = ByteBuffer.allocate(i).order(ByteOrder.LITTLE_ENDIAN);
        floatbuffer = bytebuffer.asFloatBuffer();
      }
      bytebuffer.position(0);
      floatbuffer.position(0);
      bytebuffer.put(paramArrayOfByte, paramInt1, i);
      floatbuffer.get(paramArrayOfFloat, paramInt2, paramInt3);
      return paramArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
    {
      int i = paramInt2 * 4;
      if ((bytebuffer == null) || (bytebuffer.capacity() < i))
      {
        bytebuffer = ByteBuffer.allocate(i).order(ByteOrder.LITTLE_ENDIAN);
        floatbuffer = bytebuffer.asFloatBuffer();
      }
      floatbuffer.position(0);
      bytebuffer.position(0);
      floatbuffer.put(paramArrayOfFloat, paramInt1, paramInt2);
      bytebuffer.get(paramArrayOfByte, paramInt3, i);
      return paramArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion32SB
    extends AudioFloatConverter
  {
    private AudioFloatConversion32SB() {}
    
    public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt2;
      for (int k = 0; k < paramInt3; k++)
      {
        int m = (paramArrayOfByte[(i++)] & 0xFF) << 24 | (paramArrayOfByte[(i++)] & 0xFF) << 16 | (paramArrayOfByte[(i++)] & 0xFF) << 8 | paramArrayOfByte[(i++)] & 0xFF;
        paramArrayOfFloat[(j++)] = (m * 4.656613E-10F);
      }
      return paramArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt3;
      for (int k = 0; k < paramInt2; k++)
      {
        int m = (int)(paramArrayOfFloat[(i++)] * 2.14748365E9F);
        paramArrayOfByte[(j++)] = ((byte)(m >>> 24));
        paramArrayOfByte[(j++)] = ((byte)(m >>> 16));
        paramArrayOfByte[(j++)] = ((byte)(m >>> 8));
        paramArrayOfByte[(j++)] = ((byte)m);
      }
      return paramArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion32SL
    extends AudioFloatConverter
  {
    private AudioFloatConversion32SL() {}
    
    public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt2;
      for (int k = 0; k < paramInt3; k++)
      {
        int m = paramArrayOfByte[(i++)] & 0xFF | (paramArrayOfByte[(i++)] & 0xFF) << 8 | (paramArrayOfByte[(i++)] & 0xFF) << 16 | (paramArrayOfByte[(i++)] & 0xFF) << 24;
        paramArrayOfFloat[(j++)] = (m * 4.656613E-10F);
      }
      return paramArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt3;
      for (int k = 0; k < paramInt2; k++)
      {
        int m = (int)(paramArrayOfFloat[(i++)] * 2.14748365E9F);
        paramArrayOfByte[(j++)] = ((byte)m);
        paramArrayOfByte[(j++)] = ((byte)(m >>> 8));
        paramArrayOfByte[(j++)] = ((byte)(m >>> 16));
        paramArrayOfByte[(j++)] = ((byte)(m >>> 24));
      }
      return paramArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion32UB
    extends AudioFloatConverter
  {
    private AudioFloatConversion32UB() {}
    
    public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt2;
      for (int k = 0; k < paramInt3; k++)
      {
        int m = (paramArrayOfByte[(i++)] & 0xFF) << 24 | (paramArrayOfByte[(i++)] & 0xFF) << 16 | (paramArrayOfByte[(i++)] & 0xFF) << 8 | paramArrayOfByte[(i++)] & 0xFF;
        m -= Integer.MAX_VALUE;
        paramArrayOfFloat[(j++)] = (m * 4.656613E-10F);
      }
      return paramArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt3;
      for (int k = 0; k < paramInt2; k++)
      {
        int m = (int)(paramArrayOfFloat[(i++)] * 2.14748365E9F);
        m += Integer.MAX_VALUE;
        paramArrayOfByte[(j++)] = ((byte)(m >>> 24));
        paramArrayOfByte[(j++)] = ((byte)(m >>> 16));
        paramArrayOfByte[(j++)] = ((byte)(m >>> 8));
        paramArrayOfByte[(j++)] = ((byte)m);
      }
      return paramArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion32UL
    extends AudioFloatConverter
  {
    private AudioFloatConversion32UL() {}
    
    public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt2;
      for (int k = 0; k < paramInt3; k++)
      {
        int m = paramArrayOfByte[(i++)] & 0xFF | (paramArrayOfByte[(i++)] & 0xFF) << 8 | (paramArrayOfByte[(i++)] & 0xFF) << 16 | (paramArrayOfByte[(i++)] & 0xFF) << 24;
        m -= Integer.MAX_VALUE;
        paramArrayOfFloat[(j++)] = (m * 4.656613E-10F);
      }
      return paramArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt3;
      for (int k = 0; k < paramInt2; k++)
      {
        int m = (int)(paramArrayOfFloat[(i++)] * 2.14748365E9F);
        m += Integer.MAX_VALUE;
        paramArrayOfByte[(j++)] = ((byte)m);
        paramArrayOfByte[(j++)] = ((byte)(m >>> 8));
        paramArrayOfByte[(j++)] = ((byte)(m >>> 16));
        paramArrayOfByte[(j++)] = ((byte)(m >>> 24));
      }
      return paramArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion32xSB
    extends AudioFloatConverter
  {
    final int xbytes;
    
    AudioFloatConversion32xSB(int paramInt)
    {
      xbytes = paramInt;
    }
    
    public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt2;
      for (int k = 0; k < paramInt3; k++)
      {
        int m = (paramArrayOfByte[(i++)] & 0xFF) << 24 | (paramArrayOfByte[(i++)] & 0xFF) << 16 | (paramArrayOfByte[(i++)] & 0xFF) << 8 | paramArrayOfByte[(i++)] & 0xFF;
        i += xbytes;
        paramArrayOfFloat[(j++)] = (m * 4.656613E-10F);
      }
      return paramArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt3;
      for (int k = 0; k < paramInt2; k++)
      {
        int m = (int)(paramArrayOfFloat[(i++)] * 2.14748365E9F);
        paramArrayOfByte[(j++)] = ((byte)(m >>> 24));
        paramArrayOfByte[(j++)] = ((byte)(m >>> 16));
        paramArrayOfByte[(j++)] = ((byte)(m >>> 8));
        paramArrayOfByte[(j++)] = ((byte)m);
        for (int n = 0; n < xbytes; n++) {
          paramArrayOfByte[(j++)] = 0;
        }
      }
      return paramArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion32xSL
    extends AudioFloatConverter
  {
    final int xbytes;
    
    AudioFloatConversion32xSL(int paramInt)
    {
      xbytes = paramInt;
    }
    
    public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt2;
      for (int k = 0; k < paramInt3; k++)
      {
        i += xbytes;
        int m = paramArrayOfByte[(i++)] & 0xFF | (paramArrayOfByte[(i++)] & 0xFF) << 8 | (paramArrayOfByte[(i++)] & 0xFF) << 16 | (paramArrayOfByte[(i++)] & 0xFF) << 24;
        paramArrayOfFloat[(j++)] = (m * 4.656613E-10F);
      }
      return paramArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt3;
      for (int k = 0; k < paramInt2; k++)
      {
        int m = (int)(paramArrayOfFloat[(i++)] * 2.14748365E9F);
        for (int n = 0; n < xbytes; n++) {
          paramArrayOfByte[(j++)] = 0;
        }
        paramArrayOfByte[(j++)] = ((byte)m);
        paramArrayOfByte[(j++)] = ((byte)(m >>> 8));
        paramArrayOfByte[(j++)] = ((byte)(m >>> 16));
        paramArrayOfByte[(j++)] = ((byte)(m >>> 24));
      }
      return paramArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion32xUB
    extends AudioFloatConverter
  {
    final int xbytes;
    
    AudioFloatConversion32xUB(int paramInt)
    {
      xbytes = paramInt;
    }
    
    public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt2;
      for (int k = 0; k < paramInt3; k++)
      {
        int m = (paramArrayOfByte[(i++)] & 0xFF) << 24 | (paramArrayOfByte[(i++)] & 0xFF) << 16 | (paramArrayOfByte[(i++)] & 0xFF) << 8 | paramArrayOfByte[(i++)] & 0xFF;
        i += xbytes;
        m -= Integer.MAX_VALUE;
        paramArrayOfFloat[(j++)] = (m * 4.656613E-10F);
      }
      return paramArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt3;
      for (int k = 0; k < paramInt2; k++)
      {
        int m = (int)(paramArrayOfFloat[(i++)] * 2.147483647E9D);
        m += Integer.MAX_VALUE;
        paramArrayOfByte[(j++)] = ((byte)(m >>> 24));
        paramArrayOfByte[(j++)] = ((byte)(m >>> 16));
        paramArrayOfByte[(j++)] = ((byte)(m >>> 8));
        paramArrayOfByte[(j++)] = ((byte)m);
        for (int n = 0; n < xbytes; n++) {
          paramArrayOfByte[(j++)] = 0;
        }
      }
      return paramArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion32xUL
    extends AudioFloatConverter
  {
    final int xbytes;
    
    AudioFloatConversion32xUL(int paramInt)
    {
      xbytes = paramInt;
    }
    
    public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt2;
      for (int k = 0; k < paramInt3; k++)
      {
        i += xbytes;
        int m = paramArrayOfByte[(i++)] & 0xFF | (paramArrayOfByte[(i++)] & 0xFF) << 8 | (paramArrayOfByte[(i++)] & 0xFF) << 16 | (paramArrayOfByte[(i++)] & 0xFF) << 24;
        m -= Integer.MAX_VALUE;
        paramArrayOfFloat[(j++)] = (m * 4.656613E-10F);
      }
      return paramArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt3;
      for (int k = 0; k < paramInt2; k++)
      {
        int m = (int)(paramArrayOfFloat[(i++)] * 2.14748365E9F);
        m += Integer.MAX_VALUE;
        for (int n = 0; n < xbytes; n++) {
          paramArrayOfByte[(j++)] = 0;
        }
        paramArrayOfByte[(j++)] = ((byte)m);
        paramArrayOfByte[(j++)] = ((byte)(m >>> 8));
        paramArrayOfByte[(j++)] = ((byte)(m >>> 16));
        paramArrayOfByte[(j++)] = ((byte)(m >>> 24));
      }
      return paramArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion64B
    extends AudioFloatConverter
  {
    ByteBuffer bytebuffer = null;
    DoubleBuffer floatbuffer = null;
    double[] double_buff = null;
    
    private AudioFloatConversion64B() {}
    
    public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
    {
      int i = paramInt3 * 8;
      if ((bytebuffer == null) || (bytebuffer.capacity() < i))
      {
        bytebuffer = ByteBuffer.allocate(i).order(ByteOrder.BIG_ENDIAN);
        floatbuffer = bytebuffer.asDoubleBuffer();
      }
      bytebuffer.position(0);
      floatbuffer.position(0);
      bytebuffer.put(paramArrayOfByte, paramInt1, i);
      if ((double_buff == null) || (double_buff.length < paramInt3 + paramInt2)) {
        double_buff = new double[paramInt3 + paramInt2];
      }
      floatbuffer.get(double_buff, paramInt2, paramInt3);
      int j = paramInt2 + paramInt3;
      for (int k = paramInt2; k < j; k++) {
        paramArrayOfFloat[k] = ((float)double_buff[k]);
      }
      return paramArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
    {
      int i = paramInt2 * 8;
      if ((bytebuffer == null) || (bytebuffer.capacity() < i))
      {
        bytebuffer = ByteBuffer.allocate(i).order(ByteOrder.BIG_ENDIAN);
        floatbuffer = bytebuffer.asDoubleBuffer();
      }
      floatbuffer.position(0);
      bytebuffer.position(0);
      if ((double_buff == null) || (double_buff.length < paramInt1 + paramInt2)) {
        double_buff = new double[paramInt1 + paramInt2];
      }
      int j = paramInt1 + paramInt2;
      for (int k = paramInt1; k < j; k++) {
        double_buff[k] = paramArrayOfFloat[k];
      }
      floatbuffer.put(double_buff, paramInt1, paramInt2);
      bytebuffer.get(paramArrayOfByte, paramInt3, i);
      return paramArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion64L
    extends AudioFloatConverter
  {
    ByteBuffer bytebuffer = null;
    DoubleBuffer floatbuffer = null;
    double[] double_buff = null;
    
    private AudioFloatConversion64L() {}
    
    public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
    {
      int i = paramInt3 * 8;
      if ((bytebuffer == null) || (bytebuffer.capacity() < i))
      {
        bytebuffer = ByteBuffer.allocate(i).order(ByteOrder.LITTLE_ENDIAN);
        floatbuffer = bytebuffer.asDoubleBuffer();
      }
      bytebuffer.position(0);
      floatbuffer.position(0);
      bytebuffer.put(paramArrayOfByte, paramInt1, i);
      if ((double_buff == null) || (double_buff.length < paramInt3 + paramInt2)) {
        double_buff = new double[paramInt3 + paramInt2];
      }
      floatbuffer.get(double_buff, paramInt2, paramInt3);
      int j = paramInt2 + paramInt3;
      for (int k = paramInt2; k < j; k++) {
        paramArrayOfFloat[k] = ((float)double_buff[k]);
      }
      return paramArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
    {
      int i = paramInt2 * 8;
      if ((bytebuffer == null) || (bytebuffer.capacity() < i))
      {
        bytebuffer = ByteBuffer.allocate(i).order(ByteOrder.LITTLE_ENDIAN);
        floatbuffer = bytebuffer.asDoubleBuffer();
      }
      floatbuffer.position(0);
      bytebuffer.position(0);
      if ((double_buff == null) || (double_buff.length < paramInt1 + paramInt2)) {
        double_buff = new double[paramInt1 + paramInt2];
      }
      int j = paramInt1 + paramInt2;
      for (int k = paramInt1; k < j; k++) {
        double_buff[k] = paramArrayOfFloat[k];
      }
      floatbuffer.put(double_buff, paramInt1, paramInt2);
      bytebuffer.get(paramArrayOfByte, paramInt3, i);
      return paramArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion8S
    extends AudioFloatConverter
  {
    private AudioFloatConversion8S() {}
    
    public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt2;
      for (int k = 0; k < paramInt3; k++) {
        paramArrayOfFloat[(j++)] = (paramArrayOfByte[(i++)] * 0.007874016F);
      }
      return paramArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt3;
      for (int k = 0; k < paramInt2; k++) {
        paramArrayOfByte[(j++)] = ((byte)(int)(paramArrayOfFloat[(i++)] * 127.0F));
      }
      return paramArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion8U
    extends AudioFloatConverter
  {
    private AudioFloatConversion8U() {}
    
    public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt2;
      for (int k = 0; k < paramInt3; k++) {
        paramArrayOfFloat[(j++)] = (((paramArrayOfByte[(i++)] & 0xFF) - Byte.MAX_VALUE) * 0.007874016F);
      }
      return paramArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt3;
      for (int k = 0; k < paramInt2; k++) {
        paramArrayOfByte[(j++)] = ((byte)(int)(127.0F + paramArrayOfFloat[(i++)] * 127.0F));
      }
      return paramArrayOfByte;
    }
  }
  
  private static class AudioFloatLSBFilter
    extends AudioFloatConverter
  {
    private final AudioFloatConverter converter;
    private final int offset;
    private final int stepsize;
    private final byte mask;
    private byte[] mask_buffer;
    
    AudioFloatLSBFilter(AudioFloatConverter paramAudioFloatConverter, AudioFormat paramAudioFormat)
    {
      int i = paramAudioFormat.getSampleSizeInBits();
      boolean bool = paramAudioFormat.isBigEndian();
      converter = paramAudioFloatConverter;
      stepsize = ((i + 7) / 8);
      offset = (bool ? stepsize - 1 : 0);
      int j = i % 8;
      if (j == 0) {
        mask = 0;
      } else if (j == 1) {
        mask = Byte.MIN_VALUE;
      } else if (j == 2) {
        mask = -64;
      } else if (j == 3) {
        mask = -32;
      } else if (j == 4) {
        mask = -16;
      } else if (j == 5) {
        mask = -8;
      } else if (j == 6) {
        mask = -4;
      } else if (j == 7) {
        mask = -2;
      } else {
        mask = -1;
      }
    }
    
    public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
    {
      byte[] arrayOfByte = converter.toByteArray(paramArrayOfFloat, paramInt1, paramInt2, paramArrayOfByte, paramInt3);
      int i = paramInt2 * stepsize;
      int j = paramInt3 + offset;
      while (j < i)
      {
        paramArrayOfByte[j] = ((byte)(paramArrayOfByte[j] & mask));
        j += stepsize;
      }
      return arrayOfByte;
    }
    
    public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
    {
      if ((mask_buffer == null) || (mask_buffer.length < paramArrayOfByte.length)) {
        mask_buffer = new byte[paramArrayOfByte.length];
      }
      System.arraycopy(paramArrayOfByte, 0, mask_buffer, 0, paramArrayOfByte.length);
      int i = paramInt3 * stepsize;
      int j = paramInt1 + offset;
      while (j < i)
      {
        mask_buffer[j] = ((byte)(mask_buffer[j] & mask));
        j += stepsize;
      }
      float[] arrayOfFloat = converter.toFloatArray(mask_buffer, paramInt1, paramArrayOfFloat, paramInt2, paramInt3);
      return arrayOfFloat;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\AudioFloatConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */