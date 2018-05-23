package com.sun.corba.se.pept.transport;

import com.sun.corba.se.spi.orbutil.threadpool.Work;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

public abstract interface EventHandler
{
  public abstract void setUseSelectThreadToWait(boolean paramBoolean);
  
  public abstract boolean shouldUseSelectThreadToWait();
  
  public abstract SelectableChannel getChannel();
  
  public abstract int getInterestOps();
  
  public abstract void setSelectionKey(SelectionKey paramSelectionKey);
  
  public abstract SelectionKey getSelectionKey();
  
  public abstract void handleEvent();
  
  public abstract void setUseWorkerThreadForEvent(boolean paramBoolean);
  
  public abstract boolean shouldUseWorkerThreadForEvent();
  
  public abstract void setWork(Work paramWork);
  
  public abstract Work getWork();
  
  public abstract Acceptor getAcceptor();
  
  public abstract Connection getConnection();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\pept\transport\EventHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */