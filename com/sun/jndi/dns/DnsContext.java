package com.sun.jndi.dns;

import com.sun.jndi.toolkit.ctx.ComponentDirContext;
import com.sun.jndi.toolkit.ctx.Continuation;
import java.util.Hashtable;
import java.util.Vector;
import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InvalidAttributeIdentifierException;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.spi.DirectoryManager;

public class DnsContext
  extends ComponentDirContext
{
  DnsName domain;
  Hashtable<Object, Object> environment;
  private boolean envShared;
  private boolean parentIsDns;
  private String[] servers;
  private Resolver resolver;
  private boolean authoritative;
  private boolean recursion;
  private int timeout;
  private int retries;
  static final NameParser nameParser = new DnsNameParser();
  private static final int DEFAULT_INIT_TIMEOUT = 1000;
  private static final int DEFAULT_RETRIES = 4;
  private static final String INIT_TIMEOUT = "com.sun.jndi.dns.timeout.initial";
  private static final String RETRIES = "com.sun.jndi.dns.timeout.retries";
  private CT lookupCT;
  private static final String LOOKUP_ATTR = "com.sun.jndi.dns.lookup.attr";
  private static final String RECURSION = "com.sun.jndi.dns.recursion";
  private static final int ANY = 255;
  private static final ZoneNode zoneTree = new ZoneNode(null);
  private static final boolean debug = false;
  
  public DnsContext(String paramString, String[] paramArrayOfString, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    domain = new DnsName(paramString + ".");
    servers = (paramArrayOfString == null ? null : (String[])paramArrayOfString.clone());
    environment = ((Hashtable)paramHashtable.clone());
    envShared = false;
    parentIsDns = false;
    resolver = null;
    initFromEnvironment();
  }
  
  DnsContext(DnsContext paramDnsContext, DnsName paramDnsName)
  {
    this(paramDnsContext);
    domain = paramDnsName;
    parentIsDns = true;
  }
  
  private DnsContext(DnsContext paramDnsContext)
  {
    environment = environment;
    envShared = (envShared = 1);
    parentIsDns = parentIsDns;
    domain = domain;
    servers = servers;
    resolver = resolver;
    authoritative = authoritative;
    recursion = recursion;
    timeout = timeout;
    retries = retries;
    lookupCT = lookupCT;
  }
  
  public void close()
  {
    if (resolver != null)
    {
      resolver.close();
      resolver = null;
    }
  }
  
  protected Hashtable<?, ?> p_getEnvironment()
  {
    return environment;
  }
  
  public Hashtable<?, ?> getEnvironment()
    throws NamingException
  {
    return (Hashtable)environment.clone();
  }
  
  public Object addToEnvironment(String paramString, Object paramObject)
    throws NamingException
  {
    if (paramString.equals("com.sun.jndi.dns.lookup.attr"))
    {
      lookupCT = getLookupCT((String)paramObject);
    }
    else if (paramString.equals("java.naming.authoritative"))
    {
      authoritative = "true".equalsIgnoreCase((String)paramObject);
    }
    else if (paramString.equals("com.sun.jndi.dns.recursion"))
    {
      recursion = "true".equalsIgnoreCase((String)paramObject);
    }
    else
    {
      int i;
      if (paramString.equals("com.sun.jndi.dns.timeout.initial"))
      {
        i = Integer.parseInt((String)paramObject);
        if (timeout != i)
        {
          timeout = i;
          resolver = null;
        }
      }
      else if (paramString.equals("com.sun.jndi.dns.timeout.retries"))
      {
        i = Integer.parseInt((String)paramObject);
        if (retries != i)
        {
          retries = i;
          resolver = null;
        }
      }
    }
    if (!envShared) {
      return environment.put(paramString, paramObject);
    }
    if (environment.get(paramString) != paramObject)
    {
      environment = ((Hashtable)environment.clone());
      envShared = false;
      return environment.put(paramString, paramObject);
    }
    return paramObject;
  }
  
  public Object removeFromEnvironment(String paramString)
    throws NamingException
  {
    if (paramString.equals("com.sun.jndi.dns.lookup.attr"))
    {
      lookupCT = getLookupCT(null);
    }
    else if (paramString.equals("java.naming.authoritative"))
    {
      authoritative = false;
    }
    else if (paramString.equals("com.sun.jndi.dns.recursion"))
    {
      recursion = true;
    }
    else if (paramString.equals("com.sun.jndi.dns.timeout.initial"))
    {
      if (timeout != 1000)
      {
        timeout = 1000;
        resolver = null;
      }
    }
    else if ((paramString.equals("com.sun.jndi.dns.timeout.retries")) && (retries != 4))
    {
      retries = 4;
      resolver = null;
    }
    if (!envShared) {
      return environment.remove(paramString);
    }
    if (environment.get(paramString) != null)
    {
      environment = ((Hashtable)environment.clone());
      envShared = false;
      return environment.remove(paramString);
    }
    return null;
  }
  
  void setProviderUrl(String paramString)
  {
    environment.put("java.naming.provider.url", paramString);
  }
  
  private void initFromEnvironment()
    throws InvalidAttributeIdentifierException
  {
    lookupCT = getLookupCT((String)environment.get("com.sun.jndi.dns.lookup.attr"));
    authoritative = "true".equalsIgnoreCase((String)environment.get("java.naming.authoritative"));
    String str = (String)environment.get("com.sun.jndi.dns.recursion");
    recursion = ((str == null) || ("true".equalsIgnoreCase(str)));
    str = (String)environment.get("com.sun.jndi.dns.timeout.initial");
    timeout = (str == null ? 1000 : Integer.parseInt(str));
    str = (String)environment.get("com.sun.jndi.dns.timeout.retries");
    retries = (str == null ? 4 : Integer.parseInt(str));
  }
  
  private CT getLookupCT(String paramString)
    throws InvalidAttributeIdentifierException
  {
    return paramString == null ? new CT(1, 16) : fromAttrId(paramString);
  }
  
  public Object c_lookup(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    paramContinuation.setSuccess();
    Object localObject1;
    if (paramName.isEmpty())
    {
      localObject1 = new DnsContext(this);
      resolver = new Resolver(servers, timeout, retries);
      return localObject1;
    }
    try
    {
      localObject1 = fullyQualify(paramName);
      localObject2 = getResolver().query((DnsName)localObject1, lookupCT.rrclass, lookupCT.rrtype, recursion, authoritative);
      Attributes localAttributes = rrsToAttrs((ResourceRecords)localObject2, null);
      DnsContext localDnsContext = new DnsContext(this, (DnsName)localObject1);
      return DirectoryManager.getObjectInstance(localDnsContext, paramName, this, environment, localAttributes);
    }
    catch (NamingException localNamingException)
    {
      paramContinuation.setError(this, paramName);
      throw paramContinuation.fillInException(localNamingException);
    }
    catch (Exception localException)
    {
      paramContinuation.setError(this, paramName);
      Object localObject2 = new NamingException("Problem generating object using object factory");
      ((NamingException)localObject2).setRootCause(localException);
      throw paramContinuation.fillInException((NamingException)localObject2);
    }
  }
  
  public Object c_lookupLink(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    return c_lookup(paramName, paramContinuation);
  }
  
  public NamingEnumeration<NameClassPair> c_list(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    paramContinuation.setSuccess();
    try
    {
      DnsName localDnsName = fullyQualify(paramName);
      NameNode localNameNode = getNameNode(localDnsName);
      DnsContext localDnsContext = new DnsContext(this, localDnsName);
      return new NameClassPairEnumeration(localDnsContext, localNameNode.getChildren());
    }
    catch (NamingException localNamingException)
    {
      paramContinuation.setError(this, paramName);
      throw paramContinuation.fillInException(localNamingException);
    }
  }
  
  public NamingEnumeration<Binding> c_listBindings(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    paramContinuation.setSuccess();
    try
    {
      DnsName localDnsName = fullyQualify(paramName);
      NameNode localNameNode = getNameNode(localDnsName);
      DnsContext localDnsContext = new DnsContext(this, localDnsName);
      return new BindingEnumeration(localDnsContext, localNameNode.getChildren());
    }
    catch (NamingException localNamingException)
    {
      paramContinuation.setError(this, paramName);
      throw paramContinuation.fillInException(localNamingException);
    }
  }
  
  public void c_bind(Name paramName, Object paramObject, Continuation paramContinuation)
    throws NamingException
  {
    paramContinuation.setError(this, paramName);
    throw paramContinuation.fillInException(new OperationNotSupportedException());
  }
  
  public void c_rebind(Name paramName, Object paramObject, Continuation paramContinuation)
    throws NamingException
  {
    paramContinuation.setError(this, paramName);
    throw paramContinuation.fillInException(new OperationNotSupportedException());
  }
  
  public void c_unbind(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    paramContinuation.setError(this, paramName);
    throw paramContinuation.fillInException(new OperationNotSupportedException());
  }
  
  public void c_rename(Name paramName1, Name paramName2, Continuation paramContinuation)
    throws NamingException
  {
    paramContinuation.setError(this, paramName1);
    throw paramContinuation.fillInException(new OperationNotSupportedException());
  }
  
  public Context c_createSubcontext(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    paramContinuation.setError(this, paramName);
    throw paramContinuation.fillInException(new OperationNotSupportedException());
  }
  
  public void c_destroySubcontext(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    paramContinuation.setError(this, paramName);
    throw paramContinuation.fillInException(new OperationNotSupportedException());
  }
  
  public NameParser c_getNameParser(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    paramContinuation.setSuccess();
    return nameParser;
  }
  
  public void c_bind(Name paramName, Object paramObject, Attributes paramAttributes, Continuation paramContinuation)
    throws NamingException
  {
    paramContinuation.setError(this, paramName);
    throw paramContinuation.fillInException(new OperationNotSupportedException());
  }
  
  public void c_rebind(Name paramName, Object paramObject, Attributes paramAttributes, Continuation paramContinuation)
    throws NamingException
  {
    paramContinuation.setError(this, paramName);
    throw paramContinuation.fillInException(new OperationNotSupportedException());
  }
  
  public DirContext c_createSubcontext(Name paramName, Attributes paramAttributes, Continuation paramContinuation)
    throws NamingException
  {
    paramContinuation.setError(this, paramName);
    throw paramContinuation.fillInException(new OperationNotSupportedException());
  }
  
  public Attributes c_getAttributes(Name paramName, String[] paramArrayOfString, Continuation paramContinuation)
    throws NamingException
  {
    paramContinuation.setSuccess();
    try
    {
      DnsName localDnsName = fullyQualify(paramName);
      CT[] arrayOfCT = attrIdsToClassesAndTypes(paramArrayOfString);
      CT localCT = getClassAndTypeToQuery(arrayOfCT);
      ResourceRecords localResourceRecords = getResolver().query(localDnsName, rrclass, rrtype, recursion, authoritative);
      return rrsToAttrs(localResourceRecords, arrayOfCT);
    }
    catch (NamingException localNamingException)
    {
      paramContinuation.setError(this, paramName);
      throw paramContinuation.fillInException(localNamingException);
    }
  }
  
  public void c_modifyAttributes(Name paramName, int paramInt, Attributes paramAttributes, Continuation paramContinuation)
    throws NamingException
  {
    paramContinuation.setError(this, paramName);
    throw paramContinuation.fillInException(new OperationNotSupportedException());
  }
  
  public void c_modifyAttributes(Name paramName, ModificationItem[] paramArrayOfModificationItem, Continuation paramContinuation)
    throws NamingException
  {
    paramContinuation.setError(this, paramName);
    throw paramContinuation.fillInException(new OperationNotSupportedException());
  }
  
  public NamingEnumeration<SearchResult> c_search(Name paramName, Attributes paramAttributes, String[] paramArrayOfString, Continuation paramContinuation)
    throws NamingException
  {
    throw new OperationNotSupportedException();
  }
  
  public NamingEnumeration<SearchResult> c_search(Name paramName, String paramString, SearchControls paramSearchControls, Continuation paramContinuation)
    throws NamingException
  {
    throw new OperationNotSupportedException();
  }
  
  public NamingEnumeration<SearchResult> c_search(Name paramName, String paramString, Object[] paramArrayOfObject, SearchControls paramSearchControls, Continuation paramContinuation)
    throws NamingException
  {
    throw new OperationNotSupportedException();
  }
  
  public DirContext c_getSchema(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    paramContinuation.setError(this, paramName);
    throw paramContinuation.fillInException(new OperationNotSupportedException());
  }
  
  public DirContext c_getSchemaClassDefinition(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    paramContinuation.setError(this, paramName);
    throw paramContinuation.fillInException(new OperationNotSupportedException());
  }
  
  public String getNameInNamespace()
  {
    return domain.toString();
  }
  
  public Name composeName(Name paramName1, Name paramName2)
    throws NamingException
  {
    if ((!(paramName2 instanceof DnsName)) && (!(paramName2 instanceof CompositeName))) {
      paramName2 = new DnsName().addAll(paramName2);
    }
    if ((!(paramName1 instanceof DnsName)) && (!(paramName1 instanceof CompositeName))) {
      paramName1 = new DnsName().addAll(paramName1);
    }
    if (((paramName2 instanceof DnsName)) && ((paramName1 instanceof DnsName)))
    {
      localObject = (DnsName)paramName2.clone();
      ((Name)localObject).addAll(paramName1);
      return new CompositeName().add(localObject.toString());
    }
    Name localName1 = (paramName2 instanceof CompositeName) ? paramName2 : new CompositeName().add(paramName2.toString());
    Name localName2 = (paramName1 instanceof CompositeName) ? paramName1 : new CompositeName().add(paramName1.toString());
    int i = localName1.size() - 1;
    if ((localName2.isEmpty()) || (localName2.get(0).equals("")) || (localName1.isEmpty()) || (localName1.get(i).equals(""))) {
      return super.composeName(localName2, localName1);
    }
    Object localObject = paramName2 == localName1 ? (CompositeName)localName1.clone() : localName1;
    ((Name)localObject).addAll(localName2);
    if (parentIsDns)
    {
      DnsName localDnsName = (paramName2 instanceof DnsName) ? (DnsName)paramName2.clone() : new DnsName(localName1.get(i));
      localDnsName.addAll((paramName1 instanceof DnsName) ? paramName1 : new DnsName(localName2.get(0)));
      ((Name)localObject).remove(i + 1);
      ((Name)localObject).remove(i);
      ((Name)localObject).add(i, localDnsName.toString());
    }
    return (Name)localObject;
  }
  
  private synchronized Resolver getResolver()
    throws NamingException
  {
    if (resolver == null) {
      resolver = new Resolver(servers, timeout, retries);
    }
    return resolver;
  }
  
  DnsName fullyQualify(Name paramName)
    throws NamingException
  {
    if (paramName.isEmpty()) {
      return domain;
    }
    DnsName localDnsName = (paramName instanceof CompositeName) ? new DnsName(paramName.get(0)) : (DnsName)new DnsName().addAll(paramName);
    if (localDnsName.hasRootLabel())
    {
      if (domain.size() == 1) {
        return localDnsName;
      }
      throw new InvalidNameException("DNS name " + localDnsName + " not relative to " + domain);
    }
    return (DnsName)localDnsName.addAll(0, domain);
  }
  
  private static Attributes rrsToAttrs(ResourceRecords paramResourceRecords, CT[] paramArrayOfCT)
  {
    BasicAttributes localBasicAttributes = new BasicAttributes(true);
    for (int i = 0; i < answer.size(); i++)
    {
      ResourceRecord localResourceRecord = (ResourceRecord)answer.elementAt(i);
      int j = localResourceRecord.getType();
      int k = localResourceRecord.getRrclass();
      if (classAndTypeMatch(k, j, paramArrayOfCT))
      {
        String str = toAttrId(k, j);
        Object localObject = localBasicAttributes.get(str);
        if (localObject == null)
        {
          localObject = new BasicAttribute(str);
          localBasicAttributes.put((Attribute)localObject);
        }
        ((Attribute)localObject).add(localResourceRecord.getRdata());
      }
    }
    return localBasicAttributes;
  }
  
  private static boolean classAndTypeMatch(int paramInt1, int paramInt2, CT[] paramArrayOfCT)
  {
    if (paramArrayOfCT == null) {
      return true;
    }
    for (int i = 0; i < paramArrayOfCT.length; i++)
    {
      CT localCT = paramArrayOfCT[i];
      int j = (rrclass == 255) || (rrclass == paramInt1) ? 1 : 0;
      int k = (rrtype == 255) || (rrtype == paramInt2) ? 1 : 0;
      if ((j != 0) && (k != 0)) {
        return true;
      }
    }
    return false;
  }
  
  private static String toAttrId(int paramInt1, int paramInt2)
  {
    String str = ResourceRecord.getTypeName(paramInt2);
    if (paramInt1 != 1) {
      str = ResourceRecord.getRrclassName(paramInt1) + " " + str;
    }
    return str;
  }
  
  private static CT fromAttrId(String paramString)
    throws InvalidAttributeIdentifierException
  {
    if (paramString.equals("")) {
      throw new InvalidAttributeIdentifierException("Attribute ID cannot be empty");
    }
    int k = paramString.indexOf(' ');
    int i;
    if (k < 0)
    {
      i = 1;
    }
    else
    {
      str = paramString.substring(0, k);
      i = ResourceRecord.getRrclass(str);
      if (i < 0) {
        throw new InvalidAttributeIdentifierException("Unknown resource record class '" + str + '\'');
      }
    }
    String str = paramString.substring(k + 1);
    int j = ResourceRecord.getType(str);
    if (j < 0) {
      throw new InvalidAttributeIdentifierException("Unknown resource record type '" + str + '\'');
    }
    return new CT(i, j);
  }
  
  private static CT[] attrIdsToClassesAndTypes(String[] paramArrayOfString)
    throws InvalidAttributeIdentifierException
  {
    if (paramArrayOfString == null) {
      return null;
    }
    CT[] arrayOfCT = new CT[paramArrayOfString.length];
    for (int i = 0; i < paramArrayOfString.length; i++) {
      arrayOfCT[i] = fromAttrId(paramArrayOfString[i]);
    }
    return arrayOfCT;
  }
  
  private static CT getClassAndTypeToQuery(CT[] paramArrayOfCT)
  {
    int i;
    int j;
    if (paramArrayOfCT == null)
    {
      i = 255;
      j = 255;
    }
    else if (paramArrayOfCT.length == 0)
    {
      i = 1;
      j = 255;
    }
    else
    {
      i = 0rrclass;
      j = 0rrtype;
      for (int k = 1; k < paramArrayOfCT.length; k++)
      {
        if (i != rrclass) {
          i = 255;
        }
        if (j != rrtype) {
          j = 255;
        }
      }
    }
    return new CT(i, j);
  }
  
  private NameNode getNameNode(DnsName paramDnsName)
    throws NamingException
  {
    dprint("getNameNode(" + paramDnsName + ")");
    ZoneNode localZoneNode;
    synchronized (zoneTree)
    {
      localZoneNode = zoneTree.getDeepestPopulated(paramDnsName);
    }
    dprint("Deepest related zone in zone tree: " + (localZoneNode != null ? localZoneNode.getLabel() : "[none]"));
    if (localZoneNode != null)
    {
      synchronized (localZoneNode)
      {
        ??? = localZoneNode.getContents();
      }
      if (??? != null)
      {
        localNameNode = ((NameNode)???).get(paramDnsName, localZoneNode.depth() + 1);
        if ((localNameNode != null) && (!localNameNode.isZoneCut()))
        {
          dprint("Found node " + paramDnsName + " in zone tree");
          localDnsName = (DnsName)paramDnsName.getPrefix(localZoneNode.depth() + 1);
          ??? = isZoneCurrent(localZoneNode, localDnsName);
          int i = 0;
          synchronized (localZoneNode)
          {
            if (??? != localZoneNode.getContents()) {
              i = 1;
            } else if (??? == 0) {
              localZoneNode.depopulate();
            } else {
              return localNameNode;
            }
          }
          dprint("Zone not current; discarding node");
          if (i != 0) {
            return getNameNode(paramDnsName);
          }
        }
      }
    }
    dprint("Adding node " + paramDnsName + " to zone tree");
    DnsName localDnsName = getResolver().findZoneName(paramDnsName, 1, recursion);
    dprint("Node's zone is " + localDnsName);
    synchronized (zoneTree)
    {
      localZoneNode = (ZoneNode)zoneTree.add(localDnsName, 1);
    }
    synchronized (localZoneNode)
    {
      ??? = localZoneNode.isPopulated() ? localZoneNode.getContents() : populateZone(localZoneNode, localDnsName);
    }
    NameNode localNameNode = ((NameNode)???).get(paramDnsName, localDnsName.size());
    if (localNameNode == null) {
      throw new ConfigurationException("DNS error: node not found in its own zone");
    }
    dprint("Found node in newly-populated zone");
    return localNameNode;
  }
  
  private NameNode populateZone(ZoneNode paramZoneNode, DnsName paramDnsName)
    throws NamingException
  {
    dprint("Populating zone " + paramDnsName);
    ResourceRecords localResourceRecords = getResolver().queryZone(paramDnsName, 1, recursion);
    dprint("zone xfer complete: " + answer.size() + " records");
    return paramZoneNode.populate(paramDnsName, localResourceRecords);
  }
  
  private boolean isZoneCurrent(ZoneNode paramZoneNode, DnsName paramDnsName)
    throws NamingException
  {
    if (!paramZoneNode.isPopulated()) {
      return false;
    }
    ResourceRecord localResourceRecord = getResolver().findSoa(paramDnsName, 1, recursion);
    synchronized (paramZoneNode)
    {
      if (localResourceRecord == null) {
        paramZoneNode.depopulate();
      }
      return (paramZoneNode.isPopulated()) && (paramZoneNode.compareSerialNumberTo(localResourceRecord) >= 0);
    }
  }
  
  private static final void dprint(String paramString) {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\dns\DnsContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */