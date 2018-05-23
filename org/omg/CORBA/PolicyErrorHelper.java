package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class PolicyErrorHelper
{
  private static String _id = "IDL:omg.org/CORBA/PolicyError:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public PolicyErrorHelper() {}
  
  public static void insert(Any paramAny, PolicyError paramPolicyError)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramPolicyError);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static PolicyError extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null) {
      synchronized (TypeCode.class)
      {
        if (__typeCode == null)
        {
          if (__active) {
            return ORB.init().create_recursive_tc(_id);
          }
          __active = true;
          StructMember[] arrayOfStructMember = new StructMember[1];
          TypeCode localTypeCode = null;
          localTypeCode = ORB.init().get_primitive_tc(TCKind.tk_short);
          localTypeCode = ORB.init().create_alias_tc(PolicyErrorCodeHelper.id(), "PolicyErrorCode", localTypeCode);
          arrayOfStructMember[0] = new StructMember("reason", localTypeCode, null);
          __typeCode = ORB.init().create_exception_tc(id(), "PolicyError", arrayOfStructMember);
          __active = false;
        }
      }
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static PolicyError read(InputStream paramInputStream)
  {
    PolicyError localPolicyError = new PolicyError();
    paramInputStream.read_string();
    reason = paramInputStream.read_short();
    return localPolicyError;
  }
  
  public static void write(OutputStream paramOutputStream, PolicyError paramPolicyError)
  {
    paramOutputStream.write_string(id());
    paramOutputStream.write_short(reason);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\PolicyErrorHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */