package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.util.CharArray;
import com.sun.xml.internal.fastinfoset.util.CharArrayIntMap;
import com.sun.xml.internal.fastinfoset.util.ContiguousCharArrayArray;
import com.sun.xml.internal.fastinfoset.util.LocalNameQualifiedNamesMap;
import com.sun.xml.internal.fastinfoset.util.LocalNameQualifiedNamesMap.Entry;
import com.sun.xml.internal.fastinfoset.util.PrefixArray;
import com.sun.xml.internal.fastinfoset.util.QualifiedNameArray;
import com.sun.xml.internal.fastinfoset.util.StringArray;
import com.sun.xml.internal.fastinfoset.util.StringIntMap;
import com.sun.xml.internal.fastinfoset.vocab.ParserVocabulary;
import com.sun.xml.internal.fastinfoset.vocab.SerializerVocabulary;
import com.sun.xml.internal.org.jvnet.fastinfoset.Vocabulary;
import java.util.Set;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class VocabularyGenerator
  extends DefaultHandler
  implements LexicalHandler
{
  protected SerializerVocabulary _serializerVocabulary;
  protected ParserVocabulary _parserVocabulary;
  protected Vocabulary _v;
  protected int attributeValueSizeConstraint = 32;
  protected int characterContentChunkSizeContraint = 32;
  
  public VocabularyGenerator()
  {
    _serializerVocabulary = new SerializerVocabulary();
    _parserVocabulary = new ParserVocabulary();
    _v = new Vocabulary();
  }
  
  public VocabularyGenerator(SerializerVocabulary paramSerializerVocabulary)
  {
    _serializerVocabulary = paramSerializerVocabulary;
    _parserVocabulary = new ParserVocabulary();
    _v = new Vocabulary();
  }
  
  public VocabularyGenerator(ParserVocabulary paramParserVocabulary)
  {
    _serializerVocabulary = new SerializerVocabulary();
    _parserVocabulary = paramParserVocabulary;
    _v = new Vocabulary();
  }
  
  public VocabularyGenerator(SerializerVocabulary paramSerializerVocabulary, ParserVocabulary paramParserVocabulary)
  {
    _serializerVocabulary = paramSerializerVocabulary;
    _parserVocabulary = paramParserVocabulary;
    _v = new Vocabulary();
  }
  
  public Vocabulary getVocabulary()
  {
    return _v;
  }
  
  public void setCharacterContentChunkSizeLimit(int paramInt)
  {
    if (paramInt < 0) {
      paramInt = 0;
    }
    characterContentChunkSizeContraint = paramInt;
  }
  
  public int getCharacterContentChunkSizeLimit()
  {
    return characterContentChunkSizeContraint;
  }
  
  public void setAttributeValueSizeLimit(int paramInt)
  {
    if (paramInt < 0) {
      paramInt = 0;
    }
    attributeValueSizeConstraint = paramInt;
  }
  
  public int getAttributeValueSizeLimit()
  {
    return attributeValueSizeConstraint;
  }
  
  public void startDocument()
    throws SAXException
  {}
  
  public void endDocument()
    throws SAXException
  {}
  
  public void startPrefixMapping(String paramString1, String paramString2)
    throws SAXException
  {
    addToTable(paramString1, _v.prefixes, _serializerVocabulary.prefix, _parserVocabulary.prefix);
    addToTable(paramString2, _v.namespaceNames, _serializerVocabulary.namespaceName, _parserVocabulary.namespaceName);
  }
  
  public void endPrefixMapping(String paramString)
    throws SAXException
  {}
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    addToNameTable(paramString1, paramString3, paramString2, _v.elements, _serializerVocabulary.elementName, _parserVocabulary.elementName, false);
    for (int i = 0; i < paramAttributes.getLength(); i++)
    {
      addToNameTable(paramAttributes.getURI(i), paramAttributes.getQName(i), paramAttributes.getLocalName(i), _v.attributes, _serializerVocabulary.attributeName, _parserVocabulary.attributeName, true);
      String str = paramAttributes.getValue(i);
      if (str.length() < attributeValueSizeConstraint) {
        addToTable(str, _v.attributeValues, _serializerVocabulary.attributeValue, _parserVocabulary.attributeValue);
      }
    }
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {}
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (paramInt2 < characterContentChunkSizeContraint) {
      addToCharArrayTable(new CharArray(paramArrayOfChar, paramInt1, paramInt2, true));
    }
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {}
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {}
  
  public void setDocumentLocator(Locator paramLocator) {}
  
  public void skippedEntity(String paramString)
    throws SAXException
  {}
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {}
  
  public void startCDATA()
    throws SAXException
  {}
  
  public void endCDATA()
    throws SAXException
  {}
  
  public void startDTD(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {}
  
  public void endDTD()
    throws SAXException
  {}
  
  public void startEntity(String paramString)
    throws SAXException
  {}
  
  public void endEntity(String paramString)
    throws SAXException
  {}
  
  public void addToTable(String paramString, Set paramSet, StringIntMap paramStringIntMap, StringArray paramStringArray)
  {
    if (paramString.length() == 0) {
      return;
    }
    if (paramStringIntMap.obtainIndex(paramString) == -1) {
      paramStringArray.add(paramString);
    }
    paramSet.add(paramString);
  }
  
  public void addToTable(String paramString, Set paramSet, StringIntMap paramStringIntMap, PrefixArray paramPrefixArray)
  {
    if (paramString.length() == 0) {
      return;
    }
    if (paramStringIntMap.obtainIndex(paramString) == -1) {
      paramPrefixArray.add(paramString);
    }
    paramSet.add(paramString);
  }
  
  public void addToCharArrayTable(CharArray paramCharArray)
  {
    if (_serializerVocabulary.characterContentChunk.obtainIndex(ch, start, length, false) == -1) {
      _parserVocabulary.characterContentChunk.add(ch, length);
    }
    _v.characterContentChunks.add(paramCharArray.toString());
  }
  
  public void addToNameTable(String paramString1, String paramString2, String paramString3, Set paramSet, LocalNameQualifiedNamesMap paramLocalNameQualifiedNamesMap, QualifiedNameArray paramQualifiedNameArray, boolean paramBoolean)
    throws SAXException
  {
    LocalNameQualifiedNamesMap.Entry localEntry = paramLocalNameQualifiedNamesMap.obtainEntry(paramString2);
    if (_valueIndex > 0)
    {
      localObject = _value;
      for (i = 0; i < _valueIndex; i++) {
        if ((paramString1 == namespaceName) || (paramString1.equals(namespaceName))) {
          return;
        }
      }
    }
    Object localObject = getPrefixFromQualifiedName(paramString2);
    int i = -1;
    int j = -1;
    int k = -1;
    if (paramString1.length() > 0)
    {
      i = _serializerVocabulary.namespaceName.get(paramString1);
      if (i == -1) {
        throw new SAXException(CommonResourceBundle.getInstance().getString("message.namespaceURINotIndexed", new Object[] { Integer.valueOf(i) }));
      }
      if (((String)localObject).length() > 0)
      {
        j = _serializerVocabulary.prefix.get((String)localObject);
        if (j == -1) {
          throw new SAXException(CommonResourceBundle.getInstance().getString("message.prefixNotIndexed", new Object[] { Integer.valueOf(j) }));
        }
      }
    }
    k = _serializerVocabulary.localName.obtainIndex(paramString3);
    if (k == -1)
    {
      _parserVocabulary.localName.add(paramString3);
      k = _parserVocabulary.localName.getSize() - 1;
    }
    QualifiedName localQualifiedName = new QualifiedName((String)localObject, paramString1, paramString3, paramLocalNameQualifiedNamesMap.getNextIndex(), j, i, k);
    if (paramBoolean) {
      localQualifiedName.createAttributeValues(256);
    }
    localEntry.addQualifiedName(localQualifiedName);
    paramQualifiedNameArray.add(localQualifiedName);
    paramSet.add(localQualifiedName.getQName());
  }
  
  public static String getPrefixFromQualifiedName(String paramString)
  {
    int i = paramString.indexOf(':');
    String str = "";
    if (i != -1) {
      str = paramString.substring(0, i);
    }
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\tools\VocabularyGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */