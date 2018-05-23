package com.sun.corba.se.spi.transport;

public abstract interface ReadTimeouts
{
  public abstract int get_initial_time_to_wait();
  
  public abstract int get_max_time_to_wait();
  
  public abstract double get_backoff_factor();
  
  public abstract int get_max_giop_header_time_to_wait();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\transport\ReadTimeouts.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */