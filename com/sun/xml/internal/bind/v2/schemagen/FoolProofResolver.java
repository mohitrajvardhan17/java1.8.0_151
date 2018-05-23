package com.sun.xml.internal.bind.v2.schemagen;

import com.sun.xml.internal.bind.Util;
import java.io.IOException;
import java.util.logging.Logger;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;

final class FoolProofResolver
  extends SchemaOutputResolver
{
  private static final Logger logger = Util.getClassLogger();
  private final SchemaOutputResolver resolver;
  
  public FoolProofResolver(SchemaOutputResolver paramSchemaOutputResolver)
  {
    assert (paramSchemaOutputResolver != null);
    resolver = paramSchemaOutputResolver;
  }
  
  public Result createOutput(String paramString1, String paramString2)
    throws IOException
  {
    logger.entering(getClass().getName(), "createOutput", new Object[] { paramString1, paramString2 });
    Result localResult = resolver.createOutput(paramString1, paramString2);
    if (localResult != null)
    {
      String str = localResult.getSystemId();
      logger.finer("system ID = " + str);
      if (str == null) {
        throw new AssertionError("system ID cannot be null");
      }
    }
    logger.exiting(getClass().getName(), "createOutput", localResult);
    return localResult;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\FoolProofResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */