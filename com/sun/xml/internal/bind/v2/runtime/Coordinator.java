package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.v2.ClassFactory;
import java.util.HashMap;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.helpers.ValidationEventImpl;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public abstract class Coordinator
  implements ErrorHandler, ValidationEventHandler
{
  private final HashMap<Class<? extends XmlAdapter>, XmlAdapter> adapters = new HashMap();
  private static final ThreadLocal<Coordinator> activeTable = new ThreadLocal();
  private Coordinator old;
  
  public Coordinator() {}
  
  public final XmlAdapter putAdapter(Class<? extends XmlAdapter> paramClass, XmlAdapter paramXmlAdapter)
  {
    if (paramXmlAdapter == null) {
      return (XmlAdapter)adapters.remove(paramClass);
    }
    return (XmlAdapter)adapters.put(paramClass, paramXmlAdapter);
  }
  
  public final <T extends XmlAdapter> T getAdapter(Class<T> paramClass)
  {
    XmlAdapter localXmlAdapter = (XmlAdapter)paramClass.cast(adapters.get(paramClass));
    if (localXmlAdapter == null)
    {
      localXmlAdapter = (XmlAdapter)ClassFactory.create(paramClass);
      putAdapter(paramClass, localXmlAdapter);
    }
    return localXmlAdapter;
  }
  
  public <T extends XmlAdapter> boolean containsAdapter(Class<T> paramClass)
  {
    return adapters.containsKey(paramClass);
  }
  
  protected final void pushCoordinator()
  {
    old = ((Coordinator)activeTable.get());
    activeTable.set(this);
  }
  
  protected final void popCoordinator()
  {
    if (old != null) {
      activeTable.set(old);
    } else {
      activeTable.remove();
    }
    old = null;
  }
  
  public static Coordinator _getInstance()
  {
    return (Coordinator)activeTable.get();
  }
  
  protected abstract ValidationEventLocator getLocation();
  
  public final void error(SAXParseException paramSAXParseException)
    throws SAXException
  {
    propagateEvent(1, paramSAXParseException);
  }
  
  public final void warning(SAXParseException paramSAXParseException)
    throws SAXException
  {
    propagateEvent(0, paramSAXParseException);
  }
  
  public final void fatalError(SAXParseException paramSAXParseException)
    throws SAXException
  {
    propagateEvent(2, paramSAXParseException);
  }
  
  private void propagateEvent(int paramInt, SAXParseException paramSAXParseException)
    throws SAXException
  {
    ValidationEventImpl localValidationEventImpl = new ValidationEventImpl(paramInt, paramSAXParseException.getMessage(), getLocation());
    Exception localException = paramSAXParseException.getException();
    if (localException != null) {
      localValidationEventImpl.setLinkedException(localException);
    } else {
      localValidationEventImpl.setLinkedException(paramSAXParseException);
    }
    boolean bool = handleEvent(localValidationEventImpl);
    if (!bool) {
      throw paramSAXParseException;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\Coordinator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */