package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;

public class CDRInputStream_1_2
  extends CDRInputStream_1_1
{
  protected boolean headerPadding;
  protected boolean restoreHeaderPadding;
  
  public CDRInputStream_1_2() {}
  
  void setHeaderPadding(boolean paramBoolean)
  {
    headerPadding = paramBoolean;
  }
  
  public void mark(int paramInt)
  {
    super.mark(paramInt);
    restoreHeaderPadding = headerPadding;
  }
  
  public void reset()
  {
    super.reset();
    headerPadding = restoreHeaderPadding;
    restoreHeaderPadding = false;
  }
  
  public CDRInputStreamBase dup()
  {
    CDRInputStreamBase localCDRInputStreamBase = super.dup();
    headerPadding = headerPadding;
    return localCDRInputStreamBase;
  }
  
  protected void alignAndCheck(int paramInt1, int paramInt2)
  {
    if (headerPadding == true)
    {
      headerPadding = false;
      alignOnBoundary(8);
    }
    checkBlockLength(paramInt1, paramInt2);
    int i = computeAlignment(bbwi.position(), paramInt1);
    bbwi.position(bbwi.position() + i);
    if (bbwi.position() + paramInt2 > bbwi.buflen) {
      grow(1, paramInt2);
    }
  }
  
  public GIOPVersion getGIOPVersion()
  {
    return GIOPVersion.V1_2;
  }
  
  public char read_wchar()
  {
    int i = read_octet();
    char[] arrayOfChar = getConvertedChars(i, getWCharConverter());
    if (getWCharConverter().getNumChars() > 1) {
      throw wrapper.btcResultMoreThanOneChar();
    }
    return arrayOfChar[0];
  }
  
  public String read_wstring()
  {
    int i = read_long();
    if (i == 0) {
      return new String("");
    }
    checkForNegativeLength(i);
    return new String(getConvertedChars(i, getWCharConverter()), 0, getWCharConverter().getNumChars());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\encoding\CDRInputStream_1_2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */