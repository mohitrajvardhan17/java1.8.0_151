package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.pept.protocol.ClientInvocationInfo;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Iterator;

public class CorbaInvocationInfo
  implements ClientInvocationInfo
{
  private boolean isRetryInvocation;
  private int entryCount;
  private ORB orb;
  private Iterator contactInfoListIterator;
  private ClientRequestDispatcher clientRequestDispatcher;
  private MessageMediator messageMediator;
  
  private CorbaInvocationInfo() {}
  
  public CorbaInvocationInfo(ORB paramORB)
  {
    orb = paramORB;
    isRetryInvocation = false;
    entryCount = 0;
  }
  
  public Iterator getContactInfoListIterator()
  {
    return contactInfoListIterator;
  }
  
  public void setContactInfoListIterator(Iterator paramIterator)
  {
    contactInfoListIterator = paramIterator;
  }
  
  public boolean isRetryInvocation()
  {
    return isRetryInvocation;
  }
  
  public void setIsRetryInvocation(boolean paramBoolean)
  {
    isRetryInvocation = paramBoolean;
  }
  
  public int getEntryCount()
  {
    return entryCount;
  }
  
  public void incrementEntryCount()
  {
    entryCount += 1;
  }
  
  public void decrementEntryCount()
  {
    entryCount -= 1;
  }
  
  public void setClientRequestDispatcher(ClientRequestDispatcher paramClientRequestDispatcher)
  {
    clientRequestDispatcher = paramClientRequestDispatcher;
  }
  
  public ClientRequestDispatcher getClientRequestDispatcher()
  {
    return clientRequestDispatcher;
  }
  
  public void setMessageMediator(MessageMediator paramMessageMediator)
  {
    messageMediator = paramMessageMediator;
  }
  
  public MessageMediator getMessageMediator()
  {
    return messageMediator;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\CorbaInvocationInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */