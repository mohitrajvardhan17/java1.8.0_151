package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.istack.internal.FinalArrayList;
import com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler;
import com.sun.xml.internal.bind.v2.runtime.Name;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;

public class C14nXmlOutput
  extends UTF8XmlOutput
{
  private StaticAttribute[] staticAttributes = new StaticAttribute[8];
  private int len = 0;
  private int[] nsBuf = new int[8];
  private final FinalArrayList<DynamicAttribute> otherAttributes = new FinalArrayList();
  private final boolean namedAttributesAreOrdered;
  
  public C14nXmlOutput(OutputStream paramOutputStream, Encoded[] paramArrayOfEncoded, boolean paramBoolean, CharacterEscapeHandler paramCharacterEscapeHandler)
  {
    super(paramOutputStream, paramArrayOfEncoded, paramCharacterEscapeHandler);
    namedAttributesAreOrdered = paramBoolean;
    for (int i = 0; i < staticAttributes.length; i++) {
      staticAttributes[i] = new StaticAttribute();
    }
  }
  
  public void attribute(Name paramName, String paramString)
    throws IOException
  {
    if (staticAttributes.length == len)
    {
      int i = len * 2;
      StaticAttribute[] arrayOfStaticAttribute = new StaticAttribute[i];
      System.arraycopy(staticAttributes, 0, arrayOfStaticAttribute, 0, len);
      for (int j = len; j < i; j++) {
        staticAttributes[j] = new StaticAttribute();
      }
      staticAttributes = arrayOfStaticAttribute;
    }
    staticAttributes[(len++)].set(paramName, paramString);
  }
  
  public void attribute(int paramInt, String paramString1, String paramString2)
    throws IOException
  {
    otherAttributes.add(new DynamicAttribute(paramInt, paramString1, paramString2));
  }
  
  public void endStartTag()
    throws IOException
  {
    int i;
    if (otherAttributes.isEmpty())
    {
      if (len != 0)
      {
        if (!namedAttributesAreOrdered) {
          Arrays.sort(staticAttributes, 0, len);
        }
        for (i = 0; i < len; i++) {
          staticAttributes[i].write();
        }
        len = 0;
      }
    }
    else
    {
      for (i = 0; i < len; i++) {
        otherAttributes.add(staticAttributes[i].toDynamicAttribute());
      }
      len = 0;
      Collections.sort(otherAttributes);
      i = otherAttributes.size();
      for (int j = 0; j < i; j++)
      {
        DynamicAttribute localDynamicAttribute = (DynamicAttribute)otherAttributes.get(j);
        super.attribute(prefix, localName, value);
      }
      otherAttributes.clear();
    }
    super.endStartTag();
  }
  
  protected void writeNsDecls(int paramInt)
    throws IOException
  {
    int i = nsContext.getCurrent().count();
    if (i == 0) {
      return;
    }
    if (i > nsBuf.length) {
      nsBuf = new int[i];
    }
    for (int j = i - 1; j >= 0; j--) {
      nsBuf[j] = (paramInt + j);
    }
    for (j = 0; j < i; j++) {
      for (int k = j + 1; k < i; k++)
      {
        String str1 = nsContext.getPrefix(nsBuf[j]);
        String str2 = nsContext.getPrefix(nsBuf[k]);
        if (str1.compareTo(str2) > 0)
        {
          int m = nsBuf[k];
          nsBuf[k] = nsBuf[j];
          nsBuf[j] = m;
        }
      }
    }
    for (j = 0; j < i; j++) {
      writeNsDecl(nsBuf[j]);
    }
  }
  
  final class DynamicAttribute
    implements Comparable<DynamicAttribute>
  {
    final int prefix;
    final String localName;
    final String value;
    
    public DynamicAttribute(int paramInt, String paramString1, String paramString2)
    {
      prefix = paramInt;
      localName = paramString1;
      value = paramString2;
    }
    
    private String getURI()
    {
      if (prefix == -1) {
        return "";
      }
      return nsContext.getNamespaceURI(prefix);
    }
    
    public int compareTo(DynamicAttribute paramDynamicAttribute)
    {
      int i = getURI().compareTo(paramDynamicAttribute.getURI());
      if (i != 0) {
        return i;
      }
      return localName.compareTo(localName);
    }
  }
  
  final class StaticAttribute
    implements Comparable<StaticAttribute>
  {
    Name name;
    String value;
    
    StaticAttribute() {}
    
    public void set(Name paramName, String paramString)
    {
      name = paramName;
      value = paramString;
    }
    
    void write()
      throws IOException
    {
      C14nXmlOutput.this.attribute(name, value);
    }
    
    C14nXmlOutput.DynamicAttribute toDynamicAttribute()
    {
      int i = name.nsUriIndex;
      int j;
      if (i == -1) {
        j = -1;
      } else {
        j = nsUriIndex2prefixIndex[i];
      }
      return new C14nXmlOutput.DynamicAttribute(C14nXmlOutput.this, j, name.localName, value);
    }
    
    public int compareTo(StaticAttribute paramStaticAttribute)
    {
      return name.compareTo(name);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\output\C14nXmlOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */