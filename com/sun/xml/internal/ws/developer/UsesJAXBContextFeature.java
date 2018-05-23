package com.sun.xml.internal.ws.developer;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.org.glassfish.gmbal.ManagedAttribute;
import com.sun.org.glassfish.gmbal.ManagedData;
import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.ws.api.FeatureConstructor;
import com.sun.xml.internal.ws.api.model.SEIModel;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.ws.WebServiceFeature;

@ManagedData
public class UsesJAXBContextFeature
  extends WebServiceFeature
{
  public static final String ID = "http://jax-ws.dev.java.net/features/uses-jaxb-context";
  private final JAXBContextFactory factory;
  
  @FeatureConstructor({"value"})
  public UsesJAXBContextFeature(@NotNull Class<? extends JAXBContextFactory> paramClass)
  {
    try
    {
      factory = ((JAXBContextFactory)paramClass.getConstructor(new Class[0]).newInstance(new Object[0]));
    }
    catch (InstantiationException localInstantiationException)
    {
      localObject = new InstantiationError(localInstantiationException.getMessage());
      ((Error)localObject).initCause(localInstantiationException);
      throw ((Throwable)localObject);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      localObject = new IllegalAccessError(localIllegalAccessException.getMessage());
      ((Error)localObject).initCause(localIllegalAccessException);
      throw ((Throwable)localObject);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      localObject = new InstantiationError(localInvocationTargetException.getMessage());
      ((Error)localObject).initCause(localInvocationTargetException);
      throw ((Throwable)localObject);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      Object localObject = new NoSuchMethodError(localNoSuchMethodException.getMessage());
      ((Error)localObject).initCause(localNoSuchMethodException);
      throw ((Throwable)localObject);
    }
  }
  
  public UsesJAXBContextFeature(@Nullable JAXBContextFactory paramJAXBContextFactory)
  {
    factory = paramJAXBContextFactory;
  }
  
  public UsesJAXBContextFeature(@Nullable final JAXBRIContext paramJAXBRIContext)
  {
    factory = new JAXBContextFactory()
    {
      @NotNull
      public JAXBRIContext createJAXBContext(@NotNull SEIModel paramAnonymousSEIModel, @NotNull List<Class> paramAnonymousList, @NotNull List<TypeReference> paramAnonymousList1)
        throws JAXBException
      {
        return paramJAXBRIContext;
      }
    };
  }
  
  @ManagedAttribute
  @Nullable
  public JAXBContextFactory getFactory()
  {
    return factory;
  }
  
  @ManagedAttribute
  public String getID()
  {
    return "http://jax-ws.dev.java.net/features/uses-jaxb-context";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\developer\UsesJAXBContextFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */