package com.sun.org.apache.xalan.internal.xsltc;

import java.text.Collator;
import java.util.Locale;

public abstract interface CollatorFactory
{
  public abstract Collator getCollator(String paramString1, String paramString2);
  
  public abstract Collator getCollator(Locale paramLocale);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\CollatorFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */