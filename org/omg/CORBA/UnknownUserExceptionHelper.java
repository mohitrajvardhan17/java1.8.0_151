package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class UnknownUserExceptionHelper
{
  private static String _id = "IDL:omg.org/CORBA/UnknownUserException:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public UnknownUserExceptionHelper() {}
  
  public static void insert(Any paramAny, UnknownUserException paramUnknownUserException)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramUnknownUserException);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static UnknownUserException extract(Any paramAny)
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
          localTypeCode = ORB.init().get_primitive_tc(TCKind.tk_any);
          arrayOfStructMember[0] = new StructMember("except", localTypeCode, null);
          __typeCode = ORB.init().create_exception_tc(id(), "UnknownUserException", arrayOfStructMember);
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
  
  public static UnknownUserException read(InputStream paramInputStream)
  {
    UnknownUserException localUnknownUserException = new UnknownUserException();
    paramInputStream.read_string();
    except = paramInputStream.read_any();
    return localUnknownUserException;
  }
  
  public static void write(OutputStream paramOutputStream, UnknownUserException paramUnknownUserException)
  {
    paramOutputStream.write_string(id());
    paramOutputStream.write_any(except);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\UnknownUserExceptionHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */