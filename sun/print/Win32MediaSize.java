package sun.print;

import java.util.ArrayList;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;

class Win32MediaSize
  extends MediaSizeName
{
  private static ArrayList winStringTable = new ArrayList();
  private static ArrayList winEnumTable = new ArrayList();
  private static MediaSize[] predefMedia;
  private int dmPaperID;
  
  private Win32MediaSize(int paramInt)
  {
    super(paramInt);
  }
  
  private static synchronized int nextValue(String paramString)
  {
    winStringTable.add(paramString);
    return winStringTable.size() - 1;
  }
  
  public static synchronized Win32MediaSize findMediaName(String paramString)
  {
    int i = winStringTable.indexOf(paramString);
    if (i != -1) {
      return (Win32MediaSize)winEnumTable.get(i);
    }
    return null;
  }
  
  public static MediaSize[] getPredefMedia()
  {
    return predefMedia;
  }
  
  public Win32MediaSize(String paramString, int paramInt)
  {
    super(nextValue(paramString));
    dmPaperID = paramInt;
    winEnumTable.add(this);
  }
  
  private MediaSizeName[] getSuperEnumTable()
  {
    return (MediaSizeName[])super.getEnumValueTable();
  }
  
  int getDMPaper()
  {
    return dmPaperID;
  }
  
  protected String[] getStringTable()
  {
    String[] arrayOfString = new String[winStringTable.size()];
    return (String[])winStringTable.toArray(arrayOfString);
  }
  
  protected EnumSyntax[] getEnumValueTable()
  {
    MediaSizeName[] arrayOfMediaSizeName = new MediaSizeName[winEnumTable.size()];
    return (MediaSizeName[])winEnumTable.toArray(arrayOfMediaSizeName);
  }
  
  static
  {
    Win32MediaSize localWin32MediaSize = new Win32MediaSize(-1);
    MediaSizeName[] arrayOfMediaSizeName = localWin32MediaSize.getSuperEnumTable();
    if (arrayOfMediaSizeName != null)
    {
      predefMedia = new MediaSize[arrayOfMediaSizeName.length];
      for (int i = 0; i < arrayOfMediaSizeName.length; i++) {
        predefMedia[i] = MediaSize.getMediaSizeForName(arrayOfMediaSizeName[i]);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\print\Win32MediaSize.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */