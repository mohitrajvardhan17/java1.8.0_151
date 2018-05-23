package com.sun.jmx.snmp.defaults;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

public class SnmpProperties
{
  public static final String MLET_LIB_DIR = "jmx.mlet.library.dir";
  public static final String ACL_FILE = "jdmk.acl.file";
  public static final String SECURITY_FILE = "jdmk.security.file";
  public static final String UACL_FILE = "jdmk.uacl.file";
  public static final String MIB_CORE_FILE = "mibcore.file";
  public static final String JMX_SPEC_NAME = "jmx.specification.name";
  public static final String JMX_SPEC_VERSION = "jmx.specification.version";
  public static final String JMX_SPEC_VENDOR = "jmx.specification.vendor";
  public static final String JMX_IMPL_NAME = "jmx.implementation.name";
  public static final String JMX_IMPL_VENDOR = "jmx.implementation.vendor";
  public static final String JMX_IMPL_VERSION = "jmx.implementation.version";
  public static final String SSL_CIPHER_SUITE = "jdmk.ssl.cipher.suite.";
  
  private SnmpProperties() {}
  
  public static void load(String paramString)
    throws IOException
  {
    Properties localProperties = new Properties();
    FileInputStream localFileInputStream = new FileInputStream(paramString);
    localProperties.load(localFileInputStream);
    localFileInputStream.close();
    Enumeration localEnumeration = localProperties.keys();
    while (localEnumeration.hasMoreElements())
    {
      String str = (String)localEnumeration.nextElement();
      System.setProperty(str, localProperties.getProperty(str));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\defaults\SnmpProperties.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */