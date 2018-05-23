package javax.print;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Vector;

class MimeType
  implements Serializable, Cloneable
{
  private static final long serialVersionUID = -2785720609362367683L;
  private String[] myPieces;
  private transient String myStringValue = null;
  private transient ParameterMapEntrySet myEntrySet = null;
  private transient ParameterMap myParameterMap = null;
  private static final int TOKEN_LEXEME = 0;
  private static final int QUOTED_STRING_LEXEME = 1;
  private static final int TSPECIAL_LEXEME = 2;
  private static final int EOF_LEXEME = 3;
  private static final int ILLEGAL_LEXEME = 4;
  
  public MimeType(String paramString)
  {
    parse(paramString);
  }
  
  public String getMimeType()
  {
    return getStringValue();
  }
  
  public String getMediaType()
  {
    return myPieces[0];
  }
  
  public String getMediaSubtype()
  {
    return myPieces[1];
  }
  
  public Map getParameterMap()
  {
    if (myParameterMap == null) {
      myParameterMap = new ParameterMap(null);
    }
    return myParameterMap;
  }
  
  public String toString()
  {
    return getStringValue();
  }
  
  public int hashCode()
  {
    return getStringValue().hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    return (paramObject != null) && ((paramObject instanceof MimeType)) && (getStringValue().equals(((MimeType)paramObject).getStringValue()));
  }
  
  private String getStringValue()
  {
    if (myStringValue == null)
    {
      StringBuffer localStringBuffer = new StringBuffer();
      localStringBuffer.append(myPieces[0]);
      localStringBuffer.append('/');
      localStringBuffer.append(myPieces[1]);
      int i = myPieces.length;
      for (int j = 2; j < i; j += 2)
      {
        localStringBuffer.append(';');
        localStringBuffer.append(' ');
        localStringBuffer.append(myPieces[j]);
        localStringBuffer.append('=');
        localStringBuffer.append(addQuotes(myPieces[(j + 1)]));
      }
      myStringValue = localStringBuffer.toString();
    }
    return myStringValue;
  }
  
  private static String toUnicodeLowerCase(String paramString)
  {
    int i = paramString.length();
    char[] arrayOfChar = new char[i];
    for (int j = 0; j < i; j++) {
      arrayOfChar[j] = Character.toLowerCase(paramString.charAt(j));
    }
    return new String(arrayOfChar);
  }
  
  private static String removeBackslashes(String paramString)
  {
    int i = paramString.length();
    char[] arrayOfChar = new char[i];
    int k = 0;
    for (int j = 0; j < i; j++)
    {
      int m = paramString.charAt(j);
      if (m == 92) {
        m = paramString.charAt(++j);
      }
      arrayOfChar[(k++)] = m;
    }
    return new String(arrayOfChar, 0, k);
  }
  
  private static String addQuotes(String paramString)
  {
    int i = paramString.length();
    StringBuffer localStringBuffer = new StringBuffer(i + 2);
    localStringBuffer.append('"');
    for (int j = 0; j < i; j++)
    {
      char c = paramString.charAt(j);
      if (c == '"') {
        localStringBuffer.append('\\');
      }
      localStringBuffer.append(c);
    }
    localStringBuffer.append('"');
    return localStringBuffer.toString();
  }
  
  private void parse(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    LexicalAnalyzer localLexicalAnalyzer = new LexicalAnalyzer(paramString);
    Vector localVector = new Vector();
    boolean bool1 = false;
    boolean bool2 = false;
    String str1;
    if (localLexicalAnalyzer.getLexemeType() == 0)
    {
      str1 = toUnicodeLowerCase(localLexicalAnalyzer.getLexeme());
      localVector.add(str1);
      localLexicalAnalyzer.nextLexeme();
      bool1 = str1.equals("text");
    }
    else
    {
      throw new IllegalArgumentException();
    }
    if ((localLexicalAnalyzer.getLexemeType() == 2) && (localLexicalAnalyzer.getLexemeFirstCharacter() == '/')) {
      localLexicalAnalyzer.nextLexeme();
    } else {
      throw new IllegalArgumentException();
    }
    if (localLexicalAnalyzer.getLexemeType() == 0)
    {
      localVector.add(toUnicodeLowerCase(localLexicalAnalyzer.getLexeme()));
      localLexicalAnalyzer.nextLexeme();
    }
    else
    {
      throw new IllegalArgumentException();
    }
    while ((localLexicalAnalyzer.getLexemeType() == 2) && (localLexicalAnalyzer.getLexemeFirstCharacter() == ';'))
    {
      localLexicalAnalyzer.nextLexeme();
      if (localLexicalAnalyzer.getLexemeType() == 0)
      {
        str1 = toUnicodeLowerCase(localLexicalAnalyzer.getLexeme());
        localVector.add(str1);
        localLexicalAnalyzer.nextLexeme();
        bool2 = str1.equals("charset");
      }
      else
      {
        throw new IllegalArgumentException();
      }
      if ((localLexicalAnalyzer.getLexemeType() == 2) && (localLexicalAnalyzer.getLexemeFirstCharacter() == '=')) {
        localLexicalAnalyzer.nextLexeme();
      } else {
        throw new IllegalArgumentException();
      }
      if (localLexicalAnalyzer.getLexemeType() == 0)
      {
        str1 = localLexicalAnalyzer.getLexeme();
        localVector.add((bool1) && (bool2) ? toUnicodeLowerCase(str1) : str1);
        localLexicalAnalyzer.nextLexeme();
      }
      else if (localLexicalAnalyzer.getLexemeType() == 1)
      {
        str1 = removeBackslashes(localLexicalAnalyzer.getLexeme());
        localVector.add((bool1) && (bool2) ? toUnicodeLowerCase(str1) : str1);
        localLexicalAnalyzer.nextLexeme();
      }
      else
      {
        throw new IllegalArgumentException();
      }
    }
    if (localLexicalAnalyzer.getLexemeType() != 3) {
      throw new IllegalArgumentException();
    }
    int i = localVector.size();
    myPieces = ((String[])localVector.toArray(new String[i]));
    for (int j = 4; j < i; j += 2)
    {
      for (int k = 2; (k < j) && (myPieces[k].compareTo(myPieces[j]) <= 0); k += 2) {}
      while (k < j)
      {
        String str2 = myPieces[k];
        myPieces[k] = myPieces[j];
        myPieces[j] = str2;
        str2 = myPieces[(k + 1)];
        myPieces[(k + 1)] = myPieces[(j + 1)];
        myPieces[(j + 1)] = str2;
        k += 2;
      }
    }
  }
  
  private static class LexicalAnalyzer
  {
    protected String mySource;
    protected int mySourceLength;
    protected int myCurrentIndex;
    protected int myLexemeType;
    protected int myLexemeBeginIndex;
    protected int myLexemeEndIndex;
    
    public LexicalAnalyzer(String paramString)
    {
      mySource = paramString;
      mySourceLength = paramString.length();
      myCurrentIndex = 0;
      nextLexeme();
    }
    
    public int getLexemeType()
    {
      return myLexemeType;
    }
    
    public String getLexeme()
    {
      return myLexemeBeginIndex >= mySourceLength ? null : mySource.substring(myLexemeBeginIndex, myLexemeEndIndex);
    }
    
    public char getLexemeFirstCharacter()
    {
      return myLexemeBeginIndex >= mySourceLength ? '\000' : mySource.charAt(myLexemeBeginIndex);
    }
    
    public void nextLexeme()
    {
      int i = 0;
      int j = 0;
      while (i >= 0)
      {
        int k;
        switch (i)
        {
        case 0: 
          if (myCurrentIndex >= mySourceLength)
          {
            myLexemeType = 3;
            myLexemeBeginIndex = mySourceLength;
            myLexemeEndIndex = mySourceLength;
            i = -1;
          }
          else if (Character.isWhitespace(k = mySource.charAt(myCurrentIndex++)))
          {
            i = 0;
          }
          else if (k == 34)
          {
            myLexemeType = 1;
            myLexemeBeginIndex = myCurrentIndex;
            i = 1;
          }
          else if (k == 40)
          {
            j++;
            i = 3;
          }
          else if ((k == 47) || (k == 59) || (k == 61) || (k == 41) || (k == 60) || (k == 62) || (k == 64) || (k == 44) || (k == 58) || (k == 92) || (k == 91) || (k == 93) || (k == 63))
          {
            myLexemeType = 2;
            myLexemeBeginIndex = (myCurrentIndex - 1);
            myLexemeEndIndex = myCurrentIndex;
            i = -1;
          }
          else
          {
            myLexemeType = 0;
            myLexemeBeginIndex = (myCurrentIndex - 1);
            i = 5;
          }
          break;
        case 1: 
          if (myCurrentIndex >= mySourceLength)
          {
            myLexemeType = 4;
            myLexemeBeginIndex = mySourceLength;
            myLexemeEndIndex = mySourceLength;
            i = -1;
          }
          else if ((k = mySource.charAt(myCurrentIndex++)) == '"')
          {
            myLexemeEndIndex = (myCurrentIndex - 1);
            i = -1;
          }
          else if (k == 92)
          {
            i = 2;
          }
          else
          {
            i = 1;
          }
          break;
        case 2: 
          if (myCurrentIndex >= mySourceLength)
          {
            myLexemeType = 4;
            myLexemeBeginIndex = mySourceLength;
            myLexemeEndIndex = mySourceLength;
            i = -1;
          }
          else
          {
            myCurrentIndex += 1;
            i = 1;
          }
          break;
        case 3: 
          if (myCurrentIndex >= mySourceLength)
          {
            myLexemeType = 4;
            myLexemeBeginIndex = mySourceLength;
            myLexemeEndIndex = mySourceLength;
            i = -1;
          }
          else if ((k = mySource.charAt(myCurrentIndex++)) == '(')
          {
            j++;
            i = 3;
          }
          else if (k == 41)
          {
            j--;
            i = j == 0 ? 0 : 3;
          }
          else if (k == 92)
          {
            i = 4;
          }
          else
          {
            i = 3;
          }
          break;
        case 4: 
          if (myCurrentIndex >= mySourceLength)
          {
            myLexemeType = 4;
            myLexemeBeginIndex = mySourceLength;
            myLexemeEndIndex = mySourceLength;
            i = -1;
          }
          else
          {
            myCurrentIndex += 1;
            i = 3;
          }
          break;
        case 5: 
          if (myCurrentIndex >= mySourceLength)
          {
            myLexemeEndIndex = myCurrentIndex;
            i = -1;
          }
          else if (Character.isWhitespace(k = mySource.charAt(myCurrentIndex++)))
          {
            myLexemeEndIndex = (myCurrentIndex - 1);
            i = -1;
          }
          else if ((k == 34) || (k == 40) || (k == 47) || (k == 59) || (k == 61) || (k == 41) || (k == 60) || (k == 62) || (k == 64) || (k == 44) || (k == 58) || (k == 92) || (k == 91) || (k == 93) || (k == 63))
          {
            myCurrentIndex -= 1;
            myLexemeEndIndex = myCurrentIndex;
            i = -1;
          }
          else
          {
            i = 5;
          }
          break;
        }
      }
    }
  }
  
  private class ParameterMap
    extends AbstractMap
  {
    private ParameterMap() {}
    
    public Set entrySet()
    {
      if (myEntrySet == null) {
        myEntrySet = new MimeType.ParameterMapEntrySet(MimeType.this, null);
      }
      return myEntrySet;
    }
  }
  
  private class ParameterMapEntry
    implements Map.Entry
  {
    private int myIndex;
    
    public ParameterMapEntry(int paramInt)
    {
      myIndex = paramInt;
    }
    
    public Object getKey()
    {
      return myPieces[myIndex];
    }
    
    public Object getValue()
    {
      return myPieces[(myIndex + 1)];
    }
    
    public Object setValue(Object paramObject)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean equals(Object paramObject)
    {
      return (paramObject != null) && ((paramObject instanceof Map.Entry)) && (getKey().equals(((Map.Entry)paramObject).getKey())) && (getValue().equals(((Map.Entry)paramObject).getValue()));
    }
    
    public int hashCode()
    {
      return getKey().hashCode() ^ getValue().hashCode();
    }
  }
  
  private class ParameterMapEntrySet
    extends AbstractSet
  {
    private ParameterMapEntrySet() {}
    
    public Iterator iterator()
    {
      return new MimeType.ParameterMapEntrySetIterator(MimeType.this, null);
    }
    
    public int size()
    {
      return (myPieces.length - 2) / 2;
    }
  }
  
  private class ParameterMapEntrySetIterator
    implements Iterator
  {
    private int myIndex = 2;
    
    private ParameterMapEntrySetIterator() {}
    
    public boolean hasNext()
    {
      return myIndex < myPieces.length;
    }
    
    public Object next()
    {
      if (hasNext())
      {
        MimeType.ParameterMapEntry localParameterMapEntry = new MimeType.ParameterMapEntry(MimeType.this, myIndex);
        myIndex += 2;
        return localParameterMapEntry;
      }
      throw new NoSuchElementException();
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\MimeType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */