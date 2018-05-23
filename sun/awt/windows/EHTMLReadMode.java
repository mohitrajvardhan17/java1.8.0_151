package sun.awt.windows;

import java.awt.datatransfer.DataFlavor;

 enum EHTMLReadMode
{
  HTML_READ_ALL,  HTML_READ_FRAGMENT,  HTML_READ_SELECTION;
  
  private EHTMLReadMode() {}
  
  public static EHTMLReadMode getEHTMLReadMode(DataFlavor paramDataFlavor)
  {
    EHTMLReadMode localEHTMLReadMode = HTML_READ_SELECTION;
    String str = paramDataFlavor.getParameter("document");
    if ("all".equals(str)) {
      localEHTMLReadMode = HTML_READ_ALL;
    } else if ("fragment".equals(str)) {
      localEHTMLReadMode = HTML_READ_FRAGMENT;
    }
    return localEHTMLReadMode;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\EHTMLReadMode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */