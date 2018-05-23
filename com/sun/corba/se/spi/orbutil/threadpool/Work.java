package com.sun.corba.se.spi.orbutil.threadpool;

public abstract interface Work
{
  public abstract void doWork();
  
  public abstract void setEnqueueTime(long paramLong);
  
  public abstract long getEnqueueTime();
  
  public abstract String getName();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\orbutil\threadpool\Work.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */