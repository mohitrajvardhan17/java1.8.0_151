package java.util.prefs;

import java.util.EventListener;

@FunctionalInterface
public abstract interface PreferenceChangeListener
  extends EventListener
{
  public abstract void preferenceChange(PreferenceChangeEvent paramPreferenceChangeEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\prefs\PreferenceChangeListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */