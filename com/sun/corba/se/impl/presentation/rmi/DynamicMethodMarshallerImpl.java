package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.presentation.rmi.DynamicMethodMarshaller;
import java.io.Externalizable;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import javax.rmi.CORBA.Util;
import javax.rmi.PortableRemoteObject;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.IDLEntity;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class DynamicMethodMarshallerImpl
  implements DynamicMethodMarshaller
{
  Method method;
  ExceptionHandler ehandler;
  boolean hasArguments = true;
  boolean hasVoidResult = true;
  boolean needsArgumentCopy;
  boolean needsResultCopy;
  ReaderWriter[] argRWs = null;
  ReaderWriter resultRW = null;
  private static ReaderWriter booleanRW = new ReaderWriterBase("boolean")
  {
    public Object read(InputStream paramAnonymousInputStream)
    {
      boolean bool = paramAnonymousInputStream.read_boolean();
      return new Boolean(bool);
    }
    
    public void write(OutputStream paramAnonymousOutputStream, Object paramAnonymousObject)
    {
      Boolean localBoolean = (Boolean)paramAnonymousObject;
      paramAnonymousOutputStream.write_boolean(localBoolean.booleanValue());
    }
  };
  private static ReaderWriter byteRW = new ReaderWriterBase("byte")
  {
    public Object read(InputStream paramAnonymousInputStream)
    {
      byte b = paramAnonymousInputStream.read_octet();
      return new Byte(b);
    }
    
    public void write(OutputStream paramAnonymousOutputStream, Object paramAnonymousObject)
    {
      Byte localByte = (Byte)paramAnonymousObject;
      paramAnonymousOutputStream.write_octet(localByte.byteValue());
    }
  };
  private static ReaderWriter charRW = new ReaderWriterBase("char")
  {
    public Object read(InputStream paramAnonymousInputStream)
    {
      char c = paramAnonymousInputStream.read_wchar();
      return new Character(c);
    }
    
    public void write(OutputStream paramAnonymousOutputStream, Object paramAnonymousObject)
    {
      Character localCharacter = (Character)paramAnonymousObject;
      paramAnonymousOutputStream.write_wchar(localCharacter.charValue());
    }
  };
  private static ReaderWriter shortRW = new ReaderWriterBase("short")
  {
    public Object read(InputStream paramAnonymousInputStream)
    {
      short s = paramAnonymousInputStream.read_short();
      return new Short(s);
    }
    
    public void write(OutputStream paramAnonymousOutputStream, Object paramAnonymousObject)
    {
      Short localShort = (Short)paramAnonymousObject;
      paramAnonymousOutputStream.write_short(localShort.shortValue());
    }
  };
  private static ReaderWriter intRW = new ReaderWriterBase("int")
  {
    public Object read(InputStream paramAnonymousInputStream)
    {
      int i = paramAnonymousInputStream.read_long();
      return new Integer(i);
    }
    
    public void write(OutputStream paramAnonymousOutputStream, Object paramAnonymousObject)
    {
      Integer localInteger = (Integer)paramAnonymousObject;
      paramAnonymousOutputStream.write_long(localInteger.intValue());
    }
  };
  private static ReaderWriter longRW = new ReaderWriterBase("long")
  {
    public Object read(InputStream paramAnonymousInputStream)
    {
      long l = paramAnonymousInputStream.read_longlong();
      return new Long(l);
    }
    
    public void write(OutputStream paramAnonymousOutputStream, Object paramAnonymousObject)
    {
      Long localLong = (Long)paramAnonymousObject;
      paramAnonymousOutputStream.write_longlong(localLong.longValue());
    }
  };
  private static ReaderWriter floatRW = new ReaderWriterBase("float")
  {
    public Object read(InputStream paramAnonymousInputStream)
    {
      float f = paramAnonymousInputStream.read_float();
      return new Float(f);
    }
    
    public void write(OutputStream paramAnonymousOutputStream, Object paramAnonymousObject)
    {
      Float localFloat = (Float)paramAnonymousObject;
      paramAnonymousOutputStream.write_float(localFloat.floatValue());
    }
  };
  private static ReaderWriter doubleRW = new ReaderWriterBase("double")
  {
    public Object read(InputStream paramAnonymousInputStream)
    {
      double d = paramAnonymousInputStream.read_double();
      return new Double(d);
    }
    
    public void write(OutputStream paramAnonymousOutputStream, Object paramAnonymousObject)
    {
      Double localDouble = (Double)paramAnonymousObject;
      paramAnonymousOutputStream.write_double(localDouble.doubleValue());
    }
  };
  private static ReaderWriter corbaObjectRW = new ReaderWriterBase("org.omg.CORBA.Object")
  {
    public Object read(InputStream paramAnonymousInputStream)
    {
      return paramAnonymousInputStream.read_Object();
    }
    
    public void write(OutputStream paramAnonymousOutputStream, Object paramAnonymousObject)
    {
      paramAnonymousOutputStream.write_Object((org.omg.CORBA.Object)paramAnonymousObject);
    }
  };
  private static ReaderWriter anyRW = new ReaderWriterBase("any")
  {
    public Object read(InputStream paramAnonymousInputStream)
    {
      return Util.readAny(paramAnonymousInputStream);
    }
    
    public void write(OutputStream paramAnonymousOutputStream, Object paramAnonymousObject)
    {
      Util.writeAny(paramAnonymousOutputStream, paramAnonymousObject);
    }
  };
  private static ReaderWriter abstractInterfaceRW = new ReaderWriterBase("abstract_interface")
  {
    public Object read(InputStream paramAnonymousInputStream)
    {
      return paramAnonymousInputStream.read_abstract_interface();
    }
    
    public void write(OutputStream paramAnonymousOutputStream, Object paramAnonymousObject)
    {
      Util.writeAbstractObject(paramAnonymousOutputStream, paramAnonymousObject);
    }
  };
  
  private static boolean isAnyClass(Class paramClass)
  {
    return (paramClass.equals(Object.class)) || (paramClass.equals(Serializable.class)) || (paramClass.equals(Externalizable.class));
  }
  
  private static boolean isAbstractInterface(Class paramClass)
  {
    if (IDLEntity.class.isAssignableFrom(paramClass)) {
      return paramClass.isInterface();
    }
    return (paramClass.isInterface()) && (allMethodsThrowRemoteException(paramClass));
  }
  
  private static boolean allMethodsThrowRemoteException(Class paramClass)
  {
    Method[] arrayOfMethod = paramClass.getMethods();
    for (int i = 0; i < arrayOfMethod.length; i++)
    {
      Method localMethod = arrayOfMethod[i];
      if ((localMethod.getDeclaringClass() != Object.class) && (!throwsRemote(localMethod))) {
        return false;
      }
    }
    return true;
  }
  
  private static boolean throwsRemote(Method paramMethod)
  {
    Class[] arrayOfClass = paramMethod.getExceptionTypes();
    for (int i = 0; i < arrayOfClass.length; i++)
    {
      Class localClass = arrayOfClass[i];
      if (RemoteException.class.isAssignableFrom(localClass)) {
        return true;
      }
    }
    return false;
  }
  
  public static ReaderWriter makeReaderWriter(final Class paramClass)
  {
    if (paramClass.equals(Boolean.TYPE)) {
      return booleanRW;
    }
    if (paramClass.equals(Byte.TYPE)) {
      return byteRW;
    }
    if (paramClass.equals(Character.TYPE)) {
      return charRW;
    }
    if (paramClass.equals(Short.TYPE)) {
      return shortRW;
    }
    if (paramClass.equals(Integer.TYPE)) {
      return intRW;
    }
    if (paramClass.equals(Long.TYPE)) {
      return longRW;
    }
    if (paramClass.equals(Float.TYPE)) {
      return floatRW;
    }
    if (paramClass.equals(Double.TYPE)) {
      return doubleRW;
    }
    if (Remote.class.isAssignableFrom(paramClass)) {
      new ReaderWriterBase("remote(" + paramClass.getName() + ")")
      {
        public Object read(InputStream paramAnonymousInputStream)
        {
          return PortableRemoteObject.narrow(paramAnonymousInputStream.read_Object(), paramClass);
        }
        
        public void write(OutputStream paramAnonymousOutputStream, Object paramAnonymousObject)
        {
          Util.writeRemoteObject(paramAnonymousOutputStream, paramAnonymousObject);
        }
      };
    }
    if (paramClass.equals(org.omg.CORBA.Object.class)) {
      return corbaObjectRW;
    }
    if (org.omg.CORBA.Object.class.isAssignableFrom(paramClass)) {
      new ReaderWriterBase("org.omg.CORBA.Object(" + paramClass.getName() + ")")
      {
        public Object read(InputStream paramAnonymousInputStream)
        {
          return paramAnonymousInputStream.read_Object(paramClass);
        }
        
        public void write(OutputStream paramAnonymousOutputStream, Object paramAnonymousObject)
        {
          paramAnonymousOutputStream.write_Object((org.omg.CORBA.Object)paramAnonymousObject);
        }
      };
    }
    if (isAnyClass(paramClass)) {
      return anyRW;
    }
    if (isAbstractInterface(paramClass)) {
      return abstractInterfaceRW;
    }
    new ReaderWriterBase("value(" + paramClass.getName() + ")")
    {
      public Object read(InputStream paramAnonymousInputStream)
      {
        return paramAnonymousInputStream.read_value(paramClass);
      }
      
      public void write(OutputStream paramAnonymousOutputStream, Object paramAnonymousObject)
      {
        paramAnonymousOutputStream.write_value((Serializable)paramAnonymousObject, paramClass);
      }
    };
  }
  
  public DynamicMethodMarshallerImpl(Method paramMethod)
  {
    method = paramMethod;
    ehandler = new ExceptionHandlerImpl(paramMethod.getExceptionTypes());
    needsArgumentCopy = false;
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    hasArguments = (arrayOfClass.length > 0);
    if (hasArguments)
    {
      argRWs = new ReaderWriter[arrayOfClass.length];
      for (int i = 0; i < arrayOfClass.length; i++)
      {
        if (!arrayOfClass[i].isPrimitive()) {
          needsArgumentCopy = true;
        }
        argRWs[i] = makeReaderWriter(arrayOfClass[i]);
      }
    }
    Class localClass = paramMethod.getReturnType();
    needsResultCopy = false;
    hasVoidResult = localClass.equals(Void.TYPE);
    if (!hasVoidResult)
    {
      needsResultCopy = (!localClass.isPrimitive());
      resultRW = makeReaderWriter(localClass);
    }
  }
  
  public Method getMethod()
  {
    return method;
  }
  
  public Object[] copyArguments(Object[] paramArrayOfObject, ORB paramORB)
    throws RemoteException
  {
    if (needsArgumentCopy) {
      return Util.copyObjects(paramArrayOfObject, paramORB);
    }
    return paramArrayOfObject;
  }
  
  public Object[] readArguments(InputStream paramInputStream)
  {
    Object[] arrayOfObject = null;
    if (hasArguments)
    {
      arrayOfObject = new Object[argRWs.length];
      for (int i = 0; i < argRWs.length; i++) {
        arrayOfObject[i] = argRWs[i].read(paramInputStream);
      }
    }
    return arrayOfObject;
  }
  
  public void writeArguments(OutputStream paramOutputStream, Object[] paramArrayOfObject)
  {
    if (hasArguments)
    {
      if (paramArrayOfObject.length != argRWs.length) {
        throw new IllegalArgumentException("Expected " + argRWs.length + " arguments, but got " + paramArrayOfObject.length + " arguments.");
      }
      for (int i = 0; i < argRWs.length; i++) {
        argRWs[i].write(paramOutputStream, paramArrayOfObject[i]);
      }
    }
  }
  
  public Object copyResult(Object paramObject, ORB paramORB)
    throws RemoteException
  {
    if (needsResultCopy) {
      return Util.copyObject(paramObject, paramORB);
    }
    return paramObject;
  }
  
  public Object readResult(InputStream paramInputStream)
  {
    if (hasVoidResult) {
      return null;
    }
    return resultRW.read(paramInputStream);
  }
  
  public void writeResult(OutputStream paramOutputStream, Object paramObject)
  {
    if (!hasVoidResult) {
      resultRW.write(paramOutputStream, paramObject);
    }
  }
  
  public boolean isDeclaredException(Throwable paramThrowable)
  {
    return ehandler.isDeclaredException(paramThrowable.getClass());
  }
  
  public void writeException(OutputStream paramOutputStream, Exception paramException)
  {
    ehandler.writeException(paramOutputStream, paramException);
  }
  
  public Exception readException(ApplicationException paramApplicationException)
  {
    return ehandler.readException(paramApplicationException);
  }
  
  public static abstract interface ReaderWriter
  {
    public abstract Object read(InputStream paramInputStream);
    
    public abstract void write(OutputStream paramOutputStream, Object paramObject);
  }
  
  static abstract class ReaderWriterBase
    implements DynamicMethodMarshallerImpl.ReaderWriter
  {
    String name;
    
    public ReaderWriterBase(String paramString)
    {
      name = paramString;
    }
    
    public String toString()
    {
      return "ReaderWriter[" + name + "]";
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\presentation\rmi\DynamicMethodMarshallerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */