package com.sun.corba.se.impl.naming.pcosnaming;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

class CounterDB
  implements Serializable
{
  private Integer counter;
  private static String counterFileName = "counter";
  private transient File counterFile;
  public static final int rootCounter = 0;
  
  CounterDB(File paramFile)
  {
    counterFileName = "counter";
    counterFile = new File(paramFile, counterFileName);
    if (!counterFile.exists())
    {
      counter = new Integer(0);
      writeCounter();
    }
    else
    {
      readCounter();
    }
  }
  
  private void readCounter()
  {
    try
    {
      FileInputStream localFileInputStream = new FileInputStream(counterFile);
      ObjectInputStream localObjectInputStream = new ObjectInputStream(localFileInputStream);
      counter = ((Integer)localObjectInputStream.readObject());
      localObjectInputStream.close();
    }
    catch (Exception localException) {}
  }
  
  private void writeCounter()
  {
    try
    {
      counterFile.delete();
      FileOutputStream localFileOutputStream = new FileOutputStream(counterFile);
      ObjectOutputStream localObjectOutputStream = new ObjectOutputStream(localFileOutputStream);
      localObjectOutputStream.writeObject(counter);
      localObjectOutputStream.flush();
      localObjectOutputStream.close();
    }
    catch (Exception localException) {}
  }
  
  public synchronized int getNextCounter()
  {
    int i = counter.intValue();
    counter = new Integer(++i);
    writeCounter();
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\naming\pcosnaming\CounterDB.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */