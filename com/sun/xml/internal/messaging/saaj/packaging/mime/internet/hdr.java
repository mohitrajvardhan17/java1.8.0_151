package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import com.sun.xml.internal.messaging.saaj.packaging.mime.Header;

class hdr
  implements Header
{
  String name;
  String line;
  
  hdr(String paramString)
  {
    int i = paramString.indexOf(':');
    if (i < 0) {
      name = paramString.trim();
    } else {
      name = paramString.substring(0, i).trim();
    }
    line = paramString;
  }
  
  hdr(String paramString1, String paramString2)
  {
    name = paramString1;
    line = (paramString1 + ": " + paramString2);
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getValue()
  {
    int i = line.indexOf(':');
    if (i < 0) {
      return line;
    }
    int k;
    if (name.equalsIgnoreCase("Content-Description")) {
      for (j = i + 1; j < line.length(); j++)
      {
        k = line.charAt(j);
        if ((k != 9) && (k != 13) && (k != 10)) {
          break;
        }
      }
    }
    for (int j = i + 1; j < line.length(); j++)
    {
      k = line.charAt(j);
      if ((k != 32) && (k != 9) && (k != 13) && (k != 10)) {
        break;
      }
    }
    return line.substring(j);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\internet\hdr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */