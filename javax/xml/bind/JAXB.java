package javax.xml.bind;

import java.beans.Introspector;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public final class JAXB
{
  private static volatile WeakReference<Cache> cache;
  
  private JAXB() {}
  
  private static <T> JAXBContext getContext(Class<T> paramClass)
    throws JAXBException
  {
    WeakReference localWeakReference = cache;
    if (localWeakReference != null)
    {
      localCache = (Cache)localWeakReference.get();
      if ((localCache != null) && (type == paramClass)) {
        return context;
      }
    }
    Cache localCache = new Cache(paramClass);
    cache = new WeakReference(localCache);
    return context;
  }
  
  public static <T> T unmarshal(File paramFile, Class<T> paramClass)
  {
    try
    {
      JAXBElement localJAXBElement = getContext(paramClass).createUnmarshaller().unmarshal(new StreamSource(paramFile), paramClass);
      return (T)localJAXBElement.getValue();
    }
    catch (JAXBException localJAXBException)
    {
      throw new DataBindingException(localJAXBException);
    }
  }
  
  public static <T> T unmarshal(URL paramURL, Class<T> paramClass)
  {
    try
    {
      JAXBElement localJAXBElement = getContext(paramClass).createUnmarshaller().unmarshal(toSource(paramURL), paramClass);
      return (T)localJAXBElement.getValue();
    }
    catch (JAXBException localJAXBException)
    {
      throw new DataBindingException(localJAXBException);
    }
    catch (IOException localIOException)
    {
      throw new DataBindingException(localIOException);
    }
  }
  
  public static <T> T unmarshal(URI paramURI, Class<T> paramClass)
  {
    try
    {
      JAXBElement localJAXBElement = getContext(paramClass).createUnmarshaller().unmarshal(toSource(paramURI), paramClass);
      return (T)localJAXBElement.getValue();
    }
    catch (JAXBException localJAXBException)
    {
      throw new DataBindingException(localJAXBException);
    }
    catch (IOException localIOException)
    {
      throw new DataBindingException(localIOException);
    }
  }
  
  public static <T> T unmarshal(String paramString, Class<T> paramClass)
  {
    try
    {
      JAXBElement localJAXBElement = getContext(paramClass).createUnmarshaller().unmarshal(toSource(paramString), paramClass);
      return (T)localJAXBElement.getValue();
    }
    catch (JAXBException localJAXBException)
    {
      throw new DataBindingException(localJAXBException);
    }
    catch (IOException localIOException)
    {
      throw new DataBindingException(localIOException);
    }
  }
  
  public static <T> T unmarshal(InputStream paramInputStream, Class<T> paramClass)
  {
    try
    {
      JAXBElement localJAXBElement = getContext(paramClass).createUnmarshaller().unmarshal(toSource(paramInputStream), paramClass);
      return (T)localJAXBElement.getValue();
    }
    catch (JAXBException localJAXBException)
    {
      throw new DataBindingException(localJAXBException);
    }
    catch (IOException localIOException)
    {
      throw new DataBindingException(localIOException);
    }
  }
  
  public static <T> T unmarshal(Reader paramReader, Class<T> paramClass)
  {
    try
    {
      JAXBElement localJAXBElement = getContext(paramClass).createUnmarshaller().unmarshal(toSource(paramReader), paramClass);
      return (T)localJAXBElement.getValue();
    }
    catch (JAXBException localJAXBException)
    {
      throw new DataBindingException(localJAXBException);
    }
    catch (IOException localIOException)
    {
      throw new DataBindingException(localIOException);
    }
  }
  
  public static <T> T unmarshal(Source paramSource, Class<T> paramClass)
  {
    try
    {
      JAXBElement localJAXBElement = getContext(paramClass).createUnmarshaller().unmarshal(toSource(paramSource), paramClass);
      return (T)localJAXBElement.getValue();
    }
    catch (JAXBException localJAXBException)
    {
      throw new DataBindingException(localJAXBException);
    }
    catch (IOException localIOException)
    {
      throw new DataBindingException(localIOException);
    }
  }
  
  private static Source toSource(Object paramObject)
    throws IOException
  {
    if (paramObject == null) {
      throw new IllegalArgumentException("no XML is given");
    }
    if ((paramObject instanceof String)) {
      try
      {
        paramObject = new URI((String)paramObject);
      }
      catch (URISyntaxException localURISyntaxException)
      {
        paramObject = new File((String)paramObject);
      }
    }
    Object localObject;
    if ((paramObject instanceof File))
    {
      localObject = (File)paramObject;
      return new StreamSource((File)localObject);
    }
    if ((paramObject instanceof URI))
    {
      localObject = (URI)paramObject;
      paramObject = ((URI)localObject).toURL();
    }
    if ((paramObject instanceof URL))
    {
      localObject = (URL)paramObject;
      return new StreamSource(((URL)localObject).toExternalForm());
    }
    if ((paramObject instanceof InputStream))
    {
      localObject = (InputStream)paramObject;
      return new StreamSource((InputStream)localObject);
    }
    if ((paramObject instanceof Reader))
    {
      localObject = (Reader)paramObject;
      return new StreamSource((Reader)localObject);
    }
    if ((paramObject instanceof Source)) {
      return (Source)paramObject;
    }
    throw new IllegalArgumentException("I don't understand how to handle " + paramObject.getClass());
  }
  
  public static void marshal(Object paramObject, File paramFile)
  {
    _marshal(paramObject, paramFile);
  }
  
  public static void marshal(Object paramObject, URL paramURL)
  {
    _marshal(paramObject, paramURL);
  }
  
  public static void marshal(Object paramObject, URI paramURI)
  {
    _marshal(paramObject, paramURI);
  }
  
  public static void marshal(Object paramObject, String paramString)
  {
    _marshal(paramObject, paramString);
  }
  
  public static void marshal(Object paramObject, OutputStream paramOutputStream)
  {
    _marshal(paramObject, paramOutputStream);
  }
  
  public static void marshal(Object paramObject, Writer paramWriter)
  {
    _marshal(paramObject, paramWriter);
  }
  
  public static void marshal(Object paramObject, Result paramResult)
  {
    _marshal(paramObject, paramResult);
  }
  
  private static void _marshal(Object paramObject1, Object paramObject2)
  {
    try
    {
      JAXBContext localJAXBContext;
      if ((paramObject1 instanceof JAXBElement))
      {
        localJAXBContext = getContext(((JAXBElement)paramObject1).getDeclaredType());
      }
      else
      {
        localObject = paramObject1.getClass();
        XmlRootElement localXmlRootElement = (XmlRootElement)((Class)localObject).getAnnotation(XmlRootElement.class);
        localJAXBContext = getContext((Class)localObject);
        if (localXmlRootElement == null) {
          paramObject1 = new JAXBElement(new QName(inferName((Class)localObject)), (Class)localObject, paramObject1);
        }
      }
      Object localObject = localJAXBContext.createMarshaller();
      ((Marshaller)localObject).setProperty("jaxb.formatted.output", Boolean.valueOf(true));
      ((Marshaller)localObject).marshal(paramObject1, toResult(paramObject2));
    }
    catch (JAXBException localJAXBException)
    {
      throw new DataBindingException(localJAXBException);
    }
    catch (IOException localIOException)
    {
      throw new DataBindingException(localIOException);
    }
  }
  
  private static String inferName(Class paramClass)
  {
    return Introspector.decapitalize(paramClass.getSimpleName());
  }
  
  private static Result toResult(Object paramObject)
    throws IOException
  {
    if (paramObject == null) {
      throw new IllegalArgumentException("no XML is given");
    }
    if ((paramObject instanceof String)) {
      try
      {
        paramObject = new URI((String)paramObject);
      }
      catch (URISyntaxException localURISyntaxException)
      {
        paramObject = new File((String)paramObject);
      }
    }
    Object localObject;
    if ((paramObject instanceof File))
    {
      localObject = (File)paramObject;
      return new StreamResult((File)localObject);
    }
    if ((paramObject instanceof URI))
    {
      localObject = (URI)paramObject;
      paramObject = ((URI)localObject).toURL();
    }
    if ((paramObject instanceof URL))
    {
      localObject = (URL)paramObject;
      URLConnection localURLConnection = ((URL)localObject).openConnection();
      localURLConnection.setDoOutput(true);
      localURLConnection.setDoInput(false);
      localURLConnection.connect();
      return new StreamResult(localURLConnection.getOutputStream());
    }
    if ((paramObject instanceof OutputStream))
    {
      localObject = (OutputStream)paramObject;
      return new StreamResult((OutputStream)localObject);
    }
    if ((paramObject instanceof Writer))
    {
      localObject = (Writer)paramObject;
      return new StreamResult((Writer)localObject);
    }
    if ((paramObject instanceof Result)) {
      return (Result)paramObject;
    }
    throw new IllegalArgumentException("I don't understand how to handle " + paramObject.getClass());
  }
  
  private static final class Cache
  {
    final Class type;
    final JAXBContext context;
    
    public Cache(Class paramClass)
      throws JAXBException
    {
      type = paramClass;
      context = JAXBContext.newInstance(new Class[] { paramClass });
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\bind\JAXB.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */