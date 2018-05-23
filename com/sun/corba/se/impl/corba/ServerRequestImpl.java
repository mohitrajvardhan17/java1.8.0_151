package com.sun.corba.se.impl.corba;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.protocol.PIHandler;
import org.omg.CORBA.Any;
import org.omg.CORBA.Bounds;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Context;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.ServerRequest;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public class ServerRequestImpl
  extends ServerRequest
{
  private ORB _orb = null;
  private ORBUtilSystemException _wrapper = null;
  private String _opName = null;
  private NVList _arguments = null;
  private Context _ctx = null;
  private InputStream _ins = null;
  private boolean _paramsCalled = false;
  private boolean _resultSet = false;
  private boolean _exceptionSet = false;
  private Any _resultAny = null;
  private Any _exception = null;
  
  public ServerRequestImpl(CorbaMessageMediator paramCorbaMessageMediator, ORB paramORB)
  {
    _opName = paramCorbaMessageMediator.getOperationName();
    _ins = ((InputStream)paramCorbaMessageMediator.getInputObject());
    _ctx = null;
    _orb = paramORB;
    _wrapper = ORBUtilSystemException.get(paramORB, "oa.invocation");
  }
  
  public String operation()
  {
    return _opName;
  }
  
  public void arguments(NVList paramNVList)
  {
    if (_paramsCalled) {
      throw _wrapper.argumentsCalledMultiple();
    }
    if (_exceptionSet) {
      throw _wrapper.argumentsCalledAfterException();
    }
    if (paramNVList == null) {
      throw _wrapper.argumentsCalledNullArgs();
    }
    _paramsCalled = true;
    NamedValue localNamedValue = null;
    for (int i = 0; i < paramNVList.count(); i++)
    {
      try
      {
        localNamedValue = paramNVList.item(i);
      }
      catch (Bounds localBounds)
      {
        throw _wrapper.boundsCannotOccur(localBounds);
      }
      try
      {
        if ((localNamedValue.flags() == 1) || (localNamedValue.flags() == 3)) {
          localNamedValue.value().read_value(_ins, localNamedValue.value().type());
        }
      }
      catch (Exception localException)
      {
        throw _wrapper.badArgumentsNvlist(localException);
      }
    }
    _arguments = paramNVList;
    _orb.getPIHandler().setServerPIInfo(_arguments);
    _orb.getPIHandler().invokeServerPIIntermediatePoint();
  }
  
  public void set_result(Any paramAny)
  {
    if (!_paramsCalled) {
      throw _wrapper.argumentsNotCalled();
    }
    if (_resultSet) {
      throw _wrapper.setResultCalledMultiple();
    }
    if (_exceptionSet) {
      throw _wrapper.setResultAfterException();
    }
    if (paramAny == null) {
      throw _wrapper.setResultCalledNullArgs();
    }
    _resultAny = paramAny;
    _resultSet = true;
    _orb.getPIHandler().setServerPIInfo(_resultAny);
  }
  
  public void set_exception(Any paramAny)
  {
    if (paramAny == null) {
      throw _wrapper.setExceptionCalledNullArgs();
    }
    TCKind localTCKind = paramAny.type().kind();
    if (localTCKind != TCKind.tk_except) {
      throw _wrapper.setExceptionCalledBadType();
    }
    _exception = paramAny;
    _orb.getPIHandler().setServerPIExceptionInfo(_exception);
    if ((!_exceptionSet) && (!_paramsCalled)) {
      _orb.getPIHandler().invokeServerPIIntermediatePoint();
    }
    _exceptionSet = true;
  }
  
  public Any checkResultCalled()
  {
    if ((_paramsCalled) && (_resultSet)) {
      return null;
    }
    if ((_paramsCalled) && (!_resultSet) && (!_exceptionSet)) {
      try
      {
        TypeCode localTypeCode = _orb.get_primitive_tc(TCKind.tk_void);
        _resultAny = _orb.create_any();
        _resultAny.type(localTypeCode);
        _resultSet = true;
        return null;
      }
      catch (Exception localException)
      {
        throw _wrapper.dsiResultException(CompletionStatus.COMPLETED_MAYBE, localException);
      }
    }
    if (_exceptionSet) {
      return _exception;
    }
    throw _wrapper.dsimethodNotcalled(CompletionStatus.COMPLETED_MAYBE);
  }
  
  public void marshalReplyParams(OutputStream paramOutputStream)
  {
    _resultAny.write_value(paramOutputStream);
    NamedValue localNamedValue = null;
    for (int i = 0; i < _arguments.count(); i++)
    {
      try
      {
        localNamedValue = _arguments.item(i);
      }
      catch (Bounds localBounds) {}
      if ((localNamedValue.flags() == 2) || (localNamedValue.flags() == 3)) {
        localNamedValue.value().write_value(paramOutputStream);
      }
    }
  }
  
  public Context ctx()
  {
    if ((!_paramsCalled) || (_resultSet) || (_exceptionSet)) {
      throw _wrapper.contextCalledOutOfOrder();
    }
    throw _wrapper.contextNotImplemented();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\corba\ServerRequestImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */