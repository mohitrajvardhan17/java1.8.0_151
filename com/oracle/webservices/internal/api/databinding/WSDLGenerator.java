package com.oracle.webservices.internal.api.databinding;

import java.io.File;

public abstract interface WSDLGenerator
{
  public abstract WSDLGenerator inlineSchema(boolean paramBoolean);
  
  public abstract WSDLGenerator property(String paramString, Object paramObject);
  
  public abstract void generate(WSDLResolver paramWSDLResolver);
  
  public abstract void generate(File paramFile, String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\webservices\internal\api\databinding\WSDLGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */