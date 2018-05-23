package com.sun.org.apache.xml.internal.resolver;

public class CatalogException
  extends Exception
{
  public static final int WRAPPER = 1;
  public static final int INVALID_ENTRY = 2;
  public static final int INVALID_ENTRY_TYPE = 3;
  public static final int NO_XML_PARSER = 4;
  public static final int UNKNOWN_FORMAT = 5;
  public static final int UNPARSEABLE = 6;
  public static final int PARSE_FAILED = 7;
  public static final int UNENDED_COMMENT = 8;
  private Exception exception = null;
  private int exceptionType = 0;
  
  public CatalogException(int paramInt, String paramString)
  {
    super(paramString);
    exceptionType = paramInt;
    exception = null;
  }
  
  public CatalogException(int paramInt)
  {
    super("Catalog Exception " + paramInt);
    exceptionType = paramInt;
    exception = null;
  }
  
  public CatalogException(Exception paramException)
  {
    exceptionType = 1;
    exception = paramException;
  }
  
  public CatalogException(String paramString, Exception paramException)
  {
    super(paramString);
    exceptionType = 1;
    exception = paramException;
  }
  
  public String getMessage()
  {
    String str = super.getMessage();
    if ((str == null) && (exception != null)) {
      return exception.getMessage();
    }
    return str;
  }
  
  public Exception getException()
  {
    return exception;
  }
  
  public int getExceptionType()
  {
    return exceptionType;
  }
  
  public String toString()
  {
    if (exception != null) {
      return exception.toString();
    }
    return super.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\resolver\CatalogException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */