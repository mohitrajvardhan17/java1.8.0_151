package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.utils.LocaleUtility;
import java.text.Collator;
import java.util.Locale;

public class NodeSortRecordFactory
{
  private static int DESCENDING = "descending".length();
  private static int NUMBER = "number".length();
  private final DOM _dom;
  private final String _className;
  private Class _class;
  private SortSettings _sortSettings;
  protected Collator _collator;
  
  /**
   * @deprecated
   */
  public NodeSortRecordFactory(DOM paramDOM, String paramString, Translet paramTranslet, String[] paramArrayOfString1, String[] paramArrayOfString2)
    throws TransletException
  {
    this(paramDOM, paramString, paramTranslet, paramArrayOfString1, paramArrayOfString2, null, null);
  }
  
  public NodeSortRecordFactory(DOM paramDOM, String paramString, Translet paramTranslet, String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3, String[] paramArrayOfString4)
    throws TransletException
  {
    try
    {
      _dom = paramDOM;
      _className = paramString;
      _class = paramTranslet.getAuxiliaryClass(paramString);
      if (_class == null) {
        _class = ObjectFactory.findProviderClass(paramString, true);
      }
      int i = paramArrayOfString1.length;
      int[] arrayOfInt1 = new int[i];
      int[] arrayOfInt2 = new int[i];
      for (int j = 0; j < i; j++)
      {
        if (paramArrayOfString1[j].length() == DESCENDING) {
          arrayOfInt1[j] = 1;
        }
        if (paramArrayOfString2[j].length() == NUMBER) {
          arrayOfInt2[j] = 1;
        }
      }
      String[] arrayOfString = null;
      if ((paramArrayOfString3 == null) || (paramArrayOfString4 == null))
      {
        k = paramArrayOfString1.length;
        arrayOfString = new String[k];
        for (int m = 0; m < k; m++) {
          arrayOfString[m] = "";
        }
      }
      if (paramArrayOfString3 == null) {
        paramArrayOfString3 = arrayOfString;
      }
      if (paramArrayOfString4 == null) {
        paramArrayOfString4 = arrayOfString;
      }
      int k = paramArrayOfString3.length;
      Locale[] arrayOfLocale = new Locale[k];
      Collator[] arrayOfCollator = new Collator[k];
      for (int n = 0; n < k; n++)
      {
        arrayOfLocale[n] = LocaleUtility.langToLocale(paramArrayOfString3[n]);
        arrayOfCollator[n] = Collator.getInstance(arrayOfLocale[n]);
      }
      _sortSettings = new SortSettings((AbstractTranslet)paramTranslet, arrayOfInt1, arrayOfInt2, arrayOfLocale, arrayOfCollator, paramArrayOfString4);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new TransletException(localClassNotFoundException);
    }
  }
  
  public NodeSortRecord makeNodeSortRecord(int paramInt1, int paramInt2)
    throws ExceptionInInitializerError, LinkageError, IllegalAccessException, InstantiationException, SecurityException, TransletException
  {
    NodeSortRecord localNodeSortRecord = (NodeSortRecord)_class.newInstance();
    localNodeSortRecord.initialize(paramInt1, paramInt2, _dom, _sortSettings);
    return localNodeSortRecord;
  }
  
  public String getClassName()
  {
    return _className;
  }
  
  private final void setLang(String[] paramArrayOfString) {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\NodeSortRecordFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */