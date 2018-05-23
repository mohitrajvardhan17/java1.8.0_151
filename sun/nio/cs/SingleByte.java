package sun.nio.cs;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

public class SingleByte
{
  public SingleByte() {}
  
  private static final CoderResult withResult(CoderResult paramCoderResult, Buffer paramBuffer1, int paramInt1, Buffer paramBuffer2, int paramInt2)
  {
    paramBuffer1.position(paramInt1 - paramBuffer1.arrayOffset());
    paramBuffer2.position(paramInt2 - paramBuffer2.arrayOffset());
    return paramCoderResult;
  }
  
  public static void initC2B(char[] paramArrayOfChar1, char[] paramArrayOfChar2, char[] paramArrayOfChar3, char[] paramArrayOfChar4)
  {
    for (int i = 0; i < paramArrayOfChar4.length; i++) {
      paramArrayOfChar4[i] = 65533;
    }
    for (i = 0; i < paramArrayOfChar3.length; i++) {
      paramArrayOfChar3[i] = 65533;
    }
    i = 0;
    int k;
    int m;
    for (int j = 0; j < paramArrayOfChar1.length; j++)
    {
      k = paramArrayOfChar1[j];
      if (k != 65533)
      {
        m = k >> 8;
        if (paramArrayOfChar4[m] == 65533)
        {
          paramArrayOfChar4[m] = ((char)i);
          i += 256;
        }
        m = paramArrayOfChar4[m] + (k & 0xFF);
        paramArrayOfChar3[m] = ((char)(j >= 128 ? j - 128 : j + 128));
      }
    }
    if (paramArrayOfChar2 != null)
    {
      j = 0;
      while (j < paramArrayOfChar2.length)
      {
        k = paramArrayOfChar2[(j++)];
        m = paramArrayOfChar2[(j++)];
        int n = m >> 8;
        if (paramArrayOfChar4[n] == 65533)
        {
          paramArrayOfChar4[n] = ((char)i);
          i += 256;
        }
        n = paramArrayOfChar4[n] + (m & 0xFF);
        paramArrayOfChar3[n] = k;
      }
    }
  }
  
  public static final class Decoder
    extends CharsetDecoder
    implements ArrayDecoder
  {
    private final char[] b2c;
    private char repl = 65533;
    
    public Decoder(Charset paramCharset, char[] paramArrayOfChar)
    {
      super(1.0F, 1.0F);
      b2c = paramArrayOfChar;
    }
    
    private CoderResult decodeArrayLoop(ByteBuffer paramByteBuffer, CharBuffer paramCharBuffer)
    {
      byte[] arrayOfByte = paramByteBuffer.array();
      int i = paramByteBuffer.arrayOffset() + paramByteBuffer.position();
      int j = paramByteBuffer.arrayOffset() + paramByteBuffer.limit();
      char[] arrayOfChar = paramCharBuffer.array();
      int k = paramCharBuffer.arrayOffset() + paramCharBuffer.position();
      int m = paramCharBuffer.arrayOffset() + paramCharBuffer.limit();
      CoderResult localCoderResult = CoderResult.UNDERFLOW;
      if (m - k < j - i)
      {
        j = i + (m - k);
        localCoderResult = CoderResult.OVERFLOW;
      }
      while (i < j)
      {
        int n = decode(arrayOfByte[i]);
        if (n == 65533) {
          return SingleByte.withResult(CoderResult.unmappableForLength(1), paramByteBuffer, i, paramCharBuffer, k);
        }
        arrayOfChar[(k++)] = n;
        i++;
      }
      return SingleByte.withResult(localCoderResult, paramByteBuffer, i, paramCharBuffer, k);
    }
    
    private CoderResult decodeBufferLoop(ByteBuffer paramByteBuffer, CharBuffer paramCharBuffer)
    {
      int i = paramByteBuffer.position();
      try
      {
        while (paramByteBuffer.hasRemaining())
        {
          int j = decode(paramByteBuffer.get());
          CoderResult localCoderResult2;
          if (j == 65533)
          {
            localCoderResult2 = CoderResult.unmappableForLength(1);
            return localCoderResult2;
          }
          if (!paramCharBuffer.hasRemaining())
          {
            localCoderResult2 = CoderResult.OVERFLOW;
            return localCoderResult2;
          }
          paramCharBuffer.put(j);
          i++;
        }
        CoderResult localCoderResult1 = CoderResult.UNDERFLOW;
        return localCoderResult1;
      }
      finally
      {
        paramByteBuffer.position(i);
      }
    }
    
    protected CoderResult decodeLoop(ByteBuffer paramByteBuffer, CharBuffer paramCharBuffer)
    {
      if ((paramByteBuffer.hasArray()) && (paramCharBuffer.hasArray())) {
        return decodeArrayLoop(paramByteBuffer, paramCharBuffer);
      }
      return decodeBufferLoop(paramByteBuffer, paramCharBuffer);
    }
    
    public final char decode(int paramInt)
    {
      return b2c[(paramInt + 128)];
    }
    
    protected void implReplaceWith(String paramString)
    {
      repl = paramString.charAt(0);
    }
    
    public int decode(byte[] paramArrayOfByte, int paramInt1, int paramInt2, char[] paramArrayOfChar)
    {
      if (paramInt2 > paramArrayOfChar.length) {
        paramInt2 = paramArrayOfChar.length;
      }
      for (int i = 0; i < paramInt2; i++)
      {
        paramArrayOfChar[i] = decode(paramArrayOfByte[(paramInt1++)]);
        if (paramArrayOfChar[i] == 65533) {
          paramArrayOfChar[i] = repl;
        }
      }
      return i;
    }
  }
  
  public static final class Encoder
    extends CharsetEncoder
    implements ArrayEncoder
  {
    private Surrogate.Parser sgp;
    private final char[] c2b;
    private final char[] c2bIndex;
    private byte repl = 63;
    
    public Encoder(Charset paramCharset, char[] paramArrayOfChar1, char[] paramArrayOfChar2)
    {
      super(1.0F, 1.0F);
      c2b = paramArrayOfChar1;
      c2bIndex = paramArrayOfChar2;
    }
    
    public boolean canEncode(char paramChar)
    {
      return encode(paramChar) != 65533;
    }
    
    public boolean isLegalReplacement(byte[] paramArrayOfByte)
    {
      return ((paramArrayOfByte.length == 1) && (paramArrayOfByte[0] == 63)) || (super.isLegalReplacement(paramArrayOfByte));
    }
    
    private CoderResult encodeArrayLoop(CharBuffer paramCharBuffer, ByteBuffer paramByteBuffer)
    {
      char[] arrayOfChar = paramCharBuffer.array();
      int i = paramCharBuffer.arrayOffset() + paramCharBuffer.position();
      int j = paramCharBuffer.arrayOffset() + paramCharBuffer.limit();
      byte[] arrayOfByte = paramByteBuffer.array();
      int k = paramByteBuffer.arrayOffset() + paramByteBuffer.position();
      int m = paramByteBuffer.arrayOffset() + paramByteBuffer.limit();
      CoderResult localCoderResult = CoderResult.UNDERFLOW;
      if (m - k < j - i)
      {
        j = i + (m - k);
        localCoderResult = CoderResult.OVERFLOW;
      }
      while (i < j)
      {
        char c = arrayOfChar[i];
        int n = encode(c);
        if (n == 65533)
        {
          if (Character.isSurrogate(c))
          {
            if (sgp == null) {
              sgp = new Surrogate.Parser();
            }
            if (sgp.parse(c, arrayOfChar, i, j) < 0) {
              return SingleByte.withResult(sgp.error(), paramCharBuffer, i, paramByteBuffer, k);
            }
            return SingleByte.withResult(sgp.unmappableResult(), paramCharBuffer, i, paramByteBuffer, k);
          }
          return SingleByte.withResult(CoderResult.unmappableForLength(1), paramCharBuffer, i, paramByteBuffer, k);
        }
        arrayOfByte[(k++)] = ((byte)n);
        i++;
      }
      return SingleByte.withResult(localCoderResult, paramCharBuffer, i, paramByteBuffer, k);
    }
    
    private CoderResult encodeBufferLoop(CharBuffer paramCharBuffer, ByteBuffer paramByteBuffer)
    {
      int i = paramCharBuffer.position();
      try
      {
        while (paramCharBuffer.hasRemaining())
        {
          char c = paramCharBuffer.get();
          int j = encode(c);
          CoderResult localCoderResult2;
          if (j == 65533)
          {
            if (Character.isSurrogate(c))
            {
              if (sgp == null) {
                sgp = new Surrogate.Parser();
              }
              if (sgp.parse(c, paramCharBuffer) < 0)
              {
                localCoderResult2 = sgp.error();
                return localCoderResult2;
              }
              localCoderResult2 = sgp.unmappableResult();
              return localCoderResult2;
            }
            localCoderResult2 = CoderResult.unmappableForLength(1);
            return localCoderResult2;
          }
          if (!paramByteBuffer.hasRemaining())
          {
            localCoderResult2 = CoderResult.OVERFLOW;
            return localCoderResult2;
          }
          paramByteBuffer.put((byte)j);
          i++;
        }
        CoderResult localCoderResult1 = CoderResult.UNDERFLOW;
        return localCoderResult1;
      }
      finally
      {
        paramCharBuffer.position(i);
      }
    }
    
    protected CoderResult encodeLoop(CharBuffer paramCharBuffer, ByteBuffer paramByteBuffer)
    {
      if ((paramCharBuffer.hasArray()) && (paramByteBuffer.hasArray())) {
        return encodeArrayLoop(paramCharBuffer, paramByteBuffer);
      }
      return encodeBufferLoop(paramCharBuffer, paramByteBuffer);
    }
    
    public final int encode(char paramChar)
    {
      int i = c2bIndex[(paramChar >> '\b')];
      if (i == 65533) {
        return 65533;
      }
      return c2b[(i + (paramChar & 0xFF))];
    }
    
    protected void implReplaceWith(byte[] paramArrayOfByte)
    {
      repl = paramArrayOfByte[0];
    }
    
    public int encode(char[] paramArrayOfChar, int paramInt1, int paramInt2, byte[] paramArrayOfByte)
    {
      int i = 0;
      int j = paramInt1 + Math.min(paramInt2, paramArrayOfByte.length);
      while (paramInt1 < j)
      {
        char c = paramArrayOfChar[(paramInt1++)];
        int k = encode(c);
        if (k != 65533)
        {
          paramArrayOfByte[(i++)] = ((byte)k);
        }
        else
        {
          if ((Character.isHighSurrogate(c)) && (paramInt1 < j) && (Character.isLowSurrogate(paramArrayOfChar[paramInt1])))
          {
            if (paramInt2 > paramArrayOfByte.length)
            {
              j++;
              paramInt2--;
            }
            paramInt1++;
          }
          paramArrayOfByte[(i++)] = repl;
        }
      }
      return i;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\cs\SingleByte.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */