package com.sun.nio.file;

import java.nio.file.WatchEvent.Modifier;

public enum SensitivityWatchEventModifier
  implements WatchEvent.Modifier
{
  HIGH(2),  MEDIUM(10),  LOW(30);
  
  private final int sensitivity;
  
  public int sensitivityValueInSeconds()
  {
    return sensitivity;
  }
  
  private SensitivityWatchEventModifier(int paramInt)
  {
    sensitivity = paramInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\nio\file\SensitivityWatchEventModifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */