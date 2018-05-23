package com.sun.corba.se.impl.protocol;

public class AddressingDispositionException
  extends RuntimeException
{
  private short expectedAddrDisp = 0;
  
  public AddressingDispositionException(short paramShort)
  {
    expectedAddrDisp = paramShort;
  }
  
  public short expectedAddrDisp()
  {
    return expectedAddrDisp;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\AddressingDispositionException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */