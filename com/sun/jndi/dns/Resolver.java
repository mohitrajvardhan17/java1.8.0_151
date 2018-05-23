package com.sun.jndi.dns;

import java.util.Vector;
import javax.naming.CommunicationException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

class Resolver
{
  private DnsClient dnsClient;
  private int timeout;
  private int retries;
  
  Resolver(String[] paramArrayOfString, int paramInt1, int paramInt2)
    throws NamingException
  {
    timeout = paramInt1;
    retries = paramInt2;
    dnsClient = new DnsClient(paramArrayOfString, paramInt1, paramInt2);
  }
  
  public void close()
  {
    dnsClient.close();
    dnsClient = null;
  }
  
  ResourceRecords query(DnsName paramDnsName, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
    throws NamingException
  {
    return dnsClient.query(paramDnsName, paramInt1, paramInt2, paramBoolean1, paramBoolean2);
  }
  
  ResourceRecords queryZone(DnsName paramDnsName, int paramInt, boolean paramBoolean)
    throws NamingException
  {
    DnsClient localDnsClient = new DnsClient(findNameServers(paramDnsName, paramBoolean), timeout, retries);
    try
    {
      ResourceRecords localResourceRecords = localDnsClient.queryZone(paramDnsName, paramInt, paramBoolean);
      return localResourceRecords;
    }
    finally
    {
      localDnsClient.close();
    }
  }
  
  DnsName findZoneName(DnsName paramDnsName, int paramInt, boolean paramBoolean)
    throws NamingException
  {
    paramDnsName = (DnsName)paramDnsName.clone();
    while (paramDnsName.size() > 1)
    {
      ResourceRecords localResourceRecords = null;
      try
      {
        localResourceRecords = query(paramDnsName, paramInt, 6, paramBoolean, false);
      }
      catch (NameNotFoundException localNameNotFoundException)
      {
        throw localNameNotFoundException;
      }
      catch (NamingException localNamingException) {}
      if (localResourceRecords != null)
      {
        if (answer.size() > 0) {
          return paramDnsName;
        }
        for (int i = 0; i < authority.size(); i++)
        {
          ResourceRecord localResourceRecord = (ResourceRecord)authority.elementAt(i);
          if (localResourceRecord.getType() == 6)
          {
            DnsName localDnsName = localResourceRecord.getName();
            if (paramDnsName.endsWith(localDnsName)) {
              return localDnsName;
            }
          }
        }
      }
      paramDnsName.remove(paramDnsName.size() - 1);
    }
    return paramDnsName;
  }
  
  ResourceRecord findSoa(DnsName paramDnsName, int paramInt, boolean paramBoolean)
    throws NamingException
  {
    ResourceRecords localResourceRecords = query(paramDnsName, paramInt, 6, paramBoolean, false);
    for (int i = 0; i < answer.size(); i++)
    {
      ResourceRecord localResourceRecord = (ResourceRecord)answer.elementAt(i);
      if (localResourceRecord.getType() == 6) {
        return localResourceRecord;
      }
    }
    return null;
  }
  
  private String[] findNameServers(DnsName paramDnsName, boolean paramBoolean)
    throws NamingException
  {
    ResourceRecords localResourceRecords = query(paramDnsName, 1, 2, paramBoolean, false);
    String[] arrayOfString = new String[answer.size()];
    for (int i = 0; i < arrayOfString.length; i++)
    {
      ResourceRecord localResourceRecord = (ResourceRecord)answer.elementAt(i);
      if (localResourceRecord.getType() != 2) {
        throw new CommunicationException("Corrupted DNS message");
      }
      arrayOfString[i] = ((String)localResourceRecord.getRdata());
      arrayOfString[i] = arrayOfString[i].substring(0, arrayOfString[i].length() - 1);
    }
    return arrayOfString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\dns\Resolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */