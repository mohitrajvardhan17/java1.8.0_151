package com.sun.jmx.snmp.daemon;

import javax.management.JMRuntimeException;

public class CommunicationException
  extends JMRuntimeException
{
  private static final long serialVersionUID = -2499186113233316177L;
  
  public CommunicationException(Throwable paramThrowable)
  {
    super(paramThrowable.getMessage());
    initCause(paramThrowable);
  }
  
  public CommunicationException(Throwable paramThrowable, String paramString)
  {
    super(paramString);
    initCause(paramThrowable);
  }
  
  public CommunicationException(String paramString)
  {
    super(paramString);
  }
  
  public Throwable getTargetException()
  {
    return getCause();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\daemon\CommunicationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */