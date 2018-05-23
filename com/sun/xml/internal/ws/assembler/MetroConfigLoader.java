package com.sun.xml.internal.ws.assembler;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.api.ResourceLoader;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.resources.TubelineassemblyMessages;
import com.sun.xml.internal.ws.runtime.config.MetroConfig;
import com.sun.xml.internal.ws.runtime.config.TubeFactoryList;
import com.sun.xml.internal.ws.runtime.config.TubelineDefinition;
import com.sun.xml.internal.ws.runtime.config.TubelineMapping;
import com.sun.xml.internal.ws.runtime.config.Tubelines;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.lang.reflect.Method;
import java.lang.reflect.ReflectPermission;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.ws.WebServiceException;

class MetroConfigLoader
{
  private static final Logger LOGGER = Logger.getLogger(MetroConfigLoader.class);
  private MetroConfigName defaultTubesConfigNames;
  private static final TubeFactoryListResolver ENDPOINT_SIDE_RESOLVER = new TubeFactoryListResolver()
  {
    public TubeFactoryList getFactories(TubelineDefinition paramAnonymousTubelineDefinition)
    {
      return paramAnonymousTubelineDefinition != null ? paramAnonymousTubelineDefinition.getEndpointSide() : null;
    }
  };
  private static final TubeFactoryListResolver CLIENT_SIDE_RESOLVER = new TubeFactoryListResolver()
  {
    public TubeFactoryList getFactories(TubelineDefinition paramAnonymousTubelineDefinition)
    {
      return paramAnonymousTubelineDefinition != null ? paramAnonymousTubelineDefinition.getClientSide() : null;
    }
  };
  private MetroConfig defaultConfig;
  private URL defaultConfigUrl;
  private MetroConfig appConfig;
  private URL appConfigUrl;
  
  MetroConfigLoader(Container paramContainer, MetroConfigName paramMetroConfigName)
  {
    defaultTubesConfigNames = paramMetroConfigName;
    ResourceLoader localResourceLoader = null;
    if (paramContainer != null) {
      localResourceLoader = (ResourceLoader)paramContainer.getSPI(ResourceLoader.class);
    }
    init(paramContainer, new ResourceLoader[] { localResourceLoader, new MetroConfigUrlLoader(paramContainer) });
  }
  
  private void init(Container paramContainer, ResourceLoader... paramVarArgs)
  {
    String str1 = null;
    String str2 = null;
    if (paramContainer != null)
    {
      MetroConfigName localMetroConfigName = (MetroConfigName)paramContainer.getSPI(MetroConfigName.class);
      if (localMetroConfigName != null)
      {
        str1 = localMetroConfigName.getAppFileName();
        str2 = localMetroConfigName.getDefaultFileName();
      }
    }
    if (str1 == null) {
      str1 = defaultTubesConfigNames.getAppFileName();
    }
    if (str2 == null) {
      str2 = defaultTubesConfigNames.getDefaultFileName();
    }
    defaultConfigUrl = locateResource(str2, paramVarArgs);
    if (defaultConfigUrl == null) {
      throw ((IllegalStateException)LOGGER.logSevereException(new IllegalStateException(TubelineassemblyMessages.MASM_0001_DEFAULT_CFG_FILE_NOT_FOUND(str2))));
    }
    LOGGER.config(TubelineassemblyMessages.MASM_0002_DEFAULT_CFG_FILE_LOCATED(str2, defaultConfigUrl));
    defaultConfig = loadMetroConfig(defaultConfigUrl);
    if (defaultConfig == null) {
      throw ((IllegalStateException)LOGGER.logSevereException(new IllegalStateException(TubelineassemblyMessages.MASM_0003_DEFAULT_CFG_FILE_NOT_LOADED(str2))));
    }
    if (defaultConfig.getTubelines() == null) {
      throw ((IllegalStateException)LOGGER.logSevereException(new IllegalStateException(TubelineassemblyMessages.MASM_0004_NO_TUBELINES_SECTION_IN_DEFAULT_CFG_FILE(str2))));
    }
    if (defaultConfig.getTubelines().getDefault() == null) {
      throw ((IllegalStateException)LOGGER.logSevereException(new IllegalStateException(TubelineassemblyMessages.MASM_0005_NO_DEFAULT_TUBELINE_IN_DEFAULT_CFG_FILE(str2))));
    }
    appConfigUrl = locateResource(str1, paramVarArgs);
    if (appConfigUrl != null)
    {
      LOGGER.config(TubelineassemblyMessages.MASM_0006_APP_CFG_FILE_LOCATED(appConfigUrl));
      appConfig = loadMetroConfig(appConfigUrl);
    }
    else
    {
      LOGGER.config(TubelineassemblyMessages.MASM_0007_APP_CFG_FILE_NOT_FOUND());
      appConfig = null;
    }
  }
  
  TubeFactoryList getEndpointSideTubeFactories(URI paramURI)
  {
    return getTubeFactories(paramURI, ENDPOINT_SIDE_RESOLVER);
  }
  
  TubeFactoryList getClientSideTubeFactories(URI paramURI)
  {
    return getTubeFactories(paramURI, CLIENT_SIDE_RESOLVER);
  }
  
  private TubeFactoryList getTubeFactories(URI paramURI, TubeFactoryListResolver paramTubeFactoryListResolver)
  {
    TubelineMapping localTubelineMapping;
    TubeFactoryList localTubeFactoryList;
    if ((appConfig != null) && (appConfig.getTubelines() != null))
    {
      localObject = appConfig.getTubelines().getTubelineMappings().iterator();
      while (((Iterator)localObject).hasNext())
      {
        localTubelineMapping = (TubelineMapping)((Iterator)localObject).next();
        if (localTubelineMapping.getEndpointRef().equals(paramURI.toString()))
        {
          localTubeFactoryList = paramTubeFactoryListResolver.getFactories(getTubeline(appConfig, resolveReference(localTubelineMapping.getTubelineRef())));
          if (localTubeFactoryList == null) {
            break;
          }
          return localTubeFactoryList;
        }
      }
      if (appConfig.getTubelines().getDefault() != null)
      {
        localObject = paramTubeFactoryListResolver.getFactories(getTubeline(appConfig, resolveReference(appConfig.getTubelines().getDefault())));
        if (localObject != null) {
          return (TubeFactoryList)localObject;
        }
      }
    }
    Object localObject = defaultConfig.getTubelines().getTubelineMappings().iterator();
    while (((Iterator)localObject).hasNext())
    {
      localTubelineMapping = (TubelineMapping)((Iterator)localObject).next();
      if (localTubelineMapping.getEndpointRef().equals(paramURI.toString()))
      {
        localTubeFactoryList = paramTubeFactoryListResolver.getFactories(getTubeline(defaultConfig, resolveReference(localTubelineMapping.getTubelineRef())));
        if (localTubeFactoryList == null) {
          break;
        }
        return localTubeFactoryList;
      }
    }
    return paramTubeFactoryListResolver.getFactories(getTubeline(defaultConfig, resolveReference(defaultConfig.getTubelines().getDefault())));
  }
  
  TubelineDefinition getTubeline(MetroConfig paramMetroConfig, URI paramURI)
  {
    if ((paramMetroConfig != null) && (paramMetroConfig.getTubelines() != null))
    {
      Iterator localIterator = paramMetroConfig.getTubelines().getTubelineDefinitions().iterator();
      while (localIterator.hasNext())
      {
        TubelineDefinition localTubelineDefinition = (TubelineDefinition)localIterator.next();
        if (localTubelineDefinition.getName().equals(paramURI.getFragment())) {
          return localTubelineDefinition;
        }
      }
    }
    return null;
  }
  
  private static URI resolveReference(String paramString)
  {
    try
    {
      return new URI(paramString);
    }
    catch (URISyntaxException localURISyntaxException)
    {
      throw ((WebServiceException)LOGGER.logSevereException(new WebServiceException(TubelineassemblyMessages.MASM_0008_INVALID_URI_REFERENCE(paramString), localURISyntaxException)));
    }
  }
  
  private static URL locateResource(String paramString, ResourceLoader paramResourceLoader)
  {
    if (paramResourceLoader == null) {
      return null;
    }
    try
    {
      return paramResourceLoader.getResource(paramString);
    }
    catch (MalformedURLException localMalformedURLException)
    {
      LOGGER.severe(TubelineassemblyMessages.MASM_0009_CANNOT_FORM_VALID_URL(paramString), localMalformedURLException);
    }
    return null;
  }
  
  private static URL locateResource(String paramString, ResourceLoader[] paramArrayOfResourceLoader)
  {
    for (ResourceLoader localResourceLoader : paramArrayOfResourceLoader)
    {
      URL localURL = locateResource(paramString, localResourceLoader);
      if (localURL != null) {
        return localURL;
      }
    }
    return null;
  }
  
  private static MetroConfig loadMetroConfig(@NotNull URL paramURL)
  {
    MetroConfig localMetroConfig = null;
    try
    {
      JAXBContext localJAXBContext = createJAXBContext();
      Unmarshaller localUnmarshaller = localJAXBContext.createUnmarshaller();
      XMLInputFactory localXMLInputFactory = XmlUtil.newXMLInputFactory(true);
      JAXBElement localJAXBElement = localUnmarshaller.unmarshal(localXMLInputFactory.createXMLStreamReader(paramURL.openStream()), MetroConfig.class);
      localMetroConfig = (MetroConfig)localJAXBElement.getValue();
    }
    catch (Exception localException)
    {
      LOGGER.warning(TubelineassemblyMessages.MASM_0010_ERROR_READING_CFG_FILE_FROM_LOCATION(paramURL.toString()), localException);
    }
    return localMetroConfig;
  }
  
  private static JAXBContext createJAXBContext()
    throws Exception
  {
    if (isJDKInternal()) {
      (JAXBContext)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public JAXBContext run()
          throws Exception
        {
          return JAXBContext.newInstance(MetroConfig.class.getPackage().getName());
        }
      }, createSecurityContext());
    }
    return JAXBContext.newInstance(MetroConfig.class.getPackage().getName());
  }
  
  private static AccessControlContext createSecurityContext()
  {
    Permissions localPermissions = new Permissions();
    localPermissions.add(new RuntimePermission("accessClassInPackage.com.sun.xml.internal.ws.runtime.config"));
    localPermissions.add(new ReflectPermission("suppressAccessChecks"));
    return new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, localPermissions) });
  }
  
  private static boolean isJDKInternal()
  {
    return MetroConfigLoader.class.getName().startsWith("com.sun.xml.internal.ws");
  }
  
  private static class MetroConfigUrlLoader
    extends ResourceLoader
  {
    Container container;
    ResourceLoader parentLoader;
    
    MetroConfigUrlLoader(ResourceLoader paramResourceLoader)
    {
      parentLoader = paramResourceLoader;
    }
    
    MetroConfigUrlLoader(Container paramContainer)
    {
      this(paramContainer != null ? (ResourceLoader)paramContainer.getSPI(ResourceLoader.class) : null);
      container = paramContainer;
    }
    
    public URL getResource(String paramString)
      throws MalformedURLException
    {
      MetroConfigLoader.LOGGER.entering(new Object[] { paramString });
      URL localURL1 = null;
      try
      {
        if (parentLoader != null)
        {
          if (MetroConfigLoader.LOGGER.isLoggable(Level.FINE)) {
            MetroConfigLoader.LOGGER.fine(TubelineassemblyMessages.MASM_0011_LOADING_RESOURCE(paramString, parentLoader));
          }
          localURL1 = parentLoader.getResource(paramString);
        }
        if (localURL1 == null) {
          localURL1 = loadViaClassLoaders("com/sun/xml/internal/ws/assembler/" + paramString);
        }
        if ((localURL1 == null) && (container != null)) {
          localURL1 = loadFromServletContext(paramString);
        }
        URL localURL2 = localURL1;
        return localURL2;
      }
      finally
      {
        MetroConfigLoader.LOGGER.exiting(localURL1);
      }
    }
    
    private static URL loadViaClassLoaders(String paramString)
    {
      URL localURL = tryLoadFromClassLoader(paramString, Thread.currentThread().getContextClassLoader());
      if (localURL == null)
      {
        localURL = tryLoadFromClassLoader(paramString, MetroConfigLoader.class.getClassLoader());
        if (localURL == null) {
          return ClassLoader.getSystemResource(paramString);
        }
      }
      return localURL;
    }
    
    private static URL tryLoadFromClassLoader(String paramString, ClassLoader paramClassLoader)
    {
      return paramClassLoader != null ? paramClassLoader.getResource(paramString) : null;
    }
    
    private URL loadFromServletContext(String paramString)
      throws RuntimeException
    {
      Object localObject1 = null;
      try
      {
        Class localClass = Class.forName("javax.servlet.ServletContext");
        localObject1 = container.getSPI(localClass);
        if (localObject1 != null)
        {
          if (MetroConfigLoader.LOGGER.isLoggable(Level.FINE)) {
            MetroConfigLoader.LOGGER.fine(TubelineassemblyMessages.MASM_0012_LOADING_VIA_SERVLET_CONTEXT(paramString, localObject1));
          }
          try
          {
            Method localMethod = localObject1.getClass().getMethod("getResource", new Class[] { String.class });
            Object localObject2 = localMethod.invoke(localObject1, new Object[] { "/WEB-INF/" + paramString });
            return (URL)URL.class.cast(localObject2);
          }
          catch (Exception localException)
          {
            throw ((RuntimeException)MetroConfigLoader.LOGGER.logSevereException(new RuntimeException(TubelineassemblyMessages.MASM_0013_ERROR_INVOKING_SERVLET_CONTEXT_METHOD("getResource()")), localException));
          }
        }
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        if (MetroConfigLoader.LOGGER.isLoggable(Level.FINE)) {
          MetroConfigLoader.LOGGER.fine(TubelineassemblyMessages.MASM_0014_UNABLE_TO_LOAD_CLASS("javax.servlet.ServletContext"));
        }
      }
      return null;
    }
  }
  
  private static abstract interface TubeFactoryListResolver
  {
    public abstract TubeFactoryList getFactories(TubelineDefinition paramTubelineDefinition);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\assembler\MetroConfigLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */