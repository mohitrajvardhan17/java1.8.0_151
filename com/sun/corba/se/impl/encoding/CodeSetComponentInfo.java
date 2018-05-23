package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public final class CodeSetComponentInfo
{
  private CodeSetComponent forCharData;
  private CodeSetComponent forWCharData;
  public static final CodeSetComponentInfo JAVASOFT_DEFAULT_CODESETS;
  public static final CodeSetContext LOCAL_CODE_SETS = new CodeSetContext(OSFCodeSetRegistry.ISO_8859_1.getNumber(), OSFCodeSetRegistry.UTF_16.getNumber());
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof CodeSetComponentInfo)) {
      return false;
    }
    CodeSetComponentInfo localCodeSetComponentInfo = (CodeSetComponentInfo)paramObject;
    return (forCharData.equals(forCharData)) && (forWCharData.equals(forWCharData));
  }
  
  public int hashCode()
  {
    return forCharData.hashCode() ^ forWCharData.hashCode();
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("CodeSetComponentInfo(");
    localStringBuffer.append("char_data:");
    localStringBuffer.append(forCharData.toString());
    localStringBuffer.append(" wchar_data:");
    localStringBuffer.append(forWCharData.toString());
    localStringBuffer.append(")");
    return localStringBuffer.toString();
  }
  
  public CodeSetComponentInfo()
  {
    forCharData = JAVASOFT_DEFAULT_CODESETSforCharData;
    forWCharData = JAVASOFT_DEFAULT_CODESETSforWCharData;
  }
  
  public CodeSetComponentInfo(CodeSetComponent paramCodeSetComponent1, CodeSetComponent paramCodeSetComponent2)
  {
    forCharData = paramCodeSetComponent1;
    forWCharData = paramCodeSetComponent2;
  }
  
  public void read(MarshalInputStream paramMarshalInputStream)
  {
    forCharData = new CodeSetComponent();
    forCharData.read(paramMarshalInputStream);
    forWCharData = new CodeSetComponent();
    forWCharData.read(paramMarshalInputStream);
  }
  
  public void write(MarshalOutputStream paramMarshalOutputStream)
  {
    forCharData.write(paramMarshalOutputStream);
    forWCharData.write(paramMarshalOutputStream);
  }
  
  public CodeSetComponent getCharComponent()
  {
    return forCharData;
  }
  
  public CodeSetComponent getWCharComponent()
  {
    return forWCharData;
  }
  
  public static CodeSetComponent createFromString(String paramString)
  {
    ORBUtilSystemException localORBUtilSystemException = ORBUtilSystemException.get("rpc.encoding");
    if ((paramString == null) || (paramString.length() == 0)) {
      throw localORBUtilSystemException.badCodeSetString();
    }
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ", ", false);
    int i = 0;
    int[] arrayOfInt = null;
    try
    {
      i = Integer.decode(localStringTokenizer.nextToken()).intValue();
      if (OSFCodeSetRegistry.lookupEntry(i) == null) {
        throw localORBUtilSystemException.unknownNativeCodeset(new Integer(i));
      }
      ArrayList localArrayList = new ArrayList(10);
      while (localStringTokenizer.hasMoreTokens())
      {
        Integer localInteger = Integer.decode(localStringTokenizer.nextToken());
        if (OSFCodeSetRegistry.lookupEntry(localInteger.intValue()) == null) {
          throw localORBUtilSystemException.unknownConversionCodeSet(localInteger);
        }
        localArrayList.add(localInteger);
      }
      arrayOfInt = new int[localArrayList.size()];
      for (int j = 0; j < arrayOfInt.length; j++) {
        arrayOfInt[j] = ((Integer)localArrayList.get(j)).intValue();
      }
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw localORBUtilSystemException.invalidCodeSetNumber(localNumberFormatException);
    }
    catch (NoSuchElementException localNoSuchElementException)
    {
      throw localORBUtilSystemException.invalidCodeSetString(localNoSuchElementException, paramString);
    }
    return new CodeSetComponent(i, arrayOfInt);
  }
  
  static
  {
    CodeSetComponent localCodeSetComponent1 = new CodeSetComponent(OSFCodeSetRegistry.ISO_8859_1.getNumber(), new int[] { OSFCodeSetRegistry.UTF_8.getNumber(), OSFCodeSetRegistry.ISO_646.getNumber() });
    CodeSetComponent localCodeSetComponent2 = new CodeSetComponent(OSFCodeSetRegistry.UTF_16.getNumber(), new int[] { OSFCodeSetRegistry.UCS_2.getNumber() });
    JAVASOFT_DEFAULT_CODESETS = new CodeSetComponentInfo(localCodeSetComponent1, localCodeSetComponent2);
  }
  
  public static final class CodeSetComponent
  {
    int nativeCodeSet;
    int[] conversionCodeSets;
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (!(paramObject instanceof CodeSetComponent)) {
        return false;
      }
      CodeSetComponent localCodeSetComponent = (CodeSetComponent)paramObject;
      return (nativeCodeSet == nativeCodeSet) && (Arrays.equals(conversionCodeSets, conversionCodeSets));
    }
    
    public int hashCode()
    {
      int i = nativeCodeSet;
      for (int j = 0; j < conversionCodeSets.length; j++) {
        i = 37 * i + conversionCodeSets[j];
      }
      return i;
    }
    
    public CodeSetComponent() {}
    
    public CodeSetComponent(int paramInt, int[] paramArrayOfInt)
    {
      nativeCodeSet = paramInt;
      if (paramArrayOfInt == null) {
        conversionCodeSets = new int[0];
      } else {
        conversionCodeSets = paramArrayOfInt;
      }
    }
    
    public void read(MarshalInputStream paramMarshalInputStream)
    {
      nativeCodeSet = paramMarshalInputStream.read_ulong();
      int i = paramMarshalInputStream.read_long();
      conversionCodeSets = new int[i];
      paramMarshalInputStream.read_ulong_array(conversionCodeSets, 0, i);
    }
    
    public void write(MarshalOutputStream paramMarshalOutputStream)
    {
      paramMarshalOutputStream.write_ulong(nativeCodeSet);
      paramMarshalOutputStream.write_long(conversionCodeSets.length);
      paramMarshalOutputStream.write_ulong_array(conversionCodeSets, 0, conversionCodeSets.length);
    }
    
    public String toString()
    {
      StringBuffer localStringBuffer = new StringBuffer("CodeSetComponent(");
      localStringBuffer.append("native:");
      localStringBuffer.append(Integer.toHexString(nativeCodeSet));
      localStringBuffer.append(" conversion:");
      if (conversionCodeSets == null) {
        localStringBuffer.append("null");
      } else {
        for (int i = 0; i < conversionCodeSets.length; i++)
        {
          localStringBuffer.append(Integer.toHexString(conversionCodeSets[i]));
          localStringBuffer.append(' ');
        }
      }
      localStringBuffer.append(")");
      return localStringBuffer.toString();
    }
  }
  
  public static final class CodeSetContext
  {
    private int char_data;
    private int wchar_data;
    
    public CodeSetContext() {}
    
    public CodeSetContext(int paramInt1, int paramInt2)
    {
      char_data = paramInt1;
      wchar_data = paramInt2;
    }
    
    public void read(MarshalInputStream paramMarshalInputStream)
    {
      char_data = paramMarshalInputStream.read_ulong();
      wchar_data = paramMarshalInputStream.read_ulong();
    }
    
    public void write(MarshalOutputStream paramMarshalOutputStream)
    {
      paramMarshalOutputStream.write_ulong(char_data);
      paramMarshalOutputStream.write_ulong(wchar_data);
    }
    
    public int getCharCodeSet()
    {
      return char_data;
    }
    
    public int getWCharCodeSet()
    {
      return wchar_data;
    }
    
    public String toString()
    {
      StringBuffer localStringBuffer = new StringBuffer();
      localStringBuffer.append("CodeSetContext char set: ");
      localStringBuffer.append(Integer.toHexString(char_data));
      localStringBuffer.append(" wchar set: ");
      localStringBuffer.append(Integer.toHexString(wchar_data));
      return localStringBuffer.toString();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\encoding\CodeSetComponentInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */