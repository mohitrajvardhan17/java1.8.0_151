package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.Util;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.core.Adapter;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.OptimizedAccessorFactory;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext.State;
import java.awt.Image;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.Source;
import org.xml.sax.SAXException;

public abstract class Accessor<BeanT, ValueT>
  implements Receiver
{
  public final Class<ValueT> valueType;
  private static List<Class> nonAbstractableClasses = Arrays.asList(new Class[] { Object.class, Calendar.class, Duration.class, XMLGregorianCalendar.class, Image.class, DataHandler.class, Source.class, Date.class, File.class, URI.class, URL.class, Class.class, String.class, Source.class });
  private static boolean accessWarned = false;
  private static final Accessor ERROR = new Accessor(Object.class)
  {
    public Object get(Object paramAnonymousObject)
    {
      return null;
    }
    
    public void set(Object paramAnonymousObject1, Object paramAnonymousObject2) {}
  };
  public static final Accessor<JAXBElement, Object> JAXB_ELEMENT_VALUE = new Accessor(Object.class)
  {
    public Object get(JAXBElement paramAnonymousJAXBElement)
    {
      return paramAnonymousJAXBElement.getValue();
    }
    
    public void set(JAXBElement paramAnonymousJAXBElement, Object paramAnonymousObject)
    {
      paramAnonymousJAXBElement.setValue(paramAnonymousObject);
    }
  };
  private static final Map<Class, Object> uninitializedValues = new HashMap();
  
  public Class<ValueT> getValueType()
  {
    return valueType;
  }
  
  protected Accessor(Class<ValueT> paramClass)
  {
    valueType = paramClass;
  }
  
  public Accessor<BeanT, ValueT> optimize(@Nullable JAXBContextImpl paramJAXBContextImpl)
  {
    return this;
  }
  
  public abstract ValueT get(BeanT paramBeanT)
    throws AccessorException;
  
  public abstract void set(BeanT paramBeanT, ValueT paramValueT)
    throws AccessorException;
  
  public Object getUnadapted(BeanT paramBeanT)
    throws AccessorException
  {
    return get(paramBeanT);
  }
  
  public boolean isAdapted()
  {
    return false;
  }
  
  public void setUnadapted(BeanT paramBeanT, Object paramObject)
    throws AccessorException
  {
    set(paramBeanT, paramObject);
  }
  
  public void receive(UnmarshallingContext.State paramState, Object paramObject)
    throws SAXException
  {
    try
    {
      set(paramState.getTarget(), paramObject);
    }
    catch (AccessorException localAccessorException)
    {
      Loader.handleGenericException(localAccessorException, true);
    }
    catch (IllegalAccessError localIllegalAccessError)
    {
      Loader.handleGenericError(localIllegalAccessError);
    }
  }
  
  public boolean isValueTypeAbstractable()
  {
    return !nonAbstractableClasses.contains(getValueType());
  }
  
  public boolean isAbstractable(Class paramClass)
  {
    return !nonAbstractableClasses.contains(paramClass);
  }
  
  public final <T> Accessor<BeanT, T> adapt(Class<T> paramClass, Class<? extends XmlAdapter<T, ValueT>> paramClass1)
  {
    return new AdaptedAccessor(paramClass, this, paramClass1);
  }
  
  public final <T> Accessor<BeanT, T> adapt(Adapter<Type, Class> paramAdapter)
  {
    return new AdaptedAccessor((Class)Utils.REFLECTION_NAVIGATOR.erasure(defaultType), this, (Class)adapterType);
  }
  
  public static <A, B> Accessor<A, B> getErrorInstance()
  {
    return ERROR;
  }
  
  static
  {
    uninitializedValues.put(Byte.TYPE, Byte.valueOf((byte)0));
    uninitializedValues.put(Boolean.TYPE, Boolean.valueOf(false));
    uninitializedValues.put(Character.TYPE, Character.valueOf('\000'));
    uninitializedValues.put(Float.TYPE, Float.valueOf(0.0F));
    uninitializedValues.put(Double.TYPE, Double.valueOf(0.0D));
    uninitializedValues.put(Integer.TYPE, Integer.valueOf(0));
    uninitializedValues.put(Long.TYPE, Long.valueOf(0L));
    uninitializedValues.put(Short.TYPE, Short.valueOf((short)0));
  }
  
  public static class FieldReflection<BeanT, ValueT>
    extends Accessor<BeanT, ValueT>
  {
    public final Field f;
    private static final Logger logger = ;
    
    public FieldReflection(Field paramField)
    {
      this(paramField, false);
    }
    
    public FieldReflection(Field paramField, boolean paramBoolean)
    {
      super();
      f = paramField;
      int i = paramField.getModifiers();
      if ((!Modifier.isPublic(i)) || (Modifier.isFinal(i)) || (!Modifier.isPublic(paramField.getDeclaringClass().getModifiers()))) {
        try
        {
          paramField.setAccessible(true);
        }
        catch (SecurityException localSecurityException)
        {
          if ((!Accessor.accessWarned) && (!paramBoolean)) {
            logger.log(Level.WARNING, Messages.UNABLE_TO_ACCESS_NON_PUBLIC_FIELD.format(new Object[] { paramField.getDeclaringClass().getName(), paramField.getName() }), localSecurityException);
          }
          Accessor.access$002(true);
        }
      }
    }
    
    public ValueT get(BeanT paramBeanT)
    {
      try
      {
        return (ValueT)f.get(paramBeanT);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new IllegalAccessError(localIllegalAccessException.getMessage());
      }
    }
    
    public void set(BeanT paramBeanT, ValueT paramValueT)
    {
      try
      {
        if (paramValueT == null) {
          paramValueT = Accessor.uninitializedValues.get(valueType);
        }
        f.set(paramBeanT, paramValueT);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new IllegalAccessError(localIllegalAccessException.getMessage());
      }
    }
    
    public Accessor<BeanT, ValueT> optimize(JAXBContextImpl paramJAXBContextImpl)
    {
      if ((paramJAXBContextImpl != null) && (fastBoot)) {
        return this;
      }
      Accessor localAccessor = OptimizedAccessorFactory.get(f);
      if (localAccessor != null) {
        return localAccessor;
      }
      return this;
    }
  }
  
  public static class GetterOnlyReflection<BeanT, ValueT>
    extends Accessor.GetterSetterReflection<BeanT, ValueT>
  {
    public GetterOnlyReflection(Method paramMethod)
    {
      super(null);
    }
    
    public void set(BeanT paramBeanT, ValueT paramValueT)
      throws AccessorException
    {
      throw new AccessorException(Messages.NO_SETTER.format(new Object[] { getter.toString() }));
    }
  }
  
  public static class GetterSetterReflection<BeanT, ValueT>
    extends Accessor<BeanT, ValueT>
  {
    public final Method getter;
    public final Method setter;
    private static final Logger logger = ;
    
    public GetterSetterReflection(Method paramMethod1, Method paramMethod2)
    {
      super();
      getter = paramMethod1;
      setter = paramMethod2;
      if (paramMethod1 != null) {
        makeAccessible(paramMethod1);
      }
      if (paramMethod2 != null) {
        makeAccessible(paramMethod2);
      }
    }
    
    private void makeAccessible(Method paramMethod)
    {
      if ((!Modifier.isPublic(paramMethod.getModifiers())) || (!Modifier.isPublic(paramMethod.getDeclaringClass().getModifiers()))) {
        try
        {
          paramMethod.setAccessible(true);
        }
        catch (SecurityException localSecurityException)
        {
          if (!Accessor.accessWarned) {
            logger.log(Level.WARNING, Messages.UNABLE_TO_ACCESS_NON_PUBLIC_FIELD.format(new Object[] { paramMethod.getDeclaringClass().getName(), paramMethod.getName() }), localSecurityException);
          }
          Accessor.access$002(true);
        }
      }
    }
    
    public ValueT get(BeanT paramBeanT)
      throws AccessorException
    {
      try
      {
        return (ValueT)getter.invoke(paramBeanT, new Object[0]);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new IllegalAccessError(localIllegalAccessException.getMessage());
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        throw handleInvocationTargetException(localInvocationTargetException);
      }
    }
    
    public void set(BeanT paramBeanT, ValueT paramValueT)
      throws AccessorException
    {
      try
      {
        if (paramValueT == null) {
          paramValueT = Accessor.uninitializedValues.get(valueType);
        }
        setter.invoke(paramBeanT, new Object[] { paramValueT });
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new IllegalAccessError(localIllegalAccessException.getMessage());
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        throw handleInvocationTargetException(localInvocationTargetException);
      }
    }
    
    private AccessorException handleInvocationTargetException(InvocationTargetException paramInvocationTargetException)
    {
      Throwable localThrowable = paramInvocationTargetException.getTargetException();
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof Error)) {
        throw ((Error)localThrowable);
      }
      return new AccessorException(localThrowable);
    }
    
    public Accessor<BeanT, ValueT> optimize(JAXBContextImpl paramJAXBContextImpl)
    {
      if ((getter == null) || (setter == null)) {
        return this;
      }
      if ((paramJAXBContextImpl != null) && (fastBoot)) {
        return this;
      }
      Accessor localAccessor = OptimizedAccessorFactory.get(getter, setter);
      if (localAccessor != null) {
        return localAccessor;
      }
      return this;
    }
  }
  
  public static final class ReadOnlyFieldReflection<BeanT, ValueT>
    extends Accessor.FieldReflection<BeanT, ValueT>
  {
    public ReadOnlyFieldReflection(Field paramField, boolean paramBoolean)
    {
      super(paramBoolean);
    }
    
    public ReadOnlyFieldReflection(Field paramField)
    {
      super();
    }
    
    public void set(BeanT paramBeanT, ValueT paramValueT) {}
    
    public Accessor<BeanT, ValueT> optimize(JAXBContextImpl paramJAXBContextImpl)
    {
      return this;
    }
  }
  
  public static class SetterOnlyReflection<BeanT, ValueT>
    extends Accessor.GetterSetterReflection<BeanT, ValueT>
  {
    public SetterOnlyReflection(Method paramMethod)
    {
      super(paramMethod);
    }
    
    public ValueT get(BeanT paramBeanT)
      throws AccessorException
    {
      throw new AccessorException(Messages.NO_GETTER.format(new Object[] { setter.toString() }));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\Accessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */