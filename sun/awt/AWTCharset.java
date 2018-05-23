package sun.awt;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

public class AWTCharset
  extends Charset
{
  protected Charset awtCs;
  protected Charset javaCs;
  
  public AWTCharset(String paramString, Charset paramCharset)
  {
    super(paramString, null);
    javaCs = paramCharset;
    awtCs = this;
  }
  
  public boolean contains(Charset paramCharset)
  {
    if (javaCs == null) {
      return false;
    }
    return javaCs.contains(paramCharset);
  }
  
  public CharsetEncoder newEncoder()
  {
    if (javaCs == null) {
      throw new Error("Encoder is not supported by this Charset");
    }
    return new Encoder(javaCs.newEncoder());
  }
  
  public CharsetDecoder newDecoder()
  {
    if (javaCs == null) {
      throw new Error("Decoder is not supported by this Charset");
    }
    return new Decoder(javaCs.newDecoder());
  }
  
  public class Decoder
    extends CharsetDecoder
  {
    protected CharsetDecoder dec;
    private String nr;
    ByteBuffer fbb = ByteBuffer.allocate(0);
    
    protected Decoder()
    {
      this(javaCs.newDecoder());
    }
    
    protected Decoder(CharsetDecoder paramCharsetDecoder)
    {
      super(paramCharsetDecoder.averageCharsPerByte(), paramCharsetDecoder.maxCharsPerByte());
      dec = paramCharsetDecoder;
    }
    
    protected CoderResult decodeLoop(ByteBuffer paramByteBuffer, CharBuffer paramCharBuffer)
    {
      return dec.decode(paramByteBuffer, paramCharBuffer, true);
    }
    
    protected CoderResult implFlush(CharBuffer paramCharBuffer)
    {
      dec.decode(fbb, paramCharBuffer, true);
      return dec.flush(paramCharBuffer);
    }
    
    protected void implReset()
    {
      dec.reset();
    }
    
    protected void implReplaceWith(String paramString)
    {
      if (dec != null) {
        dec.replaceWith(paramString);
      }
    }
    
    protected void implOnMalformedInput(CodingErrorAction paramCodingErrorAction)
    {
      dec.onMalformedInput(paramCodingErrorAction);
    }
    
    protected void implOnUnmappableCharacter(CodingErrorAction paramCodingErrorAction)
    {
      dec.onUnmappableCharacter(paramCodingErrorAction);
    }
  }
  
  public class Encoder
    extends CharsetEncoder
  {
    protected CharsetEncoder enc;
    
    protected Encoder()
    {
      this(javaCs.newEncoder());
    }
    
    protected Encoder(CharsetEncoder paramCharsetEncoder)
    {
      super(paramCharsetEncoder.averageBytesPerChar(), paramCharsetEncoder.maxBytesPerChar());
      enc = paramCharsetEncoder;
    }
    
    public boolean canEncode(char paramChar)
    {
      return enc.canEncode(paramChar);
    }
    
    public boolean canEncode(CharSequence paramCharSequence)
    {
      return enc.canEncode(paramCharSequence);
    }
    
    protected CoderResult encodeLoop(CharBuffer paramCharBuffer, ByteBuffer paramByteBuffer)
    {
      return enc.encode(paramCharBuffer, paramByteBuffer, true);
    }
    
    protected CoderResult implFlush(ByteBuffer paramByteBuffer)
    {
      return enc.flush(paramByteBuffer);
    }
    
    protected void implReset()
    {
      enc.reset();
    }
    
    protected void implReplaceWith(byte[] paramArrayOfByte)
    {
      if (enc != null) {
        enc.replaceWith(paramArrayOfByte);
      }
    }
    
    protected void implOnMalformedInput(CodingErrorAction paramCodingErrorAction)
    {
      enc.onMalformedInput(paramCodingErrorAction);
    }
    
    protected void implOnUnmappableCharacter(CodingErrorAction paramCodingErrorAction)
    {
      enc.onUnmappableCharacter(paramCodingErrorAction);
    }
    
    public boolean isLegalReplacement(byte[] paramArrayOfByte)
    {
      return true;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\AWTCharset.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */