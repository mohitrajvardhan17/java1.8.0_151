package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xalan.internal.xsltc.CollatorFactory;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.utils.StringComparable;
import java.text.Collator;
import java.util.Locale;

public abstract class NodeSortRecord
{
  public static final int COMPARE_STRING = 0;
  public static final int COMPARE_NUMERIC = 1;
  public static final int COMPARE_ASCENDING = 0;
  public static final int COMPARE_DESCENDING = 1;
  /**
   * @deprecated
   */
  private static final Collator DEFAULT_COLLATOR = ;
  /**
   * @deprecated
   */
  protected Collator _collator = DEFAULT_COLLATOR;
  protected Collator[] _collators;
  /**
   * @deprecated
   */
  protected Locale _locale;
  protected CollatorFactory _collatorFactory;
  protected SortSettings _settings;
  private DOM _dom = null;
  private int _node;
  private int _last = 0;
  private int _scanned = 0;
  private Object[] _values;
  
  public NodeSortRecord(int paramInt)
  {
    _node = paramInt;
  }
  
  public NodeSortRecord()
  {
    this(0);
  }
  
  public final void initialize(int paramInt1, int paramInt2, DOM paramDOM, SortSettings paramSortSettings)
    throws TransletException
  {
    _dom = paramDOM;
    _node = paramInt1;
    _last = paramInt2;
    _settings = paramSortSettings;
    int i = paramSortSettings.getSortOrders().length;
    _values = new Object[i];
    String str = null;
    try
    {
      str = SecuritySupport.getSystemProperty("com.sun.org.apache.xalan.internal.xsltc.COLLATOR_FACTORY");
    }
    catch (SecurityException localSecurityException) {}
    if (str != null)
    {
      try
      {
        Class localClass = ObjectFactory.findProviderClass(str, true);
        _collatorFactory = ((CollatorFactory)localClass);
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        throw new TransletException(localClassNotFoundException);
      }
      Locale[] arrayOfLocale = paramSortSettings.getLocales();
      _collators = new Collator[i];
      for (int j = 0; j < i; j++) {
        _collators[j] = _collatorFactory.getCollator(arrayOfLocale[j]);
      }
      _collator = _collators[0];
    }
    else
    {
      _collators = paramSortSettings.getCollators();
      _collator = _collators[0];
    }
  }
  
  public final int getNode()
  {
    return _node;
  }
  
  public final int compareDocOrder(NodeSortRecord paramNodeSortRecord)
  {
    return _node - _node;
  }
  
  private final Comparable stringValue(int paramInt)
  {
    if (_scanned <= paramInt)
    {
      AbstractTranslet localAbstractTranslet = _settings.getTranslet();
      Locale[] arrayOfLocale = _settings.getLocales();
      String[] arrayOfString = _settings.getCaseOrders();
      String str = extractValueFromDOM(_dom, _node, paramInt, localAbstractTranslet, _last);
      Comparable localComparable = StringComparable.getComparator(str, arrayOfLocale[paramInt], _collators[paramInt], arrayOfString[paramInt]);
      _values[(_scanned++)] = localComparable;
      return localComparable;
    }
    return (Comparable)_values[paramInt];
  }
  
  private final Double numericValue(int paramInt)
  {
    if (_scanned <= paramInt)
    {
      AbstractTranslet localAbstractTranslet = _settings.getTranslet();
      String str = extractValueFromDOM(_dom, _node, paramInt, localAbstractTranslet, _last);
      Double localDouble;
      try
      {
        localDouble = new Double(str);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        localDouble = new Double(Double.NEGATIVE_INFINITY);
      }
      _values[(_scanned++)] = localDouble;
      return localDouble;
    }
    return (Double)_values[paramInt];
  }
  
  public int compareTo(NodeSortRecord paramNodeSortRecord)
  {
    int[] arrayOfInt1 = _settings.getSortOrders();
    int k = _settings.getSortOrders().length;
    int[] arrayOfInt2 = _settings.getTypes();
    for (int j = 0; j < k; j++)
    {
      Object localObject1;
      Object localObject2;
      int i;
      if (arrayOfInt2[j] == 1)
      {
        localObject1 = numericValue(j);
        localObject2 = paramNodeSortRecord.numericValue(j);
        i = ((Double)localObject1).compareTo((Double)localObject2);
      }
      else
      {
        localObject1 = stringValue(j);
        localObject2 = paramNodeSortRecord.stringValue(j);
        i = ((Comparable)localObject1).compareTo(localObject2);
      }
      if (i != 0) {
        return arrayOfInt1[j] == 1 ? 0 - i : i;
      }
    }
    return _node - _node;
  }
  
  public Collator[] getCollator()
  {
    return _collators;
  }
  
  public abstract String extractValueFromDOM(DOM paramDOM, int paramInt1, int paramInt2, AbstractTranslet paramAbstractTranslet, int paramInt3);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\NodeSortRecord.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */