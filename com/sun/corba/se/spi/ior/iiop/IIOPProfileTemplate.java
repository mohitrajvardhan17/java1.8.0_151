package com.sun.corba.se.spi.ior.iiop;

import com.sun.corba.se.spi.ior.TaggedProfileTemplate;

public abstract interface IIOPProfileTemplate
  extends TaggedProfileTemplate
{
  public abstract GIOPVersion getGIOPVersion();
  
  public abstract IIOPAddress getPrimaryAddress();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\ior\iiop\IIOPProfileTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */