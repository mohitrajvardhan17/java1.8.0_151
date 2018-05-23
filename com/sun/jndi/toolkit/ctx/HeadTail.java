package com.sun.jndi.toolkit.ctx;

import javax.naming.Name;

public class HeadTail
{
  private int status;
  private Name head;
  private Name tail;
  
  public HeadTail(Name paramName1, Name paramName2)
  {
    this(paramName1, paramName2, 0);
  }
  
  public HeadTail(Name paramName1, Name paramName2, int paramInt)
  {
    status = paramInt;
    head = paramName1;
    tail = paramName2;
  }
  
  public void setStatus(int paramInt)
  {
    status = paramInt;
  }
  
  public Name getHead()
  {
    return head;
  }
  
  public Name getTail()
  {
    return tail;
  }
  
  public int getStatus()
  {
    return status;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\toolkit\ctx\HeadTail.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */