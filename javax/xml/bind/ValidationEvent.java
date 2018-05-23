package javax.xml.bind;

public abstract interface ValidationEvent
{
  public static final int WARNING = 0;
  public static final int ERROR = 1;
  public static final int FATAL_ERROR = 2;
  
  public abstract int getSeverity();
  
  public abstract String getMessage();
  
  public abstract Throwable getLinkedException();
  
  public abstract ValidationEventLocator getLocator();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\bind\ValidationEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */