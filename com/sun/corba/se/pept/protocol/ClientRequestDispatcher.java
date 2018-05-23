package com.sun.corba.se.pept.protocol;

import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.transport.ContactInfo;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.RemarshalException;

public abstract interface ClientRequestDispatcher
{
  public abstract OutputObject beginRequest(Object paramObject, String paramString, boolean paramBoolean, ContactInfo paramContactInfo);
  
  public abstract InputObject marshalingComplete(Object paramObject, OutputObject paramOutputObject)
    throws ApplicationException, RemarshalException;
  
  public abstract void endRequest(Broker paramBroker, Object paramObject, InputObject paramInputObject);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\pept\protocol\ClientRequestDispatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */