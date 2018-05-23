package org.xml.sax.helpers;

class NewInstance
{
  private static final String DEFAULT_PACKAGE = "com.sun.org.apache.xerces.internal";
  
  NewInstance() {}
  
  static Object newInstance(ClassLoader paramClassLoader, String paramString)
    throws ClassNotFoundException, IllegalAccessException, InstantiationException
  {
    int i = 0;
    if ((System.getSecurityManager() != null) && (paramString != null) && (paramString.startsWith("com.sun.org.apache.xerces.internal"))) {
      i = 1;
    }
    Class localClass;
    if ((paramClassLoader == null) || (i != 0)) {
      localClass = Class.forName(paramString);
    } else {
      localClass = paramClassLoader.loadClass(paramString);
    }
    return localClass.newInstance();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\xml\sax\helpers\NewInstance.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */