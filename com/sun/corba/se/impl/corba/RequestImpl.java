package com.sun.corba.se.impl.corba;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import com.sun.corba.se.spi.protocol.PIHandler;
import org.omg.CORBA.Any;
import org.omg.CORBA.Bounds;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.Environment;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.Request;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.WrongTransaction;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;

public class RequestImpl
  extends Request
{
  protected org.omg.CORBA.Object _target;
  protected String _opName;
  protected NVList _arguments;
  protected ExceptionList _exceptions;
  private NamedValue _result;
  protected Environment _env;
  private Context _ctx;
  private ContextList _ctxList;
  protected ORB _orb;
  private ORBUtilSystemException _wrapper;
  protected boolean _isOneWay = false;
  private int[] _paramCodes;
  private long[] _paramLongs;
  private Object[] _paramObjects;
  protected boolean gotResponse = false;
  
  public RequestImpl(ORB paramORB, org.omg.CORBA.Object paramObject, Context paramContext, String paramString, NVList paramNVList, NamedValue paramNamedValue, ExceptionList paramExceptionList, ContextList paramContextList)
  {
    _orb = paramORB;
    _wrapper = ORBUtilSystemException.get(paramORB, "oa.invocation");
    _target = paramObject;
    _ctx = paramContext;
    _opName = paramString;
    if (paramNVList == null) {
      _arguments = new NVListImpl(_orb);
    } else {
      _arguments = paramNVList;
    }
    _result = paramNamedValue;
    if (paramExceptionList == null) {
      _exceptions = new ExceptionListImpl();
    } else {
      _exceptions = paramExceptionList;
    }
    if (paramContextList == null) {
      _ctxList = new ContextListImpl(_orb);
    } else {
      _ctxList = paramContextList;
    }
    _env = new EnvironmentImpl();
  }
  
  public org.omg.CORBA.Object target()
  {
    return _target;
  }
  
  public String operation()
  {
    return _opName;
  }
  
  public NVList arguments()
  {
    return _arguments;
  }
  
  public NamedValue result()
  {
    return _result;
  }
  
  public Environment env()
  {
    return _env;
  }
  
  public ExceptionList exceptions()
  {
    return _exceptions;
  }
  
  public ContextList contexts()
  {
    return _ctxList;
  }
  
  public synchronized Context ctx()
  {
    if (_ctx == null) {
      _ctx = new ContextImpl(_orb);
    }
    return _ctx;
  }
  
  public synchronized void ctx(Context paramContext)
  {
    _ctx = paramContext;
  }
  
  public synchronized Any add_in_arg()
  {
    return _arguments.add(1).value();
  }
  
  public synchronized Any add_named_in_arg(String paramString)
  {
    return _arguments.add_item(paramString, 1).value();
  }
  
  public synchronized Any add_inout_arg()
  {
    return _arguments.add(3).value();
  }
  
  public synchronized Any add_named_inout_arg(String paramString)
  {
    return _arguments.add_item(paramString, 3).value();
  }
  
  public synchronized Any add_out_arg()
  {
    return _arguments.add(2).value();
  }
  
  public synchronized Any add_named_out_arg(String paramString)
  {
    return _arguments.add_item(paramString, 2).value();
  }
  
  public synchronized void set_return_type(TypeCode paramTypeCode)
  {
    if (_result == null) {
      _result = new NamedValueImpl(_orb);
    }
    _result.value().type(paramTypeCode);
  }
  
  public synchronized Any return_value()
  {
    if (_result == null) {
      _result = new NamedValueImpl(_orb);
    }
    return _result.value();
  }
  
  public synchronized void add_exception(TypeCode paramTypeCode)
  {
    _exceptions.add(paramTypeCode);
  }
  
  public synchronized void invoke()
  {
    doInvocation();
  }
  
  public synchronized void send_oneway()
  {
    _isOneWay = true;
    doInvocation();
  }
  
  public synchronized void send_deferred()
  {
    AsynchInvoke localAsynchInvoke = new AsynchInvoke(_orb, this, false);
    new Thread(localAsynchInvoke).start();
  }
  
  public synchronized boolean poll_response()
  {
    return gotResponse;
  }
  
  public synchronized void get_response()
    throws WrongTransaction
  {
    while (!gotResponse) {
      try
      {
        wait();
      }
      catch (InterruptedException localInterruptedException) {}
    }
  }
  
  protected void doInvocation()
  {
    Delegate localDelegate = StubAdapter.getDelegate(_target);
    _orb.getPIHandler().initiateClientPIRequest(true);
    _orb.getPIHandler().setClientPIInfo(this);
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = localDelegate.request(null, _opName, !_isOneWay);
      try
      {
        for (int i = 0; i < _arguments.count(); i++)
        {
          NamedValue localNamedValue = _arguments.item(i);
          switch (localNamedValue.flags())
          {
          case 1: 
            localNamedValue.value().write_value(localOutputStream);
            break;
          case 2: 
            break;
          case 3: 
            localNamedValue.value().write_value(localOutputStream);
          }
        }
      }
      catch (Bounds localBounds)
      {
        throw _wrapper.boundsErrorInDiiRequest(localBounds);
      }
      localInputStream = localDelegate.invoke(null, localOutputStream);
    }
    catch (ApplicationException localApplicationException) {}catch (RemarshalException localRemarshalException)
    {
      doInvocation();
    }
    catch (SystemException localSystemException)
    {
      _env.exception(localSystemException);
      throw localSystemException;
    }
    finally
    {
      localDelegate.releaseReply(null, localInputStream);
    }
  }
  
  public void unmarshalReply(InputStream paramInputStream)
  {
    Object localObject;
    if (_result != null)
    {
      Any localAny1 = _result.value();
      localObject = localAny1.type();
      if (((TypeCode)localObject).kind().value() != 1) {
        localAny1.read_value(paramInputStream, (TypeCode)localObject);
      }
    }
    try
    {
      for (int i = 0; i < _arguments.count(); i++)
      {
        localObject = _arguments.item(i);
        switch (((NamedValue)localObject).flags())
        {
        case 1: 
          break;
        case 2: 
        case 3: 
          Any localAny2 = ((NamedValue)localObject).value();
          localAny2.read_value(paramInputStream, localAny2.type());
        }
      }
    }
    catch (Bounds localBounds) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\corba\RequestImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */