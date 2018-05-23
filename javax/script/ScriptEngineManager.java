package javax.script;

import java.io.PrintStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

public class ScriptEngineManager
{
  private static final boolean DEBUG = false;
  private HashSet<ScriptEngineFactory> engineSpis;
  private HashMap<String, ScriptEngineFactory> nameAssociations;
  private HashMap<String, ScriptEngineFactory> extensionAssociations;
  private HashMap<String, ScriptEngineFactory> mimeTypeAssociations;
  private Bindings globalScope;
  
  public ScriptEngineManager()
  {
    ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
    init(localClassLoader);
  }
  
  public ScriptEngineManager(ClassLoader paramClassLoader)
  {
    init(paramClassLoader);
  }
  
  private void init(ClassLoader paramClassLoader)
  {
    globalScope = new SimpleBindings();
    engineSpis = new HashSet();
    nameAssociations = new HashMap();
    extensionAssociations = new HashMap();
    mimeTypeAssociations = new HashMap();
    initEngines(paramClassLoader);
  }
  
  private ServiceLoader<ScriptEngineFactory> getServiceLoader(ClassLoader paramClassLoader)
  {
    if (paramClassLoader != null) {
      return ServiceLoader.load(ScriptEngineFactory.class, paramClassLoader);
    }
    return ServiceLoader.loadInstalled(ScriptEngineFactory.class);
  }
  
  private void initEngines(final ClassLoader paramClassLoader)
  {
    Iterator localIterator = null;
    try
    {
      ServiceLoader localServiceLoader = (ServiceLoader)AccessController.doPrivileged(new PrivilegedAction()
      {
        public ServiceLoader<ScriptEngineFactory> run()
        {
          return ScriptEngineManager.this.getServiceLoader(paramClassLoader);
        }
      });
      localIterator = localServiceLoader.iterator();
    }
    catch (ServiceConfigurationError localServiceConfigurationError1)
    {
      System.err.println("Can't find ScriptEngineFactory providers: " + localServiceConfigurationError1.getMessage());
      return;
    }
    try
    {
      while (localIterator.hasNext()) {
        try
        {
          ScriptEngineFactory localScriptEngineFactory = (ScriptEngineFactory)localIterator.next();
          engineSpis.add(localScriptEngineFactory);
        }
        catch (ServiceConfigurationError localServiceConfigurationError2)
        {
          System.err.println("ScriptEngineManager providers.next(): " + localServiceConfigurationError2.getMessage());
        }
      }
    }
    catch (ServiceConfigurationError localServiceConfigurationError3)
    {
      System.err.println("ScriptEngineManager providers.hasNext(): " + localServiceConfigurationError3.getMessage());
      return;
    }
  }
  
  public void setBindings(Bindings paramBindings)
  {
    if (paramBindings == null) {
      throw new IllegalArgumentException("Global scope cannot be null.");
    }
    globalScope = paramBindings;
  }
  
  public Bindings getBindings()
  {
    return globalScope;
  }
  
  public void put(String paramString, Object paramObject)
  {
    globalScope.put(paramString, paramObject);
  }
  
  public Object get(String paramString)
  {
    return globalScope.get(paramString);
  }
  
  public ScriptEngine getEngineByName(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    Object localObject1;
    if (null != (localObject1 = nameAssociations.get(paramString)))
    {
      localObject2 = (ScriptEngineFactory)localObject1;
      try
      {
        ScriptEngine localScriptEngine1 = ((ScriptEngineFactory)localObject2).getScriptEngine();
        localScriptEngine1.setBindings(getBindings(), 200);
        return localScriptEngine1;
      }
      catch (Exception localException1) {}
    }
    Object localObject2 = engineSpis.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      ScriptEngineFactory localScriptEngineFactory = (ScriptEngineFactory)((Iterator)localObject2).next();
      List localList = null;
      try
      {
        localList = localScriptEngineFactory.getNames();
      }
      catch (Exception localException2) {}
      if (localList != null)
      {
        Iterator localIterator = localList.iterator();
        while (localIterator.hasNext())
        {
          String str = (String)localIterator.next();
          if (paramString.equals(str)) {
            try
            {
              ScriptEngine localScriptEngine2 = localScriptEngineFactory.getScriptEngine();
              localScriptEngine2.setBindings(getBindings(), 200);
              return localScriptEngine2;
            }
            catch (Exception localException3) {}
          }
        }
      }
    }
    return null;
  }
  
  public ScriptEngine getEngineByExtension(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    Object localObject1;
    if (null != (localObject1 = extensionAssociations.get(paramString)))
    {
      localObject2 = (ScriptEngineFactory)localObject1;
      try
      {
        ScriptEngine localScriptEngine1 = ((ScriptEngineFactory)localObject2).getScriptEngine();
        localScriptEngine1.setBindings(getBindings(), 200);
        return localScriptEngine1;
      }
      catch (Exception localException1) {}
    }
    Object localObject2 = engineSpis.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      ScriptEngineFactory localScriptEngineFactory = (ScriptEngineFactory)((Iterator)localObject2).next();
      List localList = null;
      try
      {
        localList = localScriptEngineFactory.getExtensions();
      }
      catch (Exception localException2) {}
      if (localList != null)
      {
        Iterator localIterator = localList.iterator();
        while (localIterator.hasNext())
        {
          String str = (String)localIterator.next();
          if (paramString.equals(str)) {
            try
            {
              ScriptEngine localScriptEngine2 = localScriptEngineFactory.getScriptEngine();
              localScriptEngine2.setBindings(getBindings(), 200);
              return localScriptEngine2;
            }
            catch (Exception localException3) {}
          }
        }
      }
    }
    return null;
  }
  
  public ScriptEngine getEngineByMimeType(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    Object localObject1;
    if (null != (localObject1 = mimeTypeAssociations.get(paramString)))
    {
      localObject2 = (ScriptEngineFactory)localObject1;
      try
      {
        ScriptEngine localScriptEngine1 = ((ScriptEngineFactory)localObject2).getScriptEngine();
        localScriptEngine1.setBindings(getBindings(), 200);
        return localScriptEngine1;
      }
      catch (Exception localException1) {}
    }
    Object localObject2 = engineSpis.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      ScriptEngineFactory localScriptEngineFactory = (ScriptEngineFactory)((Iterator)localObject2).next();
      List localList = null;
      try
      {
        localList = localScriptEngineFactory.getMimeTypes();
      }
      catch (Exception localException2) {}
      if (localList != null)
      {
        Iterator localIterator = localList.iterator();
        while (localIterator.hasNext())
        {
          String str = (String)localIterator.next();
          if (paramString.equals(str)) {
            try
            {
              ScriptEngine localScriptEngine2 = localScriptEngineFactory.getScriptEngine();
              localScriptEngine2.setBindings(getBindings(), 200);
              return localScriptEngine2;
            }
            catch (Exception localException3) {}
          }
        }
      }
    }
    return null;
  }
  
  public List<ScriptEngineFactory> getEngineFactories()
  {
    ArrayList localArrayList = new ArrayList(engineSpis.size());
    Iterator localIterator = engineSpis.iterator();
    while (localIterator.hasNext())
    {
      ScriptEngineFactory localScriptEngineFactory = (ScriptEngineFactory)localIterator.next();
      localArrayList.add(localScriptEngineFactory);
    }
    return Collections.unmodifiableList(localArrayList);
  }
  
  public void registerEngineName(String paramString, ScriptEngineFactory paramScriptEngineFactory)
  {
    if ((paramString == null) || (paramScriptEngineFactory == null)) {
      throw new NullPointerException();
    }
    nameAssociations.put(paramString, paramScriptEngineFactory);
  }
  
  public void registerEngineMimeType(String paramString, ScriptEngineFactory paramScriptEngineFactory)
  {
    if ((paramString == null) || (paramScriptEngineFactory == null)) {
      throw new NullPointerException();
    }
    mimeTypeAssociations.put(paramString, paramScriptEngineFactory);
  }
  
  public void registerEngineExtension(String paramString, ScriptEngineFactory paramScriptEngineFactory)
  {
    if ((paramString == null) || (paramScriptEngineFactory == null)) {
      throw new NullPointerException();
    }
    extensionAssociations.put(paramString, paramScriptEngineFactory);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\script\ScriptEngineManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */