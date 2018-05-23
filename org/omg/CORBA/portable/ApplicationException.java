package org.omg.CORBA.portable;

public class ApplicationException
  extends Exception
{
  private String id;
  private InputStream ins;
  
  public ApplicationException(String paramString, InputStream paramInputStream)
  {
    id = paramString;
    ins = paramInputStream;
  }
  
  public String getId()
  {
    return id;
  }
  
  public InputStream getInputStream()
  {
    return ins;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\portable\ApplicationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */