package com.sun.corba.se.impl.activation;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

public class ProcessMonitorThread
  extends Thread
{
  private HashMap serverTable;
  private int sleepTime;
  private static ProcessMonitorThread instance = null;
  
  private ProcessMonitorThread(HashMap paramHashMap, int paramInt)
  {
    serverTable = paramHashMap;
    sleepTime = paramInt;
  }
  
  public void run()
  {
    for (;;)
    {
      try
      {
        Thread.sleep(sleepTime);
      }
      catch (InterruptedException localInterruptedException)
      {
        break;
      }
      Iterator localIterator;
      synchronized (serverTable)
      {
        localIterator = serverTable.values().iterator();
      }
      try
      {
        checkServerHealth(localIterator);
      }
      catch (ConcurrentModificationException localConcurrentModificationException)
      {
        break;
      }
    }
  }
  
  private void checkServerHealth(Iterator paramIterator)
  {
    if (paramIterator == null) {
      return;
    }
    while (paramIterator.hasNext())
    {
      ServerTableEntry localServerTableEntry = (ServerTableEntry)paramIterator.next();
      localServerTableEntry.checkProcessHealth();
    }
  }
  
  static void start(HashMap paramHashMap)
  {
    int i = 1000;
    String str = System.getProperties().getProperty("com.sun.CORBA.activation.ServerPollingTime");
    if (str != null) {
      try
      {
        i = Integer.parseInt(str);
      }
      catch (Exception localException) {}
    }
    instance = new ProcessMonitorThread(paramHashMap, i);
    instance.setDaemon(true);
    instance.start();
  }
  
  static void interruptThread()
  {
    instance.interrupt();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\activation\ProcessMonitorThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */