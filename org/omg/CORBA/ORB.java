package org.omg.CORBA;

import com.sun.corba.se.impl.orb.ORBImpl;
import com.sun.corba.se.impl.orb.ORBSingleton;
import java.applet.Applet;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;
import org.omg.CORBA.ORBPackage.InconsistentTypeCode;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.portable.OutputStream;
import sun.reflect.misc.ReflectUtil;

public abstract class ORB
{
  private static final String ORBClassKey = "org.omg.CORBA.ORBClass";
  private static final String ORBSingletonClassKey = "org.omg.CORBA.ORBSingletonClass";
  private static ORB singleton;
  
  public ORB() {}
  
  private static String getSystemProperty(String paramString)
  {
    String str = (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public java.lang.Object run()
      {
        return System.getProperty(val$name);
      }
    });
    return str;
  }
  
  private static String getPropertyFromFile(String paramString)
  {
    String str = (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      private Properties getFileProperties(String paramAnonymousString)
      {
        try
        {
          File localFile = new File(paramAnonymousString);
          if (!localFile.exists()) {
            return null;
          }
          Properties localProperties = new Properties();
          FileInputStream localFileInputStream = new FileInputStream(localFile);
          try
          {
            localProperties.load(localFileInputStream);
          }
          finally
          {
            localFileInputStream.close();
          }
          return localProperties;
        }
        catch (Exception localException) {}
        return null;
      }
      
      public java.lang.Object run()
      {
        String str1 = System.getProperty("user.home");
        String str2 = str1 + File.separator + "orb.properties";
        Properties localProperties = getFileProperties(str2);
        if (localProperties != null)
        {
          str3 = localProperties.getProperty(val$name);
          if (str3 != null) {
            return str3;
          }
        }
        String str3 = System.getProperty("java.home");
        str2 = str3 + File.separator + "lib" + File.separator + "orb.properties";
        localProperties = getFileProperties(str2);
        if (localProperties == null) {
          return null;
        }
        return localProperties.getProperty(val$name);
      }
    });
    return str;
  }
  
  public static synchronized ORB init()
  {
    if (singleton == null)
    {
      String str = getSystemProperty("org.omg.CORBA.ORBSingletonClass");
      if (str == null) {
        str = getPropertyFromFile("org.omg.CORBA.ORBSingletonClass");
      }
      if ((str == null) || (str.equals("com.sun.corba.se.impl.orb.ORBSingleton"))) {
        singleton = new ORBSingleton();
      } else {
        singleton = create_impl(str);
      }
    }
    return singleton;
  }
  
  private static ORB create_impl(String paramString)
  {
    ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
    if (localClassLoader == null) {
      localClassLoader = ClassLoader.getSystemClassLoader();
    }
    try
    {
      ReflectUtil.checkPackageAccess(paramString);
      Class localClass = ORB.class;
      localObject = Class.forName(paramString, true, localClassLoader).asSubclass(localClass);
      return (ORB)((Class)localObject).newInstance();
    }
    catch (Throwable localThrowable)
    {
      java.lang.Object localObject = new INITIALIZE("can't instantiate default ORB implementation " + paramString);
      ((SystemException)localObject).initCause(localThrowable);
      throw ((Throwable)localObject);
    }
  }
  
  public static ORB init(String[] paramArrayOfString, Properties paramProperties)
  {
    String str = null;
    if (paramProperties != null) {
      str = paramProperties.getProperty("org.omg.CORBA.ORBClass");
    }
    if (str == null) {
      str = getSystemProperty("org.omg.CORBA.ORBClass");
    }
    if (str == null) {
      str = getPropertyFromFile("org.omg.CORBA.ORBClass");
    }
    java.lang.Object localObject;
    if ((str == null) || (str.equals("com.sun.corba.se.impl.orb.ORBImpl"))) {
      localObject = new ORBImpl();
    } else {
      localObject = create_impl(str);
    }
    ((ORB)localObject).set_parameters(paramArrayOfString, paramProperties);
    return (ORB)localObject;
  }
  
  public static ORB init(Applet paramApplet, Properties paramProperties)
  {
    String str = paramApplet.getParameter("org.omg.CORBA.ORBClass");
    if ((str == null) && (paramProperties != null)) {
      str = paramProperties.getProperty("org.omg.CORBA.ORBClass");
    }
    if (str == null) {
      str = getSystemProperty("org.omg.CORBA.ORBClass");
    }
    if (str == null) {
      str = getPropertyFromFile("org.omg.CORBA.ORBClass");
    }
    java.lang.Object localObject;
    if ((str == null) || (str.equals("com.sun.corba.se.impl.orb.ORBImpl"))) {
      localObject = new ORBImpl();
    } else {
      localObject = create_impl(str);
    }
    ((ORB)localObject).set_parameters(paramApplet, paramProperties);
    return (ORB)localObject;
  }
  
  protected abstract void set_parameters(String[] paramArrayOfString, Properties paramProperties);
  
  protected abstract void set_parameters(Applet paramApplet, Properties paramProperties);
  
  public void connect(Object paramObject)
  {
    throw new NO_IMPLEMENT();
  }
  
  public void destroy()
  {
    throw new NO_IMPLEMENT();
  }
  
  public void disconnect(Object paramObject)
  {
    throw new NO_IMPLEMENT();
  }
  
  public abstract String[] list_initial_services();
  
  public abstract Object resolve_initial_references(String paramString)
    throws InvalidName;
  
  public abstract String object_to_string(Object paramObject);
  
  public abstract Object string_to_object(String paramString);
  
  public abstract NVList create_list(int paramInt);
  
  public NVList create_operation_list(Object paramObject)
  {
    try
    {
      String str = "org.omg.CORBA.OperationDef";
      localObject = null;
      ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
      if (localClassLoader == null) {
        localClassLoader = ClassLoader.getSystemClassLoader();
      }
      localObject = Class.forName(str, true, localClassLoader);
      Class[] arrayOfClass = { localObject };
      Method localMethod = getClass().getMethod("create_operation_list", arrayOfClass);
      java.lang.Object[] arrayOfObject = { paramObject };
      return (NVList)localMethod.invoke(this, arrayOfObject);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      java.lang.Object localObject = localInvocationTargetException.getTargetException();
      if ((localObject instanceof Error)) {
        throw ((Error)localObject);
      }
      if ((localObject instanceof RuntimeException)) {
        throw ((RuntimeException)localObject);
      }
      throw new NO_IMPLEMENT();
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (Exception localException)
    {
      throw new NO_IMPLEMENT();
    }
  }
  
  public abstract NamedValue create_named_value(String paramString, Any paramAny, int paramInt);
  
  public abstract ExceptionList create_exception_list();
  
  public abstract ContextList create_context_list();
  
  public abstract Context get_default_context();
  
  public abstract Environment create_environment();
  
  public abstract OutputStream create_output_stream();
  
  public abstract void send_multiple_requests_oneway(Request[] paramArrayOfRequest);
  
  public abstract void send_multiple_requests_deferred(Request[] paramArrayOfRequest);
  
  public abstract boolean poll_next_response();
  
  public abstract Request get_next_response()
    throws WrongTransaction;
  
  public abstract TypeCode get_primitive_tc(TCKind paramTCKind);
  
  public abstract TypeCode create_struct_tc(String paramString1, String paramString2, StructMember[] paramArrayOfStructMember);
  
  public abstract TypeCode create_union_tc(String paramString1, String paramString2, TypeCode paramTypeCode, UnionMember[] paramArrayOfUnionMember);
  
  public abstract TypeCode create_enum_tc(String paramString1, String paramString2, String[] paramArrayOfString);
  
  public abstract TypeCode create_alias_tc(String paramString1, String paramString2, TypeCode paramTypeCode);
  
  public abstract TypeCode create_exception_tc(String paramString1, String paramString2, StructMember[] paramArrayOfStructMember);
  
  public abstract TypeCode create_interface_tc(String paramString1, String paramString2);
  
  public abstract TypeCode create_string_tc(int paramInt);
  
  public abstract TypeCode create_wstring_tc(int paramInt);
  
  public abstract TypeCode create_sequence_tc(int paramInt, TypeCode paramTypeCode);
  
  @Deprecated
  public abstract TypeCode create_recursive_sequence_tc(int paramInt1, int paramInt2);
  
  public abstract TypeCode create_array_tc(int paramInt, TypeCode paramTypeCode);
  
  public TypeCode create_native_tc(String paramString1, String paramString2)
  {
    throw new NO_IMPLEMENT();
  }
  
  public TypeCode create_abstract_interface_tc(String paramString1, String paramString2)
  {
    throw new NO_IMPLEMENT();
  }
  
  public TypeCode create_fixed_tc(short paramShort1, short paramShort2)
  {
    throw new NO_IMPLEMENT();
  }
  
  public TypeCode create_value_tc(String paramString1, String paramString2, short paramShort, TypeCode paramTypeCode, ValueMember[] paramArrayOfValueMember)
  {
    throw new NO_IMPLEMENT();
  }
  
  public TypeCode create_recursive_tc(String paramString)
  {
    throw new NO_IMPLEMENT();
  }
  
  public TypeCode create_value_box_tc(String paramString1, String paramString2, TypeCode paramTypeCode)
  {
    throw new NO_IMPLEMENT();
  }
  
  public abstract Any create_any();
  
  @Deprecated
  public Current get_current()
  {
    throw new NO_IMPLEMENT();
  }
  
  public void run()
  {
    throw new NO_IMPLEMENT();
  }
  
  public void shutdown(boolean paramBoolean)
  {
    throw new NO_IMPLEMENT();
  }
  
  public boolean work_pending()
  {
    throw new NO_IMPLEMENT();
  }
  
  public void perform_work()
  {
    throw new NO_IMPLEMENT();
  }
  
  public boolean get_service_information(short paramShort, ServiceInformationHolder paramServiceInformationHolder)
  {
    throw new NO_IMPLEMENT();
  }
  
  @Deprecated
  public DynAny create_dyn_any(Any paramAny)
  {
    throw new NO_IMPLEMENT();
  }
  
  @Deprecated
  public DynAny create_basic_dyn_any(TypeCode paramTypeCode)
    throws InconsistentTypeCode
  {
    throw new NO_IMPLEMENT();
  }
  
  @Deprecated
  public DynStruct create_dyn_struct(TypeCode paramTypeCode)
    throws InconsistentTypeCode
  {
    throw new NO_IMPLEMENT();
  }
  
  @Deprecated
  public DynSequence create_dyn_sequence(TypeCode paramTypeCode)
    throws InconsistentTypeCode
  {
    throw new NO_IMPLEMENT();
  }
  
  @Deprecated
  public DynArray create_dyn_array(TypeCode paramTypeCode)
    throws InconsistentTypeCode
  {
    throw new NO_IMPLEMENT();
  }
  
  @Deprecated
  public DynUnion create_dyn_union(TypeCode paramTypeCode)
    throws InconsistentTypeCode
  {
    throw new NO_IMPLEMENT();
  }
  
  @Deprecated
  public DynEnum create_dyn_enum(TypeCode paramTypeCode)
    throws InconsistentTypeCode
  {
    throw new NO_IMPLEMENT();
  }
  
  public Policy create_policy(int paramInt, Any paramAny)
    throws PolicyError
  {
    throw new NO_IMPLEMENT();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\ORB.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */