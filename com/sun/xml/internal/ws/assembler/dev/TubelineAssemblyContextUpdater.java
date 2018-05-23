package com.sun.xml.internal.ws.assembler.dev;

import javax.xml.ws.WebServiceException;

public abstract interface TubelineAssemblyContextUpdater
{
  public abstract void prepareContext(ClientTubelineAssemblyContext paramClientTubelineAssemblyContext)
    throws WebServiceException;
  
  public abstract void prepareContext(ServerTubelineAssemblyContext paramServerTubelineAssemblyContext)
    throws WebServiceException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\assembler\dev\TubelineAssemblyContextUpdater.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */