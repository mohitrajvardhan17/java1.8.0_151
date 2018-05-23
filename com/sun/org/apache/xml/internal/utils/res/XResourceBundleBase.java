package com.sun.org.apache.xml.internal.utils.res;

import java.util.ListResourceBundle;

public abstract class XResourceBundleBase
  extends ListResourceBundle
{
  public XResourceBundleBase() {}
  
  public abstract String getMessageKey(int paramInt);
  
  public abstract String getWarningKey(int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\res\XResourceBundleBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */