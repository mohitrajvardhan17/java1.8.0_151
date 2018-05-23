package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import org.omg.CORBA.UserException;
import org.omg.CORBA.portable.ApplicationException;

public class ExceptionHandlerImpl
  implements ExceptionHandler
{
  private ExceptionRW[] rws;
  private final ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.presentation");
  
  public ExceptionHandlerImpl(Class[] paramArrayOfClass)
  {
    int i = 0;
    for (int j = 0; j < paramArrayOfClass.length; j++)
    {
      Class localClass1 = paramArrayOfClass[j];
      if (!RemoteException.class.isAssignableFrom(localClass1)) {
        i++;
      }
    }
    rws = new ExceptionRW[i];
    j = 0;
    for (int k = 0; k < paramArrayOfClass.length; k++)
    {
      Class localClass2 = paramArrayOfClass[k];
      if (!RemoteException.class.isAssignableFrom(localClass2))
      {
        Object localObject = null;
        if (UserException.class.isAssignableFrom(localClass2)) {
          localObject = new ExceptionRWIDLImpl(localClass2);
        } else {
          localObject = new ExceptionRWRMIImpl(localClass2);
        }
        rws[(j++)] = localObject;
      }
    }
  }
  
  private int findDeclaredException(Class paramClass)
  {
    for (int i = 0; i < rws.length; i++)
    {
      Class localClass = rws[i].getExceptionClass();
      if (localClass.isAssignableFrom(paramClass)) {
        return i;
      }
    }
    return -1;
  }
  
  private int findDeclaredException(String paramString)
  {
    for (int i = 0; i < rws.length; i++)
    {
      if (rws[i] == null) {
        return -1;
      }
      String str = rws[i].getId();
      if (paramString.equals(str)) {
        return i;
      }
    }
    return -1;
  }
  
  public boolean isDeclaredException(Class paramClass)
  {
    return findDeclaredException(paramClass) >= 0;
  }
  
  public void writeException(org.omg.CORBA_2_3.portable.OutputStream paramOutputStream, Exception paramException)
  {
    int i = findDeclaredException(paramException.getClass());
    if (i < 0) {
      throw wrapper.writeUndeclaredException(paramException, paramException.getClass().getName());
    }
    rws[i].write(paramOutputStream, paramException);
  }
  
  public Exception readException(ApplicationException paramApplicationException)
  {
    org.omg.CORBA_2_3.portable.InputStream localInputStream = (org.omg.CORBA_2_3.portable.InputStream)paramApplicationException.getInputStream();
    String str = paramApplicationException.getId();
    int i = findDeclaredException(str);
    if (i < 0)
    {
      str = localInputStream.read_string();
      UnexpectedException localUnexpectedException = new UnexpectedException(str);
      localUnexpectedException.initCause(paramApplicationException);
      return localUnexpectedException;
    }
    return rws[i].read(localInputStream);
  }
  
  public ExceptionRW getRMIExceptionRW(Class paramClass)
  {
    return new ExceptionRWRMIImpl(paramClass);
  }
  
  public static abstract interface ExceptionRW
  {
    public abstract Class getExceptionClass();
    
    public abstract String getId();
    
    public abstract void write(org.omg.CORBA_2_3.portable.OutputStream paramOutputStream, Exception paramException);
    
    public abstract Exception read(org.omg.CORBA_2_3.portable.InputStream paramInputStream);
  }
  
  public abstract class ExceptionRWBase
    implements ExceptionHandlerImpl.ExceptionRW
  {
    private Class cls;
    private String id;
    
    public ExceptionRWBase(Class paramClass)
    {
      cls = paramClass;
    }
    
    public Class getExceptionClass()
    {
      return cls;
    }
    
    public String getId()
    {
      return id;
    }
    
    void setId(String paramString)
    {
      id = paramString;
    }
  }
  
  public class ExceptionRWIDLImpl
    extends ExceptionHandlerImpl.ExceptionRWBase
  {
    private Method readMethod;
    private Method writeMethod;
    
    public ExceptionRWIDLImpl(Class paramClass)
    {
      super(paramClass);
      String str = paramClass.getName() + "Helper";
      ClassLoader localClassLoader = paramClass.getClassLoader();
      Class localClass;
      try
      {
        localClass = Class.forName(str, true, localClassLoader);
        Method localMethod = localClass.getDeclaredMethod("id", (Class[])null);
        setId((String)localMethod.invoke(null, (Object[])null));
      }
      catch (Exception localException1)
      {
        throw wrapper.badHelperIdMethod(localException1, str);
      }
      try
      {
        Class[] arrayOfClass1 = { org.omg.CORBA.portable.OutputStream.class, paramClass };
        writeMethod = localClass.getDeclaredMethod("write", arrayOfClass1);
      }
      catch (Exception localException2)
      {
        throw wrapper.badHelperWriteMethod(localException2, str);
      }
      try
      {
        Class[] arrayOfClass2 = { org.omg.CORBA.portable.InputStream.class };
        readMethod = localClass.getDeclaredMethod("read", arrayOfClass2);
      }
      catch (Exception localException3)
      {
        throw wrapper.badHelperReadMethod(localException3, str);
      }
    }
    
    public void write(org.omg.CORBA_2_3.portable.OutputStream paramOutputStream, Exception paramException)
    {
      try
      {
        Object[] arrayOfObject = { paramOutputStream, paramException };
        writeMethod.invoke(null, arrayOfObject);
      }
      catch (Exception localException)
      {
        throw wrapper.badHelperWriteMethod(localException, writeMethod.getDeclaringClass().getName());
      }
    }
    
    public Exception read(org.omg.CORBA_2_3.portable.InputStream paramInputStream)
    {
      try
      {
        Object[] arrayOfObject = { paramInputStream };
        return (Exception)readMethod.invoke(null, arrayOfObject);
      }
      catch (Exception localException)
      {
        throw wrapper.badHelperReadMethod(localException, readMethod.getDeclaringClass().getName());
      }
    }
  }
  
  public class ExceptionRWRMIImpl
    extends ExceptionHandlerImpl.ExceptionRWBase
  {
    public ExceptionRWRMIImpl(Class paramClass)
    {
      super(paramClass);
      setId(IDLNameTranslatorImpl.getExceptionId(paramClass));
    }
    
    public void write(org.omg.CORBA_2_3.portable.OutputStream paramOutputStream, Exception paramException)
    {
      paramOutputStream.write_string(getId());
      paramOutputStream.write_value(paramException, getExceptionClass());
    }
    
    public Exception read(org.omg.CORBA_2_3.portable.InputStream paramInputStream)
    {
      paramInputStream.read_string();
      return (Exception)paramInputStream.read_value(getExceptionClass());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\presentation\rmi\ExceptionHandlerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */