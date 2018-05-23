package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ServiceDetailHelper
{
  private static TypeCode _tc;
  
  public ServiceDetailHelper() {}
  
  public static void write(OutputStream paramOutputStream, ServiceDetail paramServiceDetail)
  {
    paramOutputStream.write_ulong(service_detail_type);
    paramOutputStream.write_long(service_detail.length);
    paramOutputStream.write_octet_array(service_detail, 0, service_detail.length);
  }
  
  public static ServiceDetail read(InputStream paramInputStream)
  {
    ServiceDetail localServiceDetail = new ServiceDetail();
    service_detail_type = paramInputStream.read_ulong();
    int i = paramInputStream.read_long();
    service_detail = new byte[i];
    paramInputStream.read_octet_array(service_detail, 0, service_detail.length);
    return localServiceDetail;
  }
  
  public static ServiceDetail extract(Any paramAny)
  {
    InputStream localInputStream = paramAny.create_input_stream();
    return read(localInputStream);
  }
  
  public static void insert(Any paramAny, ServiceDetail paramServiceDetail)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    write(localOutputStream, paramServiceDetail);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static synchronized TypeCode type()
  {
    int i = 2;
    StructMember[] arrayOfStructMember = null;
    if (_tc == null)
    {
      arrayOfStructMember = new StructMember[2];
      arrayOfStructMember[0] = new StructMember("service_detail_type", ORB.init().get_primitive_tc(TCKind.tk_ulong), null);
      arrayOfStructMember[1] = new StructMember("service_detail", ORB.init().create_sequence_tc(0, ORB.init().get_primitive_tc(TCKind.tk_octet)), null);
      _tc = ORB.init().create_struct_tc(id(), "ServiceDetail", arrayOfStructMember);
    }
    return _tc;
  }
  
  public static String id()
  {
    return "IDL:omg.org/CORBA/ServiceDetail:1.0";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\ServiceDetailHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */