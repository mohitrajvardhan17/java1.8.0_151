package com.sun.corba.se.impl.transport;

import com.sun.corba.se.spi.transport.ReadTimeouts;

public class ReadTCPTimeoutsImpl
  implements ReadTimeouts
{
  private int initial_time_to_wait;
  private int max_time_to_wait;
  private int max_giop_header_time_to_wait;
  private double backoff_factor;
  
  public ReadTCPTimeoutsImpl(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    initial_time_to_wait = paramInt1;
    max_time_to_wait = paramInt2;
    max_giop_header_time_to_wait = paramInt3;
    backoff_factor = (1.0D + paramInt4 / 100.0D);
  }
  
  public int get_initial_time_to_wait()
  {
    return initial_time_to_wait;
  }
  
  public int get_max_time_to_wait()
  {
    return max_time_to_wait;
  }
  
  public double get_backoff_factor()
  {
    return backoff_factor;
  }
  
  public int get_max_giop_header_time_to_wait()
  {
    return max_giop_header_time_to_wait;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\transport\ReadTCPTimeoutsImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */