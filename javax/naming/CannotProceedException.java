package javax.naming;

import java.util.Hashtable;

public class CannotProceedException
  extends NamingException
{
  protected Name remainingNewName = null;
  protected Hashtable<?, ?> environment = null;
  protected Name altName = null;
  protected Context altNameCtx = null;
  private static final long serialVersionUID = 1219724816191576813L;
  
  public CannotProceedException(String paramString)
  {
    super(paramString);
  }
  
  public CannotProceedException() {}
  
  public Hashtable<?, ?> getEnvironment()
  {
    return environment;
  }
  
  public void setEnvironment(Hashtable<?, ?> paramHashtable)
  {
    environment = paramHashtable;
  }
  
  public Name getRemainingNewName()
  {
    return remainingNewName;
  }
  
  public void setRemainingNewName(Name paramName)
  {
    if (paramName != null) {
      remainingNewName = ((Name)paramName.clone());
    } else {
      remainingNewName = null;
    }
  }
  
  public Name getAltName()
  {
    return altName;
  }
  
  public void setAltName(Name paramName)
  {
    altName = paramName;
  }
  
  public Context getAltNameCtx()
  {
    return altNameCtx;
  }
  
  public void setAltNameCtx(Context paramContext)
  {
    altNameCtx = paramContext;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\CannotProceedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */