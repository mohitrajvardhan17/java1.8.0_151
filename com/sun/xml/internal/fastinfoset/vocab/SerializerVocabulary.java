package com.sun.xml.internal.fastinfoset.vocab;

import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.util.CharArrayIntMap;
import com.sun.xml.internal.fastinfoset.util.FixedEntryStringIntMap;
import com.sun.xml.internal.fastinfoset.util.KeyIntMap;
import com.sun.xml.internal.fastinfoset.util.LocalNameQualifiedNamesMap;
import com.sun.xml.internal.fastinfoset.util.LocalNameQualifiedNamesMap.Entry;
import com.sun.xml.internal.fastinfoset.util.StringIntMap;
import java.util.Iterator;
import java.util.Set;
import javax.xml.namespace.QName;

public class SerializerVocabulary
  extends Vocabulary
{
  public final StringIntMap restrictedAlphabet;
  public final StringIntMap encodingAlgorithm;
  public final StringIntMap namespaceName;
  public final StringIntMap prefix;
  public final StringIntMap localName;
  public final StringIntMap otherNCName;
  public final StringIntMap otherURI;
  public final StringIntMap attributeValue;
  public final CharArrayIntMap otherString;
  public final CharArrayIntMap characterContentChunk;
  public final LocalNameQualifiedNamesMap elementName;
  public final LocalNameQualifiedNamesMap attributeName;
  public final KeyIntMap[] tables = new KeyIntMap[12];
  protected boolean _useLocalNameAsKey;
  protected SerializerVocabulary _readOnlyVocabulary;
  
  public SerializerVocabulary()
  {
    tables[0] = (restrictedAlphabet = new StringIntMap(4));
    tables[1] = (encodingAlgorithm = new StringIntMap(4));
    tables[2] = (prefix = new FixedEntryStringIntMap("xml", 8));
    tables[3] = (namespaceName = new FixedEntryStringIntMap("http://www.w3.org/XML/1998/namespace", 8));
    tables[4] = (localName = new StringIntMap());
    tables[5] = (otherNCName = new StringIntMap(4));
    tables[6] = (otherURI = new StringIntMap(4));
    tables[7] = (attributeValue = new StringIntMap());
    tables[8] = (otherString = new CharArrayIntMap(4));
    tables[9] = (characterContentChunk = new CharArrayIntMap());
    tables[10] = (elementName = new LocalNameQualifiedNamesMap());
    tables[11] = (attributeName = new LocalNameQualifiedNamesMap());
  }
  
  public SerializerVocabulary(com.sun.xml.internal.org.jvnet.fastinfoset.Vocabulary paramVocabulary, boolean paramBoolean)
  {
    this();
    _useLocalNameAsKey = paramBoolean;
    convertVocabulary(paramVocabulary);
  }
  
  public SerializerVocabulary getReadOnlyVocabulary()
  {
    return _readOnlyVocabulary;
  }
  
  protected void setReadOnlyVocabulary(SerializerVocabulary paramSerializerVocabulary, boolean paramBoolean)
  {
    for (int i = 0; i < tables.length; i++) {
      tables[i].setReadOnlyMap(tables[i], paramBoolean);
    }
  }
  
  public void setInitialVocabulary(SerializerVocabulary paramSerializerVocabulary, boolean paramBoolean)
  {
    setExternalVocabularyURI(null);
    setInitialReadOnlyVocabulary(true);
    setReadOnlyVocabulary(paramSerializerVocabulary, paramBoolean);
  }
  
  public void setExternalVocabulary(String paramString, SerializerVocabulary paramSerializerVocabulary, boolean paramBoolean)
  {
    setInitialReadOnlyVocabulary(false);
    setExternalVocabularyURI(paramString);
    setReadOnlyVocabulary(paramSerializerVocabulary, paramBoolean);
  }
  
  public void clear()
  {
    for (int i = 0; i < tables.length; i++) {
      tables[i].clear();
    }
  }
  
  private void convertVocabulary(com.sun.xml.internal.org.jvnet.fastinfoset.Vocabulary paramVocabulary)
  {
    addToTable(restrictedAlphabets.iterator(), restrictedAlphabet);
    addToTable(encodingAlgorithms.iterator(), encodingAlgorithm);
    addToTable(prefixes.iterator(), prefix);
    addToTable(namespaceNames.iterator(), namespaceName);
    addToTable(localNames.iterator(), localName);
    addToTable(otherNCNames.iterator(), otherNCName);
    addToTable(otherURIs.iterator(), otherURI);
    addToTable(attributeValues.iterator(), attributeValue);
    addToTable(otherStrings.iterator(), otherString);
    addToTable(characterContentChunks.iterator(), characterContentChunk);
    addToTable(elements.iterator(), elementName);
    addToTable(attributes.iterator(), attributeName);
  }
  
  private void addToTable(Iterator paramIterator, StringIntMap paramStringIntMap)
  {
    while (paramIterator.hasNext()) {
      addToTable((String)paramIterator.next(), paramStringIntMap);
    }
  }
  
  private void addToTable(String paramString, StringIntMap paramStringIntMap)
  {
    if (paramString.length() == 0) {
      return;
    }
    paramStringIntMap.obtainIndex(paramString);
  }
  
  private void addToTable(Iterator paramIterator, CharArrayIntMap paramCharArrayIntMap)
  {
    while (paramIterator.hasNext()) {
      addToTable((String)paramIterator.next(), paramCharArrayIntMap);
    }
  }
  
  private void addToTable(String paramString, CharArrayIntMap paramCharArrayIntMap)
  {
    if (paramString.length() == 0) {
      return;
    }
    char[] arrayOfChar = paramString.toCharArray();
    paramCharArrayIntMap.obtainIndex(arrayOfChar, 0, arrayOfChar.length, false);
  }
  
  private void addToTable(Iterator paramIterator, LocalNameQualifiedNamesMap paramLocalNameQualifiedNamesMap)
  {
    while (paramIterator.hasNext()) {
      addToNameTable((QName)paramIterator.next(), paramLocalNameQualifiedNamesMap);
    }
  }
  
  private void addToNameTable(QName paramQName, LocalNameQualifiedNamesMap paramLocalNameQualifiedNamesMap)
  {
    int i = -1;
    int j = -1;
    if (paramQName.getNamespaceURI().length() > 0)
    {
      i = namespaceName.obtainIndex(paramQName.getNamespaceURI());
      if (i == -1) {
        i = namespaceName.get(paramQName.getNamespaceURI());
      }
      if (paramQName.getPrefix().length() > 0)
      {
        j = prefix.obtainIndex(paramQName.getPrefix());
        if (j == -1) {
          j = prefix.get(paramQName.getPrefix());
        }
      }
    }
    int k = localName.obtainIndex(paramQName.getLocalPart());
    if (k == -1) {
      k = localName.get(paramQName.getLocalPart());
    }
    QualifiedName localQualifiedName = new QualifiedName(paramQName.getPrefix(), paramQName.getNamespaceURI(), paramQName.getLocalPart(), paramLocalNameQualifiedNamesMap.getNextIndex(), j, i, k);
    LocalNameQualifiedNamesMap.Entry localEntry = null;
    if (_useLocalNameAsKey)
    {
      localEntry = paramLocalNameQualifiedNamesMap.obtainEntry(paramQName.getLocalPart());
    }
    else
    {
      String str = paramQName.getPrefix() + ":" + paramQName.getLocalPart();
      localEntry = paramLocalNameQualifiedNamesMap.obtainEntry(str);
    }
    localEntry.addQualifiedName(localQualifiedName);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\vocab\SerializerVocabulary.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */