package sun.security.jgss;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import sun.security.action.GetPropertyAction;
import sun.security.jgss.spi.MechanismFactory;
import sun.security.jgss.wrapper.NativeGSSFactory;
import sun.security.jgss.wrapper.SunNativeProvider;

public final class ProviderList
{
  private static final String PROV_PROP_PREFIX = "GssApiMechanism.";
  private static final int PROV_PROP_PREFIX_LEN = "GssApiMechanism.".length();
  private static final String SPI_MECH_FACTORY_TYPE = "sun.security.jgss.spi.MechanismFactory";
  private static final String DEFAULT_MECH_PROP = "sun.security.jgss.mechanism";
  public static final Oid DEFAULT_MECH_OID;
  private ArrayList<PreferencesEntry> preferences = new ArrayList(5);
  private HashMap<PreferencesEntry, MechanismFactory> factories = new HashMap(5);
  private HashSet<Oid> mechs = new HashSet(5);
  private final GSSCaller caller;
  
  public ProviderList(GSSCaller paramGSSCaller, boolean paramBoolean)
  {
    caller = paramGSSCaller;
    Provider[] arrayOfProvider;
    if (paramBoolean)
    {
      arrayOfProvider = new Provider[1];
      arrayOfProvider[0] = new SunNativeProvider();
    }
    else
    {
      arrayOfProvider = Security.getProviders();
    }
    for (int i = 0; i < arrayOfProvider.length; i++)
    {
      Provider localProvider = arrayOfProvider[i];
      try
      {
        addProviderAtEnd(localProvider, null);
      }
      catch (GSSException localGSSException)
      {
        GSSUtil.debug("Error in adding provider " + localProvider.getName() + ": " + localGSSException);
      }
    }
  }
  
  private boolean isMechFactoryProperty(String paramString)
  {
    return (paramString.startsWith("GssApiMechanism.")) || (paramString.regionMatches(true, 0, "GssApiMechanism.", 0, PROV_PROP_PREFIX_LEN));
  }
  
  private Oid getOidFromMechFactoryProperty(String paramString)
    throws GSSException
  {
    String str = paramString.substring(PROV_PROP_PREFIX_LEN);
    return new Oid(str);
  }
  
  public synchronized MechanismFactory getMechFactory(Oid paramOid)
    throws GSSException
  {
    if (paramOid == null) {
      paramOid = DEFAULT_MECH_OID;
    }
    return getMechFactory(paramOid, null);
  }
  
  public synchronized MechanismFactory getMechFactory(Oid paramOid, Provider paramProvider)
    throws GSSException
  {
    if (paramOid == null) {
      paramOid = DEFAULT_MECH_OID;
    }
    if (paramProvider == null)
    {
      Iterator localIterator = preferences.iterator();
      while (localIterator.hasNext())
      {
        PreferencesEntry localPreferencesEntry2 = (PreferencesEntry)localIterator.next();
        if (localPreferencesEntry2.impliesMechanism(paramOid))
        {
          MechanismFactory localMechanismFactory = getMechFactory(localPreferencesEntry2, paramOid);
          if (localMechanismFactory != null) {
            return localMechanismFactory;
          }
        }
      }
      throw new GSSExceptionImpl(2, paramOid);
    }
    PreferencesEntry localPreferencesEntry1 = new PreferencesEntry(paramProvider, paramOid);
    return getMechFactory(localPreferencesEntry1, paramOid);
  }
  
  private MechanismFactory getMechFactory(PreferencesEntry paramPreferencesEntry, Oid paramOid)
    throws GSSException
  {
    Provider localProvider = paramPreferencesEntry.getProvider();
    PreferencesEntry localPreferencesEntry = new PreferencesEntry(localProvider, paramOid);
    MechanismFactory localMechanismFactory = (MechanismFactory)factories.get(localPreferencesEntry);
    if (localMechanismFactory == null)
    {
      String str1 = "GssApiMechanism." + paramOid.toString();
      String str2 = localProvider.getProperty(str1);
      if (str2 != null)
      {
        localMechanismFactory = getMechFactoryImpl(localProvider, str2, paramOid, caller);
        factories.put(localPreferencesEntry, localMechanismFactory);
      }
      else if (paramPreferencesEntry.getOid() != null)
      {
        throw new GSSExceptionImpl(2, "Provider " + localProvider.getName() + " does not support mechanism " + paramOid);
      }
    }
    return localMechanismFactory;
  }
  
  private static MechanismFactory getMechFactoryImpl(Provider paramProvider, String paramString, Oid paramOid, GSSCaller paramGSSCaller)
    throws GSSException
  {
    try
    {
      Class localClass1 = Class.forName("sun.security.jgss.spi.MechanismFactory");
      ClassLoader localClassLoader = paramProvider.getClass().getClassLoader();
      Class localClass2;
      if (localClassLoader != null) {
        localClass2 = localClassLoader.loadClass(paramString);
      } else {
        localClass2 = Class.forName(paramString);
      }
      if (localClass1.isAssignableFrom(localClass2))
      {
        Constructor localConstructor = localClass2.getConstructor(new Class[] { GSSCaller.class });
        MechanismFactory localMechanismFactory = (MechanismFactory)localConstructor.newInstance(new Object[] { paramGSSCaller });
        if ((localMechanismFactory instanceof NativeGSSFactory)) {
          ((NativeGSSFactory)localMechanismFactory).setMech(paramOid);
        }
        return localMechanismFactory;
      }
      throw createGSSException(paramProvider, paramString, "is not a sun.security.jgss.spi.MechanismFactory", null);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw createGSSException(paramProvider, paramString, "cannot be created", localClassNotFoundException);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      throw createGSSException(paramProvider, paramString, "cannot be created", localNoSuchMethodException);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw createGSSException(paramProvider, paramString, "cannot be created", localInvocationTargetException);
    }
    catch (InstantiationException localInstantiationException)
    {
      throw createGSSException(paramProvider, paramString, "cannot be created", localInstantiationException);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw createGSSException(paramProvider, paramString, "cannot be created", localIllegalAccessException);
    }
    catch (SecurityException localSecurityException)
    {
      throw createGSSException(paramProvider, paramString, "cannot be created", localSecurityException);
    }
  }
  
  private static GSSException createGSSException(Provider paramProvider, String paramString1, String paramString2, Exception paramException)
  {
    String str = paramString1 + " configured by " + paramProvider.getName() + " for GSS-API Mechanism Factory ";
    return new GSSExceptionImpl(2, str + paramString2, paramException);
  }
  
  public Oid[] getMechs()
  {
    return (Oid[])mechs.toArray(new Oid[0]);
  }
  
  public synchronized void addProviderAtFront(Provider paramProvider, Oid paramOid)
    throws GSSException
  {
    PreferencesEntry localPreferencesEntry1 = new PreferencesEntry(paramProvider, paramOid);
    Iterator localIterator = preferences.iterator();
    while (localIterator.hasNext())
    {
      PreferencesEntry localPreferencesEntry2 = (PreferencesEntry)localIterator.next();
      if (localPreferencesEntry1.implies(localPreferencesEntry2)) {
        localIterator.remove();
      }
    }
    boolean bool;
    if (paramOid == null)
    {
      bool = addAllMechsFromProvider(paramProvider);
    }
    else
    {
      String str = paramOid.toString();
      if (paramProvider.getProperty("GssApiMechanism." + str) == null) {
        throw new GSSExceptionImpl(2, "Provider " + paramProvider.getName() + " does not support " + str);
      }
      mechs.add(paramOid);
      bool = true;
    }
    if (bool) {
      preferences.add(0, localPreferencesEntry1);
    }
  }
  
  public synchronized void addProviderAtEnd(Provider paramProvider, Oid paramOid)
    throws GSSException
  {
    PreferencesEntry localPreferencesEntry1 = new PreferencesEntry(paramProvider, paramOid);
    Iterator localIterator = preferences.iterator();
    while (localIterator.hasNext())
    {
      PreferencesEntry localPreferencesEntry2 = (PreferencesEntry)localIterator.next();
      if (localPreferencesEntry2.implies(localPreferencesEntry1)) {
        return;
      }
    }
    boolean bool;
    if (paramOid == null)
    {
      bool = addAllMechsFromProvider(paramProvider);
    }
    else
    {
      String str = paramOid.toString();
      if (paramProvider.getProperty("GssApiMechanism." + str) == null) {
        throw new GSSExceptionImpl(2, "Provider " + paramProvider.getName() + " does not support " + str);
      }
      mechs.add(paramOid);
      bool = true;
    }
    if (bool) {
      preferences.add(localPreferencesEntry1);
    }
  }
  
  private boolean addAllMechsFromProvider(Provider paramProvider)
  {
    boolean bool = false;
    Enumeration localEnumeration = paramProvider.keys();
    while (localEnumeration.hasMoreElements())
    {
      String str = (String)localEnumeration.nextElement();
      if (isMechFactoryProperty(str)) {
        try
        {
          Oid localOid = getOidFromMechFactoryProperty(str);
          mechs.add(localOid);
          bool = true;
        }
        catch (GSSException localGSSException)
        {
          GSSUtil.debug("Ignore the invalid property " + str + " from provider " + paramProvider.getName());
        }
      }
    }
    return bool;
  }
  
  static
  {
    Oid localOid = null;
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.security.jgss.mechanism"));
    if (str != null) {
      localOid = GSSUtil.createOid(str);
    }
    DEFAULT_MECH_OID = localOid == null ? GSSUtil.GSS_KRB5_MECH_OID : localOid;
  }
  
  private static final class PreferencesEntry
  {
    private Provider p;
    private Oid oid;
    
    PreferencesEntry(Provider paramProvider, Oid paramOid)
    {
      p = paramProvider;
      oid = paramOid;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (!(paramObject instanceof PreferencesEntry)) {
        return false;
      }
      PreferencesEntry localPreferencesEntry = (PreferencesEntry)paramObject;
      if (p.getName().equals(p.getName()))
      {
        if ((oid != null) && (oid != null)) {
          return oid.equals(oid);
        }
        return (oid == null) && (oid == null);
      }
      return false;
    }
    
    public int hashCode()
    {
      int i = 17;
      i = 37 * i + p.getName().hashCode();
      if (oid != null) {
        i = 37 * i + oid.hashCode();
      }
      return i;
    }
    
    boolean implies(Object paramObject)
    {
      if ((paramObject instanceof PreferencesEntry))
      {
        PreferencesEntry localPreferencesEntry = (PreferencesEntry)paramObject;
        return (equals(localPreferencesEntry)) || ((p.getName().equals(p.getName())) && (oid == null));
      }
      return false;
    }
    
    Provider getProvider()
    {
      return p;
    }
    
    Oid getOid()
    {
      return oid;
    }
    
    boolean impliesMechanism(Oid paramOid)
    {
      return (oid == null) || (oid.equals(paramOid));
    }
    
    public String toString()
    {
      StringBuffer localStringBuffer = new StringBuffer("<");
      localStringBuffer.append(p.getName());
      localStringBuffer.append(", ");
      localStringBuffer.append(oid);
      localStringBuffer.append(">");
      return localStringBuffer.toString();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jgss\ProviderList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */