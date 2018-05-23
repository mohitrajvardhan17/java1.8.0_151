package javax.xml.bind;

import java.io.IOException;
import javax.xml.transform.Result;

public abstract class SchemaOutputResolver
{
  public SchemaOutputResolver() {}
  
  public abstract Result createOutput(String paramString1, String paramString2)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\bind\SchemaOutputResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */