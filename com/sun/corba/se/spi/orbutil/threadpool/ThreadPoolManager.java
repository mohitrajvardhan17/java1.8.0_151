package com.sun.corba.se.spi.orbutil.threadpool;

import java.io.Closeable;

public abstract interface ThreadPoolManager
  extends Closeable
{
  public abstract ThreadPool getThreadPool(String paramString)
    throws NoSuchThreadPoolException;
  
  public abstract ThreadPool getThreadPool(int paramInt)
    throws NoSuchThreadPoolException;
  
  public abstract int getThreadPoolNumericId(String paramString);
  
  public abstract String getThreadPoolStringId(int paramInt);
  
  public abstract ThreadPool getDefaultThreadPool();
  
  public abstract ThreadPoolChooser getThreadPoolChooser(String paramString);
  
  public abstract ThreadPoolChooser getThreadPoolChooser(int paramInt);
  
  public abstract void setThreadPoolChooser(String paramString, ThreadPoolChooser paramThreadPoolChooser);
  
  public abstract int getThreadPoolChooserNumericId(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\orbutil\threadpool\ThreadPoolManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */