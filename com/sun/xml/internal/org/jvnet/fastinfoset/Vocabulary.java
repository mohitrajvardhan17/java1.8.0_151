package com.sun.xml.internal.org.jvnet.fastinfoset;

import java.util.LinkedHashSet;
import java.util.Set;

public class Vocabulary
{
  public final Set restrictedAlphabets = new LinkedHashSet();
  public final Set encodingAlgorithms = new LinkedHashSet();
  public final Set prefixes = new LinkedHashSet();
  public final Set namespaceNames = new LinkedHashSet();
  public final Set localNames = new LinkedHashSet();
  public final Set otherNCNames = new LinkedHashSet();
  public final Set otherURIs = new LinkedHashSet();
  public final Set attributeValues = new LinkedHashSet();
  public final Set otherStrings = new LinkedHashSet();
  public final Set characterContentChunks = new LinkedHashSet();
  public final Set elements = new LinkedHashSet();
  public final Set attributes = new LinkedHashSet();
  
  public Vocabulary() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\fastinfoset\Vocabulary.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */