package org.w3c.dom;

public abstract interface DOMError
{
  public static final short SEVERITY_WARNING = 1;
  public static final short SEVERITY_ERROR = 2;
  public static final short SEVERITY_FATAL_ERROR = 3;
  
  public abstract short getSeverity();
  
  public abstract String getMessage();
  
  public abstract String getType();
  
  public abstract Object getRelatedException();
  
  public abstract Object getRelatedData();
  
  public abstract DOMLocator getLocation();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\DOMError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */