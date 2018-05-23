package javax.naming.spi;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;

public abstract interface Resolver
{
  public abstract ResolveResult resolveToClass(Name paramName, Class<? extends Context> paramClass)
    throws NamingException;
  
  public abstract ResolveResult resolveToClass(String paramString, Class<? extends Context> paramClass)
    throws NamingException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\spi\Resolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */