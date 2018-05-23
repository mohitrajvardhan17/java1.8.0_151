package javax.naming;

public class LinkLoopException
  extends LinkException
{
  private static final long serialVersionUID = -3119189944325198009L;
  
  public LinkLoopException(String paramString)
  {
    super(paramString);
  }
  
  public LinkLoopException() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\LinkLoopException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */