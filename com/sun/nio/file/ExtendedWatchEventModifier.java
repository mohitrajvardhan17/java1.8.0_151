package com.sun.nio.file;

import java.nio.file.WatchEvent.Modifier;

public enum ExtendedWatchEventModifier
  implements WatchEvent.Modifier
{
  FILE_TREE;
  
  private ExtendedWatchEventModifier() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\nio\file\ExtendedWatchEventModifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */