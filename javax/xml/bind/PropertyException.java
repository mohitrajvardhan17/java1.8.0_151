package javax.xml.bind;

public class PropertyException
  extends JAXBException
{
  public PropertyException(String paramString)
  {
    super(paramString);
  }
  
  public PropertyException(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
  }
  
  public PropertyException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  public PropertyException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public PropertyException(String paramString1, String paramString2, Throwable paramThrowable)
  {
    super(paramString1, paramString2, paramThrowable);
  }
  
  public PropertyException(String paramString, Object paramObject)
  {
    super(Messages.format("PropertyException.NameValue", paramString, paramObject.toString()));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\bind\PropertyException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */