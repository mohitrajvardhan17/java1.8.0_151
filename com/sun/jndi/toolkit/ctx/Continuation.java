package com.sun.jndi.toolkit.ctx;

import java.util.Hashtable;
import javax.naming.CannotProceedException;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.LinkRef;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ResolveResult;

public class Continuation
  extends ResolveResult
{
  protected Name starter;
  protected Object followingLink = null;
  protected Hashtable<?, ?> environment = null;
  protected boolean continuing = false;
  protected Context resolvedContext = null;
  protected Name relativeResolvedName = null;
  private static final long serialVersionUID = 8162530656132624308L;
  
  public Continuation() {}
  
  public Continuation(Name paramName, Hashtable<?, ?> paramHashtable)
  {
    starter = paramName;
    environment = ((Hashtable)(paramHashtable == null ? null : paramHashtable.clone()));
  }
  
  public boolean isContinue()
  {
    return continuing;
  }
  
  public void setSuccess()
  {
    continuing = false;
  }
  
  public NamingException fillInException(NamingException paramNamingException)
  {
    paramNamingException.setRemainingName(remainingName);
    paramNamingException.setResolvedObj(resolvedObj);
    if ((starter == null) || (starter.isEmpty())) {
      paramNamingException.setResolvedName(null);
    } else if (remainingName == null) {
      paramNamingException.setResolvedName(starter);
    } else {
      paramNamingException.setResolvedName(starter.getPrefix(starter.size() - remainingName.size()));
    }
    if ((paramNamingException instanceof CannotProceedException))
    {
      CannotProceedException localCannotProceedException = (CannotProceedException)paramNamingException;
      Hashtable localHashtable = environment == null ? new Hashtable(11) : (Hashtable)environment.clone();
      localCannotProceedException.setEnvironment(localHashtable);
      localCannotProceedException.setAltNameCtx(resolvedContext);
      localCannotProceedException.setAltName(relativeResolvedName);
    }
    return paramNamingException;
  }
  
  public void setErrorNNS(Object paramObject, Name paramName)
  {
    Name localName = (Name)paramName.clone();
    try
    {
      localName.add("");
    }
    catch (InvalidNameException localInvalidNameException) {}
    setErrorAux(paramObject, localName);
  }
  
  public void setErrorNNS(Object paramObject, String paramString)
  {
    CompositeName localCompositeName = new CompositeName();
    try
    {
      if ((paramString != null) && (!paramString.equals(""))) {
        localCompositeName.add(paramString);
      }
      localCompositeName.add("");
    }
    catch (InvalidNameException localInvalidNameException) {}
    setErrorAux(paramObject, localCompositeName);
  }
  
  public void setError(Object paramObject, Name paramName)
  {
    if (paramName != null) {
      remainingName = ((Name)paramName.clone());
    } else {
      remainingName = null;
    }
    setErrorAux(paramObject, remainingName);
  }
  
  public void setError(Object paramObject, String paramString)
  {
    CompositeName localCompositeName = new CompositeName();
    if ((paramString != null) && (!paramString.equals(""))) {
      try
      {
        localCompositeName.add(paramString);
      }
      catch (InvalidNameException localInvalidNameException) {}
    }
    setErrorAux(paramObject, localCompositeName);
  }
  
  private void setErrorAux(Object paramObject, Name paramName)
  {
    remainingName = paramName;
    resolvedObj = paramObject;
    continuing = false;
  }
  
  private void setContinueAux(Object paramObject, Name paramName1, Context paramContext, Name paramName2)
  {
    if ((paramObject instanceof LinkRef))
    {
      setContinueLink(paramObject, paramName1, paramContext, paramName2);
    }
    else
    {
      remainingName = paramName2;
      resolvedObj = paramObject;
      relativeResolvedName = paramName1;
      resolvedContext = paramContext;
      continuing = true;
    }
  }
  
  public void setContinueNNS(Object paramObject, Name paramName, Context paramContext)
  {
    CompositeName localCompositeName = new CompositeName();
    setContinue(paramObject, paramName, paramContext, PartialCompositeContext._NNS_NAME);
  }
  
  public void setContinueNNS(Object paramObject, String paramString, Context paramContext)
  {
    CompositeName localCompositeName = new CompositeName();
    try
    {
      localCompositeName.add(paramString);
    }
    catch (NamingException localNamingException) {}
    setContinue(paramObject, localCompositeName, paramContext, PartialCompositeContext._NNS_NAME);
  }
  
  public void setContinue(Object paramObject, Name paramName, Context paramContext)
  {
    setContinueAux(paramObject, paramName, paramContext, (Name)PartialCompositeContext._EMPTY_NAME.clone());
  }
  
  public void setContinue(Object paramObject, Name paramName1, Context paramContext, Name paramName2)
  {
    if (paramName2 != null) {
      remainingName = ((Name)paramName2.clone());
    } else {
      remainingName = new CompositeName();
    }
    setContinueAux(paramObject, paramName1, paramContext, remainingName);
  }
  
  public void setContinue(Object paramObject, String paramString1, Context paramContext, String paramString2)
  {
    CompositeName localCompositeName1 = new CompositeName();
    if (!paramString1.equals("")) {
      try
      {
        localCompositeName1.add(paramString1);
      }
      catch (NamingException localNamingException1) {}
    }
    CompositeName localCompositeName2 = new CompositeName();
    if (!paramString2.equals("")) {
      try
      {
        localCompositeName2.add(paramString2);
      }
      catch (NamingException localNamingException2) {}
    }
    setContinueAux(paramObject, localCompositeName1, paramContext, localCompositeName2);
  }
  
  @Deprecated
  public void setContinue(Object paramObject1, Object paramObject2)
  {
    setContinue(paramObject1, null, (Context)paramObject2);
  }
  
  private void setContinueLink(Object paramObject, Name paramName1, Context paramContext, Name paramName2)
  {
    followingLink = paramObject;
    remainingName = paramName2;
    resolvedObj = paramContext;
    relativeResolvedName = PartialCompositeContext._EMPTY_NAME;
    resolvedContext = paramContext;
    continuing = true;
  }
  
  public String toString()
  {
    if (remainingName != null) {
      return starter.toString() + "; remainingName: '" + remainingName + "'";
    }
    return starter.toString();
  }
  
  public String toString(boolean paramBoolean)
  {
    if ((!paramBoolean) || (resolvedObj == null)) {
      return toString();
    }
    return toString() + "; resolvedObj: " + resolvedObj + "; relativeResolvedName: " + relativeResolvedName + "; resolvedContext: " + resolvedContext;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\toolkit\ctx\Continuation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */