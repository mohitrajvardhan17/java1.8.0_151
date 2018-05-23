package sun.nio.cs;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

public class US_ASCII
  extends Charset
  implements HistoricallyNamedCharset
{
  public US_ASCII()
  {
    super("US-ASCII", StandardCharsets.aliases_US_ASCII);
  }
  
  public String historicalName()
  {
    return "ASCII";
  }
  
  public boolean contains(Charset paramCharset)
  {
    return paramCharset instanceof US_ASCII;
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
    private char repl = 65533;
    
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
          CoderResult localCoderResult2;
          if (n >= 0)
          {
            if (k >= m)
            {
              localCoderResult2 = CoderResult.OVERFLOW;
              return localCoderResult2;
            }
            arrayOfChar[(k++)] = ((char)n);
            i++;
          }
          else
          {
            localCoderResult2 = CoderResult.malformedForLength(1);
            return localCoderResult2;
          }
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
          CoderResult localCoderResult2;
          if (j >= 0)
          {
            if (!paramCharBuffer.hasRemaining())
            {
              localCoderResult2 = CoderResult.OVERFLOW;
              return localCoderResult2;
            }
            paramCharBuffer.put((char)j);
            i++;
          }
          else
          {
            localCoderResult2 = CoderResult.malformedForLength(1);
            return localCoderResult2;
          }
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
    
    protected void implReplaceWith(String paramString)
    {
      repl = paramString.charAt(0);
    }
    
    public int decode(byte[] paramArrayOfByte, int paramInt1, int paramInt2, char[] paramArrayOfChar)
    {
      int i = 0;
      paramInt2 = Math.min(paramInt2, paramArrayOfChar.length);
      while (i < paramInt2)
      {
        int j = paramArrayOfByte[(paramInt1++)];
        if (j >= 0) {
          paramArrayOfChar[(i++)] = ((char)j);
        } else {
          paramArrayOfChar[(i++)] = repl;
        }
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
      return paramChar < '';
    }
    
    public boolean isLegalReplacement(byte[] paramArrayOfByte)
    {
      return ((paramArrayOfByte.length == 1) && (paramArrayOfByte[0] >= 0)) || (super.isLegalReplacement(paramArrayOfByte));
    }
    
    private CoderResult encodeArrayLoop(CharBuffer paramCharBuffer, ByteBuffer paramByteBuffer)
    {
      char[] arrayOfChar = paramCharBuffer.array();
      int i = paramCharBuffer.arrayOffset() + paramCharBuffer.position();
      int j = paramCharBuffer.arrayOffset() + paramCharBuffer.limit();
      assert (i <= j);
      i = i <= j ? i : j;
      byte[] arrayOfByte = paramByteBuffer.array();
      int k = paramByteBuffer.arrayOffset() + paramByteBuffer.position();
      int m = paramByteBuffer.arrayOffset() + paramByteBuffer.limit();
      assert (k <= m);
      k = k <= m ? k : m;
      try
      {
        while (i < j)
        {
          char c = arrayOfChar[i];
          CoderResult localCoderResult2;
          if (c < '')
          {
            if (k >= m)
            {
              localCoderResult2 = CoderResult.OVERFLOW;
              return localCoderResult2;
            }
            arrayOfByte[k] = ((byte)c);
            i++;
            k++;
          }
          else
          {
            if (sgp.parse(c, arrayOfChar, i, j) < 0)
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
        paramCharBuffer.position(i - paramCharBuffer.arrayOffset());
        paramByteBuffer.position(k - paramByteBuffer.arrayOffset());
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
          if (c < '')
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
      int j = paramInt1 + Math.min(paramInt2, paramArrayOfByte.length);
      while (paramInt1 < j)
      {
        char c = paramArrayOfChar[(paramInt1++)];
        if (c < '')
        {
          paramArrayOfByte[(i++)] = ((byte)c);
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\cs\US_ASCII.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */