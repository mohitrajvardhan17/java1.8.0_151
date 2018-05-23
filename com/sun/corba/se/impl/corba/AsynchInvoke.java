package com.sun.corba.se.impl.corba;

import com.sun.corba.se.spi.orb.ORB;

public class AsynchInvoke
  implements Runnable
{
  private RequestImpl _req;
  private ORB _orb;
  private boolean _notifyORB;
  
  public AsynchInvoke(ORB paramORB, RequestImpl paramRequestImpl, boolean paramBoolean)
  {
    _orb = paramORB;
    _req = paramRequestImpl;
    _notifyORB = paramBoolean;
  }
  
  public void run()
  {
    _req.doInvocation();
    synchronized (_req)
    {
      _req.gotResponse = true;
      _req.notify();
    }
    if (_notifyORB == true) {
      _orb.notifyORB();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\corba\AsynchInvoke.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */