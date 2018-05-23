package sun.awt.windows;

final class WPageDialogPeer
  extends WPrintDialogPeer
{
  WPageDialogPeer(WPageDialog paramWPageDialog)
  {
    super(paramWPageDialog);
  }
  
  private native boolean _show();
  
  public void show()
  {
    new Thread(new Runnable()
    {
      public void run()
      {
        try
        {
          ((WPrintDialog)target).setRetVal(WPageDialogPeer.this._show());
        }
        catch (Exception localException) {}
        ((WPrintDialog)target).setVisible(false);
      }
    }).start();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WPageDialogPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */