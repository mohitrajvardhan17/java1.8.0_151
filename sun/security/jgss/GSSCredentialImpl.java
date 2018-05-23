package sun.security.jgss;

import com.sun.security.jgss.ExtendedGSSCredential;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.jgss.spnego.SpNegoCredElement;

public class GSSCredentialImpl
  implements ExtendedGSSCredential
{
  private GSSManagerImpl gssManager = null;
  private boolean destroyed = false;
  private Hashtable<SearchKey, GSSCredentialSpi> hashtable = null;
  private GSSCredentialSpi tempCred = null;
  
  GSSCredentialImpl(GSSManagerImpl paramGSSManagerImpl, int paramInt)
    throws GSSException
  {
    this(paramGSSManagerImpl, null, 0, (Oid[])null, paramInt);
  }
  
  GSSCredentialImpl(GSSManagerImpl paramGSSManagerImpl, GSSName paramGSSName, int paramInt1, Oid paramOid, int paramInt2)
    throws GSSException
  {
    if (paramOid == null) {
      paramOid = ProviderList.DEFAULT_MECH_OID;
    }
    init(paramGSSManagerImpl);
    add(paramGSSName, paramInt1, paramInt1, paramOid, paramInt2);
  }
  
  GSSCredentialImpl(GSSManagerImpl paramGSSManagerImpl, GSSName paramGSSName, int paramInt1, Oid[] paramArrayOfOid, int paramInt2)
    throws GSSException
  {
    init(paramGSSManagerImpl);
    int i = 0;
    if (paramArrayOfOid == null)
    {
      paramArrayOfOid = paramGSSManagerImpl.getMechs();
      i = 1;
    }
    for (int j = 0; j < paramArrayOfOid.length; j++) {
      try
      {
        add(paramGSSName, paramInt1, paramInt1, paramArrayOfOid[j], paramInt2);
      }
      catch (GSSException localGSSException)
      {
        if (i != 0) {
          GSSUtil.debug("Ignore " + localGSSException + " while acquring cred for " + paramArrayOfOid[j]);
        } else {
          throw localGSSException;
        }
      }
    }
    if ((hashtable.size() == 0) || (paramInt2 != getUsage())) {
      throw new GSSException(13);
    }
  }
  
  public GSSCredentialImpl(GSSManagerImpl paramGSSManagerImpl, GSSCredentialSpi paramGSSCredentialSpi)
    throws GSSException
  {
    init(paramGSSManagerImpl);
    int i = 2;
    if (paramGSSCredentialSpi.isInitiatorCredential()) {
      if (paramGSSCredentialSpi.isAcceptorCredential()) {
        i = 0;
      } else {
        i = 1;
      }
    }
    SearchKey localSearchKey = new SearchKey(paramGSSCredentialSpi.getMechanism(), i);
    tempCred = paramGSSCredentialSpi;
    hashtable.put(localSearchKey, tempCred);
    if (!GSSUtil.isSpNegoMech(paramGSSCredentialSpi.getMechanism()))
    {
      localSearchKey = new SearchKey(GSSUtil.GSS_SPNEGO_MECH_OID, i);
      hashtable.put(localSearchKey, new SpNegoCredElement(paramGSSCredentialSpi));
    }
  }
  
  void init(GSSManagerImpl paramGSSManagerImpl)
  {
    gssManager = paramGSSManagerImpl;
    hashtable = new Hashtable(paramGSSManagerImpl.getMechs().length);
  }
  
  public void dispose()
    throws GSSException
  {
    if (!destroyed)
    {
      Enumeration localEnumeration = hashtable.elements();
      while (localEnumeration.hasMoreElements())
      {
        GSSCredentialSpi localGSSCredentialSpi = (GSSCredentialSpi)localEnumeration.nextElement();
        localGSSCredentialSpi.dispose();
      }
      destroyed = true;
    }
  }
  
  public GSSCredential impersonate(GSSName paramGSSName)
    throws GSSException
  {
    if (destroyed) {
      throw new IllegalStateException("This credential is no longer valid");
    }
    Oid localOid = tempCred.getMechanism();
    GSSNameSpi localGSSNameSpi = paramGSSName == null ? null : ((GSSNameImpl)paramGSSName).getElement(localOid);
    GSSCredentialSpi localGSSCredentialSpi = tempCred.impersonate(localGSSNameSpi);
    return localGSSCredentialSpi == null ? null : new GSSCredentialImpl(gssManager, localGSSCredentialSpi);
  }
  
  public GSSName getName()
    throws GSSException
  {
    if (destroyed) {
      throw new IllegalStateException("This credential is no longer valid");
    }
    return GSSNameImpl.wrapElement(gssManager, tempCred.getName());
  }
  
  public GSSName getName(Oid paramOid)
    throws GSSException
  {
    if (destroyed) {
      throw new IllegalStateException("This credential is no longer valid");
    }
    SearchKey localSearchKey = null;
    GSSCredentialSpi localGSSCredentialSpi = null;
    if (paramOid == null) {
      paramOid = ProviderList.DEFAULT_MECH_OID;
    }
    localSearchKey = new SearchKey(paramOid, 1);
    localGSSCredentialSpi = (GSSCredentialSpi)hashtable.get(localSearchKey);
    if (localGSSCredentialSpi == null)
    {
      localSearchKey = new SearchKey(paramOid, 2);
      localGSSCredentialSpi = (GSSCredentialSpi)hashtable.get(localSearchKey);
    }
    if (localGSSCredentialSpi == null)
    {
      localSearchKey = new SearchKey(paramOid, 0);
      localGSSCredentialSpi = (GSSCredentialSpi)hashtable.get(localSearchKey);
    }
    if (localGSSCredentialSpi == null) {
      throw new GSSExceptionImpl(2, paramOid);
    }
    return GSSNameImpl.wrapElement(gssManager, localGSSCredentialSpi.getName());
  }
  
  public int getRemainingLifetime()
    throws GSSException
  {
    if (destroyed) {
      throw new IllegalStateException("This credential is no longer valid");
    }
    int i = 0;
    int j = 0;
    int k = 0;
    int m = Integer.MAX_VALUE;
    Enumeration localEnumeration = hashtable.keys();
    while (localEnumeration.hasMoreElements())
    {
      SearchKey localSearchKey = (SearchKey)localEnumeration.nextElement();
      GSSCredentialSpi localGSSCredentialSpi = (GSSCredentialSpi)hashtable.get(localSearchKey);
      if (localSearchKey.getUsage() == 1)
      {
        i = localGSSCredentialSpi.getInitLifetime();
      }
      else if (localSearchKey.getUsage() == 2)
      {
        i = localGSSCredentialSpi.getAcceptLifetime();
      }
      else
      {
        j = localGSSCredentialSpi.getInitLifetime();
        k = localGSSCredentialSpi.getAcceptLifetime();
        i = j < k ? j : k;
      }
      if (m > i) {
        m = i;
      }
    }
    return m;
  }
  
  public int getRemainingInitLifetime(Oid paramOid)
    throws GSSException
  {
    if (destroyed) {
      throw new IllegalStateException("This credential is no longer valid");
    }
    GSSCredentialSpi localGSSCredentialSpi = null;
    SearchKey localSearchKey = null;
    int i = 0;
    int j = 0;
    if (paramOid == null) {
      paramOid = ProviderList.DEFAULT_MECH_OID;
    }
    localSearchKey = new SearchKey(paramOid, 1);
    localGSSCredentialSpi = (GSSCredentialSpi)hashtable.get(localSearchKey);
    if (localGSSCredentialSpi != null)
    {
      i = 1;
      if (j < localGSSCredentialSpi.getInitLifetime()) {
        j = localGSSCredentialSpi.getInitLifetime();
      }
    }
    localSearchKey = new SearchKey(paramOid, 0);
    localGSSCredentialSpi = (GSSCredentialSpi)hashtable.get(localSearchKey);
    if (localGSSCredentialSpi != null)
    {
      i = 1;
      if (j < localGSSCredentialSpi.getInitLifetime()) {
        j = localGSSCredentialSpi.getInitLifetime();
      }
    }
    if (i == 0) {
      throw new GSSExceptionImpl(2, paramOid);
    }
    return j;
  }
  
  public int getRemainingAcceptLifetime(Oid paramOid)
    throws GSSException
  {
    if (destroyed) {
      throw new IllegalStateException("This credential is no longer valid");
    }
    GSSCredentialSpi localGSSCredentialSpi = null;
    SearchKey localSearchKey = null;
    int i = 0;
    int j = 0;
    if (paramOid == null) {
      paramOid = ProviderList.DEFAULT_MECH_OID;
    }
    localSearchKey = new SearchKey(paramOid, 2);
    localGSSCredentialSpi = (GSSCredentialSpi)hashtable.get(localSearchKey);
    if (localGSSCredentialSpi != null)
    {
      i = 1;
      if (j < localGSSCredentialSpi.getAcceptLifetime()) {
        j = localGSSCredentialSpi.getAcceptLifetime();
      }
    }
    localSearchKey = new SearchKey(paramOid, 0);
    localGSSCredentialSpi = (GSSCredentialSpi)hashtable.get(localSearchKey);
    if (localGSSCredentialSpi != null)
    {
      i = 1;
      if (j < localGSSCredentialSpi.getAcceptLifetime()) {
        j = localGSSCredentialSpi.getAcceptLifetime();
      }
    }
    if (i == 0) {
      throw new GSSExceptionImpl(2, paramOid);
    }
    return j;
  }
  
  public int getUsage()
    throws GSSException
  {
    if (destroyed) {
      throw new IllegalStateException("This credential is no longer valid");
    }
    int i = 0;
    int j = 0;
    Enumeration localEnumeration = hashtable.keys();
    while (localEnumeration.hasMoreElements())
    {
      SearchKey localSearchKey = (SearchKey)localEnumeration.nextElement();
      if (localSearchKey.getUsage() == 1) {
        i = 1;
      } else if (localSearchKey.getUsage() == 2) {
        j = 1;
      } else {
        return 0;
      }
    }
    if (i != 0)
    {
      if (j != 0) {
        return 0;
      }
      return 1;
    }
    return 2;
  }
  
  public int getUsage(Oid paramOid)
    throws GSSException
  {
    if (destroyed) {
      throw new IllegalStateException("This credential is no longer valid");
    }
    GSSCredentialSpi localGSSCredentialSpi = null;
    SearchKey localSearchKey = null;
    int i = 0;
    int j = 0;
    if (paramOid == null) {
      paramOid = ProviderList.DEFAULT_MECH_OID;
    }
    localSearchKey = new SearchKey(paramOid, 1);
    localGSSCredentialSpi = (GSSCredentialSpi)hashtable.get(localSearchKey);
    if (localGSSCredentialSpi != null) {
      i = 1;
    }
    localSearchKey = new SearchKey(paramOid, 2);
    localGSSCredentialSpi = (GSSCredentialSpi)hashtable.get(localSearchKey);
    if (localGSSCredentialSpi != null) {
      j = 1;
    }
    localSearchKey = new SearchKey(paramOid, 0);
    localGSSCredentialSpi = (GSSCredentialSpi)hashtable.get(localSearchKey);
    if (localGSSCredentialSpi != null)
    {
      i = 1;
      j = 1;
    }
    if ((i != 0) && (j != 0)) {
      return 0;
    }
    if (i != 0) {
      return 1;
    }
    if (j != 0) {
      return 2;
    }
    throw new GSSExceptionImpl(2, paramOid);
  }
  
  public Oid[] getMechs()
    throws GSSException
  {
    if (destroyed) {
      throw new IllegalStateException("This credential is no longer valid");
    }
    Vector localVector = new Vector(hashtable.size());
    Enumeration localEnumeration = hashtable.keys();
    while (localEnumeration.hasMoreElements())
    {
      SearchKey localSearchKey = (SearchKey)localEnumeration.nextElement();
      localVector.addElement(localSearchKey.getMech());
    }
    return (Oid[])localVector.toArray(new Oid[0]);
  }
  
  public void add(GSSName paramGSSName, int paramInt1, int paramInt2, Oid paramOid, int paramInt3)
    throws GSSException
  {
    if (destroyed) {
      throw new IllegalStateException("This credential is no longer valid");
    }
    if (paramOid == null) {
      paramOid = ProviderList.DEFAULT_MECH_OID;
    }
    SearchKey localSearchKey = new SearchKey(paramOid, paramInt3);
    if (hashtable.containsKey(localSearchKey)) {
      throw new GSSExceptionImpl(17, "Duplicate element found: " + getElementStr(paramOid, paramInt3));
    }
    GSSNameSpi localGSSNameSpi = paramGSSName == null ? null : ((GSSNameImpl)paramGSSName).getElement(paramOid);
    tempCred = gssManager.getCredentialElement(localGSSNameSpi, paramInt1, paramInt2, paramOid, paramInt3);
    if (tempCred != null) {
      if ((paramInt3 == 0) && ((!tempCred.isAcceptorCredential()) || (!tempCred.isInitiatorCredential())))
      {
        int i;
        int j;
        if (!tempCred.isInitiatorCredential())
        {
          i = 2;
          j = 1;
        }
        else
        {
          i = 1;
          j = 2;
        }
        localSearchKey = new SearchKey(paramOid, i);
        hashtable.put(localSearchKey, tempCred);
        tempCred = gssManager.getCredentialElement(localGSSNameSpi, paramInt1, paramInt2, paramOid, j);
        localSearchKey = new SearchKey(paramOid, j);
        hashtable.put(localSearchKey, tempCred);
      }
      else
      {
        hashtable.put(localSearchKey, tempCred);
      }
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (destroyed) {
      throw new IllegalStateException("This credential is no longer valid");
    }
    if (this == paramObject) {
      return true;
    }
    return (paramObject instanceof GSSCredentialImpl);
  }
  
  public int hashCode()
  {
    if (destroyed) {
      throw new IllegalStateException("This credential is no longer valid");
    }
    return 1;
  }
  
  public GSSCredentialSpi getElement(Oid paramOid, boolean paramBoolean)
    throws GSSException
  {
    if (destroyed) {
      throw new IllegalStateException("This credential is no longer valid");
    }
    SearchKey localSearchKey;
    GSSCredentialSpi localGSSCredentialSpi;
    if (paramOid == null)
    {
      paramOid = ProviderList.DEFAULT_MECH_OID;
      localSearchKey = new SearchKey(paramOid, paramBoolean ? 1 : 2);
      localGSSCredentialSpi = (GSSCredentialSpi)hashtable.get(localSearchKey);
      if (localGSSCredentialSpi == null)
      {
        localSearchKey = new SearchKey(paramOid, 0);
        localGSSCredentialSpi = (GSSCredentialSpi)hashtable.get(localSearchKey);
        if (localGSSCredentialSpi == null)
        {
          Object[] arrayOfObject = hashtable.entrySet().toArray();
          for (int i = 0; i < arrayOfObject.length; i++)
          {
            localGSSCredentialSpi = (GSSCredentialSpi)((Map.Entry)arrayOfObject[i]).getValue();
            if (localGSSCredentialSpi.isInitiatorCredential() == paramBoolean) {
              break;
            }
          }
        }
      }
    }
    else
    {
      if (paramBoolean) {
        localSearchKey = new SearchKey(paramOid, 1);
      } else {
        localSearchKey = new SearchKey(paramOid, 2);
      }
      localGSSCredentialSpi = (GSSCredentialSpi)hashtable.get(localSearchKey);
      if (localGSSCredentialSpi == null)
      {
        localSearchKey = new SearchKey(paramOid, 0);
        localGSSCredentialSpi = (GSSCredentialSpi)hashtable.get(localSearchKey);
      }
    }
    if (localGSSCredentialSpi == null) {
      throw new GSSExceptionImpl(13, "No credential found for: " + getElementStr(paramOid, paramBoolean ? 1 : 2));
    }
    return localGSSCredentialSpi;
  }
  
  Set<GSSCredentialSpi> getElements()
  {
    HashSet localHashSet = new HashSet(hashtable.size());
    Enumeration localEnumeration = hashtable.elements();
    while (localEnumeration.hasMoreElements())
    {
      GSSCredentialSpi localGSSCredentialSpi = (GSSCredentialSpi)localEnumeration.nextElement();
      localHashSet.add(localGSSCredentialSpi);
    }
    return localHashSet;
  }
  
  private static String getElementStr(Oid paramOid, int paramInt)
  {
    String str = paramOid.toString();
    if (paramInt == 1) {
      str = str.concat(" usage: Initiate");
    } else if (paramInt == 2) {
      str = str.concat(" usage: Accept");
    } else {
      str = str.concat(" usage: Initiate and Accept");
    }
    return str;
  }
  
  public String toString()
  {
    if (destroyed) {
      throw new IllegalStateException("This credential is no longer valid");
    }
    GSSCredentialSpi localGSSCredentialSpi = null;
    StringBuffer localStringBuffer = new StringBuffer("[GSSCredential: ");
    Object[] arrayOfObject = hashtable.entrySet().toArray();
    for (int i = 0; i < arrayOfObject.length; i++) {
      try
      {
        localStringBuffer.append('\n');
        localGSSCredentialSpi = (GSSCredentialSpi)((Map.Entry)arrayOfObject[i]).getValue();
        localStringBuffer.append(localGSSCredentialSpi.getName());
        localStringBuffer.append(' ');
        localStringBuffer.append(localGSSCredentialSpi.getMechanism());
        localStringBuffer.append(localGSSCredentialSpi.isInitiatorCredential() ? " Initiate" : "");
        localStringBuffer.append(localGSSCredentialSpi.isAcceptorCredential() ? " Accept" : "");
        localStringBuffer.append(" [");
        localStringBuffer.append(localGSSCredentialSpi.getClass());
        localStringBuffer.append(']');
      }
      catch (GSSException localGSSException) {}
    }
    localStringBuffer.append(']');
    return localStringBuffer.toString();
  }
  
  static class SearchKey
  {
    private Oid mechOid = null;
    private int usage = 0;
    
    public SearchKey(Oid paramOid, int paramInt)
    {
      mechOid = paramOid;
      usage = paramInt;
    }
    
    public Oid getMech()
    {
      return mechOid;
    }
    
    public int getUsage()
    {
      return usage;
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof SearchKey)) {
        return false;
      }
      SearchKey localSearchKey = (SearchKey)paramObject;
      return (mechOid.equals(mechOid)) && (usage == usage);
    }
    
    public int hashCode()
    {
      return mechOid.hashCode();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jgss\GSSCredentialImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */