package jdk.internal.util.xml.impl;

import java.io.Reader;

public class Input
{
  public String pubid;
  public String sysid;
  public String xmlenc;
  public char xmlver;
  public Reader src;
  public char[] chars;
  public int chLen;
  public int chIdx;
  public Input next;
  
  public Input(int paramInt)
  {
    chars = new char[paramInt];
    chLen = chars.length;
  }
  
  public Input(char[] paramArrayOfChar)
  {
    chars = paramArrayOfChar;
    chLen = chars.length;
  }
  
  public Input() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\util\xml\impl\Input.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */