package com.sun.xml.internal.ws.api.wsdl.parser;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.xml.sax.EntityResolver;

public abstract class MetadataResolverFactory
{
  public MetadataResolverFactory() {}
  
  @NotNull
  public abstract MetaDataResolver metadataResolver(@Nullable EntityResolver paramEntityResolver);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\wsdl\parser\MetadataResolverFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */