package sun.print;

import java.util.ArrayList;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaTray;

class CustomMediaTray
  extends MediaTray
{
  private static ArrayList customStringTable = new ArrayList();
  private static ArrayList customEnumTable = new ArrayList();
  private String choiceName;
  private static final long serialVersionUID = 1019451298193987013L;
  
  private CustomMediaTray(int paramInt)
  {
    super(paramInt);
  }
  
  private static synchronized int nextValue(String paramString)
  {
    customStringTable.add(paramString);
    return customStringTable.size() - 1;
  }
  
  public CustomMediaTray(String paramString1, String paramString2)
  {
    super(nextValue(paramString1));
    choiceName = paramString2;
    customEnumTable.add(this);
  }
  
  public String getChoiceName()
  {
    return choiceName;
  }
  
  public Media[] getSuperEnumTable()
  {
    return (Media[])super.getEnumValueTable();
  }
  
  protected String[] getStringTable()
  {
    String[] arrayOfString = new String[customStringTable.size()];
    return (String[])customStringTable.toArray(arrayOfString);
  }
  
  protected EnumSyntax[] getEnumValueTable()
  {
    MediaTray[] arrayOfMediaTray = new MediaTray[customEnumTable.size()];
    return (MediaTray[])customEnumTable.toArray(arrayOfMediaTray);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\print\CustomMediaTray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */