package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA.CompletionStatus;

public class CDROutputStream_1_2
  extends CDROutputStream_1_1
{
  protected boolean primitiveAcrossFragmentedChunk = false;
  protected boolean specialChunk = false;
  private boolean headerPadding;
  
  public CDROutputStream_1_2() {}
  
  protected void handleSpecialChunkBegin(int paramInt)
  {
    if ((inBlock) && (paramInt + bbwi.position() > bbwi.buflen))
    {
      int i = bbwi.position();
      bbwi.position(blockSizeIndex - 4);
      writeLongWithoutAlign(i - blockSizeIndex + paramInt);
      bbwi.position(i);
      specialChunk = true;
    }
  }
  
  protected void handleSpecialChunkEnd()
  {
    if ((inBlock) && (specialChunk))
    {
      inBlock = false;
      blockSizeIndex = -1;
      blockSizePosition = -1;
      start_block();
      specialChunk = false;
    }
  }
  
  private void checkPrimitiveAcrossFragmentedChunk()
  {
    if (primitiveAcrossFragmentedChunk)
    {
      primitiveAcrossFragmentedChunk = false;
      inBlock = false;
      blockSizeIndex = -1;
      blockSizePosition = -1;
      start_block();
    }
  }
  
  public void write_octet(byte paramByte)
  {
    super.write_octet(paramByte);
    checkPrimitiveAcrossFragmentedChunk();
  }
  
  public void write_short(short paramShort)
  {
    super.write_short(paramShort);
    checkPrimitiveAcrossFragmentedChunk();
  }
  
  public void write_long(int paramInt)
  {
    super.write_long(paramInt);
    checkPrimitiveAcrossFragmentedChunk();
  }
  
  public void write_longlong(long paramLong)
  {
    super.write_longlong(paramLong);
    checkPrimitiveAcrossFragmentedChunk();
  }
  
  void setHeaderPadding(boolean paramBoolean)
  {
    headerPadding = paramBoolean;
  }
  
  protected void alignAndReserve(int paramInt1, int paramInt2)
  {
    if (headerPadding == true)
    {
      headerPadding = false;
      alignOnBoundary(8);
    }
    bbwi.position(bbwi.position() + computeAlignment(paramInt1));
    if (bbwi.position() + paramInt2 > bbwi.buflen) {
      grow(paramInt1, paramInt2);
    }
  }
  
  protected void grow(int paramInt1, int paramInt2)
  {
    int i = bbwi.position();
    int j = (inBlock) && (!specialChunk) ? 1 : 0;
    if (j != 0)
    {
      int k = bbwi.position();
      bbwi.position(blockSizeIndex - 4);
      writeLongWithoutAlign(k - blockSizeIndex + paramInt2);
      bbwi.position(k);
    }
    bbwi.needed = paramInt2;
    bufferManagerWrite.overflow(bbwi);
    if (bbwi.fragmented)
    {
      bbwi.fragmented = false;
      fragmentOffset += i - bbwi.position();
      if (j != 0) {
        primitiveAcrossFragmentedChunk = true;
      }
    }
  }
  
  public GIOPVersion getGIOPVersion()
  {
    return GIOPVersion.V1_2;
  }
  
  public void write_wchar(char paramChar)
  {
    CodeSetConversion.CTBConverter localCTBConverter = getWCharConverter();
    localCTBConverter.convert(paramChar);
    handleSpecialChunkBegin(1 + localCTBConverter.getNumBytes());
    write_octet((byte)localCTBConverter.getNumBytes());
    byte[] arrayOfByte = localCTBConverter.getBytes();
    internalWriteOctetArray(arrayOfByte, 0, localCTBConverter.getNumBytes());
    handleSpecialChunkEnd();
  }
  
  public void write_wchar_array(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if (paramArrayOfChar == null) {
      throw wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
    }
    CodeSetConversion.CTBConverter localCTBConverter = getWCharConverter();
    int i = 0;
    int j = (int)Math.ceil(localCTBConverter.getMaxBytesPerChar() * paramInt2);
    byte[] arrayOfByte = new byte[j + paramInt2];
    for (int k = 0; k < paramInt2; k++)
    {
      localCTBConverter.convert(paramArrayOfChar[(paramInt1 + k)]);
      arrayOfByte[(i++)] = ((byte)localCTBConverter.getNumBytes());
      System.arraycopy(localCTBConverter.getBytes(), 0, arrayOfByte, i, localCTBConverter.getNumBytes());
      i += localCTBConverter.getNumBytes();
    }
    handleSpecialChunkBegin(i);
    internalWriteOctetArray(arrayOfByte, 0, i);
    handleSpecialChunkEnd();
  }
  
  public void write_wstring(String paramString)
  {
    if (paramString == null) {
      throw wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
    }
    if (paramString.length() == 0)
    {
      write_long(0);
      return;
    }
    CodeSetConversion.CTBConverter localCTBConverter = getWCharConverter();
    localCTBConverter.convert(paramString);
    handleSpecialChunkBegin(computeAlignment(4) + 4 + localCTBConverter.getNumBytes());
    write_long(localCTBConverter.getNumBytes());
    internalWriteOctetArray(localCTBConverter.getBytes(), 0, localCTBConverter.getNumBytes());
    handleSpecialChunkEnd();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\encoding\CDROutputStream_1_2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */