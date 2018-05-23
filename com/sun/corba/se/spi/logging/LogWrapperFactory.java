package com.sun.corba.se.spi.logging;

import java.util.logging.Logger;

public abstract interface LogWrapperFactory
{
  public abstract LogWrapperBase create(Logger paramLogger);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\logging\LogWrapperFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */