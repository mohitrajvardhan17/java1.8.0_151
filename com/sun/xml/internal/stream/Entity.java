package com.sun.xml.internal.stream;

import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.xml.internal.stream.util.BufferAllocator;
import com.sun.xml.internal.stream.util.ThreadLocalBufferAllocator;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public abstract class Entity
{
  public String name;
  public boolean inExternalSubset;
  
  public Entity()
  {
    clear();
  }
  
  public Entity(String paramString, boolean paramBoolean)
  {
    name = paramString;
    inExternalSubset = paramBoolean;
  }
  
  public boolean isEntityDeclInExternalSubset()
  {
    return inExternalSubset;
  }
  
  public abstract boolean isExternal();
  
  public abstract boolean isUnparsed();
  
  public void clear()
  {
    name = null;
    inExternalSubset = false;
  }
  
  public void setValues(Entity paramEntity)
  {
    name = name;
    inExternalSubset = inExternalSubset;
  }
  
  public static class ExternalEntity
    extends Entity
  {
    public XMLResourceIdentifier entityLocation;
    public String notation;
    
    public ExternalEntity()
    {
      clear();
    }
    
    public ExternalEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, boolean paramBoolean)
    {
      super(paramBoolean);
      entityLocation = paramXMLResourceIdentifier;
      notation = paramString2;
    }
    
    public final boolean isExternal()
    {
      return true;
    }
    
    public final boolean isUnparsed()
    {
      return notation != null;
    }
    
    public void clear()
    {
      super.clear();
      entityLocation = null;
      notation = null;
    }
    
    public void setValues(Entity paramEntity)
    {
      super.setValues(paramEntity);
      entityLocation = null;
      notation = null;
    }
    
    public void setValues(ExternalEntity paramExternalEntity)
    {
      super.setValues(paramExternalEntity);
      entityLocation = entityLocation;
      notation = notation;
    }
  }
  
  public static class InternalEntity
    extends Entity
  {
    public String text;
    
    public InternalEntity()
    {
      clear();
    }
    
    public InternalEntity(String paramString1, String paramString2, boolean paramBoolean)
    {
      super(paramBoolean);
      text = paramString2;
    }
    
    public final boolean isExternal()
    {
      return false;
    }
    
    public final boolean isUnparsed()
    {
      return false;
    }
    
    public void clear()
    {
      super.clear();
      text = null;
    }
    
    public void setValues(Entity paramEntity)
    {
      super.setValues(paramEntity);
      text = null;
    }
    
    public void setValues(InternalEntity paramInternalEntity)
    {
      super.setValues(paramInternalEntity);
      text = text;
    }
  }
  
  public static class ScannedEntity
    extends Entity
  {
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    public int fBufferSize = 8192;
    public static final int DEFAULT_XMLDECL_BUFFER_SIZE = 28;
    public static final int DEFAULT_INTERNAL_BUFFER_SIZE = 1024;
    public InputStream stream;
    public Reader reader;
    public XMLResourceIdentifier entityLocation;
    public String encoding;
    public boolean literal;
    public boolean isExternal;
    public String version;
    public char[] ch = null;
    public int position;
    public int count;
    public int lineNumber = 1;
    public int columnNumber = 1;
    boolean declaredEncoding = false;
    boolean externallySpecifiedEncoding = false;
    public String xmlVersion = "1.0";
    public int fTotalCountTillLastLoad;
    public int fLastCount;
    public int baseCharOffset;
    public int startPosition;
    public boolean mayReadChunks;
    public boolean xmlDeclChunkRead = false;
    public boolean isGE = false;
    
    public String getEncodingName()
    {
      return encoding;
    }
    
    public String getEntityVersion()
    {
      return version;
    }
    
    public void setEntityVersion(String paramString)
    {
      version = paramString;
    }
    
    public Reader getEntityReader()
    {
      return reader;
    }
    
    public InputStream getEntityInputStream()
    {
      return stream;
    }
    
    public ScannedEntity(boolean paramBoolean1, String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, InputStream paramInputStream, Reader paramReader, String paramString2, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
    {
      isGE = paramBoolean1;
      name = paramString1;
      entityLocation = paramXMLResourceIdentifier;
      stream = paramInputStream;
      reader = paramReader;
      encoding = paramString2;
      literal = paramBoolean2;
      mayReadChunks = paramBoolean3;
      isExternal = paramBoolean4;
      int i = paramBoolean4 ? fBufferSize : 1024;
      BufferAllocator localBufferAllocator = ThreadLocalBufferAllocator.getBufferAllocator();
      ch = localBufferAllocator.getCharBuffer(i);
      if (ch == null) {
        ch = new char[i];
      }
    }
    
    public void close()
      throws IOException
    {
      BufferAllocator localBufferAllocator = ThreadLocalBufferAllocator.getBufferAllocator();
      localBufferAllocator.returnCharBuffer(ch);
      ch = null;
      reader.close();
    }
    
    public boolean isEncodingExternallySpecified()
    {
      return externallySpecifiedEncoding;
    }
    
    public void setEncodingExternallySpecified(boolean paramBoolean)
    {
      externallySpecifiedEncoding = paramBoolean;
    }
    
    public boolean isDeclaredEncoding()
    {
      return declaredEncoding;
    }
    
    public void setDeclaredEncoding(boolean paramBoolean)
    {
      declaredEncoding = paramBoolean;
    }
    
    public final boolean isExternal()
    {
      return isExternal;
    }
    
    public final boolean isUnparsed()
    {
      return false;
    }
    
    public String toString()
    {
      StringBuffer localStringBuffer = new StringBuffer();
      localStringBuffer.append("name=\"" + name + '"');
      localStringBuffer.append(",ch=" + new String(ch));
      localStringBuffer.append(",position=" + position);
      localStringBuffer.append(",count=" + count);
      return localStringBuffer.toString();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\Entity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */