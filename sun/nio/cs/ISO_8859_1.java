package sun.nio.cs;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

class ISO_8859_1
  extends Charset
  implements HistoricallyNamedCharset
{
  public ISO_8859_1()
  {
    super("ISO-8859-1", StandardCharsets.aliases_ISO_8859_1);
  }
  
  public String historicalName()
  {
    return "ISO8859_1";
  }
  
  public boolean contains(Charset paramCharset)
  {
    return ((paramCharset instanceof US_ASCII)) || ((paramCharset instanceof ISO_8859_1));
  }
  
  public CharsetDecoder newDecoder()
  {
    return new Decoder(this, null);
  }
  
  public CharsetEncoder newEncoder()
  {
    return new Encoder(this, null);
  }
  
  private static class Decoder
    extends CharsetDecoder
    implements ArrayDecoder
  {
    private Decoder(Charset paramCharset)
    {
      super(1.0F, 1.0F);
    }
    
    private CoderResult decodeArrayLoop(ByteBuffer paramByteBuffer, CharBuffer paramCharBuffer)
    {
      byte[] arrayOfByte = paramByteBuffer.array();
      int i = paramByteBuffer.arrayOffset() + paramByteBuffer.position();
      int j = paramByteBuffer.arrayOffset() + paramByteBuffer.limit();
      assert (i <= j);
      i = i <= j ? i : j;
      char[] arrayOfChar = paramCharBuffer.array();
      int k = paramCharBuffer.arrayOffset() + paramCharBuffer.position();
      int m = paramCharBuffer.arrayOffset() + paramCharBuffer.limit();
      assert (k <= m);
      k = k <= m ? k : m;
      try
      {
        while (i < j)
        {
          int n = arrayOfByte[i];
          if (k >= m)
          {
            CoderResult localCoderResult2 = CoderResult.OVERFLOW;
            return localCoderResult2;
          }
          arrayOfChar[(k++)] = ((char)(n & 0xFF));
          i++;
        }
        CoderResult localCoderResult1 = CoderResult.UNDERFLOW;
        return localCoderResult1;
      }
      finally
      {
        paramByteBuffer.position(i - paramByteBuffer.arrayOffset());
        paramCharBuffer.position(k - paramCharBuffer.arrayOffset());
      }
    }
    
    private CoderResult decodeBufferLoop(ByteBuffer paramByteBuffer, CharBuffer paramCharBuffer)
    {
      int i = paramByteBuffer.position();
      try
      {
        while (paramByteBuffer.hasRemaining())
        {
          int j = paramByteBuffer.get();
          if (!paramCharBuffer.hasRemaining())
          {
            CoderResult localCoderResult2 = CoderResult.OVERFLOW;
            return localCoderResult2;
          }
          paramCharBuffer.put((char)(j & 0xFF));
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
    
    public int decode(byte[] paramArrayOfByte, int paramInt1, int paramInt2, char[] paramArrayOfChar)
    {
      if (paramInt2 > paramArrayOfChar.length) {
        paramInt2 = paramArrayOfChar.length;
      }
      int i = 0;
      while (i < paramInt2) {
        paramArrayOfChar[(i++)] = ((char)(paramArrayOfByte[(paramInt1++)] & 0xFF));
      }
      return i;
    }
  }
  
  private static class Encoder
    extends CharsetEncoder
    implements ArrayEncoder
  {
    private final Surrogate.Parser sgp = new Surrogate.Parser();
    private byte repl = 63;
    
    private Encoder(Charset paramCharset)
    {
      super(1.0F, 1.0F);
    }
    
    public boolean canEncode(char paramChar)
    {
      return paramChar <= 'ÿ';
    }
    
    public boolean isLegalReplacement(byte[] paramArrayOfByte)
    {
      return true;
    }
    
    private static int encodeISOArray(char[] paramArrayOfChar, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
    {
      for (int i = 0; i < paramInt3; i++)
      {
        int j = paramArrayOfChar[(paramInt1++)];
        if (j > 255) {
          break;
        }
        paramArrayOfByte[(paramInt2++)] = ((byte)j);
      }
      return i;
    }
    
    private CoderResult encodeArrayLoop(CharBuffer paramCharBuffer, ByteBuffer paramByteBuffer)
    {
      char[] arrayOfChar = paramCharBuffer.array();
      int i = paramCharBuffer.arrayOffset();
      int j = i + paramCharBuffer.position();
      int k = i + paramCharBuffer.limit();
      assert (j <= k);
      j = j <= k ? j : k;
      byte[] arrayOfByte = paramByteBuffer.array();
      int m = paramByteBuffer.arrayOffset();
      int n = m + paramByteBuffer.position();
      int i1 = m + paramByteBuffer.limit();
      assert (n <= i1);
      n = n <= i1 ? n : i1;
      int i2 = i1 - n;
      int i3 = k - j;
      int i4 = i2 < i3 ? i2 : i3;
      try
      {
        int i5 = encodeISOArray(arrayOfChar, j, arrayOfByte, n, i4);
        j += i5;
        n += i5;
        if (i5 != i4)
        {
          if (sgp.parse(arrayOfChar[j], arrayOfChar, j, k) < 0)
          {
            localCoderResult = sgp.error();
            return localCoderResult;
          }
          localCoderResult = sgp.unmappableResult();
          return localCoderResult;
        }
        if (i4 < i3)
        {
          localCoderResult = CoderResult.OVERFLOW;
          return localCoderResult;
        }
        CoderResult localCoderResult = CoderResult.UNDERFLOW;
        return localCoderResult;
      }
      finally
      {
        paramCharBuffer.position(j - i);
        paramByteBuffer.position(n - m);
      }
    }
    
    private CoderResult encodeBufferLoop(CharBuffer paramCharBuffer, ByteBuffer paramByteBuffer)
    {
      int i = paramCharBuffer.position();
      try
      {
        while (paramCharBuffer.hasRemaining())
        {
          char c = paramCharBuffer.get();
          CoderResult localCoderResult2;
          if (c <= 'ÿ')
          {
            if (!paramByteBuffer.hasRemaining())
            {
              localCoderResult2 = CoderResult.OVERFLOW;
              return localCoderResult2;
            }
            paramByteBuffer.put((byte)c);
            i++;
          }
          else
          {
            if (sgp.parse(c, paramCharBuffer) < 0)
            {
              localCoderResult2 = sgp.error();
              return localCoderResult2;
            }
            localCoderResult2 = sgp.unmappableResult();
            return localCoderResult2;
          }
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
    
    protected void implReplaceWith(byte[] paramArrayOfByte)
    {
      repl = paramArrayOfByte[0];
    }
    
    public int encode(char[] paramArrayOfChar, int paramInt1, int paramInt2, byte[] paramArrayOfByte)
    {
      int i = 0;
      int j = Math.min(paramInt2, paramArrayOfByte.length);
      int k = paramInt1 + j;
      while (paramInt1 < k)
      {
        int m = encodeISOArray(paramArrayOfChar, paramInt1, paramArrayOfByte, i, j);
        paramInt1 += m;
        i += m;
        if (m != j)
        {
          char c = paramArrayOfChar[(paramInt1++)];
          if ((Character.isHighSurrogate(c)) && (paramInt1 < k) && (Character.isLowSurrogate(paramArrayOfChar[paramInt1])))
          {
            if (paramInt2 > paramArrayOfByte.length)
            {
              k++;
              paramInt2--;
            }
            paramInt1++;
          }
          paramArrayOfByte[(i++)] = repl;
          j = Math.min(k - paramInt1, paramArrayOfByte.length - i);
        }
      }
      return i;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\cs\ISO_8859_1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */