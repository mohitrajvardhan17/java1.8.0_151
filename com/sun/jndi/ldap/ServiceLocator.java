package com.sun.jndi.ldap;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.naming.spi.NamingManager;

class ServiceLocator
{
  private static final String SRV_RR = "SRV";
  private static final String[] SRV_RR_ATTR = { "SRV" };
  private static final Random random = new Random();
  
  private ServiceLocator() {}
  
  static String mapDnToDomainName(String paramString)
    throws InvalidNameException
  {
    if (paramString == null) {
      return null;
    }
    StringBuffer localStringBuffer = new StringBuffer();
    LdapName localLdapName = new LdapName(paramString);
    List localList = localLdapName.getRdns();
    for (int i = localList.size() - 1; i >= 0; i--)
    {
      Rdn localRdn = (Rdn)localList.get(i);
      if ((localRdn.size() == 1) && ("dc".equalsIgnoreCase(localRdn.getType())))
      {
        Object localObject = localRdn.getValue();
        if ((localObject instanceof String))
        {
          if ((localObject.equals(".")) || ((localStringBuffer.length() == 1) && (localStringBuffer.charAt(0) == '.'))) {
            localStringBuffer.setLength(0);
          }
          if (localStringBuffer.length() > 0) {
            localStringBuffer.append('.');
          }
          localStringBuffer.append(localObject);
        }
        else
        {
          localStringBuffer.setLength(0);
        }
      }
      else
      {
        localStringBuffer.setLength(0);
      }
    }
    return localStringBuffer.length() != 0 ? localStringBuffer.toString() : null;
  }
  
  static String[] getLdapService(String paramString, Hashtable<?, ?> paramHashtable)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return null;
    }
    String str = "dns:///_ldap._tcp." + paramString;
    String[] arrayOfString = null;
    try
    {
      Context localContext = NamingManager.getURLContext("dns", paramHashtable);
      if (!(localContext instanceof DirContext)) {
        return null;
      }
      Attributes localAttributes = ((DirContext)localContext).getAttributes(str, SRV_RR_ATTR);
      Attribute localAttribute;
      if ((localAttributes != null) && ((localAttribute = localAttributes.get("SRV")) != null))
      {
        int i = localAttribute.size();
        int j = 0;
        Object localObject = new SrvRecord[i];
        int k = 0;
        int m = 0;
        while (k < i)
        {
          try
          {
            localObject[m] = new SrvRecord((String)localAttribute.get(k));
            m++;
          }
          catch (Exception localException) {}
          k++;
        }
        j = m;
        if (j < i)
        {
          SrvRecord[] arrayOfSrvRecord = new SrvRecord[j];
          System.arraycopy(localObject, 0, arrayOfSrvRecord, 0, j);
          localObject = arrayOfSrvRecord;
        }
        if (j > 1) {
          Arrays.sort((Object[])localObject);
        }
        arrayOfString = extractHostports((SrvRecord[])localObject);
      }
    }
    catch (NamingException localNamingException) {}
    return arrayOfString;
  }
  
  private static String[] extractHostports(SrvRecord[] paramArrayOfSrvRecord)
  {
    String[] arrayOfString = null;
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    for (int n = 0; n < paramArrayOfSrvRecord.length; n++)
    {
      if (arrayOfString == null) {
        arrayOfString = new String[paramArrayOfSrvRecord.length];
      }
      i = n;
      while ((n < paramArrayOfSrvRecord.length - 1) && (priority == 1priority)) {
        n++;
      }
      j = n;
      k = j - i + 1;
      for (int i1 = 0; i1 < k; i1++) {
        arrayOfString[(m++)] = selectHostport(paramArrayOfSrvRecord, i, j);
      }
    }
    return arrayOfString;
  }
  
  private static String selectHostport(SrvRecord[] paramArrayOfSrvRecord, int paramInt1, int paramInt2)
  {
    if (paramInt1 == paramInt2) {
      return hostport;
    }
    int i = 0;
    for (int j = paramInt1; j <= paramInt2; j++) {
      if (paramArrayOfSrvRecord[j] != null)
      {
        i += weight;
        sum = i;
      }
    }
    String str = null;
    int k = i == 0 ? 0 : random.nextInt(i + 1);
    for (int m = paramInt1; m <= paramInt2; m++) {
      if ((paramArrayOfSrvRecord[m] != null) && (sum >= k))
      {
        str = hostport;
        paramArrayOfSrvRecord[m] = null;
        break;
      }
    }
    return str;
  }
  
  static class SrvRecord
    implements Comparable<SrvRecord>
  {
    int priority;
    int weight;
    int sum;
    String hostport;
    
    SrvRecord(String paramString)
      throws Exception
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString, " ");
      if (localStringTokenizer.countTokens() == 4)
      {
        priority = Integer.parseInt(localStringTokenizer.nextToken());
        weight = Integer.parseInt(localStringTokenizer.nextToken());
        String str = localStringTokenizer.nextToken();
        hostport = (localStringTokenizer.nextToken() + ":" + str);
      }
      else
      {
        throw new IllegalArgumentException();
      }
    }
    
    public int compareTo(SrvRecord paramSrvRecord)
    {
      if (priority > priority) {
        return 1;
      }
      if (priority < priority) {
        return -1;
      }
      if ((weight == 0) && (weight != 0)) {
        return -1;
      }
      if ((weight != 0) && (weight == 0)) {
        return 1;
      }
      return 0;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\ServiceLocator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */