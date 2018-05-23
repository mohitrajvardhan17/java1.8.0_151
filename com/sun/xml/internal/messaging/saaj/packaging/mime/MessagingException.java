package com.sun.xml.internal.messaging.saaj.packaging.mime;

public class MessagingException
  extends Exception
{
  private Exception next;
  
  public MessagingException() {}
  
  public MessagingException(String paramString)
  {
    super(paramString);
  }
  
  public MessagingException(String paramString, Exception paramException)
  {
    super(paramString);
    next = paramException;
  }
  
  public Exception getNextException()
  {
    return next;
  }
  
  public synchronized boolean setNextException(Exception paramException)
  {
    for (Object localObject = this; ((localObject instanceof MessagingException)) && (next != null); localObject = next) {}
    if ((localObject instanceof MessagingException))
    {
      next = paramException;
      return true;
    }
    return false;
  }
  
  public String getMessage()
  {
    if (next == null) {
      return super.getMessage();
    }
    Exception localException = next;
    String str1 = super.getMessage();
    StringBuffer localStringBuffer = new StringBuffer(str1 == null ? "" : str1);
    while (localException != null)
    {
      localStringBuffer.append(";\n  nested exception is:\n\t");
      if ((localException instanceof MessagingException))
      {
        MessagingException localMessagingException = (MessagingException)localException;
        localStringBuffer.append(localException.getClass().toString());
        String str2 = localMessagingException.getSuperMessage();
        if (str2 != null)
        {
          localStringBuffer.append(": ");
          localStringBuffer.append(str2);
        }
        localException = next;
      }
      else
      {
        localStringBuffer.append(localException.toString());
        localException = null;
      }
    }
    return localStringBuffer.toString();
  }
  
  private String getSuperMessage()
  {
    return super.getMessage();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\MessagingException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */