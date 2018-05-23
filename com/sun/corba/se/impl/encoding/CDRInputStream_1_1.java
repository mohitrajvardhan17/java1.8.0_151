package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;

public class CDRInputStream_1_1
  extends CDRInputStream_1_0
{
  protected int fragmentOffset = 0;
  
  public CDRInputStream_1_1() {}
  
  public GIOPVersion getGIOPVersion()
  {
    return GIOPVersion.V1_1;
  }
  
  public CDRInputStreamBase dup()
  {
    CDRInputStreamBase localCDRInputStreamBase = super.dup();
    fragmentOffset = fragmentOffset;
    return localCDRInputStreamBase;
  }
  
  protected int get_offset()
  {
    return bbwi.position() + fragmentOffset;
  }
  
  protected void alignAndCheck(int paramInt1, int paramInt2)
  {
    checkBlockLength(paramInt1, paramInt2);
    int i = computeAlignment(bbwi.position(), paramInt1);
    if (bbwi.position() + paramInt2 + i > bbwi.buflen)
    {
      if (bbwi.position() + i == bbwi.buflen) {
        bbwi.position(bbwi.position() + i);
      }
      grow(paramInt1, paramInt2);
      i = computeAlignment(bbwi.position(), paramInt1);
    }
    bbwi.position(bbwi.position() + i);
  }
  
  protected void grow(int paramInt1, int paramInt2)
  {
    bbwi.needed = paramInt2;
    int i = bbwi.position();
    bbwi = bufferManagerRead.underflow(bbwi);
    if (bbwi.fragmented)
    {
      fragmentOffset += i - bbwi.position();
      markAndResetHandler.fragmentationOccured(bbwi);
      bbwi.fragmented = false;
    }
  }
  
  public Object createStreamMemento()
  {
    return new FragmentableStreamMemento();
  }
  
  public void restoreInternalState(Object paramObject)
  {
    super.restoreInternalState(paramObject);
    fragmentOffset = fragmentOffset_;
  }
  
  public char read_wchar()
  {
    alignAndCheck(2, 2);
    char[] arrayOfChar = getConvertedChars(2, getWCharConverter());
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
    i -= 1;
    char[] arrayOfChar = getConvertedChars(i * 2, getWCharConverter());
    read_short();
    return new String(arrayOfChar, 0, getWCharConverter().getNumChars());
  }
  
  private class FragmentableStreamMemento
    extends CDRInputStream_1_0.StreamMemento
  {
    private int fragmentOffset_ = fragmentOffset;
    
    public FragmentableStreamMemento()
    {
      super();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\encoding\CDRInputStream_1_1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */