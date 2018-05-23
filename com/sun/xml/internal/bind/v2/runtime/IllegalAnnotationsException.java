package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.v2.model.core.ErrorHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.JAXBException;

public class IllegalAnnotationsException
  extends JAXBException
{
  private final List<IllegalAnnotationException> errors;
  private static final long serialVersionUID = 1L;
  
  public IllegalAnnotationsException(List<IllegalAnnotationException> paramList)
  {
    super(paramList.size() + " counts of IllegalAnnotationExceptions");
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
      IllegalAnnotationException localIllegalAnnotationException = (IllegalAnnotationException)localIterator.next();
      localStringBuilder.append(localIllegalAnnotationException.toString()).append('\n');
    }
    return localStringBuilder.toString();
  }
  
  public List<IllegalAnnotationException> getErrors()
  {
    return errors;
  }
  
  public static class Builder
    implements ErrorHandler
  {
    private final List<IllegalAnnotationException> list = new ArrayList();
    
    public Builder() {}
    
    public void error(IllegalAnnotationException paramIllegalAnnotationException)
    {
      list.add(paramIllegalAnnotationException);
    }
    
    public void check()
      throws IllegalAnnotationsException
    {
      if (list.isEmpty()) {
        return;
      }
      throw new IllegalAnnotationsException(list);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\IllegalAnnotationsException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */