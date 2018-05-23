package com.sun.org.apache.xalan.internal.res;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import java.util.ListResourceBundle;

public class XSLMessages
  extends XPATHMessages
{
  private static ListResourceBundle XSLTBundle = null;
  private static final String XSLT_ERROR_RESOURCES = "com.sun.org.apache.xalan.internal.res.XSLTErrorResources";
  
  public XSLMessages() {}
  
  public static String createMessage(String paramString, Object[] paramArrayOfObject)
  {
    if (XSLTBundle == null) {
      XSLTBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xalan.internal.res.XSLTErrorResources");
    }
    if (XSLTBundle != null) {
      return createMsg(XSLTBundle, paramString, paramArrayOfObject);
    }
    return "Could not load any resource bundles.";
  }
  
  public static String createWarning(String paramString, Object[] paramArrayOfObject)
  {
    if (XSLTBundle == null) {
      XSLTBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xalan.internal.res.XSLTErrorResources");
    }
    if (XSLTBundle != null) {
      return createMsg(XSLTBundle, paramString, paramArrayOfObject);
    }
    return "Could not load any resource bundles.";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\res\XSLMessages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */