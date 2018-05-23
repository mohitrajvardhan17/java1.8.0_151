package java.beans;

import com.sun.beans.decoder.DocumentHandler;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

public class XMLDecoder
  implements AutoCloseable
{
  private final AccessControlContext acc = AccessController.getContext();
  private final DocumentHandler handler = new DocumentHandler();
  private final InputSource input;
  private Object owner;
  private Object[] array;
  private int index;
  
  public XMLDecoder(InputStream paramInputStream)
  {
    this(paramInputStream, null);
  }
  
  public XMLDecoder(InputStream paramInputStream, Object paramObject)
  {
    this(paramInputStream, paramObject, null);
  }
  
  public XMLDecoder(InputStream paramInputStream, Object paramObject, ExceptionListener paramExceptionListener)
  {
    this(paramInputStream, paramObject, paramExceptionListener, null);
  }
  
  public XMLDecoder(InputStream paramInputStream, Object paramObject, ExceptionListener paramExceptionListener, ClassLoader paramClassLoader)
  {
    this(new InputSource(paramInputStream), paramObject, paramExceptionListener, paramClassLoader);
  }
  
  public XMLDecoder(InputSource paramInputSource)
  {
    this(paramInputSource, null, null, null);
  }
  
  private XMLDecoder(InputSource paramInputSource, Object paramObject, ExceptionListener paramExceptionListener, ClassLoader paramClassLoader)
  {
    input = paramInputSource;
    owner = paramObject;
    setExceptionListener(paramExceptionListener);
    handler.setClassLoader(paramClassLoader);
    handler.setOwner(this);
  }
  
  public void close()
  {
    if (parsingComplete())
    {
      close(input.getCharacterStream());
      close(input.getByteStream());
    }
  }
  
  private void close(Closeable paramCloseable)
  {
    if (paramCloseable != null) {
      try
      {
        paramCloseable.close();
      }
      catch (IOException localIOException)
      {
        getExceptionListener().exceptionThrown(localIOException);
      }
    }
  }
  
  private boolean parsingComplete()
  {
    if (input == null) {
      return false;
    }
    if (array == null)
    {
      if ((acc == null) && (null != System.getSecurityManager())) {
        throw new SecurityException("AccessControlContext is not set");
      }
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Void run()
        {
          handler.parse(input);
          return null;
        }
      }, acc);
      array = handler.getObjects();
    }
    return true;
  }
  
  public void setExceptionListener(ExceptionListener paramExceptionListener)
  {
    if (paramExceptionListener == null) {
      paramExceptionListener = Statement.defaultExceptionListener;
    }
    handler.setExceptionListener(paramExceptionListener);
  }
  
  public ExceptionListener getExceptionListener()
  {
    return handler.getExceptionListener();
  }
  
  public Object readObject()
  {
    return parsingComplete() ? array[(index++)] : null;
  }
  
  public void setOwner(Object paramObject)
  {
    owner = paramObject;
  }
  
  public Object getOwner()
  {
    return owner;
  }
  
  public static DefaultHandler createHandler(Object paramObject, ExceptionListener paramExceptionListener, ClassLoader paramClassLoader)
  {
    DocumentHandler localDocumentHandler = new DocumentHandler();
    localDocumentHandler.setOwner(paramObject);
    localDocumentHandler.setExceptionListener(paramExceptionListener);
    localDocumentHandler.setClassLoader(paramClassLoader);
    return localDocumentHandler;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\XMLDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */