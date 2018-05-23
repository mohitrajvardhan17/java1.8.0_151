package com.sun.corba.se.spi.legacy.interceptor;

import com.sun.corba.se.spi.oa.ObjectAdapter;

public abstract interface IORInfoExt
{
  public abstract int getServerPort(String paramString)
    throws UnknownType;
  
  public abstract ObjectAdapter getObjectAdapter();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\legacy\interceptor\IORInfoExt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */