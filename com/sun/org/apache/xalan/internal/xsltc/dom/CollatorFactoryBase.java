package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.CollatorFactory;
import java.text.Collator;
import java.util.Locale;

public class CollatorFactoryBase
  implements CollatorFactory
{
  public static final Locale DEFAULT_LOCALE = ;
  public static final Collator DEFAULT_COLLATOR = Collator.getInstance();
  
  public CollatorFactoryBase() {}
  
  public Collator getCollator(String paramString1, String paramString2)
  {
    return Collator.getInstance(new Locale(paramString1, paramString2));
  }
  
  public Collator getCollator(Locale paramLocale)
  {
    if (paramLocale == DEFAULT_LOCALE) {
      return DEFAULT_COLLATOR;
    }
    return Collator.getInstance(paramLocale);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\CollatorFactoryBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */