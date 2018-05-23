package com.sun.xml.internal.ws.api.wsdl.parser;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import java.net.URI;

public abstract class MetaDataResolver
{
  public MetaDataResolver() {}
  
  @Nullable
  public abstract ServiceDescriptor resolve(@NotNull URI paramURI);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\wsdl\parser\MetaDataResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */