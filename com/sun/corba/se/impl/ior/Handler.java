package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import org.omg.CORBA.OctetSeqHolder;
import org.omg.CORBA_2_3.portable.InputStream;

abstract interface Handler
{
  public abstract ObjectKeyTemplate handle(int paramInt1, int paramInt2, InputStream paramInputStream, OctetSeqHolder paramOctetSeqHolder);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\Handler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */