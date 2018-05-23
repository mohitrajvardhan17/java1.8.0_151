package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.SystemException;
import org.omg.PortableInterceptor.ClientRequestInterceptor;
import org.omg.PortableInterceptor.ForwardRequest;
import org.omg.PortableInterceptor.IORInterceptor;
import org.omg.PortableInterceptor.IORInterceptor_3_0;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;
import org.omg.PortableInterceptor.ServerRequestInterceptor;

public class InterceptorInvoker
{
  private ORB orb;
  private InterceptorList interceptorList;
  private boolean enabled = false;
  private PICurrent current;
  
  InterceptorInvoker(ORB paramORB, InterceptorList paramInterceptorList, PICurrent paramPICurrent)
  {
    orb = paramORB;
    interceptorList = paramInterceptorList;
    enabled = false;
    current = paramPICurrent;
  }
  
  void setEnabled(boolean paramBoolean)
  {
    enabled = paramBoolean;
  }
  
  void objectAdapterCreated(ObjectAdapter paramObjectAdapter)
  {
    if (enabled)
    {
      IORInfoImpl localIORInfoImpl = new IORInfoImpl(paramObjectAdapter);
      IORInterceptor[] arrayOfIORInterceptor = (IORInterceptor[])interceptorList.getInterceptors(2);
      int i = arrayOfIORInterceptor.length;
      IORInterceptor localIORInterceptor;
      for (int j = i - 1; j >= 0; j--)
      {
        localIORInterceptor = arrayOfIORInterceptor[j];
        try
        {
          localIORInterceptor.establish_components(localIORInfoImpl);
        }
        catch (Exception localException) {}
      }
      localIORInfoImpl.makeStateEstablished();
      for (j = i - 1; j >= 0; j--)
      {
        localIORInterceptor = arrayOfIORInterceptor[j];
        if ((localIORInterceptor instanceof IORInterceptor_3_0))
        {
          IORInterceptor_3_0 localIORInterceptor_3_0 = (IORInterceptor_3_0)localIORInterceptor;
          localIORInterceptor_3_0.components_established(localIORInfoImpl);
        }
      }
      localIORInfoImpl.makeStateDone();
    }
  }
  
  void adapterManagerStateChanged(int paramInt, short paramShort)
  {
    if (enabled)
    {
      IORInterceptor[] arrayOfIORInterceptor = (IORInterceptor[])interceptorList.getInterceptors(2);
      int i = arrayOfIORInterceptor.length;
      for (int j = i - 1; j >= 0; j--) {
        try
        {
          IORInterceptor localIORInterceptor = arrayOfIORInterceptor[j];
          if ((localIORInterceptor instanceof IORInterceptor_3_0))
          {
            IORInterceptor_3_0 localIORInterceptor_3_0 = (IORInterceptor_3_0)localIORInterceptor;
            localIORInterceptor_3_0.adapter_manager_state_changed(paramInt, paramShort);
          }
        }
        catch (Exception localException) {}
      }
    }
  }
  
  void adapterStateChanged(ObjectReferenceTemplate[] paramArrayOfObjectReferenceTemplate, short paramShort)
  {
    if (enabled)
    {
      IORInterceptor[] arrayOfIORInterceptor = (IORInterceptor[])interceptorList.getInterceptors(2);
      int i = arrayOfIORInterceptor.length;
      for (int j = i - 1; j >= 0; j--) {
        try
        {
          IORInterceptor localIORInterceptor = arrayOfIORInterceptor[j];
          if ((localIORInterceptor instanceof IORInterceptor_3_0))
          {
            IORInterceptor_3_0 localIORInterceptor_3_0 = (IORInterceptor_3_0)localIORInterceptor;
            localIORInterceptor_3_0.adapter_state_changed(paramArrayOfObjectReferenceTemplate, paramShort);
          }
        }
        catch (Exception localException) {}
      }
    }
  }
  
  void invokeClientInterceptorStartingPoint(ClientRequestInfoImpl paramClientRequestInfoImpl)
  {
    if (enabled) {
      try
      {
        current.pushSlotTable();
        paramClientRequestInfoImpl.setPICurrentPushed(true);
        paramClientRequestInfoImpl.setCurrentExecutionPoint(0);
        ClientRequestInterceptor[] arrayOfClientRequestInterceptor = (ClientRequestInterceptor[])interceptorList.getInterceptors(0);
        int i = arrayOfClientRequestInterceptor.length;
        int j = i;
        int k = 1;
        for (int m = 0; (k != 0) && (m < i); m++) {
          try
          {
            arrayOfClientRequestInterceptor[m].send_request(paramClientRequestInfoImpl);
          }
          catch (ForwardRequest localForwardRequest)
          {
            j = m;
            paramClientRequestInfoImpl.setForwardRequest(localForwardRequest);
            paramClientRequestInfoImpl.setEndingPointCall(2);
            paramClientRequestInfoImpl.setReplyStatus((short)3);
            updateClientRequestDispatcherForward(paramClientRequestInfoImpl);
            k = 0;
          }
          catch (SystemException localSystemException)
          {
            j = m;
            paramClientRequestInfoImpl.setEndingPointCall(1);
            paramClientRequestInfoImpl.setReplyStatus((short)1);
            paramClientRequestInfoImpl.setException(localSystemException);
            k = 0;
          }
        }
        paramClientRequestInfoImpl.setFlowStackIndex(j);
      }
      finally
      {
        current.resetSlotTable();
      }
    }
  }
  
  void invokeClientInterceptorEndingPoint(ClientRequestInfoImpl paramClientRequestInfoImpl)
  {
    if (enabled) {
      try
      {
        paramClientRequestInfoImpl.setCurrentExecutionPoint(2);
        ClientRequestInterceptor[] arrayOfClientRequestInterceptor = (ClientRequestInterceptor[])interceptorList.getInterceptors(0);
        int i = paramClientRequestInfoImpl.getFlowStackIndex();
        int j = paramClientRequestInfoImpl.getEndingPointCall();
        if ((j == 0) && (paramClientRequestInfoImpl.getIsOneWay()))
        {
          j = 2;
          paramClientRequestInfoImpl.setEndingPointCall(j);
        }
        for (int k = i - 1; k >= 0; k--) {
          try
          {
            switch (j)
            {
            case 0: 
              arrayOfClientRequestInterceptor[k].receive_reply(paramClientRequestInfoImpl);
              break;
            case 1: 
              arrayOfClientRequestInterceptor[k].receive_exception(paramClientRequestInfoImpl);
              break;
            case 2: 
              arrayOfClientRequestInterceptor[k].receive_other(paramClientRequestInfoImpl);
            }
          }
          catch (ForwardRequest localForwardRequest)
          {
            j = 2;
            paramClientRequestInfoImpl.setEndingPointCall(j);
            paramClientRequestInfoImpl.setReplyStatus((short)3);
            paramClientRequestInfoImpl.setForwardRequest(localForwardRequest);
            updateClientRequestDispatcherForward(paramClientRequestInfoImpl);
          }
          catch (SystemException localSystemException)
          {
            j = 1;
            paramClientRequestInfoImpl.setEndingPointCall(j);
            paramClientRequestInfoImpl.setReplyStatus((short)1);
            paramClientRequestInfoImpl.setException(localSystemException);
          }
        }
      }
      finally
      {
        if ((paramClientRequestInfoImpl != null) && (paramClientRequestInfoImpl.isPICurrentPushed())) {
          current.popSlotTable();
        }
      }
    }
  }
  
  void invokeServerInterceptorStartingPoint(ServerRequestInfoImpl paramServerRequestInfoImpl)
  {
    if (enabled) {
      try
      {
        current.pushSlotTable();
        paramServerRequestInfoImpl.setSlotTable(current.getSlotTable());
        current.pushSlotTable();
        paramServerRequestInfoImpl.setCurrentExecutionPoint(0);
        ServerRequestInterceptor[] arrayOfServerRequestInterceptor = (ServerRequestInterceptor[])interceptorList.getInterceptors(1);
        int i = arrayOfServerRequestInterceptor.length;
        int j = i;
        int k = 1;
        for (int m = 0; (k != 0) && (m < i); m++) {
          try
          {
            arrayOfServerRequestInterceptor[m].receive_request_service_contexts(paramServerRequestInfoImpl);
          }
          catch (ForwardRequest localForwardRequest)
          {
            j = m;
            paramServerRequestInfoImpl.setForwardRequest(localForwardRequest);
            paramServerRequestInfoImpl.setIntermediatePointCall(1);
            paramServerRequestInfoImpl.setEndingPointCall(2);
            paramServerRequestInfoImpl.setReplyStatus((short)3);
            k = 0;
          }
          catch (SystemException localSystemException)
          {
            j = m;
            paramServerRequestInfoImpl.setException(localSystemException);
            paramServerRequestInfoImpl.setIntermediatePointCall(1);
            paramServerRequestInfoImpl.setEndingPointCall(1);
            paramServerRequestInfoImpl.setReplyStatus((short)1);
            k = 0;
          }
        }
        paramServerRequestInfoImpl.setFlowStackIndex(j);
      }
      finally
      {
        current.popSlotTable();
      }
    }
  }
  
  void invokeServerInterceptorIntermediatePoint(ServerRequestInfoImpl paramServerRequestInfoImpl)
  {
    int i = paramServerRequestInfoImpl.getIntermediatePointCall();
    if ((enabled) && (i != 1))
    {
      paramServerRequestInfoImpl.setCurrentExecutionPoint(1);
      ServerRequestInterceptor[] arrayOfServerRequestInterceptor = (ServerRequestInterceptor[])interceptorList.getInterceptors(1);
      int j = arrayOfServerRequestInterceptor.length;
      for (int k = 0; k < j; k++) {
        try
        {
          arrayOfServerRequestInterceptor[k].receive_request(paramServerRequestInfoImpl);
        }
        catch (ForwardRequest localForwardRequest)
        {
          paramServerRequestInfoImpl.setForwardRequest(localForwardRequest);
          paramServerRequestInfoImpl.setEndingPointCall(2);
          paramServerRequestInfoImpl.setReplyStatus((short)3);
          break;
        }
        catch (SystemException localSystemException)
        {
          paramServerRequestInfoImpl.setException(localSystemException);
          paramServerRequestInfoImpl.setEndingPointCall(1);
          paramServerRequestInfoImpl.setReplyStatus((short)1);
          break;
        }
      }
    }
  }
  
  void invokeServerInterceptorEndingPoint(ServerRequestInfoImpl paramServerRequestInfoImpl)
  {
    if (enabled) {
      try
      {
        ServerRequestInterceptor[] arrayOfServerRequestInterceptor = (ServerRequestInterceptor[])interceptorList.getInterceptors(1);
        int i = paramServerRequestInfoImpl.getFlowStackIndex();
        int j = paramServerRequestInfoImpl.getEndingPointCall();
        for (int k = i - 1; k >= 0; k--) {
          try
          {
            switch (j)
            {
            case 0: 
              arrayOfServerRequestInterceptor[k].send_reply(paramServerRequestInfoImpl);
              break;
            case 1: 
              arrayOfServerRequestInterceptor[k].send_exception(paramServerRequestInfoImpl);
              break;
            case 2: 
              arrayOfServerRequestInterceptor[k].send_other(paramServerRequestInfoImpl);
            }
          }
          catch (ForwardRequest localForwardRequest)
          {
            j = 2;
            paramServerRequestInfoImpl.setEndingPointCall(j);
            paramServerRequestInfoImpl.setForwardRequest(localForwardRequest);
            paramServerRequestInfoImpl.setReplyStatus((short)3);
            paramServerRequestInfoImpl.setForwardRequestRaisedInEnding();
          }
          catch (SystemException localSystemException)
          {
            j = 1;
            paramServerRequestInfoImpl.setEndingPointCall(j);
            paramServerRequestInfoImpl.setException(localSystemException);
            paramServerRequestInfoImpl.setReplyStatus((short)1);
          }
        }
        paramServerRequestInfoImpl.setAlreadyExecuted(true);
      }
      finally
      {
        current.popSlotTable();
      }
    }
  }
  
  private void updateClientRequestDispatcherForward(ClientRequestInfoImpl paramClientRequestInfoImpl)
  {
    ForwardRequest localForwardRequest = paramClientRequestInfoImpl.getForwardRequestException();
    if (localForwardRequest != null)
    {
      org.omg.CORBA.Object localObject = forward;
      IOR localIOR = ORBUtility.getIOR(localObject);
      paramClientRequestInfoImpl.setLocatedIOR(localIOR);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\interceptors\InterceptorInvoker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */