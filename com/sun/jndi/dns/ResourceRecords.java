package com.sun.jndi.dns;

import java.util.Vector;
import javax.naming.CommunicationException;
import javax.naming.NamingException;

class ResourceRecords
{
  Vector<ResourceRecord> question = new Vector();
  Vector<ResourceRecord> answer = new Vector();
  Vector<ResourceRecord> authority = new Vector();
  Vector<ResourceRecord> additional = new Vector();
  boolean zoneXfer;
  
  ResourceRecords(byte[] paramArrayOfByte, int paramInt, Header paramHeader, boolean paramBoolean)
    throws NamingException
  {
    if (paramBoolean) {
      answer.ensureCapacity(8192);
    }
    zoneXfer = paramBoolean;
    add(paramArrayOfByte, paramInt, paramHeader);
  }
  
  int getFirstAnsType()
  {
    if (answer.size() == 0) {
      return -1;
    }
    return ((ResourceRecord)answer.firstElement()).getType();
  }
  
  int getLastAnsType()
  {
    if (answer.size() == 0) {
      return -1;
    }
    return ((ResourceRecord)answer.lastElement()).getType();
  }
  
  void add(byte[] paramArrayOfByte, int paramInt, Header paramHeader)
    throws NamingException
  {
    int i = 12;
    try
    {
      ResourceRecord localResourceRecord;
      for (int j = 0; j < numQuestions; j++)
      {
        localResourceRecord = new ResourceRecord(paramArrayOfByte, paramInt, i, true, false);
        if (!zoneXfer) {
          question.addElement(localResourceRecord);
        }
        i += localResourceRecord.size();
      }
      for (j = 0; j < numAnswers; j++)
      {
        localResourceRecord = new ResourceRecord(paramArrayOfByte, paramInt, i, false, !zoneXfer);
        answer.addElement(localResourceRecord);
        i += localResourceRecord.size();
      }
      if (zoneXfer) {
        return;
      }
      for (j = 0; j < numAuthorities; j++)
      {
        localResourceRecord = new ResourceRecord(paramArrayOfByte, paramInt, i, false, true);
        authority.addElement(localResourceRecord);
        i += localResourceRecord.size();
      }
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
    {
      throw new CommunicationException("DNS error: corrupted message");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\dns\ResourceRecords.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */