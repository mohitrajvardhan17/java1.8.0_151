package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.impl.encoding.MarshalInputStream;
import com.sun.corba.se.impl.encoding.MarshalOutputStream;
import com.sun.corba.se.spi.ior.TaggedComponentBase;
import com.sun.corba.se.spi.ior.iiop.CodeSetsComponent;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class CodeSetsComponentImpl
  extends TaggedComponentBase
  implements CodeSetsComponent
{
  CodeSetComponentInfo csci;
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof CodeSetsComponentImpl)) {
      return false;
    }
    CodeSetsComponentImpl localCodeSetsComponentImpl = (CodeSetsComponentImpl)paramObject;
    return csci.equals(csci);
  }
  
  public int hashCode()
  {
    return csci.hashCode();
  }
  
  public String toString()
  {
    return "CodeSetsComponentImpl[csci=" + csci + "]";
  }
  
  public CodeSetsComponentImpl()
  {
    csci = new CodeSetComponentInfo();
  }
  
  public CodeSetsComponentImpl(InputStream paramInputStream)
  {
    csci = new CodeSetComponentInfo();
    csci.read((MarshalInputStream)paramInputStream);
  }
  
  public CodeSetsComponentImpl(ORB paramORB)
  {
    if (paramORB == null) {
      csci = new CodeSetComponentInfo();
    } else {
      csci = paramORB.getORBData().getCodeSetComponentInfo();
    }
  }
  
  public CodeSetComponentInfo getCodeSetComponentInfo()
  {
    return csci;
  }
  
  public void writeContents(OutputStream paramOutputStream)
  {
    csci.write((MarshalOutputStream)paramOutputStream);
  }
  
  public int getId()
  {
    return 1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\iiop\CodeSetsComponentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */