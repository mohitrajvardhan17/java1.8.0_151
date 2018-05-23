package com.sun.jndi.url.dns;

import com.sun.jndi.dns.DnsContextFactory;
import com.sun.jndi.dns.DnsUrl;
import com.sun.jndi.toolkit.url.GenericURLDirContext;
import java.net.MalformedURLException;
import java.util.Hashtable;
import javax.naming.CompositeName;
import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.spi.ResolveResult;

public class dnsURLContext
  extends GenericURLDirContext
{
  public dnsURLContext(Hashtable<?, ?> paramHashtable)
  {
    super(paramHashtable);
  }
  
  protected ResolveResult getRootURLContext(String paramString, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    DnsUrl localDnsUrl;
    try
    {
      localDnsUrl = new DnsUrl(paramString);
    }
    catch (MalformedURLException localMalformedURLException)
    {
      throw new InvalidNameException(localMalformedURLException.getMessage());
    }
    DnsUrl[] arrayOfDnsUrl = { localDnsUrl };
    String str = localDnsUrl.getDomain();
    return new ResolveResult(DnsContextFactory.getContext(".", arrayOfDnsUrl, paramHashtable), new CompositeName().add(str));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\url\dns\dnsURLContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */