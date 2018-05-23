package com.sun.jndi.ldap;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.naming.CommunicationException;

final class LdapRequest
{
  LdapRequest next;
  int msgId;
  private int gotten = 0;
  private BlockingQueue<BerDecoder> replies;
  private int highWatermark = -1;
  private boolean cancelled = false;
  private boolean pauseAfterReceipt = false;
  private boolean completed = false;
  
  LdapRequest(int paramInt, boolean paramBoolean)
  {
    this(paramInt, paramBoolean, -1);
  }
  
  LdapRequest(int paramInt1, boolean paramBoolean, int paramInt2)
  {
    msgId = paramInt1;
    pauseAfterReceipt = paramBoolean;
    if (paramInt2 == -1)
    {
      replies = new LinkedBlockingQueue();
    }
    else
    {
      replies = new LinkedBlockingQueue(paramInt2);
      highWatermark = (paramInt2 * 80 / 100);
    }
  }
  
  synchronized void cancel()
  {
    cancelled = true;
    notify();
  }
  
  synchronized boolean addReplyBer(BerDecoder paramBerDecoder)
  {
    if (cancelled) {
      return false;
    }
    try
    {
      replies.put(paramBerDecoder);
    }
    catch (InterruptedException localInterruptedException) {}
    try
    {
      paramBerDecoder.parseSeq(null);
      paramBerDecoder.parseInt();
      completed = (paramBerDecoder.peekByte() == 101);
    }
    catch (IOException localIOException) {}
    paramBerDecoder.reset();
    notify();
    if ((highWatermark != -1) && (replies.size() >= highWatermark)) {
      return true;
    }
    return pauseAfterReceipt;
  }
  
  synchronized BerDecoder getReplyBer()
    throws CommunicationException
  {
    if (cancelled) {
      throw new CommunicationException("Request: " + msgId + " cancelled");
    }
    BerDecoder localBerDecoder = (BerDecoder)replies.poll();
    return localBerDecoder;
  }
  
  synchronized boolean hasSearchCompleted()
  {
    return completed;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\LdapRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */