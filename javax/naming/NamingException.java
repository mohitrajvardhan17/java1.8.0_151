package javax.naming;

public class NamingException
  extends Exception
{
  protected Name resolvedName = remainingName = null;
  protected Object resolvedObj = null;
  protected Name remainingName;
  protected Throwable rootException = null;
  private static final long serialVersionUID = -1299181962103167177L;
  
  public NamingException(String paramString)
  {
    super(paramString);
  }
  
  public NamingException() {}
  
  public Name getResolvedName()
  {
    return resolvedName;
  }
  
  public Name getRemainingName()
  {
    return remainingName;
  }
  
  public Object getResolvedObj()
  {
    return resolvedObj;
  }
  
  public String getExplanation()
  {
    return getMessage();
  }
  
  public void setResolvedName(Name paramName)
  {
    if (paramName != null) {
      resolvedName = ((Name)paramName.clone());
    } else {
      resolvedName = null;
    }
  }
  
  public void setRemainingName(Name paramName)
  {
    if (paramName != null) {
      remainingName = ((Name)paramName.clone());
    } else {
      remainingName = null;
    }
  }
  
  public void setResolvedObj(Object paramObject)
  {
    resolvedObj = paramObject;
  }
  
  public void appendRemainingComponent(String paramString)
  {
    if (paramString != null) {
      try
      {
        if (remainingName == null) {
          remainingName = new CompositeName();
        }
        remainingName.add(paramString);
      }
      catch (NamingException localNamingException)
      {
        throw new IllegalArgumentException(localNamingException.toString());
      }
    }
  }
  
  public void appendRemainingName(Name paramName)
  {
    if (paramName == null) {
      return;
    }
    if (remainingName != null) {
      try
      {
        remainingName.addAll(paramName);
      }
      catch (NamingException localNamingException)
      {
        throw new IllegalArgumentException(localNamingException.toString());
      }
    } else {
      remainingName = ((Name)paramName.clone());
    }
  }
  
  public Throwable getRootCause()
  {
    return rootException;
  }
  
  public void setRootCause(Throwable paramThrowable)
  {
    if (paramThrowable != this) {
      rootException = paramThrowable;
    }
  }
  
  public Throwable getCause()
  {
    return getRootCause();
  }
  
  public Throwable initCause(Throwable paramThrowable)
  {
    super.initCause(paramThrowable);
    setRootCause(paramThrowable);
    return this;
  }
  
  public String toString()
  {
    String str = super.toString();
    if (rootException != null) {
      str = str + " [Root exception is " + rootException + "]";
    }
    if (remainingName != null) {
      str = str + "; remaining name '" + remainingName + "'";
    }
    return str;
  }
  
  public String toString(boolean paramBoolean)
  {
    if ((!paramBoolean) || (resolvedObj == null)) {
      return toString();
    }
    return toString() + "; resolved object " + resolvedObj;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\NamingException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */