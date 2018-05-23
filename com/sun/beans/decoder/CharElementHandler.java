package com.sun.beans.decoder;

final class CharElementHandler
  extends StringElementHandler
{
  CharElementHandler() {}
  
  public void addAttribute(String paramString1, String paramString2)
  {
    if (paramString1.equals("code"))
    {
      int i = Integer.decode(paramString2).intValue();
      for (char c : Character.toChars(i)) {
        addCharacter(c);
      }
    }
    else
    {
      super.addAttribute(paramString1, paramString2);
    }
  }
  
  public Object getValue(String paramString)
  {
    if (paramString.length() != 1) {
      throw new IllegalArgumentException("Wrong characters count");
    }
    return Character.valueOf(paramString.charAt(0));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\decoder\CharElementHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */