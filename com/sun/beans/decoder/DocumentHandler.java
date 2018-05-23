package com.sun.beans.decoder;

import com.sun.beans.finder.ClassFinder;
import java.beans.ExceptionListener;
import java.io.IOException;
import java.io.StringReader;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import sun.misc.JavaSecurityAccess;
import sun.misc.SharedSecrets;

public final class DocumentHandler
  extends DefaultHandler
{
  private final AccessControlContext acc = AccessController.getContext();
  private final Map<String, Class<? extends ElementHandler>> handlers = new HashMap();
  private final Map<String, Object> environment = new HashMap();
  private final List<Object> objects = new ArrayList();
  private Reference<ClassLoader> loader;
  private ExceptionListener listener;
  private Object owner;
  private ElementHandler handler;
  
  public DocumentHandler()
  {
    setElementHandler("java", JavaElementHandler.class);
    setElementHandler("null", NullElementHandler.class);
    setElementHandler("array", ArrayElementHandler.class);
    setElementHandler("class", ClassElementHandler.class);
    setElementHandler("string", StringElementHandler.class);
    setElementHandler("object", ObjectElementHandler.class);
    setElementHandler("void", VoidElementHandler.class);
    setElementHandler("char", CharElementHandler.class);
    setElementHandler("byte", ByteElementHandler.class);
    setElementHandler("short", ShortElementHandler.class);
    setElementHandler("int", IntElementHandler.class);
    setElementHandler("long", LongElementHandler.class);
    setElementHandler("float", FloatElementHandler.class);
    setElementHandler("double", DoubleElementHandler.class);
    setElementHandler("boolean", BooleanElementHandler.class);
    setElementHandler("new", NewElementHandler.class);
    setElementHandler("var", VarElementHandler.class);
    setElementHandler("true", TrueElementHandler.class);
    setElementHandler("false", FalseElementHandler.class);
    setElementHandler("field", FieldElementHandler.class);
    setElementHandler("method", MethodElementHandler.class);
    setElementHandler("property", PropertyElementHandler.class);
  }
  
  public ClassLoader getClassLoader()
  {
    return loader != null ? (ClassLoader)loader.get() : null;
  }
  
  public void setClassLoader(ClassLoader paramClassLoader)
  {
    loader = new WeakReference(paramClassLoader);
  }
  
  public ExceptionListener getExceptionListener()
  {
    return listener;
  }
  
  public void setExceptionListener(ExceptionListener paramExceptionListener)
  {
    listener = paramExceptionListener;
  }
  
  public Object getOwner()
  {
    return owner;
  }
  
  public void setOwner(Object paramObject)
  {
    owner = paramObject;
  }
  
  public Class<? extends ElementHandler> getElementHandler(String paramString)
  {
    Class localClass = (Class)handlers.get(paramString);
    if (localClass == null) {
      throw new IllegalArgumentException("Unsupported element: " + paramString);
    }
    return localClass;
  }
  
  public void setElementHandler(String paramString, Class<? extends ElementHandler> paramClass)
  {
    handlers.put(paramString, paramClass);
  }
  
  public boolean hasVariable(String paramString)
  {
    return environment.containsKey(paramString);
  }
  
  public Object getVariable(String paramString)
  {
    if (!environment.containsKey(paramString)) {
      throw new IllegalArgumentException("Unbound variable: " + paramString);
    }
    return environment.get(paramString);
  }
  
  public void setVariable(String paramString, Object paramObject)
  {
    environment.put(paramString, paramObject);
  }
  
  public Object[] getObjects()
  {
    return objects.toArray();
  }
  
  void addObject(Object paramObject)
  {
    objects.add(paramObject);
  }
  
  public InputSource resolveEntity(String paramString1, String paramString2)
  {
    return new InputSource(new StringReader(""));
  }
  
  public void startDocument()
  {
    objects.clear();
    handler = null;
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    ElementHandler localElementHandler = handler;
    try
    {
      handler = ((ElementHandler)getElementHandler(paramString3).newInstance());
      handler.setOwner(this);
      handler.setParent(localElementHandler);
    }
    catch (Exception localException)
    {
      throw new SAXException(localException);
    }
    for (int i = 0; i < paramAttributes.getLength(); i++) {
      try
      {
        String str1 = paramAttributes.getQName(i);
        String str2 = paramAttributes.getValue(i);
        handler.addAttribute(str1, str2);
      }
      catch (RuntimeException localRuntimeException)
      {
        handleException(localRuntimeException);
      }
    }
    handler.startElement();
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
  {
    try
    {
      handler.endElement();
    }
    catch (RuntimeException localRuntimeException)
    {
      handleException(localRuntimeException);
    }
    finally
    {
      handler = handler.getParent();
    }
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if (handler != null) {
      try
      {
        while (0 < paramInt2--) {
          handler.addCharacter(paramArrayOfChar[(paramInt1++)]);
        }
      }
      catch (RuntimeException localRuntimeException)
      {
        handleException(localRuntimeException);
      }
    }
  }
  
  public void handleException(Exception paramException)
  {
    if (listener == null) {
      throw new IllegalStateException(paramException);
    }
    listener.exceptionThrown(paramException);
  }
  
  public void parse(final InputSource paramInputSource)
  {
    if ((acc == null) && (null != System.getSecurityManager())) {
      throw new SecurityException("AccessControlContext is not set");
    }
    AccessControlContext localAccessControlContext = AccessController.getContext();
    SharedSecrets.getJavaSecurityAccess().doIntersectionPrivilege(new PrivilegedAction()
    {
      public Void run()
      {
        try
        {
          SAXParserFactory.newInstance().newSAXParser().parse(paramInputSource, DocumentHandler.this);
        }
        catch (ParserConfigurationException localParserConfigurationException)
        {
          handleException(localParserConfigurationException);
        }
        catch (SAXException localSAXException)
        {
          Object localObject = localSAXException.getException();
          if (localObject == null) {
            localObject = localSAXException;
          }
          handleException((Exception)localObject);
        }
        catch (IOException localIOException)
        {
          handleException(localIOException);
        }
        return null;
      }
    }, localAccessControlContext, acc);
  }
  
  public Class<?> findClass(String paramString)
  {
    try
    {
      return ClassFinder.resolveClass(paramString, getClassLoader());
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      handleException(localClassNotFoundException);
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\decoder\DocumentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */