package java.awt.peer;

public abstract interface TrayIconPeer
{
  public abstract void dispose();
  
  public abstract void setToolTip(String paramString);
  
  public abstract void updateImage();
  
  public abstract void displayMessage(String paramString1, String paramString2, String paramString3);
  
  public abstract void showPopupMenu(int paramInt1, int paramInt2);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\peer\TrayIconPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */