package com.sun.corba.se.pept.protocol;

import java.util.Iterator;

public abstract interface ClientInvocationInfo
{
  public abstract Iterator getContactInfoListIterator();
  
  public abstract void setContactInfoListIterator(Iterator paramIterator);
  
  public abstract boolean isRetryInvocation();
  
  public abstract void setIsRetryInvocation(boolean paramBoolean);
  
  public abstract int getEntryCount();
  
  public abstract void incrementEntryCount();
  
  public abstract void decrementEntryCount();
  
  public abstract void setClientRequestDispatcher(ClientRequestDispatcher paramClientRequestDispatcher);
  
  public abstract ClientRequestDispatcher getClientRequestDispatcher();
  
  public abstract void setMessageMediator(MessageMediator paramMessageMediator);
  
  public abstract MessageMediator getMessageMediator();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\pept\protocol\ClientInvocationInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */