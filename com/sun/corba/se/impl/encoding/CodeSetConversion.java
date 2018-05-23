package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.UnmappableCharacterException;
import java.nio.charset.UnsupportedCharsetException;

public class CodeSetConversion
{
  private static CodeSetConversion implementation;
  private static final int FALLBACK_CODESET = 0;
  private CodeSetCache cache = new CodeSetCache();
  
  public CTBConverter getCTBConverter(OSFCodeSetRegistry.Entry paramEntry)
  {
    int i = !paramEntry.isFixedWidth() ? 1 : paramEntry.getMaxBytesPerChar();
    return new JavaCTBConverter(paramEntry, i);
  }
  
  public CTBConverter getCTBConverter(OSFCodeSetRegistry.Entry paramEntry, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramEntry == OSFCodeSetRegistry.UCS_2) {
      return new UTF16CTBConverter(paramBoolean1);
    }
    if (paramEntry == OSFCodeSetRegistry.UTF_16)
    {
      if (paramBoolean2) {
        return new UTF16CTBConverter();
      }
      return new UTF16CTBConverter(paramBoolean1);
    }
    int i = !paramEntry.isFixedWidth() ? 1 : paramEntry.getMaxBytesPerChar();
    return new JavaCTBConverter(paramEntry, i);
  }
  
  public BTCConverter getBTCConverter(OSFCodeSetRegistry.Entry paramEntry)
  {
    return new JavaBTCConverter(paramEntry);
  }
  
  public BTCConverter getBTCConverter(OSFCodeSetRegistry.Entry paramEntry, boolean paramBoolean)
  {
    if ((paramEntry == OSFCodeSetRegistry.UTF_16) || (paramEntry == OSFCodeSetRegistry.UCS_2)) {
      return new UTF16BTCConverter(paramBoolean);
    }
    return new JavaBTCConverter(paramEntry);
  }
  
  private int selectEncoding(CodeSetComponentInfo.CodeSetComponent paramCodeSetComponent1, CodeSetComponentInfo.CodeSetComponent paramCodeSetComponent2)
  {
    int i = nativeCodeSet;
    if (i == 0) {
      if (conversionCodeSets.length > 0) {
        i = conversionCodeSets[0];
      } else {
        return 0;
      }
    }
    if (nativeCodeSet == i) {
      return i;
    }
    for (int j = 0; j < conversionCodeSets.length; j++) {
      if (i == conversionCodeSets[j]) {
        return i;
      }
    }
    for (j = 0; j < conversionCodeSets.length; j++) {
      if (nativeCodeSet == conversionCodeSets[j]) {
        return nativeCodeSet;
      }
    }
    for (j = 0; j < conversionCodeSets.length; j++) {
      for (int k = 0; k < conversionCodeSets.length; k++) {
        if (conversionCodeSets[j] == conversionCodeSets[k]) {
          return conversionCodeSets[j];
        }
      }
    }
    return 0;
  }
  
  public CodeSetComponentInfo.CodeSetContext negotiate(CodeSetComponentInfo paramCodeSetComponentInfo1, CodeSetComponentInfo paramCodeSetComponentInfo2)
  {
    int i = selectEncoding(paramCodeSetComponentInfo1.getCharComponent(), paramCodeSetComponentInfo2.getCharComponent());
    if (i == 0) {
      i = OSFCodeSetRegistry.UTF_8.getNumber();
    }
    int j = selectEncoding(paramCodeSetComponentInfo1.getWCharComponent(), paramCodeSetComponentInfo2.getWCharComponent());
    if (j == 0) {
      j = OSFCodeSetRegistry.UTF_16.getNumber();
    }
    return new CodeSetComponentInfo.CodeSetContext(i, j);
  }
  
  private CodeSetConversion() {}
  
  public static final CodeSetConversion impl()
  {
    return CodeSetConversionHolder.csc;
  }
  
  public static abstract class BTCConverter
  {
    public BTCConverter() {}
    
    public abstract boolean isFixedWidthEncoding();
    
    public abstract int getFixedCharWidth();
    
    public abstract int getNumChars();
    
    public abstract char[] getChars(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  }
  
  public static abstract class CTBConverter
  {
    public CTBConverter() {}
    
    public abstract void convert(char paramChar);
    
    public abstract void convert(String paramString);
    
    public abstract int getNumBytes();
    
    public abstract float getMaxBytesPerChar();
    
    public abstract boolean isFixedWidthEncoding();
    
    public abstract int getAlignment();
    
    public abstract byte[] getBytes();
  }
  
  private static class CodeSetConversionHolder
  {
    static final CodeSetConversion csc = new CodeSetConversion(null);
    
    private CodeSetConversionHolder() {}
  }
  
  private class JavaBTCConverter
    extends CodeSetConversion.BTCConverter
  {
    private ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.encoding");
    private OMGSystemException omgWrapper = OMGSystemException.get("rpc.encoding");
    protected CharsetDecoder btc = getConverter(paramEntry.getName());
    private char[] buffer;
    private int resultingNumChars;
    private OSFCodeSetRegistry.Entry codeset;
    
    public JavaBTCConverter(OSFCodeSetRegistry.Entry paramEntry)
    {
      codeset = paramEntry;
    }
    
    public final boolean isFixedWidthEncoding()
    {
      return codeset.isFixedWidth();
    }
    
    public final int getFixedCharWidth()
    {
      return codeset.getMaxBytesPerChar();
    }
    
    public final int getNumChars()
    {
      return resultingNumChars;
    }
    
    public char[] getChars(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      try
      {
        ByteBuffer localByteBuffer = ByteBuffer.wrap(paramArrayOfByte, paramInt1, paramInt2);
        CharBuffer localCharBuffer = btc.decode(localByteBuffer);
        resultingNumChars = localCharBuffer.limit();
        if (localCharBuffer.limit() == localCharBuffer.capacity())
        {
          buffer = localCharBuffer.array();
        }
        else
        {
          buffer = new char[localCharBuffer.limit()];
          localCharBuffer.get(buffer, 0, localCharBuffer.limit()).position(0);
        }
        return buffer;
      }
      catch (IllegalStateException localIllegalStateException)
      {
        throw wrapper.btcConverterFailure(localIllegalStateException);
      }
      catch (MalformedInputException localMalformedInputException)
      {
        throw wrapper.badUnicodePair(localMalformedInputException);
      }
      catch (UnmappableCharacterException localUnmappableCharacterException)
      {
        throw omgWrapper.charNotInCodeset(localUnmappableCharacterException);
      }
      catch (CharacterCodingException localCharacterCodingException)
      {
        throw wrapper.btcConverterFailure(localCharacterCodingException);
      }
    }
    
    protected CharsetDecoder getConverter(String paramString)
    {
      CharsetDecoder localCharsetDecoder = null;
      try
      {
        localCharsetDecoder = cache.getByteToCharConverter(paramString);
        if (localCharsetDecoder == null)
        {
          Charset localCharset = Charset.forName(paramString);
          localCharsetDecoder = localCharset.newDecoder();
          cache.setConverter(paramString, localCharsetDecoder);
        }
      }
      catch (IllegalCharsetNameException localIllegalCharsetNameException)
      {
        throw wrapper.invalidBtcConverterName(localIllegalCharsetNameException, paramString);
      }
      return localCharsetDecoder;
    }
  }
  
  private class JavaCTBConverter
    extends CodeSetConversion.CTBConverter
  {
    private ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.encoding");
    private OMGSystemException omgWrapper = OMGSystemException.get("rpc.encoding");
    private CharsetEncoder ctb;
    private int alignment;
    private char[] chars = null;
    private int numBytes = 0;
    private int numChars = 0;
    private ByteBuffer buffer;
    private OSFCodeSetRegistry.Entry codeset;
    
    public JavaCTBConverter(OSFCodeSetRegistry.Entry paramEntry, int paramInt)
    {
      try
      {
        ctb = cache.getCharToByteConverter(paramEntry.getName());
        if (ctb == null)
        {
          Charset localCharset = Charset.forName(paramEntry.getName());
          ctb = localCharset.newEncoder();
          cache.setConverter(paramEntry.getName(), ctb);
        }
      }
      catch (IllegalCharsetNameException localIllegalCharsetNameException)
      {
        throw wrapper.invalidCtbConverterName(localIllegalCharsetNameException, paramEntry.getName());
      }
      catch (UnsupportedCharsetException localUnsupportedCharsetException)
      {
        throw wrapper.invalidCtbConverterName(localUnsupportedCharsetException, paramEntry.getName());
      }
      codeset = paramEntry;
      alignment = paramInt;
    }
    
    public final float getMaxBytesPerChar()
    {
      return ctb.maxBytesPerChar();
    }
    
    public void convert(char paramChar)
    {
      if (chars == null) {
        chars = new char[1];
      }
      chars[0] = paramChar;
      numChars = 1;
      convertCharArray();
    }
    
    public void convert(String paramString)
    {
      if ((chars == null) || (chars.length < paramString.length())) {
        chars = new char[paramString.length()];
      }
      numChars = paramString.length();
      paramString.getChars(0, numChars, chars, 0);
      convertCharArray();
    }
    
    public final int getNumBytes()
    {
      return numBytes;
    }
    
    public final int getAlignment()
    {
      return alignment;
    }
    
    public final boolean isFixedWidthEncoding()
    {
      return codeset.isFixedWidth();
    }
    
    public byte[] getBytes()
    {
      return buffer.array();
    }
    
    private void convertCharArray()
    {
      try
      {
        buffer = ctb.encode(CharBuffer.wrap(chars, 0, numChars));
        numBytes = buffer.limit();
      }
      catch (IllegalStateException localIllegalStateException)
      {
        throw wrapper.ctbConverterFailure(localIllegalStateException);
      }
      catch (MalformedInputException localMalformedInputException)
      {
        throw wrapper.badUnicodePair(localMalformedInputException);
      }
      catch (UnmappableCharacterException localUnmappableCharacterException)
      {
        throw omgWrapper.charNotInCodeset(localUnmappableCharacterException);
      }
      catch (CharacterCodingException localCharacterCodingException)
      {
        throw wrapper.ctbConverterFailure(localCharacterCodingException);
      }
    }
  }
  
  private class UTF16BTCConverter
    extends CodeSetConversion.JavaBTCConverter
  {
    private boolean defaultToLittleEndian;
    private boolean converterUsesBOM = true;
    private static final char UTF16_BE_MARKER = '﻿';
    private static final char UTF16_LE_MARKER = '￾';
    
    public UTF16BTCConverter(boolean paramBoolean)
    {
      super(OSFCodeSetRegistry.UTF_16);
      defaultToLittleEndian = paramBoolean;
    }
    
    public char[] getChars(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      if (hasUTF16ByteOrderMarker(paramArrayOfByte, paramInt1, paramInt2))
      {
        if (!converterUsesBOM) {
          switchToConverter(OSFCodeSetRegistry.UTF_16);
        }
        converterUsesBOM = true;
        return super.getChars(paramArrayOfByte, paramInt1, paramInt2);
      }
      if (converterUsesBOM)
      {
        if (defaultToLittleEndian) {
          switchToConverter(OSFCodeSetRegistry.UTF_16LE);
        } else {
          switchToConverter(OSFCodeSetRegistry.UTF_16BE);
        }
        converterUsesBOM = false;
      }
      return super.getChars(paramArrayOfByte, paramInt1, paramInt2);
    }
    
    private boolean hasUTF16ByteOrderMarker(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      if (paramInt2 >= 4)
      {
        int i = paramArrayOfByte[paramInt1] & 0xFF;
        int j = paramArrayOfByte[(paramInt1 + 1)] & 0xFF;
        int k = (char)(i << 8 | j << 0);
        return (k == 65279) || (k == 65534);
      }
      return false;
    }
    
    private void switchToConverter(OSFCodeSetRegistry.Entry paramEntry)
    {
      btc = super.getConverter(paramEntry.getName());
    }
  }
  
  private class UTF16CTBConverter
    extends CodeSetConversion.JavaCTBConverter
  {
    public UTF16CTBConverter()
    {
      super(OSFCodeSetRegistry.UTF_16, 2);
    }
    
    public UTF16CTBConverter(boolean paramBoolean)
    {
      super(paramBoolean ? OSFCodeSetRegistry.UTF_16LE : OSFCodeSetRegistry.UTF_16BE, 2);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\encoding\CodeSetConversion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */