package java.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import sun.security.util.Debug;

public abstract class Provider
  extends Properties
{
  static final long serialVersionUID = -4298000515446427739L;
  private static final Debug debug = Debug.getInstance("provider", "Provider");
  private String name;
  private String info;
  private double version;
  private transient Set<Map.Entry<Object, Object>> entrySet = null;
  private transient int entrySetCallCount = 0;
  private transient boolean initialized;
  private transient boolean legacyChanged;
  private transient boolean servicesChanged;
  private transient Map<String, String> legacyStrings;
  private transient Map<ServiceKey, Service> serviceMap;
  private transient Map<ServiceKey, Service> legacyMap;
  private transient Set<Service> serviceSet;
  private static final String ALIAS_PREFIX = "Alg.Alias.";
  private static final String ALIAS_PREFIX_LOWER = "alg.alias.";
  private static final int ALIAS_LENGTH = "Alg.Alias.".length();
  private static volatile ServiceKey previousKey = new ServiceKey("", "", false, null);
  private static final Map<String, EngineDescription> knownEngines = new HashMap();
  
  protected Provider(String paramString1, double paramDouble, String paramString2)
  {
    name = paramString1;
    version = paramDouble;
    info = paramString2;
    putId();
    initialized = true;
  }
  
  public String getName()
  {
    return name;
  }
  
  public double getVersion()
  {
    return version;
  }
  
  public String getInfo()
  {
    return info;
  }
  
  public String toString()
  {
    return name + " version " + version;
  }
  
  public synchronized void clear()
  {
    check("clearProviderProperties." + name);
    if (debug != null) {
      debug.println("Remove " + name + " provider properties");
    }
    implClear();
  }
  
  public synchronized void load(InputStream paramInputStream)
    throws IOException
  {
    check("putProviderProperty." + name);
    if (debug != null) {
      debug.println("Load " + name + " provider properties");
    }
    Properties localProperties = new Properties();
    localProperties.load(paramInputStream);
    implPutAll(localProperties);
  }
  
  public synchronized void putAll(Map<?, ?> paramMap)
  {
    check("putProviderProperty." + name);
    if (debug != null) {
      debug.println("Put all " + name + " provider properties");
    }
    implPutAll(paramMap);
  }
  
  public synchronized Set<Map.Entry<Object, Object>> entrySet()
  {
    checkInitialized();
    if (entrySet == null) {
      if (entrySetCallCount++ == 0) {
        entrySet = Collections.unmodifiableMap(this).entrySet();
      } else {
        return super.entrySet();
      }
    }
    if (entrySetCallCount != 2) {
      throw new RuntimeException("Internal error.");
    }
    return entrySet;
  }
  
  public Set<Object> keySet()
  {
    checkInitialized();
    return Collections.unmodifiableSet(super.keySet());
  }
  
  public Collection<Object> values()
  {
    checkInitialized();
    return Collections.unmodifiableCollection(super.values());
  }
  
  public synchronized Object put(Object paramObject1, Object paramObject2)
  {
    check("putProviderProperty." + name);
    if (debug != null) {
      debug.println("Set " + name + " provider property [" + paramObject1 + "/" + paramObject2 + "]");
    }
    return implPut(paramObject1, paramObject2);
  }
  
  public synchronized Object putIfAbsent(Object paramObject1, Object paramObject2)
  {
    check("putProviderProperty." + name);
    if (debug != null) {
      debug.println("Set " + name + " provider property [" + paramObject1 + "/" + paramObject2 + "]");
    }
    return implPutIfAbsent(paramObject1, paramObject2);
  }
  
  public synchronized Object remove(Object paramObject)
  {
    check("removeProviderProperty." + name);
    if (debug != null) {
      debug.println("Remove " + name + " provider property " + paramObject);
    }
    return implRemove(paramObject);
  }
  
  public synchronized boolean remove(Object paramObject1, Object paramObject2)
  {
    check("removeProviderProperty." + name);
    if (debug != null) {
      debug.println("Remove " + name + " provider property " + paramObject1);
    }
    return implRemove(paramObject1, paramObject2);
  }
  
  public synchronized boolean replace(Object paramObject1, Object paramObject2, Object paramObject3)
  {
    check("putProviderProperty." + name);
    if (debug != null) {
      debug.println("Replace " + name + " provider property " + paramObject1);
    }
    return implReplace(paramObject1, paramObject2, paramObject3);
  }
  
  public synchronized Object replace(Object paramObject1, Object paramObject2)
  {
    check("putProviderProperty." + name);
    if (debug != null) {
      debug.println("Replace " + name + " provider property " + paramObject1);
    }
    return implReplace(paramObject1, paramObject2);
  }
  
  public synchronized void replaceAll(BiFunction<? super Object, ? super Object, ? extends Object> paramBiFunction)
  {
    check("putProviderProperty." + name);
    if (debug != null) {
      debug.println("ReplaceAll " + name + " provider property ");
    }
    implReplaceAll(paramBiFunction);
  }
  
  public synchronized Object compute(Object paramObject, BiFunction<? super Object, ? super Object, ? extends Object> paramBiFunction)
  {
    check("putProviderProperty." + name);
    check("removeProviderProperty" + name);
    if (debug != null) {
      debug.println("Compute " + name + " provider property " + paramObject);
    }
    return implCompute(paramObject, paramBiFunction);
  }
  
  public synchronized Object computeIfAbsent(Object paramObject, Function<? super Object, ? extends Object> paramFunction)
  {
    check("putProviderProperty." + name);
    check("removeProviderProperty" + name);
    if (debug != null) {
      debug.println("ComputeIfAbsent " + name + " provider property " + paramObject);
    }
    return implComputeIfAbsent(paramObject, paramFunction);
  }
  
  public synchronized Object computeIfPresent(Object paramObject, BiFunction<? super Object, ? super Object, ? extends Object> paramBiFunction)
  {
    check("putProviderProperty." + name);
    check("removeProviderProperty" + name);
    if (debug != null) {
      debug.println("ComputeIfPresent " + name + " provider property " + paramObject);
    }
    return implComputeIfPresent(paramObject, paramBiFunction);
  }
  
  public synchronized Object merge(Object paramObject1, Object paramObject2, BiFunction<? super Object, ? super Object, ? extends Object> paramBiFunction)
  {
    check("putProviderProperty." + name);
    check("removeProviderProperty" + name);
    if (debug != null) {
      debug.println("Merge " + name + " provider property " + paramObject1);
    }
    return implMerge(paramObject1, paramObject2, paramBiFunction);
  }
  
  public Object get(Object paramObject)
  {
    checkInitialized();
    return super.get(paramObject);
  }
  
  public synchronized Object getOrDefault(Object paramObject1, Object paramObject2)
  {
    checkInitialized();
    return super.getOrDefault(paramObject1, paramObject2);
  }
  
  public synchronized void forEach(BiConsumer<? super Object, ? super Object> paramBiConsumer)
  {
    checkInitialized();
    super.forEach(paramBiConsumer);
  }
  
  public Enumeration<Object> keys()
  {
    checkInitialized();
    return super.keys();
  }
  
  public Enumeration<Object> elements()
  {
    checkInitialized();
    return super.elements();
  }
  
  public String getProperty(String paramString)
  {
    checkInitialized();
    return super.getProperty(paramString);
  }
  
  private void checkInitialized()
  {
    if (!initialized) {
      throw new IllegalStateException();
    }
  }
  
  private void check(String paramString)
  {
    checkInitialized();
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkSecurityAccess(paramString);
    }
  }
  
  private void putId()
  {
    super.put("Provider.id name", String.valueOf(name));
    super.put("Provider.id version", String.valueOf(version));
    super.put("Provider.id info", String.valueOf(info));
    super.put("Provider.id className", getClass().getName());
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    HashMap localHashMap = new HashMap();
    Iterator localIterator = super.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      localHashMap.put(localEntry.getKey(), localEntry.getValue());
    }
    defaults = null;
    paramObjectInputStream.defaultReadObject();
    implClear();
    initialized = true;
    putAll(localHashMap);
  }
  
  private boolean checkLegacy(Object paramObject)
  {
    String str = (String)paramObject;
    if (str.startsWith("Provider.")) {
      return false;
    }
    legacyChanged = true;
    if (legacyStrings == null) {
      legacyStrings = new LinkedHashMap();
    }
    return true;
  }
  
  private void implPutAll(Map<?, ?> paramMap)
  {
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      implPut(localEntry.getKey(), localEntry.getValue());
    }
  }
  
  private Object implRemove(Object paramObject)
  {
    if ((paramObject instanceof String))
    {
      if (!checkLegacy(paramObject)) {
        return null;
      }
      legacyStrings.remove((String)paramObject);
    }
    return super.remove(paramObject);
  }
  
  private boolean implRemove(Object paramObject1, Object paramObject2)
  {
    if (((paramObject1 instanceof String)) && ((paramObject2 instanceof String)))
    {
      if (!checkLegacy(paramObject1)) {
        return false;
      }
      legacyStrings.remove((String)paramObject1, paramObject2);
    }
    return super.remove(paramObject1, paramObject2);
  }
  
  private boolean implReplace(Object paramObject1, Object paramObject2, Object paramObject3)
  {
    if (((paramObject1 instanceof String)) && ((paramObject2 instanceof String)) && ((paramObject3 instanceof String)))
    {
      if (!checkLegacy(paramObject1)) {
        return false;
      }
      legacyStrings.replace((String)paramObject1, (String)paramObject2, (String)paramObject3);
    }
    return super.replace(paramObject1, paramObject2, paramObject3);
  }
  
  private Object implReplace(Object paramObject1, Object paramObject2)
  {
    if (((paramObject1 instanceof String)) && ((paramObject2 instanceof String)))
    {
      if (!checkLegacy(paramObject1)) {
        return null;
      }
      legacyStrings.replace((String)paramObject1, (String)paramObject2);
    }
    return super.replace(paramObject1, paramObject2);
  }
  
  private void implReplaceAll(BiFunction<? super Object, ? super Object, ? extends Object> paramBiFunction)
  {
    legacyChanged = true;
    if (legacyStrings == null) {
      legacyStrings = new LinkedHashMap();
    } else {
      legacyStrings.replaceAll(paramBiFunction);
    }
    super.replaceAll(paramBiFunction);
  }
  
  private Object implMerge(Object paramObject1, Object paramObject2, BiFunction<? super Object, ? super Object, ? extends Object> paramBiFunction)
  {
    if (((paramObject1 instanceof String)) && ((paramObject2 instanceof String)))
    {
      if (!checkLegacy(paramObject1)) {
        return null;
      }
      legacyStrings.merge((String)paramObject1, (String)paramObject2, paramBiFunction);
    }
    return super.merge(paramObject1, paramObject2, paramBiFunction);
  }
  
  private Object implCompute(Object paramObject, BiFunction<? super Object, ? super Object, ? extends Object> paramBiFunction)
  {
    if ((paramObject instanceof String))
    {
      if (!checkLegacy(paramObject)) {
        return null;
      }
      legacyStrings.computeIfAbsent((String)paramObject, (Function)paramBiFunction);
    }
    return super.compute(paramObject, paramBiFunction);
  }
  
  private Object implComputeIfAbsent(Object paramObject, Function<? super Object, ? extends Object> paramFunction)
  {
    if ((paramObject instanceof String))
    {
      if (!checkLegacy(paramObject)) {
        return null;
      }
      legacyStrings.computeIfAbsent((String)paramObject, paramFunction);
    }
    return super.computeIfAbsent(paramObject, paramFunction);
  }
  
  private Object implComputeIfPresent(Object paramObject, BiFunction<? super Object, ? super Object, ? extends Object> paramBiFunction)
  {
    if ((paramObject instanceof String))
    {
      if (!checkLegacy(paramObject)) {
        return null;
      }
      legacyStrings.computeIfPresent((String)paramObject, paramBiFunction);
    }
    return super.computeIfPresent(paramObject, paramBiFunction);
  }
  
  private Object implPut(Object paramObject1, Object paramObject2)
  {
    if (((paramObject1 instanceof String)) && ((paramObject2 instanceof String)))
    {
      if (!checkLegacy(paramObject1)) {
        return null;
      }
      legacyStrings.put((String)paramObject1, (String)paramObject2);
    }
    return super.put(paramObject1, paramObject2);
  }
  
  private Object implPutIfAbsent(Object paramObject1, Object paramObject2)
  {
    if (((paramObject1 instanceof String)) && ((paramObject2 instanceof String)))
    {
      if (!checkLegacy(paramObject1)) {
        return null;
      }
      legacyStrings.putIfAbsent((String)paramObject1, (String)paramObject2);
    }
    return super.putIfAbsent(paramObject1, paramObject2);
  }
  
  private void implClear()
  {
    if (legacyStrings != null) {
      legacyStrings.clear();
    }
    if (legacyMap != null) {
      legacyMap.clear();
    }
    if (serviceMap != null) {
      serviceMap.clear();
    }
    legacyChanged = false;
    servicesChanged = false;
    serviceSet = null;
    super.clear();
    putId();
  }
  
  private void ensureLegacyParsed()
  {
    if ((!legacyChanged) || (legacyStrings == null)) {
      return;
    }
    serviceSet = null;
    if (legacyMap == null) {
      legacyMap = new LinkedHashMap();
    } else {
      legacyMap.clear();
    }
    Iterator localIterator = legacyStrings.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      parseLegacyPut((String)localEntry.getKey(), (String)localEntry.getValue());
    }
    removeInvalidServices(legacyMap);
    legacyChanged = false;
  }
  
  private void removeInvalidServices(Map<ServiceKey, Service> paramMap)
  {
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Service localService = (Service)((Map.Entry)localIterator.next()).getValue();
      if (!localService.isValid()) {
        localIterator.remove();
      }
    }
  }
  
  private String[] getTypeAndAlgorithm(String paramString)
  {
    int i = paramString.indexOf(".");
    if (i < 1)
    {
      if (debug != null) {
        debug.println("Ignoring invalid entry in provider " + name + ":" + paramString);
      }
      return null;
    }
    String str1 = paramString.substring(0, i);
    String str2 = paramString.substring(i + 1);
    return new String[] { str1, str2 };
  }
  
  private void parseLegacyPut(String paramString1, String paramString2)
  {
    Object localObject1;
    Object localObject2;
    String str2;
    String str3;
    Object localObject3;
    Object localObject4;
    if (paramString1.toLowerCase(Locale.ENGLISH).startsWith("alg.alias."))
    {
      localObject1 = paramString2;
      String str1 = paramString1.substring(ALIAS_LENGTH);
      localObject2 = getTypeAndAlgorithm(str1);
      if (localObject2 == null) {
        return;
      }
      str2 = getEngineName(localObject2[0]);
      str3 = localObject2[1].intern();
      localObject3 = new ServiceKey(str2, (String)localObject1, true, null);
      localObject4 = (Service)legacyMap.get(localObject3);
      if (localObject4 == null)
      {
        localObject4 = new Service(this, null);
        type = str2;
        algorithm = ((String)localObject1);
        legacyMap.put(localObject3, localObject4);
      }
      legacyMap.put(new ServiceKey(str2, str3, true, null), localObject4);
      ((Service)localObject4).addAlias(str3);
    }
    else
    {
      localObject1 = getTypeAndAlgorithm(paramString1);
      if (localObject1 == null) {
        return;
      }
      int i = localObject1[1].indexOf(' ');
      if (i == -1)
      {
        localObject2 = getEngineName(localObject1[0]);
        str2 = localObject1[1].intern();
        str3 = paramString2;
        localObject3 = new ServiceKey((String)localObject2, str2, true, null);
        localObject4 = (Service)legacyMap.get(localObject3);
        if (localObject4 == null)
        {
          localObject4 = new Service(this, null);
          type = ((String)localObject2);
          algorithm = str2;
          legacyMap.put(localObject3, localObject4);
        }
        className = str3;
      }
      else
      {
        localObject2 = paramString2;
        str2 = getEngineName(localObject1[0]);
        str3 = localObject1[1];
        localObject3 = str3.substring(0, i).intern();
        for (localObject4 = str3.substring(i + 1); ((String)localObject4).startsWith(" "); localObject4 = ((String)localObject4).substring(1)) {}
        localObject4 = ((String)localObject4).intern();
        ServiceKey localServiceKey = new ServiceKey(str2, (String)localObject3, true, null);
        Service localService = (Service)legacyMap.get(localServiceKey);
        if (localService == null)
        {
          localService = new Service(this, null);
          type = str2;
          algorithm = ((String)localObject3);
          legacyMap.put(localServiceKey, localService);
        }
        localService.addAttribute((String)localObject4, (String)localObject2);
      }
    }
  }
  
  public synchronized Service getService(String paramString1, String paramString2)
  {
    checkInitialized();
    ServiceKey localServiceKey = previousKey;
    if (!localServiceKey.matches(paramString1, paramString2))
    {
      localServiceKey = new ServiceKey(paramString1, paramString2, false, null);
      previousKey = localServiceKey;
    }
    if (serviceMap != null)
    {
      Service localService = (Service)serviceMap.get(localServiceKey);
      if (localService != null) {
        return localService;
      }
    }
    ensureLegacyParsed();
    return legacyMap != null ? (Service)legacyMap.get(localServiceKey) : null;
  }
  
  public synchronized Set<Service> getServices()
  {
    checkInitialized();
    if ((legacyChanged) || (servicesChanged)) {
      serviceSet = null;
    }
    if (serviceSet == null)
    {
      ensureLegacyParsed();
      LinkedHashSet localLinkedHashSet = new LinkedHashSet();
      if (serviceMap != null) {
        localLinkedHashSet.addAll(serviceMap.values());
      }
      if (legacyMap != null) {
        localLinkedHashSet.addAll(legacyMap.values());
      }
      serviceSet = Collections.unmodifiableSet(localLinkedHashSet);
      servicesChanged = false;
    }
    return serviceSet;
  }
  
  protected synchronized void putService(Service paramService)
  {
    check("putProviderProperty." + name);
    if (debug != null) {
      debug.println(name + ".putService(): " + paramService);
    }
    if (paramService == null) {
      throw new NullPointerException();
    }
    if (paramService.getProvider() != this) {
      throw new IllegalArgumentException("service.getProvider() must match this Provider object");
    }
    if (serviceMap == null) {
      serviceMap = new LinkedHashMap();
    }
    servicesChanged = true;
    String str1 = paramService.getType();
    String str2 = paramService.getAlgorithm();
    ServiceKey localServiceKey = new ServiceKey(str1, str2, true, null);
    implRemoveService((Service)serviceMap.get(localServiceKey));
    serviceMap.put(localServiceKey, paramService);
    Iterator localIterator = paramService.getAliases().iterator();
    while (localIterator.hasNext())
    {
      String str3 = (String)localIterator.next();
      serviceMap.put(new ServiceKey(str1, str3, true, null), paramService);
    }
    putPropertyStrings(paramService);
  }
  
  private void putPropertyStrings(Service paramService)
  {
    String str1 = paramService.getType();
    String str2 = paramService.getAlgorithm();
    super.put(str1 + "." + str2, paramService.getClassName());
    Iterator localIterator = paramService.getAliases().iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (String)localIterator.next();
      super.put("Alg.Alias." + str1 + "." + (String)localObject, str2);
    }
    localIterator = attributes.entrySet().iterator();
    while (localIterator.hasNext())
    {
      localObject = (Map.Entry)localIterator.next();
      String str3 = str1 + "." + str2 + " " + ((Map.Entry)localObject).getKey();
      super.put(str3, ((Map.Entry)localObject).getValue());
    }
  }
  
  private void removePropertyStrings(Service paramService)
  {
    String str1 = paramService.getType();
    String str2 = paramService.getAlgorithm();
    super.remove(str1 + "." + str2);
    Iterator localIterator = paramService.getAliases().iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (String)localIterator.next();
      super.remove("Alg.Alias." + str1 + "." + (String)localObject);
    }
    localIterator = attributes.entrySet().iterator();
    while (localIterator.hasNext())
    {
      localObject = (Map.Entry)localIterator.next();
      String str3 = str1 + "." + str2 + " " + ((Map.Entry)localObject).getKey();
      super.remove(str3);
    }
  }
  
  protected synchronized void removeService(Service paramService)
  {
    check("removeProviderProperty." + name);
    if (debug != null) {
      debug.println(name + ".removeService(): " + paramService);
    }
    if (paramService == null) {
      throw new NullPointerException();
    }
    implRemoveService(paramService);
  }
  
  private void implRemoveService(Service paramService)
  {
    if ((paramService == null) || (serviceMap == null)) {
      return;
    }
    String str1 = paramService.getType();
    String str2 = paramService.getAlgorithm();
    ServiceKey localServiceKey = new ServiceKey(str1, str2, false, null);
    Service localService = (Service)serviceMap.get(localServiceKey);
    if (paramService != localService) {
      return;
    }
    servicesChanged = true;
    serviceMap.remove(localServiceKey);
    Iterator localIterator = paramService.getAliases().iterator();
    while (localIterator.hasNext())
    {
      String str3 = (String)localIterator.next();
      serviceMap.remove(new ServiceKey(str1, str3, false, null));
    }
    removePropertyStrings(paramService);
  }
  
  private static void addEngine(String paramString1, boolean paramBoolean, String paramString2)
  {
    EngineDescription localEngineDescription = new EngineDescription(paramString1, paramBoolean, paramString2);
    knownEngines.put(paramString1.toLowerCase(Locale.ENGLISH), localEngineDescription);
    knownEngines.put(paramString1, localEngineDescription);
  }
  
  private static String getEngineName(String paramString)
  {
    EngineDescription localEngineDescription = (EngineDescription)knownEngines.get(paramString);
    if (localEngineDescription == null) {
      localEngineDescription = (EngineDescription)knownEngines.get(paramString.toLowerCase(Locale.ENGLISH));
    }
    return localEngineDescription == null ? paramString : name;
  }
  
  static
  {
    addEngine("AlgorithmParameterGenerator", false, null);
    addEngine("AlgorithmParameters", false, null);
    addEngine("KeyFactory", false, null);
    addEngine("KeyPairGenerator", false, null);
    addEngine("KeyStore", false, null);
    addEngine("MessageDigest", false, null);
    addEngine("SecureRandom", false, null);
    addEngine("Signature", true, null);
    addEngine("CertificateFactory", false, null);
    addEngine("CertPathBuilder", false, null);
    addEngine("CertPathValidator", false, null);
    addEngine("CertStore", false, "java.security.cert.CertStoreParameters");
    addEngine("Cipher", true, null);
    addEngine("ExemptionMechanism", false, null);
    addEngine("Mac", true, null);
    addEngine("KeyAgreement", true, null);
    addEngine("KeyGenerator", false, null);
    addEngine("SecretKeyFactory", false, null);
    addEngine("KeyManagerFactory", false, null);
    addEngine("SSLContext", false, null);
    addEngine("TrustManagerFactory", false, null);
    addEngine("GssApiMechanism", false, null);
    addEngine("SaslClientFactory", false, null);
    addEngine("SaslServerFactory", false, null);
    addEngine("Policy", false, "java.security.Policy$Parameters");
    addEngine("Configuration", false, "javax.security.auth.login.Configuration$Parameters");
    addEngine("XMLSignatureFactory", false, null);
    addEngine("KeyInfoFactory", false, null);
    addEngine("TransformService", false, null);
    addEngine("TerminalFactory", false, "java.lang.Object");
  }
  
  private static class EngineDescription
  {
    final String name;
    final boolean supportsParameter;
    final String constructorParameterClassName;
    private volatile Class<?> constructorParameterClass;
    
    EngineDescription(String paramString1, boolean paramBoolean, String paramString2)
    {
      name = paramString1;
      supportsParameter = paramBoolean;
      constructorParameterClassName = paramString2;
    }
    
    Class<?> getConstructorParameterClass()
      throws ClassNotFoundException
    {
      Class localClass = constructorParameterClass;
      if (localClass == null)
      {
        localClass = Class.forName(constructorParameterClassName);
        constructorParameterClass = localClass;
      }
      return localClass;
    }
  }
  
  public static class Service
  {
    private String type;
    private String algorithm;
    private String className;
    private final Provider provider;
    private List<String> aliases;
    private Map<Provider.UString, String> attributes;
    private volatile Reference<Class<?>> classRef;
    private volatile Boolean hasKeyAttributes;
    private String[] supportedFormats;
    private Class[] supportedClasses;
    private boolean registered;
    private static final Class<?>[] CLASS0 = new Class[0];
    
    private Service(Provider paramProvider)
    {
      provider = paramProvider;
      aliases = Collections.emptyList();
      attributes = Collections.emptyMap();
    }
    
    private boolean isValid()
    {
      return (type != null) && (algorithm != null) && (className != null);
    }
    
    private void addAlias(String paramString)
    {
      if (aliases.isEmpty()) {
        aliases = new ArrayList(2);
      }
      aliases.add(paramString);
    }
    
    void addAttribute(String paramString1, String paramString2)
    {
      if (attributes.isEmpty()) {
        attributes = new HashMap(8);
      }
      attributes.put(new Provider.UString(paramString1), paramString2);
    }
    
    public Service(Provider paramProvider, String paramString1, String paramString2, String paramString3, List<String> paramList, Map<String, String> paramMap)
    {
      if ((paramProvider == null) || (paramString1 == null) || (paramString2 == null) || (paramString3 == null)) {
        throw new NullPointerException();
      }
      provider = paramProvider;
      type = Provider.getEngineName(paramString1);
      algorithm = paramString2;
      className = paramString3;
      if (paramList == null) {
        aliases = Collections.emptyList();
      } else {
        aliases = new ArrayList(paramList);
      }
      if (paramMap == null)
      {
        attributes = Collections.emptyMap();
      }
      else
      {
        attributes = new HashMap();
        Iterator localIterator = paramMap.entrySet().iterator();
        while (localIterator.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator.next();
          attributes.put(new Provider.UString((String)localEntry.getKey()), localEntry.getValue());
        }
      }
    }
    
    public final String getType()
    {
      return type;
    }
    
    public final String getAlgorithm()
    {
      return algorithm;
    }
    
    public final Provider getProvider()
    {
      return provider;
    }
    
    public final String getClassName()
    {
      return className;
    }
    
    private final List<String> getAliases()
    {
      return aliases;
    }
    
    public final String getAttribute(String paramString)
    {
      if (paramString == null) {
        throw new NullPointerException();
      }
      return (String)attributes.get(new Provider.UString(paramString));
    }
    
    public Object newInstance(Object paramObject)
      throws NoSuchAlgorithmException
    {
      if (!registered)
      {
        if (provider.getService(type, algorithm) != this) {
          throw new NoSuchAlgorithmException("Service not registered with Provider " + provider.getName() + ": " + this);
        }
        registered = true;
      }
      try
      {
        Provider.EngineDescription localEngineDescription = (Provider.EngineDescription)Provider.knownEngines.get(type);
        if (localEngineDescription == null) {
          return newInstanceGeneric(paramObject);
        }
        if (constructorParameterClassName == null)
        {
          if (paramObject != null) {
            throw new InvalidParameterException("constructorParameter not used with " + type + " engines");
          }
          localClass = getImplClass();
          localObject = new Class[0];
          localConstructor = localClass.getConstructor((Class[])localObject);
          return localConstructor.newInstance(new Object[0]);
        }
        Class localClass = localEngineDescription.getConstructorParameterClass();
        if (paramObject != null)
        {
          localObject = paramObject.getClass();
          if (!localClass.isAssignableFrom((Class)localObject)) {
            throw new InvalidParameterException("constructorParameter must be instanceof " + constructorParameterClassName.replace('$', '.') + " for engine type " + type);
          }
        }
        Object localObject = getImplClass();
        Constructor localConstructor = ((Class)localObject).getConstructor(new Class[] { localClass });
        return localConstructor.newInstance(new Object[] { paramObject });
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
      {
        throw localNoSuchAlgorithmException;
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        throw new NoSuchAlgorithmException("Error constructing implementation (algorithm: " + algorithm + ", provider: " + provider.getName() + ", class: " + className + ")", localInvocationTargetException.getCause());
      }
      catch (Exception localException)
      {
        throw new NoSuchAlgorithmException("Error constructing implementation (algorithm: " + algorithm + ", provider: " + provider.getName() + ", class: " + className + ")", localException);
      }
    }
    
    private Class<?> getImplClass()
      throws NoSuchAlgorithmException
    {
      try
      {
        Reference localReference = classRef;
        Class localClass = localReference == null ? null : (Class)localReference.get();
        if (localClass == null)
        {
          ClassLoader localClassLoader = provider.getClass().getClassLoader();
          if (localClassLoader == null) {
            localClass = Class.forName(className);
          } else {
            localClass = localClassLoader.loadClass(className);
          }
          if (!Modifier.isPublic(localClass.getModifiers())) {
            throw new NoSuchAlgorithmException("class configured for " + type + " (provider: " + provider.getName() + ") is not public.");
          }
          classRef = new WeakReference(localClass);
        }
        return localClass;
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        throw new NoSuchAlgorithmException("class configured for " + type + " (provider: " + provider.getName() + ") cannot be found.", localClassNotFoundException);
      }
    }
    
    private Object newInstanceGeneric(Object paramObject)
      throws Exception
    {
      Class localClass1 = getImplClass();
      if (paramObject == null) {
        try
        {
          Class[] arrayOfClass1 = new Class[0];
          localObject1 = localClass1.getConstructor(arrayOfClass1);
          return ((Constructor)localObject1).newInstance(new Object[0]);
        }
        catch (NoSuchMethodException localNoSuchMethodException)
        {
          throw new NoSuchAlgorithmException("No public no-arg constructor found in class " + className);
        }
      }
      Class localClass2 = paramObject.getClass();
      Object localObject1 = localClass1.getConstructors();
      for (Object localObject3 : localObject1)
      {
        Class[] arrayOfClass2 = ((Constructor)localObject3).getParameterTypes();
        if ((arrayOfClass2.length == 1) && (arrayOfClass2[0].isAssignableFrom(localClass2))) {
          return ((Constructor)localObject3).newInstance(new Object[] { paramObject });
        }
      }
      throw new NoSuchAlgorithmException("No public constructor matching " + localClass2.getName() + " found in class " + className);
    }
    
    public boolean supportsParameter(Object paramObject)
    {
      Provider.EngineDescription localEngineDescription = (Provider.EngineDescription)Provider.knownEngines.get(type);
      if (localEngineDescription == null) {
        return true;
      }
      if (!supportsParameter) {
        throw new InvalidParameterException("supportsParameter() not used with " + type + " engines");
      }
      if ((paramObject != null) && (!(paramObject instanceof Key))) {
        throw new InvalidParameterException("Parameter must be instanceof Key for engine " + type);
      }
      if (!hasKeyAttributes()) {
        return true;
      }
      if (paramObject == null) {
        return false;
      }
      Key localKey = (Key)paramObject;
      if (supportsKeyFormat(localKey)) {
        return true;
      }
      return supportsKeyClass(localKey);
    }
    
    private boolean hasKeyAttributes()
    {
      Boolean localBoolean = hasKeyAttributes;
      if (localBoolean == null) {
        synchronized (this)
        {
          String str1 = getAttribute("SupportedKeyFormats");
          if (str1 != null) {
            supportedFormats = str1.split("\\|");
          }
          str1 = getAttribute("SupportedKeyClasses");
          if (str1 != null)
          {
            String[] arrayOfString1 = str1.split("\\|");
            ArrayList localArrayList = new ArrayList(arrayOfString1.length);
            for (String str2 : arrayOfString1)
            {
              Class localClass = getKeyClass(str2);
              if (localClass != null) {
                localArrayList.add(localClass);
              }
            }
            supportedClasses = ((Class[])localArrayList.toArray(CLASS0));
          }
          boolean bool = (supportedFormats != null) || (supportedClasses != null);
          localBoolean = Boolean.valueOf(bool);
          hasKeyAttributes = localBoolean;
        }
      }
      return localBoolean.booleanValue();
    }
    
    private Class<?> getKeyClass(String paramString)
    {
      try
      {
        return Class.forName(paramString);
      }
      catch (ClassNotFoundException localClassNotFoundException1)
      {
        try
        {
          ClassLoader localClassLoader = provider.getClass().getClassLoader();
          if (localClassLoader != null) {
            return localClassLoader.loadClass(paramString);
          }
        }
        catch (ClassNotFoundException localClassNotFoundException2) {}
      }
      return null;
    }
    
    private boolean supportsKeyFormat(Key paramKey)
    {
      if (supportedFormats == null) {
        return false;
      }
      String str1 = paramKey.getFormat();
      if (str1 == null) {
        return false;
      }
      for (String str2 : supportedFormats) {
        if (str2.equals(str1)) {
          return true;
        }
      }
      return false;
    }
    
    private boolean supportsKeyClass(Key paramKey)
    {
      if (supportedClasses == null) {
        return false;
      }
      Class localClass1 = paramKey.getClass();
      for (Class localClass2 : supportedClasses) {
        if (localClass2.isAssignableFrom(localClass1)) {
          return true;
        }
      }
      return false;
    }
    
    public String toString()
    {
      String str1 = "\r\n  aliases: " + aliases.toString();
      String str2 = "\r\n  attributes: " + attributes.toString();
      return provider.getName() + ": " + type + "." + algorithm + " -> " + className + str1 + str2 + "\r\n";
    }
  }
  
  private static class ServiceKey
  {
    private final String type;
    private final String algorithm;
    private final String originalAlgorithm;
    
    private ServiceKey(String paramString1, String paramString2, boolean paramBoolean)
    {
      type = paramString1;
      originalAlgorithm = paramString2;
      paramString2 = paramString2.toUpperCase(Locale.ENGLISH);
      algorithm = (paramBoolean ? paramString2.intern() : paramString2);
    }
    
    public int hashCode()
    {
      return type.hashCode() + algorithm.hashCode();
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (!(paramObject instanceof ServiceKey)) {
        return false;
      }
      ServiceKey localServiceKey = (ServiceKey)paramObject;
      return (type.equals(type)) && (algorithm.equals(algorithm));
    }
    
    boolean matches(String paramString1, String paramString2)
    {
      return (type == paramString1) && (originalAlgorithm == paramString2);
    }
  }
  
  private static class UString
  {
    final String string;
    final String lowerString;
    
    UString(String paramString)
    {
      string = paramString;
      lowerString = paramString.toLowerCase(Locale.ENGLISH);
    }
    
    public int hashCode()
    {
      return lowerString.hashCode();
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (!(paramObject instanceof UString)) {
        return false;
      }
      UString localUString = (UString)paramObject;
      return lowerString.equals(lowerString);
    }
    
    public String toString()
    {
      return string;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\Provider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */