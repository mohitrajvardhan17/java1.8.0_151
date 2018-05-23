package javax.xml.bind;

public class MarshalException
  extends JAXBException
{
  public MarshalException(String paramString)
  {
    this(paramString, null, null);
  }
  
  public MarshalException(String paramString1, String paramString2)
  {
    this(paramString1, paramString2, null);
  }
  
  public MarshalException(Throwable paramThrowable)
  {
    this(null, null, paramThrowable);
  }
  
  public MarshalException(String paramString, Throwable paramThrowable)
  {
    this(paramString, null, paramThrowable);
  }
  
  public MarshalException(String paramString1, String paramString2, Throwable paramThrowable)
  {
    super(paramString1, paramString2, paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\bind\MarshalException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */