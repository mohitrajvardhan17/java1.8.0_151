package com.sun.jndi.cosnaming;

import org.omg.CORBA.ORB;

class OrbReuseTracker
{
  int referenceCnt;
  ORB orb;
  private static final boolean debug = false;
  
  OrbReuseTracker(ORB paramORB)
  {
    orb = paramORB;
    referenceCnt += 1;
  }
  
  synchronized void incRefCount()
  {
    referenceCnt += 1;
  }
  
  synchronized void decRefCount()
  {
    referenceCnt -= 1;
    if (referenceCnt == 0) {
      orb.destroy();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\cosnaming\OrbReuseTracker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */