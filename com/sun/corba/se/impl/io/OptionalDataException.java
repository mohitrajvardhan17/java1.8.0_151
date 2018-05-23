package com.sun.corba.se.impl.io;

import java.io.IOException;

public class OptionalDataException
  extends IOException
{
  public int length;
  public boolean eof;
  
  OptionalDataException(int paramInt)
  {
    eof = false;
    length = paramInt;
  }
  
  OptionalDataException(boolean paramBoolean)
  {
    length = 0;
    eof = paramBoolean;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\io\OptionalDataException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */