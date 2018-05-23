package java.util.prefs;

class WindowsPreferencesFactory
  implements PreferencesFactory
{
  WindowsPreferencesFactory() {}
  
  public Preferences userRoot()
  {
    return WindowsPreferences.userRoot;
  }
  
  public Preferences systemRoot()
  {
    return WindowsPreferences.systemRoot;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\prefs\WindowsPreferencesFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */