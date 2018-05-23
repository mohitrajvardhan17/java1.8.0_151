package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA.CompletionStatus;

public class CDROutputStream_1_1
  extends CDROutputStream_1_0
{
  protected int fragmentOffset = 0;
  
  public CDROutputStream_1_1() {}
  
  protected void alignAndReserve(int paramInt1, int paramInt2)
  {
    int i = computeAlignment(paramInt1);
    if (bbwi.position() + paramInt2 + i > bbwi.buflen)
    {
      grow(paramInt1, paramInt2);
      i = computeAlignment(paramInt1);
    }
    bbwi.position(bbwi.position() + i);
  }
  
  protected void grow(int paramInt1, int paramInt2)
  {
    int i = bbwi.position();
    super.grow(paramInt1, paramInt2);
    if (bbwi.fragmented)
    {
      bbwi.fragmented = false;
      fragmentOffset += i - bbwi.position();
    }
  }
  
  public int get_offset()
  {
    return bbwi.position() + fragmentOffset;
  }
  
  public GIOPVersion getGIOPVersion()
  {
    return GIOPVersion.V1_1;
  }
  
  public void write_wchar(char paramChar)
  {
    CodeSetConversion.CTBConverter localCTBConverter = getWCharConverter();
    localCTBConverter.convert(paramChar);
    if (localCTBConverter.getNumBytes() != 2) {
      throw wrapper.badGiop11Ctb(CompletionStatus.COMPLETED_MAYBE);
    }
    alignAndReserve(localCTBConverter.getAlignment(), localCTBConverter.getNumBytes());
    parent.write_octet_array(localCTBConverter.getBytes(), 0, localCTBConverter.getNumBytes());
  }
  
  public void write_wstring(String paramString)
  {
    if (paramString == null) {
      throw wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
    }
    int i = paramString.length() + 1;
    write_long(i);
    CodeSetConversion.CTBConverter localCTBConverter = getWCharConverter();
    localCTBConverter.convert(paramString);
    internalWriteOctetArray(localCTBConverter.getBytes(), 0, localCTBConverter.getNumBytes());
    write_short((short)0);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\encoding\CDROutputStream_1_1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */