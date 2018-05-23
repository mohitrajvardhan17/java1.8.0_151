package javax.management;

public class MBeanRegistrationException
  extends MBeanException
{
  private static final long serialVersionUID = 4482382455277067805L;
  
  public MBeanRegistrationException(Exception paramException)
  {
    super(paramException);
  }
  
  public MBeanRegistrationException(Exception paramException, String paramString)
  {
    super(paramException, paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\MBeanRegistrationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */