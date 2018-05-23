package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.InputStream;
import sun.corba.EncapsInputStreamFactory;
import sun.corba.OutputStreamFactory;

public final class TypeCodeOutputStream
  extends EncapsOutputStream
{
  private org.omg.CORBA_2_3.portable.OutputStream enclosure = null;
  private Map typeMap = null;
  private boolean isEncapsulation = false;
  
  public TypeCodeOutputStream(com.sun.corba.se.spi.orb.ORB paramORB)
  {
    super(paramORB, false);
  }
  
  public TypeCodeOutputStream(com.sun.corba.se.spi.orb.ORB paramORB, boolean paramBoolean)
  {
    super(paramORB, paramBoolean);
  }
  
  public InputStream create_input_stream()
  {
    TypeCodeInputStream localTypeCodeInputStream = EncapsInputStreamFactory.newTypeCodeInputStream((com.sun.corba.se.spi.orb.ORB)orb(), getByteBuffer(), getIndex(), isLittleEndian(), getGIOPVersion());
    return localTypeCodeInputStream;
  }
  
  public void setEnclosingOutputStream(org.omg.CORBA_2_3.portable.OutputStream paramOutputStream)
  {
    enclosure = paramOutputStream;
  }
  
  public TypeCodeOutputStream getTopLevelStream()
  {
    if (enclosure == null) {
      return this;
    }
    if ((enclosure instanceof TypeCodeOutputStream)) {
      return ((TypeCodeOutputStream)enclosure).getTopLevelStream();
    }
    return this;
  }
  
  public int getTopLevelPosition()
  {
    if ((enclosure != null) && ((enclosure instanceof TypeCodeOutputStream)))
    {
      int i = ((TypeCodeOutputStream)enclosure).getTopLevelPosition() + getPosition();
      if (isEncapsulation) {
        i += 4;
      }
      return i;
    }
    return getPosition();
  }
  
  public void addIDAtPosition(String paramString, int paramInt)
  {
    if (typeMap == null) {
      typeMap = new HashMap(16);
    }
    typeMap.put(paramString, new Integer(paramInt));
  }
  
  public int getPositionForID(String paramString)
  {
    if (typeMap == null) {
      throw wrapper.refTypeIndirType(CompletionStatus.COMPLETED_NO);
    }
    return ((Integer)typeMap.get(paramString)).intValue();
  }
  
  public void writeRawBuffer(org.omg.CORBA.portable.OutputStream paramOutputStream, int paramInt)
  {
    paramOutputStream.write_long(paramInt);
    ByteBuffer localByteBuffer = getByteBuffer();
    if (localByteBuffer.hasArray())
    {
      paramOutputStream.write_octet_array(localByteBuffer.array(), 4, getIndex() - 4);
    }
    else
    {
      byte[] arrayOfByte = new byte[localByteBuffer.limit()];
      for (int i = 0; i < arrayOfByte.length; i++) {
        arrayOfByte[i] = localByteBuffer.get(i);
      }
      paramOutputStream.write_octet_array(arrayOfByte, 4, getIndex() - 4);
    }
  }
  
  public TypeCodeOutputStream createEncapsulation(org.omg.CORBA.ORB paramORB)
  {
    TypeCodeOutputStream localTypeCodeOutputStream = OutputStreamFactory.newTypeCodeOutputStream((com.sun.corba.se.spi.orb.ORB)paramORB, isLittleEndian());
    localTypeCodeOutputStream.setEnclosingOutputStream(this);
    localTypeCodeOutputStream.makeEncapsulation();
    return localTypeCodeOutputStream;
  }
  
  protected void makeEncapsulation()
  {
    putEndian();
    isEncapsulation = true;
  }
  
  public static TypeCodeOutputStream wrapOutputStream(org.omg.CORBA_2_3.portable.OutputStream paramOutputStream)
  {
    boolean bool = (paramOutputStream instanceof CDROutputStream) ? ((CDROutputStream)paramOutputStream).isLittleEndian() : false;
    TypeCodeOutputStream localTypeCodeOutputStream = OutputStreamFactory.newTypeCodeOutputStream((com.sun.corba.se.spi.orb.ORB)paramOutputStream.orb(), bool);
    localTypeCodeOutputStream.setEnclosingOutputStream(paramOutputStream);
    return localTypeCodeOutputStream;
  }
  
  public int getPosition()
  {
    return getIndex();
  }
  
  public int getRealIndex(int paramInt)
  {
    int i = getTopLevelPosition();
    return i;
  }
  
  public byte[] getTypeCodeBuffer()
  {
    ByteBuffer localByteBuffer = getByteBuffer();
    byte[] arrayOfByte = new byte[getIndex() - 4];
    for (int i = 0; i < arrayOfByte.length; i++) {
      arrayOfByte[i] = localByteBuffer.get(i + 4);
    }
    return arrayOfByte;
  }
  
  public void printTypeMap()
  {
    System.out.println("typeMap = {");
    Iterator localIterator = typeMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      Integer localInteger = (Integer)typeMap.get(str);
      System.out.println("  key = " + str + ", value = " + localInteger);
    }
    System.out.println("}");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\encoding\TypeCodeOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */