package com.sun.nio.file;

import java.nio.file.OpenOption;

public enum ExtendedOpenOption
  implements OpenOption
{
  NOSHARE_READ,  NOSHARE_WRITE,  NOSHARE_DELETE;
  
  private ExtendedOpenOption() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\nio\file\ExtendedOpenOption.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */