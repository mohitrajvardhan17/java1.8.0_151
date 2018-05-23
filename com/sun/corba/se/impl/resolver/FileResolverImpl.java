package com.sun.corba.se.impl.resolver;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.resolver.Resolver;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class FileResolverImpl
  implements Resolver
{
  private ORB orb;
  private File file;
  private Properties savedProps;
  private long fileModified = 0L;
  
  public FileResolverImpl(ORB paramORB, File paramFile)
  {
    orb = paramORB;
    file = paramFile;
    savedProps = new Properties();
  }
  
  public org.omg.CORBA.Object resolve(String paramString)
  {
    check();
    String str = savedProps.getProperty(paramString);
    if (str == null) {
      return null;
    }
    return orb.string_to_object(str);
  }
  
  public Set list()
  {
    check();
    HashSet localHashSet = new HashSet();
    Enumeration localEnumeration = savedProps.propertyNames();
    while (localEnumeration.hasMoreElements()) {
      localHashSet.add(localEnumeration.nextElement());
    }
    return localHashSet;
  }
  
  private void check()
  {
    if (file == null) {
      return;
    }
    long l = file.lastModified();
    if (l > fileModified) {
      try
      {
        FileInputStream localFileInputStream = new FileInputStream(file);
        savedProps.clear();
        savedProps.load(localFileInputStream);
        localFileInputStream.close();
        fileModified = l;
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        System.err.println(CorbaResourceUtil.getText("bootstrap.filenotfound", file.getAbsolutePath()));
      }
      catch (IOException localIOException)
      {
        System.err.println(CorbaResourceUtil.getText("bootstrap.exception", file.getAbsolutePath(), localIOException.toString()));
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\resolver\FileResolverImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */