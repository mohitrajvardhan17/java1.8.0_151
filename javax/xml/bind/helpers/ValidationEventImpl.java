package javax.xml.bind.helpers;

import java.text.MessageFormat;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;

public class ValidationEventImpl
  implements ValidationEvent
{
  private int severity;
  private String message;
  private Throwable linkedException;
  private ValidationEventLocator locator;
  
  public ValidationEventImpl(int paramInt, String paramString, ValidationEventLocator paramValidationEventLocator)
  {
    this(paramInt, paramString, paramValidationEventLocator, null);
  }
  
  public ValidationEventImpl(int paramInt, String paramString, ValidationEventLocator paramValidationEventLocator, Throwable paramThrowable)
  {
    setSeverity(paramInt);
    message = paramString;
    locator = paramValidationEventLocator;
    linkedException = paramThrowable;
  }
  
  public int getSeverity()
  {
    return severity;
  }
  
  public void setSeverity(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1) && (paramInt != 2)) {
      throw new IllegalArgumentException(Messages.format("ValidationEventImpl.IllegalSeverity"));
    }
    severity = paramInt;
  }
  
  public String getMessage()
  {
    return message;
  }
  
  public void setMessage(String paramString)
  {
    message = paramString;
  }
  
  public Throwable getLinkedException()
  {
    return linkedException;
  }
  
  public void setLinkedException(Throwable paramThrowable)
  {
    linkedException = paramThrowable;
  }
  
  public ValidationEventLocator getLocator()
  {
    return locator;
  }
  
  public void setLocator(ValidationEventLocator paramValidationEventLocator)
  {
    locator = paramValidationEventLocator;
  }
  
  public String toString()
  {
    String str;
    switch (getSeverity())
    {
    case 0: 
      str = "WARNING";
      break;
    case 1: 
      str = "ERROR";
      break;
    case 2: 
      str = "FATAL_ERROR";
      break;
    default: 
      str = String.valueOf(getSeverity());
    }
    return MessageFormat.format("[severity={0},message={1},locator={2}]", new Object[] { str, getMessage(), getLocator() });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\bind\helpers\ValidationEventImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */