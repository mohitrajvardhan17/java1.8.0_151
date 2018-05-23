package sun.nio.cs;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

public abstract class UnicodeEncoder
  extends CharsetEncoder
{
  protected static final char BYTE_ORDER_MARK = '﻿';
  protected static final char REVERSED_MARK = '￾';
  protected static final int BIG = 0;
  protected static final int LITTLE = 1;
  private int byteOrder;
  private boolean usesMark;
  private boolean needsMark;
  private final Surrogate.Parser sgp = new Surrogate.Parser();
  
  protected UnicodeEncoder(Charset paramCharset, int paramInt, boolean paramBoolean)
  {
    super(paramCharset, 2.0F, paramBoolean ? 4.0F : 2.0F, new byte[] { -3, paramInt == 0 ? new byte[] { -1, -3 } : -1 });
    usesMark = (needsMark = paramBoolean);
    byteOrder = paramInt;
  }
  
  private void put(char paramChar, ByteBuffer paramByteBuffer)
  {
    if (byteOrder == 0)
    {
      paramByteBuffer.put((byte)(paramChar >> '\b'));
      paramByteBuffer.put((byte)(paramChar & 0xFF));
    }
    else
    {
      paramByteBuffer.put((byte)(paramChar & 0xFF));
      paramByteBuffer.put((byte)(paramChar >> '\b'));
    }
  }
  
  protected CoderResult encodeLoop(CharBuffer paramCharBuffer, ByteBuffer paramByteBuffer)
  {
    int i = paramCharBuffer.position();
    if ((needsMark) && (paramCharBuffer.hasRemaining()))
    {
      if (paramByteBuffer.remaining() < 2) {
        return CoderResult.OVERFLOW;
      }
      put(65279, paramByteBuffer);
      needsMark = false;
    }
    try
    {
      while (paramCharBuffer.hasRemaining())
      {
        char c = paramCharBuffer.get();
        if (!Character.isSurrogate(c))
        {
          if (paramByteBuffer.remaining() < 2)
          {
            CoderResult localCoderResult2 = CoderResult.OVERFLOW;
            return localCoderResult2;
          }
          i++;
          put(c, paramByteBuffer);
        }
        else
        {
          int j = sgp.parse(c, paramCharBuffer);
          CoderResult localCoderResult3;
          if (j < 0)
          {
            localCoderResult3 = sgp.error();
            return localCoderResult3;
          }
          if (paramByteBuffer.remaining() < 4)
          {
            localCoderResult3 = CoderResult.OVERFLOW;
            return localCoderResult3;
          }
          i += 2;
          put(Character.highSurrogate(j), paramByteBuffer);
          put(Character.lowSurrogate(j), paramByteBuffer);
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
  
  protected void implReset()
  {
    needsMark = usesMark;
  }
  
  public boolean canEncode(char paramChar)
  {
    return !Character.isSurrogate(paramChar);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\cs\UnicodeEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */