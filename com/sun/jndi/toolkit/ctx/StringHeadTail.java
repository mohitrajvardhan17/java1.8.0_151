package com.sun.jndi.toolkit.ctx;

public class StringHeadTail
{
  private int status;
  private String head;
  private String tail;
  
  public StringHeadTail(String paramString1, String paramString2)
  {
    this(paramString1, paramString2, 0);
  }
  
  public StringHeadTail(String paramString1, String paramString2, int paramInt)
  {
    status = paramInt;
    head = paramString1;
    tail = paramString2;
  }
  
  public void setStatus(int paramInt)
  {
    status = paramInt;
  }
  
  public String getHead()
  {
    return head;
  }
  
  public String getTail()
  {
    return tail;
  }
  
  public int getStatus()
  {
    return status;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\toolkit\ctx\StringHeadTail.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */