package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.xml.internal.stream.XMLBufferListener;

public class XMLAttributesImpl
  implements XMLAttributes, XMLBufferListener
{
  protected static final int TABLE_SIZE = 101;
  protected static final int MAX_HASH_COLLISIONS = 40;
  protected static final int MULTIPLIERS_SIZE = 32;
  protected static final int MULTIPLIERS_MASK = 31;
  protected static final int SIZE_LIMIT = 20;
  protected boolean fNamespaces = true;
  protected int fLargeCount = 1;
  protected int fLength;
  protected Attribute[] fAttributes = new Attribute[4];
  protected Attribute[] fAttributeTableView;
  protected int[] fAttributeTableViewChainState;
  protected int fTableViewBuckets;
  protected boolean fIsTableViewConsistent;
  protected int[] fHashMultipliers;
  
  public XMLAttributesImpl()
  {
    this(101);
  }
  
  public XMLAttributesImpl(int paramInt)
  {
    fTableViewBuckets = paramInt;
    for (int i = 0; i < fAttributes.length; i++) {
      fAttributes[i] = new Attribute();
    }
  }
  
  public void setNamespaces(boolean paramBoolean)
  {
    fNamespaces = paramBoolean;
  }
  
  public int addAttribute(QName paramQName, String paramString1, String paramString2)
  {
    return addAttribute(paramQName, paramString1, paramString2, null);
  }
  
  public int addAttribute(QName paramQName, String paramString1, String paramString2, XMLString paramXMLString)
  {
    int i;
    if (fLength < 20)
    {
      i = (uri != null) && (!uri.equals("")) ? getIndexFast(uri, localpart) : getIndexFast(rawname);
      if (i == -1)
      {
        i = fLength;
        if (fLength++ == fAttributes.length)
        {
          Attribute[] arrayOfAttribute1 = new Attribute[fAttributes.length + 4];
          System.arraycopy(fAttributes, 0, arrayOfAttribute1, 0, fAttributes.length);
          for (int k = fAttributes.length; k < arrayOfAttribute1.length; k++) {
            arrayOfAttribute1[k] = new Attribute();
          }
          fAttributes = arrayOfAttribute1;
        }
      }
    }
    else if ((uri == null) || (uri.length() == 0) || ((i = getIndexFast(uri, localpart)) == -1))
    {
      if ((!fIsTableViewConsistent) || (fLength == 20) || ((fLength > 20) && (fLength > fTableViewBuckets)))
      {
        prepareAndPopulateTableView();
        fIsTableViewConsistent = true;
      }
      int j = getTableViewBucket(rawname);
      if (fAttributeTableViewChainState[j] != fLargeCount)
      {
        i = fLength;
        if (fLength++ == fAttributes.length)
        {
          Attribute[] arrayOfAttribute2 = new Attribute[fAttributes.length << 1];
          System.arraycopy(fAttributes, 0, arrayOfAttribute2, 0, fAttributes.length);
          for (int n = fAttributes.length; n < arrayOfAttribute2.length; n++) {
            arrayOfAttribute2[n] = new Attribute();
          }
          fAttributes = arrayOfAttribute2;
        }
        fAttributeTableViewChainState[j] = fLargeCount;
        fAttributes[i].next = null;
        fAttributeTableView[j] = fAttributes[i];
      }
      else
      {
        int m = 0;
        Attribute localAttribute2 = fAttributeTableView[j];
        while ((localAttribute2 != null) && (name.rawname != rawname))
        {
          localAttribute2 = next;
          m++;
        }
        if (localAttribute2 == null)
        {
          i = fLength;
          if (fLength++ == fAttributes.length)
          {
            Attribute[] arrayOfAttribute3 = new Attribute[fAttributes.length << 1];
            System.arraycopy(fAttributes, 0, arrayOfAttribute3, 0, fAttributes.length);
            for (int i1 = fAttributes.length; i1 < arrayOfAttribute3.length; i1++) {
              arrayOfAttribute3[i1] = new Attribute();
            }
            fAttributes = arrayOfAttribute3;
          }
          if (m >= 40)
          {
            fAttributes[i].name.setValues(paramQName);
            rebalanceTableView(fLength);
          }
          else
          {
            fAttributes[i].next = fAttributeTableView[j];
            fAttributeTableView[j] = fAttributes[i];
          }
        }
        else
        {
          i = getIndexFast(rawname);
        }
      }
    }
    Attribute localAttribute1 = fAttributes[i];
    name.setValues(paramQName);
    type = paramString1;
    value = paramString2;
    xmlValue = paramXMLString;
    nonNormalizedValue = paramString2;
    specified = false;
    if (augs != null) {
      augs.removeAllItems();
    }
    return i;
  }
  
  public void removeAllAttributes()
  {
    fLength = 0;
  }
  
  public void removeAttributeAt(int paramInt)
  {
    fIsTableViewConsistent = false;
    if (paramInt < fLength - 1)
    {
      Attribute localAttribute = fAttributes[paramInt];
      System.arraycopy(fAttributes, paramInt + 1, fAttributes, paramInt, fLength - paramInt - 1);
      fAttributes[(fLength - 1)] = localAttribute;
    }
    fLength -= 1;
  }
  
  public void setName(int paramInt, QName paramQName)
  {
    fAttributes[paramInt].name.setValues(paramQName);
  }
  
  public void getName(int paramInt, QName paramQName)
  {
    paramQName.setValues(fAttributes[paramInt].name);
  }
  
  public void setType(int paramInt, String paramString)
  {
    fAttributes[paramInt].type = paramString;
  }
  
  public void setValue(int paramInt, String paramString)
  {
    setValue(paramInt, paramString, null);
  }
  
  public void setValue(int paramInt, String paramString, XMLString paramXMLString)
  {
    Attribute localAttribute = fAttributes[paramInt];
    value = paramString;
    nonNormalizedValue = paramString;
    xmlValue = paramXMLString;
  }
  
  public void setNonNormalizedValue(int paramInt, String paramString)
  {
    if (paramString == null) {
      paramString = fAttributes[paramInt].value;
    }
    fAttributes[paramInt].nonNormalizedValue = paramString;
  }
  
  public String getNonNormalizedValue(int paramInt)
  {
    String str = fAttributes[paramInt].nonNormalizedValue;
    return str;
  }
  
  public void setSpecified(int paramInt, boolean paramBoolean)
  {
    fAttributes[paramInt].specified = paramBoolean;
  }
  
  public boolean isSpecified(int paramInt)
  {
    return fAttributes[paramInt].specified;
  }
  
  public int getLength()
  {
    return fLength;
  }
  
  public String getType(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= fLength)) {
      return null;
    }
    return getReportableType(fAttributes[paramInt].type);
  }
  
  public String getType(String paramString)
  {
    int i = getIndex(paramString);
    return i != -1 ? getReportableType(fAttributes[i].type) : null;
  }
  
  public String getValue(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= fLength)) {
      return null;
    }
    if ((fAttributes[paramInt].value == null) && (fAttributes[paramInt].xmlValue != null)) {
      fAttributes[paramInt].value = fAttributes[paramInt].xmlValue.toString();
    }
    return fAttributes[paramInt].value;
  }
  
  public String getValue(String paramString)
  {
    int i = getIndex(paramString);
    if (i == -1) {
      return null;
    }
    if (fAttributes[i].value == null) {
      fAttributes[i].value = fAttributes[i].xmlValue.toString();
    }
    return fAttributes[i].value;
  }
  
  public String getName(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= fLength)) {
      return null;
    }
    return fAttributes[paramInt].name.rawname;
  }
  
  public int getIndex(String paramString)
  {
    for (int i = 0; i < fLength; i++)
    {
      Attribute localAttribute = fAttributes[i];
      if ((name.rawname != null) && (name.rawname.equals(paramString))) {
        return i;
      }
    }
    return -1;
  }
  
  public int getIndex(String paramString1, String paramString2)
  {
    for (int i = 0; i < fLength; i++)
    {
      Attribute localAttribute = fAttributes[i];
      if ((name.localpart != null) && (name.localpart.equals(paramString2)) && ((paramString1 == name.uri) || ((paramString1 != null) && (name.uri != null) && (name.uri.equals(paramString1))))) {
        return i;
      }
    }
    return -1;
  }
  
  public int getIndexByLocalName(String paramString)
  {
    for (int i = 0; i < fLength; i++)
    {
      Attribute localAttribute = fAttributes[i];
      if ((name.localpart != null) && (name.localpart.equals(paramString))) {
        return i;
      }
    }
    return -1;
  }
  
  public String getLocalName(int paramInt)
  {
    if (!fNamespaces) {
      return "";
    }
    if ((paramInt < 0) || (paramInt >= fLength)) {
      return null;
    }
    return fAttributes[paramInt].name.localpart;
  }
  
  public String getQName(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= fLength)) {
      return null;
    }
    String str = fAttributes[paramInt].name.rawname;
    return str != null ? str : "";
  }
  
  public QName getQualifiedName(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= fLength)) {
      return null;
    }
    return fAttributes[paramInt].name;
  }
  
  public String getType(String paramString1, String paramString2)
  {
    if (!fNamespaces) {
      return null;
    }
    int i = getIndex(paramString1, paramString2);
    return i != -1 ? getType(i) : null;
  }
  
  public int getIndexFast(String paramString)
  {
    for (int i = 0; i < fLength; i++)
    {
      Attribute localAttribute = fAttributes[i];
      if (name.rawname == paramString) {
        return i;
      }
    }
    return -1;
  }
  
  public void addAttributeNS(QName paramQName, String paramString1, String paramString2)
  {
    int i = fLength;
    if (fLength++ == fAttributes.length)
    {
      if (fLength < 20) {
        localObject = new Attribute[fAttributes.length + 4];
      } else {
        localObject = new Attribute[fAttributes.length << 1];
      }
      System.arraycopy(fAttributes, 0, localObject, 0, fAttributes.length);
      for (int j = fAttributes.length; j < localObject.length; j++) {
        localObject[j] = new Attribute();
      }
      fAttributes = ((Attribute[])localObject);
    }
    Object localObject = fAttributes[i];
    name.setValues(paramQName);
    type = paramString1;
    value = paramString2;
    nonNormalizedValue = paramString2;
    specified = false;
    augs.removeAllItems();
  }
  
  public QName checkDuplicatesNS()
  {
    int i = fLength;
    if (i <= 20)
    {
      Attribute[] arrayOfAttribute = fAttributes;
      for (int j = 0; j < i - 1; j++)
      {
        Attribute localAttribute1 = arrayOfAttribute[j];
        for (int k = j + 1; k < i; k++)
        {
          Attribute localAttribute2 = arrayOfAttribute[k];
          if ((name.localpart == name.localpart) && (name.uri == name.uri)) {
            return name;
          }
        }
      }
      return null;
    }
    return checkManyDuplicatesNS();
  }
  
  private QName checkManyDuplicatesNS()
  {
    fIsTableViewConsistent = false;
    prepareTableView();
    int j = fLength;
    Attribute[] arrayOfAttribute1 = fAttributes;
    Attribute[] arrayOfAttribute2 = fAttributeTableView;
    int[] arrayOfInt = fAttributeTableViewChainState;
    int k = fLargeCount;
    for (int m = 0; m < j; m++)
    {
      Attribute localAttribute1 = arrayOfAttribute1[m];
      int i = getTableViewBucket(name.localpart, name.uri);
      if (arrayOfInt[i] != k)
      {
        arrayOfInt[i] = k;
        next = null;
        arrayOfAttribute2[i] = localAttribute1;
      }
      else
      {
        int n = 0;
        Attribute localAttribute2 = arrayOfAttribute2[i];
        while (localAttribute2 != null)
        {
          if ((name.localpart == name.localpart) && (name.uri == name.uri)) {
            return name;
          }
          localAttribute2 = next;
          n++;
        }
        if (n >= 40)
        {
          rebalanceTableViewNS(m + 1);
          k = fLargeCount;
        }
        else
        {
          next = arrayOfAttribute2[i];
          arrayOfAttribute2[i] = localAttribute1;
        }
      }
    }
    return null;
  }
  
  public int getIndexFast(String paramString1, String paramString2)
  {
    for (int i = 0; i < fLength; i++)
    {
      Attribute localAttribute = fAttributes[i];
      if ((name.localpart == paramString2) && (name.uri == paramString1)) {
        return i;
      }
    }
    return -1;
  }
  
  private String getReportableType(String paramString)
  {
    if (paramString.charAt(0) == '(') {
      return "NMTOKEN";
    }
    return paramString;
  }
  
  protected int getTableViewBucket(String paramString)
  {
    return (hash(paramString) & 0x7FFFFFFF) % fTableViewBuckets;
  }
  
  protected int getTableViewBucket(String paramString1, String paramString2)
  {
    if (paramString2 == null) {
      return (hash(paramString1) & 0x7FFFFFFF) % fTableViewBuckets;
    }
    return (hash(paramString1, paramString2) & 0x7FFFFFFF) % fTableViewBuckets;
  }
  
  private int hash(String paramString)
  {
    if (fHashMultipliers == null) {
      return paramString.hashCode();
    }
    return hash0(paramString);
  }
  
  private int hash(String paramString1, String paramString2)
  {
    if (fHashMultipliers == null) {
      return paramString1.hashCode() + paramString2.hashCode() * 31;
    }
    return hash0(paramString1) + hash0(paramString2) * fHashMultipliers[32];
  }
  
  private int hash0(String paramString)
  {
    int i = 0;
    int j = paramString.length();
    int[] arrayOfInt = fHashMultipliers;
    for (int k = 0; k < j; k++) {
      i = i * arrayOfInt[(k & 0x1F)] + paramString.charAt(k);
    }
    return i;
  }
  
  protected void cleanTableView()
  {
    if (++fLargeCount < 0)
    {
      if (fAttributeTableViewChainState != null) {
        for (int i = fTableViewBuckets - 1; i >= 0; i--) {
          fAttributeTableViewChainState[i] = 0;
        }
      }
      fLargeCount = 1;
    }
  }
  
  private void growTableView()
  {
    int i = fLength;
    int j = fTableViewBuckets;
    do
    {
      j = (j << 1) + 1;
      if (j < 0)
      {
        j = Integer.MAX_VALUE;
        break;
      }
    } while (i > j);
    fTableViewBuckets = j;
    fAttributeTableView = null;
    fLargeCount = 1;
  }
  
  protected void prepareTableView()
  {
    if (fLength > fTableViewBuckets) {
      growTableView();
    }
    if (fAttributeTableView == null)
    {
      fAttributeTableView = new Attribute[fTableViewBuckets];
      fAttributeTableViewChainState = new int[fTableViewBuckets];
    }
    else
    {
      cleanTableView();
    }
  }
  
  protected void prepareAndPopulateTableView()
  {
    prepareAndPopulateTableView(fLength);
  }
  
  private void prepareAndPopulateTableView(int paramInt)
  {
    prepareTableView();
    for (int j = 0; j < paramInt; j++)
    {
      Attribute localAttribute = fAttributes[j];
      int i = getTableViewBucket(name.rawname);
      if (fAttributeTableViewChainState[i] != fLargeCount)
      {
        fAttributeTableViewChainState[i] = fLargeCount;
        next = null;
        fAttributeTableView[i] = localAttribute;
      }
      else
      {
        next = fAttributeTableView[i];
        fAttributeTableView[i] = localAttribute;
      }
    }
  }
  
  public String getPrefix(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= fLength)) {
      return null;
    }
    String str = fAttributes[paramInt].name.prefix;
    return str != null ? str : "";
  }
  
  public String getURI(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= fLength)) {
      return null;
    }
    String str = fAttributes[paramInt].name.uri;
    return str;
  }
  
  public String getValue(String paramString1, String paramString2)
  {
    int i = getIndex(paramString1, paramString2);
    return i != -1 ? getValue(i) : null;
  }
  
  public Augmentations getAugmentations(String paramString1, String paramString2)
  {
    int i = getIndex(paramString1, paramString2);
    return i != -1 ? fAttributes[i].augs : null;
  }
  
  public Augmentations getAugmentations(String paramString)
  {
    int i = getIndex(paramString);
    return i != -1 ? fAttributes[i].augs : null;
  }
  
  public Augmentations getAugmentations(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= fLength)) {
      return null;
    }
    return fAttributes[paramInt].augs;
  }
  
  public void setAugmentations(int paramInt, Augmentations paramAugmentations)
  {
    fAttributes[paramInt].augs = paramAugmentations;
  }
  
  public void setURI(int paramInt, String paramString)
  {
    fAttributes[paramInt].name.uri = paramString;
  }
  
  public void setSchemaId(int paramInt, boolean paramBoolean)
  {
    fAttributes[paramInt].schemaId = paramBoolean;
  }
  
  public boolean getSchemaId(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= fLength)) {
      return false;
    }
    return fAttributes[paramInt].schemaId;
  }
  
  public boolean getSchemaId(String paramString)
  {
    int i = getIndex(paramString);
    return i != -1 ? fAttributes[i].schemaId : false;
  }
  
  public boolean getSchemaId(String paramString1, String paramString2)
  {
    if (!fNamespaces) {
      return false;
    }
    int i = getIndex(paramString1, paramString2);
    return i != -1 ? fAttributes[i].schemaId : false;
  }
  
  public void refresh()
  {
    if (fLength > 0) {
      for (int i = 0; i < fLength; i++) {
        getValue(i);
      }
    }
  }
  
  public void refresh(int paramInt) {}
  
  private void prepareAndPopulateTableViewNS(int paramInt)
  {
    prepareTableView();
    for (int j = 0; j < paramInt; j++)
    {
      Attribute localAttribute = fAttributes[j];
      int i = getTableViewBucket(name.localpart, name.uri);
      if (fAttributeTableViewChainState[i] != fLargeCount)
      {
        fAttributeTableViewChainState[i] = fLargeCount;
        next = null;
        fAttributeTableView[i] = localAttribute;
      }
      else
      {
        next = fAttributeTableView[i];
        fAttributeTableView[i] = localAttribute;
      }
    }
  }
  
  private void rebalanceTableView(int paramInt)
  {
    if (fHashMultipliers == null) {
      fHashMultipliers = new int[33];
    }
    PrimeNumberSequenceGenerator.generateSequence(fHashMultipliers);
    prepareAndPopulateTableView(paramInt);
  }
  
  private void rebalanceTableViewNS(int paramInt)
  {
    if (fHashMultipliers == null) {
      fHashMultipliers = new int[33];
    }
    PrimeNumberSequenceGenerator.generateSequence(fHashMultipliers);
    prepareAndPopulateTableViewNS(paramInt);
  }
  
  static class Attribute
  {
    public QName name = new QName();
    public String type;
    public String value;
    public XMLString xmlValue;
    public String nonNormalizedValue;
    public boolean specified;
    public boolean schemaId;
    public Augmentations augs = new AugmentationsImpl();
    public Attribute next;
    
    Attribute() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\util\XMLAttributesImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */