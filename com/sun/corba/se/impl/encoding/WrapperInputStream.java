package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.corba.TypeCodeImpl;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.omg.CORBA.Any;
import org.omg.CORBA.Context;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA_2_3.portable.InputStream;

public class WrapperInputStream
  extends InputStream
  implements TypeCodeReader
{
  private CDRInputStream stream;
  private Map typeMap = null;
  private int startPos = 0;
  
  public WrapperInputStream(CDRInputStream paramCDRInputStream)
  {
    stream = paramCDRInputStream;
    startPos = stream.getPosition();
  }
  
  public int read()
    throws IOException
  {
    return stream.read();
  }
  
  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    return stream.read(paramArrayOfByte);
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    return stream.read(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    return stream.skip(paramLong);
  }
  
  public int available()
    throws IOException
  {
    return stream.available();
  }
  
  public void close()
    throws IOException
  {
    stream.close();
  }
  
  public void mark(int paramInt)
  {
    stream.mark(paramInt);
  }
  
  public void reset()
  {
    stream.reset();
  }
  
  public boolean markSupported()
  {
    return stream.markSupported();
  }
  
  public int getPosition()
  {
    return stream.getPosition();
  }
  
  public void consumeEndian()
  {
    stream.consumeEndian();
  }
  
  public boolean read_boolean()
  {
    return stream.read_boolean();
  }
  
  public char read_char()
  {
    return stream.read_char();
  }
  
  public char read_wchar()
  {
    return stream.read_wchar();
  }
  
  public byte read_octet()
  {
    return stream.read_octet();
  }
  
  public short read_short()
  {
    return stream.read_short();
  }
  
  public short read_ushort()
  {
    return stream.read_ushort();
  }
  
  public int read_long()
  {
    return stream.read_long();
  }
  
  public int read_ulong()
  {
    return stream.read_ulong();
  }
  
  public long read_longlong()
  {
    return stream.read_longlong();
  }
  
  public long read_ulonglong()
  {
    return stream.read_ulonglong();
  }
  
  public float read_float()
  {
    return stream.read_float();
  }
  
  public double read_double()
  {
    return stream.read_double();
  }
  
  public String read_string()
  {
    return stream.read_string();
  }
  
  public String read_wstring()
  {
    return stream.read_wstring();
  }
  
  public void read_boolean_array(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2)
  {
    stream.read_boolean_array(paramArrayOfBoolean, paramInt1, paramInt2);
  }
  
  public void read_char_array(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    stream.read_char_array(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void read_wchar_array(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    stream.read_wchar_array(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void read_octet_array(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    stream.read_octet_array(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public void read_short_array(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    stream.read_short_array(paramArrayOfShort, paramInt1, paramInt2);
  }
  
  public void read_ushort_array(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    stream.read_ushort_array(paramArrayOfShort, paramInt1, paramInt2);
  }
  
  public void read_long_array(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    stream.read_long_array(paramArrayOfInt, paramInt1, paramInt2);
  }
  
  public void read_ulong_array(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    stream.read_ulong_array(paramArrayOfInt, paramInt1, paramInt2);
  }
  
  public void read_longlong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2)
  {
    stream.read_longlong_array(paramArrayOfLong, paramInt1, paramInt2);
  }
  
  public void read_ulonglong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2)
  {
    stream.read_ulonglong_array(paramArrayOfLong, paramInt1, paramInt2);
  }
  
  public void read_float_array(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    stream.read_float_array(paramArrayOfFloat, paramInt1, paramInt2);
  }
  
  public void read_double_array(double[] paramArrayOfDouble, int paramInt1, int paramInt2)
  {
    stream.read_double_array(paramArrayOfDouble, paramInt1, paramInt2);
  }
  
  public Object read_Object()
  {
    return stream.read_Object();
  }
  
  public Serializable read_value()
  {
    return stream.read_value();
  }
  
  public TypeCode read_TypeCode()
  {
    return stream.read_TypeCode();
  }
  
  public Any read_any()
  {
    return stream.read_any();
  }
  
  public Principal read_Principal()
  {
    return stream.read_Principal();
  }
  
  public BigDecimal read_fixed()
  {
    return stream.read_fixed();
  }
  
  public Context read_Context()
  {
    return stream.read_Context();
  }
  
  public ORB orb()
  {
    return stream.orb();
  }
  
  public void addTypeCodeAtPosition(TypeCodeImpl paramTypeCodeImpl, int paramInt)
  {
    if (typeMap == null) {
      typeMap = new HashMap(16);
    }
    typeMap.put(new Integer(paramInt), paramTypeCodeImpl);
  }
  
  public TypeCodeImpl getTypeCodeAtPosition(int paramInt)
  {
    if (typeMap == null) {
      return null;
    }
    return (TypeCodeImpl)typeMap.get(new Integer(paramInt));
  }
  
  public void setEnclosingInputStream(InputStream paramInputStream) {}
  
  public TypeCodeReader getTopLevelStream()
  {
    return this;
  }
  
  public int getTopLevelPosition()
  {
    return getPosition() - startPos;
  }
  
  public void performORBVersionSpecificInit()
  {
    stream.performORBVersionSpecificInit();
  }
  
  public void resetCodeSetConverters()
  {
    stream.resetCodeSetConverters();
  }
  
  public void printTypeMap()
  {
    System.out.println("typeMap = {");
    ArrayList localArrayList = new ArrayList(typeMap.keySet());
    Collections.sort(localArrayList);
    Iterator localIterator = localArrayList.iterator();
    while (localIterator.hasNext())
    {
      Integer localInteger = (Integer)localIterator.next();
      TypeCodeImpl localTypeCodeImpl = (TypeCodeImpl)typeMap.get(localInteger);
      System.out.println("  key = " + localInteger.intValue() + ", value = " + localTypeCodeImpl.description());
    }
    System.out.println("}");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\encoding\WrapperInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */