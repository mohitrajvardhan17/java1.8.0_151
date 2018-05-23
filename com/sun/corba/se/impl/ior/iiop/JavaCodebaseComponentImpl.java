package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.spi.ior.TaggedComponentBase;
import com.sun.corba.se.spi.ior.iiop.JavaCodebaseComponent;
import org.omg.CORBA_2_3.portable.OutputStream;

public class JavaCodebaseComponentImpl
  extends TaggedComponentBase
  implements JavaCodebaseComponent
{
  private String URLs;
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (!(paramObject instanceof JavaCodebaseComponentImpl)) {
      return false;
    }
    JavaCodebaseComponentImpl localJavaCodebaseComponentImpl = (JavaCodebaseComponentImpl)paramObject;
    return URLs.equals(localJavaCodebaseComponentImpl.getURLs());
  }
  
  public int hashCode()
  {
    return URLs.hashCode();
  }
  
  public String toString()
  {
    return "JavaCodebaseComponentImpl[URLs=" + URLs + "]";
  }
  
  public String getURLs()
  {
    return URLs;
  }
  
  public JavaCodebaseComponentImpl(String paramString)
  {
    URLs = paramString;
  }
  
  public void writeContents(OutputStream paramOutputStream)
  {
    paramOutputStream.write_string(URLs);
  }
  
  public int getId()
  {
    return 25;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\iiop\JavaCodebaseComponentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */