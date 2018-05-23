package org.w3c.dom;

public abstract interface DOMConfiguration
{
  public abstract void setParameter(String paramString, Object paramObject)
    throws DOMException;
  
  public abstract Object getParameter(String paramString)
    throws DOMException;
  
  public abstract boolean canSetParameter(String paramString, Object paramObject);
  
  public abstract DOMStringList getParameterNames();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\DOMConfiguration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */