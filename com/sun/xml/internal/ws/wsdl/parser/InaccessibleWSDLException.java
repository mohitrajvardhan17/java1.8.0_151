package com.sun.xml.internal.ws.wsdl.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.ws.WebServiceException;

public class InaccessibleWSDLException
  extends WebServiceException
{
  private final List<Throwable> errors;
  private static final long serialVersionUID = 1L;
  
  public InaccessibleWSDLException(List<Throwable> paramList)
  {
    super(paramList.size() + " counts of InaccessibleWSDLException.\n");
    assert (!paramList.isEmpty()) : "there must be at least one error";
    errors = Collections.unmodifiableList(new ArrayList(paramList));
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(super.toString());
    localStringBuilder.append('\n');
    Iterator localIterator = errors.iterator();
    while (localIterator.hasNext())
    {
      Throwable localThrowable = (Throwable)localIterator.next();
      localStringBuilder.append(localThrowable.toString()).append('\n');
    }
    return localStringBuilder.toString();
  }
  
  public List<Throwable> getErrors()
  {
    return errors;
  }
  
  public static class Builder
    implements ErrorHandler
  {
    private final List<Throwable> list = new ArrayList();
    
    public Builder() {}
    
    public void error(Throwable paramThrowable)
    {
      list.add(paramThrowable);
    }
    
    public void check()
      throws InaccessibleWSDLException
    {
      if (list.isEmpty()) {
        return;
      }
      throw new InaccessibleWSDLException(list);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\wsdl\parser\InaccessibleWSDLException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */