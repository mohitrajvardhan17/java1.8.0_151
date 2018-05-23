package sun.security.tools.policytool;

class AWTPerm
  extends Perm
{
  public AWTPerm()
  {
    super("AWTPermission", "java.awt.AWTPermission", new String[] { "accessClipboard", "accessEventQueue", "accessSystemTray", "createRobot", "fullScreenExclusive", "listenToAllAWTEvents", "readDisplayPixels", "replaceKeyboardFocusManager", "setAppletStub", "setWindowAlwaysOnTop", "showWindowWithoutWarningBanner", "toolkitModality", "watchMousePointer" }, null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\AWTPerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */