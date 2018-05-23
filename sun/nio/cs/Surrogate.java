package sun.nio.cs;

import java.nio.CharBuffer;
import java.nio.charset.CoderResult;

public class Surrogate
{
  public static final char MIN_HIGH = '?';
  public static final char MAX_HIGH = '?';
  public static final char MIN_LOW = '?';
  public static final char MAX_LOW = '?';
  public static final char MIN = '?';
  public static final char MAX = '?';
  public static final int UCS4_MIN = 65536;
  public static final int UCS4_MAX = 1114111;
  
  private Surrogate() {}
  
  public static boolean isHigh(int paramInt)
  {
    return (55296 <= paramInt) && (paramInt <= 56319);
  }
  
  public static boolean isLow(int paramInt)
  {
    return (56320 <= paramInt) && (paramInt <= 57343);
  }
  
  public static boolean is(int paramInt)
  {
    return (55296 <= paramInt) && (paramInt <= 57343);
  }
  
  public static boolean neededFor(int paramInt)
  {
    return Character.isSupplementaryCodePoint(paramInt);
  }
  
  public static char high(int paramInt)
  {
    assert (Character.isSupplementaryCodePoint(paramInt));
    return Character.highSurrogate(paramInt);
  }
  
  public static char low(int paramInt)
  {
    assert (Character.isSupplementaryCodePoint(paramInt));
    return Character.lowSurrogate(paramInt);
  }
  
  public static int toUCS4(char paramChar1, char paramChar2)
  {
    assert ((Character.isHighSurrogate(paramChar1)) && (Character.isLowSurrogate(paramChar2)));
    return Character.toCodePoint(paramChar1, paramChar2);
  }
  
  public static class Generator
  {
    private CoderResult error = CoderResult.OVERFLOW;
    
    public Generator() {}
    
    public CoderResult error()
    {
      assert (error != null);
      return error;
    }
    
    public int generate(int paramInt1, int paramInt2, CharBuffer paramCharBuffer)
    {
      if (Character.isBmpCodePoint(paramInt1))
      {
        char c = (char)paramInt1;
        if (Character.isSurrogate(c))
        {
          error = CoderResult.malformedForLength(paramInt2);
          return -1;
        }
        if (paramCharBuffer.remaining() < 1)
        {
          error = CoderResult.OVERFLOW;
          return -1;
        }
        paramCharBuffer.put(c);
        error = null;
        return 1;
      }
      if (Character.isValidCodePoint(paramInt1))
      {
        if (paramCharBuffer.remaining() < 2)
        {
          error = CoderResult.OVERFLOW;
          return -1;
        }
        paramCharBuffer.put(Character.highSurrogate(paramInt1));
        paramCharBuffer.put(Character.lowSurrogate(paramInt1));
        error = null;
        return 2;
      }
      error = CoderResult.unmappableForLength(paramInt2);
      return -1;
    }
    
    public int generate(int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3, int paramInt4)
    {
      if (Character.isBmpCodePoint(paramInt1))
      {
        char c = (char)paramInt1;
        if (Character.isSurrogate(c))
        {
          error = CoderResult.malformedForLength(paramInt2);
          return -1;
        }
        if (paramInt4 - paramInt3 < 1)
        {
          error = CoderResult.OVERFLOW;
          return -1;
        }
        paramArrayOfChar[paramInt3] = c;
        error = null;
        return 1;
      }
      if (Character.isValidCodePoint(paramInt1))
      {
        if (paramInt4 - paramInt3 < 2)
        {
          error = CoderResult.OVERFLOW;
          return -1;
        }
        paramArrayOfChar[paramInt3] = Character.highSurrogate(paramInt1);
        paramArrayOfChar[(paramInt3 + 1)] = Character.lowSurrogate(paramInt1);
        error = null;
        return 2;
      }
      error = CoderResult.unmappableForLength(paramInt2);
      return -1;
    }
  }
  
  public static class Parser
  {
    private int character;
    private CoderResult error = CoderResult.UNDERFLOW;
    private boolean isPair;
    
    public Parser() {}
    
    public int character()
    {
      assert (error == null);
      return character;
    }
    
    public boolean isPair()
    {
      assert (error == null);
      return isPair;
    }
    
    public int increment()
    {
      assert (error == null);
      return isPair ? 2 : 1;
    }
    
    public CoderResult error()
    {
      assert (error != null);
      return error;
    }
    
    public CoderResult unmappableResult()
    {
      assert (error == null);
      return CoderResult.unmappableForLength(isPair ? 2 : 1);
    }
    
    public int parse(char paramChar, CharBuffer paramCharBuffer)
    {
      if (Character.isHighSurrogate(paramChar))
      {
        if (!paramCharBuffer.hasRemaining())
        {
          error = CoderResult.UNDERFLOW;
          return -1;
        }
        char c = paramCharBuffer.get();
        if (Character.isLowSurrogate(c))
        {
          character = Character.toCodePoint(paramChar, c);
          isPair = true;
          error = null;
          return character;
        }
        error = CoderResult.malformedForLength(1);
        return -1;
      }
      if (Character.isLowSurrogate(paramChar))
      {
        error = CoderResult.malformedForLength(1);
        return -1;
      }
      character = paramChar;
      isPair = false;
      error = null;
      return character;
    }
    
    public int parse(char paramChar, char[] paramArrayOfChar, int paramInt1, int paramInt2)
    {
      assert (paramArrayOfChar[paramInt1] == paramChar);
      if (Character.isHighSurrogate(paramChar))
      {
        if (paramInt2 - paramInt1 < 2)
        {
          error = CoderResult.UNDERFLOW;
          return -1;
        }
        char c = paramArrayOfChar[(paramInt1 + 1)];
        if (Character.isLowSurrogate(c))
        {
          character = Character.toCodePoint(paramChar, c);
          isPair = true;
          error = null;
          return character;
        }
        error = CoderResult.malformedForLength(1);
        return -1;
      }
      if (Character.isLowSurrogate(paramChar))
      {
        error = CoderResult.malformedForLength(1);
        return -1;
      }
      character = paramChar;
      isPair = false;
      error = null;
      return character;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\cs\Surrogate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */