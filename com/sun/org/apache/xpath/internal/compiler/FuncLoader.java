package com.sun.org.apache.xpath.internal.compiler;

import com.sun.org.apache.xalan.internal.utils.ConfigurationError;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xpath.internal.functions.Function;
import javax.xml.transform.TransformerException;

public class FuncLoader
{
  private int m_funcID;
  private String m_funcName;
  
  public String getName()
  {
    return m_funcName;
  }
  
  public FuncLoader(String paramString, int paramInt)
  {
    m_funcID = paramInt;
    m_funcName = paramString;
  }
  
  Function getFunction()
    throws TransformerException
  {
    try
    {
      String str1 = m_funcName;
      if (str1.indexOf(".") < 0) {
        str1 = "com.sun.org.apache.xpath.internal.functions." + str1;
      }
      String str2 = str1.substring(0, str1.lastIndexOf('.'));
      if ((!str2.equals("com.sun.org.apache.xalan.internal.templates")) && (!str2.equals("com.sun.org.apache.xpath.internal.functions"))) {
        throw new TransformerException("Application can't install his own xpath function.");
      }
      return (Function)ObjectFactory.newInstance(str1, true);
    }
    catch (ConfigurationError localConfigurationError)
    {
      throw new TransformerException(localConfigurationError.getException());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\compiler\FuncLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */