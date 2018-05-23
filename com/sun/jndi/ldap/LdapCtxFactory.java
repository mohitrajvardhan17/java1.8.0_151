package com.sun.jndi.ldap;

import com.sun.jndi.url.ldap.ldapURLContextFactory;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import javax.naming.AuthenticationException;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.ldap.Control;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.ObjectFactory;

public final class LdapCtxFactory
  implements ObjectFactory, InitialContextFactory
{
  public static final String ADDRESS_TYPE = "URL";
  
  public LdapCtxFactory() {}
  
  public Object getObjectInstance(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable)
    throws Exception
  {
    if (!isLdapRef(paramObject)) {
      return null;
    }
    ldapURLContextFactory localldapURLContextFactory = new ldapURLContextFactory();
    String[] arrayOfString = getURLs((Reference)paramObject);
    return localldapURLContextFactory.getObjectInstance(arrayOfString, paramName, paramContext, paramHashtable);
  }
  
  public Context getInitialContext(Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    try
    {
      String str = paramHashtable != null ? (String)paramHashtable.get("java.naming.provider.url") : null;
      if (str == null) {
        return new LdapCtx("", "localhost", 389, paramHashtable, false);
      }
      arrayOfString = LdapURL.fromList(str);
      if (arrayOfString.length == 0) {
        throw new ConfigurationException("java.naming.provider.url property does not contain a URL");
      }
      return getLdapCtxInstance(arrayOfString, paramHashtable);
    }
    catch (LdapReferralException localLdapReferralException)
    {
      if ((paramHashtable != null) && ("throw".equals(paramHashtable.get("java.naming.referral")))) {
        throw localLdapReferralException;
      }
      String[] arrayOfString = paramHashtable != null ? (Control[])paramHashtable.get("java.naming.ldap.control.connect") : null;
      return (LdapCtx)localLdapReferralException.getReferralContext(paramHashtable, arrayOfString);
    }
  }
  
  private static boolean isLdapRef(Object paramObject)
  {
    if (!(paramObject instanceof Reference)) {
      return false;
    }
    String str = LdapCtxFactory.class.getName();
    Reference localReference = (Reference)paramObject;
    return str.equals(localReference.getFactoryClassName());
  }
  
  private static String[] getURLs(Reference paramReference)
    throws NamingException
  {
    int i = 0;
    String[] arrayOfString = new String[paramReference.size()];
    Enumeration localEnumeration = paramReference.getAll();
    while (localEnumeration.hasMoreElements())
    {
      localObject = (RefAddr)localEnumeration.nextElement();
      if (((localObject instanceof StringRefAddr)) && (((RefAddr)localObject).getType().equals("URL"))) {
        arrayOfString[(i++)] = ((String)((RefAddr)localObject).getContent());
      }
    }
    if (i == 0) {
      throw new ConfigurationException("Reference contains no valid addresses");
    }
    if (i == paramReference.size()) {
      return arrayOfString;
    }
    Object localObject = new String[i];
    System.arraycopy(arrayOfString, 0, localObject, 0, i);
    return (String[])localObject;
  }
  
  public static DirContext getLdapCtxInstance(Object paramObject, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    if ((paramObject instanceof String)) {
      return getUsingURL((String)paramObject, paramHashtable);
    }
    if ((paramObject instanceof String[])) {
      return getUsingURLs((String[])paramObject, paramHashtable);
    }
    throw new IllegalArgumentException("argument must be an LDAP URL String or array of them");
  }
  
  private static DirContext getUsingURL(String paramString, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    Object localObject = null;
    LdapURL localLdapURL = new LdapURL(paramString);
    String str1 = localLdapURL.getDN();
    String str2 = localLdapURL.getHost();
    int i = localLdapURL.getPort();
    String str3 = null;
    String[] arrayOfString1;
    if ((str2 == null) && (i == -1) && (str1 != null) && ((str3 = ServiceLocator.mapDnToDomainName(str1)) != null) && ((arrayOfString1 = ServiceLocator.getLdapService(str3, paramHashtable)) != null))
    {
      String str4 = localLdapURL.getScheme() + "://";
      String[] arrayOfString2 = new String[arrayOfString1.length];
      String str5 = localLdapURL.getQuery();
      String str6 = localLdapURL.getPath() + (str5 != null ? str5 : "");
      for (int j = 0; j < arrayOfString1.length; j++) {
        arrayOfString2[j] = (str4 + arrayOfString1[j] + str6);
      }
      localObject = getUsingURLs(arrayOfString2, paramHashtable);
      ((LdapCtx)localObject).setDomainName(str3);
    }
    else
    {
      localObject = new LdapCtx(str1, str2, i, paramHashtable, localLdapURL.useSsl());
      ((LdapCtx)localObject).setProviderUrl(paramString);
    }
    return (DirContext)localObject;
  }
  
  private static DirContext getUsingURLs(String[] paramArrayOfString, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    Object localObject1 = null;
    Object localObject2 = null;
    int i = 0;
    while (i < paramArrayOfString.length) {
      try
      {
        return getUsingURL(paramArrayOfString[i], paramHashtable);
      }
      catch (AuthenticationException localAuthenticationException)
      {
        throw localAuthenticationException;
      }
      catch (NamingException localNamingException)
      {
        localObject1 = localNamingException;
        i++;
      }
    }
    throw ((Throwable)localObject1);
  }
  
  public static Attribute createTypeNameAttr(Class<?> paramClass)
  {
    Vector localVector = new Vector(10);
    String[] arrayOfString = getTypeNames(paramClass, localVector);
    if (arrayOfString.length > 0)
    {
      BasicAttribute localBasicAttribute = new BasicAttribute(Obj.JAVA_ATTRIBUTES[6]);
      for (int i = 0; i < arrayOfString.length; i++) {
        localBasicAttribute.add(arrayOfString[i]);
      }
      return localBasicAttribute;
    }
    return null;
  }
  
  private static String[] getTypeNames(Class<?> paramClass, Vector<String> paramVector)
  {
    getClassesAux(paramClass, paramVector);
    Class[] arrayOfClass = paramClass.getInterfaces();
    for (int i = 0; i < arrayOfClass.length; i++) {
      getClassesAux(arrayOfClass[i], paramVector);
    }
    String[] arrayOfString = new String[paramVector.size()];
    int j = 0;
    Iterator localIterator = paramVector.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      arrayOfString[(j++)] = str;
    }
    return arrayOfString;
  }
  
  private static void getClassesAux(Class<?> paramClass, Vector<String> paramVector)
  {
    if (!paramVector.contains(paramClass.getName())) {
      paramVector.addElement(paramClass.getName());
    }
    for (paramClass = paramClass.getSuperclass(); paramClass != null; paramClass = paramClass.getSuperclass()) {
      getTypeNames(paramClass, paramVector);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\LdapCtxFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */