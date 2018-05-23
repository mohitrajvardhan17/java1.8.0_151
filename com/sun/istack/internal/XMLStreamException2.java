package com.sun.istack.internal;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

public class XMLStreamException2
  extends XMLStreamException
{
  public XMLStreamException2(String paramString)
  {
    super(paramString);
  }
  
  public XMLStreamException2(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  public XMLStreamException2(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public XMLStreamException2(String paramString, Location paramLocation)
  {
    super(paramString, paramLocation);
  }
  
  public XMLStreamException2(String paramString, Location paramLocation, Throwable paramThrowable)
  {
    super(paramString, paramLocation, paramThrowable);
  }
  
  public Throwable getCause()
  {
    return getNestedException();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\istack\internal\XMLStreamException2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */