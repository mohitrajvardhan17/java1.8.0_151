package com.sun.jndi.ldap;

import com.sun.jndi.ldap.pool.PoolCallback;
import com.sun.jndi.ldap.pool.PooledConnection;
import com.sun.jndi.ldap.sasl.LdapSasl;
import com.sun.jndi.ldap.sasl.SaslInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;
import javax.naming.AuthenticationException;
import javax.naming.AuthenticationNotSupportedException;
import javax.naming.CommunicationException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.InvalidAttributeValueException;
import javax.naming.ldap.Control;

public final class LdapClient
  implements PooledConnection
{
  private static final int debug = 0;
  static final boolean caseIgnore = true;
  private static final Hashtable<String, Boolean> defaultBinaryAttrs = new Hashtable(23, 0.75F);
  private static final String DISCONNECT_OID = "1.3.6.1.4.1.1466.20036";
  boolean isLdapv3;
  int referenceCount = 1;
  Connection conn;
  private final PoolCallback pcb;
  private final boolean pooled;
  private boolean authenticateCalled = false;
  static final int SCOPE_BASE_OBJECT = 0;
  static final int SCOPE_ONE_LEVEL = 1;
  static final int SCOPE_SUBTREE = 2;
  static final int ADD = 0;
  static final int DELETE = 1;
  static final int REPLACE = 2;
  static final int LDAP_VERSION3_VERSION2 = 32;
  static final int LDAP_VERSION2 = 2;
  static final int LDAP_VERSION3 = 3;
  static final int LDAP_VERSION = 3;
  static final int LDAP_REF_FOLLOW = 1;
  static final int LDAP_REF_THROW = 2;
  static final int LDAP_REF_IGNORE = 3;
  static final int LDAP_REF_FOLLOW_SCHEME = 4;
  static final String LDAP_URL = "ldap://";
  static final String LDAPS_URL = "ldaps://";
  static final int LBER_BOOLEAN = 1;
  static final int LBER_INTEGER = 2;
  static final int LBER_BITSTRING = 3;
  static final int LBER_OCTETSTRING = 4;
  static final int LBER_NULL = 5;
  static final int LBER_ENUMERATED = 10;
  static final int LBER_SEQUENCE = 48;
  static final int LBER_SET = 49;
  static final int LDAP_SUPERIOR_DN = 128;
  static final int LDAP_REQ_BIND = 96;
  static final int LDAP_REQ_UNBIND = 66;
  static final int LDAP_REQ_SEARCH = 99;
  static final int LDAP_REQ_MODIFY = 102;
  static final int LDAP_REQ_ADD = 104;
  static final int LDAP_REQ_DELETE = 74;
  static final int LDAP_REQ_MODRDN = 108;
  static final int LDAP_REQ_COMPARE = 110;
  static final int LDAP_REQ_ABANDON = 80;
  static final int LDAP_REQ_EXTENSION = 119;
  static final int LDAP_REP_BIND = 97;
  static final int LDAP_REP_SEARCH = 100;
  static final int LDAP_REP_SEARCH_REF = 115;
  static final int LDAP_REP_RESULT = 101;
  static final int LDAP_REP_MODIFY = 103;
  static final int LDAP_REP_ADD = 105;
  static final int LDAP_REP_DELETE = 107;
  static final int LDAP_REP_MODRDN = 109;
  static final int LDAP_REP_COMPARE = 111;
  static final int LDAP_REP_EXTENSION = 120;
  static final int LDAP_REP_REFERRAL = 163;
  static final int LDAP_REP_EXT_OID = 138;
  static final int LDAP_REP_EXT_VAL = 139;
  static final int LDAP_CONTROLS = 160;
  static final String LDAP_CONTROL_MANAGE_DSA_IT = "2.16.840.1.113730.3.4.2";
  static final String LDAP_CONTROL_PREFERRED_LANG = "1.3.6.1.4.1.1466.20035";
  static final String LDAP_CONTROL_PAGED_RESULTS = "1.2.840.113556.1.4.319";
  static final String LDAP_CONTROL_SERVER_SORT_REQ = "1.2.840.113556.1.4.473";
  static final String LDAP_CONTROL_SERVER_SORT_RES = "1.2.840.113556.1.4.474";
  static final int LDAP_SUCCESS = 0;
  static final int LDAP_OPERATIONS_ERROR = 1;
  static final int LDAP_PROTOCOL_ERROR = 2;
  static final int LDAP_TIME_LIMIT_EXCEEDED = 3;
  static final int LDAP_SIZE_LIMIT_EXCEEDED = 4;
  static final int LDAP_COMPARE_FALSE = 5;
  static final int LDAP_COMPARE_TRUE = 6;
  static final int LDAP_AUTH_METHOD_NOT_SUPPORTED = 7;
  static final int LDAP_STRONG_AUTH_REQUIRED = 8;
  static final int LDAP_PARTIAL_RESULTS = 9;
  static final int LDAP_REFERRAL = 10;
  static final int LDAP_ADMIN_LIMIT_EXCEEDED = 11;
  static final int LDAP_UNAVAILABLE_CRITICAL_EXTENSION = 12;
  static final int LDAP_CONFIDENTIALITY_REQUIRED = 13;
  static final int LDAP_SASL_BIND_IN_PROGRESS = 14;
  static final int LDAP_NO_SUCH_ATTRIBUTE = 16;
  static final int LDAP_UNDEFINED_ATTRIBUTE_TYPE = 17;
  static final int LDAP_INAPPROPRIATE_MATCHING = 18;
  static final int LDAP_CONSTRAINT_VIOLATION = 19;
  static final int LDAP_ATTRIBUTE_OR_VALUE_EXISTS = 20;
  static final int LDAP_INVALID_ATTRIBUTE_SYNTAX = 21;
  static final int LDAP_NO_SUCH_OBJECT = 32;
  static final int LDAP_ALIAS_PROBLEM = 33;
  static final int LDAP_INVALID_DN_SYNTAX = 34;
  static final int LDAP_IS_LEAF = 35;
  static final int LDAP_ALIAS_DEREFERENCING_PROBLEM = 36;
  static final int LDAP_INAPPROPRIATE_AUTHENTICATION = 48;
  static final int LDAP_INVALID_CREDENTIALS = 49;
  static final int LDAP_INSUFFICIENT_ACCESS_RIGHTS = 50;
  static final int LDAP_BUSY = 51;
  static final int LDAP_UNAVAILABLE = 52;
  static final int LDAP_UNWILLING_TO_PERFORM = 53;
  static final int LDAP_LOOP_DETECT = 54;
  static final int LDAP_NAMING_VIOLATION = 64;
  static final int LDAP_OBJECT_CLASS_VIOLATION = 65;
  static final int LDAP_NOT_ALLOWED_ON_NON_LEAF = 66;
  static final int LDAP_NOT_ALLOWED_ON_RDN = 67;
  static final int LDAP_ENTRY_ALREADY_EXISTS = 68;
  static final int LDAP_OBJECT_CLASS_MODS_PROHIBITED = 69;
  static final int LDAP_AFFECTS_MULTIPLE_DSAS = 71;
  static final int LDAP_OTHER = 80;
  static final String[] ldap_error_message = { "Success", "Operations Error", "Protocol Error", "Timelimit Exceeded", "Sizelimit Exceeded", "Compare False", "Compare True", "Authentication Method Not Supported", "Strong Authentication Required", null, "Referral", "Administrative Limit Exceeded", "Unavailable Critical Extension", "Confidentiality Required", "SASL Bind In Progress", null, "No Such Attribute", "Undefined Attribute Type", "Inappropriate Matching", "Constraint Violation", "Attribute Or Value Exists", "Invalid Attribute Syntax", null, null, null, null, null, null, null, null, null, null, "No Such Object", "Alias Problem", "Invalid DN Syntax", null, "Alias Dereferencing Problem", null, null, null, null, null, null, null, null, null, null, null, "Inappropriate Authentication", "Invalid Credentials", "Insufficient Access Rights", "Busy", "Unavailable", "Unwilling To Perform", "Loop Detect", null, null, null, null, null, null, null, null, null, "Naming Violation", "Object Class Violation", "Not Allowed On Non-leaf", "Not Allowed On RDN", "Entry Already Exists", "Object Class Modifications Prohibited", null, "Affects Multiple DSAs", null, null, null, null, null, null, null, null, "Other", null, null, null, null, null, null, null, null, null, null };
  private Vector<LdapCtx> unsolicited = new Vector(3);
  
  LdapClient(String paramString1, int paramInt1, String paramString2, int paramInt2, int paramInt3, OutputStream paramOutputStream, PoolCallback paramPoolCallback)
    throws NamingException
  {
    conn = new Connection(this, paramString1, paramInt1, paramString2, paramInt2, paramInt3, paramOutputStream);
    pcb = paramPoolCallback;
    pooled = (paramPoolCallback != null);
  }
  
  synchronized boolean authenticateCalled()
  {
    return authenticateCalled;
  }
  
  synchronized LdapResult authenticate(boolean paramBoolean, String paramString1, Object paramObject, int paramInt, String paramString2, Control[] paramArrayOfControl, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    int i = conn.readTimeout;
    conn.readTimeout = conn.connectTimeout;
    LdapResult localLdapResult = null;
    try
    {
      authenticateCalled = true;
      CommunicationException localCommunicationException1;
      try
      {
        ensureOpen();
      }
      catch (IOException localIOException1)
      {
        localCommunicationException1 = new CommunicationException();
        localCommunicationException1.setRootCause(localIOException1);
        throw localCommunicationException1;
      }
      switch (paramInt)
      {
      case 3: 
      case 32: 
        isLdapv3 = true;
        break;
      case 2: 
        isLdapv3 = false;
        break;
      default: 
        throw new CommunicationException("Protocol version " + paramInt + " not supported");
      }
      CommunicationException localCommunicationException3;
      if ((paramString2.equalsIgnoreCase("none")) || (paramString2.equalsIgnoreCase("anonymous")))
      {
        if ((!paramBoolean) || (paramInt == 2) || (paramInt == 32) || ((paramArrayOfControl != null) && (paramArrayOfControl.length > 0)))
        {
          try
          {
            localLdapResult = ldapBind(paramString1 = null, (byte[])(paramObject = null), paramArrayOfControl, null, false);
            if (status == 0) {
              conn.setBound();
            }
          }
          catch (IOException localIOException2)
          {
            localCommunicationException1 = new CommunicationException("anonymous bind failed: " + conn.host + ":" + conn.port);
            localCommunicationException1.setRootCause(localIOException2);
            throw localCommunicationException1;
          }
        }
        else
        {
          localLdapResult = new LdapResult();
          status = 0;
        }
      }
      else if (paramString2.equalsIgnoreCase("simple"))
      {
        byte[] arrayOfByte = null;
        try
        {
          arrayOfByte = encodePassword(paramObject, isLdapv3);
          localLdapResult = ldapBind(paramString1, arrayOfByte, paramArrayOfControl, null, false);
          if (status == 0) {
            conn.setBound();
          }
        }
        catch (IOException localIOException4)
        {
          int j;
          localCommunicationException3 = new CommunicationException("simple bind failed: " + conn.host + ":" + conn.port);
          localCommunicationException3.setRootCause(localIOException4);
          throw localCommunicationException3;
        }
        finally
        {
          if ((arrayOfByte != paramObject) && (arrayOfByte != null)) {
            for (int m = 0; m < arrayOfByte.length; m++) {
              arrayOfByte[m] = 0;
            }
          }
        }
      }
      else if (isLdapv3)
      {
        try
        {
          localLdapResult = LdapSasl.saslBind(this, conn, conn.host, paramString1, paramObject, paramString2, paramHashtable, paramArrayOfControl);
          if (status == 0) {
            conn.setBound();
          }
        }
        catch (IOException localIOException3)
        {
          CommunicationException localCommunicationException2 = new CommunicationException("SASL bind failed: " + conn.host + ":" + conn.port);
          localCommunicationException2.setRootCause(localIOException3);
          throw localCommunicationException2;
        }
      }
      else
      {
        throw new AuthenticationNotSupportedException(paramString2);
      }
      if ((paramBoolean) && (status == 2) && (paramInt == 32) && ((paramString2.equalsIgnoreCase("none")) || (paramString2.equalsIgnoreCase("anonymous")) || (paramString2.equalsIgnoreCase("simple"))))
      {
        localObject1 = null;
        try
        {
          isLdapv3 = false;
          localObject1 = encodePassword(paramObject, false);
          localLdapResult = ldapBind(paramString1, (byte[])localObject1, paramArrayOfControl, null, false);
          if (status == 0) {
            conn.setBound();
          }
        }
        catch (IOException localIOException5)
        {
          int k;
          localCommunicationException3 = new CommunicationException(paramString2 + ":" + conn.host + ":" + conn.port);
          localCommunicationException3.setRootCause(localIOException5);
          throw localCommunicationException3;
        }
        finally
        {
          if ((localObject1 != paramObject) && (localObject1 != null)) {
            for (int n = 0; n < localObject1.length; n++) {
              localObject1[n] = 0;
            }
          }
        }
      }
      if (status == 32) {
        throw new AuthenticationException(getErrorMessage(status, errorMessage));
      }
      conn.setV3(isLdapv3);
      Object localObject1 = localLdapResult;
      return (LdapResult)localObject1;
    }
    finally
    {
      conn.readTimeout = i;
    }
  }
  
  public synchronized LdapResult ldapBind(String paramString1, byte[] paramArrayOfByte, Control[] paramArrayOfControl, String paramString2, boolean paramBoolean)
    throws IOException, NamingException
  {
    ensureOpen();
    conn.abandonOutstandingReqs(null);
    BerEncoder localBerEncoder = new BerEncoder();
    int i = conn.getMsgId();
    LdapResult localLdapResult = new LdapResult();
    status = 1;
    localBerEncoder.beginSeq(48);
    localBerEncoder.encodeInt(i);
    localBerEncoder.beginSeq(96);
    localBerEncoder.encodeInt(isLdapv3 ? 3 : 2);
    localBerEncoder.encodeString(paramString1, isLdapv3);
    if (paramString2 != null)
    {
      localBerEncoder.beginSeq(163);
      localBerEncoder.encodeString(paramString2, isLdapv3);
      if (paramArrayOfByte != null) {
        localBerEncoder.encodeOctetString(paramArrayOfByte, 4);
      }
      localBerEncoder.endSeq();
    }
    else if (paramArrayOfByte != null)
    {
      localBerEncoder.encodeOctetString(paramArrayOfByte, 128);
    }
    else
    {
      localBerEncoder.encodeOctetString(null, 128, 0, 0);
    }
    localBerEncoder.endSeq();
    if (isLdapv3) {
      encodeControls(localBerEncoder, paramArrayOfControl);
    }
    localBerEncoder.endSeq();
    LdapRequest localLdapRequest = conn.writeRequest(localBerEncoder, i, paramBoolean);
    if (paramArrayOfByte != null) {
      localBerEncoder.reset();
    }
    BerDecoder localBerDecoder = conn.readReply(localLdapRequest);
    localBerDecoder.parseSeq(null);
    localBerDecoder.parseInt();
    if (localBerDecoder.parseByte() != 97) {
      return localLdapResult;
    }
    localBerDecoder.parseLength();
    parseResult(localBerDecoder, localLdapResult, isLdapv3);
    if ((isLdapv3) && (localBerDecoder.bytesLeft() > 0) && (localBerDecoder.peekByte() == 135)) {
      serverCreds = localBerDecoder.parseOctetString(135, null);
    }
    resControls = (isLdapv3 ? parseControls(localBerDecoder) : null);
    conn.removeRequest(localLdapRequest);
    return localLdapResult;
  }
  
  boolean usingSaslStreams()
  {
    return conn.inStream instanceof SaslInputStream;
  }
  
  synchronized void incRefCount()
  {
    referenceCount += 1;
  }
  
  private static byte[] encodePassword(Object paramObject, boolean paramBoolean)
    throws IOException
  {
    if ((paramObject instanceof char[])) {
      paramObject = new String((char[])paramObject);
    }
    if ((paramObject instanceof String))
    {
      if (paramBoolean) {
        return ((String)paramObject).getBytes("UTF8");
      }
      return ((String)paramObject).getBytes("8859_1");
    }
    return (byte[])paramObject;
  }
  
  synchronized void close(Control[] paramArrayOfControl, boolean paramBoolean)
  {
    referenceCount -= 1;
    if ((referenceCount <= 0) && (conn != null)) {
      if (!pooled)
      {
        conn.cleanup(paramArrayOfControl, false);
        conn = null;
      }
      else if (paramBoolean)
      {
        conn.cleanup(paramArrayOfControl, false);
        conn = null;
        pcb.removePooledConnection(this);
      }
      else
      {
        pcb.releasePooledConnection(this);
      }
    }
  }
  
  private void forceClose(boolean paramBoolean)
  {
    referenceCount = 0;
    if (conn != null)
    {
      conn.cleanup(null, false);
      conn = null;
      if (paramBoolean) {
        pcb.removePooledConnection(this);
      }
    }
  }
  
  protected void finalize()
  {
    forceClose(pooled);
  }
  
  public synchronized void closeConnection()
  {
    forceClose(false);
  }
  
  void processConnectionClosure()
  {
    if (unsolicited.size() > 0)
    {
      String str;
      if (conn != null) {
        str = conn.host + ":" + conn.port + " connection closed";
      } else {
        str = "Connection closed";
      }
      notifyUnsolicited(new CommunicationException(str));
    }
    if (pooled) {
      pcb.removePooledConnection(this);
    }
  }
  
  LdapResult search(String paramString1, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, String[] paramArrayOfString, String paramString2, int paramInt5, Control[] paramArrayOfControl, Hashtable<String, Boolean> paramHashtable, boolean paramBoolean2, int paramInt6)
    throws IOException, NamingException
  {
    ensureOpen();
    LdapResult localLdapResult = new LdapResult();
    BerEncoder localBerEncoder = new BerEncoder();
    int i = conn.getMsgId();
    localBerEncoder.beginSeq(48);
    localBerEncoder.encodeInt(i);
    localBerEncoder.beginSeq(99);
    localBerEncoder.encodeString(paramString1 == null ? "" : paramString1, isLdapv3);
    localBerEncoder.encodeInt(paramInt1, 10);
    localBerEncoder.encodeInt(paramInt2, 10);
    localBerEncoder.encodeInt(paramInt3);
    localBerEncoder.encodeInt(paramInt4);
    localBerEncoder.encodeBoolean(paramBoolean1);
    Filter.encodeFilterString(localBerEncoder, paramString2, isLdapv3);
    localBerEncoder.beginSeq(48);
    localBerEncoder.encodeStringArray(paramArrayOfString, isLdapv3);
    localBerEncoder.endSeq();
    localBerEncoder.endSeq();
    if (isLdapv3) {
      encodeControls(localBerEncoder, paramArrayOfControl);
    }
    localBerEncoder.endSeq();
    LdapRequest localLdapRequest = conn.writeRequest(localBerEncoder, i, false, paramInt6);
    msgId = i;
    status = 0;
    if (paramBoolean2) {
      localLdapResult = getSearchReply(localLdapRequest, paramInt5, localLdapResult, paramHashtable);
    }
    return localLdapResult;
  }
  
  void clearSearchReply(LdapResult paramLdapResult, Control[] paramArrayOfControl)
  {
    if ((paramLdapResult != null) && (conn != null))
    {
      LdapRequest localLdapRequest = conn.findRequest(msgId);
      if (localLdapRequest == null) {
        return;
      }
      if (localLdapRequest.hasSearchCompleted()) {
        conn.removeRequest(localLdapRequest);
      } else {
        conn.abandonRequest(localLdapRequest, paramArrayOfControl);
      }
    }
  }
  
  LdapResult getSearchReply(int paramInt, LdapResult paramLdapResult, Hashtable<String, Boolean> paramHashtable)
    throws IOException, NamingException
  {
    ensureOpen();
    LdapRequest localLdapRequest;
    if ((localLdapRequest = conn.findRequest(msgId)) == null) {
      return null;
    }
    return getSearchReply(localLdapRequest, paramInt, paramLdapResult, paramHashtable);
  }
  
  private LdapResult getSearchReply(LdapRequest paramLdapRequest, int paramInt, LdapResult paramLdapResult, Hashtable<String, Boolean> paramHashtable)
    throws IOException, NamingException
  {
    if (paramInt == 0) {
      paramInt = Integer.MAX_VALUE;
    }
    if (entries != null) {
      entries.setSize(0);
    } else {
      entries = new Vector(paramInt == Integer.MAX_VALUE ? 32 : paramInt);
    }
    if (referrals != null) {
      referrals.setSize(0);
    }
    int k = 0;
    while (k < paramInt)
    {
      BerDecoder localBerDecoder = conn.readReply(paramLdapRequest);
      localBerDecoder.parseSeq(null);
      localBerDecoder.parseInt();
      int i = localBerDecoder.parseSeq(null);
      if (i == 100)
      {
        BasicAttributes localBasicAttributes = new BasicAttributes(true);
        String str = localBerDecoder.parseString(isLdapv3);
        LdapEntry localLdapEntry = new LdapEntry(str, localBasicAttributes);
        int[] arrayOfInt = new int[1];
        localBerDecoder.parseSeq(arrayOfInt);
        int j = localBerDecoder.getParsePosition() + arrayOfInt[0];
        while ((localBerDecoder.getParsePosition() < j) && (localBerDecoder.bytesLeft() > 0))
        {
          Attribute localAttribute = parseAttribute(localBerDecoder, paramHashtable);
          localBasicAttributes.put(localAttribute);
        }
        respCtls = (isLdapv3 ? parseControls(localBerDecoder) : null);
        entries.addElement(localLdapEntry);
        k++;
      }
      else if ((i == 115) && (isLdapv3))
      {
        Vector localVector = new Vector(4);
        if (localBerDecoder.peekByte() == 48) {
          localBerDecoder.parseSeq(null);
        }
        while ((localBerDecoder.bytesLeft() > 0) && (localBerDecoder.peekByte() == 4)) {
          localVector.addElement(localBerDecoder.parseString(isLdapv3));
        }
        if (referrals == null) {
          referrals = new Vector(4);
        }
        referrals.addElement(localVector);
        resControls = (isLdapv3 ? parseControls(localBerDecoder) : null);
      }
      else if (i == 120)
      {
        parseExtResponse(localBerDecoder, paramLdapResult);
      }
      else if (i == 101)
      {
        parseResult(localBerDecoder, paramLdapResult, isLdapv3);
        resControls = (isLdapv3 ? parseControls(localBerDecoder) : null);
        conn.removeRequest(paramLdapRequest);
        return paramLdapResult;
      }
    }
    return paramLdapResult;
  }
  
  private Attribute parseAttribute(BerDecoder paramBerDecoder, Hashtable<String, Boolean> paramHashtable)
    throws IOException
  {
    int[] arrayOfInt = new int[1];
    int i = paramBerDecoder.parseSeq(null);
    String str = paramBerDecoder.parseString(isLdapv3);
    boolean bool = isBinaryValued(str, paramHashtable);
    LdapAttribute localLdapAttribute = new LdapAttribute(str);
    if ((i = paramBerDecoder.parseSeq(arrayOfInt)) == 49)
    {
      int j = arrayOfInt[0];
      for (;;)
      {
        if ((paramBerDecoder.bytesLeft() > 0) && (j > 0)) {
          try
          {
            j -= parseAttributeValue(paramBerDecoder, localLdapAttribute, bool);
          }
          catch (IOException localIOException)
          {
            paramBerDecoder.seek(j);
          }
        }
      }
    }
    else
    {
      paramBerDecoder.seek(arrayOfInt[0]);
    }
    return localLdapAttribute;
  }
  
  private int parseAttributeValue(BerDecoder paramBerDecoder, Attribute paramAttribute, boolean paramBoolean)
    throws IOException
  {
    int[] arrayOfInt = new int[1];
    if (paramBoolean) {
      paramAttribute.add(paramBerDecoder.parseOctetString(paramBerDecoder.peekByte(), arrayOfInt));
    } else {
      paramAttribute.add(paramBerDecoder.parseStringWithTag(4, isLdapv3, arrayOfInt));
    }
    return arrayOfInt[0];
  }
  
  private boolean isBinaryValued(String paramString, Hashtable<String, Boolean> paramHashtable)
  {
    String str = paramString.toLowerCase(Locale.ENGLISH);
    return (str.indexOf(";binary") != -1) || (defaultBinaryAttrs.containsKey(str)) || ((paramHashtable != null) && (paramHashtable.containsKey(str)));
  }
  
  static void parseResult(BerDecoder paramBerDecoder, LdapResult paramLdapResult, boolean paramBoolean)
    throws IOException
  {
    status = paramBerDecoder.parseEnumeration();
    matchedDN = paramBerDecoder.parseString(paramBoolean);
    errorMessage = paramBerDecoder.parseString(paramBoolean);
    if ((paramBoolean) && (paramBerDecoder.bytesLeft() > 0) && (paramBerDecoder.peekByte() == 163))
    {
      Vector localVector = new Vector(4);
      int[] arrayOfInt = new int[1];
      paramBerDecoder.parseSeq(arrayOfInt);
      int i = paramBerDecoder.getParsePosition() + arrayOfInt[0];
      while ((paramBerDecoder.getParsePosition() < i) && (paramBerDecoder.bytesLeft() > 0)) {
        localVector.addElement(paramBerDecoder.parseString(paramBoolean));
      }
      if (referrals == null) {
        referrals = new Vector(4);
      }
      referrals.addElement(localVector);
    }
  }
  
  static Vector<Control> parseControls(BerDecoder paramBerDecoder)
    throws IOException
  {
    if ((paramBerDecoder.bytesLeft() > 0) && (paramBerDecoder.peekByte() == 160))
    {
      Vector localVector = new Vector(4);
      boolean bool = false;
      byte[] arrayOfByte = null;
      int[] arrayOfInt = new int[1];
      paramBerDecoder.parseSeq(arrayOfInt);
      int i = paramBerDecoder.getParsePosition() + arrayOfInt[0];
      while ((paramBerDecoder.getParsePosition() < i) && (paramBerDecoder.bytesLeft() > 0))
      {
        paramBerDecoder.parseSeq(null);
        String str = paramBerDecoder.parseString(true);
        if ((paramBerDecoder.bytesLeft() > 0) && (paramBerDecoder.peekByte() == 1)) {
          bool = paramBerDecoder.parseBoolean();
        }
        if ((paramBerDecoder.bytesLeft() > 0) && (paramBerDecoder.peekByte() == 4)) {
          arrayOfByte = paramBerDecoder.parseOctetString(4, null);
        }
        if (str != null) {
          localVector.addElement(new BasicControl(str, bool, arrayOfByte));
        }
      }
      return localVector;
    }
    return null;
  }
  
  private void parseExtResponse(BerDecoder paramBerDecoder, LdapResult paramLdapResult)
    throws IOException
  {
    parseResult(paramBerDecoder, paramLdapResult, isLdapv3);
    if ((paramBerDecoder.bytesLeft() > 0) && (paramBerDecoder.peekByte() == 138)) {
      extensionId = paramBerDecoder.parseStringWithTag(138, isLdapv3, null);
    }
    if ((paramBerDecoder.bytesLeft() > 0) && (paramBerDecoder.peekByte() == 139)) {
      extensionValue = paramBerDecoder.parseOctetString(139, null);
    }
    resControls = parseControls(paramBerDecoder);
  }
  
  static void encodeControls(BerEncoder paramBerEncoder, Control[] paramArrayOfControl)
    throws IOException
  {
    if ((paramArrayOfControl == null) || (paramArrayOfControl.length == 0)) {
      return;
    }
    paramBerEncoder.beginSeq(160);
    for (int i = 0; i < paramArrayOfControl.length; i++)
    {
      paramBerEncoder.beginSeq(48);
      paramBerEncoder.encodeString(paramArrayOfControl[i].getID(), true);
      if (paramArrayOfControl[i].isCritical()) {
        paramBerEncoder.encodeBoolean(true);
      }
      byte[] arrayOfByte;
      if ((arrayOfByte = paramArrayOfControl[i].getEncodedValue()) != null) {
        paramBerEncoder.encodeOctetString(arrayOfByte, 4);
      }
      paramBerEncoder.endSeq();
    }
    paramBerEncoder.endSeq();
  }
  
  private LdapResult processReply(LdapRequest paramLdapRequest, LdapResult paramLdapResult, int paramInt)
    throws IOException, NamingException
  {
    BerDecoder localBerDecoder = conn.readReply(paramLdapRequest);
    localBerDecoder.parseSeq(null);
    localBerDecoder.parseInt();
    if (localBerDecoder.parseByte() != paramInt) {
      return paramLdapResult;
    }
    localBerDecoder.parseLength();
    parseResult(localBerDecoder, paramLdapResult, isLdapv3);
    resControls = (isLdapv3 ? parseControls(localBerDecoder) : null);
    conn.removeRequest(paramLdapRequest);
    return paramLdapResult;
  }
  
  LdapResult modify(String paramString, int[] paramArrayOfInt, Attribute[] paramArrayOfAttribute, Control[] paramArrayOfControl)
    throws IOException, NamingException
  {
    ensureOpen();
    LdapResult localLdapResult = new LdapResult();
    status = 1;
    if ((paramString == null) || (paramArrayOfInt.length != paramArrayOfAttribute.length)) {
      return localLdapResult;
    }
    BerEncoder localBerEncoder = new BerEncoder();
    int i = conn.getMsgId();
    localBerEncoder.beginSeq(48);
    localBerEncoder.encodeInt(i);
    localBerEncoder.beginSeq(102);
    localBerEncoder.encodeString(paramString, isLdapv3);
    localBerEncoder.beginSeq(48);
    for (int j = 0; j < paramArrayOfInt.length; j++)
    {
      localBerEncoder.beginSeq(48);
      localBerEncoder.encodeInt(paramArrayOfInt[j], 10);
      if ((paramArrayOfInt[j] == 0) && (hasNoValue(paramArrayOfAttribute[j]))) {
        throw new InvalidAttributeValueException("'" + paramArrayOfAttribute[j].getID() + "' has no values.");
      }
      encodeAttribute(localBerEncoder, paramArrayOfAttribute[j]);
      localBerEncoder.endSeq();
    }
    localBerEncoder.endSeq();
    localBerEncoder.endSeq();
    if (isLdapv3) {
      encodeControls(localBerEncoder, paramArrayOfControl);
    }
    localBerEncoder.endSeq();
    LdapRequest localLdapRequest = conn.writeRequest(localBerEncoder, i);
    return processReply(localLdapRequest, localLdapResult, 103);
  }
  
  private void encodeAttribute(BerEncoder paramBerEncoder, Attribute paramAttribute)
    throws IOException, NamingException
  {
    paramBerEncoder.beginSeq(48);
    paramBerEncoder.encodeString(paramAttribute.getID(), isLdapv3);
    paramBerEncoder.beginSeq(49);
    NamingEnumeration localNamingEnumeration = paramAttribute.getAll();
    while (localNamingEnumeration.hasMore())
    {
      Object localObject = localNamingEnumeration.next();
      if ((localObject instanceof String)) {
        paramBerEncoder.encodeString((String)localObject, isLdapv3);
      } else if ((localObject instanceof byte[])) {
        paramBerEncoder.encodeOctetString((byte[])localObject, 4);
      } else if (localObject != null) {
        throw new InvalidAttributeValueException("Malformed '" + paramAttribute.getID() + "' attribute value");
      }
    }
    paramBerEncoder.endSeq();
    paramBerEncoder.endSeq();
  }
  
  private static boolean hasNoValue(Attribute paramAttribute)
    throws NamingException
  {
    return (paramAttribute.size() == 0) || ((paramAttribute.size() == 1) && (paramAttribute.get() == null));
  }
  
  LdapResult add(LdapEntry paramLdapEntry, Control[] paramArrayOfControl)
    throws IOException, NamingException
  {
    ensureOpen();
    LdapResult localLdapResult = new LdapResult();
    status = 1;
    if ((paramLdapEntry == null) || (DN == null)) {
      return localLdapResult;
    }
    BerEncoder localBerEncoder = new BerEncoder();
    int i = conn.getMsgId();
    localBerEncoder.beginSeq(48);
    localBerEncoder.encodeInt(i);
    localBerEncoder.beginSeq(104);
    localBerEncoder.encodeString(DN, isLdapv3);
    localBerEncoder.beginSeq(48);
    NamingEnumeration localNamingEnumeration = attributes.getAll();
    while (localNamingEnumeration.hasMore())
    {
      Attribute localAttribute = (Attribute)localNamingEnumeration.next();
      if (hasNoValue(localAttribute)) {
        throw new InvalidAttributeValueException("'" + localAttribute.getID() + "' has no values.");
      }
      encodeAttribute(localBerEncoder, localAttribute);
    }
    localBerEncoder.endSeq();
    localBerEncoder.endSeq();
    if (isLdapv3) {
      encodeControls(localBerEncoder, paramArrayOfControl);
    }
    localBerEncoder.endSeq();
    LdapRequest localLdapRequest = conn.writeRequest(localBerEncoder, i);
    return processReply(localLdapRequest, localLdapResult, 105);
  }
  
  LdapResult delete(String paramString, Control[] paramArrayOfControl)
    throws IOException, NamingException
  {
    ensureOpen();
    LdapResult localLdapResult = new LdapResult();
    status = 1;
    if (paramString == null) {
      return localLdapResult;
    }
    BerEncoder localBerEncoder = new BerEncoder();
    int i = conn.getMsgId();
    localBerEncoder.beginSeq(48);
    localBerEncoder.encodeInt(i);
    localBerEncoder.encodeString(paramString, 74, isLdapv3);
    if (isLdapv3) {
      encodeControls(localBerEncoder, paramArrayOfControl);
    }
    localBerEncoder.endSeq();
    LdapRequest localLdapRequest = conn.writeRequest(localBerEncoder, i);
    return processReply(localLdapRequest, localLdapResult, 107);
  }
  
  LdapResult moddn(String paramString1, String paramString2, boolean paramBoolean, String paramString3, Control[] paramArrayOfControl)
    throws IOException, NamingException
  {
    ensureOpen();
    int i = (paramString3 != null) && (paramString3.length() > 0) ? 1 : 0;
    LdapResult localLdapResult = new LdapResult();
    status = 1;
    if ((paramString1 == null) || (paramString2 == null)) {
      return localLdapResult;
    }
    BerEncoder localBerEncoder = new BerEncoder();
    int j = conn.getMsgId();
    localBerEncoder.beginSeq(48);
    localBerEncoder.encodeInt(j);
    localBerEncoder.beginSeq(108);
    localBerEncoder.encodeString(paramString1, isLdapv3);
    localBerEncoder.encodeString(paramString2, isLdapv3);
    localBerEncoder.encodeBoolean(paramBoolean);
    if ((isLdapv3) && (i != 0)) {
      localBerEncoder.encodeString(paramString3, 128, isLdapv3);
    }
    localBerEncoder.endSeq();
    if (isLdapv3) {
      encodeControls(localBerEncoder, paramArrayOfControl);
    }
    localBerEncoder.endSeq();
    LdapRequest localLdapRequest = conn.writeRequest(localBerEncoder, j);
    return processReply(localLdapRequest, localLdapResult, 109);
  }
  
  LdapResult compare(String paramString1, String paramString2, String paramString3, Control[] paramArrayOfControl)
    throws IOException, NamingException
  {
    ensureOpen();
    LdapResult localLdapResult = new LdapResult();
    status = 1;
    if ((paramString1 == null) || (paramString2 == null) || (paramString3 == null)) {
      return localLdapResult;
    }
    BerEncoder localBerEncoder = new BerEncoder();
    int i = conn.getMsgId();
    localBerEncoder.beginSeq(48);
    localBerEncoder.encodeInt(i);
    localBerEncoder.beginSeq(110);
    localBerEncoder.encodeString(paramString1, isLdapv3);
    localBerEncoder.beginSeq(48);
    localBerEncoder.encodeString(paramString2, isLdapv3);
    byte[] arrayOfByte = isLdapv3 ? paramString3.getBytes("UTF8") : paramString3.getBytes("8859_1");
    localBerEncoder.encodeOctetString(Filter.unescapeFilterValue(arrayOfByte, 0, arrayOfByte.length), 4);
    localBerEncoder.endSeq();
    localBerEncoder.endSeq();
    if (isLdapv3) {
      encodeControls(localBerEncoder, paramArrayOfControl);
    }
    localBerEncoder.endSeq();
    LdapRequest localLdapRequest = conn.writeRequest(localBerEncoder, i);
    return processReply(localLdapRequest, localLdapResult, 111);
  }
  
  LdapResult extendedOp(String paramString, byte[] paramArrayOfByte, Control[] paramArrayOfControl, boolean paramBoolean)
    throws IOException, NamingException
  {
    ensureOpen();
    LdapResult localLdapResult = new LdapResult();
    status = 1;
    if (paramString == null) {
      return localLdapResult;
    }
    BerEncoder localBerEncoder = new BerEncoder();
    int i = conn.getMsgId();
    localBerEncoder.beginSeq(48);
    localBerEncoder.encodeInt(i);
    localBerEncoder.beginSeq(119);
    localBerEncoder.encodeString(paramString, 128, isLdapv3);
    if (paramArrayOfByte != null) {
      localBerEncoder.encodeOctetString(paramArrayOfByte, 129);
    }
    localBerEncoder.endSeq();
    encodeControls(localBerEncoder, paramArrayOfControl);
    localBerEncoder.endSeq();
    LdapRequest localLdapRequest = conn.writeRequest(localBerEncoder, i, paramBoolean);
    BerDecoder localBerDecoder = conn.readReply(localLdapRequest);
    localBerDecoder.parseSeq(null);
    localBerDecoder.parseInt();
    if (localBerDecoder.parseByte() != 120) {
      return localLdapResult;
    }
    localBerDecoder.parseLength();
    parseExtResponse(localBerDecoder, localLdapResult);
    conn.removeRequest(localLdapRequest);
    return localLdapResult;
  }
  
  static String getErrorMessage(int paramInt, String paramString)
  {
    String str = "[LDAP: error code " + paramInt;
    if ((paramString != null) && (paramString.length() != 0)) {
      str = str + " - " + paramString + "]";
    } else {
      try
      {
        if (ldap_error_message[paramInt] != null) {
          str = str + " - " + ldap_error_message[paramInt] + "]";
        }
      }
      catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
      {
        str = str + "]";
      }
    }
    return str;
  }
  
  void addUnsolicited(LdapCtx paramLdapCtx)
  {
    unsolicited.addElement(paramLdapCtx);
  }
  
  void removeUnsolicited(LdapCtx paramLdapCtx)
  {
    unsolicited.removeElement(paramLdapCtx);
  }
  
  void processUnsolicited(BerDecoder paramBerDecoder)
  {
    try
    {
      LdapResult localLdapResult = new LdapResult();
      paramBerDecoder.parseSeq(null);
      paramBerDecoder.parseInt();
      if (paramBerDecoder.parseByte() != 120) {
        throw new IOException("Unsolicited Notification must be an Extended Response");
      }
      paramBerDecoder.parseLength();
      parseExtResponse(paramBerDecoder, localLdapResult);
      if ("1.3.6.1.4.1.1466.20036".equals(extensionId)) {
        forceClose(pooled);
      }
      localObject1 = null;
      UnsolicitedResponseImpl localUnsolicitedResponseImpl = null;
      synchronized (unsolicited)
      {
        if (unsolicited.size() > 0)
        {
          localObject1 = (LdapCtx)unsolicited.elementAt(0);
          localUnsolicitedResponseImpl = new UnsolicitedResponseImpl(extensionId, extensionValue, referrals, status, errorMessage, matchedDN, resControls != null ? ((LdapCtx)localObject1).convertControls(resControls) : null);
        }
      }
      if (localUnsolicitedResponseImpl != null)
      {
        notifyUnsolicited(localUnsolicitedResponseImpl);
        if ("1.3.6.1.4.1.1466.20036".equals(extensionId)) {
          notifyUnsolicited(new CommunicationException("Connection closed"));
        }
      }
    }
    catch (IOException localIOException)
    {
      Object localObject1 = new CommunicationException("Problem parsing unsolicited notification");
      ((NamingException)localObject1).setRootCause(localIOException);
      notifyUnsolicited(localObject1);
    }
    catch (NamingException localNamingException)
    {
      notifyUnsolicited(localNamingException);
    }
  }
  
  private void notifyUnsolicited(Object paramObject)
  {
    Vector localVector;
    synchronized (unsolicited)
    {
      localVector = new Vector(unsolicited);
      if ((paramObject instanceof NamingException)) {
        unsolicited.setSize(0);
      }
    }
    for (int i = 0; i < localVector.size(); i++) {
      ((LdapCtx)localVector.elementAt(i)).fireUnsolicited(paramObject);
    }
  }
  
  private void ensureOpen()
    throws IOException
  {
    if ((conn == null) || (!conn.useable))
    {
      if ((conn != null) && (conn.closureReason != null)) {
        throw conn.closureReason;
      }
      throw new IOException("connection closed");
    }
  }
  
  static LdapClient getInstance(boolean paramBoolean, String paramString1, int paramInt1, String paramString2, int paramInt2, int paramInt3, OutputStream paramOutputStream, int paramInt4, String paramString3, Control[] paramArrayOfControl, String paramString4, String paramString5, Object paramObject, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    if ((paramBoolean) && (LdapPoolManager.isPoolingAllowed(paramString2, paramOutputStream, paramString3, paramString4, paramHashtable)))
    {
      LdapClient localLdapClient = LdapPoolManager.getLdapClient(paramString1, paramInt1, paramString2, paramInt2, paramInt3, paramOutputStream, paramInt4, paramString3, paramArrayOfControl, paramString4, paramString5, paramObject, paramHashtable);
      referenceCount = 1;
      return localLdapClient;
    }
    return new LdapClient(paramString1, paramInt1, paramString2, paramInt2, paramInt3, paramOutputStream, null);
  }
  
  static
  {
    defaultBinaryAttrs.put("userpassword", Boolean.TRUE);
    defaultBinaryAttrs.put("javaserializeddata", Boolean.TRUE);
    defaultBinaryAttrs.put("javaserializedobject", Boolean.TRUE);
    defaultBinaryAttrs.put("jpegphoto", Boolean.TRUE);
    defaultBinaryAttrs.put("audio", Boolean.TRUE);
    defaultBinaryAttrs.put("thumbnailphoto", Boolean.TRUE);
    defaultBinaryAttrs.put("thumbnaillogo", Boolean.TRUE);
    defaultBinaryAttrs.put("usercertificate", Boolean.TRUE);
    defaultBinaryAttrs.put("cacertificate", Boolean.TRUE);
    defaultBinaryAttrs.put("certificaterevocationlist", Boolean.TRUE);
    defaultBinaryAttrs.put("authorityrevocationlist", Boolean.TRUE);
    defaultBinaryAttrs.put("crosscertificatepair", Boolean.TRUE);
    defaultBinaryAttrs.put("photo", Boolean.TRUE);
    defaultBinaryAttrs.put("personalsignature", Boolean.TRUE);
    defaultBinaryAttrs.put("x500uniqueidentifier", Boolean.TRUE);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\LdapClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */