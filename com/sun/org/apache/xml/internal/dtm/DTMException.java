package com.sun.org.apache.xml.internal.dtm;

public class DTMException
  extends RuntimeException
{
  static final long serialVersionUID = -775576419181334734L;
  
  public DTMException(String paramString)
  {
    super(paramString);
  }
  
  public DTMException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  public DTMException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\dtm\DTMException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */