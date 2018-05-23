package java.util.zip;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import sun.nio.cs.ArrayDecoder;
import sun.nio.cs.ArrayEncoder;

final class ZipCoder
{
  private Charset cs;
  private CharsetDecoder dec;
  private CharsetEncoder enc;
  private boolean isUTF8;
  private ZipCoder utf8;
  
  String toString(byte[] paramArrayOfByte, int paramInt)
  {
    CharsetDecoder localCharsetDecoder = decoder().reset();
    int i = (int)(paramInt * localCharsetDecoder.maxCharsPerByte());
    char[] arrayOfChar = new char[i];
    if (i == 0) {
      return new String(arrayOfChar);
    }
    if ((isUTF8) && ((localCharsetDecoder instanceof ArrayDecoder)))
    {
      int j = ((ArrayDecoder)localCharsetDecoder).decode(paramArrayOfByte, 0, paramInt, arrayOfChar);
      if (j == -1) {
        throw new IllegalArgumentException("MALFORMED");
      }
      return new String(arrayOfChar, 0, j);
    }
    ByteBuffer localByteBuffer = ByteBuffer.wrap(paramArrayOfByte, 0, paramInt);
    CharBuffer localCharBuffer = CharBuffer.wrap(arrayOfChar);
    CoderResult localCoderResult = localCharsetDecoder.decode(localByteBuffer, localCharBuffer, true);
    if (!localCoderResult.isUnderflow()) {
      throw new IllegalArgumentException(localCoderResult.toString());
    }
    localCoderResult = localCharsetDecoder.flush(localCharBuffer);
    if (!localCoderResult.isUnderflow()) {
      throw new IllegalArgumentException(localCoderResult.toString());
    }
    return new String(arrayOfChar, 0, localCharBuffer.position());
  }
  
  String toString(byte[] paramArrayOfByte)
  {
    return toString(paramArrayOfByte, paramArrayOfByte.length);
  }
  
  byte[] getBytes(String paramString)
  {
    CharsetEncoder localCharsetEncoder = encoder().reset();
    char[] arrayOfChar = paramString.toCharArray();
    int i = (int)(arrayOfChar.length * localCharsetEncoder.maxBytesPerChar());
    byte[] arrayOfByte = new byte[i];
    if (i == 0) {
      return arrayOfByte;
    }
    if ((isUTF8) && ((localCharsetEncoder instanceof ArrayEncoder)))
    {
      int j = ((ArrayEncoder)localCharsetEncoder).encode(arrayOfChar, 0, arrayOfChar.length, arrayOfByte);
      if (j == -1) {
        throw new IllegalArgumentException("MALFORMED");
      }
      return Arrays.copyOf(arrayOfByte, j);
    }
    ByteBuffer localByteBuffer = ByteBuffer.wrap(arrayOfByte);
    CharBuffer localCharBuffer = CharBuffer.wrap(arrayOfChar);
    CoderResult localCoderResult = localCharsetEncoder.encode(localCharBuffer, localByteBuffer, true);
    if (!localCoderResult.isUnderflow()) {
      throw new IllegalArgumentException(localCoderResult.toString());
    }
    localCoderResult = localCharsetEncoder.flush(localByteBuffer);
    if (!localCoderResult.isUnderflow()) {
      throw new IllegalArgumentException(localCoderResult.toString());
    }
    if (localByteBuffer.position() == arrayOfByte.length) {
      return arrayOfByte;
    }
    return Arrays.copyOf(arrayOfByte, localByteBuffer.position());
  }
  
  byte[] getBytesUTF8(String paramString)
  {
    if (isUTF8) {
      return getBytes(paramString);
    }
    if (utf8 == null) {
      utf8 = new ZipCoder(StandardCharsets.UTF_8);
    }
    return utf8.getBytes(paramString);
  }
  
  String toStringUTF8(byte[] paramArrayOfByte, int paramInt)
  {
    if (isUTF8) {
      return toString(paramArrayOfByte, paramInt);
    }
    if (utf8 == null) {
      utf8 = new ZipCoder(StandardCharsets.UTF_8);
    }
    return utf8.toString(paramArrayOfByte, paramInt);
  }
  
  boolean isUTF8()
  {
    return isUTF8;
  }
  
  private ZipCoder(Charset paramCharset)
  {
    cs = paramCharset;
    isUTF8 = paramCharset.name().equals(StandardCharsets.UTF_8.name());
  }
  
  static ZipCoder get(Charset paramCharset)
  {
    return new ZipCoder(paramCharset);
  }
  
  private CharsetDecoder decoder()
  {
    if (dec == null) {
      dec = cs.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
    }
    return dec;
  }
  
  private CharsetEncoder encoder()
  {
    if (enc == null) {
      enc = cs.newEncoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
    }
    return enc;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\zip\ZipCoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */