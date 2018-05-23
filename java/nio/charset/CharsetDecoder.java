package java.nio.charset;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

public abstract class CharsetDecoder
{
  private final Charset charset;
  private final float averageCharsPerByte;
  private final float maxCharsPerByte;
  private String replacement;
  private CodingErrorAction malformedInputAction = CodingErrorAction.REPORT;
  private CodingErrorAction unmappableCharacterAction = CodingErrorAction.REPORT;
  private static final int ST_RESET = 0;
  private static final int ST_CODING = 1;
  private static final int ST_END = 2;
  private static final int ST_FLUSHED = 3;
  private int state = 0;
  private static String[] stateNames = { "RESET", "CODING", "CODING_END", "FLUSHED" };
  
  private CharsetDecoder(Charset paramCharset, float paramFloat1, float paramFloat2, String paramString)
  {
    charset = paramCharset;
    if (paramFloat1 <= 0.0F) {
      throw new IllegalArgumentException("Non-positive averageCharsPerByte");
    }
    if (paramFloat2 <= 0.0F) {
      throw new IllegalArgumentException("Non-positive maxCharsPerByte");
    }
    if ((!Charset.atBugLevel("1.4")) && (paramFloat1 > paramFloat2)) {
      throw new IllegalArgumentException("averageCharsPerByte exceeds maxCharsPerByte");
    }
    replacement = paramString;
    averageCharsPerByte = paramFloat1;
    maxCharsPerByte = paramFloat2;
    replaceWith(paramString);
  }
  
  protected CharsetDecoder(Charset paramCharset, float paramFloat1, float paramFloat2)
  {
    this(paramCharset, paramFloat1, paramFloat2, "�");
  }
  
  public final Charset charset()
  {
    return charset;
  }
  
  public final String replacement()
  {
    return replacement;
  }
  
  public final CharsetDecoder replaceWith(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("Null replacement");
    }
    int i = paramString.length();
    if (i == 0) {
      throw new IllegalArgumentException("Empty replacement");
    }
    if (i > maxCharsPerByte) {
      throw new IllegalArgumentException("Replacement too long");
    }
    replacement = paramString;
    implReplaceWith(replacement);
    return this;
  }
  
  protected void implReplaceWith(String paramString) {}
  
  public CodingErrorAction malformedInputAction()
  {
    return malformedInputAction;
  }
  
  public final CharsetDecoder onMalformedInput(CodingErrorAction paramCodingErrorAction)
  {
    if (paramCodingErrorAction == null) {
      throw new IllegalArgumentException("Null action");
    }
    malformedInputAction = paramCodingErrorAction;
    implOnMalformedInput(paramCodingErrorAction);
    return this;
  }
  
  protected void implOnMalformedInput(CodingErrorAction paramCodingErrorAction) {}
  
  public CodingErrorAction unmappableCharacterAction()
  {
    return unmappableCharacterAction;
  }
  
  public final CharsetDecoder onUnmappableCharacter(CodingErrorAction paramCodingErrorAction)
  {
    if (paramCodingErrorAction == null) {
      throw new IllegalArgumentException("Null action");
    }
    unmappableCharacterAction = paramCodingErrorAction;
    implOnUnmappableCharacter(paramCodingErrorAction);
    return this;
  }
  
  protected void implOnUnmappableCharacter(CodingErrorAction paramCodingErrorAction) {}
  
  public final float averageCharsPerByte()
  {
    return averageCharsPerByte;
  }
  
  public final float maxCharsPerByte()
  {
    return maxCharsPerByte;
  }
  
  public final CoderResult decode(ByteBuffer paramByteBuffer, CharBuffer paramCharBuffer, boolean paramBoolean)
  {
    int i = paramBoolean ? 2 : 1;
    if ((state != 0) && (state != 1) && ((!paramBoolean) || (state != 2))) {
      throwIllegalStateException(state, i);
    }
    state = i;
    for (;;)
    {
      CoderResult localCoderResult;
      try
      {
        localCoderResult = decodeLoop(paramByteBuffer, paramCharBuffer);
      }
      catch (BufferUnderflowException localBufferUnderflowException)
      {
        throw new CoderMalfunctionError(localBufferUnderflowException);
      }
      catch (BufferOverflowException localBufferOverflowException)
      {
        throw new CoderMalfunctionError(localBufferOverflowException);
      }
      if (localCoderResult.isOverflow()) {
        return localCoderResult;
      }
      if (localCoderResult.isUnderflow()) {
        if ((paramBoolean) && (paramByteBuffer.hasRemaining())) {
          localCoderResult = CoderResult.malformedForLength(paramByteBuffer.remaining());
        } else {
          return localCoderResult;
        }
      }
      CodingErrorAction localCodingErrorAction = null;
      if (localCoderResult.isMalformed()) {
        localCodingErrorAction = malformedInputAction;
      } else if (localCoderResult.isUnmappable()) {
        localCodingErrorAction = unmappableCharacterAction;
      } else if (!$assertionsDisabled) {
        throw new AssertionError(localCoderResult.toString());
      }
      if (localCodingErrorAction == CodingErrorAction.REPORT) {
        return localCoderResult;
      }
      if (localCodingErrorAction == CodingErrorAction.REPLACE)
      {
        if (paramCharBuffer.remaining() < replacement.length()) {
          return CoderResult.OVERFLOW;
        }
        paramCharBuffer.put(replacement);
      }
      if ((localCodingErrorAction == CodingErrorAction.IGNORE) || (localCodingErrorAction == CodingErrorAction.REPLACE)) {
        paramByteBuffer.position(paramByteBuffer.position() + localCoderResult.length());
      } else if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
  }
  
  public final CoderResult flush(CharBuffer paramCharBuffer)
  {
    if (state == 2)
    {
      CoderResult localCoderResult = implFlush(paramCharBuffer);
      if (localCoderResult.isUnderflow()) {
        state = 3;
      }
      return localCoderResult;
    }
    if (state != 3) {
      throwIllegalStateException(state, 3);
    }
    return CoderResult.UNDERFLOW;
  }
  
  protected CoderResult implFlush(CharBuffer paramCharBuffer)
  {
    return CoderResult.UNDERFLOW;
  }
  
  public final CharsetDecoder reset()
  {
    implReset();
    state = 0;
    return this;
  }
  
  protected void implReset() {}
  
  protected abstract CoderResult decodeLoop(ByteBuffer paramByteBuffer, CharBuffer paramCharBuffer);
  
  public final CharBuffer decode(ByteBuffer paramByteBuffer)
    throws CharacterCodingException
  {
    int i = (int)(paramByteBuffer.remaining() * averageCharsPerByte());
    Object localObject = CharBuffer.allocate(i);
    if ((i == 0) && (paramByteBuffer.remaining() == 0)) {
      return (CharBuffer)localObject;
    }
    reset();
    for (;;)
    {
      CoderResult localCoderResult = paramByteBuffer.hasRemaining() ? decode(paramByteBuffer, (CharBuffer)localObject, true) : CoderResult.UNDERFLOW;
      if (localCoderResult.isUnderflow()) {
        localCoderResult = flush((CharBuffer)localObject);
      }
      if (localCoderResult.isUnderflow()) {
        break;
      }
      if (localCoderResult.isOverflow())
      {
        i = 2 * i + 1;
        CharBuffer localCharBuffer = CharBuffer.allocate(i);
        ((CharBuffer)localObject).flip();
        localCharBuffer.put((CharBuffer)localObject);
        localObject = localCharBuffer;
      }
      else
      {
        localCoderResult.throwException();
      }
    }
    ((CharBuffer)localObject).flip();
    return (CharBuffer)localObject;
  }
  
  public boolean isAutoDetecting()
  {
    return false;
  }
  
  public boolean isCharsetDetected()
  {
    throw new UnsupportedOperationException();
  }
  
  public Charset detectedCharset()
  {
    throw new UnsupportedOperationException();
  }
  
  private void throwIllegalStateException(int paramInt1, int paramInt2)
  {
    throw new IllegalStateException("Current state = " + stateNames[paramInt1] + ", new state = " + stateNames[paramInt2]);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\charset\CharsetDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */