package com.sun.org.apache.xml.internal.dtm;

public abstract interface DTMFilter
{
  public static final int SHOW_ALL = -1;
  public static final int SHOW_ELEMENT = 1;
  public static final int SHOW_ATTRIBUTE = 2;
  public static final int SHOW_TEXT = 4;
  public static final int SHOW_CDATA_SECTION = 8;
  public static final int SHOW_ENTITY_REFERENCE = 16;
  public static final int SHOW_ENTITY = 32;
  public static final int SHOW_PROCESSING_INSTRUCTION = 64;
  public static final int SHOW_COMMENT = 128;
  public static final int SHOW_DOCUMENT = 256;
  public static final int SHOW_DOCUMENT_TYPE = 512;
  public static final int SHOW_DOCUMENT_FRAGMENT = 1024;
  public static final int SHOW_NOTATION = 2048;
  public static final int SHOW_NAMESPACE = 4096;
  public static final int SHOW_BYFUNCTION = 65536;
  
  public abstract short acceptNode(int paramInt1, int paramInt2);
  
  public abstract short acceptNode(int paramInt1, int paramInt2, int paramInt3);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\dtm\DTMFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */