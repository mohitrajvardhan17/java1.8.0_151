package javax.swing.text.html.parser;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import sun.awt.AppContext;

public class DTD
  implements DTDConstants
{
  public String name;
  public Vector<Element> elements = new Vector();
  public Hashtable<String, Element> elementHash = new Hashtable();
  public Hashtable<Object, Entity> entityHash = new Hashtable();
  public final Element pcdata = getElement("#pcdata");
  public final Element html = getElement("html");
  public final Element meta = getElement("meta");
  public final Element base = getElement("base");
  public final Element isindex = getElement("isindex");
  public final Element head = getElement("head");
  public final Element body = getElement("body");
  public final Element applet = getElement("applet");
  public final Element param = getElement("param");
  public final Element p = getElement("p");
  public final Element title = getElement("title");
  final Element style = getElement("style");
  final Element link = getElement("link");
  final Element script = getElement("script");
  public static final int FILE_VERSION = 1;
  private static final Object DTD_HASH_KEY = new Object();
  
  protected DTD(String paramString)
  {
    name = paramString;
    defEntity("#RE", 65536, 13);
    defEntity("#RS", 65536, 10);
    defEntity("#SPACE", 65536, 32);
    defineElement("unknown", 17, false, true, null, null, null, null);
  }
  
  public String getName()
  {
    return name;
  }
  
  public Entity getEntity(String paramString)
  {
    return (Entity)entityHash.get(paramString);
  }
  
  public Entity getEntity(int paramInt)
  {
    return (Entity)entityHash.get(Integer.valueOf(paramInt));
  }
  
  boolean elementExists(String paramString)
  {
    return (!"unknown".equals(paramString)) && (elementHash.get(paramString) != null);
  }
  
  public Element getElement(String paramString)
  {
    Element localElement = (Element)elementHash.get(paramString);
    if (localElement == null)
    {
      localElement = new Element(paramString, elements.size());
      elements.addElement(localElement);
      elementHash.put(paramString, localElement);
    }
    return localElement;
  }
  
  public Element getElement(int paramInt)
  {
    return (Element)elements.elementAt(paramInt);
  }
  
  public Entity defineEntity(String paramString, int paramInt, char[] paramArrayOfChar)
  {
    Entity localEntity = (Entity)entityHash.get(paramString);
    if (localEntity == null)
    {
      localEntity = new Entity(paramString, paramInt, paramArrayOfChar);
      entityHash.put(paramString, localEntity);
      if (((paramInt & 0x10000) != 0) && (paramArrayOfChar.length == 1)) {
        switch (paramInt & 0xFFFEFFFF)
        {
        case 1: 
        case 11: 
          entityHash.put(Integer.valueOf(paramArrayOfChar[0]), localEntity);
        }
      }
    }
    return localEntity;
  }
  
  public Element defineElement(String paramString, int paramInt, boolean paramBoolean1, boolean paramBoolean2, ContentModel paramContentModel, BitSet paramBitSet1, BitSet paramBitSet2, AttributeList paramAttributeList)
  {
    Element localElement = getElement(paramString);
    type = paramInt;
    oStart = paramBoolean1;
    oEnd = paramBoolean2;
    content = paramContentModel;
    exclusions = paramBitSet1;
    inclusions = paramBitSet2;
    atts = paramAttributeList;
    return localElement;
  }
  
  public void defineAttributes(String paramString, AttributeList paramAttributeList)
  {
    Element localElement = getElement(paramString);
    atts = paramAttributeList;
  }
  
  public Entity defEntity(String paramString, int paramInt1, int paramInt2)
  {
    char[] arrayOfChar = { (char)paramInt2 };
    return defineEntity(paramString, paramInt1, arrayOfChar);
  }
  
  protected Entity defEntity(String paramString1, int paramInt, String paramString2)
  {
    int i = paramString2.length();
    char[] arrayOfChar = new char[i];
    paramString2.getChars(0, i, arrayOfChar, 0);
    return defineEntity(paramString1, paramInt, arrayOfChar);
  }
  
  protected Element defElement(String paramString, int paramInt, boolean paramBoolean1, boolean paramBoolean2, ContentModel paramContentModel, String[] paramArrayOfString1, String[] paramArrayOfString2, AttributeList paramAttributeList)
  {
    BitSet localBitSet = null;
    if ((paramArrayOfString1 != null) && (paramArrayOfString1.length > 0))
    {
      localBitSet = new BitSet();
      for (String str1 : paramArrayOfString1) {
        if (str1.length() > 0) {
          localBitSet.set(getElement(str1).getIndex());
        }
      }
    }
    ??? = null;
    if ((paramArrayOfString2 != null) && (paramArrayOfString2.length > 0))
    {
      ??? = new BitSet();
      for (String str2 : paramArrayOfString2) {
        if (str2.length() > 0) {
          ((BitSet)???).set(getElement(str2).getIndex());
        }
      }
    }
    return defineElement(paramString, paramInt, paramBoolean1, paramBoolean2, paramContentModel, localBitSet, (BitSet)???, paramAttributeList);
  }
  
  protected AttributeList defAttributeList(String paramString1, int paramInt1, int paramInt2, String paramString2, String paramString3, AttributeList paramAttributeList)
  {
    Vector localVector = null;
    if (paramString3 != null)
    {
      localVector = new Vector();
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString3, "|");
      while (localStringTokenizer.hasMoreTokens())
      {
        String str = localStringTokenizer.nextToken();
        if (str.length() > 0) {
          localVector.addElement(str);
        }
      }
    }
    return new AttributeList(paramString1, paramInt1, paramInt2, paramString2, localVector, paramAttributeList);
  }
  
  protected ContentModel defContentModel(int paramInt, Object paramObject, ContentModel paramContentModel)
  {
    return new ContentModel(paramInt, paramObject, paramContentModel);
  }
  
  public String toString()
  {
    return name;
  }
  
  public static void putDTDHash(String paramString, DTD paramDTD)
  {
    getDtdHash().put(paramString, paramDTD);
  }
  
  public static DTD getDTD(String paramString)
    throws IOException
  {
    paramString = paramString.toLowerCase();
    DTD localDTD = (DTD)getDtdHash().get(paramString);
    if (localDTD == null) {
      localDTD = new DTD(paramString);
    }
    return localDTD;
  }
  
  private static Hashtable<String, DTD> getDtdHash()
  {
    AppContext localAppContext = AppContext.getAppContext();
    Hashtable localHashtable = (Hashtable)localAppContext.get(DTD_HASH_KEY);
    if (localHashtable == null)
    {
      localHashtable = new Hashtable();
      localAppContext.put(DTD_HASH_KEY, localHashtable);
    }
    return localHashtable;
  }
  
  public void read(DataInputStream paramDataInputStream)
    throws IOException
  {
    if (paramDataInputStream.readInt() != 1) {}
    String[] arrayOfString1 = new String[paramDataInputStream.readShort()];
    for (int i = 0; i < arrayOfString1.length; i++) {
      arrayOfString1[i] = paramDataInputStream.readUTF();
    }
    i = paramDataInputStream.readShort();
    int k;
    int m;
    for (int j = 0; j < i; j++)
    {
      k = paramDataInputStream.readShort();
      m = paramDataInputStream.readByte();
      String str = paramDataInputStream.readUTF();
      defEntity(arrayOfString1[k], m | 0x10000, str);
    }
    i = paramDataInputStream.readShort();
    for (j = 0; j < i; j++)
    {
      k = paramDataInputStream.readShort();
      m = paramDataInputStream.readByte();
      int n = paramDataInputStream.readByte();
      ContentModel localContentModel = readContentModel(paramDataInputStream, arrayOfString1);
      String[] arrayOfString2 = readNameArray(paramDataInputStream, arrayOfString1);
      String[] arrayOfString3 = readNameArray(paramDataInputStream, arrayOfString1);
      AttributeList localAttributeList = readAttributeList(paramDataInputStream, arrayOfString1);
      defElement(arrayOfString1[k], m, (n & 0x1) != 0, (n & 0x2) != 0, localContentModel, arrayOfString2, arrayOfString3, localAttributeList);
    }
  }
  
  private ContentModel readContentModel(DataInputStream paramDataInputStream, String[] paramArrayOfString)
    throws IOException
  {
    int i = paramDataInputStream.readByte();
    int j;
    Object localObject;
    ContentModel localContentModel;
    switch (i)
    {
    case 0: 
      return null;
    case 1: 
      j = paramDataInputStream.readByte();
      localObject = readContentModel(paramDataInputStream, paramArrayOfString);
      localContentModel = readContentModel(paramDataInputStream, paramArrayOfString);
      return defContentModel(j, localObject, localContentModel);
    case 2: 
      j = paramDataInputStream.readByte();
      localObject = getElement(paramArrayOfString[paramDataInputStream.readShort()]);
      localContentModel = readContentModel(paramDataInputStream, paramArrayOfString);
      return defContentModel(j, localObject, localContentModel);
    }
    throw new IOException("bad bdtd");
  }
  
  private String[] readNameArray(DataInputStream paramDataInputStream, String[] paramArrayOfString)
    throws IOException
  {
    int i = paramDataInputStream.readShort();
    if (i == 0) {
      return null;
    }
    String[] arrayOfString = new String[i];
    for (int j = 0; j < i; j++) {
      arrayOfString[j] = paramArrayOfString[paramDataInputStream.readShort()];
    }
    return arrayOfString;
  }
  
  private AttributeList readAttributeList(DataInputStream paramDataInputStream, String[] paramArrayOfString)
    throws IOException
  {
    AttributeList localAttributeList = null;
    for (int i = paramDataInputStream.readByte(); i > 0; i--)
    {
      int j = paramDataInputStream.readShort();
      int k = paramDataInputStream.readByte();
      int m = paramDataInputStream.readByte();
      int n = paramDataInputStream.readShort();
      String str = n == -1 ? null : paramArrayOfString[n];
      Vector localVector = null;
      int i1 = paramDataInputStream.readShort();
      if (i1 > 0)
      {
        localVector = new Vector(i1);
        for (int i2 = 0; i2 < i1; i2++) {
          localVector.addElement(paramArrayOfString[paramDataInputStream.readShort()]);
        }
      }
      localAttributeList = new AttributeList(paramArrayOfString[j], k, m, str, localVector, localAttributeList);
    }
    return localAttributeList;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\parser\DTD.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */