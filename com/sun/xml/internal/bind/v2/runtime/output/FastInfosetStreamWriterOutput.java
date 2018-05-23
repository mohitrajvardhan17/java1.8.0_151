package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Base64Data;
import com.sun.xml.internal.fastinfoset.stax.StAXDocumentSerializer;
import com.sun.xml.internal.org.jvnet.fastinfoset.VocabularyApplicationData;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import javax.xml.bind.JAXBContext;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public final class FastInfosetStreamWriterOutput
  extends XMLStreamWriterOutput
{
  private final StAXDocumentSerializer fiout;
  private final Encoded[] localNames;
  private final TablesPerJAXBContext tables;
  
  public FastInfosetStreamWriterOutput(StAXDocumentSerializer paramStAXDocumentSerializer, JAXBContextImpl paramJAXBContextImpl)
  {
    super(paramStAXDocumentSerializer);
    fiout = paramStAXDocumentSerializer;
    localNames = paramJAXBContextImpl.getUTF8NameTable();
    VocabularyApplicationData localVocabularyApplicationData = fiout.getVocabularyApplicationData();
    AppData localAppData = null;
    if ((localVocabularyApplicationData == null) || (!(localVocabularyApplicationData instanceof AppData)))
    {
      localAppData = new AppData();
      fiout.setVocabularyApplicationData(localAppData);
    }
    else
    {
      localAppData = (AppData)localVocabularyApplicationData;
    }
    TablesPerJAXBContext localTablesPerJAXBContext = (TablesPerJAXBContext)contexts.get(paramJAXBContextImpl);
    if (localTablesPerJAXBContext != null)
    {
      tables = localTablesPerJAXBContext;
      tables.clearOrResetTables(paramStAXDocumentSerializer.getLocalNameIndex());
    }
    else
    {
      tables = new TablesPerJAXBContext(paramJAXBContextImpl, paramStAXDocumentSerializer.getLocalNameIndex());
      contexts.put(paramJAXBContextImpl, tables);
    }
  }
  
  public void startDocument(XMLSerializer paramXMLSerializer, boolean paramBoolean, int[] paramArrayOfInt, NamespaceContextImpl paramNamespaceContextImpl)
    throws IOException, SAXException, XMLStreamException
  {
    super.startDocument(paramXMLSerializer, paramBoolean, paramArrayOfInt, paramNamespaceContextImpl);
    if (paramBoolean) {
      fiout.initiateLowLevelWriting();
    }
  }
  
  public void endDocument(boolean paramBoolean)
    throws IOException, SAXException, XMLStreamException
  {
    super.endDocument(paramBoolean);
  }
  
  public void beginStartTag(Name paramName)
    throws IOException
  {
    fiout.writeLowLevelTerminationAndMark();
    if (nsContext.getCurrent().count() == 0)
    {
      int i = tables.elementIndexes[qNameIndex] - tables.indexOffset;
      int j = nsUriIndex2prefixIndex[nsUriIndex];
      if ((i >= 0) && (tables.elementIndexPrefixes[qNameIndex] == j))
      {
        fiout.writeLowLevelStartElementIndexed(0, i);
      }
      else
      {
        tables.elementIndexes[qNameIndex] = (fiout.getNextElementIndex() + tables.indexOffset);
        tables.elementIndexPrefixes[qNameIndex] = j;
        writeLiteral(60, paramName, nsContext.getPrefix(j), nsContext.getNamespaceURI(j));
      }
    }
    else
    {
      beginStartTagWithNamespaces(paramName);
    }
  }
  
  public void beginStartTagWithNamespaces(Name paramName)
    throws IOException
  {
    NamespaceContextImpl.Element localElement = nsContext.getCurrent();
    fiout.writeLowLevelStartNamespaces();
    for (int i = localElement.count() - 1; i >= 0; i--)
    {
      String str = localElement.getNsUri(i);
      if ((str.length() != 0) || (localElement.getBase() != 1)) {
        fiout.writeLowLevelNamespace(localElement.getPrefix(i), str);
      }
    }
    fiout.writeLowLevelEndNamespaces();
    i = tables.elementIndexes[qNameIndex] - tables.indexOffset;
    int j = nsUriIndex2prefixIndex[nsUriIndex];
    if ((i >= 0) && (tables.elementIndexPrefixes[qNameIndex] == j))
    {
      fiout.writeLowLevelStartElementIndexed(0, i);
    }
    else
    {
      tables.elementIndexes[qNameIndex] = (fiout.getNextElementIndex() + tables.indexOffset);
      tables.elementIndexPrefixes[qNameIndex] = j;
      writeLiteral(60, paramName, nsContext.getPrefix(j), nsContext.getNamespaceURI(j));
    }
  }
  
  public void attribute(Name paramName, String paramString)
    throws IOException
  {
    fiout.writeLowLevelStartAttributes();
    int i = tables.attributeIndexes[qNameIndex] - tables.indexOffset;
    if (i >= 0)
    {
      fiout.writeLowLevelAttributeIndexed(i);
    }
    else
    {
      tables.attributeIndexes[qNameIndex] = (fiout.getNextAttributeIndex() + tables.indexOffset);
      int j = nsUriIndex;
      if (j == -1)
      {
        writeLiteral(120, paramName, "", "");
      }
      else
      {
        int k = nsUriIndex2prefixIndex[j];
        writeLiteral(120, paramName, nsContext.getPrefix(k), nsContext.getNamespaceURI(k));
      }
    }
    fiout.writeLowLevelAttributeValue(paramString);
  }
  
  private void writeLiteral(int paramInt, Name paramName, String paramString1, String paramString2)
    throws IOException
  {
    int i = tables.localNameIndexes[localNameIndex] - tables.indexOffset;
    if (i < 0)
    {
      tables.localNameIndexes[localNameIndex] = (fiout.getNextLocalNameIndex() + tables.indexOffset);
      fiout.writeLowLevelStartNameLiteral(paramInt, paramString1, localNames[localNameIndex].buf, paramString2);
    }
    else
    {
      fiout.writeLowLevelStartNameLiteral(paramInt, paramString1, i, paramString2);
    }
  }
  
  public void endStartTag()
    throws IOException
  {
    fiout.writeLowLevelEndStartElement();
  }
  
  public void endTag(Name paramName)
    throws IOException
  {
    fiout.writeLowLevelEndElement();
  }
  
  public void endTag(int paramInt, String paramString)
    throws IOException
  {
    fiout.writeLowLevelEndElement();
  }
  
  public void text(Pcdata paramPcdata, boolean paramBoolean)
    throws IOException
  {
    if (paramBoolean) {
      fiout.writeLowLevelText(" ");
    }
    if (!(paramPcdata instanceof Base64Data))
    {
      int i = paramPcdata.length();
      if (i < buf.length)
      {
        paramPcdata.writeTo(buf, 0);
        fiout.writeLowLevelText(buf, i);
      }
      else
      {
        fiout.writeLowLevelText(paramPcdata.toString());
      }
    }
    else
    {
      Base64Data localBase64Data = (Base64Data)paramPcdata;
      fiout.writeLowLevelOctets(localBase64Data.get(), localBase64Data.getDataLen());
    }
  }
  
  public void text(String paramString, boolean paramBoolean)
    throws IOException
  {
    if (paramBoolean) {
      fiout.writeLowLevelText(" ");
    }
    fiout.writeLowLevelText(paramString);
  }
  
  public void beginStartTag(int paramInt, String paramString)
    throws IOException
  {
    fiout.writeLowLevelTerminationAndMark();
    int i = 0;
    if (nsContext.getCurrent().count() > 0)
    {
      NamespaceContextImpl.Element localElement = nsContext.getCurrent();
      fiout.writeLowLevelStartNamespaces();
      for (int j = localElement.count() - 1; j >= 0; j--)
      {
        String str = localElement.getNsUri(j);
        if ((str.length() != 0) || (localElement.getBase() != 1)) {
          fiout.writeLowLevelNamespace(localElement.getPrefix(j), str);
        }
      }
      fiout.writeLowLevelEndNamespaces();
      i = 0;
    }
    boolean bool = fiout.writeLowLevelStartElement(i, nsContext.getPrefix(paramInt), paramString, nsContext.getNamespaceURI(paramInt));
    if (!bool) {
      tables.incrementMaxIndexValue();
    }
  }
  
  public void attribute(int paramInt, String paramString1, String paramString2)
    throws IOException
  {
    fiout.writeLowLevelStartAttributes();
    boolean bool;
    if (paramInt == -1) {
      bool = fiout.writeLowLevelAttribute("", "", paramString1);
    } else {
      bool = fiout.writeLowLevelAttribute(nsContext.getPrefix(paramInt), nsContext.getNamespaceURI(paramInt), paramString1);
    }
    if (!bool) {
      tables.incrementMaxIndexValue();
    }
    fiout.writeLowLevelAttributeValue(paramString2);
  }
  
  static final class AppData
    implements VocabularyApplicationData
  {
    final Map<JAXBContext, FastInfosetStreamWriterOutput.TablesPerJAXBContext> contexts = new WeakHashMap();
    final Collection<FastInfosetStreamWriterOutput.TablesPerJAXBContext> collectionOfContexts = contexts.values();
    
    AppData() {}
    
    public void clear()
    {
      Iterator localIterator = collectionOfContexts.iterator();
      while (localIterator.hasNext())
      {
        FastInfosetStreamWriterOutput.TablesPerJAXBContext localTablesPerJAXBContext = (FastInfosetStreamWriterOutput.TablesPerJAXBContext)localIterator.next();
        localTablesPerJAXBContext.requireClearTables();
      }
    }
  }
  
  static final class TablesPerJAXBContext
  {
    final int[] elementIndexes;
    final int[] elementIndexPrefixes;
    final int[] attributeIndexes;
    final int[] localNameIndexes;
    int indexOffset;
    int maxIndex;
    boolean requiresClear;
    
    TablesPerJAXBContext(JAXBContextImpl paramJAXBContextImpl, int paramInt)
    {
      elementIndexes = new int[paramJAXBContextImpl.getNumberOfElementNames()];
      elementIndexPrefixes = new int[paramJAXBContextImpl.getNumberOfElementNames()];
      attributeIndexes = new int[paramJAXBContextImpl.getNumberOfAttributeNames()];
      localNameIndexes = new int[paramJAXBContextImpl.getNumberOfLocalNames()];
      indexOffset = 1;
      maxIndex = (paramInt + elementIndexes.length + attributeIndexes.length);
    }
    
    public void requireClearTables()
    {
      requiresClear = true;
    }
    
    public void clearOrResetTables(int paramInt)
    {
      if (requiresClear)
      {
        requiresClear = false;
        indexOffset += maxIndex;
        maxIndex = (paramInt + elementIndexes.length + attributeIndexes.length);
        if (indexOffset + maxIndex < 0) {
          clearAll();
        }
      }
      else
      {
        maxIndex = (paramInt + elementIndexes.length + attributeIndexes.length);
        if (indexOffset + maxIndex < 0) {
          resetAll();
        }
      }
    }
    
    private void clearAll()
    {
      clear(elementIndexes);
      clear(attributeIndexes);
      clear(localNameIndexes);
      indexOffset = 1;
    }
    
    private void clear(int[] paramArrayOfInt)
    {
      for (int i = 0; i < paramArrayOfInt.length; i++) {
        paramArrayOfInt[i] = 0;
      }
    }
    
    public void incrementMaxIndexValue()
    {
      maxIndex += 1;
      if (indexOffset + maxIndex < 0) {
        resetAll();
      }
    }
    
    private void resetAll()
    {
      clear(elementIndexes);
      clear(attributeIndexes);
      clear(localNameIndexes);
      indexOffset = 1;
    }
    
    private void reset(int[] paramArrayOfInt)
    {
      for (int i = 0; i < paramArrayOfInt.length; i++) {
        if (paramArrayOfInt[i] > indexOffset) {
          paramArrayOfInt[i] = (paramArrayOfInt[i] - indexOffset + 1);
        } else {
          paramArrayOfInt[i] = 0;
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\output\FastInfosetStreamWriterOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */