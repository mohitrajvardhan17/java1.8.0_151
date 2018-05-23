package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class BadServerDefinitionHolder
  implements Streamable
{
  public BadServerDefinition value = null;
  
  public BadServerDefinitionHolder() {}
  
  public BadServerDefinitionHolder(BadServerDefinition paramBadServerDefinition)
  {
    value = paramBadServerDefinition;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = BadServerDefinitionHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    BadServerDefinitionHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return BadServerDefinitionHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\BadServerDefinitionHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */