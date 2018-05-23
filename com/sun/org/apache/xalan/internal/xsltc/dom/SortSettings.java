package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import java.text.Collator;
import java.util.Locale;

final class SortSettings
{
  private AbstractTranslet _translet;
  private int[] _sortOrders;
  private int[] _types;
  private Locale[] _locales;
  private Collator[] _collators;
  private String[] _caseOrders;
  
  SortSettings(AbstractTranslet paramAbstractTranslet, int[] paramArrayOfInt1, int[] paramArrayOfInt2, Locale[] paramArrayOfLocale, Collator[] paramArrayOfCollator, String[] paramArrayOfString)
  {
    _translet = paramAbstractTranslet;
    _sortOrders = paramArrayOfInt1;
    _types = paramArrayOfInt2;
    _locales = paramArrayOfLocale;
    _collators = paramArrayOfCollator;
    _caseOrders = paramArrayOfString;
  }
  
  AbstractTranslet getTranslet()
  {
    return _translet;
  }
  
  int[] getSortOrders()
  {
    return _sortOrders;
  }
  
  int[] getTypes()
  {
    return _types;
  }
  
  Locale[] getLocales()
  {
    return _locales;
  }
  
  Collator[] getCollators()
  {
    return _collators;
  }
  
  String[] getCaseOrders()
  {
    return _caseOrders;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\SortSettings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */