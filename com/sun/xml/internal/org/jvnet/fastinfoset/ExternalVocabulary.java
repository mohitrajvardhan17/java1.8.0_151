package com.sun.xml.internal.org.jvnet.fastinfoset;

public class ExternalVocabulary
{
  public final String URI;
  public final Vocabulary vocabulary;
  
  public ExternalVocabulary(String paramString, Vocabulary paramVocabulary)
  {
    if ((paramString == null) || (paramVocabulary == null)) {
      throw new IllegalArgumentException();
    }
    URI = paramString;
    vocabulary = paramVocabulary;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\fastinfoset\ExternalVocabulary.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */