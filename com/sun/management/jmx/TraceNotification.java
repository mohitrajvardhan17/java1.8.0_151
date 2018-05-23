package com.sun.management.jmx;

import javax.management.Notification;

@Deprecated
public class TraceNotification
  extends Notification
{
  public int level;
  public int type;
  public String className;
  public String methodName;
  public String info;
  public Throwable exception;
  public long globalSequenceNumber;
  public long sequenceNumber;
  
  public TraceNotification(Object paramObject, long paramLong1, long paramLong2, int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, Throwable paramThrowable)
  {
    super(null, paramObject, paramLong1);
    sequenceNumber = paramLong1;
    globalSequenceNumber = paramLong2;
    level = paramInt1;
    type = paramInt2;
    className = (paramString1 != null ? paramString1 : "");
    methodName = (paramString2 != null ? paramString2 : "");
    info = (paramString3 != null ? paramString3 : null);
    exception = paramThrowable;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\management\jmx\TraceNotification.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */