package com.sun.corba.se.spi.orbutil.threadpool;

public abstract interface ThreadPoolChooser
{
  public abstract ThreadPool getThreadPool();
  
  public abstract ThreadPool getThreadPool(int paramInt);
  
  public abstract String[] getThreadPoolIds();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\orbutil\threadpool\ThreadPoolChooser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */