package com.sun.xml.internal.fastinfoset.vocab;

import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.util.CharArray;
import com.sun.xml.internal.fastinfoset.util.CharArrayArray;
import com.sun.xml.internal.fastinfoset.util.ContiguousCharArrayArray;
import com.sun.xml.internal.fastinfoset.util.FixedEntryStringIntMap;
import com.sun.xml.internal.fastinfoset.util.PrefixArray;
import com.sun.xml.internal.fastinfoset.util.QualifiedNameArray;
import com.sun.xml.internal.fastinfoset.util.StringArray;
import com.sun.xml.internal.fastinfoset.util.StringIntMap;
import com.sun.xml.internal.fastinfoset.util.ValueArray;
import java.util.Iterator;
import java.util.Set;
import javax.xml.namespace.QName;

public class ParserVocabulary
  extends Vocabulary
{
  public static final String IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS_PEOPERTY = "com.sun.xml.internal.fastinfoset.vocab.ParserVocabulary.IdentifyingStringTable.maximumItems";
  public static final String NON_IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS_PEOPERTY = "com.sun.xml.internal.fastinfoset.vocab.ParserVocabulary.NonIdentifyingStringTable.maximumItems";
  public static final String NON_IDENTIFYING_STRING_TABLE_MAXIMUM_CHARACTERS_PEOPERTY = "com.sun.xml.internal.fastinfoset.vocab.ParserVocabulary.NonIdentifyingStringTable.maximumCharacters";
  protected static final int IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS = getIntegerValueFromProperty("com.sun.xml.internal.fastinfoset.vocab.ParserVocabulary.IdentifyingStringTable.maximumItems");
  protected static final int NON_IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS = getIntegerValueFromProperty("com.sun.xml.internal.fastinfoset.vocab.ParserVocabulary.NonIdentifyingStringTable.maximumItems");
  protected static final int NON_IDENTIFYING_STRING_TABLE_MAXIMUM_CHARACTERS = getIntegerValueFromProperty("com.sun.xml.internal.fastinfoset.vocab.ParserVocabulary.NonIdentifyingStringTable.maximumCharacters");
  public final CharArrayArray restrictedAlphabet = new CharArrayArray(10, 256);
  public final StringArray encodingAlgorithm = new StringArray(10, 256, true);
  public final StringArray namespaceName = new StringArray(10, IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS, false);
  public final PrefixArray prefix = new PrefixArray(10, IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS);
  public final StringArray localName = new StringArray(10, IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS, false);
  public final StringArray otherNCName = new StringArray(10, IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS, false);
  public final StringArray otherURI = new StringArray(10, IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS, true);
  public final StringArray attributeValue = new StringArray(10, NON_IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS, true);
  public final CharArrayArray otherString = new CharArrayArray(10, NON_IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS);
  public final ContiguousCharArrayArray characterContentChunk = new ContiguousCharArrayArray(10, NON_IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS, 512, NON_IDENTIFYING_STRING_TABLE_MAXIMUM_CHARACTERS);
  public final QualifiedNameArray elementName = new QualifiedNameArray(10, IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS);
  public final QualifiedNameArray attributeName = new QualifiedNameArray(10, IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS);
  public final ValueArray[] tables = new ValueArray[12];
  protected SerializerVocabulary _readOnlyVocabulary;
  
  private static int getIntegerValueFromProperty(String paramString)
  {
    String str = System.getProperty(paramString);
    if (str == null) {
      return Integer.MAX_VALUE;
    }
    try
    {
      return Math.max(Integer.parseInt(str), 10);
    }
    catch (NumberFormatException localNumberFormatException) {}
    return Integer.MAX_VALUE;
  }
  
  public ParserVocabulary()
  {
    tables[0] = restrictedAlphabet;
    tables[1] = encodingAlgorithm;
    tables[2] = prefix;
    tables[3] = namespaceName;
    tables[4] = localName;
    tables[5] = otherNCName;
    tables[6] = otherURI;
    tables[7] = attributeValue;
    tables[8] = otherString;
    tables[9] = characterContentChunk;
    tables[10] = elementName;
    tables[11] = attributeName;
  }
  
  public ParserVocabulary(com.sun.xml.internal.org.jvnet.fastinfoset.Vocabulary paramVocabulary)
  {
    this();
    convertVocabulary(paramVocabulary);
  }
  
  void setReadOnlyVocabulary(ParserVocabulary paramParserVocabulary, boolean paramBoolean)
  {
    for (int i = 0; i < tables.length; i++) {
      tables[i].setReadOnlyArray(tables[i], paramBoolean);
    }
  }
  
  public void setInitialVocabulary(ParserVocabulary paramParserVocabulary, boolean paramBoolean)
  {
    setExternalVocabularyURI(null);
    setInitialReadOnlyVocabulary(true);
    setReadOnlyVocabulary(paramParserVocabulary, paramBoolean);
  }
  
  public void setReferencedVocabulary(String paramString, ParserVocabulary paramParserVocabulary, boolean paramBoolean)
  {
    if (!paramString.equals(getExternalVocabularyURI()))
    {
      setInitialReadOnlyVocabulary(false);
      setExternalVocabularyURI(paramString);
      setReadOnlyVocabulary(paramParserVocabulary, paramBoolean);
    }
  }
  
  public void clear()
  {
    for (int i = 0; i < tables.length; i++) {
      tables[i].clear();
    }
  }
  
  private void convertVocabulary(com.sun.xml.internal.org.jvnet.fastinfoset.Vocabulary paramVocabulary)
  {
    FixedEntryStringIntMap localFixedEntryStringIntMap1 = new FixedEntryStringIntMap("xml", 8);
    FixedEntryStringIntMap localFixedEntryStringIntMap2 = new FixedEntryStringIntMap("http://www.w3.org/XML/1998/namespace", 8);
    StringIntMap localStringIntMap = new StringIntMap();
    addToTable(restrictedAlphabets.iterator(), restrictedAlphabet);
    addToTable(encodingAlgorithms.iterator(), encodingAlgorithm);
    addToTable(prefixes.iterator(), prefix, localFixedEntryStringIntMap1);
    addToTable(namespaceNames.iterator(), namespaceName, localFixedEntryStringIntMap2);
    addToTable(localNames.iterator(), localName, localStringIntMap);
    addToTable(otherNCNames.iterator(), otherNCName);
    addToTable(otherURIs.iterator(), otherURI);
    addToTable(attributeValues.iterator(), attributeValue);
    addToTable(otherStrings.iterator(), otherString);
    addToTable(characterContentChunks.iterator(), characterContentChunk);
    addToTable(elements.iterator(), elementName, false, localFixedEntryStringIntMap1, localFixedEntryStringIntMap2, localStringIntMap);
    addToTable(attributes.iterator(), attributeName, true, localFixedEntryStringIntMap1, localFixedEntryStringIntMap2, localStringIntMap);
  }
  
  private void addToTable(Iterator paramIterator, StringArray paramStringArray)
  {
    while (paramIterator.hasNext()) {
      addToTable((String)paramIterator.next(), paramStringArray, null);
    }
  }
  
  private void addToTable(Iterator paramIterator, StringArray paramStringArray, StringIntMap paramStringIntMap)
  {
    while (paramIterator.hasNext()) {
      addToTable((String)paramIterator.next(), paramStringArray, paramStringIntMap);
    }
  }
  
  private void addToTable(String paramString, StringArray paramStringArray, StringIntMap paramStringIntMap)
  {
    if (paramString.length() == 0) {
      return;
    }
    if (paramStringIntMap != null) {
      paramStringIntMap.obtainIndex(paramString);
    }
    paramStringArray.add(paramString);
  }
  
  private void addToTable(Iterator paramIterator, PrefixArray paramPrefixArray, StringIntMap paramStringIntMap)
  {
    while (paramIterator.hasNext()) {
      addToTable((String)paramIterator.next(), paramPrefixArray, paramStringIntMap);
    }
  }
  
  private void addToTable(String paramString, PrefixArray paramPrefixArray, StringIntMap paramStringIntMap)
  {
    if (paramString.length() == 0) {
      return;
    }
    if (paramStringIntMap != null) {
      paramStringIntMap.obtainIndex(paramString);
    }
    paramPrefixArray.add(paramString);
  }
  
  private void addToTable(Iterator paramIterator, ContiguousCharArrayArray paramContiguousCharArrayArray)
  {
    while (paramIterator.hasNext()) {
      addToTable((String)paramIterator.next(), paramContiguousCharArrayArray);
    }
  }
  
  private void addToTable(String paramString, ContiguousCharArrayArray paramContiguousCharArrayArray)
  {
    if (paramString.length() == 0) {
      return;
    }
    char[] arrayOfChar = paramString.toCharArray();
    paramContiguousCharArrayArray.add(arrayOfChar, arrayOfChar.length);
  }
  
  private void addToTable(Iterator paramIterator, CharArrayArray paramCharArrayArray)
  {
    while (paramIterator.hasNext()) {
      addToTable((String)paramIterator.next(), paramCharArrayArray);
    }
  }
  
  private void addToTable(String paramString, CharArrayArray paramCharArrayArray)
  {
    if (paramString.length() == 0) {
      return;
    }
    char[] arrayOfChar = paramString.toCharArray();
    paramCharArrayArray.add(new CharArray(arrayOfChar, 0, arrayOfChar.length, false));
  }
  
  private void addToTable(Iterator paramIterator, QualifiedNameArray paramQualifiedNameArray, boolean paramBoolean, StringIntMap paramStringIntMap1, StringIntMap paramStringIntMap2, StringIntMap paramStringIntMap3)
  {
    while (paramIterator.hasNext()) {
      addToNameTable((QName)paramIterator.next(), paramQualifiedNameArray, paramBoolean, paramStringIntMap1, paramStringIntMap2, paramStringIntMap3);
    }
  }
  
  private void addToNameTable(QName paramQName, QualifiedNameArray paramQualifiedNameArray, boolean paramBoolean, StringIntMap paramStringIntMap1, StringIntMap paramStringIntMap2, StringIntMap paramStringIntMap3)
  {
    int i = -1;
    int j = -1;
    if (paramQName.getNamespaceURI().length() > 0)
    {
      i = paramStringIntMap2.obtainIndex(paramQName.getNamespaceURI());
      if (i == -1)
      {
        i = namespaceName.getSize();
        namespaceName.add(paramQName.getNamespaceURI());
      }
      if (paramQName.getPrefix().length() > 0)
      {
        j = paramStringIntMap1.obtainIndex(paramQName.getPrefix());
        if (j == -1)
        {
          j = prefix.getSize();
          prefix.add(paramQName.getPrefix());
        }
      }
    }
    int k = paramStringIntMap3.obtainIndex(paramQName.getLocalPart());
    if (k == -1)
    {
      k = localName.getSize();
      localName.add(paramQName.getLocalPart());
    }
    QualifiedName localQualifiedName = new QualifiedName(paramQName.getPrefix(), paramQName.getNamespaceURI(), paramQName.getLocalPart(), paramQualifiedNameArray.getSize(), j, i, k);
    if (paramBoolean) {
      localQualifiedName.createAttributeValues(256);
    }
    paramQualifiedNameArray.add(localQualifiedName);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\vocab\ParserVocabulary.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */