package com.sun.xml.internal.ws.spi.db;

import com.sun.xml.internal.ws.db.glassfish.JAXBRIContextFactory;
import com.sun.xml.internal.ws.util.ServiceConfigurationError;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

public abstract class BindingContextFactory
{
  public static final String DefaultDatabindingMode = "glassfish.jaxb";
  public static final String JAXB_CONTEXT_FACTORY_PROPERTY = BindingContextFactory.class.getName();
  public static final Logger LOGGER = Logger.getLogger(BindingContextFactory.class.getName());
  
  public BindingContextFactory() {}
  
  public static Iterator<BindingContextFactory> serviceIterator()
  {
    ServiceFinder localServiceFinder = ServiceFinder.find(BindingContextFactory.class);
    Iterator localIterator = localServiceFinder.iterator();
    new Iterator()
    {
      private BindingContextFactory bcf;
      
      public boolean hasNext()
      {
        for (;;)
        {
          try
          {
            if (val$ibcf.hasNext())
            {
              bcf = ((BindingContextFactory)val$ibcf.next());
              return true;
            }
            return false;
          }
          catch (ServiceConfigurationError localServiceConfigurationError)
          {
            BindingContextFactory.LOGGER.warning("skipping factory: ServiceConfigurationError: " + localServiceConfigurationError.getMessage());
          }
          catch (NoClassDefFoundError localNoClassDefFoundError)
          {
            BindingContextFactory.LOGGER.fine("skipping factory: NoClassDefFoundError: " + localNoClassDefFoundError.getMessage());
          }
        }
      }
      
      public BindingContextFactory next()
      {
        if (BindingContextFactory.LOGGER.isLoggable(Level.FINER)) {
          BindingContextFactory.LOGGER.finer("SPI found provider: " + bcf.getClass().getName());
        }
        return bcf;
      }
      
      public void remove()
      {
        throw new UnsupportedOperationException();
      }
    };
  }
  
  private static List<BindingContextFactory> factories()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = serviceIterator();
    while (localIterator.hasNext()) {
      localArrayList.add(localIterator.next());
    }
    if (localArrayList.isEmpty())
    {
      if (LOGGER.isLoggable(Level.FINER)) {
        LOGGER.log(Level.FINER, "No SPI providers for BindingContextFactory found, adding: " + JAXBRIContextFactory.class.getName());
      }
      localArrayList.add(new JAXBRIContextFactory());
    }
    return localArrayList;
  }
  
  protected abstract BindingContext newContext(JAXBContext paramJAXBContext);
  
  protected abstract BindingContext newContext(BindingInfo paramBindingInfo);
  
  protected abstract boolean isFor(String paramString);
  
  /**
   * @deprecated
   */
  protected abstract BindingContext getContext(Marshaller paramMarshaller);
  
  private static BindingContextFactory getFactory(String paramString)
  {
    Iterator localIterator = factories().iterator();
    while (localIterator.hasNext())
    {
      BindingContextFactory localBindingContextFactory = (BindingContextFactory)localIterator.next();
      if (localBindingContextFactory.isFor(paramString)) {
        return localBindingContextFactory;
      }
    }
    return null;
  }
  
  public static BindingContext create(JAXBContext paramJAXBContext)
    throws DatabindingException
  {
    return getJAXBFactory(paramJAXBContext).newContext(paramJAXBContext);
  }
  
  public static BindingContext create(BindingInfo paramBindingInfo)
  {
    String str = paramBindingInfo.getDatabindingMode();
    if (str != null)
    {
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, "Using SEI-configured databindng mode: " + str);
      }
    }
    else if ((str = System.getProperty("BindingContextFactory")) != null)
    {
      paramBindingInfo.setDatabindingMode(str);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, "Using databindng: " + str + " based on 'BindingContextFactory' System property");
      }
    }
    else if ((str = System.getProperty(JAXB_CONTEXT_FACTORY_PROPERTY)) != null)
    {
      paramBindingInfo.setDatabindingMode(str);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, "Using databindng: " + str + " based on '" + JAXB_CONTEXT_FACTORY_PROPERTY + "' System property");
      }
    }
    else
    {
      localObject = factories().iterator();
      if (((Iterator)localObject).hasNext())
      {
        BindingContextFactory localBindingContextFactory = (BindingContextFactory)((Iterator)localObject).next();
        if (LOGGER.isLoggable(Level.FINE)) {
          LOGGER.log(Level.FINE, "Using SPI-determined databindng mode: " + localBindingContextFactory.getClass().getName());
        }
        return localBindingContextFactory.newContext(paramBindingInfo);
      }
      LOGGER.log(Level.SEVERE, "No Binding Context Factories found.");
      throw new DatabindingException("No Binding Context Factories found.");
    }
    Object localObject = getFactory(str);
    if (localObject != null) {
      return ((BindingContextFactory)localObject).newContext(paramBindingInfo);
    }
    LOGGER.severe("Unknown Databinding mode: " + str);
    throw new DatabindingException("Unknown Databinding mode: " + str);
  }
  
  public static boolean isContextSupported(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    String str = paramObject.getClass().getPackage().getName();
    Iterator localIterator = factories().iterator();
    while (localIterator.hasNext())
    {
      BindingContextFactory localBindingContextFactory = (BindingContextFactory)localIterator.next();
      if (localBindingContextFactory.isFor(str)) {
        return true;
      }
    }
    return false;
  }
  
  static BindingContextFactory getJAXBFactory(Object paramObject)
  {
    String str = paramObject.getClass().getPackage().getName();
    BindingContextFactory localBindingContextFactory = getFactory(str);
    if (localBindingContextFactory != null) {
      return localBindingContextFactory;
    }
    throw new DatabindingException("Unknown JAXBContext implementation: " + paramObject.getClass());
  }
  
  /**
   * @deprecated
   */
  public static BindingContext getBindingContext(Marshaller paramMarshaller)
  {
    return getJAXBFactory(paramMarshaller).getContext(paramMarshaller);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\spi\db\BindingContextFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */