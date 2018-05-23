package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ServiceInformationHelper
{
  private static TypeCode _tc;
  
  public ServiceInformationHelper() {}
  
  public static void write(OutputStream paramOutputStream, ServiceInformation paramServiceInformation)
  {
    paramOutputStream.write_long(service_options.length);
    paramOutputStream.write_ulong_array(service_options, 0, service_options.length);
    paramOutputStream.write_long(service_details.length);
    for (int i = 0; i < service_details.length; i++) {
      ServiceDetailHelper.write(paramOutputStream, service_details[i]);
    }
  }
  
  public static ServiceInformation read(InputStream paramInputStream)
  {
    ServiceInformation localServiceInformation = new ServiceInformation();
    int i = paramInputStream.read_long();
    service_options = new int[i];
    paramInputStream.read_ulong_array(service_options, 0, service_options.length);
    i = paramInputStream.read_long();
    service_details = new ServiceDetail[i];
    for (int j = 0; j < service_details.length; j++) {
      service_details[j] = ServiceDetailHelper.read(paramInputStream);
    }
    return localServiceInformation;
  }
  
  public static ServiceInformation extract(Any paramAny)
  {
    InputStream localInputStream = paramAny.create_input_stream();
    return read(localInputStream);
  }
  
  public static void insert(Any paramAny, ServiceInformation paramServiceInformation)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    write(localOutputStream, paramServiceInformation);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static synchronized TypeCode type()
  {
    int i = 2;
    StructMember[] arrayOfStructMember = null;
    if (_tc == null)
    {
      arrayOfStructMember = new StructMember[2];
      arrayOfStructMember[0] = new StructMember("service_options", ORB.init().create_sequence_tc(0, ORB.init().get_primitive_tc(TCKind.tk_ulong)), null);
      arrayOfStructMember[1] = new StructMember("service_details", ORB.init().create_sequence_tc(0, ServiceDetailHelper.type()), null);
      _tc = ORB.init().create_struct_tc(id(), "ServiceInformation", arrayOfStructMember);
    }
    return _tc;
  }
  
  public static String id()
  {
    return "IDL:omg.org/CORBA/ServiceInformation:1.0";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\ServiceInformationHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */