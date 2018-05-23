package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.encoding.CDROutputStream;
import com.sun.corba.se.impl.encoding.EncapsInputStream;
import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.spi.ior.Identifiable;
import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
import com.sun.corba.se.spi.ior.WriteContents;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Iterator;
import java.util.List;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import sun.corba.EncapsInputStreamFactory;
import sun.corba.OutputStreamFactory;

public class EncapsulationUtility
{
  private EncapsulationUtility() {}
  
  public static void readIdentifiableSequence(List paramList, IdentifiableFactoryFinder paramIdentifiableFactoryFinder, InputStream paramInputStream)
  {
    int i = paramInputStream.read_long();
    for (int j = 0; j < i; j++)
    {
      int k = paramInputStream.read_long();
      Identifiable localIdentifiable = paramIdentifiableFactoryFinder.create(k, paramInputStream);
      paramList.add(localIdentifiable);
    }
  }
  
  public static void writeIdentifiableSequence(List paramList, OutputStream paramOutputStream)
  {
    paramOutputStream.write_long(paramList.size());
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      Identifiable localIdentifiable = (Identifiable)localIterator.next();
      paramOutputStream.write_long(localIdentifiable.getId());
      localIdentifiable.write(paramOutputStream);
    }
  }
  
  public static void writeOutputStream(OutputStream paramOutputStream1, OutputStream paramOutputStream2)
  {
    byte[] arrayOfByte = ((CDROutputStream)paramOutputStream1).toByteArray();
    paramOutputStream2.write_long(arrayOfByte.length);
    paramOutputStream2.write_octet_array(arrayOfByte, 0, arrayOfByte.length);
  }
  
  public static InputStream getEncapsulationStream(InputStream paramInputStream)
  {
    byte[] arrayOfByte = readOctets(paramInputStream);
    EncapsInputStream localEncapsInputStream = EncapsInputStreamFactory.newEncapsInputStream(paramInputStream.orb(), arrayOfByte, arrayOfByte.length);
    localEncapsInputStream.consumeEndian();
    return localEncapsInputStream;
  }
  
  public static byte[] readOctets(InputStream paramInputStream)
  {
    int i = paramInputStream.read_ulong();
    byte[] arrayOfByte = new byte[i];
    paramInputStream.read_octet_array(arrayOfByte, 0, i);
    return arrayOfByte;
  }
  
  public static void writeEncapsulation(WriteContents paramWriteContents, OutputStream paramOutputStream)
  {
    EncapsOutputStream localEncapsOutputStream = OutputStreamFactory.newEncapsOutputStream((ORB)paramOutputStream.orb());
    localEncapsOutputStream.putEndian();
    paramWriteContents.writeContents(localEncapsOutputStream);
    writeOutputStream(localEncapsOutputStream, paramOutputStream);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\EncapsulationUtility.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */