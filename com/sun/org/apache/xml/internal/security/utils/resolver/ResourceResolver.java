package com.sun.org.apache.xml.internal.security.utils.resolver;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.implementations.ResolverDirectHTTP;
import com.sun.org.apache.xml.internal.security.utils.resolver.implementations.ResolverFragment;
import com.sun.org.apache.xml.internal.security.utils.resolver.implementations.ResolverLocalFilesystem;
import com.sun.org.apache.xml.internal.security.utils.resolver.implementations.ResolverXPointer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Attr;

public class ResourceResolver
{
  private static Logger log = Logger.getLogger(ResourceResolver.class.getName());
  private static List<ResourceResolver> resolverList = new ArrayList();
  private final ResourceResolverSpi resolverSpi;
  
  public ResourceResolver(ResourceResolverSpi paramResourceResolverSpi)
  {
    resolverSpi = paramResourceResolverSpi;
  }
  
  public static final ResourceResolver getInstance(Attr paramAttr, String paramString)
    throws ResourceResolverException
  {
    return getInstance(paramAttr, paramString, false);
  }
  
  public static final ResourceResolver getInstance(Attr paramAttr, String paramString, boolean paramBoolean)
    throws ResourceResolverException
  {
    ResourceResolverContext localResourceResolverContext = new ResourceResolverContext(paramAttr, paramString, paramBoolean);
    return internalGetInstance(localResourceResolverContext);
  }
  
  private static <N> ResourceResolver internalGetInstance(ResourceResolverContext paramResourceResolverContext)
    throws ResourceResolverException
  {
    synchronized (resolverList)
    {
      Iterator localIterator = resolverList.iterator();
      while (localIterator.hasNext())
      {
        ResourceResolver localResourceResolver1 = (ResourceResolver)localIterator.next();
        ResourceResolver localResourceResolver2 = localResourceResolver1;
        if (!resolverSpi.engineIsThreadSafe()) {
          try
          {
            localResourceResolver2 = new ResourceResolver((ResourceResolverSpi)resolverSpi.getClass().newInstance());
          }
          catch (InstantiationException localInstantiationException)
          {
            throw new ResourceResolverException("", localInstantiationException, attr, baseUri);
          }
          catch (IllegalAccessException localIllegalAccessException)
          {
            throw new ResourceResolverException("", localIllegalAccessException, attr, baseUri);
          }
        }
        if (log.isLoggable(Level.FINE)) {
          log.log(Level.FINE, "check resolvability by class " + localResourceResolver2.getClass().getName());
        }
        if ((localResourceResolver2 != null) && (localResourceResolver2.canResolve(paramResourceResolverContext)))
        {
          if ((secureValidation) && (((resolverSpi instanceof ResolverLocalFilesystem)) || ((resolverSpi instanceof ResolverDirectHTTP))))
          {
            Object[] arrayOfObject = { resolverSpi.getClass().getName() };
            throw new ResourceResolverException("signature.Reference.ForbiddenResolver", arrayOfObject, attr, baseUri);
          }
          return localResourceResolver2;
        }
      }
    }
    ??? = new Object[] { uriToResolve != null ? uriToResolve : "null", baseUri };
    throw new ResourceResolverException("utils.resolver.noClass", (Object[])???, attr, baseUri);
  }
  
  public static ResourceResolver getInstance(Attr paramAttr, String paramString, List<ResourceResolver> paramList)
    throws ResourceResolverException
  {
    return getInstance(paramAttr, paramString, paramList, false);
  }
  
  public static ResourceResolver getInstance(Attr paramAttr, String paramString, List<ResourceResolver> paramList, boolean paramBoolean)
    throws ResourceResolverException
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "I was asked to create a ResourceResolver and got " + (paramList == null ? 0 : paramList.size()));
    }
    ResourceResolverContext localResourceResolverContext = new ResourceResolverContext(paramAttr, paramString, paramBoolean);
    if (paramList != null) {
      for (int i = 0; i < paramList.size(); i++)
      {
        ResourceResolver localResourceResolver = (ResourceResolver)paramList.get(i);
        if (localResourceResolver != null)
        {
          if (log.isLoggable(Level.FINE))
          {
            String str = resolverSpi.getClass().getName();
            log.log(Level.FINE, "check resolvability by class " + str);
          }
          if (localResourceResolver.canResolve(localResourceResolverContext)) {
            return localResourceResolver;
          }
        }
      }
    }
    return internalGetInstance(localResourceResolverContext);
  }
  
  public static void register(String paramString)
  {
    
    try
    {
      Class localClass = Class.forName(paramString);
      register(localClass, false);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      log.log(Level.WARNING, "Error loading resolver " + paramString + " disabling it");
    }
  }
  
  public static void registerAtStart(String paramString)
  {
    
    try
    {
      Class localClass = Class.forName(paramString);
      register(localClass, true);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      log.log(Level.WARNING, "Error loading resolver " + paramString + " disabling it");
    }
  }
  
  public static void register(Class<? extends ResourceResolverSpi> paramClass, boolean paramBoolean)
  {
    
    try
    {
      ResourceResolverSpi localResourceResolverSpi = (ResourceResolverSpi)paramClass.newInstance();
      register(localResourceResolverSpi, paramBoolean);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      log.log(Level.WARNING, "Error loading resolver " + paramClass + " disabling it");
    }
    catch (InstantiationException localInstantiationException)
    {
      log.log(Level.WARNING, "Error loading resolver " + paramClass + " disabling it");
    }
  }
  
  public static void register(ResourceResolverSpi paramResourceResolverSpi, boolean paramBoolean)
  {
    
    synchronized (resolverList)
    {
      if (paramBoolean) {
        resolverList.add(0, new ResourceResolver(paramResourceResolverSpi));
      } else {
        resolverList.add(new ResourceResolver(paramResourceResolverSpi));
      }
    }
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Registered resolver: " + paramResourceResolverSpi.toString());
    }
  }
  
  public static void registerDefaultResolvers()
  {
    synchronized (resolverList)
    {
      resolverList.add(new ResourceResolver(new ResolverFragment()));
      resolverList.add(new ResourceResolver(new ResolverLocalFilesystem()));
      resolverList.add(new ResourceResolver(new ResolverXPointer()));
      resolverList.add(new ResourceResolver(new ResolverDirectHTTP()));
    }
  }
  
  @Deprecated
  public XMLSignatureInput resolve(Attr paramAttr, String paramString)
    throws ResourceResolverException
  {
    return resolve(paramAttr, paramString, true);
  }
  
  public XMLSignatureInput resolve(Attr paramAttr, String paramString, boolean paramBoolean)
    throws ResourceResolverException
  {
    ResourceResolverContext localResourceResolverContext = new ResourceResolverContext(paramAttr, paramString, paramBoolean);
    return resolverSpi.engineResolveURI(localResourceResolverContext);
  }
  
  public void setProperty(String paramString1, String paramString2)
  {
    resolverSpi.engineSetProperty(paramString1, paramString2);
  }
  
  public String getProperty(String paramString)
  {
    return resolverSpi.engineGetProperty(paramString);
  }
  
  public void addProperties(Map<String, String> paramMap)
  {
    resolverSpi.engineAddProperies(paramMap);
  }
  
  public String[] getPropertyKeys()
  {
    return resolverSpi.engineGetPropertyKeys();
  }
  
  public boolean understandsProperty(String paramString)
  {
    return resolverSpi.understandsProperty(paramString);
  }
  
  private boolean canResolve(ResourceResolverContext paramResourceResolverContext)
  {
    return resolverSpi.engineCanResolveURI(paramResourceResolverContext);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\utils\resolver\ResourceResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */