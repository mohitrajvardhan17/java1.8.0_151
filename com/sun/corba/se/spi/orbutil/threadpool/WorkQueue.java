package com.sun.corba.se.spi.orbutil.threadpool;

public abstract interface WorkQueue
{
  public abstract void addWork(Work paramWork);
  
  public abstract String getName();
  
  public abstract long totalWorkItemsAdded();
  
  public abstract int workItemsInQueue();
  
  public abstract long averageTimeInQueue();
  
  public abstract void setThreadPool(ThreadPool paramThreadPool);
  
  public abstract ThreadPool getThreadPool();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\orbutil\threadpool\WorkQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */