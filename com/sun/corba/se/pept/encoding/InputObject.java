package com.sun.corba.se.pept.encoding;

import com.sun.corba.se.pept.protocol.MessageMediator;
import java.io.IOException;

public abstract interface InputObject
{
  public abstract void setMessageMediator(MessageMediator paramMessageMediator);
  
  public abstract MessageMediator getMessageMediator();
  
  public abstract void close()
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\pept\encoding\InputObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */