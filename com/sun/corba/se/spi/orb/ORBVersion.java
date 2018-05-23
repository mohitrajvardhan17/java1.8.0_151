package com.sun.corba.se.spi.orb;

import org.omg.CORBA.portable.OutputStream;

public abstract interface ORBVersion
  extends Comparable
{
  public static final byte FOREIGN = 0;
  public static final byte OLD = 1;
  public static final byte NEW = 2;
  public static final byte JDK1_3_1_01 = 3;
  public static final byte NEWER = 10;
  public static final byte PEORB = 20;
  
  public abstract byte getORBType();
  
  public abstract void write(OutputStream paramOutputStream);
  
  public abstract boolean lessThan(ORBVersion paramORBVersion);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\orb\ORBVersion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */