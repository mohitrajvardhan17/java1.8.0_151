package com.sun.jmx.snmp.agent;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.SnmpEngine;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpVarBind;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

final class SnmpRequestTree
{
  private Hashtable<Object, Handler> hashtable = null;
  private SnmpMibRequest request = null;
  private int version = 0;
  private boolean creationflag = false;
  private boolean getnextflag = false;
  private int type = 0;
  private boolean setreqflag = false;
  
  SnmpRequestTree(SnmpMibRequest paramSnmpMibRequest, boolean paramBoolean, int paramInt)
  {
    request = paramSnmpMibRequest;
    version = paramSnmpMibRequest.getVersion();
    creationflag = paramBoolean;
    hashtable = new Hashtable();
    setPduType(paramInt);
  }
  
  public static int mapSetException(int paramInt1, int paramInt2)
    throws SnmpStatusException
  {
    int i = paramInt1;
    if (paramInt2 == 0) {
      return i;
    }
    int j = i;
    if (i == 225) {
      j = 17;
    } else if (i == 224) {
      j = 17;
    }
    return j;
  }
  
  public static int mapGetException(int paramInt1, int paramInt2)
    throws SnmpStatusException
  {
    int i = paramInt1;
    if (paramInt2 == 0) {
      return i;
    }
    int j = i;
    if (i == 225) {
      j = i;
    } else if (i == 224) {
      j = i;
    } else if (i == 6) {
      j = 224;
    } else if (i == 18) {
      j = 224;
    } else if ((i >= 7) && (i <= 12)) {
      j = 224;
    } else if (i == 4) {
      j = 224;
    } else if ((i != 16) && (i != 5)) {
      j = 225;
    }
    return j;
  }
  
  public Object getUserData()
  {
    return request.getUserData();
  }
  
  public boolean isCreationAllowed()
  {
    return creationflag;
  }
  
  public boolean isSetRequest()
  {
    return setreqflag;
  }
  
  public int getVersion()
  {
    return version;
  }
  
  public int getRequestPduVersion()
  {
    return request.getRequestPduVersion();
  }
  
  public SnmpMibNode getMetaNode(Handler paramHandler)
  {
    return meta;
  }
  
  public int getOidDepth(Handler paramHandler)
  {
    return depth;
  }
  
  public Enumeration<SnmpMibSubRequest> getSubRequests(Handler paramHandler)
  {
    return new Enum(this, paramHandler);
  }
  
  public Enumeration<Handler> getHandlers()
  {
    return hashtable.elements();
  }
  
  public void add(SnmpMibNode paramSnmpMibNode, int paramInt, SnmpVarBind paramSnmpVarBind)
    throws SnmpStatusException
  {
    registerNode(paramSnmpMibNode, paramInt, null, paramSnmpVarBind, false, null);
  }
  
  public void add(SnmpMibNode paramSnmpMibNode, int paramInt, SnmpOid paramSnmpOid, SnmpVarBind paramSnmpVarBind, boolean paramBoolean)
    throws SnmpStatusException
  {
    registerNode(paramSnmpMibNode, paramInt, paramSnmpOid, paramSnmpVarBind, paramBoolean, null);
  }
  
  public void add(SnmpMibNode paramSnmpMibNode, int paramInt, SnmpOid paramSnmpOid, SnmpVarBind paramSnmpVarBind1, boolean paramBoolean, SnmpVarBind paramSnmpVarBind2)
    throws SnmpStatusException
  {
    registerNode(paramSnmpMibNode, paramInt, paramSnmpOid, paramSnmpVarBind1, paramBoolean, paramSnmpVarBind2);
  }
  
  void setPduType(int paramInt)
  {
    type = paramInt;
    setreqflag = ((paramInt == 253) || (paramInt == 163));
  }
  
  void setGetNextFlag()
  {
    getnextflag = true;
  }
  
  void switchCreationFlag(boolean paramBoolean)
  {
    creationflag = paramBoolean;
  }
  
  SnmpMibSubRequest getSubRequest(Handler paramHandler)
  {
    if (paramHandler == null) {
      return null;
    }
    return new SnmpMibSubRequestImpl(request, paramHandler.getSubList(), null, false, getnextflag, null);
  }
  
  SnmpMibSubRequest getSubRequest(Handler paramHandler, SnmpOid paramSnmpOid)
  {
    if (paramHandler == null) {
      return null;
    }
    int i = paramHandler.getEntryPos(paramSnmpOid);
    if (i == -1) {
      return null;
    }
    return new SnmpMibSubRequestImpl(request, paramHandler.getEntrySubList(i), paramHandler.getEntryOid(i), paramHandler.isNewEntry(i), getnextflag, paramHandler.getRowStatusVarBind(i));
  }
  
  SnmpMibSubRequest getSubRequest(Handler paramHandler, int paramInt)
  {
    if (paramHandler == null) {
      return null;
    }
    return new SnmpMibSubRequestImpl(request, paramHandler.getEntrySubList(paramInt), paramHandler.getEntryOid(paramInt), paramHandler.isNewEntry(paramInt), getnextflag, paramHandler.getRowStatusVarBind(paramInt));
  }
  
  private void put(Object paramObject, Handler paramHandler)
  {
    if (paramHandler == null) {
      return;
    }
    if (paramObject == null) {
      return;
    }
    if (hashtable == null) {
      hashtable = new Hashtable();
    }
    hashtable.put(paramObject, paramHandler);
  }
  
  private Handler get(Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if (hashtable == null) {
      return null;
    }
    return (Handler)hashtable.get(paramObject);
  }
  
  private static int findOid(SnmpOid[] paramArrayOfSnmpOid, int paramInt, SnmpOid paramSnmpOid)
  {
    int i = paramInt;
    int j = 0;
    int k = i - 1;
    for (int m = j + (k - j) / 2; j <= k; m = j + (k - j) / 2)
    {
      SnmpOid localSnmpOid = paramArrayOfSnmpOid[m];
      int n = paramSnmpOid.compareTo(localSnmpOid);
      if (n == 0) {
        return m;
      }
      if (paramSnmpOid.equals(localSnmpOid)) {
        return m;
      }
      if (n > 0) {
        j = m + 1;
      } else {
        k = m - 1;
      }
    }
    return -1;
  }
  
  private static int getInsertionPoint(SnmpOid[] paramArrayOfSnmpOid, int paramInt, SnmpOid paramSnmpOid)
  {
    SnmpOid[] arrayOfSnmpOid = paramArrayOfSnmpOid;
    int i = paramInt;
    int j = 0;
    int k = i - 1;
    for (int m = j + (k - j) / 2; j <= k; m = j + (k - j) / 2)
    {
      SnmpOid localSnmpOid = arrayOfSnmpOid[m];
      int n = paramSnmpOid.compareTo(localSnmpOid);
      if (n == 0) {
        return m;
      }
      if (n > 0) {
        j = m + 1;
      } else {
        k = m - 1;
      }
    }
    return m;
  }
  
  private void registerNode(SnmpMibNode paramSnmpMibNode, int paramInt, SnmpOid paramSnmpOid, SnmpVarBind paramSnmpVarBind1, boolean paramBoolean, SnmpVarBind paramSnmpVarBind2)
    throws SnmpStatusException
  {
    if (paramSnmpMibNode == null)
    {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpRequestTree.class.getName(), "registerNode", "meta-node is null!");
      return;
    }
    if (paramSnmpVarBind1 == null)
    {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpRequestTree.class.getName(), "registerNode", "varbind is null!");
      return;
    }
    SnmpMibNode localSnmpMibNode = paramSnmpMibNode;
    Handler localHandler = get(localSnmpMibNode);
    if (localHandler == null)
    {
      localHandler = new Handler(type);
      meta = paramSnmpMibNode;
      depth = paramInt;
      put(localSnmpMibNode, localHandler);
    }
    if (paramSnmpOid == null) {
      localHandler.addVarbind(paramSnmpVarBind1);
    } else {
      localHandler.addVarbind(paramSnmpVarBind1, paramSnmpOid, paramBoolean, paramSnmpVarBind2);
    }
  }
  
  static final class Enum
    implements Enumeration<SnmpMibSubRequest>
  {
    private final SnmpRequestTree.Handler handler;
    private final SnmpRequestTree hlist;
    private int entry = 0;
    private int iter = 0;
    private int size = 0;
    
    Enum(SnmpRequestTree paramSnmpRequestTree, SnmpRequestTree.Handler paramHandler)
    {
      handler = paramHandler;
      hlist = paramSnmpRequestTree;
      size = paramHandler.getSubReqCount();
    }
    
    public boolean hasMoreElements()
    {
      return iter < size;
    }
    
    public SnmpMibSubRequest nextElement()
      throws NoSuchElementException
    {
      if ((iter == 0) && (handler.sublist != null))
      {
        iter += 1;
        return hlist.getSubRequest(handler);
      }
      iter += 1;
      if (iter > size) {
        throw new NoSuchElementException();
      }
      SnmpMibSubRequest localSnmpMibSubRequest = hlist.getSubRequest(handler, entry);
      entry += 1;
      return localSnmpMibSubRequest;
    }
  }
  
  static final class Handler
  {
    SnmpMibNode meta;
    int depth;
    Vector<SnmpVarBind> sublist;
    SnmpOid[] entryoids = null;
    Vector<SnmpVarBind>[] entrylists = null;
    boolean[] isentrynew = null;
    SnmpVarBind[] rowstatus = null;
    int entrycount = 0;
    int entrysize = 0;
    final int type;
    private static final int Delta = 10;
    
    public Handler(int paramInt)
    {
      type = paramInt;
    }
    
    public void addVarbind(SnmpVarBind paramSnmpVarBind)
    {
      if (sublist == null) {
        sublist = new Vector();
      }
      sublist.addElement(paramSnmpVarBind);
    }
    
    void add(int paramInt, SnmpOid paramSnmpOid, Vector<SnmpVarBind> paramVector, boolean paramBoolean, SnmpVarBind paramSnmpVarBind)
    {
      if (entryoids == null)
      {
        entryoids = new SnmpOid[10];
        entrylists = ((Vector[])new Vector[10]);
        isentrynew = new boolean[10];
        rowstatus = new SnmpVarBind[10];
        entrysize = 10;
        paramInt = 0;
      }
      else if ((paramInt >= entrysize) || (entrycount == entrysize))
      {
        SnmpOid[] arrayOfSnmpOid = entryoids;
        Vector[] arrayOfVector = entrylists;
        boolean[] arrayOfBoolean = isentrynew;
        SnmpVarBind[] arrayOfSnmpVarBind = rowstatus;
        entrysize += 10;
        entryoids = new SnmpOid[entrysize];
        entrylists = ((Vector[])new Vector[entrysize]);
        isentrynew = new boolean[entrysize];
        rowstatus = new SnmpVarBind[entrysize];
        if (paramInt > entrycount) {
          paramInt = entrycount;
        }
        if (paramInt < 0) {
          paramInt = 0;
        }
        int k = paramInt;
        int m = entrycount - paramInt;
        if (k > 0)
        {
          System.arraycopy(arrayOfSnmpOid, 0, entryoids, 0, k);
          System.arraycopy(arrayOfVector, 0, entrylists, 0, k);
          System.arraycopy(arrayOfBoolean, 0, isentrynew, 0, k);
          System.arraycopy(arrayOfSnmpVarBind, 0, rowstatus, 0, k);
        }
        if (m > 0)
        {
          int n = k + 1;
          System.arraycopy(arrayOfSnmpOid, k, entryoids, n, m);
          System.arraycopy(arrayOfVector, k, entrylists, n, m);
          System.arraycopy(arrayOfBoolean, k, isentrynew, n, m);
          System.arraycopy(arrayOfSnmpVarBind, k, rowstatus, n, m);
        }
      }
      else if (paramInt < entrycount)
      {
        int i = paramInt + 1;
        int j = entrycount - paramInt;
        System.arraycopy(entryoids, paramInt, entryoids, i, j);
        System.arraycopy(entrylists, paramInt, entrylists, i, j);
        System.arraycopy(isentrynew, paramInt, isentrynew, i, j);
        System.arraycopy(rowstatus, paramInt, rowstatus, i, j);
      }
      entryoids[paramInt] = paramSnmpOid;
      entrylists[paramInt] = paramVector;
      isentrynew[paramInt] = paramBoolean;
      rowstatus[paramInt] = paramSnmpVarBind;
      entrycount += 1;
    }
    
    public void addVarbind(SnmpVarBind paramSnmpVarBind1, SnmpOid paramSnmpOid, boolean paramBoolean, SnmpVarBind paramSnmpVarBind2)
      throws SnmpStatusException
    {
      Vector localVector = null;
      SnmpVarBind localSnmpVarBind = paramSnmpVarBind2;
      if (entryoids == null)
      {
        localVector = new Vector();
        add(0, paramSnmpOid, localVector, paramBoolean, localSnmpVarBind);
      }
      else
      {
        int i = SnmpRequestTree.getInsertionPoint(entryoids, entrycount, paramSnmpOid);
        if ((i > -1) && (i < entrycount) && (paramSnmpOid.compareTo(entryoids[i]) == 0))
        {
          localVector = entrylists[i];
          localSnmpVarBind = rowstatus[i];
        }
        else
        {
          localVector = new Vector();
          add(i, paramSnmpOid, localVector, paramBoolean, localSnmpVarBind);
        }
        if (paramSnmpVarBind2 != null)
        {
          if ((localSnmpVarBind != null) && (localSnmpVarBind != paramSnmpVarBind2) && ((type == 253) || (type == 163))) {
            throw new SnmpStatusException(12);
          }
          rowstatus[i] = paramSnmpVarBind2;
        }
      }
      if (paramSnmpVarBind2 != paramSnmpVarBind1) {
        localVector.addElement(paramSnmpVarBind1);
      }
    }
    
    public int getSubReqCount()
    {
      int i = 0;
      if (sublist != null) {
        i++;
      }
      if (entryoids != null) {
        i += entrycount;
      }
      return i;
    }
    
    public Vector<SnmpVarBind> getSubList()
    {
      return sublist;
    }
    
    public int getEntryPos(SnmpOid paramSnmpOid)
    {
      return SnmpRequestTree.findOid(entryoids, entrycount, paramSnmpOid);
    }
    
    public SnmpOid getEntryOid(int paramInt)
    {
      if (entryoids == null) {
        return null;
      }
      if ((paramInt == -1) || (paramInt >= entrycount)) {
        return null;
      }
      return entryoids[paramInt];
    }
    
    public boolean isNewEntry(int paramInt)
    {
      if (entryoids == null) {
        return false;
      }
      if ((paramInt == -1) || (paramInt >= entrycount)) {
        return false;
      }
      return isentrynew[paramInt];
    }
    
    public SnmpVarBind getRowStatusVarBind(int paramInt)
    {
      if (entryoids == null) {
        return null;
      }
      if ((paramInt == -1) || (paramInt >= entrycount)) {
        return null;
      }
      return rowstatus[paramInt];
    }
    
    public Vector<SnmpVarBind> getEntrySubList(int paramInt)
    {
      if (entrylists == null) {
        return null;
      }
      if ((paramInt == -1) || (paramInt >= entrycount)) {
        return null;
      }
      return entrylists[paramInt];
    }
    
    public Iterator<SnmpOid> getEntryOids()
    {
      if (entryoids == null) {
        return null;
      }
      return Arrays.asList(entryoids).iterator();
    }
    
    public int getEntryCount()
    {
      if (entryoids == null) {
        return 0;
      }
      return entrycount;
    }
  }
  
  static final class SnmpMibSubRequestImpl
    implements SnmpMibSubRequest
  {
    private final Vector<SnmpVarBind> varbinds;
    private final SnmpMibRequest global;
    private final int version;
    private final boolean isnew;
    private final SnmpOid entryoid;
    private final boolean getnextflag;
    private final SnmpVarBind statusvb;
    
    SnmpMibSubRequestImpl(SnmpMibRequest paramSnmpMibRequest, Vector<SnmpVarBind> paramVector, SnmpOid paramSnmpOid, boolean paramBoolean1, boolean paramBoolean2, SnmpVarBind paramSnmpVarBind)
    {
      global = paramSnmpMibRequest;
      varbinds = paramVector;
      version = paramSnmpMibRequest.getVersion();
      entryoid = paramSnmpOid;
      isnew = paramBoolean1;
      getnextflag = paramBoolean2;
      statusvb = paramSnmpVarBind;
    }
    
    public Enumeration<SnmpVarBind> getElements()
    {
      return varbinds.elements();
    }
    
    public Vector<SnmpVarBind> getSubList()
    {
      return varbinds;
    }
    
    public final int getSize()
    {
      if (varbinds == null) {
        return 0;
      }
      return varbinds.size();
    }
    
    public void addVarBind(SnmpVarBind paramSnmpVarBind)
    {
      varbinds.addElement(paramSnmpVarBind);
      global.addVarBind(paramSnmpVarBind);
    }
    
    public boolean isNewEntry()
    {
      return isnew;
    }
    
    public SnmpOid getEntryOid()
    {
      return entryoid;
    }
    
    public int getVarIndex(SnmpVarBind paramSnmpVarBind)
    {
      if (paramSnmpVarBind == null) {
        return 0;
      }
      return global.getVarIndex(paramSnmpVarBind);
    }
    
    public Object getUserData()
    {
      return global.getUserData();
    }
    
    public void registerGetException(SnmpVarBind paramSnmpVarBind, SnmpStatusException paramSnmpStatusException)
      throws SnmpStatusException
    {
      if (version == 0) {
        throw new SnmpStatusException(paramSnmpStatusException, getVarIndex(paramSnmpVarBind) + 1);
      }
      if (paramSnmpVarBind == null) {
        throw paramSnmpStatusException;
      }
      if (getnextflag)
      {
        value = SnmpVarBind.endOfMibView;
        return;
      }
      int i = SnmpRequestTree.mapGetException(paramSnmpStatusException.getStatus(), version);
      if (i == 225) {
        value = SnmpVarBind.noSuchObject;
      } else if (i == 224) {
        value = SnmpVarBind.noSuchInstance;
      } else {
        throw new SnmpStatusException(i, getVarIndex(paramSnmpVarBind) + 1);
      }
    }
    
    public void registerSetException(SnmpVarBind paramSnmpVarBind, SnmpStatusException paramSnmpStatusException)
      throws SnmpStatusException
    {
      if (version == 0) {
        throw new SnmpStatusException(paramSnmpStatusException, getVarIndex(paramSnmpVarBind) + 1);
      }
      throw new SnmpStatusException(15, getVarIndex(paramSnmpVarBind) + 1);
    }
    
    public void registerCheckException(SnmpVarBind paramSnmpVarBind, SnmpStatusException paramSnmpStatusException)
      throws SnmpStatusException
    {
      int i = paramSnmpStatusException.getStatus();
      int j = SnmpRequestTree.mapSetException(i, version);
      if (i != j) {
        throw new SnmpStatusException(j, getVarIndex(paramSnmpVarBind) + 1);
      }
      throw new SnmpStatusException(paramSnmpStatusException, getVarIndex(paramSnmpVarBind) + 1);
    }
    
    public int getVersion()
    {
      return version;
    }
    
    public SnmpVarBind getRowStatusVarBind()
    {
      return statusvb;
    }
    
    public SnmpPdu getPdu()
    {
      return global.getPdu();
    }
    
    public int getRequestPduVersion()
    {
      return global.getRequestPduVersion();
    }
    
    public SnmpEngine getEngine()
    {
      return global.getEngine();
    }
    
    public String getPrincipal()
    {
      return global.getPrincipal();
    }
    
    public int getSecurityLevel()
    {
      return global.getSecurityLevel();
    }
    
    public int getSecurityModel()
    {
      return global.getSecurityModel();
    }
    
    public byte[] getContextName()
    {
      return global.getContextName();
    }
    
    public byte[] getAccessContextName()
    {
      return global.getAccessContextName();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\agent\SnmpRequestTree.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */