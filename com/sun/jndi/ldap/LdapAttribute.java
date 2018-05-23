package com.sun.jndi.ldap;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

final class LdapAttribute
  extends BasicAttribute
{
  static final long serialVersionUID = -4288716561020779584L;
  private transient DirContext baseCtx = null;
  private Name rdn = new CompositeName();
  private String baseCtxURL;
  private Hashtable<String, ? super String> baseCtxEnv;
  
  public Object clone()
  {
    LdapAttribute localLdapAttribute = new LdapAttribute(attrID, baseCtx, rdn);
    values = ((Vector)values.clone());
    return localLdapAttribute;
  }
  
  public boolean add(Object paramObject)
  {
    values.addElement(paramObject);
    return true;
  }
  
  LdapAttribute(String paramString)
  {
    super(paramString);
  }
  
  private LdapAttribute(String paramString, DirContext paramDirContext, Name paramName)
  {
    super(paramString);
    baseCtx = paramDirContext;
    rdn = paramName;
  }
  
  void setParent(DirContext paramDirContext, Name paramName)
  {
    baseCtx = paramDirContext;
    rdn = paramName;
  }
  
  private DirContext getBaseCtx()
    throws NamingException
  {
    if (baseCtx == null)
    {
      if (baseCtxEnv == null) {
        baseCtxEnv = new Hashtable(3);
      }
      baseCtxEnv.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
      baseCtxEnv.put("java.naming.provider.url", baseCtxURL);
      baseCtx = new InitialDirContext(baseCtxEnv);
    }
    return baseCtx;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    setBaseCtxInfo();
    paramObjectOutputStream.defaultWriteObject();
  }
  
  private void setBaseCtxInfo()
  {
    Hashtable localHashtable1 = null;
    Hashtable localHashtable2 = null;
    if (baseCtx != null)
    {
      localHashtable1 = baseCtx).envprops;
      baseCtxURL = ((LdapCtx)baseCtx).getURL();
    }
    if ((localHashtable1 != null) && (localHashtable1.size() > 0))
    {
      Iterator localIterator = localHashtable1.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        if (str.indexOf("security") != -1)
        {
          if (localHashtable2 == null) {
            localHashtable2 = (Hashtable)localHashtable1.clone();
          }
          localHashtable2.remove(str);
        }
      }
    }
    baseCtxEnv = (localHashtable2 == null ? localHashtable1 : localHashtable2);
  }
  
  public DirContext getAttributeSyntaxDefinition()
    throws NamingException
  {
    DirContext localDirContext1 = getBaseCtx().getSchema(rdn);
    DirContext localDirContext2 = (DirContext)localDirContext1.lookup("AttributeDefinition/" + getID());
    Attribute localAttribute = localDirContext2.getAttributes("").get("SYNTAX");
    if ((localAttribute == null) || (localAttribute.size() == 0)) {
      throw new NameNotFoundException(getID() + "does not have a syntax associated with it");
    }
    String str = (String)localAttribute.get();
    return (DirContext)localDirContext1.lookup("SyntaxDefinition/" + str);
  }
  
  public DirContext getAttributeDefinition()
    throws NamingException
  {
    DirContext localDirContext = getBaseCtx().getSchema(rdn);
    return (DirContext)localDirContext.lookup("AttributeDefinition/" + getID());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\LdapAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */