package com.sun.xml.internal.ws.api.streaming;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.resources.StreamingMessages;
import com.sun.xml.internal.ws.streaming.XMLReaderException;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.InputSource;

public abstract class XMLStreamReaderFactory
{
  private static final Logger LOGGER = Logger.getLogger(XMLStreamReaderFactory.class.getName());
  private static final String CLASS_NAME_OF_WSTXINPUTFACTORY = "com.ctc.wstx.stax.WstxInputFactory";
  private static volatile ContextClassloaderLocal<XMLStreamReaderFactory> streamReader = new ContextClassloaderLocal()
  {
    protected XMLStreamReaderFactory initialValue()
    {
      XMLInputFactory localXMLInputFactory = XMLStreamReaderFactory.access$000();
      Object localObject = null;
      if (!XMLStreamReaderFactory.getProperty(XMLStreamReaderFactory.class.getName() + ".noPool").booleanValue()) {
        localObject = XMLStreamReaderFactory.Zephyr.newInstance(localXMLInputFactory);
      }
      if ((localObject == null) && (localXMLInputFactory.getClass().getName().equals("com.ctc.wstx.stax.WstxInputFactory"))) {
        localObject = new XMLStreamReaderFactory.Woodstox(localXMLInputFactory);
      }
      if (localObject == null) {
        localObject = new XMLStreamReaderFactory.Default();
      }
      if (XMLStreamReaderFactory.LOGGER.isLoggable(Level.FINE)) {
        XMLStreamReaderFactory.LOGGER.log(Level.FINE, "XMLStreamReaderFactory instance is = {0}", localObject);
      }
      return (XMLStreamReaderFactory)localObject;
    }
  };
  
  public XMLStreamReaderFactory() {}
  
  private static XMLInputFactory getXMLInputFactory()
  {
    XMLInputFactory localXMLInputFactory = null;
    if (getProperty(XMLStreamReaderFactory.class.getName() + ".woodstox").booleanValue()) {
      try
      {
        localXMLInputFactory = (XMLInputFactory)Class.forName("com.ctc.wstx.stax.WstxInputFactory").newInstance();
      }
      catch (Exception localException)
      {
        if (LOGGER.isLoggable(Level.WARNING)) {
          LOGGER.log(Level.WARNING, StreamingMessages.WOODSTOX_CANT_LOAD("com.ctc.wstx.stax.WstxInputFactory"), localException);
        }
      }
    }
    if (localXMLInputFactory == null) {
      localXMLInputFactory = XmlUtil.newXMLInputFactory(true);
    }
    localXMLInputFactory.setProperty("javax.xml.stream.isNamespaceAware", Boolean.valueOf(true));
    localXMLInputFactory.setProperty("javax.xml.stream.supportDTD", Boolean.valueOf(false));
    localXMLInputFactory.setProperty("javax.xml.stream.isCoalescing", Boolean.valueOf(true));
    return localXMLInputFactory;
  }
  
  public static void set(XMLStreamReaderFactory paramXMLStreamReaderFactory)
  {
    if (paramXMLStreamReaderFactory == null) {
      throw new IllegalArgumentException();
    }
    streamReader.set(paramXMLStreamReaderFactory);
  }
  
  public static XMLStreamReaderFactory get()
  {
    return (XMLStreamReaderFactory)streamReader.get();
  }
  
  public static XMLStreamReader create(InputSource paramInputSource, boolean paramBoolean)
  {
    try
    {
      if (paramInputSource.getCharacterStream() != null) {
        return get().doCreate(paramInputSource.getSystemId(), paramInputSource.getCharacterStream(), paramBoolean);
      }
      if (paramInputSource.getByteStream() != null) {
        return get().doCreate(paramInputSource.getSystemId(), paramInputSource.getByteStream(), paramBoolean);
      }
      return get().doCreate(paramInputSource.getSystemId(), new URL(paramInputSource.getSystemId()).openStream(), paramBoolean);
    }
    catch (IOException localIOException)
    {
      throw new XMLReaderException("stax.cantCreate", new Object[] { localIOException });
    }
  }
  
  public static XMLStreamReader create(@Nullable String paramString, InputStream paramInputStream, boolean paramBoolean)
  {
    return get().doCreate(paramString, paramInputStream, paramBoolean);
  }
  
  public static XMLStreamReader create(@Nullable String paramString1, InputStream paramInputStream, @Nullable String paramString2, boolean paramBoolean)
  {
    return paramString2 == null ? create(paramString1, paramInputStream, paramBoolean) : get().doCreate(paramString1, paramInputStream, paramString2, paramBoolean);
  }
  
  public static XMLStreamReader create(@Nullable String paramString, Reader paramReader, boolean paramBoolean)
  {
    return get().doCreate(paramString, paramReader, paramBoolean);
  }
  
  public static void recycle(XMLStreamReader paramXMLStreamReader)
  {
    get().doRecycle(paramXMLStreamReader);
    if ((paramXMLStreamReader instanceof RecycleAware)) {
      ((RecycleAware)paramXMLStreamReader).onRecycled();
    }
  }
  
  public abstract XMLStreamReader doCreate(String paramString, InputStream paramInputStream, boolean paramBoolean);
  
  private XMLStreamReader doCreate(String paramString1, InputStream paramInputStream, @NotNull String paramString2, boolean paramBoolean)
  {
    InputStreamReader localInputStreamReader;
    try
    {
      localInputStreamReader = new InputStreamReader(paramInputStream, paramString2);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new XMLReaderException("stax.cantCreate", new Object[] { localUnsupportedEncodingException });
    }
    return doCreate(paramString1, localInputStreamReader, paramBoolean);
  }
  
  public abstract XMLStreamReader doCreate(String paramString, Reader paramReader, boolean paramBoolean);
  
  public abstract void doRecycle(XMLStreamReader paramXMLStreamReader);
  
  private static int buildIntegerValue(String paramString, int paramInt)
  {
    String str = System.getProperty(paramString);
    if ((str != null) && (str.length() > 0)) {
      try
      {
        Integer localInteger = Integer.valueOf(Integer.parseInt(str));
        if (localInteger.intValue() > 0) {
          return localInteger.intValue();
        }
      }
      catch (NumberFormatException localNumberFormatException)
      {
        if (LOGGER.isLoggable(Level.WARNING)) {
          LOGGER.log(Level.WARNING, StreamingMessages.INVALID_PROPERTY_VALUE_INTEGER(paramString, str, Integer.toString(paramInt)), localNumberFormatException);
        }
      }
    }
    return paramInt;
  }
  
  private static long buildLongValue(String paramString, long paramLong)
  {
    String str = System.getProperty(paramString);
    if ((str != null) && (str.length() > 0)) {
      try
      {
        long l = Long.parseLong(str);
        if (l > 0L) {
          return l;
        }
      }
      catch (NumberFormatException localNumberFormatException)
      {
        if (LOGGER.isLoggable(Level.WARNING)) {
          LOGGER.log(Level.WARNING, StreamingMessages.INVALID_PROPERTY_VALUE_LONG(paramString, str, Long.toString(paramLong)), localNumberFormatException);
        }
      }
    }
    return paramLong;
  }
  
  private static Boolean getProperty(String paramString)
  {
    (Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Boolean run()
      {
        String str = System.getProperty(val$prop);
        return str != null ? Boolean.valueOf(str) : Boolean.FALSE;
      }
    });
  }
  
  public static final class Default
    extends XMLStreamReaderFactory
  {
    private final ThreadLocal<XMLInputFactory> xif = new ThreadLocal()
    {
      public XMLInputFactory initialValue()
      {
        return XMLStreamReaderFactory.access$000();
      }
    };
    
    public Default() {}
    
    public XMLStreamReader doCreate(String paramString, InputStream paramInputStream, boolean paramBoolean)
    {
      try
      {
        return ((XMLInputFactory)xif.get()).createXMLStreamReader(paramString, paramInputStream);
      }
      catch (XMLStreamException localXMLStreamException)
      {
        throw new XMLReaderException("stax.cantCreate", new Object[] { localXMLStreamException });
      }
    }
    
    public XMLStreamReader doCreate(String paramString, Reader paramReader, boolean paramBoolean)
    {
      try
      {
        return ((XMLInputFactory)xif.get()).createXMLStreamReader(paramString, paramReader);
      }
      catch (XMLStreamException localXMLStreamException)
      {
        throw new XMLReaderException("stax.cantCreate", new Object[] { localXMLStreamException });
      }
    }
    
    public void doRecycle(XMLStreamReader paramXMLStreamReader) {}
  }
  
  public static class NoLock
    extends XMLStreamReaderFactory
  {
    private final XMLInputFactory xif;
    
    public NoLock(XMLInputFactory paramXMLInputFactory)
    {
      xif = paramXMLInputFactory;
    }
    
    public XMLStreamReader doCreate(String paramString, InputStream paramInputStream, boolean paramBoolean)
    {
      try
      {
        return xif.createXMLStreamReader(paramString, paramInputStream);
      }
      catch (XMLStreamException localXMLStreamException)
      {
        throw new XMLReaderException("stax.cantCreate", new Object[] { localXMLStreamException });
      }
    }
    
    public XMLStreamReader doCreate(String paramString, Reader paramReader, boolean paramBoolean)
    {
      try
      {
        return xif.createXMLStreamReader(paramString, paramReader);
      }
      catch (XMLStreamException localXMLStreamException)
      {
        throw new XMLReaderException("stax.cantCreate", new Object[] { localXMLStreamException });
      }
    }
    
    public void doRecycle(XMLStreamReader paramXMLStreamReader) {}
  }
  
  public static abstract interface RecycleAware
  {
    public abstract void onRecycled();
  }
  
  public static final class Woodstox
    extends XMLStreamReaderFactory.NoLock
  {
    public static final String PROPERTY_MAX_ATTRIBUTES_PER_ELEMENT = "xml.ws.maximum.AttributesPerElement";
    public static final String PROPERTY_MAX_ATTRIBUTE_SIZE = "xml.ws.maximum.AttributeSize";
    public static final String PROPERTY_MAX_CHILDREN_PER_ELEMENT = "xml.ws.maximum.ChildrenPerElement";
    public static final String PROPERTY_MAX_ELEMENT_COUNT = "xml.ws.maximum.ElementCount";
    public static final String PROPERTY_MAX_ELEMENT_DEPTH = "xml.ws.maximum.ElementDepth";
    public static final String PROPERTY_MAX_CHARACTERS = "xml.ws.maximum.Characters";
    private static final int DEFAULT_MAX_ATTRIBUTES_PER_ELEMENT = 500;
    private static final int DEFAULT_MAX_ATTRIBUTE_SIZE = 524288;
    private static final int DEFAULT_MAX_CHILDREN_PER_ELEMENT = Integer.MAX_VALUE;
    private static final int DEFAULT_MAX_ELEMENT_DEPTH = 500;
    private static final long DEFAULT_MAX_ELEMENT_COUNT = 2147483647L;
    private static final long DEFAULT_MAX_CHARACTERS = Long.MAX_VALUE;
    private int maxAttributesPerElement = 500;
    private int maxAttributeSize = 524288;
    private int maxChildrenPerElement = Integer.MAX_VALUE;
    private int maxElementDepth = 500;
    private long maxElementCount = 2147483647L;
    private long maxCharacters = Long.MAX_VALUE;
    private static final String P_MAX_ATTRIBUTES_PER_ELEMENT = "com.ctc.wstx.maxAttributesPerElement";
    private static final String P_MAX_ATTRIBUTE_SIZE = "com.ctc.wstx.maxAttributeSize";
    private static final String P_MAX_CHILDREN_PER_ELEMENT = "com.ctc.wstx.maxChildrenPerElement";
    private static final String P_MAX_ELEMENT_COUNT = "com.ctc.wstx.maxElementCount";
    private static final String P_MAX_ELEMENT_DEPTH = "com.ctc.wstx.maxElementDepth";
    private static final String P_MAX_CHARACTERS = "com.ctc.wstx.maxCharacters";
    private static final String P_INTERN_NSURIS = "org.codehaus.stax2.internNsUris";
    
    public Woodstox(XMLInputFactory paramXMLInputFactory)
    {
      super();
      if (paramXMLInputFactory.isPropertySupported("org.codehaus.stax2.internNsUris"))
      {
        paramXMLInputFactory.setProperty("org.codehaus.stax2.internNsUris", Boolean.valueOf(true));
        if (XMLStreamReaderFactory.LOGGER.isLoggable(Level.FINE)) {
          XMLStreamReaderFactory.LOGGER.log(Level.FINE, "org.codehaus.stax2.internNsUris is {0}", Boolean.valueOf(true));
        }
      }
      if (paramXMLInputFactory.isPropertySupported("com.ctc.wstx.maxAttributesPerElement"))
      {
        maxAttributesPerElement = Integer.valueOf(XMLStreamReaderFactory.buildIntegerValue("xml.ws.maximum.AttributesPerElement", 500)).intValue();
        paramXMLInputFactory.setProperty("com.ctc.wstx.maxAttributesPerElement", Integer.valueOf(maxAttributesPerElement));
        if (XMLStreamReaderFactory.LOGGER.isLoggable(Level.FINE)) {
          XMLStreamReaderFactory.LOGGER.log(Level.FINE, "com.ctc.wstx.maxAttributesPerElement is {0}", Integer.valueOf(maxAttributesPerElement));
        }
      }
      if (paramXMLInputFactory.isPropertySupported("com.ctc.wstx.maxAttributeSize"))
      {
        maxAttributeSize = Integer.valueOf(XMLStreamReaderFactory.buildIntegerValue("xml.ws.maximum.AttributeSize", 524288)).intValue();
        paramXMLInputFactory.setProperty("com.ctc.wstx.maxAttributeSize", Integer.valueOf(maxAttributeSize));
        if (XMLStreamReaderFactory.LOGGER.isLoggable(Level.FINE)) {
          XMLStreamReaderFactory.LOGGER.log(Level.FINE, "com.ctc.wstx.maxAttributeSize is {0}", Integer.valueOf(maxAttributeSize));
        }
      }
      if (paramXMLInputFactory.isPropertySupported("com.ctc.wstx.maxChildrenPerElement"))
      {
        maxChildrenPerElement = Integer.valueOf(XMLStreamReaderFactory.buildIntegerValue("xml.ws.maximum.ChildrenPerElement", Integer.MAX_VALUE)).intValue();
        paramXMLInputFactory.setProperty("com.ctc.wstx.maxChildrenPerElement", Integer.valueOf(maxChildrenPerElement));
        if (XMLStreamReaderFactory.LOGGER.isLoggable(Level.FINE)) {
          XMLStreamReaderFactory.LOGGER.log(Level.FINE, "com.ctc.wstx.maxChildrenPerElement is {0}", Integer.valueOf(maxChildrenPerElement));
        }
      }
      if (paramXMLInputFactory.isPropertySupported("com.ctc.wstx.maxElementDepth"))
      {
        maxElementDepth = Integer.valueOf(XMLStreamReaderFactory.buildIntegerValue("xml.ws.maximum.ElementDepth", 500)).intValue();
        paramXMLInputFactory.setProperty("com.ctc.wstx.maxElementDepth", Integer.valueOf(maxElementDepth));
        if (XMLStreamReaderFactory.LOGGER.isLoggable(Level.FINE)) {
          XMLStreamReaderFactory.LOGGER.log(Level.FINE, "com.ctc.wstx.maxElementDepth is {0}", Integer.valueOf(maxElementDepth));
        }
      }
      if (paramXMLInputFactory.isPropertySupported("com.ctc.wstx.maxElementCount"))
      {
        maxElementCount = Long.valueOf(XMLStreamReaderFactory.buildLongValue("xml.ws.maximum.ElementCount", 2147483647L)).longValue();
        paramXMLInputFactory.setProperty("com.ctc.wstx.maxElementCount", Long.valueOf(maxElementCount));
        if (XMLStreamReaderFactory.LOGGER.isLoggable(Level.FINE)) {
          XMLStreamReaderFactory.LOGGER.log(Level.FINE, "com.ctc.wstx.maxElementCount is {0}", Long.valueOf(maxElementCount));
        }
      }
      if (paramXMLInputFactory.isPropertySupported("com.ctc.wstx.maxCharacters"))
      {
        maxCharacters = Long.valueOf(XMLStreamReaderFactory.buildLongValue("xml.ws.maximum.Characters", Long.MAX_VALUE)).longValue();
        paramXMLInputFactory.setProperty("com.ctc.wstx.maxCharacters", Long.valueOf(maxCharacters));
        if (XMLStreamReaderFactory.LOGGER.isLoggable(Level.FINE)) {
          XMLStreamReaderFactory.LOGGER.log(Level.FINE, "com.ctc.wstx.maxCharacters is {0}", Long.valueOf(maxCharacters));
        }
      }
    }
    
    public XMLStreamReader doCreate(String paramString, InputStream paramInputStream, boolean paramBoolean)
    {
      return super.doCreate(paramString, paramInputStream, paramBoolean);
    }
    
    public XMLStreamReader doCreate(String paramString, Reader paramReader, boolean paramBoolean)
    {
      return super.doCreate(paramString, paramReader, paramBoolean);
    }
  }
  
  private static final class Zephyr
    extends XMLStreamReaderFactory
  {
    private final XMLInputFactory xif;
    private final ThreadLocal<XMLStreamReader> pool = new ThreadLocal();
    private final Method setInputSourceMethod;
    private final Method resetMethod;
    private final Class zephyrClass;
    
    @Nullable
    public static XMLStreamReaderFactory newInstance(XMLInputFactory paramXMLInputFactory)
    {
      try
      {
        Class localClass = paramXMLInputFactory.createXMLStreamReader(new StringReader("<foo/>")).getClass();
        if (!localClass.getName().startsWith("com.sun.xml.internal.stream.")) {
          return null;
        }
        return new Zephyr(paramXMLInputFactory, localClass);
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        return null;
      }
      catch (XMLStreamException localXMLStreamException) {}
      return null;
    }
    
    public Zephyr(XMLInputFactory paramXMLInputFactory, Class paramClass)
      throws NoSuchMethodException
    {
      zephyrClass = paramClass;
      setInputSourceMethod = paramClass.getMethod("setInputSource", new Class[] { InputSource.class });
      resetMethod = paramClass.getMethod("reset", new Class[0]);
      try
      {
        paramXMLInputFactory.setProperty("reuse-instance", Boolean.valueOf(false));
      }
      catch (IllegalArgumentException localIllegalArgumentException) {}
      xif = paramXMLInputFactory;
    }
    
    @Nullable
    private XMLStreamReader fetch()
    {
      XMLStreamReader localXMLStreamReader = (XMLStreamReader)pool.get();
      if (localXMLStreamReader == null) {
        return null;
      }
      pool.set(null);
      return localXMLStreamReader;
    }
    
    public void doRecycle(XMLStreamReader paramXMLStreamReader)
    {
      if (zephyrClass.isInstance(paramXMLStreamReader)) {
        pool.set(paramXMLStreamReader);
      }
    }
    
    public XMLStreamReader doCreate(String paramString, InputStream paramInputStream, boolean paramBoolean)
    {
      try
      {
        XMLStreamReader localXMLStreamReader = fetch();
        if (localXMLStreamReader == null) {
          return xif.createXMLStreamReader(paramString, paramInputStream);
        }
        InputSource localInputSource = new InputSource(paramString);
        localInputSource.setByteStream(paramInputStream);
        reuse(localXMLStreamReader, localInputSource);
        return localXMLStreamReader;
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new XMLReaderException("stax.cantCreate", new Object[] { localIllegalAccessException });
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        throw new XMLReaderException("stax.cantCreate", new Object[] { localInvocationTargetException });
      }
      catch (XMLStreamException localXMLStreamException)
      {
        throw new XMLReaderException("stax.cantCreate", new Object[] { localXMLStreamException });
      }
    }
    
    public XMLStreamReader doCreate(String paramString, Reader paramReader, boolean paramBoolean)
    {
      try
      {
        XMLStreamReader localXMLStreamReader = fetch();
        if (localXMLStreamReader == null) {
          return xif.createXMLStreamReader(paramString, paramReader);
        }
        localObject = new InputSource(paramString);
        ((InputSource)localObject).setCharacterStream(paramReader);
        reuse(localXMLStreamReader, (InputSource)localObject);
        return localXMLStreamReader;
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new XMLReaderException("stax.cantCreate", new Object[] { localIllegalAccessException });
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        Object localObject = localInvocationTargetException.getCause();
        if (localObject == null) {
          localObject = localInvocationTargetException;
        }
        throw new XMLReaderException("stax.cantCreate", new Object[] { localObject });
      }
      catch (XMLStreamException localXMLStreamException)
      {
        throw new XMLReaderException("stax.cantCreate", new Object[] { localXMLStreamException });
      }
    }
    
    private void reuse(XMLStreamReader paramXMLStreamReader, InputSource paramInputSource)
      throws IllegalAccessException, InvocationTargetException
    {
      resetMethod.invoke(paramXMLStreamReader, new Object[0]);
      setInputSourceMethod.invoke(paramXMLStreamReader, new Object[] { paramInputSource });
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\streaming\XMLStreamReaderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */