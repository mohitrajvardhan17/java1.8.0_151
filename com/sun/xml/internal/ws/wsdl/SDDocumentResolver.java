package com.sun.xml.internal.ws.wsdl;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.server.SDDocument;

public abstract interface SDDocumentResolver
{
  @Nullable
  public abstract SDDocument resolve(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\wsdl\SDDocumentResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */