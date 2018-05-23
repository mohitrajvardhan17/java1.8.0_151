package com.sun.corba.se.impl.javax.rmi.CORBA;

class KeepAlive
  extends Thread
{
  boolean quit = false;
  
  public KeepAlive()
  {
    setDaemon(false);
  }
  
  public synchronized void run()
  {
    while (!quit) {
      try
      {
        wait();
      }
      catch (InterruptedException localInterruptedException) {}
    }
  }
  
  public synchronized void quit()
  {
    quit = true;
    notifyAll();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\javax\rmi\CORBA\KeepAlive.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */