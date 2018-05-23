package com.sun.xml.internal.ws.util;

import java.util.List;
import java.util.Set;
import javax.xml.ws.handler.Handler;

public class HandlerAnnotationInfo
{
  private List<Handler> handlers;
  private Set<String> roles;
  
  public HandlerAnnotationInfo() {}
  
  public List<Handler> getHandlers()
  {
    return handlers;
  }
  
  public void setHandlers(List<Handler> paramList)
  {
    handlers = paramList;
  }
  
  public Set<String> getRoles()
  {
    return roles;
  }
  
  public void setRoles(Set<String> paramSet)
  {
    roles = paramSet;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\util\HandlerAnnotationInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */