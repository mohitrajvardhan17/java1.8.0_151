package javax.naming.spi;

import java.io.Serializable;
import javax.naming.CompositeName;
import javax.naming.InvalidNameException;
import javax.naming.Name;

public class ResolveResult
  implements Serializable
{
  protected Object resolvedObj;
  protected Name remainingName;
  private static final long serialVersionUID = -4552108072002407559L;
  
  protected ResolveResult()
  {
    resolvedObj = null;
    remainingName = null;
  }
  
  public ResolveResult(Object paramObject, String paramString)
  {
    resolvedObj = paramObject;
    try
    {
      remainingName = new CompositeName(paramString);
    }
    catch (InvalidNameException localInvalidNameException) {}
  }
  
  public ResolveResult(Object paramObject, Name paramName)
  {
    resolvedObj = paramObject;
    setRemainingName(paramName);
  }
  
  public Name getRemainingName()
  {
    return remainingName;
  }
  
  public Object getResolvedObj()
  {
    return resolvedObj;
  }
  
  public void setRemainingName(Name paramName)
  {
    if (paramName != null) {
      remainingName = ((Name)paramName.clone());
    } else {
      remainingName = null;
    }
  }
  
  public void appendRemainingName(Name paramName)
  {
    if (paramName != null) {
      if (remainingName != null) {
        try
        {
          remainingName.addAll(paramName);
        }
        catch (InvalidNameException localInvalidNameException) {}
      } else {
        remainingName = ((Name)paramName.clone());
      }
    }
  }
  
  public void appendRemainingComponent(String paramString)
  {
    if (paramString != null)
    {
      CompositeName localCompositeName = new CompositeName();
      try
      {
        localCompositeName.add(paramString);
      }
      catch (InvalidNameException localInvalidNameException) {}
      appendRemainingName(localCompositeName);
    }
  }
  
  public void setResolvedObj(Object paramObject)
  {
    resolvedObj = paramObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\spi\ResolveResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */