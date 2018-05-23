package org.omg.PortableServer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.PortableServer.portable.Delegate;

public abstract class Servant
{
  private transient Delegate _delegate = null;
  
  public Servant() {}
  
  public final Delegate _get_delegate()
  {
    if (_delegate == null) {
      throw new BAD_INV_ORDER("The Servant has not been associated with an ORB instance");
    }
    return _delegate;
  }
  
  public final void _set_delegate(Delegate paramDelegate)
  {
    _delegate = paramDelegate;
  }
  
  public final org.omg.CORBA.Object _this_object()
  {
    return _get_delegate().this_object(this);
  }
  
  public final org.omg.CORBA.Object _this_object(org.omg.CORBA.ORB paramORB)
  {
    try
    {
      ((org.omg.CORBA_2_3.ORB)paramORB).set_delegate(this);
    }
    catch (ClassCastException localClassCastException)
    {
      throw new BAD_PARAM("POA Servant requires an instance of org.omg.CORBA_2_3.ORB");
    }
    return _this_object();
  }
  
  public final org.omg.CORBA.ORB _orb()
  {
    return _get_delegate().orb(this);
  }
  
  public final POA _poa()
  {
    return _get_delegate().poa(this);
  }
  
  public final byte[] _object_id()
  {
    return _get_delegate().object_id(this);
  }
  
  public POA _default_POA()
  {
    return _get_delegate().default_POA(this);
  }
  
  public boolean _is_a(String paramString)
  {
    return _get_delegate().is_a(this, paramString);
  }
  
  public boolean _non_existent()
  {
    return _get_delegate().non_existent(this);
  }
  
  public org.omg.CORBA.Object _get_interface_def()
  {
    Delegate localDelegate = _get_delegate();
    try
    {
      return localDelegate.get_interface_def(this);
    }
    catch (AbstractMethodError localAbstractMethodError)
    {
      try
      {
        Class[] arrayOfClass = { Servant.class };
        localObject = localDelegate.getClass().getMethod("get_interface", arrayOfClass);
        Object[] arrayOfObject = { this };
        return (org.omg.CORBA.Object)((Method)localObject).invoke(localDelegate, arrayOfObject);
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        Object localObject = localInvocationTargetException.getTargetException();
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
  }
  
  public abstract String[] _all_interfaces(POA paramPOA, byte[] paramArrayOfByte);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableServer\Servant.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */