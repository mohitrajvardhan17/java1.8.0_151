package javax.xml.stream;

public class XMLStreamException
  extends Exception
{
  protected Throwable nested;
  protected Location location;
  
  public XMLStreamException() {}
  
  public XMLStreamException(String paramString)
  {
    super(paramString);
  }
  
  public XMLStreamException(Throwable paramThrowable)
  {
    super(paramThrowable);
    nested = paramThrowable;
  }
  
  public XMLStreamException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
    nested = paramThrowable;
  }
  
  public XMLStreamException(String paramString, Location paramLocation, Throwable paramThrowable)
  {
    super("ParseError at [row,col]:[" + paramLocation.getLineNumber() + "," + paramLocation.getColumnNumber() + "]\nMessage: " + paramString);
    nested = paramThrowable;
    location = paramLocation;
  }
  
  public XMLStreamException(String paramString, Location paramLocation)
  {
    super("ParseError at [row,col]:[" + paramLocation.getLineNumber() + "," + paramLocation.getColumnNumber() + "]\nMessage: " + paramString);
    location = paramLocation;
  }
  
  public Throwable getNestedException()
  {
    return nested;
  }
  
  public Location getLocation()
  {
    return location;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\stream\XMLStreamException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */