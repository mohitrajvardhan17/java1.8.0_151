package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.util.Map;

final class PrimitiveArrayListerCharacter<BeanT>
  extends Lister<BeanT, char[], Character, CharacterArrayPack>
{
  private PrimitiveArrayListerCharacter() {}
  
  static void register()
  {
    Lister.primitiveArrayListers.put(Character.TYPE, new PrimitiveArrayListerCharacter());
  }
  
  public ListIterator<Character> iterator(final char[] paramArrayOfChar, XMLSerializer paramXMLSerializer)
  {
    new ListIterator()
    {
      int idx = 0;
      
      public boolean hasNext()
      {
        return idx < paramArrayOfChar.length;
      }
      
      public Character next()
      {
        return Character.valueOf(paramArrayOfChar[(idx++)]);
      }
    };
  }
  
  public CharacterArrayPack startPacking(BeanT paramBeanT, Accessor<BeanT, char[]> paramAccessor)
  {
    return new CharacterArrayPack();
  }
  
  public void addToPack(CharacterArrayPack paramCharacterArrayPack, Character paramCharacter)
  {
    paramCharacterArrayPack.add(paramCharacter);
  }
  
  public void endPacking(CharacterArrayPack paramCharacterArrayPack, BeanT paramBeanT, Accessor<BeanT, char[]> paramAccessor)
    throws AccessorException
  {
    paramAccessor.set(paramBeanT, paramCharacterArrayPack.build());
  }
  
  public void reset(BeanT paramBeanT, Accessor<BeanT, char[]> paramAccessor)
    throws AccessorException
  {
    paramAccessor.set(paramBeanT, new char[0]);
  }
  
  static final class CharacterArrayPack
  {
    char[] buf = new char[16];
    int size;
    
    CharacterArrayPack() {}
    
    void add(Character paramCharacter)
    {
      if (buf.length == size)
      {
        char[] arrayOfChar = new char[buf.length * 2];
        System.arraycopy(buf, 0, arrayOfChar, 0, buf.length);
        buf = arrayOfChar;
      }
      if (paramCharacter != null) {
        buf[(size++)] = paramCharacter.charValue();
      }
    }
    
    char[] build()
    {
      if (buf.length == size) {
        return buf;
      }
      char[] arrayOfChar = new char[size];
      System.arraycopy(buf, 0, arrayOfChar, 0, size);
      return arrayOfChar;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\PrimitiveArrayListerCharacter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */