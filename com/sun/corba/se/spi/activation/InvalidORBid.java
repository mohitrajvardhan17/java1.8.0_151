package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class InvalidORBid
  extends UserException
{
  public InvalidORBid()
  {
    super(InvalidORBidHelper.id());
  }
  
  public InvalidORBid(String paramString)
  {
    super(InvalidORBidHelper.id() + "  " + paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\InvalidORBid.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */