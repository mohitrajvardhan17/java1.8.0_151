package com.sun.xml.internal.stream.dtd.nonvalidating;

import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDContentModelSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDSource;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DTDGrammar
{
  public static final int TOP_LEVEL_SCOPE = -1;
  private static final int CHUNK_SHIFT = 8;
  private static final int CHUNK_SIZE = 256;
  private static final int CHUNK_MASK = 255;
  private static final int INITIAL_CHUNK_COUNT = 4;
  private static final short LIST_FLAG = 128;
  private static final short LIST_MASK = -129;
  private static final boolean DEBUG = false;
  protected XMLDTDSource fDTDSource = null;
  protected XMLDTDContentModelSource fDTDContentModelSource = null;
  protected int fCurrentElementIndex;
  protected int fCurrentAttributeIndex;
  protected boolean fReadingExternalDTD = false;
  private SymbolTable fSymbolTable;
  private ArrayList notationDecls = new ArrayList();
  private int fElementDeclCount = 0;
  private QName[][] fElementDeclName = new QName[4][];
  private short[][] fElementDeclType = new short[4][];
  private int[][] fElementDeclFirstAttributeDeclIndex = new int[4][];
  private int[][] fElementDeclLastAttributeDeclIndex = new int[4][];
  private int fAttributeDeclCount = 0;
  private QName[][] fAttributeDeclName = new QName[4][];
  private short[][] fAttributeDeclType = new short[4][];
  private String[][][] fAttributeDeclEnumeration = new String[4][][];
  private short[][] fAttributeDeclDefaultType = new short[4][];
  private String[][] fAttributeDeclDefaultValue = new String[4][];
  private String[][] fAttributeDeclNonNormalizedDefaultValue = new String[4][];
  private int[][] fAttributeDeclNextAttributeDeclIndex = new int[4][];
  private final Map<String, Integer> fElementIndexMap = new HashMap();
  private final QName fQName = new QName();
  protected XMLAttributeDecl fAttributeDecl = new XMLAttributeDecl();
  private XMLElementDecl fElementDecl = new XMLElementDecl();
  private XMLSimpleType fSimpleType = new XMLSimpleType();
  Map<String, XMLElementDecl> fElementDeclTab = new HashMap();
  
  public DTDGrammar(SymbolTable paramSymbolTable)
  {
    fSymbolTable = paramSymbolTable;
  }
  
  public int getAttributeDeclIndex(int paramInt, String paramString)
  {
    if (paramInt == -1) {
      return -1;
    }
    for (int i = getFirstAttributeDeclIndex(paramInt); i != -1; i = getNextAttributeDeclIndex(i))
    {
      getAttributeDecl(i, fAttributeDecl);
      if ((fAttributeDecl.name.rawname == paramString) || (paramString.equals(fAttributeDecl.name.rawname))) {
        return i;
      }
    }
    return -1;
  }
  
  public void startDTD(XMLLocator paramXMLLocator, Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void elementDecl(String paramString1, String paramString2, Augmentations paramAugmentations)
    throws XNIException
  {
    XMLElementDecl localXMLElementDecl1 = (XMLElementDecl)fElementDeclTab.get(paramString1);
    if (localXMLElementDecl1 != null)
    {
      if (type == -1) {
        fCurrentElementIndex = getElementDeclIndex(paramString1);
      }
    }
    else {
      fCurrentElementIndex = createElementDecl();
    }
    XMLElementDecl localXMLElementDecl2 = new XMLElementDecl();
    QName localQName = new QName(null, paramString1, paramString1, null);
    name.setValues(localQName);
    scope = -1;
    if (paramString2.equals("EMPTY")) {
      type = 1;
    } else if (paramString2.equals("ANY")) {
      type = 0;
    } else if (paramString2.startsWith("(")) {
      if (paramString2.indexOf("#PCDATA") > 0) {
        type = 2;
      } else {
        type = 3;
      }
    }
    fElementDeclTab.put(paramString1, localXMLElementDecl2);
    fElementDecl = localXMLElementDecl2;
    setElementDecl(fCurrentElementIndex, fElementDecl);
    int i = fCurrentElementIndex >> 8;
    ensureElementDeclCapacity(i);
  }
  
  public void attributeDecl(String paramString1, String paramString2, String paramString3, String[] paramArrayOfString, String paramString4, XMLString paramXMLString1, XMLString paramXMLString2, Augmentations paramAugmentations)
    throws XNIException
  {
    if ((paramString3 != XMLSymbols.fCDATASymbol) && (paramXMLString1 != null)) {
      normalizeDefaultAttrValue(paramXMLString1);
    }
    if (!fElementDeclTab.containsKey(paramString1))
    {
      fCurrentElementIndex = createElementDecl();
      XMLElementDecl localXMLElementDecl = new XMLElementDecl();
      name.setValues(null, paramString1, paramString1, null);
      scope = -1;
      fElementDeclTab.put(paramString1, localXMLElementDecl);
      setElementDecl(fCurrentElementIndex, localXMLElementDecl);
    }
    int i = getElementDeclIndex(paramString1);
    if (getAttributeDeclIndex(i, paramString2) != -1) {
      return;
    }
    fCurrentAttributeIndex = createAttributeDecl();
    fSimpleType.clear();
    if (paramString4 != null) {
      if (paramString4.equals("#FIXED")) {
        fSimpleType.defaultType = 1;
      } else if (paramString4.equals("#IMPLIED")) {
        fSimpleType.defaultType = 0;
      } else if (paramString4.equals("#REQUIRED")) {
        fSimpleType.defaultType = 2;
      }
    }
    fSimpleType.defaultValue = (paramXMLString1 != null ? paramXMLString1.toString() : null);
    fSimpleType.nonNormalizedDefaultValue = (paramXMLString2 != null ? paramXMLString2.toString() : null);
    fSimpleType.enumeration = paramArrayOfString;
    if (paramString3.equals("CDATA"))
    {
      fSimpleType.type = 0;
    }
    else if (paramString3.equals("ID"))
    {
      fSimpleType.type = 3;
    }
    else if (paramString3.startsWith("IDREF"))
    {
      fSimpleType.type = 4;
      if (paramString3.indexOf("S") > 0) {
        fSimpleType.list = true;
      }
    }
    else if (paramString3.equals("ENTITIES"))
    {
      fSimpleType.type = 1;
      fSimpleType.list = true;
    }
    else if (paramString3.equals("ENTITY"))
    {
      fSimpleType.type = 1;
    }
    else if (paramString3.equals("NMTOKENS"))
    {
      fSimpleType.type = 5;
      fSimpleType.list = true;
    }
    else if (paramString3.equals("NMTOKEN"))
    {
      fSimpleType.type = 5;
    }
    else if (paramString3.startsWith("NOTATION"))
    {
      fSimpleType.type = 6;
    }
    else if (paramString3.startsWith("ENUMERATION"))
    {
      fSimpleType.type = 2;
    }
    else
    {
      System.err.println("!!! unknown attribute type " + paramString3);
    }
    fQName.setValues(null, paramString2, paramString2, null);
    fAttributeDecl.setValues(fQName, fSimpleType, false);
    setAttributeDecl(i, fCurrentAttributeIndex, fAttributeDecl);
    int j = fCurrentAttributeIndex >> 8;
    ensureAttributeDeclCapacity(j);
  }
  
  public SymbolTable getSymbolTable()
  {
    return fSymbolTable;
  }
  
  public int getFirstElementDeclIndex()
  {
    return fElementDeclCount >= 0 ? 0 : -1;
  }
  
  public int getNextElementDeclIndex(int paramInt)
  {
    return paramInt < fElementDeclCount - 1 ? paramInt + 1 : -1;
  }
  
  public int getElementDeclIndex(String paramString)
  {
    Integer localInteger = (Integer)fElementIndexMap.get(paramString);
    if (localInteger == null) {
      localInteger = Integer.valueOf(-1);
    }
    return localInteger.intValue();
  }
  
  public int getElementDeclIndex(QName paramQName)
  {
    return getElementDeclIndex(rawname);
  }
  
  public short getContentSpecType(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= fElementDeclCount)) {
      return -1;
    }
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    if (fElementDeclType[i][j] == -1) {
      return -1;
    }
    return (short)(fElementDeclType[i][j] & 0xFF7F);
  }
  
  public boolean getElementDecl(int paramInt, XMLElementDecl paramXMLElementDecl)
  {
    if ((paramInt < 0) || (paramInt >= fElementDeclCount)) {
      return false;
    }
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    name.setValues(fElementDeclName[i][j]);
    if (fElementDeclType[i][j] == -1)
    {
      type = -1;
      simpleType.list = false;
    }
    else
    {
      type = ((short)(fElementDeclType[i][j] & 0xFF7F));
      simpleType.list = ((fElementDeclType[i][j] & 0x80) != 0);
    }
    simpleType.defaultType = -1;
    simpleType.defaultValue = null;
    return true;
  }
  
  public int getFirstAttributeDeclIndex(int paramInt)
  {
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    return fElementDeclFirstAttributeDeclIndex[i][j];
  }
  
  public int getNextAttributeDeclIndex(int paramInt)
  {
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    return fAttributeDeclNextAttributeDeclIndex[i][j];
  }
  
  public boolean getAttributeDecl(int paramInt, XMLAttributeDecl paramXMLAttributeDecl)
  {
    if ((paramInt < 0) || (paramInt >= fAttributeDeclCount)) {
      return false;
    }
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    name.setValues(fAttributeDeclName[i][j]);
    short s;
    boolean bool;
    if (fAttributeDeclType[i][j] == -1)
    {
      s = -1;
      bool = false;
    }
    else
    {
      s = (short)(fAttributeDeclType[i][j] & 0xFF7F);
      bool = (fAttributeDeclType[i][j] & 0x80) != 0;
    }
    simpleType.setValues(s, fAttributeDeclName[i][j].localpart, fAttributeDeclEnumeration[i][j], bool, fAttributeDeclDefaultType[i][j], fAttributeDeclDefaultValue[i][j], fAttributeDeclNonNormalizedDefaultValue[i][j]);
    return true;
  }
  
  public boolean isCDATAAttribute(QName paramQName1, QName paramQName2)
  {
    int i = getElementDeclIndex(paramQName1);
    return (!getAttributeDecl(i, fAttributeDecl)) || (fAttributeDecl.simpleType.type == 0);
  }
  
  public void printElements()
  {
    int i = 0;
    XMLElementDecl localXMLElementDecl = new XMLElementDecl();
    while (getElementDecl(i++, localXMLElementDecl)) {
      System.out.println("element decl: " + name + ", " + name.rawname);
    }
  }
  
  public void printAttributes(int paramInt)
  {
    int i = getFirstAttributeDeclIndex(paramInt);
    System.out.print(paramInt);
    System.out.print(" [");
    while (i != -1)
    {
      System.out.print(' ');
      System.out.print(i);
      printAttribute(i);
      i = getNextAttributeDeclIndex(i);
      if (i != -1) {
        System.out.print(",");
      }
    }
    System.out.println(" ]");
  }
  
  protected int createElementDecl()
  {
    int i = fElementDeclCount >> 8;
    int j = fElementDeclCount & 0xFF;
    ensureElementDeclCapacity(i);
    fElementDeclName[i][j] = new QName();
    fElementDeclType[i][j] = -1;
    fElementDeclFirstAttributeDeclIndex[i][j] = -1;
    fElementDeclLastAttributeDeclIndex[i][j] = -1;
    return fElementDeclCount++;
  }
  
  protected void setElementDecl(int paramInt, XMLElementDecl paramXMLElementDecl)
  {
    if ((paramInt < 0) || (paramInt >= fElementDeclCount)) {
      return;
    }
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    int k = scope;
    fElementDeclName[i][j].setValues(name);
    fElementDeclType[i][j] = type;
    if (simpleType.list == true)
    {
      int tmp79_77 = j;
      short[] tmp79_76 = fElementDeclType[i];
      tmp79_76[tmp79_77] = ((short)(tmp79_76[tmp79_77] | 0x80));
    }
    fElementIndexMap.put(name.rawname, Integer.valueOf(paramInt));
  }
  
  protected void setFirstAttributeDeclIndex(int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt1 >= fElementDeclCount)) {
      return;
    }
    int i = paramInt1 >> 8;
    int j = paramInt1 & 0xFF;
    fElementDeclFirstAttributeDeclIndex[i][j] = paramInt2;
  }
  
  protected int createAttributeDecl()
  {
    int i = fAttributeDeclCount >> 8;
    int j = fAttributeDeclCount & 0xFF;
    ensureAttributeDeclCapacity(i);
    fAttributeDeclName[i][j] = new QName();
    fAttributeDeclType[i][j] = -1;
    fAttributeDeclEnumeration[i][j] = null;
    fAttributeDeclDefaultType[i][j] = 0;
    fAttributeDeclDefaultValue[i][j] = null;
    fAttributeDeclNonNormalizedDefaultValue[i][j] = null;
    fAttributeDeclNextAttributeDeclIndex[i][j] = -1;
    return fAttributeDeclCount++;
  }
  
  protected void setAttributeDecl(int paramInt1, int paramInt2, XMLAttributeDecl paramXMLAttributeDecl)
  {
    int i = paramInt2 >> 8;
    int j = paramInt2 & 0xFF;
    fAttributeDeclName[i][j].setValues(name);
    fAttributeDeclType[i][j] = simpleType.type;
    if (simpleType.list)
    {
      int tmp66_64 = j;
      short[] tmp66_63 = fAttributeDeclType[i];
      tmp66_63[tmp66_64] = ((short)(tmp66_63[tmp66_64] | 0x80));
    }
    fAttributeDeclEnumeration[i][j] = simpleType.enumeration;
    fAttributeDeclDefaultType[i][j] = simpleType.defaultType;
    fAttributeDeclDefaultValue[i][j] = simpleType.defaultValue;
    fAttributeDeclNonNormalizedDefaultValue[i][j] = simpleType.nonNormalizedDefaultValue;
    int k = paramInt1 >> 8;
    int m = paramInt1 & 0xFF;
    for (int n = fElementDeclFirstAttributeDeclIndex[k][m]; (n != -1) && (n != paramInt2); n = fAttributeDeclNextAttributeDeclIndex[i][j])
    {
      i = n >> 8;
      j = n & 0xFF;
    }
    if (n == -1)
    {
      if (fElementDeclFirstAttributeDeclIndex[k][m] == -1)
      {
        fElementDeclFirstAttributeDeclIndex[k][m] = paramInt2;
      }
      else
      {
        n = fElementDeclLastAttributeDeclIndex[k][m];
        i = n >> 8;
        j = n & 0xFF;
        fAttributeDeclNextAttributeDeclIndex[i][j] = paramInt2;
      }
      fElementDeclLastAttributeDeclIndex[k][m] = paramInt2;
    }
  }
  
  public void notationDecl(String paramString, XMLResourceIdentifier paramXMLResourceIdentifier, Augmentations paramAugmentations)
    throws XNIException
  {
    XMLNotationDecl localXMLNotationDecl = new XMLNotationDecl();
    localXMLNotationDecl.setValues(paramString, paramXMLResourceIdentifier.getPublicId(), paramXMLResourceIdentifier.getLiteralSystemId(), paramXMLResourceIdentifier.getBaseSystemId());
    notationDecls.add(localXMLNotationDecl);
  }
  
  public List getNotationDecls()
  {
    return notationDecls;
  }
  
  private void printAttribute(int paramInt)
  {
    XMLAttributeDecl localXMLAttributeDecl = new XMLAttributeDecl();
    if (getAttributeDecl(paramInt, localXMLAttributeDecl))
    {
      System.out.print(" { ");
      System.out.print(name.localpart);
      System.out.print(" }");
    }
  }
  
  private void ensureElementDeclCapacity(int paramInt)
  {
    if (paramInt >= fElementDeclName.length)
    {
      fElementDeclName = resize(fElementDeclName, fElementDeclName.length * 2);
      fElementDeclType = resize(fElementDeclType, fElementDeclType.length * 2);
      fElementDeclFirstAttributeDeclIndex = resize(fElementDeclFirstAttributeDeclIndex, fElementDeclFirstAttributeDeclIndex.length * 2);
      fElementDeclLastAttributeDeclIndex = resize(fElementDeclLastAttributeDeclIndex, fElementDeclLastAttributeDeclIndex.length * 2);
    }
    else if (fElementDeclName[paramInt] != null)
    {
      return;
    }
    fElementDeclName[paramInt] = new QName['Ā'];
    fElementDeclType[paramInt] = new short['Ā'];
    fElementDeclFirstAttributeDeclIndex[paramInt] = new int['Ā'];
    fElementDeclLastAttributeDeclIndex[paramInt] = new int['Ā'];
  }
  
  private void ensureAttributeDeclCapacity(int paramInt)
  {
    if (paramInt >= fAttributeDeclName.length)
    {
      fAttributeDeclName = resize(fAttributeDeclName, fAttributeDeclName.length * 2);
      fAttributeDeclType = resize(fAttributeDeclType, fAttributeDeclType.length * 2);
      fAttributeDeclEnumeration = resize(fAttributeDeclEnumeration, fAttributeDeclEnumeration.length * 2);
      fAttributeDeclDefaultType = resize(fAttributeDeclDefaultType, fAttributeDeclDefaultType.length * 2);
      fAttributeDeclDefaultValue = resize(fAttributeDeclDefaultValue, fAttributeDeclDefaultValue.length * 2);
      fAttributeDeclNonNormalizedDefaultValue = resize(fAttributeDeclNonNormalizedDefaultValue, fAttributeDeclNonNormalizedDefaultValue.length * 2);
      fAttributeDeclNextAttributeDeclIndex = resize(fAttributeDeclNextAttributeDeclIndex, fAttributeDeclNextAttributeDeclIndex.length * 2);
    }
    else if (fAttributeDeclName[paramInt] != null)
    {
      return;
    }
    fAttributeDeclName[paramInt] = new QName['Ā'];
    fAttributeDeclType[paramInt] = new short['Ā'];
    fAttributeDeclEnumeration[paramInt] = new String['Ā'][];
    fAttributeDeclDefaultType[paramInt] = new short['Ā'];
    fAttributeDeclDefaultValue[paramInt] = new String['Ā'];
    fAttributeDeclNonNormalizedDefaultValue[paramInt] = new String['Ā'];
    fAttributeDeclNextAttributeDeclIndex[paramInt] = new int['Ā'];
  }
  
  private static short[][] resize(short[][] paramArrayOfShort, int paramInt)
  {
    short[][] arrayOfShort = new short[paramInt][];
    System.arraycopy(paramArrayOfShort, 0, arrayOfShort, 0, paramArrayOfShort.length);
    return arrayOfShort;
  }
  
  private static int[][] resize(int[][] paramArrayOfInt, int paramInt)
  {
    int[][] arrayOfInt = new int[paramInt][];
    System.arraycopy(paramArrayOfInt, 0, arrayOfInt, 0, paramArrayOfInt.length);
    return arrayOfInt;
  }
  
  private static QName[][] resize(QName[][] paramArrayOfQName, int paramInt)
  {
    QName[][] arrayOfQName = new QName[paramInt][];
    System.arraycopy(paramArrayOfQName, 0, arrayOfQName, 0, paramArrayOfQName.length);
    return arrayOfQName;
  }
  
  private static String[][] resize(String[][] paramArrayOfString, int paramInt)
  {
    String[][] arrayOfString = new String[paramInt][];
    System.arraycopy(paramArrayOfString, 0, arrayOfString, 0, paramArrayOfString.length);
    return arrayOfString;
  }
  
  private static String[][][] resize(String[][][] paramArrayOfString, int paramInt)
  {
    String[][][] arrayOfString = new String[paramInt][][];
    System.arraycopy(paramArrayOfString, 0, arrayOfString, 0, paramArrayOfString.length);
    return arrayOfString;
  }
  
  private boolean normalizeDefaultAttrValue(XMLString paramXMLString)
  {
    int i = length;
    int j = 1;
    int k = offset;
    int m = offset + length;
    for (int n = offset; n < m; n++) {
      if (ch[n] == ' ')
      {
        if (j == 0)
        {
          ch[(k++)] = ' ';
          j = 1;
        }
      }
      else
      {
        if (k != n) {
          ch[k] = ch[n];
        }
        k++;
        j = 0;
      }
    }
    if (k != m)
    {
      if (j != 0) {
        k--;
      }
      length = (k - offset);
      return true;
    }
    return false;
  }
  
  public void endDTD(Augmentations paramAugmentations)
    throws XNIException
  {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\dtd\nonvalidating\DTDGrammar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */