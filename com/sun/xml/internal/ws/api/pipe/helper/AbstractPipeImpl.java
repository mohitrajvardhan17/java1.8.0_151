package com.sun.xml.internal.ws.api.pipe.helper;

import com.sun.xml.internal.ws.api.pipe.Pipe;
import com.sun.xml.internal.ws.api.pipe.PipeCloner;

public abstract class AbstractPipeImpl
  implements Pipe
{
  protected AbstractPipeImpl() {}
  
  protected AbstractPipeImpl(Pipe paramPipe, PipeCloner paramPipeCloner)
  {
    paramPipeCloner.add(paramPipe, this);
  }
  
  public void preDestroy() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\pipe\helper\AbstractPipeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */