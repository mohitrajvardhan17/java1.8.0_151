package com.sun.corba.se.spi.orb;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public abstract class ParserImplTableBase
  extends ParserImplBase
{
  private final ParserData[] entries;
  
  public ParserImplTableBase(ParserData[] paramArrayOfParserData)
  {
    entries = paramArrayOfParserData;
    setDefaultValues();
  }
  
  protected PropertyParser makeParser()
  {
    PropertyParser localPropertyParser = new PropertyParser();
    for (int i = 0; i < entries.length; i++)
    {
      ParserData localParserData = entries[i];
      localParserData.addToParser(localPropertyParser);
    }
    return localPropertyParser;
  }
  
  protected void setDefaultValues()
  {
    FieldMap localFieldMap = new FieldMap(entries, true);
    setFields(localFieldMap);
  }
  
  public void setTestValues()
  {
    FieldMap localFieldMap = new FieldMap(entries, false);
    setFields(localFieldMap);
  }
  
  private static class FieldMap
    extends AbstractMap
  {
    private final ParserData[] entries;
    private final boolean useDefault;
    
    public FieldMap(ParserData[] paramArrayOfParserData, boolean paramBoolean)
    {
      entries = paramArrayOfParserData;
      useDefault = paramBoolean;
    }
    
    public Set entrySet()
    {
      new AbstractSet()
      {
        public Iterator iterator()
        {
          new Iterator()
          {
            int ctr = 0;
            
            public boolean hasNext()
            {
              return ctr < entries.length;
            }
            
            public Object next()
            {
              ParserData localParserData = entries[(ctr++)];
              ParserImplTableBase.MapEntry localMapEntry = new ParserImplTableBase.MapEntry(localParserData.getFieldName());
              if (useDefault) {
                localMapEntry.setValue(localParserData.getDefaultValue());
              } else {
                localMapEntry.setValue(localParserData.getTestValue());
              }
              return localMapEntry;
            }
            
            public void remove()
            {
              throw new UnsupportedOperationException();
            }
          };
        }
        
        public int size()
        {
          return entries.length;
        }
      };
    }
  }
  
  private static final class MapEntry
    implements Map.Entry
  {
    private Object key;
    private Object value;
    
    public MapEntry(Object paramObject)
    {
      key = paramObject;
    }
    
    public Object getKey()
    {
      return key;
    }
    
    public Object getValue()
    {
      return value;
    }
    
    public Object setValue(Object paramObject)
    {
      Object localObject = value;
      value = paramObject;
      return localObject;
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof MapEntry)) {
        return false;
      }
      MapEntry localMapEntry = (MapEntry)paramObject;
      return (key.equals(key)) && (value.equals(value));
    }
    
    public int hashCode()
    {
      return key.hashCode() ^ value.hashCode();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\orb\ParserImplTableBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */