package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.corba.TypeCodeImpl;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.omg.CORBA_2_3.portable.InputStream;
import sun.corba.EncapsInputStreamFactory;

public class TypeCodeInputStream
  extends EncapsInputStream
  implements TypeCodeReader
{
  private Map typeMap = null;
  private InputStream enclosure = null;
  private boolean isEncapsulation = false;
  
  public TypeCodeInputStream(org.omg.CORBA.ORB paramORB, byte[] paramArrayOfByte, int paramInt)
  {
    super(paramORB, paramArrayOfByte, paramInt);
  }
  
  public TypeCodeInputStream(org.omg.CORBA.ORB paramORB, byte[] paramArrayOfByte, int paramInt, boolean paramBoolean, GIOPVersion paramGIOPVersion)
  {
    super(paramORB, paramArrayOfByte, paramInt, paramBoolean, paramGIOPVersion);
  }
  
  public TypeCodeInputStream(org.omg.CORBA.ORB paramORB, ByteBuffer paramByteBuffer, int paramInt, boolean paramBoolean, GIOPVersion paramGIOPVersion)
  {
    super(paramORB, paramByteBuffer, paramInt, paramBoolean, paramGIOPVersion);
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
  
  public void setEnclosingInputStream(InputStream paramInputStream)
  {
    enclosure = paramInputStream;
  }
  
  public TypeCodeReader getTopLevelStream()
  {
    if (enclosure == null) {
      return this;
    }
    if ((enclosure instanceof TypeCodeReader)) {
      return ((TypeCodeReader)enclosure).getTopLevelStream();
    }
    return this;
  }
  
  public int getTopLevelPosition()
  {
    if ((enclosure != null) && ((enclosure instanceof TypeCodeReader)))
    {
      int i = ((TypeCodeReader)enclosure).getTopLevelPosition();
      int j = i - getBufferLength() + getPosition();
      return j;
    }
    return getPosition();
  }
  
  public static TypeCodeInputStream readEncapsulation(InputStream paramInputStream, org.omg.CORBA.ORB paramORB)
  {
    int i = paramInputStream.read_long();
    byte[] arrayOfByte = new byte[i];
    paramInputStream.read_octet_array(arrayOfByte, 0, arrayOfByte.length);
    TypeCodeInputStream localTypeCodeInputStream;
    if ((paramInputStream instanceof CDRInputStream)) {
      localTypeCodeInputStream = EncapsInputStreamFactory.newTypeCodeInputStream((com.sun.corba.se.spi.orb.ORB)paramORB, arrayOfByte, arrayOfByte.length, ((CDRInputStream)paramInputStream).isLittleEndian(), ((CDRInputStream)paramInputStream).getGIOPVersion());
    } else {
      localTypeCodeInputStream = EncapsInputStreamFactory.newTypeCodeInputStream((com.sun.corba.se.spi.orb.ORB)paramORB, arrayOfByte, arrayOfByte.length);
    }
    localTypeCodeInputStream.setEnclosingInputStream(paramInputStream);
    localTypeCodeInputStream.makeEncapsulation();
    return localTypeCodeInputStream;
  }
  
  protected void makeEncapsulation()
  {
    consumeEndian();
    isEncapsulation = true;
  }
  
  public void printTypeMap()
  {
    System.out.println("typeMap = {");
    Iterator localIterator = typeMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      Integer localInteger = (Integer)localIterator.next();
      TypeCodeImpl localTypeCodeImpl = (TypeCodeImpl)typeMap.get(localInteger);
      System.out.println("  key = " + localInteger.intValue() + ", value = " + localTypeCodeImpl.description());
    }
    System.out.println("}");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\encoding\TypeCodeInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */