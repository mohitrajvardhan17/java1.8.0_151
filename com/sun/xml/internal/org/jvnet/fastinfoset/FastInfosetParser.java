package com.sun.xml.internal.org.jvnet.fastinfoset;

import java.util.Map;

public abstract interface FastInfosetParser
{
  public static final String STRING_INTERNING_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/string-interning";
  public static final String BUFFER_SIZE_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/buffer-size";
  public static final String REGISTERED_ENCODING_ALGORITHMS_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/registered-encoding-algorithms";
  public static final String EXTERNAL_VOCABULARIES_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/external-vocabularies";
  public static final String FORCE_STREAM_CLOSE_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/force-stream-close";
  
  public abstract void setStringInterning(boolean paramBoolean);
  
  public abstract boolean getStringInterning();
  
  public abstract void setBufferSize(int paramInt);
  
  public abstract int getBufferSize();
  
  public abstract void setRegisteredEncodingAlgorithms(Map paramMap);
  
  public abstract Map getRegisteredEncodingAlgorithms();
  
  public abstract void setExternalVocabularies(Map paramMap);
  
  /**
   * @deprecated
   */
  public abstract Map getExternalVocabularies();
  
  public abstract void setParseFragments(boolean paramBoolean);
  
  public abstract boolean getParseFragments();
  
  public abstract void setForceStreamClose(boolean paramBoolean);
  
  public abstract boolean getForceStreamClose();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\fastinfoset\FastInfosetParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */