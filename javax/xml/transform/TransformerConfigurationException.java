package javax.xml.transform;

public class TransformerConfigurationException
  extends TransformerException
{
  private static final long serialVersionUID = 1285547467942875745L;
  
  public TransformerConfigurationException()
  {
    super("Configuration Error");
  }
  
  public TransformerConfigurationException(String paramString)
  {
    super(paramString);
  }
  
  public TransformerConfigurationException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  public TransformerConfigurationException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public TransformerConfigurationException(String paramString, SourceLocator paramSourceLocator)
  {
    super(paramString, paramSourceLocator);
  }
  
  public TransformerConfigurationException(String paramString, SourceLocator paramSourceLocator, Throwable paramThrowable)
  {
    super(paramString, paramSourceLocator, paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\transform\TransformerConfigurationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */