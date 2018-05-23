package com.sun.jndi.ldap;

import com.sun.jndi.ldap.ext.StartTlsResponseImpl;
import com.sun.jndi.toolkit.ctx.ComponentDirContext;
import com.sun.jndi.toolkit.ctx.Continuation;
import com.sun.jndi.toolkit.dir.HierMemDirCtx;
import com.sun.jndi.toolkit.dir.SearchFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.naming.AuthenticationException;
import javax.naming.AuthenticationNotSupportedException;
import javax.naming.Binding;
import javax.naming.CommunicationException;
import javax.naming.CompositeName;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.ContextNotEmptyException;
import javax.naming.InvalidNameException;
import javax.naming.LimitExceededException;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NoPermissionException;
import javax.naming.OperationNotSupportedException;
import javax.naming.PartialResultException;
import javax.naming.ServiceUnavailableException;
import javax.naming.SizeLimitExceededException;
import javax.naming.TimeLimitExceededException;
import javax.naming.directory.Attribute;
import javax.naming.directory.AttributeInUseException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InvalidAttributeIdentifierException;
import javax.naming.directory.InvalidAttributeValueException;
import javax.naming.directory.InvalidSearchFilterException;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.NoSuchAttributeException;
import javax.naming.directory.SchemaViolationException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.event.EventDirContext;
import javax.naming.event.NamingListener;
import javax.naming.ldap.Control;
import javax.naming.ldap.ControlFactory;
import javax.naming.ldap.ExtendedRequest;
import javax.naming.ldap.ExtendedResponse;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.naming.ldap.UnsolicitedNotificationListener;
import javax.naming.spi.DirectoryManager;

public final class LdapCtx
  extends ComponentDirContext
  implements EventDirContext, LdapContext
{
  private static final boolean debug = false;
  private static final boolean HARD_CLOSE = true;
  private static final boolean SOFT_CLOSE = false;
  public static final int DEFAULT_PORT = 389;
  public static final int DEFAULT_SSL_PORT = 636;
  public static final String DEFAULT_HOST = "localhost";
  private static final boolean DEFAULT_DELETE_RDN = true;
  private static final boolean DEFAULT_TYPES_ONLY = false;
  private static final int DEFAULT_DEREF_ALIASES = 3;
  private static final int DEFAULT_LDAP_VERSION = 32;
  private static final int DEFAULT_BATCH_SIZE = 1;
  private static final int DEFAULT_REFERRAL_MODE = 3;
  private static final char DEFAULT_REF_SEPARATOR = '#';
  static final String DEFAULT_SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
  private static final int DEFAULT_REFERRAL_LIMIT = 10;
  private static final String STARTTLS_REQ_OID = "1.3.6.1.4.1.1466.20037";
  private static final String[] SCHEMA_ATTRIBUTES = { "objectClasses", "attributeTypes", "matchingRules", "ldapSyntaxes" };
  private static final String VERSION = "java.naming.ldap.version";
  private static final String BINARY_ATTRIBUTES = "java.naming.ldap.attributes.binary";
  private static final String DELETE_RDN = "java.naming.ldap.deleteRDN";
  private static final String DEREF_ALIASES = "java.naming.ldap.derefAliases";
  private static final String TYPES_ONLY = "java.naming.ldap.typesOnly";
  private static final String REF_SEPARATOR = "java.naming.ldap.ref.separator";
  private static final String SOCKET_FACTORY = "java.naming.ldap.factory.socket";
  static final String BIND_CONTROLS = "java.naming.ldap.control.connect";
  private static final String REFERRAL_LIMIT = "java.naming.ldap.referral.limit";
  private static final String TRACE_BER = "com.sun.jndi.ldap.trace.ber";
  private static final String NETSCAPE_SCHEMA_BUG = "com.sun.jndi.ldap.netscape.schemaBugs";
  private static final String OLD_NETSCAPE_SCHEMA_BUG = "com.sun.naming.netscape.schemaBugs";
  private static final String CONNECT_TIMEOUT = "com.sun.jndi.ldap.connect.timeout";
  private static final String READ_TIMEOUT = "com.sun.jndi.ldap.read.timeout";
  private static final String ENABLE_POOL = "com.sun.jndi.ldap.connect.pool";
  private static final String DOMAIN_NAME = "com.sun.jndi.ldap.domainname";
  private static final String WAIT_FOR_REPLY = "com.sun.jndi.ldap.search.waitForReply";
  private static final String REPLY_QUEUE_SIZE = "com.sun.jndi.ldap.search.replyQueueSize";
  private static final NameParser parser = new LdapNameParser();
  private static final ControlFactory myResponseControlFactory = new DefaultResponseControlFactory();
  private static final Control manageReferralControl = new ManageReferralControl(false);
  private static final HierMemDirCtx EMPTY_SCHEMA = new HierMemDirCtx();
  int port_number;
  String hostname = null;
  LdapClient clnt = null;
  Hashtable<String, Object> envprops = null;
  int handleReferrals = 3;
  boolean hasLdapsScheme = false;
  String currentDN;
  Name currentParsedDN;
  Vector<Control> respCtls = null;
  Control[] reqCtls = null;
  private OutputStream trace = null;
  private boolean netscapeSchemaBug = false;
  private Control[] bindCtls = null;
  private int referralHopLimit = 10;
  private Hashtable<String, DirContext> schemaTrees = null;
  private int batchSize = 1;
  private boolean deleteRDN = true;
  private boolean typesOnly = false;
  private int derefAliases = 3;
  private char addrEncodingSeparator = '#';
  private Hashtable<String, Boolean> binaryAttrs = null;
  private int connectTimeout = -1;
  private int readTimeout = -1;
  private boolean waitForReply = true;
  private int replyQueueSize = -1;
  private boolean useSsl = false;
  private boolean useDefaultPortNumber = false;
  private boolean parentIsLdapCtx = false;
  private int hopCount = 1;
  private String url = null;
  private EventSupport eventSupport;
  private boolean unsolicited = false;
  private boolean sharable = true;
  private int enumCount = 0;
  private boolean closeRequested = false;
  
  public LdapCtx(String paramString1, String paramString2, int paramInt, Hashtable<?, ?> paramHashtable, boolean paramBoolean)
    throws NamingException
  {
    useSsl = (hasLdapsScheme = paramBoolean);
    if (paramHashtable != null)
    {
      envprops = ((Hashtable)paramHashtable.clone());
      if ("ssl".equals(envprops.get("java.naming.security.protocol"))) {
        useSsl = true;
      }
      trace = ((OutputStream)envprops.get("com.sun.jndi.ldap.trace.ber"));
      if ((paramHashtable.get("com.sun.jndi.ldap.netscape.schemaBugs") != null) || (paramHashtable.get("com.sun.naming.netscape.schemaBugs") != null)) {
        netscapeSchemaBug = true;
      }
    }
    currentDN = (paramString1 != null ? paramString1 : "");
    currentParsedDN = parser.parse(currentDN);
    hostname = ((paramString2 != null) && (paramString2.length() > 0) ? paramString2 : "localhost");
    if (hostname.charAt(0) == '[') {
      hostname = hostname.substring(1, hostname.length() - 1);
    }
    if (paramInt > 0)
    {
      port_number = paramInt;
    }
    else
    {
      port_number = (useSsl ? 636 : 389);
      useDefaultPortNumber = true;
    }
    schemaTrees = new Hashtable(11, 0.75F);
    initEnv();
    try
    {
      connect(false);
    }
    catch (NamingException localNamingException)
    {
      try
      {
        close();
      }
      catch (Exception localException) {}
      throw localNamingException;
    }
  }
  
  LdapCtx(LdapCtx paramLdapCtx, String paramString)
    throws NamingException
  {
    useSsl = useSsl;
    hasLdapsScheme = hasLdapsScheme;
    useDefaultPortNumber = useDefaultPortNumber;
    hostname = hostname;
    port_number = port_number;
    currentDN = paramString;
    if (currentDN == currentDN) {
      currentParsedDN = currentParsedDN;
    } else {
      currentParsedDN = parser.parse(currentDN);
    }
    envprops = envprops;
    schemaTrees = schemaTrees;
    clnt = clnt;
    clnt.incRefCount();
    parentIsLdapCtx = ((paramString == null) || (paramString.equals(currentDN)) ? parentIsLdapCtx : true);
    trace = trace;
    netscapeSchemaBug = netscapeSchemaBug;
    initEnv();
  }
  
  public LdapContext newInstance(Control[] paramArrayOfControl)
    throws NamingException
  {
    LdapCtx localLdapCtx = new LdapCtx(this, currentDN);
    localLdapCtx.setRequestControls(paramArrayOfControl);
    return localLdapCtx;
  }
  
  protected void c_bind(Name paramName, Object paramObject, Continuation paramContinuation)
    throws NamingException
  {
    c_bind(paramName, paramObject, null, paramContinuation);
  }
  
  protected void c_bind(Name paramName, Object paramObject, Attributes paramAttributes, Continuation paramContinuation)
    throws NamingException
  {
    paramContinuation.setError(this, paramName);
    Attributes localAttributes = paramAttributes;
    try
    {
      ensureOpen();
      if (paramObject == null)
      {
        if (paramAttributes == null) {
          throw new IllegalArgumentException("cannot bind null object with no attributes");
        }
      }
      else {
        paramAttributes = Obj.determineBindAttrs(addrEncodingSeparator, paramObject, paramAttributes, false, paramName, this, envprops);
      }
      String str = fullyQualifiedName(paramName);
      paramAttributes = addRdnAttributes(str, paramAttributes, localAttributes != paramAttributes);
      localObject2 = new LdapEntry(str, paramAttributes);
      LdapResult localLdapResult = clnt.add((LdapEntry)localObject2, reqCtls);
      respCtls = resControls;
      if (status != 0) {
        processReturnCode(localLdapResult, paramName);
      }
    }
    catch (LdapReferralException localLdapReferralException1)
    {
      if (handleReferrals == 2) {
        throw paramContinuation.fillInException(localLdapReferralException1);
      }
      for (;;)
      {
        localObject2 = (LdapReferralContext)localLdapReferralException1.getReferralContext(envprops, bindCtls);
        try
        {
          ((LdapReferralContext)localObject2).bind(paramName, paramObject, localAttributes);
          return;
        }
        catch (LdapReferralException localLdapReferralException2)
        {
          Object localObject1 = localLdapReferralException2;
          ((LdapReferralContext)localObject2).close();
        }
        finally
        {
          ((LdapReferralContext)localObject2).close();
        }
      }
    }
    catch (IOException localIOException)
    {
      Object localObject2 = new CommunicationException(localIOException.getMessage());
      ((NamingException)localObject2).setRootCause(localIOException);
      throw paramContinuation.fillInException((NamingException)localObject2);
    }
    catch (NamingException localNamingException)
    {
      throw paramContinuation.fillInException(localNamingException);
    }
  }
  
  protected void c_rebind(Name paramName, Object paramObject, Continuation paramContinuation)
    throws NamingException
  {
    c_rebind(paramName, paramObject, null, paramContinuation);
  }
  
  protected void c_rebind(Name paramName, Object paramObject, Attributes paramAttributes, Continuation paramContinuation)
    throws NamingException
  {
    paramContinuation.setError(this, paramName);
    Attributes localAttributes1 = paramAttributes;
    try
    {
      Attributes localAttributes2 = null;
      try
      {
        localAttributes2 = c_getAttributes(paramName, null, paramContinuation);
      }
      catch (NameNotFoundException localNameNotFoundException) {}
      if (localAttributes2 == null)
      {
        c_bind(paramName, paramObject, paramAttributes, paramContinuation);
        return;
      }
      if ((paramAttributes == null) && ((paramObject instanceof DirContext))) {
        paramAttributes = ((DirContext)paramObject).getAttributes("");
      }
      localObject2 = (Attributes)localAttributes2.clone();
      if (paramAttributes == null)
      {
        localObject3 = localAttributes2.get(Obj.JAVA_ATTRIBUTES[0]);
        if (localObject3 != null)
        {
          localObject3 = (Attribute)((Attribute)localObject3).clone();
          for (i = 0; i < Obj.JAVA_OBJECT_CLASSES.length; i++)
          {
            ((Attribute)localObject3).remove(Obj.JAVA_OBJECT_CLASSES_LOWER[i]);
            ((Attribute)localObject3).remove(Obj.JAVA_OBJECT_CLASSES[i]);
          }
          localAttributes2.put((Attribute)localObject3);
        }
        for (int i = 1; i < Obj.JAVA_ATTRIBUTES.length; i++) {
          localAttributes2.remove(Obj.JAVA_ATTRIBUTES[i]);
        }
        paramAttributes = localAttributes2;
      }
      if (paramObject != null) {
        paramAttributes = Obj.determineBindAttrs(addrEncodingSeparator, paramObject, paramAttributes, localAttributes1 != paramAttributes, paramName, this, envprops);
      }
      Object localObject3 = fullyQualifiedName(paramName);
      LdapResult localLdapResult1 = clnt.delete((String)localObject3, reqCtls);
      respCtls = resControls;
      if (status != 0)
      {
        processReturnCode(localLdapResult1, paramName);
        return;
      }
      Object localObject4 = null;
      try
      {
        paramAttributes = addRdnAttributes((String)localObject3, paramAttributes, localAttributes1 != paramAttributes);
        LdapEntry localLdapEntry = new LdapEntry((String)localObject3, paramAttributes);
        localLdapResult1 = clnt.add(localLdapEntry, reqCtls);
        if (resControls != null) {
          respCtls = appendVector(respCtls, resControls);
        }
      }
      catch (NamingException|IOException localNamingException2)
      {
        localObject4 = localNamingException2;
      }
      if (((localObject4 != null) && (!(localObject4 instanceof LdapReferralException))) || (status != 0))
      {
        LdapResult localLdapResult2 = clnt.add(new LdapEntry((String)localObject3, (Attributes)localObject2), reqCtls);
        if (resControls != null) {
          respCtls = appendVector(respCtls, resControls);
        }
        if (localObject4 == null) {
          processReturnCode(localLdapResult1, paramName);
        }
      }
      if ((localObject4 instanceof NamingException)) {
        throw ((NamingException)localObject4);
      }
      if ((localObject4 instanceof IOException)) {
        throw ((IOException)localObject4);
      }
    }
    catch (LdapReferralException localLdapReferralException1)
    {
      if (handleReferrals == 2) {
        throw paramContinuation.fillInException(localLdapReferralException1);
      }
      for (;;)
      {
        localObject2 = (LdapReferralContext)localLdapReferralException1.getReferralContext(envprops, bindCtls);
        try
        {
          ((LdapReferralContext)localObject2).rebind(paramName, paramObject, localAttributes1);
          return;
        }
        catch (LdapReferralException localLdapReferralException2)
        {
          Object localObject1 = localLdapReferralException2;
          ((LdapReferralContext)localObject2).close();
        }
        finally
        {
          ((LdapReferralContext)localObject2).close();
        }
      }
    }
    catch (IOException localIOException)
    {
      Object localObject2 = new CommunicationException(localIOException.getMessage());
      ((NamingException)localObject2).setRootCause(localIOException);
      throw paramContinuation.fillInException((NamingException)localObject2);
    }
    catch (NamingException localNamingException1)
    {
      throw paramContinuation.fillInException(localNamingException1);
    }
  }
  
  protected void c_unbind(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    paramContinuation.setError(this, paramName);
    try
    {
      ensureOpen();
      String str = fullyQualifiedName(paramName);
      localObject2 = clnt.delete(str, reqCtls);
      respCtls = resControls;
      adjustDeleteStatus(str, (LdapResult)localObject2);
      if (status != 0) {
        processReturnCode((LdapResult)localObject2, paramName);
      }
    }
    catch (LdapReferralException localLdapReferralException1)
    {
      if (handleReferrals == 2) {
        throw paramContinuation.fillInException(localLdapReferralException1);
      }
      for (;;)
      {
        localObject2 = (LdapReferralContext)localLdapReferralException1.getReferralContext(envprops, bindCtls);
        try
        {
          ((LdapReferralContext)localObject2).unbind(paramName);
          return;
        }
        catch (LdapReferralException localLdapReferralException2)
        {
          Object localObject1 = localLdapReferralException2;
          ((LdapReferralContext)localObject2).close();
        }
        finally
        {
          ((LdapReferralContext)localObject2).close();
        }
      }
    }
    catch (IOException localIOException)
    {
      Object localObject2 = new CommunicationException(localIOException.getMessage());
      ((NamingException)localObject2).setRootCause(localIOException);
      throw paramContinuation.fillInException((NamingException)localObject2);
    }
    catch (NamingException localNamingException)
    {
      throw paramContinuation.fillInException(localNamingException);
    }
  }
  
  protected void c_rename(Name paramName1, Name paramName2, Continuation paramContinuation)
    throws NamingException
  {
    String str1 = null;
    String str2 = null;
    paramContinuation.setError(this, paramName1);
    try
    {
      ensureOpen();
      Name localName3;
      if (paramName1.isEmpty())
      {
        localName3 = parser.parse("");
      }
      else
      {
        Name localName1 = parser.parse(paramName1.get(0));
        localName3 = localName1.getPrefix(localName1.size() - 1);
      }
      Name localName2;
      if ((paramName2 instanceof CompositeName)) {
        localName2 = parser.parse(paramName2.get(0));
      } else {
        localName2 = paramName2;
      }
      Name localName4 = localName2.getPrefix(localName2.size() - 1);
      if (!localName3.equals(localName4))
      {
        if (!clnt.isLdapv3) {
          throw new InvalidNameException("LDAPv2 doesn't support changing the parent as a result of a rename");
        }
        str2 = fullyQualifiedName(localName4.toString());
      }
      str1 = localName2.get(localName2.size() - 1);
      LdapResult localLdapResult = clnt.moddn(fullyQualifiedName(paramName1), str1, deleteRDN, str2, reqCtls);
      respCtls = resControls;
      if (status != 0) {
        processReturnCode(localLdapResult, paramName1);
      }
    }
    catch (LdapReferralException localLdapReferralException1)
    {
      localLdapReferralException1.setNewRdn(str1);
      if (str2 != null)
      {
        localObject2 = new PartialResultException("Cannot continue referral processing when newSuperior is nonempty: " + str2);
        ((PartialResultException)localObject2).setRootCause(paramContinuation.fillInException(localLdapReferralException1));
        throw paramContinuation.fillInException((NamingException)localObject2);
      }
      if (handleReferrals == 2) {
        throw paramContinuation.fillInException(localLdapReferralException1);
      }
      for (;;)
      {
        localObject2 = (LdapReferralContext)localLdapReferralException1.getReferralContext(envprops, bindCtls);
        try
        {
          ((LdapReferralContext)localObject2).rename(paramName1, paramName2);
          return;
        }
        catch (LdapReferralException localLdapReferralException2)
        {
          Object localObject1 = localLdapReferralException2;
          ((LdapReferralContext)localObject2).close();
        }
        finally
        {
          ((LdapReferralContext)localObject2).close();
        }
      }
    }
    catch (IOException localIOException)
    {
      Object localObject2 = new CommunicationException(localIOException.getMessage());
      ((NamingException)localObject2).setRootCause(localIOException);
      throw paramContinuation.fillInException((NamingException)localObject2);
    }
    catch (NamingException localNamingException)
    {
      throw paramContinuation.fillInException(localNamingException);
    }
  }
  
  protected Context c_createSubcontext(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    return c_createSubcontext(paramName, null, paramContinuation);
  }
  
  protected DirContext c_createSubcontext(Name paramName, Attributes paramAttributes, Continuation paramContinuation)
    throws NamingException
  {
    paramContinuation.setError(this, paramName);
    Attributes localAttributes = paramAttributes;
    try
    {
      ensureOpen();
      if (paramAttributes == null)
      {
        localObject1 = new BasicAttribute(Obj.JAVA_ATTRIBUTES[0], Obj.JAVA_OBJECT_CLASSES[0]);
        ((Attribute)localObject1).add("top");
        paramAttributes = new BasicAttributes(true);
        paramAttributes.put((Attribute)localObject1);
      }
      Object localObject1 = fullyQualifiedName(paramName);
      paramAttributes = addRdnAttributes((String)localObject1, paramAttributes, localAttributes != paramAttributes);
      localObject3 = new LdapEntry((String)localObject1, paramAttributes);
      localObject4 = clnt.add((LdapEntry)localObject3, reqCtls);
      respCtls = resControls;
      if (status != 0)
      {
        processReturnCode((LdapResult)localObject4, paramName);
        return null;
      }
      return new LdapCtx(this, (String)localObject1);
    }
    catch (LdapReferralException localLdapReferralException1)
    {
      Object localObject4;
      if (handleReferrals == 2) {
        throw paramContinuation.fillInException(localLdapReferralException1);
      }
      for (;;)
      {
        localObject3 = (LdapReferralContext)localLdapReferralException1.getReferralContext(envprops, bindCtls);
        try
        {
          localObject4 = ((LdapReferralContext)localObject3).createSubcontext(paramName, localAttributes);
          return (DirContext)localObject4;
        }
        catch (LdapReferralException localLdapReferralException2)
        {
          Object localObject2 = localLdapReferralException2;
          ((LdapReferralContext)localObject3).close();
        }
        finally
        {
          ((LdapReferralContext)localObject3).close();
        }
      }
    }
    catch (IOException localIOException)
    {
      Object localObject3 = new CommunicationException(localIOException.getMessage());
      ((NamingException)localObject3).setRootCause(localIOException);
      throw paramContinuation.fillInException((NamingException)localObject3);
    }
    catch (NamingException localNamingException)
    {
      throw paramContinuation.fillInException(localNamingException);
    }
  }
  
  protected void c_destroySubcontext(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    paramContinuation.setError(this, paramName);
    try
    {
      ensureOpen();
      String str = fullyQualifiedName(paramName);
      localObject2 = clnt.delete(str, reqCtls);
      respCtls = resControls;
      adjustDeleteStatus(str, (LdapResult)localObject2);
      if (status != 0) {
        processReturnCode((LdapResult)localObject2, paramName);
      }
    }
    catch (LdapReferralException localLdapReferralException1)
    {
      if (handleReferrals == 2) {
        throw paramContinuation.fillInException(localLdapReferralException1);
      }
      for (;;)
      {
        localObject2 = (LdapReferralContext)localLdapReferralException1.getReferralContext(envprops, bindCtls);
        try
        {
          ((LdapReferralContext)localObject2).destroySubcontext(paramName);
          return;
        }
        catch (LdapReferralException localLdapReferralException2)
        {
          Object localObject1 = localLdapReferralException2;
          ((LdapReferralContext)localObject2).close();
        }
        finally
        {
          ((LdapReferralContext)localObject2).close();
        }
      }
    }
    catch (IOException localIOException)
    {
      Object localObject2 = new CommunicationException(localIOException.getMessage());
      ((NamingException)localObject2).setRootCause(localIOException);
      throw paramContinuation.fillInException((NamingException)localObject2);
    }
    catch (NamingException localNamingException)
    {
      throw paramContinuation.fillInException(localNamingException);
    }
  }
  
  private static Attributes addRdnAttributes(String paramString, Attributes paramAttributes, boolean paramBoolean)
    throws NamingException
  {
    if (paramString.equals("")) {
      return paramAttributes;
    }
    List localList = new LdapName(paramString).getRdns();
    Rdn localRdn = (Rdn)localList.get(localList.size() - 1);
    Attributes localAttributes = localRdn.toAttributes();
    NamingEnumeration localNamingEnumeration = localAttributes.getAll();
    while (localNamingEnumeration.hasMore())
    {
      Attribute localAttribute = (Attribute)localNamingEnumeration.next();
      if ((paramAttributes.get(localAttribute.getID()) == null) && ((paramAttributes.isCaseIgnored()) || (!containsIgnoreCase(paramAttributes.getIDs(), localAttribute.getID()))))
      {
        if (!paramBoolean)
        {
          paramAttributes = (Attributes)paramAttributes.clone();
          paramBoolean = true;
        }
        paramAttributes.put(localAttribute);
      }
    }
    return paramAttributes;
  }
  
  private static boolean containsIgnoreCase(NamingEnumeration<String> paramNamingEnumeration, String paramString)
    throws NamingException
  {
    while (paramNamingEnumeration.hasMore())
    {
      String str = (String)paramNamingEnumeration.next();
      if (str.equalsIgnoreCase(paramString)) {
        return true;
      }
    }
    return false;
  }
  
  private void adjustDeleteStatus(String paramString, LdapResult paramLdapResult)
  {
    if ((status == 32) && (matchedDN != null)) {
      try
      {
        Name localName1 = parser.parse(paramString);
        Name localName2 = parser.parse(matchedDN);
        if (localName1.size() - localName2.size() == 1) {
          status = 0;
        }
      }
      catch (NamingException localNamingException) {}
    }
  }
  
  private static <T> Vector<T> appendVector(Vector<T> paramVector1, Vector<T> paramVector2)
  {
    if (paramVector1 == null) {
      paramVector1 = paramVector2;
    } else {
      for (int i = 0; i < paramVector2.size(); i++) {
        paramVector1.addElement(paramVector2.elementAt(i));
      }
    }
    return paramVector1;
  }
  
  protected Object c_lookupLink(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    return c_lookup(paramName, paramContinuation);
  }
  
  protected Object c_lookup(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    paramContinuation.setError(this, paramName);
    Object localObject1 = null;
    Object localObject4;
    Object localObject2;
    try
    {
      SearchControls localSearchControls = new SearchControls();
      localSearchControls.setSearchScope(0);
      localSearchControls.setReturningAttributes(null);
      localSearchControls.setReturningObjFlag(true);
      localObject4 = doSearchOnce(paramName, "(objectClass=*)", localSearchControls, true);
      respCtls = resControls;
      if (status != 0) {
        processReturnCode((LdapResult)localObject4, paramName);
      }
      if ((entries == null) || (entries.size() != 1))
      {
        localObject2 = new BasicAttributes(true);
      }
      else
      {
        localObject5 = (LdapEntry)entries.elementAt(0);
        localObject2 = attributes;
        Vector localVector = respCtls;
        if (localVector != null) {
          appendVector(respCtls, localVector);
        }
      }
      if (((Attributes)localObject2).get(Obj.JAVA_ATTRIBUTES[2]) != null) {
        localObject1 = Obj.decodeObject((Attributes)localObject2);
      }
      if (localObject1 == null) {
        localObject1 = new LdapCtx(this, fullyQualifiedName(paramName));
      }
    }
    catch (LdapReferralException localLdapReferralException1)
    {
      Object localObject5;
      if (handleReferrals == 2) {
        throw paramContinuation.fillInException(localLdapReferralException1);
      }
      for (;;)
      {
        localObject4 = (LdapReferralContext)localLdapReferralException1.getReferralContext(envprops, bindCtls);
        try
        {
          localObject5 = ((LdapReferralContext)localObject4).lookup(paramName);
          return localObject5;
        }
        catch (LdapReferralException localLdapReferralException2)
        {
          Object localObject3 = localLdapReferralException2;
          ((LdapReferralContext)localObject4).close();
        }
        finally
        {
          ((LdapReferralContext)localObject4).close();
        }
      }
    }
    catch (NamingException localNamingException1)
    {
      throw paramContinuation.fillInException(localNamingException1);
    }
    try
    {
      return DirectoryManager.getObjectInstance(localObject1, paramName, this, envprops, (Attributes)localObject2);
    }
    catch (NamingException localNamingException2)
    {
      throw paramContinuation.fillInException(localNamingException2);
    }
    catch (Exception localException)
    {
      localObject4 = new NamingException("problem generating object using object factory");
      ((NamingException)localObject4).setRootCause(localException);
      throw paramContinuation.fillInException((NamingException)localObject4);
    }
  }
  
  protected NamingEnumeration<NameClassPair> c_list(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    SearchControls localSearchControls = new SearchControls();
    String[] arrayOfString = new String[2];
    arrayOfString[0] = Obj.JAVA_ATTRIBUTES[0];
    arrayOfString[1] = Obj.JAVA_ATTRIBUTES[2];
    localSearchControls.setReturningAttributes(arrayOfString);
    localSearchControls.setReturningObjFlag(true);
    paramContinuation.setError(this, paramName);
    LdapResult localLdapResult = null;
    try
    {
      localLdapResult = doSearch(paramName, "(objectClass=*)", localSearchControls, true, true);
      if ((status != 0) || (referrals != null)) {
        processReturnCode(localLdapResult, paramName);
      }
      return new LdapNamingEnumeration(this, localLdapResult, paramName, paramContinuation);
    }
    catch (LdapReferralException localLdapReferralException1)
    {
      if (handleReferrals == 2) {
        throw paramContinuation.fillInException(localLdapReferralException1);
      }
      for (;;)
      {
        localObject2 = (LdapReferralContext)localLdapReferralException1.getReferralContext(envprops, bindCtls);
        try
        {
          NamingEnumeration localNamingEnumeration = ((LdapReferralContext)localObject2).list(paramName);
          return localNamingEnumeration;
        }
        catch (LdapReferralException localLdapReferralException2)
        {
          Object localObject1 = localLdapReferralException2;
          ((LdapReferralContext)localObject2).close();
        }
        finally
        {
          ((LdapReferralContext)localObject2).close();
        }
      }
    }
    catch (LimitExceededException localLimitExceededException)
    {
      localObject2 = new LdapNamingEnumeration(this, localLdapResult, paramName, paramContinuation);
      ((LdapNamingEnumeration)localObject2).setNamingException((LimitExceededException)paramContinuation.fillInException(localLimitExceededException));
      return (NamingEnumeration<NameClassPair>)localObject2;
    }
    catch (PartialResultException localPartialResultException)
    {
      Object localObject2 = new LdapNamingEnumeration(this, localLdapResult, paramName, paramContinuation);
      ((LdapNamingEnumeration)localObject2).setNamingException((PartialResultException)paramContinuation.fillInException(localPartialResultException));
      return (NamingEnumeration<NameClassPair>)localObject2;
    }
    catch (NamingException localNamingException)
    {
      throw paramContinuation.fillInException(localNamingException);
    }
  }
  
  protected NamingEnumeration<Binding> c_listBindings(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    SearchControls localSearchControls = new SearchControls();
    localSearchControls.setReturningAttributes(null);
    localSearchControls.setReturningObjFlag(true);
    paramContinuation.setError(this, paramName);
    LdapResult localLdapResult = null;
    try
    {
      localLdapResult = doSearch(paramName, "(objectClass=*)", localSearchControls, true, true);
      if ((status != 0) || (referrals != null)) {
        processReturnCode(localLdapResult, paramName);
      }
      return new LdapBindingEnumeration(this, localLdapResult, paramName, paramContinuation);
    }
    catch (LdapReferralException localLdapReferralException1)
    {
      if (handleReferrals == 2) {
        throw paramContinuation.fillInException(localLdapReferralException1);
      }
      for (;;)
      {
        localObject2 = (LdapReferralContext)localLdapReferralException1.getReferralContext(envprops, bindCtls);
        try
        {
          NamingEnumeration localNamingEnumeration = ((LdapReferralContext)localObject2).listBindings(paramName);
          return localNamingEnumeration;
        }
        catch (LdapReferralException localLdapReferralException2)
        {
          Object localObject1 = localLdapReferralException2;
          ((LdapReferralContext)localObject2).close();
        }
        finally
        {
          ((LdapReferralContext)localObject2).close();
        }
      }
    }
    catch (LimitExceededException localLimitExceededException)
    {
      localObject2 = new LdapBindingEnumeration(this, localLdapResult, paramName, paramContinuation);
      ((LdapBindingEnumeration)localObject2).setNamingException(paramContinuation.fillInException(localLimitExceededException));
      return (NamingEnumeration<Binding>)localObject2;
    }
    catch (PartialResultException localPartialResultException)
    {
      Object localObject2 = new LdapBindingEnumeration(this, localLdapResult, paramName, paramContinuation);
      ((LdapBindingEnumeration)localObject2).setNamingException(paramContinuation.fillInException(localPartialResultException));
      return (NamingEnumeration<Binding>)localObject2;
    }
    catch (NamingException localNamingException)
    {
      throw paramContinuation.fillInException(localNamingException);
    }
  }
  
  protected NameParser c_getNameParser(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    paramContinuation.setSuccess();
    return parser;
  }
  
  public String getNameInNamespace()
  {
    return currentDN;
  }
  
  public Name composeName(Name paramName1, Name paramName2)
    throws NamingException
  {
    if (((paramName1 instanceof LdapName)) && ((paramName2 instanceof LdapName)))
    {
      localName = (Name)paramName2.clone();
      localName.addAll(paramName1);
      return new CompositeName().add(localName.toString());
    }
    if (!(paramName1 instanceof CompositeName)) {
      paramName1 = new CompositeName().add(paramName1.toString());
    }
    if (!(paramName2 instanceof CompositeName)) {
      paramName2 = new CompositeName().add(paramName2.toString());
    }
    int i = paramName2.size() - 1;
    if ((paramName1.isEmpty()) || (paramName2.isEmpty()) || (paramName1.get(0).equals("")) || (paramName2.get(i).equals(""))) {
      return super.composeName(paramName1, paramName2);
    }
    Name localName = (Name)paramName2.clone();
    localName.addAll(paramName1);
    if (parentIsLdapCtx)
    {
      String str = concatNames(localName.get(i + 1), localName.get(i));
      localName.remove(i + 1);
      localName.remove(i);
      localName.add(i, str);
    }
    return localName;
  }
  
  private String fullyQualifiedName(Name paramName)
  {
    return paramName.isEmpty() ? currentDN : fullyQualifiedName(paramName.get(0));
  }
  
  private String fullyQualifiedName(String paramString)
  {
    return concatNames(paramString, currentDN);
  }
  
  private static String concatNames(String paramString1, String paramString2)
  {
    if ((paramString1 == null) || (paramString1.equals(""))) {
      return paramString2;
    }
    if ((paramString2 == null) || (paramString2.equals(""))) {
      return paramString1;
    }
    return paramString1 + "," + paramString2;
  }
  
  protected Attributes c_getAttributes(Name paramName, String[] paramArrayOfString, Continuation paramContinuation)
    throws NamingException
  {
    paramContinuation.setError(this, paramName);
    SearchControls localSearchControls = new SearchControls();
    localSearchControls.setSearchScope(0);
    localSearchControls.setReturningAttributes(paramArrayOfString);
    try
    {
      LdapResult localLdapResult = doSearchOnce(paramName, "(objectClass=*)", localSearchControls, true);
      respCtls = resControls;
      if (status != 0) {
        processReturnCode(localLdapResult, paramName);
      }
      if ((entries == null) || (entries.size() != 1)) {
        return new BasicAttributes(true);
      }
      localObject2 = (LdapEntry)entries.elementAt(0);
      localObject3 = respCtls;
      if (localObject3 != null) {
        appendVector(respCtls, (Vector)localObject3);
      }
      setParents(attributes, (Name)paramName.clone());
      return attributes;
    }
    catch (LdapReferralException localLdapReferralException1)
    {
      Object localObject2;
      Object localObject3;
      if (handleReferrals == 2) {
        throw paramContinuation.fillInException(localLdapReferralException1);
      }
      for (;;)
      {
        localObject2 = (LdapReferralContext)localLdapReferralException1.getReferralContext(envprops, bindCtls);
        try
        {
          localObject3 = ((LdapReferralContext)localObject2).getAttributes(paramName, paramArrayOfString);
          return (Attributes)localObject3;
        }
        catch (LdapReferralException localLdapReferralException2)
        {
          Object localObject1 = localLdapReferralException2;
          ((LdapReferralContext)localObject2).close();
        }
        finally
        {
          ((LdapReferralContext)localObject2).close();
        }
      }
    }
    catch (NamingException localNamingException)
    {
      throw paramContinuation.fillInException(localNamingException);
    }
  }
  
  protected void c_modifyAttributes(Name paramName, int paramInt, Attributes paramAttributes, Continuation paramContinuation)
    throws NamingException
  {
    paramContinuation.setError(this, paramName);
    try
    {
      ensureOpen();
      if ((paramAttributes == null) || (paramAttributes.size() == 0)) {
        return;
      }
      String str = fullyQualifiedName(paramName);
      int i = convertToLdapModCode(paramInt);
      int[] arrayOfInt = new int[paramAttributes.size()];
      Attribute[] arrayOfAttribute = new Attribute[paramAttributes.size()];
      NamingEnumeration localNamingEnumeration = paramAttributes.getAll();
      for (int j = 0; (j < arrayOfInt.length) && (localNamingEnumeration.hasMore()); j++)
      {
        arrayOfInt[j] = i;
        arrayOfAttribute[j] = ((Attribute)localNamingEnumeration.next());
      }
      LdapResult localLdapResult = clnt.modify(str, arrayOfInt, arrayOfAttribute, reqCtls);
      respCtls = resControls;
      if (status != 0)
      {
        processReturnCode(localLdapResult, paramName);
        return;
      }
    }
    catch (LdapReferralException localLdapReferralException1)
    {
      if (handleReferrals == 2) {
        throw paramContinuation.fillInException(localLdapReferralException1);
      }
      for (;;)
      {
        localObject2 = (LdapReferralContext)localLdapReferralException1.getReferralContext(envprops, bindCtls);
        try
        {
          ((LdapReferralContext)localObject2).modifyAttributes(paramName, paramInt, paramAttributes);
          return;
        }
        catch (LdapReferralException localLdapReferralException2)
        {
          Object localObject1 = localLdapReferralException2;
          ((LdapReferralContext)localObject2).close();
        }
        finally
        {
          ((LdapReferralContext)localObject2).close();
        }
      }
    }
    catch (IOException localIOException)
    {
      Object localObject2 = new CommunicationException(localIOException.getMessage());
      ((NamingException)localObject2).setRootCause(localIOException);
      throw paramContinuation.fillInException((NamingException)localObject2);
    }
    catch (NamingException localNamingException)
    {
      throw paramContinuation.fillInException(localNamingException);
    }
  }
  
  protected void c_modifyAttributes(Name paramName, ModificationItem[] paramArrayOfModificationItem, Continuation paramContinuation)
    throws NamingException
  {
    paramContinuation.setError(this, paramName);
    try
    {
      ensureOpen();
      if ((paramArrayOfModificationItem == null) || (paramArrayOfModificationItem.length == 0)) {
        return;
      }
      String str = fullyQualifiedName(paramName);
      localObject2 = new int[paramArrayOfModificationItem.length];
      Attribute[] arrayOfAttribute = new Attribute[paramArrayOfModificationItem.length];
      for (int i = 0; i < localObject2.length; i++)
      {
        ModificationItem localModificationItem = paramArrayOfModificationItem[i];
        localObject2[i] = convertToLdapModCode(localModificationItem.getModificationOp());
        arrayOfAttribute[i] = localModificationItem.getAttribute();
      }
      LdapResult localLdapResult = clnt.modify(str, (int[])localObject2, arrayOfAttribute, reqCtls);
      respCtls = resControls;
      if (status != 0) {
        processReturnCode(localLdapResult, paramName);
      }
    }
    catch (LdapReferralException localLdapReferralException1)
    {
      if (handleReferrals == 2) {
        throw paramContinuation.fillInException(localLdapReferralException1);
      }
      for (;;)
      {
        localObject2 = (LdapReferralContext)localLdapReferralException1.getReferralContext(envprops, bindCtls);
        try
        {
          ((LdapReferralContext)localObject2).modifyAttributes(paramName, paramArrayOfModificationItem);
          return;
        }
        catch (LdapReferralException localLdapReferralException2)
        {
          Object localObject1 = localLdapReferralException2;
          ((LdapReferralContext)localObject2).close();
        }
        finally
        {
          ((LdapReferralContext)localObject2).close();
        }
      }
    }
    catch (IOException localIOException)
    {
      Object localObject2 = new CommunicationException(localIOException.getMessage());
      ((NamingException)localObject2).setRootCause(localIOException);
      throw paramContinuation.fillInException((NamingException)localObject2);
    }
    catch (NamingException localNamingException)
    {
      throw paramContinuation.fillInException(localNamingException);
    }
  }
  
  private static int convertToLdapModCode(int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
      return 0;
    case 2: 
      return 2;
    case 3: 
      return 1;
    }
    throw new IllegalArgumentException("Invalid modification code");
  }
  
  protected DirContext c_getSchema(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    paramContinuation.setError(this, paramName);
    try
    {
      return getSchemaTree(paramName);
    }
    catch (NamingException localNamingException)
    {
      throw paramContinuation.fillInException(localNamingException);
    }
  }
  
  protected DirContext c_getSchemaClassDefinition(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    paramContinuation.setError(this, paramName);
    try
    {
      Attribute localAttribute = c_getAttributes(paramName, new String[] { "objectclass" }, paramContinuation).get("objectclass");
      if ((localAttribute == null) || (localAttribute.size() == 0)) {
        return EMPTY_SCHEMA;
      }
      Context localContext = (Context)c_getSchema(paramName, paramContinuation).lookup("ClassDefinition");
      HierMemDirCtx localHierMemDirCtx = new HierMemDirCtx();
      NamingEnumeration localNamingEnumeration = localAttribute.getAll();
      while (localNamingEnumeration.hasMoreElements())
      {
        String str = (String)localNamingEnumeration.nextElement();
        DirContext localDirContext = (DirContext)localContext.lookup(str);
        localHierMemDirCtx.bind(str, localDirContext);
      }
      localHierMemDirCtx.setReadOnly(new SchemaViolationException("Cannot update schema object"));
      return localHierMemDirCtx;
    }
    catch (NamingException localNamingException)
    {
      throw paramContinuation.fillInException(localNamingException);
    }
  }
  
  private DirContext getSchemaTree(Name paramName)
    throws NamingException
  {
    String str = getSchemaEntry(paramName, true);
    DirContext localDirContext = (DirContext)schemaTrees.get(str);
    if (localDirContext == null)
    {
      localDirContext = buildSchemaTree(str);
      schemaTrees.put(str, localDirContext);
    }
    return localDirContext;
  }
  
  private DirContext buildSchemaTree(String paramString)
    throws NamingException
  {
    SearchControls localSearchControls = new SearchControls(0, 0L, 0, SCHEMA_ATTRIBUTES, true, false);
    Name localName = new CompositeName().add(paramString);
    NamingEnumeration localNamingEnumeration = searchAux(localName, "(objectClass=subschema)", localSearchControls, false, true, new Continuation());
    if (!localNamingEnumeration.hasMore()) {
      throw new OperationNotSupportedException("Cannot get read subschemasubentry: " + paramString);
    }
    SearchResult localSearchResult = (SearchResult)localNamingEnumeration.next();
    localNamingEnumeration.close();
    Object localObject = localSearchResult.getObject();
    if (!(localObject instanceof LdapCtx)) {
      throw new NamingException("Cannot get schema object as DirContext: " + paramString);
    }
    return LdapSchemaCtx.createSchemaTree(envprops, paramString, (LdapCtx)localObject, localSearchResult.getAttributes(), netscapeSchemaBug);
  }
  
  private String getSchemaEntry(Name paramName, boolean paramBoolean)
    throws NamingException
  {
    SearchControls localSearchControls = new SearchControls(0, 0L, 0, new String[] { "subschemasubentry" }, false, false);
    NamingEnumeration localNamingEnumeration;
    try
    {
      localNamingEnumeration = searchAux(paramName, "objectclass=*", localSearchControls, paramBoolean, true, new Continuation());
    }
    catch (NamingException localNamingException)
    {
      if ((!clnt.isLdapv3) && (currentDN.length() == 0) && (paramName.isEmpty())) {
        throw new OperationNotSupportedException("Cannot get schema information from server");
      }
      throw localNamingException;
    }
    if (!localNamingEnumeration.hasMoreElements()) {
      throw new ConfigurationException("Requesting schema of nonexistent entry: " + paramName);
    }
    SearchResult localSearchResult = (SearchResult)localNamingEnumeration.next();
    localNamingEnumeration.close();
    Attribute localAttribute = localSearchResult.getAttributes().get("subschemasubentry");
    if ((localAttribute == null) || (localAttribute.size() < 0))
    {
      if ((currentDN.length() == 0) && (paramName.isEmpty())) {
        throw new OperationNotSupportedException("Cannot read subschemasubentry of root DSE");
      }
      return getSchemaEntry(new CompositeName(), false);
    }
    return (String)localAttribute.get();
  }
  
  void setParents(Attributes paramAttributes, Name paramName)
    throws NamingException
  {
    NamingEnumeration localNamingEnumeration = paramAttributes.getAll();
    while (localNamingEnumeration.hasMore()) {
      ((LdapAttribute)localNamingEnumeration.next()).setParent(this, paramName);
    }
  }
  
  String getURL()
  {
    if (url == null) {
      url = LdapURL.toUrlString(hostname, port_number, currentDN, hasLdapsScheme);
    }
    return url;
  }
  
  protected NamingEnumeration<SearchResult> c_search(Name paramName, Attributes paramAttributes, Continuation paramContinuation)
    throws NamingException
  {
    return c_search(paramName, paramAttributes, null, paramContinuation);
  }
  
  protected NamingEnumeration<SearchResult> c_search(Name paramName, Attributes paramAttributes, String[] paramArrayOfString, Continuation paramContinuation)
    throws NamingException
  {
    SearchControls localSearchControls = new SearchControls();
    localSearchControls.setReturningAttributes(paramArrayOfString);
    String str;
    try
    {
      str = SearchFilter.format(paramAttributes);
    }
    catch (NamingException localNamingException)
    {
      paramContinuation.setError(this, paramName);
      throw paramContinuation.fillInException(localNamingException);
    }
    return c_search(paramName, str, localSearchControls, paramContinuation);
  }
  
  protected NamingEnumeration<SearchResult> c_search(Name paramName, String paramString, SearchControls paramSearchControls, Continuation paramContinuation)
    throws NamingException
  {
    return searchAux(paramName, paramString, cloneSearchControls(paramSearchControls), true, waitForReply, paramContinuation);
  }
  
  protected NamingEnumeration<SearchResult> c_search(Name paramName, String paramString, Object[] paramArrayOfObject, SearchControls paramSearchControls, Continuation paramContinuation)
    throws NamingException
  {
    String str;
    try
    {
      str = SearchFilter.format(paramString, paramArrayOfObject);
    }
    catch (NamingException localNamingException)
    {
      paramContinuation.setError(this, paramName);
      throw paramContinuation.fillInException(localNamingException);
    }
    return c_search(paramName, str, paramSearchControls, paramContinuation);
  }
  
  NamingEnumeration<SearchResult> searchAux(Name paramName, String paramString, SearchControls paramSearchControls, boolean paramBoolean1, boolean paramBoolean2, Continuation paramContinuation)
    throws NamingException
  {
    LdapResult localLdapResult = null;
    String[] arrayOfString1 = new String[2];
    if (paramSearchControls == null) {
      paramSearchControls = new SearchControls();
    }
    String[] arrayOfString2 = paramSearchControls.getReturningAttributes();
    if ((paramSearchControls.getReturningObjFlag()) && (arrayOfString2 != null))
    {
      int i = 0;
      for (int j = arrayOfString2.length - 1; j >= 0; j--) {
        if (arrayOfString2[j].equals("*"))
        {
          i = 1;
          break;
        }
      }
      if (i == 0)
      {
        String[] arrayOfString3 = new String[arrayOfString2.length + Obj.JAVA_ATTRIBUTES.length];
        System.arraycopy(arrayOfString2, 0, arrayOfString3, 0, arrayOfString2.length);
        System.arraycopy(Obj.JAVA_ATTRIBUTES, 0, arrayOfString3, arrayOfString2.length, Obj.JAVA_ATTRIBUTES.length);
        paramSearchControls.setReturningAttributes(arrayOfString3);
      }
    }
    SearchArgs localSearchArgs = new SearchArgs(paramName, paramString, paramSearchControls, arrayOfString2);
    paramContinuation.setError(this, paramName);
    try
    {
      if (searchToCompare(paramString, paramSearchControls, arrayOfString1))
      {
        localLdapResult = compare(paramName, arrayOfString1[0], arrayOfString1[1]);
        if (!localLdapResult.compareToSearchResult(fullyQualifiedName(paramName))) {
          processReturnCode(localLdapResult, paramName);
        }
      }
      else
      {
        localLdapResult = doSearch(paramName, paramString, paramSearchControls, paramBoolean1, paramBoolean2);
        processReturnCode(localLdapResult, paramName);
      }
      return new LdapSearchEnumeration(this, localLdapResult, fullyQualifiedName(paramName), localSearchArgs, paramContinuation);
    }
    catch (LdapReferralException localLdapReferralException1)
    {
      if (handleReferrals == 2) {
        throw paramContinuation.fillInException(localLdapReferralException1);
      }
      for (;;)
      {
        localObject2 = (LdapReferralContext)localLdapReferralException1.getReferralContext(envprops, bindCtls);
        try
        {
          NamingEnumeration localNamingEnumeration = ((LdapReferralContext)localObject2).search(paramName, paramString, paramSearchControls);
          return localNamingEnumeration;
        }
        catch (LdapReferralException localLdapReferralException2)
        {
          Object localObject1 = localLdapReferralException2;
          ((LdapReferralContext)localObject2).close();
        }
        finally
        {
          ((LdapReferralContext)localObject2).close();
        }
      }
    }
    catch (LimitExceededException localLimitExceededException)
    {
      localObject2 = new LdapSearchEnumeration(this, localLdapResult, fullyQualifiedName(paramName), localSearchArgs, paramContinuation);
      ((LdapSearchEnumeration)localObject2).setNamingException(localLimitExceededException);
      return (NamingEnumeration<SearchResult>)localObject2;
    }
    catch (PartialResultException localPartialResultException)
    {
      localObject2 = new LdapSearchEnumeration(this, localLdapResult, fullyQualifiedName(paramName), localSearchArgs, paramContinuation);
      ((LdapSearchEnumeration)localObject2).setNamingException(localPartialResultException);
      return (NamingEnumeration<SearchResult>)localObject2;
    }
    catch (IOException localIOException)
    {
      Object localObject2 = new CommunicationException(localIOException.getMessage());
      ((NamingException)localObject2).setRootCause(localIOException);
      throw paramContinuation.fillInException((NamingException)localObject2);
    }
    catch (NamingException localNamingException)
    {
      throw paramContinuation.fillInException(localNamingException);
    }
  }
  
  LdapResult getSearchReply(LdapClient paramLdapClient, LdapResult paramLdapResult)
    throws NamingException
  {
    if (clnt != paramLdapClient) {
      throw new CommunicationException("Context's connection changed; unable to continue enumeration");
    }
    try
    {
      return paramLdapClient.getSearchReply(batchSize, paramLdapResult, binaryAttrs);
    }
    catch (IOException localIOException)
    {
      CommunicationException localCommunicationException = new CommunicationException(localIOException.getMessage());
      localCommunicationException.setRootCause(localIOException);
      throw localCommunicationException;
    }
  }
  
  private LdapResult doSearchOnce(Name paramName, String paramString, SearchControls paramSearchControls, boolean paramBoolean)
    throws NamingException
  {
    int i = batchSize;
    batchSize = 2;
    LdapResult localLdapResult = doSearch(paramName, paramString, paramSearchControls, paramBoolean, true);
    batchSize = i;
    return localLdapResult;
  }
  
  private LdapResult doSearch(Name paramName, String paramString, SearchControls paramSearchControls, boolean paramBoolean1, boolean paramBoolean2)
    throws NamingException
  {
    ensureOpen();
    try
    {
      int i;
      switch (paramSearchControls.getSearchScope())
      {
      case 0: 
        i = 0;
        break;
      case 1: 
      default: 
        i = 1;
        break;
      case 2: 
        i = 2;
      }
      localObject = paramSearchControls.getReturningAttributes();
      if ((localObject != null) && (localObject.length == 0))
      {
        localObject = new String[1];
        localObject[0] = "1.1";
      }
      String str = paramName.isEmpty() ? "" : paramBoolean1 ? fullyQualifiedName(paramName) : paramName.get(0);
      int j = paramSearchControls.getTimeLimit();
      int k = 0;
      if (j > 0) {
        k = j / 1000 + 1;
      }
      LdapResult localLdapResult = clnt.search(str, i, derefAliases, (int)paramSearchControls.getCountLimit(), k, paramSearchControls.getReturningObjFlag() ? false : typesOnly, (String[])localObject, paramString, batchSize, reqCtls, binaryAttrs, paramBoolean2, replyQueueSize);
      respCtls = resControls;
      return localLdapResult;
    }
    catch (IOException localIOException)
    {
      Object localObject = new CommunicationException(localIOException.getMessage());
      ((NamingException)localObject).setRootCause(localIOException);
      throw ((Throwable)localObject);
    }
  }
  
  private static boolean searchToCompare(String paramString, SearchControls paramSearchControls, String[] paramArrayOfString)
  {
    if (paramSearchControls.getSearchScope() != 0) {
      return false;
    }
    String[] arrayOfString = paramSearchControls.getReturningAttributes();
    if ((arrayOfString == null) || (arrayOfString.length != 0)) {
      return false;
    }
    return filterToAssertion(paramString, paramArrayOfString);
  }
  
  private static boolean filterToAssertion(String paramString, String[] paramArrayOfString)
  {
    StringTokenizer localStringTokenizer1 = new StringTokenizer(paramString, "=");
    if (localStringTokenizer1.countTokens() != 2) {
      return false;
    }
    paramArrayOfString[0] = localStringTokenizer1.nextToken();
    paramArrayOfString[1] = localStringTokenizer1.nextToken();
    if (paramArrayOfString[1].indexOf('*') != -1) {
      return false;
    }
    int i = 0;
    int j = paramArrayOfString[1].length();
    if ((paramArrayOfString[0].charAt(0) == '(') && (paramArrayOfString[1].charAt(j - 1) == ')')) {
      i = 1;
    } else if ((paramArrayOfString[0].charAt(0) == '(') || (paramArrayOfString[1].charAt(j - 1) == ')')) {
      return false;
    }
    StringTokenizer localStringTokenizer2 = new StringTokenizer(paramArrayOfString[0], "()&|!=~><*", true);
    if (localStringTokenizer2.countTokens() != (i != 0 ? 2 : 1)) {
      return false;
    }
    localStringTokenizer2 = new StringTokenizer(paramArrayOfString[1], "()&|!=~><*", true);
    if (localStringTokenizer2.countTokens() != (i != 0 ? 2 : 1)) {
      return false;
    }
    if (i != 0)
    {
      paramArrayOfString[0] = paramArrayOfString[0].substring(1);
      paramArrayOfString[1] = paramArrayOfString[1].substring(0, j - 1);
    }
    return true;
  }
  
  private LdapResult compare(Name paramName, String paramString1, String paramString2)
    throws IOException, NamingException
  {
    ensureOpen();
    String str = fullyQualifiedName(paramName);
    LdapResult localLdapResult = clnt.compare(str, paramString1, paramString2, reqCtls);
    respCtls = resControls;
    return localLdapResult;
  }
  
  private static SearchControls cloneSearchControls(SearchControls paramSearchControls)
  {
    if (paramSearchControls == null) {
      return null;
    }
    Object localObject = paramSearchControls.getReturningAttributes();
    if (localObject != null)
    {
      String[] arrayOfString = new String[localObject.length];
      System.arraycopy(localObject, 0, arrayOfString, 0, localObject.length);
      localObject = arrayOfString;
    }
    return new SearchControls(paramSearchControls.getSearchScope(), paramSearchControls.getCountLimit(), paramSearchControls.getTimeLimit(), (String[])localObject, paramSearchControls.getReturningObjFlag(), paramSearchControls.getDerefLinkFlag());
  }
  
  protected Hashtable<String, Object> p_getEnvironment()
  {
    return envprops;
  }
  
  public Hashtable<String, Object> getEnvironment()
    throws NamingException
  {
    return envprops == null ? new Hashtable(5, 0.75F) : (Hashtable)envprops.clone();
  }
  
  public Object removeFromEnvironment(String paramString)
    throws NamingException
  {
    if ((envprops == null) || (envprops.get(paramString) == null)) {
      return null;
    }
    switch (paramString)
    {
    case "java.naming.ldap.ref.separator": 
      addrEncodingSeparator = '#';
      break;
    case "java.naming.ldap.typesOnly": 
      typesOnly = false;
      break;
    case "java.naming.ldap.deleteRDN": 
      deleteRDN = true;
      break;
    case "java.naming.ldap.derefAliases": 
      derefAliases = 3;
      break;
    case "java.naming.batchsize": 
      batchSize = 1;
      break;
    case "java.naming.ldap.referral.limit": 
      referralHopLimit = 10;
      break;
    case "java.naming.referral": 
      setReferralMode(null, true);
      break;
    case "java.naming.ldap.attributes.binary": 
      setBinaryAttributes(null);
      break;
    case "com.sun.jndi.ldap.connect.timeout": 
      connectTimeout = -1;
      break;
    case "com.sun.jndi.ldap.read.timeout": 
      readTimeout = -1;
      break;
    case "com.sun.jndi.ldap.search.waitForReply": 
      waitForReply = true;
      break;
    case "com.sun.jndi.ldap.search.replyQueueSize": 
      replyQueueSize = -1;
      break;
    case "java.naming.security.protocol": 
      closeConnection(false);
      if ((useSsl) && (!hasLdapsScheme))
      {
        useSsl = false;
        url = null;
        if (useDefaultPortNumber) {
          port_number = 389;
        }
      }
      break;
    case "java.naming.ldap.factory.socket": 
    case "java.naming.ldap.version": 
      closeConnection(false);
      break;
    case "java.naming.security.authentication": 
    case "java.naming.security.credentials": 
    case "java.naming.security.principal": 
      sharable = false;
    }
    envprops = ((Hashtable)envprops.clone());
    return envprops.remove(paramString);
  }
  
  public Object addToEnvironment(String paramString, Object paramObject)
    throws NamingException
  {
    if (paramObject == null) {
      return removeFromEnvironment(paramString);
    }
    switch (paramString)
    {
    case "java.naming.ldap.ref.separator": 
      setRefSeparator((String)paramObject);
      break;
    case "java.naming.ldap.typesOnly": 
      setTypesOnly((String)paramObject);
      break;
    case "java.naming.ldap.deleteRDN": 
      setDeleteRDN((String)paramObject);
      break;
    case "java.naming.ldap.derefAliases": 
      setDerefAliases((String)paramObject);
      break;
    case "java.naming.batchsize": 
      setBatchSize((String)paramObject);
      break;
    case "java.naming.ldap.referral.limit": 
      setReferralLimit((String)paramObject);
      break;
    case "java.naming.referral": 
      setReferralMode((String)paramObject, true);
      break;
    case "java.naming.ldap.attributes.binary": 
      setBinaryAttributes((String)paramObject);
      break;
    case "com.sun.jndi.ldap.connect.timeout": 
      setConnectTimeout((String)paramObject);
      break;
    case "com.sun.jndi.ldap.read.timeout": 
      setReadTimeout((String)paramObject);
      break;
    case "com.sun.jndi.ldap.search.waitForReply": 
      setWaitForReply((String)paramObject);
      break;
    case "com.sun.jndi.ldap.search.replyQueueSize": 
      setReplyQueueSize((String)paramObject);
      break;
    case "java.naming.security.protocol": 
      closeConnection(false);
      if ("ssl".equals(paramObject))
      {
        useSsl = true;
        url = null;
        if (useDefaultPortNumber) {
          port_number = 636;
        }
      }
      break;
    case "java.naming.ldap.factory.socket": 
    case "java.naming.ldap.version": 
      closeConnection(false);
      break;
    case "java.naming.security.authentication": 
    case "java.naming.security.credentials": 
    case "java.naming.security.principal": 
      sharable = false;
    }
    envprops = (envprops == null ? new Hashtable(5, 0.75F) : (Hashtable)envprops.clone());
    return envprops.put(paramString, paramObject);
  }
  
  void setProviderUrl(String paramString)
  {
    if (envprops != null) {
      envprops.put("java.naming.provider.url", paramString);
    }
  }
  
  void setDomainName(String paramString)
  {
    if (envprops != null) {
      envprops.put("com.sun.jndi.ldap.domainname", paramString);
    }
  }
  
  private void initEnv()
    throws NamingException
  {
    if (envprops == null)
    {
      setReferralMode(null, false);
      return;
    }
    setBatchSize((String)envprops.get("java.naming.batchsize"));
    setRefSeparator((String)envprops.get("java.naming.ldap.ref.separator"));
    setDeleteRDN((String)envprops.get("java.naming.ldap.deleteRDN"));
    setTypesOnly((String)envprops.get("java.naming.ldap.typesOnly"));
    setDerefAliases((String)envprops.get("java.naming.ldap.derefAliases"));
    setReferralLimit((String)envprops.get("java.naming.ldap.referral.limit"));
    setBinaryAttributes((String)envprops.get("java.naming.ldap.attributes.binary"));
    bindCtls = cloneControls((Control[])envprops.get("java.naming.ldap.control.connect"));
    setReferralMode((String)envprops.get("java.naming.referral"), false);
    setConnectTimeout((String)envprops.get("com.sun.jndi.ldap.connect.timeout"));
    setReadTimeout((String)envprops.get("com.sun.jndi.ldap.read.timeout"));
    setWaitForReply((String)envprops.get("com.sun.jndi.ldap.search.waitForReply"));
    setReplyQueueSize((String)envprops.get("com.sun.jndi.ldap.search.replyQueueSize"));
  }
  
  private void setDeleteRDN(String paramString)
  {
    if ((paramString != null) && (paramString.equalsIgnoreCase("false"))) {
      deleteRDN = false;
    } else {
      deleteRDN = true;
    }
  }
  
  private void setTypesOnly(String paramString)
  {
    if ((paramString != null) && (paramString.equalsIgnoreCase("true"))) {
      typesOnly = true;
    } else {
      typesOnly = false;
    }
  }
  
  private void setBatchSize(String paramString)
  {
    if (paramString != null) {
      batchSize = Integer.parseInt(paramString);
    } else {
      batchSize = 1;
    }
  }
  
  private void setReferralMode(String paramString, boolean paramBoolean)
  {
    if (paramString != null) {
      switch (paramString)
      {
      case "follow-scheme": 
        handleReferrals = 4;
        break;
      case "follow": 
        handleReferrals = 1;
        break;
      case "throw": 
        handleReferrals = 2;
        break;
      case "ignore": 
        handleReferrals = 3;
        break;
      default: 
        throw new IllegalArgumentException("Illegal value for java.naming.referral property.");
      }
    } else {
      handleReferrals = 3;
    }
    if (handleReferrals == 3) {
      reqCtls = addControl(reqCtls, manageReferralControl);
    } else if (paramBoolean) {
      reqCtls = removeControl(reqCtls, manageReferralControl);
    }
  }
  
  private void setDerefAliases(String paramString)
  {
    if (paramString != null) {
      switch (paramString)
      {
      case "never": 
        derefAliases = 0;
        break;
      case "searching": 
        derefAliases = 1;
        break;
      case "finding": 
        derefAliases = 2;
        break;
      case "always": 
        derefAliases = 3;
        break;
      default: 
        throw new IllegalArgumentException("Illegal value for java.naming.ldap.derefAliases property.");
      }
    } else {
      derefAliases = 3;
    }
  }
  
  private void setRefSeparator(String paramString)
    throws NamingException
  {
    if ((paramString != null) && (paramString.length() > 0)) {
      addrEncodingSeparator = paramString.charAt(0);
    } else {
      addrEncodingSeparator = '#';
    }
  }
  
  private void setReferralLimit(String paramString)
  {
    if (paramString != null)
    {
      referralHopLimit = Integer.parseInt(paramString);
      if (referralHopLimit == 0) {
        referralHopLimit = Integer.MAX_VALUE;
      }
    }
    else
    {
      referralHopLimit = 10;
    }
  }
  
  void setHopCount(int paramInt)
  {
    hopCount = paramInt;
  }
  
  private void setConnectTimeout(String paramString)
  {
    if (paramString != null) {
      connectTimeout = Integer.parseInt(paramString);
    } else {
      connectTimeout = -1;
    }
  }
  
  private void setReplyQueueSize(String paramString)
  {
    if (paramString != null)
    {
      replyQueueSize = Integer.parseInt(paramString);
      if (replyQueueSize <= 0) {
        replyQueueSize = -1;
      }
    }
    else
    {
      replyQueueSize = -1;
    }
  }
  
  private void setWaitForReply(String paramString)
  {
    if ((paramString != null) && (paramString.equalsIgnoreCase("false"))) {
      waitForReply = false;
    } else {
      waitForReply = true;
    }
  }
  
  private void setReadTimeout(String paramString)
  {
    if (paramString != null) {
      readTimeout = Integer.parseInt(paramString);
    } else {
      readTimeout = -1;
    }
  }
  
  private static Vector<Vector<String>> extractURLs(String paramString)
  {
    int i = 0;
    for (int j = 0; (i = paramString.indexOf('\n', i)) >= 0; j++) {
      i++;
    }
    Vector localVector1 = new Vector(j);
    int m = 0;
    i = paramString.indexOf('\n');
    for (int k = i + 1; (i = paramString.indexOf('\n', k)) >= 0; k = i + 1)
    {
      localVector2 = new Vector(1);
      localVector2.addElement(paramString.substring(k, i));
      localVector1.addElement(localVector2);
    }
    Vector localVector2 = new Vector(1);
    localVector2.addElement(paramString.substring(k));
    localVector1.addElement(localVector2);
    return localVector1;
  }
  
  private void setBinaryAttributes(String paramString)
  {
    if (paramString == null)
    {
      binaryAttrs = null;
    }
    else
    {
      binaryAttrs = new Hashtable(11, 0.75F);
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString.toLowerCase(Locale.ENGLISH), " ");
      while (localStringTokenizer.hasMoreTokens()) {
        binaryAttrs.put(localStringTokenizer.nextToken(), Boolean.TRUE);
      }
    }
  }
  
  protected void finalize()
  {
    try
    {
      close();
    }
    catch (NamingException localNamingException) {}
  }
  
  public synchronized void close()
    throws NamingException
  {
    if (eventSupport != null)
    {
      eventSupport.cleanup();
      removeUnsolicited();
    }
    if (enumCount > 0)
    {
      closeRequested = true;
      return;
    }
    closeConnection(false);
  }
  
  public void reconnect(Control[] paramArrayOfControl)
    throws NamingException
  {
    envprops = (envprops == null ? new Hashtable(5, 0.75F) : (Hashtable)envprops.clone());
    if (paramArrayOfControl == null)
    {
      envprops.remove("java.naming.ldap.control.connect");
      bindCtls = null;
    }
    else
    {
      envprops.put("java.naming.ldap.control.connect", bindCtls = cloneControls(paramArrayOfControl));
    }
    sharable = false;
    ensureOpen();
  }
  
  private void ensureOpen()
    throws NamingException
  {
    ensureOpen(false);
  }
  
  private void ensureOpen(boolean paramBoolean)
    throws NamingException
  {
    try
    {
      if (clnt == null)
      {
        schemaTrees = new Hashtable(11, 0.75F);
        connect(paramBoolean);
      }
      else if ((!sharable) || (paramBoolean))
      {
        synchronized (clnt)
        {
          if ((!clnt.isLdapv3) || (clnt.referenceCount > 1) || (clnt.usingSaslStreams())) {
            closeConnection(false);
          }
        }
        schemaTrees = new Hashtable(11, 0.75F);
        connect(paramBoolean);
      }
    }
    finally
    {
      sharable = true;
    }
  }
  
  private void connect(boolean paramBoolean)
    throws NamingException
  {
    String str1 = null;
    Object localObject1 = null;
    String str2 = null;
    String str3 = null;
    String str4 = null;
    String str5 = null;
    boolean bool1 = false;
    if (envprops != null)
    {
      str1 = (String)envprops.get("java.naming.security.principal");
      localObject1 = envprops.get("java.naming.security.credentials");
      str5 = (String)envprops.get("java.naming.ldap.version");
      str2 = useSsl ? "ssl" : (String)envprops.get("java.naming.security.protocol");
      str3 = (String)envprops.get("java.naming.ldap.factory.socket");
      str4 = (String)envprops.get("java.naming.security.authentication");
      bool1 = "true".equalsIgnoreCase((String)envprops.get("com.sun.jndi.ldap.connect.pool"));
    }
    if (str3 == null) {
      str3 = "ssl".equals(str2) ? "javax.net.ssl.SSLSocketFactory" : null;
    }
    if (str4 == null) {
      str4 = str1 == null ? "none" : "simple";
    }
    try
    {
      boolean bool2 = clnt == null;
      int i;
      if (bool2)
      {
        i = str5 != null ? Integer.parseInt(str5) : 32;
        clnt = LdapClient.getInstance(bool1, hostname, port_number, str3, connectTimeout, readTimeout, trace, i, str4, bindCtls, str2, str1, localObject1, envprops);
        if (!clnt.authenticateCalled()) {}
      }
      else
      {
        if ((sharable) && (paramBoolean)) {
          return;
        }
        i = 3;
      }
      localObject2 = clnt.authenticate(bool2, str1, localObject1, i, str4, bindCtls, envprops);
      respCtls = resControls;
      if (status != 0)
      {
        if (bool2) {
          closeConnection(true);
        }
        processReturnCode((LdapResult)localObject2);
      }
    }
    catch (LdapReferralException localLdapReferralException)
    {
      Object localObject2;
      if (handleReferrals == 2) {
        throw localLdapReferralException;
      }
      Object localObject3 = null;
      for (;;)
      {
        if ((localObject2 = localLdapReferralException.getNextReferral()) == null)
        {
          if (localObject3 != null) {
            throw ((NamingException)((NamingException)localObject3).fillInStackTrace());
          }
          throw new NamingException("Internal error processing referral during connection");
        }
        LdapURL localLdapURL = new LdapURL((String)localObject2);
        hostname = localLdapURL.getHost();
        if ((hostname != null) && (hostname.charAt(0) == '[')) {
          hostname = hostname.substring(1, hostname.length() - 1);
        }
        port_number = localLdapURL.getPort();
        try
        {
          connect(paramBoolean);
        }
        catch (NamingException localNamingException)
        {
          localObject3 = localNamingException;
        }
      }
    }
  }
  
  private void closeConnection(boolean paramBoolean)
  {
    removeUnsolicited();
    if (clnt != null)
    {
      clnt.close(reqCtls, paramBoolean);
      clnt = null;
    }
  }
  
  synchronized void incEnumCount()
  {
    enumCount += 1;
  }
  
  synchronized void decEnumCount()
  {
    enumCount -= 1;
    if ((enumCount == 0) && (closeRequested)) {
      try
      {
        close();
      }
      catch (NamingException localNamingException) {}
    }
  }
  
  protected void processReturnCode(LdapResult paramLdapResult)
    throws NamingException
  {
    processReturnCode(paramLdapResult, null, this, null, envprops, null);
  }
  
  void processReturnCode(LdapResult paramLdapResult, Name paramName)
    throws NamingException
  {
    processReturnCode(paramLdapResult, new CompositeName().add(currentDN), this, paramName, envprops, fullyQualifiedName(paramName));
  }
  
  protected void processReturnCode(LdapResult paramLdapResult, Name paramName1, Object paramObject, Name paramName2, Hashtable<?, ?> paramHashtable, String paramString)
    throws NamingException
  {
    String str = LdapClient.getErrorMessage(status, errorMessage);
    LdapReferralException localLdapReferralException = null;
    Object localObject1;
    Object localObject2;
    Object localObject3;
    switch (status)
    {
    case 0: 
      if (referrals != null)
      {
        str = "Unprocessed Continuation Reference(s)";
        if (handleReferrals == 3)
        {
          localObject1 = new PartialResultException(str);
          break;
        }
        int i = referrals.size();
        localObject2 = null;
        localObject3 = null;
        str = "Continuation Reference";
        for (int j = 0; j < i; j++)
        {
          localLdapReferralException = new LdapReferralException(paramName1, paramObject, paramName2, str, paramHashtable, paramString, handleReferrals, reqCtls);
          localLdapReferralException.setReferralInfo((Vector)referrals.elementAt(j), true);
          if (hopCount > 1) {
            localLdapReferralException.setHopCount(hopCount);
          }
          if (localObject2 == null)
          {
            localObject2 = localObject3 = localLdapReferralException;
          }
          else
          {
            nextReferralEx = localLdapReferralException;
            localObject3 = localLdapReferralException;
          }
        }
        referrals = null;
        if (refEx == null)
        {
          refEx = ((LdapReferralException)localObject2);
        }
        else
        {
          for (localObject3 = refEx; nextReferralEx != null; localObject3 = nextReferralEx) {}
          nextReferralEx = ((LdapReferralException)localObject2);
        }
        if (hopCount > referralHopLimit)
        {
          LimitExceededException localLimitExceededException = new LimitExceededException("Referral limit exceeded");
          localLimitExceededException.setRootCause(localLdapReferralException);
          throw localLimitExceededException;
        }
      }
      return;
    case 10: 
      if (handleReferrals == 3)
      {
        localObject1 = new PartialResultException(str);
      }
      else
      {
        localLdapReferralException = new LdapReferralException(paramName1, paramObject, paramName2, str, paramHashtable, paramString, handleReferrals, reqCtls);
        Vector localVector;
        if (referrals == null)
        {
          localVector = null;
        }
        else if (handleReferrals == 4)
        {
          localVector = new Vector();
          localObject2 = ((Vector)referrals.elementAt(0)).iterator();
          while (((Iterator)localObject2).hasNext())
          {
            localObject3 = (String)((Iterator)localObject2).next();
            if (((String)localObject3).startsWith("ldap:")) {
              localVector.add(localObject3);
            }
          }
          if (localVector.isEmpty()) {
            localVector = null;
          }
        }
        else
        {
          localVector = (Vector)referrals.elementAt(0);
        }
        localLdapReferralException.setReferralInfo(localVector, false);
        if (hopCount > 1) {
          localLdapReferralException.setHopCount(hopCount);
        }
        if (hopCount > referralHopLimit)
        {
          localObject2 = new LimitExceededException("Referral limit exceeded");
          ((NamingException)localObject2).setRootCause(localLdapReferralException);
          localObject1 = localObject2;
        }
        else
        {
          localObject1 = localLdapReferralException;
        }
      }
      break;
    case 9: 
      if (handleReferrals == 3)
      {
        localObject1 = new PartialResultException(str);
      }
      else
      {
        if ((errorMessage != null) && (!errorMessage.equals("")))
        {
          referrals = extractURLs(errorMessage);
        }
        else
        {
          localObject1 = new PartialResultException(str);
          break;
        }
        localLdapReferralException = new LdapReferralException(paramName1, paramObject, paramName2, str, paramHashtable, paramString, handleReferrals, reqCtls);
        if (hopCount > 1) {
          localLdapReferralException.setHopCount(hopCount);
        }
        if (((entries == null) || (entries.isEmpty())) && (referrals != null) && (referrals.size() == 1))
        {
          localLdapReferralException.setReferralInfo(referrals, false);
          if (hopCount > referralHopLimit)
          {
            localObject2 = new LimitExceededException("Referral limit exceeded");
            ((NamingException)localObject2).setRootCause(localLdapReferralException);
            localObject1 = localObject2;
          }
          else
          {
            localObject1 = localLdapReferralException;
          }
        }
        else
        {
          localLdapReferralException.setReferralInfo(referrals, true);
          refEx = localLdapReferralException;
          return;
        }
      }
      break;
    case 34: 
    case 64: 
      if (paramName2 != null) {
        localObject1 = new InvalidNameException(paramName2.toString() + ": " + str);
      } else {
        localObject1 = new InvalidNameException(str);
      }
      break;
    default: 
      localObject1 = mapErrorCode(status, errorMessage);
    }
    ((NamingException)localObject1).setResolvedName(paramName1);
    ((NamingException)localObject1).setResolvedObj(paramObject);
    ((NamingException)localObject1).setRemainingName(paramName2);
    throw ((Throwable)localObject1);
  }
  
  public static NamingException mapErrorCode(int paramInt, String paramString)
  {
    if (paramInt == 0) {
      return null;
    }
    Object localObject = null;
    String str = LdapClient.getErrorMessage(paramInt, paramString);
    switch (paramInt)
    {
    case 36: 
      localObject = new NamingException(str);
      break;
    case 33: 
      localObject = new NamingException(str);
      break;
    case 20: 
      localObject = new AttributeInUseException(str);
      break;
    case 7: 
    case 8: 
    case 13: 
    case 48: 
      localObject = new AuthenticationNotSupportedException(str);
      break;
    case 68: 
      localObject = new NameAlreadyBoundException(str);
      break;
    case 14: 
    case 49: 
      localObject = new AuthenticationException(str);
      break;
    case 18: 
      localObject = new InvalidSearchFilterException(str);
      break;
    case 50: 
      localObject = new NoPermissionException(str);
      break;
    case 19: 
    case 21: 
      localObject = new InvalidAttributeValueException(str);
      break;
    case 54: 
      localObject = new NamingException(str);
      break;
    case 16: 
      localObject = new NoSuchAttributeException(str);
      break;
    case 32: 
      localObject = new NameNotFoundException(str);
      break;
    case 65: 
    case 67: 
    case 69: 
      localObject = new SchemaViolationException(str);
      break;
    case 66: 
      localObject = new ContextNotEmptyException(str);
      break;
    case 1: 
      localObject = new NamingException(str);
      break;
    case 80: 
      localObject = new NamingException(str);
      break;
    case 2: 
      localObject = new CommunicationException(str);
      break;
    case 4: 
      localObject = new SizeLimitExceededException(str);
      break;
    case 3: 
      localObject = new TimeLimitExceededException(str);
      break;
    case 12: 
      localObject = new OperationNotSupportedException(str);
      break;
    case 51: 
    case 52: 
      localObject = new ServiceUnavailableException(str);
      break;
    case 17: 
      localObject = new InvalidAttributeIdentifierException(str);
      break;
    case 53: 
      localObject = new OperationNotSupportedException(str);
      break;
    case 5: 
    case 6: 
    case 35: 
      localObject = new NamingException(str);
      break;
    case 11: 
      localObject = new LimitExceededException(str);
      break;
    case 10: 
      localObject = new NamingException(str);
      break;
    case 9: 
      localObject = new NamingException(str);
      break;
    case 34: 
    case 64: 
      localObject = new InvalidNameException(str);
      break;
    case 15: 
    case 22: 
    case 23: 
    case 24: 
    case 25: 
    case 26: 
    case 27: 
    case 28: 
    case 29: 
    case 30: 
    case 31: 
    case 37: 
    case 38: 
    case 39: 
    case 40: 
    case 41: 
    case 42: 
    case 43: 
    case 44: 
    case 45: 
    case 46: 
    case 47: 
    case 55: 
    case 56: 
    case 57: 
    case 58: 
    case 59: 
    case 60: 
    case 61: 
    case 62: 
    case 63: 
    case 70: 
    case 71: 
    case 72: 
    case 73: 
    case 74: 
    case 75: 
    case 76: 
    case 77: 
    case 78: 
    case 79: 
    default: 
      localObject = new NamingException(str);
    }
    return (NamingException)localObject;
  }
  
  public ExtendedResponse extendedOperation(ExtendedRequest paramExtendedRequest)
    throws NamingException
  {
    boolean bool = paramExtendedRequest.getID().equals("1.3.6.1.4.1.1466.20037");
    ensureOpen(bool);
    try
    {
      LdapResult localLdapResult = clnt.extendedOp(paramExtendedRequest.getID(), paramExtendedRequest.getEncodedValue(), reqCtls, bool);
      respCtls = resControls;
      if (status != 0) {
        processReturnCode(localLdapResult, new CompositeName());
      }
      int i = extensionValue == null ? 0 : extensionValue.length;
      localExtendedResponse = paramExtendedRequest.createExtendedResponse(extensionId, extensionValue, 0, i);
      if ((localExtendedResponse instanceof StartTlsResponseImpl))
      {
        String str = (String)(envprops != null ? envprops.get("com.sun.jndi.ldap.domainname") : null);
        ((StartTlsResponseImpl)localExtendedResponse).setConnection(clnt.conn, str);
      }
      return localExtendedResponse;
    }
    catch (LdapReferralException localLdapReferralException1)
    {
      ExtendedResponse localExtendedResponse;
      if (handleReferrals == 2) {
        throw localLdapReferralException1;
      }
      for (;;)
      {
        localObject2 = (LdapReferralContext)localLdapReferralException1.getReferralContext(envprops, bindCtls);
        try
        {
          localExtendedResponse = ((LdapReferralContext)localObject2).extendedOperation(paramExtendedRequest);
          return localExtendedResponse;
        }
        catch (LdapReferralException localLdapReferralException2)
        {
          Object localObject1 = localLdapReferralException2;
          ((LdapReferralContext)localObject2).close();
        }
        finally
        {
          ((LdapReferralContext)localObject2).close();
        }
      }
    }
    catch (IOException localIOException)
    {
      Object localObject2 = new CommunicationException(localIOException.getMessage());
      ((NamingException)localObject2).setRootCause(localIOException);
      throw ((Throwable)localObject2);
    }
  }
  
  public void setRequestControls(Control[] paramArrayOfControl)
    throws NamingException
  {
    if (handleReferrals == 3) {
      reqCtls = addControl(paramArrayOfControl, manageReferralControl);
    } else {
      reqCtls = cloneControls(paramArrayOfControl);
    }
  }
  
  public Control[] getRequestControls()
    throws NamingException
  {
    return cloneControls(reqCtls);
  }
  
  public Control[] getConnectControls()
    throws NamingException
  {
    return cloneControls(bindCtls);
  }
  
  public Control[] getResponseControls()
    throws NamingException
  {
    return respCtls != null ? convertControls(respCtls) : null;
  }
  
  Control[] convertControls(Vector<Control> paramVector)
    throws NamingException
  {
    int i = paramVector.size();
    if (i == 0) {
      return null;
    }
    Control[] arrayOfControl = new Control[i];
    for (int j = 0; j < i; j++)
    {
      arrayOfControl[j] = myResponseControlFactory.getControlInstance((Control)paramVector.elementAt(j));
      if (arrayOfControl[j] == null) {
        arrayOfControl[j] = ControlFactory.getControlInstance((Control)paramVector.elementAt(j), this, envprops);
      }
    }
    return arrayOfControl;
  }
  
  private static Control[] addControl(Control[] paramArrayOfControl, Control paramControl)
  {
    if (paramArrayOfControl == null) {
      return new Control[] { paramControl };
    }
    int i = findControl(paramArrayOfControl, paramControl);
    if (i != -1) {
      return paramArrayOfControl;
    }
    Control[] arrayOfControl = new Control[paramArrayOfControl.length + 1];
    System.arraycopy(paramArrayOfControl, 0, arrayOfControl, 0, paramArrayOfControl.length);
    arrayOfControl[paramArrayOfControl.length] = paramControl;
    return arrayOfControl;
  }
  
  private static int findControl(Control[] paramArrayOfControl, Control paramControl)
  {
    for (int i = 0; i < paramArrayOfControl.length; i++) {
      if (paramArrayOfControl[i] == paramControl) {
        return i;
      }
    }
    return -1;
  }
  
  private static Control[] removeControl(Control[] paramArrayOfControl, Control paramControl)
  {
    if (paramArrayOfControl == null) {
      return null;
    }
    int i = findControl(paramArrayOfControl, paramControl);
    if (i == -1) {
      return paramArrayOfControl;
    }
    Control[] arrayOfControl = new Control[paramArrayOfControl.length - 1];
    System.arraycopy(paramArrayOfControl, 0, arrayOfControl, 0, i);
    System.arraycopy(paramArrayOfControl, i + 1, arrayOfControl, i, paramArrayOfControl.length - i - 1);
    return arrayOfControl;
  }
  
  private static Control[] cloneControls(Control[] paramArrayOfControl)
  {
    if (paramArrayOfControl == null) {
      return null;
    }
    Control[] arrayOfControl = new Control[paramArrayOfControl.length];
    System.arraycopy(paramArrayOfControl, 0, arrayOfControl, 0, paramArrayOfControl.length);
    return arrayOfControl;
  }
  
  public void addNamingListener(Name paramName, int paramInt, NamingListener paramNamingListener)
    throws NamingException
  {
    addNamingListener(getTargetName(paramName), paramInt, paramNamingListener);
  }
  
  public void addNamingListener(String paramString, int paramInt, NamingListener paramNamingListener)
    throws NamingException
  {
    if (eventSupport == null) {
      eventSupport = new EventSupport(this);
    }
    eventSupport.addNamingListener(getTargetName(new CompositeName(paramString)), paramInt, paramNamingListener);
    if (((paramNamingListener instanceof UnsolicitedNotificationListener)) && (!unsolicited)) {
      addUnsolicited();
    }
  }
  
  public void removeNamingListener(NamingListener paramNamingListener)
    throws NamingException
  {
    if (eventSupport == null) {
      return;
    }
    eventSupport.removeNamingListener(paramNamingListener);
    if (((paramNamingListener instanceof UnsolicitedNotificationListener)) && (!eventSupport.hasUnsolicited())) {
      removeUnsolicited();
    }
  }
  
  public void addNamingListener(String paramString1, String paramString2, SearchControls paramSearchControls, NamingListener paramNamingListener)
    throws NamingException
  {
    if (eventSupport == null) {
      eventSupport = new EventSupport(this);
    }
    eventSupport.addNamingListener(getTargetName(new CompositeName(paramString1)), paramString2, cloneSearchControls(paramSearchControls), paramNamingListener);
    if (((paramNamingListener instanceof UnsolicitedNotificationListener)) && (!unsolicited)) {
      addUnsolicited();
    }
  }
  
  public void addNamingListener(Name paramName, String paramString, SearchControls paramSearchControls, NamingListener paramNamingListener)
    throws NamingException
  {
    addNamingListener(getTargetName(paramName), paramString, paramSearchControls, paramNamingListener);
  }
  
  public void addNamingListener(Name paramName, String paramString, Object[] paramArrayOfObject, SearchControls paramSearchControls, NamingListener paramNamingListener)
    throws NamingException
  {
    addNamingListener(getTargetName(paramName), paramString, paramArrayOfObject, paramSearchControls, paramNamingListener);
  }
  
  public void addNamingListener(String paramString1, String paramString2, Object[] paramArrayOfObject, SearchControls paramSearchControls, NamingListener paramNamingListener)
    throws NamingException
  {
    String str = SearchFilter.format(paramString2, paramArrayOfObject);
    addNamingListener(getTargetName(new CompositeName(paramString1)), str, paramSearchControls, paramNamingListener);
  }
  
  public boolean targetMustExist()
  {
    return true;
  }
  
  private static String getTargetName(Name paramName)
    throws NamingException
  {
    if ((paramName instanceof CompositeName))
    {
      if (paramName.size() > 1) {
        throw new InvalidNameException("Target cannot span multiple namespaces: " + paramName);
      }
      if (paramName.isEmpty()) {
        return "";
      }
      return paramName.get(0);
    }
    return paramName.toString();
  }
  
  private void addUnsolicited()
    throws NamingException
  {
    ensureOpen();
    synchronized (eventSupport)
    {
      clnt.addUnsolicited(this);
      unsolicited = true;
    }
  }
  
  private void removeUnsolicited()
  {
    if (eventSupport == null) {
      return;
    }
    synchronized (eventSupport)
    {
      if ((unsolicited) && (clnt != null)) {
        clnt.removeUnsolicited(this);
      }
      unsolicited = false;
    }
  }
  
  void fireUnsolicited(Object paramObject)
  {
    synchronized (eventSupport)
    {
      if (unsolicited)
      {
        eventSupport.fireUnsolicited(paramObject);
        if ((paramObject instanceof NamingException)) {
          unsolicited = false;
        }
      }
    }
  }
  
  static
  {
    EMPTY_SCHEMA.setReadOnly(new SchemaViolationException("Cannot update schema object"));
  }
  
  static final class SearchArgs
  {
    Name name;
    String filter;
    SearchControls cons;
    String[] reqAttrs;
    
    SearchArgs(Name paramName, String paramString, SearchControls paramSearchControls, String[] paramArrayOfString)
    {
      name = paramName;
      filter = paramString;
      cons = paramSearchControls;
      reqAttrs = paramArrayOfString;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\LdapCtx.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */