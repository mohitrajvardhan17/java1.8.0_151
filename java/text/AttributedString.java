package java.text;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

public class AttributedString
{
  private static final int ARRAY_SIZE_INCREMENT = 10;
  String text;
  int runArraySize;
  int runCount;
  int[] runStarts;
  Vector<AttributedCharacterIterator.Attribute>[] runAttributes;
  Vector<Object>[] runAttributeValues;
  
  AttributedString(AttributedCharacterIterator[] paramArrayOfAttributedCharacterIterator)
  {
    if (paramArrayOfAttributedCharacterIterator == null) {
      throw new NullPointerException("Iterators must not be null");
    }
    if (paramArrayOfAttributedCharacterIterator.length == 0)
    {
      text = "";
    }
    else
    {
      StringBuffer localStringBuffer = new StringBuffer();
      for (int i = 0; i < paramArrayOfAttributedCharacterIterator.length; i++) {
        appendContents(localStringBuffer, paramArrayOfAttributedCharacterIterator[i]);
      }
      text = localStringBuffer.toString();
      if (text.length() > 0)
      {
        i = 0;
        Object localObject = null;
        for (int j = 0; j < paramArrayOfAttributedCharacterIterator.length; j++)
        {
          AttributedCharacterIterator localAttributedCharacterIterator = paramArrayOfAttributedCharacterIterator[j];
          int k = localAttributedCharacterIterator.getBeginIndex();
          int m = localAttributedCharacterIterator.getEndIndex();
          for (int n = k; n < m; n = localAttributedCharacterIterator.getRunLimit())
          {
            localAttributedCharacterIterator.setIndex(n);
            Map localMap = localAttributedCharacterIterator.getAttributes();
            if (mapsDiffer((Map)localObject, localMap)) {
              setAttributes(localMap, n - k + i);
            }
            localObject = localMap;
          }
          i += m - k;
        }
      }
    }
  }
  
  public AttributedString(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    text = paramString;
  }
  
  public AttributedString(String paramString, Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap)
  {
    if ((paramString == null) || (paramMap == null)) {
      throw new NullPointerException();
    }
    text = paramString;
    if (paramString.length() == 0)
    {
      if (paramMap.isEmpty()) {
        return;
      }
      throw new IllegalArgumentException("Can't add attribute to 0-length text");
    }
    int i = paramMap.size();
    if (i > 0)
    {
      createRunAttributeDataVectors();
      Vector localVector1 = new Vector(i);
      Vector localVector2 = new Vector(i);
      runAttributes[0] = localVector1;
      runAttributeValues[0] = localVector2;
      Iterator localIterator = paramMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        localVector1.addElement(localEntry.getKey());
        localVector2.addElement(localEntry.getValue());
      }
    }
  }
  
  public AttributedString(AttributedCharacterIterator paramAttributedCharacterIterator)
  {
    this(paramAttributedCharacterIterator, paramAttributedCharacterIterator.getBeginIndex(), paramAttributedCharacterIterator.getEndIndex(), null);
  }
  
  public AttributedString(AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt1, int paramInt2)
  {
    this(paramAttributedCharacterIterator, paramInt1, paramInt2, null);
  }
  
  public AttributedString(AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt1, int paramInt2, AttributedCharacterIterator.Attribute[] paramArrayOfAttribute)
  {
    if (paramAttributedCharacterIterator == null) {
      throw new NullPointerException();
    }
    int i = paramAttributedCharacterIterator.getBeginIndex();
    int j = paramAttributedCharacterIterator.getEndIndex();
    if ((paramInt1 < i) || (paramInt2 > j) || (paramInt1 > paramInt2)) {
      throw new IllegalArgumentException("Invalid substring range");
    }
    StringBuffer localStringBuffer = new StringBuffer();
    paramAttributedCharacterIterator.setIndex(paramInt1);
    for (char c = paramAttributedCharacterIterator.current(); paramAttributedCharacterIterator.getIndex() < paramInt2; c = paramAttributedCharacterIterator.next()) {
      localStringBuffer.append(c);
    }
    text = localStringBuffer.toString();
    if (paramInt1 == paramInt2) {
      return;
    }
    HashSet localHashSet = new HashSet();
    if (paramArrayOfAttribute == null)
    {
      localHashSet.addAll(paramAttributedCharacterIterator.getAllAttributeKeys());
    }
    else
    {
      for (int k = 0; k < paramArrayOfAttribute.length; k++) {
        localHashSet.add(paramArrayOfAttribute[k]);
      }
      localHashSet.retainAll(paramAttributedCharacterIterator.getAllAttributeKeys());
    }
    if (localHashSet.isEmpty()) {
      return;
    }
    Iterator localIterator = localHashSet.iterator();
    while (localIterator.hasNext())
    {
      AttributedCharacterIterator.Attribute localAttribute = (AttributedCharacterIterator.Attribute)localIterator.next();
      paramAttributedCharacterIterator.setIndex(i);
      while (paramAttributedCharacterIterator.getIndex() < paramInt2)
      {
        int m = paramAttributedCharacterIterator.getRunStart(localAttribute);
        int n = paramAttributedCharacterIterator.getRunLimit(localAttribute);
        Object localObject = paramAttributedCharacterIterator.getAttribute(localAttribute);
        if (localObject != null) {
          if ((localObject instanceof Annotation))
          {
            if ((m >= paramInt1) && (n <= paramInt2)) {
              addAttribute(localAttribute, localObject, m - paramInt1, n - paramInt1);
            } else if (n > paramInt2) {
              break;
            }
          }
          else
          {
            if (m >= paramInt2) {
              break;
            }
            if (n > paramInt1)
            {
              if (m < paramInt1) {
                m = paramInt1;
              }
              if (n > paramInt2) {
                n = paramInt2;
              }
              if (m != n) {
                addAttribute(localAttribute, localObject, m - paramInt1, n - paramInt1);
              }
            }
          }
        }
        paramAttributedCharacterIterator.setIndex(n);
      }
    }
  }
  
  public void addAttribute(AttributedCharacterIterator.Attribute paramAttribute, Object paramObject)
  {
    if (paramAttribute == null) {
      throw new NullPointerException();
    }
    int i = length();
    if (i == 0) {
      throw new IllegalArgumentException("Can't add attribute to 0-length text");
    }
    addAttributeImpl(paramAttribute, paramObject, 0, i);
  }
  
  public void addAttribute(AttributedCharacterIterator.Attribute paramAttribute, Object paramObject, int paramInt1, int paramInt2)
  {
    if (paramAttribute == null) {
      throw new NullPointerException();
    }
    if ((paramInt1 < 0) || (paramInt2 > length()) || (paramInt1 >= paramInt2)) {
      throw new IllegalArgumentException("Invalid substring range");
    }
    addAttributeImpl(paramAttribute, paramObject, paramInt1, paramInt2);
  }
  
  public void addAttributes(Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap, int paramInt1, int paramInt2)
  {
    if (paramMap == null) {
      throw new NullPointerException();
    }
    if ((paramInt1 < 0) || (paramInt2 > length()) || (paramInt1 > paramInt2)) {
      throw new IllegalArgumentException("Invalid substring range");
    }
    if (paramInt1 == paramInt2)
    {
      if (paramMap.isEmpty()) {
        return;
      }
      throw new IllegalArgumentException("Can't add attribute to 0-length text");
    }
    if (runCount == 0) {
      createRunAttributeDataVectors();
    }
    int i = ensureRunBreak(paramInt1);
    int j = ensureRunBreak(paramInt2);
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      addAttributeRunData((AttributedCharacterIterator.Attribute)localEntry.getKey(), localEntry.getValue(), i, j);
    }
  }
  
  private synchronized void addAttributeImpl(AttributedCharacterIterator.Attribute paramAttribute, Object paramObject, int paramInt1, int paramInt2)
  {
    if (runCount == 0) {
      createRunAttributeDataVectors();
    }
    int i = ensureRunBreak(paramInt1);
    int j = ensureRunBreak(paramInt2);
    addAttributeRunData(paramAttribute, paramObject, i, j);
  }
  
  private final void createRunAttributeDataVectors()
  {
    int[] arrayOfInt = new int[10];
    Vector[] arrayOfVector1 = (Vector[])new Vector[10];
    Vector[] arrayOfVector2 = (Vector[])new Vector[10];
    runStarts = arrayOfInt;
    runAttributes = arrayOfVector1;
    runAttributeValues = arrayOfVector2;
    runArraySize = 10;
    runCount = 1;
  }
  
  private final int ensureRunBreak(int paramInt)
  {
    return ensureRunBreak(paramInt, true);
  }
  
  private final int ensureRunBreak(int paramInt, boolean paramBoolean)
  {
    if (paramInt == length()) {
      return runCount;
    }
    for (int i = 0; (i < runCount) && (runStarts[i] < paramInt); i++) {}
    if ((i < runCount) && (runStarts[i] == paramInt)) {
      return i;
    }
    Object localObject2;
    Object localObject3;
    if (runCount == runArraySize)
    {
      int j = runArraySize + 10;
      localObject1 = new int[j];
      localObject2 = (Vector[])new Vector[j];
      localObject3 = (Vector[])new Vector[j];
      for (int m = 0; m < runArraySize; m++)
      {
        localObject1[m] = runStarts[m];
        localObject2[m] = runAttributes[m];
        localObject3[m] = runAttributeValues[m];
      }
      runStarts = ((int[])localObject1);
      runAttributes = ((Vector[])localObject2);
      runAttributeValues = ((Vector[])localObject3);
      runArraySize = j;
    }
    Vector localVector = null;
    Object localObject1 = null;
    if (paramBoolean)
    {
      localObject2 = runAttributes[(i - 1)];
      localObject3 = runAttributeValues[(i - 1)];
      if (localObject2 != null) {
        localVector = new Vector((Collection)localObject2);
      }
      if (localObject3 != null) {
        localObject1 = new Vector((Collection)localObject3);
      }
    }
    runCount += 1;
    for (int k = runCount - 1; k > i; k--)
    {
      runStarts[k] = runStarts[(k - 1)];
      runAttributes[k] = runAttributes[(k - 1)];
      runAttributeValues[k] = runAttributeValues[(k - 1)];
    }
    runStarts[i] = paramInt;
    runAttributes[i] = localVector;
    runAttributeValues[i] = localObject1;
    return i;
  }
  
  private void addAttributeRunData(AttributedCharacterIterator.Attribute paramAttribute, Object paramObject, int paramInt1, int paramInt2)
  {
    for (int i = paramInt1; i < paramInt2; i++)
    {
      int j = -1;
      if (runAttributes[i] == null)
      {
        Vector localVector1 = new Vector();
        Vector localVector2 = new Vector();
        runAttributes[i] = localVector1;
        runAttributeValues[i] = localVector2;
      }
      else
      {
        j = runAttributes[i].indexOf(paramAttribute);
      }
      if (j == -1)
      {
        int k = runAttributes[i].size();
        runAttributes[i].addElement(paramAttribute);
        try
        {
          runAttributeValues[i].addElement(paramObject);
        }
        catch (Exception localException)
        {
          runAttributes[i].setSize(k);
          runAttributeValues[i].setSize(k);
        }
      }
      else
      {
        runAttributeValues[i].set(j, paramObject);
      }
    }
  }
  
  public AttributedCharacterIterator getIterator()
  {
    return getIterator(null, 0, length());
  }
  
  public AttributedCharacterIterator getIterator(AttributedCharacterIterator.Attribute[] paramArrayOfAttribute)
  {
    return getIterator(paramArrayOfAttribute, 0, length());
  }
  
  public AttributedCharacterIterator getIterator(AttributedCharacterIterator.Attribute[] paramArrayOfAttribute, int paramInt1, int paramInt2)
  {
    return new AttributedStringIterator(paramArrayOfAttribute, paramInt1, paramInt2);
  }
  
  int length()
  {
    return text.length();
  }
  
  private char charAt(int paramInt)
  {
    return text.charAt(paramInt);
  }
  
  private synchronized Object getAttribute(AttributedCharacterIterator.Attribute paramAttribute, int paramInt)
  {
    Vector localVector1 = runAttributes[paramInt];
    Vector localVector2 = runAttributeValues[paramInt];
    if (localVector1 == null) {
      return null;
    }
    int i = localVector1.indexOf(paramAttribute);
    if (i != -1) {
      return localVector2.elementAt(i);
    }
    return null;
  }
  
  private Object getAttributeCheckRange(AttributedCharacterIterator.Attribute paramAttribute, int paramInt1, int paramInt2, int paramInt3)
  {
    Object localObject = getAttribute(paramAttribute, paramInt1);
    if ((localObject instanceof Annotation))
    {
      int j;
      if (paramInt2 > 0)
      {
        i = paramInt1;
        for (j = runStarts[i]; (j >= paramInt2) && (valuesMatch(localObject, getAttribute(paramAttribute, i - 1))); j = runStarts[i]) {
          i--;
        }
        if (j < paramInt2) {
          return null;
        }
      }
      int i = length();
      if (paramInt3 < i)
      {
        j = paramInt1;
        for (int k = j < runCount - 1 ? runStarts[(j + 1)] : i; (k <= paramInt3) && (valuesMatch(localObject, getAttribute(paramAttribute, j + 1))); k = j < runCount - 1 ? runStarts[(j + 1)] : i) {
          j++;
        }
        if (k > paramInt3) {
          return null;
        }
      }
    }
    return localObject;
  }
  
  private boolean attributeValuesMatch(Set<? extends AttributedCharacterIterator.Attribute> paramSet, int paramInt1, int paramInt2)
  {
    Iterator localIterator = paramSet.iterator();
    while (localIterator.hasNext())
    {
      AttributedCharacterIterator.Attribute localAttribute = (AttributedCharacterIterator.Attribute)localIterator.next();
      if (!valuesMatch(getAttribute(localAttribute, paramInt1), getAttribute(localAttribute, paramInt2))) {
        return false;
      }
    }
    return true;
  }
  
  private static final boolean valuesMatch(Object paramObject1, Object paramObject2)
  {
    if (paramObject1 == null) {
      return paramObject2 == null;
    }
    return paramObject1.equals(paramObject2);
  }
  
  private final void appendContents(StringBuffer paramStringBuffer, CharacterIterator paramCharacterIterator)
  {
    int i = paramCharacterIterator.getBeginIndex();
    int j = paramCharacterIterator.getEndIndex();
    while (i < j)
    {
      paramCharacterIterator.setIndex(i++);
      paramStringBuffer.append(paramCharacterIterator.current());
    }
  }
  
  private void setAttributes(Map<AttributedCharacterIterator.Attribute, Object> paramMap, int paramInt)
  {
    if (runCount == 0) {
      createRunAttributeDataVectors();
    }
    int i = ensureRunBreak(paramInt, false);
    int j;
    if ((paramMap != null) && ((j = paramMap.size()) > 0))
    {
      Vector localVector1 = new Vector(j);
      Vector localVector2 = new Vector(j);
      Iterator localIterator = paramMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        localVector1.add(localEntry.getKey());
        localVector2.add(localEntry.getValue());
      }
      runAttributes[i] = localVector1;
      runAttributeValues[i] = localVector2;
    }
  }
  
  private static <K, V> boolean mapsDiffer(Map<K, V> paramMap1, Map<K, V> paramMap2)
  {
    if (paramMap1 == null) {
      return (paramMap2 != null) && (paramMap2.size() > 0);
    }
    return !paramMap1.equals(paramMap2);
  }
  
  private final class AttributeMap
    extends AbstractMap<AttributedCharacterIterator.Attribute, Object>
  {
    int runIndex;
    int beginIndex;
    int endIndex;
    
    AttributeMap(int paramInt1, int paramInt2, int paramInt3)
    {
      runIndex = paramInt1;
      beginIndex = paramInt2;
      endIndex = paramInt3;
    }
    
    public Set<Map.Entry<AttributedCharacterIterator.Attribute, Object>> entrySet()
    {
      HashSet localHashSet = new HashSet();
      synchronized (AttributedString.this)
      {
        int i = runAttributes[runIndex].size();
        for (int j = 0; j < i; j++)
        {
          AttributedCharacterIterator.Attribute localAttribute = (AttributedCharacterIterator.Attribute)runAttributes[runIndex].get(j);
          Object localObject1 = runAttributeValues[runIndex].get(j);
          if ((localObject1 instanceof Annotation))
          {
            localObject1 = AttributedString.this.getAttributeCheckRange(localAttribute, runIndex, beginIndex, endIndex);
            if (localObject1 == null) {}
          }
          else
          {
            AttributeEntry localAttributeEntry = new AttributeEntry(localAttribute, localObject1);
            localHashSet.add(localAttributeEntry);
          }
        }
      }
      return localHashSet;
    }
    
    public Object get(Object paramObject)
    {
      return AttributedString.this.getAttributeCheckRange((AttributedCharacterIterator.Attribute)paramObject, runIndex, beginIndex, endIndex);
    }
  }
  
  private final class AttributedStringIterator
    implements AttributedCharacterIterator
  {
    private int beginIndex;
    private int endIndex;
    private AttributedCharacterIterator.Attribute[] relevantAttributes;
    private int currentIndex;
    private int currentRunIndex;
    private int currentRunStart;
    private int currentRunLimit;
    
    AttributedStringIterator(AttributedCharacterIterator.Attribute[] paramArrayOfAttribute, int paramInt1, int paramInt2)
    {
      if ((paramInt1 < 0) || (paramInt1 > paramInt2) || (paramInt2 > length())) {
        throw new IllegalArgumentException("Invalid substring range");
      }
      beginIndex = paramInt1;
      endIndex = paramInt2;
      currentIndex = paramInt1;
      updateRunInfo();
      if (paramArrayOfAttribute != null) {
        relevantAttributes = ((AttributedCharacterIterator.Attribute[])paramArrayOfAttribute.clone());
      }
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (!(paramObject instanceof AttributedStringIterator)) {
        return false;
      }
      AttributedStringIterator localAttributedStringIterator = (AttributedStringIterator)paramObject;
      if (AttributedString.this != localAttributedStringIterator.getString()) {
        return false;
      }
      return (currentIndex == currentIndex) && (beginIndex == beginIndex) && (endIndex == endIndex);
    }
    
    public int hashCode()
    {
      return text.hashCode() ^ currentIndex ^ beginIndex ^ endIndex;
    }
    
    public Object clone()
    {
      try
      {
        AttributedStringIterator localAttributedStringIterator = (AttributedStringIterator)super.clone();
        return localAttributedStringIterator;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        throw new InternalError(localCloneNotSupportedException);
      }
    }
    
    public char first()
    {
      return internalSetIndex(beginIndex);
    }
    
    public char last()
    {
      if (endIndex == beginIndex) {
        return internalSetIndex(endIndex);
      }
      return internalSetIndex(endIndex - 1);
    }
    
    public char current()
    {
      if (currentIndex == endIndex) {
        return 65535;
      }
      return AttributedString.this.charAt(currentIndex);
    }
    
    public char next()
    {
      if (currentIndex < endIndex) {
        return internalSetIndex(currentIndex + 1);
      }
      return 65535;
    }
    
    public char previous()
    {
      if (currentIndex > beginIndex) {
        return internalSetIndex(currentIndex - 1);
      }
      return 65535;
    }
    
    public char setIndex(int paramInt)
    {
      if ((paramInt < beginIndex) || (paramInt > endIndex)) {
        throw new IllegalArgumentException("Invalid index");
      }
      return internalSetIndex(paramInt);
    }
    
    public int getBeginIndex()
    {
      return beginIndex;
    }
    
    public int getEndIndex()
    {
      return endIndex;
    }
    
    public int getIndex()
    {
      return currentIndex;
    }
    
    public int getRunStart()
    {
      return currentRunStart;
    }
    
    public int getRunStart(AttributedCharacterIterator.Attribute paramAttribute)
    {
      if ((currentRunStart == beginIndex) || (currentRunIndex == -1)) {
        return currentRunStart;
      }
      Object localObject = getAttribute(paramAttribute);
      int i = currentRunStart;
      int j = currentRunIndex;
      while ((i > beginIndex) && (AttributedString.valuesMatch(localObject, AttributedString.access$100(AttributedString.this, paramAttribute, j - 1))))
      {
        j--;
        i = runStarts[j];
      }
      if (i < beginIndex) {
        i = beginIndex;
      }
      return i;
    }
    
    public int getRunStart(Set<? extends AttributedCharacterIterator.Attribute> paramSet)
    {
      if ((currentRunStart == beginIndex) || (currentRunIndex == -1)) {
        return currentRunStart;
      }
      int i = currentRunStart;
      int j = currentRunIndex;
      while ((i > beginIndex) && (AttributedString.this.attributeValuesMatch(paramSet, currentRunIndex, j - 1)))
      {
        j--;
        i = runStarts[j];
      }
      if (i < beginIndex) {
        i = beginIndex;
      }
      return i;
    }
    
    public int getRunLimit()
    {
      return currentRunLimit;
    }
    
    public int getRunLimit(AttributedCharacterIterator.Attribute paramAttribute)
    {
      if ((currentRunLimit == endIndex) || (currentRunIndex == -1)) {
        return currentRunLimit;
      }
      Object localObject = getAttribute(paramAttribute);
      int i = currentRunLimit;
      int j = currentRunIndex;
      while ((i < endIndex) && (AttributedString.valuesMatch(localObject, AttributedString.access$100(AttributedString.this, paramAttribute, j + 1))))
      {
        j++;
        i = j < runCount - 1 ? runStarts[(j + 1)] : endIndex;
      }
      if (i > endIndex) {
        i = endIndex;
      }
      return i;
    }
    
    public int getRunLimit(Set<? extends AttributedCharacterIterator.Attribute> paramSet)
    {
      if ((currentRunLimit == endIndex) || (currentRunIndex == -1)) {
        return currentRunLimit;
      }
      int i = currentRunLimit;
      int j = currentRunIndex;
      while ((i < endIndex) && (AttributedString.this.attributeValuesMatch(paramSet, currentRunIndex, j + 1)))
      {
        j++;
        i = j < runCount - 1 ? runStarts[(j + 1)] : endIndex;
      }
      if (i > endIndex) {
        i = endIndex;
      }
      return i;
    }
    
    public Map<AttributedCharacterIterator.Attribute, Object> getAttributes()
    {
      if ((runAttributes == null) || (currentRunIndex == -1) || (runAttributes[currentRunIndex] == null)) {
        return new Hashtable();
      }
      return new AttributedString.AttributeMap(AttributedString.this, currentRunIndex, beginIndex, endIndex);
    }
    
    public Set<AttributedCharacterIterator.Attribute> getAllAttributeKeys()
    {
      if (runAttributes == null) {
        return new HashSet();
      }
      synchronized (AttributedString.this)
      {
        HashSet localHashSet = new HashSet();
        for (int i = 0; i < runCount; i++) {
          if ((runStarts[i] < endIndex) && ((i == runCount - 1) || (runStarts[(i + 1)] > beginIndex)))
          {
            Vector localVector = runAttributes[i];
            if (localVector != null)
            {
              int j = localVector.size();
              while (j-- > 0) {
                localHashSet.add(localVector.get(j));
              }
            }
          }
        }
        return localHashSet;
      }
    }
    
    public Object getAttribute(AttributedCharacterIterator.Attribute paramAttribute)
    {
      int i = currentRunIndex;
      if (i < 0) {
        return null;
      }
      return AttributedString.this.getAttributeCheckRange(paramAttribute, i, beginIndex, endIndex);
    }
    
    private AttributedString getString()
    {
      return AttributedString.this;
    }
    
    private char internalSetIndex(int paramInt)
    {
      currentIndex = paramInt;
      if ((paramInt < currentRunStart) || (paramInt >= currentRunLimit)) {
        updateRunInfo();
      }
      if (currentIndex == endIndex) {
        return 65535;
      }
      return AttributedString.this.charAt(paramInt);
    }
    
    private void updateRunInfo()
    {
      if (currentIndex == endIndex)
      {
        currentRunStart = (currentRunLimit = endIndex);
        currentRunIndex = -1;
      }
      else
      {
        synchronized (AttributedString.this)
        {
          for (int i = -1; (i < runCount - 1) && (runStarts[(i + 1)] <= currentIndex); i++) {}
          currentRunIndex = i;
          if (i >= 0)
          {
            currentRunStart = runStarts[i];
            if (currentRunStart < beginIndex) {
              currentRunStart = beginIndex;
            }
          }
          else
          {
            currentRunStart = beginIndex;
          }
          if (i < runCount - 1)
          {
            currentRunLimit = runStarts[(i + 1)];
            if (currentRunLimit > endIndex) {
              currentRunLimit = endIndex;
            }
          }
          else
          {
            currentRunLimit = endIndex;
          }
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\AttributedString.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */