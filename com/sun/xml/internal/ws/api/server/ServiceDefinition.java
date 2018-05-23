package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;

public abstract interface ServiceDefinition
  extends Iterable<SDDocument>
{
  @NotNull
  public abstract SDDocument getPrimary();
  
  public abstract void addFilter(@NotNull SDDocumentFilter paramSDDocumentFilter);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\server\ServiceDefinition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */