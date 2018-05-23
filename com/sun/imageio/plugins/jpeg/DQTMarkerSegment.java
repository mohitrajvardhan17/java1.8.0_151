package com.sun.imageio.plugins.jpeg;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.plugins.jpeg.JPEGQTable;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class DQTMarkerSegment
  extends MarkerSegment
{
  List tables = new ArrayList();
  
  DQTMarkerSegment(float paramFloat, boolean paramBoolean)
  {
    super(219);
    tables.add(new Qtable(true, paramFloat));
    if (paramBoolean) {
      tables.add(new Qtable(false, paramFloat));
    }
  }
  
  DQTMarkerSegment(JPEGBuffer paramJPEGBuffer)
    throws IOException
  {
    super(paramJPEGBuffer);
    int i = length;
    while (i > 0)
    {
      Qtable localQtable = new Qtable(paramJPEGBuffer);
      tables.add(localQtable);
      i -= data.length + 1;
    }
    bufAvail -= length;
  }
  
  DQTMarkerSegment(JPEGQTable[] paramArrayOfJPEGQTable)
  {
    super(219);
    for (int i = 0; i < paramArrayOfJPEGQTable.length; i++) {
      tables.add(new Qtable(paramArrayOfJPEGQTable[i], i));
    }
  }
  
  DQTMarkerSegment(Node paramNode)
    throws IIOInvalidTreeException
  {
    super(219);
    NodeList localNodeList = paramNode.getChildNodes();
    int i = localNodeList.getLength();
    if ((i < 1) || (i > 4)) {
      throw new IIOInvalidTreeException("Invalid DQT node", paramNode);
    }
    for (int j = 0; j < i; j++) {
      tables.add(new Qtable(localNodeList.item(j)));
    }
  }
  
  protected Object clone()
  {
    DQTMarkerSegment localDQTMarkerSegment = (DQTMarkerSegment)super.clone();
    tables = new ArrayList(tables.size());
    Iterator localIterator = tables.iterator();
    while (localIterator.hasNext())
    {
      Qtable localQtable = (Qtable)localIterator.next();
      tables.add(localQtable.clone());
    }
    return localDQTMarkerSegment;
  }
  
  IIOMetadataNode getNativeNode()
  {
    IIOMetadataNode localIIOMetadataNode = new IIOMetadataNode("dqt");
    for (int i = 0; i < tables.size(); i++)
    {
      Qtable localQtable = (Qtable)tables.get(i);
      localIIOMetadataNode.appendChild(localQtable.getNativeNode());
    }
    return localIIOMetadataNode;
  }
  
  void write(ImageOutputStream paramImageOutputStream)
    throws IOException
  {}
  
  void print()
  {
    printTag("DQT");
    System.out.println("Num tables: " + Integer.toString(tables.size()));
    for (int i = 0; i < tables.size(); i++)
    {
      Qtable localQtable = (Qtable)tables.get(i);
      localQtable.print();
    }
    System.out.println();
  }
  
  Qtable getChromaForLuma(Qtable paramQtable)
  {
    Qtable localQtable = null;
    int i = 1;
    for (int j = 1;; j++)
    {
      paramQtable.getClass();
      if (j >= 64) {
        break;
      }
      if (data[j] != data[(j - 1)])
      {
        i = 0;
        break;
      }
    }
    if (i != 0)
    {
      localQtable = (Qtable)paramQtable.clone();
      tableID = 1;
    }
    else
    {
      j = 0;
      for (int k = 1;; k++)
      {
        paramQtable.getClass();
        if (k >= 64) {
          break;
        }
        if (data[k] > data[j]) {
          j = k;
        }
      }
      float f = data[j] / JPEGQTable.K1Div2Luminance.getTable()[j];
      JPEGQTable localJPEGQTable = JPEGQTable.K2Div2Chrominance.getScaledInstance(f, true);
      localQtable = new Qtable(localJPEGQTable, 1);
    }
    return localQtable;
  }
  
  Qtable getQtableFromNode(Node paramNode)
    throws IIOInvalidTreeException
  {
    return new Qtable(paramNode);
  }
  
  class Qtable
    implements Cloneable
  {
    int elementPrecision;
    int tableID;
    final int QTABLE_SIZE = 64;
    int[] data;
    private final int[] zigzag = { 0, 1, 5, 6, 14, 15, 27, 28, 2, 4, 7, 13, 16, 26, 29, 42, 3, 8, 12, 17, 25, 30, 41, 43, 9, 11, 18, 24, 31, 40, 44, 53, 10, 19, 23, 32, 39, 45, 52, 54, 20, 22, 33, 38, 46, 51, 55, 60, 21, 34, 37, 47, 50, 56, 59, 61, 35, 36, 48, 49, 57, 58, 62, 63 };
    
    Qtable(boolean paramBoolean, float paramFloat)
    {
      elementPrecision = 0;
      JPEGQTable localJPEGQTable = null;
      if (paramBoolean)
      {
        tableID = 0;
        localJPEGQTable = JPEGQTable.K1Div2Luminance;
      }
      else
      {
        tableID = 1;
        localJPEGQTable = JPEGQTable.K2Div2Chrominance;
      }
      if (paramFloat != 0.75F)
      {
        paramFloat = JPEG.convertToLinearQuality(paramFloat);
        if (paramBoolean) {
          localJPEGQTable = JPEGQTable.K1Luminance.getScaledInstance(paramFloat, true);
        } else {
          localJPEGQTable = JPEGQTable.K2Div2Chrominance.getScaledInstance(paramFloat, true);
        }
      }
      data = localJPEGQTable.getTable();
    }
    
    Qtable(JPEGBuffer paramJPEGBuffer)
      throws IIOException
    {
      elementPrecision = (buf[bufPtr] >>> 4);
      tableID = (buf[(bufPtr++)] & 0xF);
      if (elementPrecision != 0) {
        throw new IIOException("Unsupported element precision");
      }
      data = new int[64];
      for (int i = 0; i < 64; i++) {
        data[i] = (buf[(bufPtr + zigzag[i])] & 0xFF);
      }
      bufPtr += 64;
    }
    
    Qtable(JPEGQTable paramJPEGQTable, int paramInt)
    {
      elementPrecision = 0;
      tableID = paramInt;
      data = paramJPEGQTable.getTable();
    }
    
    Qtable(Node paramNode)
      throws IIOInvalidTreeException
    {
      if (paramNode.getNodeName().equals("dqtable"))
      {
        NamedNodeMap localNamedNodeMap = paramNode.getAttributes();
        int i = localNamedNodeMap.getLength();
        if ((i < 1) || (i > 2)) {
          throw new IIOInvalidTreeException("dqtable node must have 1 or 2 attributes", paramNode);
        }
        elementPrecision = 0;
        tableID = MarkerSegment.getAttributeValue(paramNode, localNamedNodeMap, "qtableId", 0, 3, true);
        if ((paramNode instanceof IIOMetadataNode))
        {
          IIOMetadataNode localIIOMetadataNode = (IIOMetadataNode)paramNode;
          JPEGQTable localJPEGQTable = (JPEGQTable)localIIOMetadataNode.getUserObject();
          if (localJPEGQTable == null) {
            throw new IIOInvalidTreeException("dqtable node must have user object", paramNode);
          }
          data = localJPEGQTable.getTable();
        }
        else
        {
          throw new IIOInvalidTreeException("dqtable node must have user object", paramNode);
        }
      }
      else
      {
        throw new IIOInvalidTreeException("Invalid node, expected dqtable", paramNode);
      }
    }
    
    protected Object clone()
    {
      Qtable localQtable = null;
      try
      {
        localQtable = (Qtable)super.clone();
      }
      catch (CloneNotSupportedException localCloneNotSupportedException) {}
      if (data != null) {
        data = ((int[])data.clone());
      }
      return localQtable;
    }
    
    IIOMetadataNode getNativeNode()
    {
      IIOMetadataNode localIIOMetadataNode = new IIOMetadataNode("dqtable");
      localIIOMetadataNode.setAttribute("elementPrecision", Integer.toString(elementPrecision));
      localIIOMetadataNode.setAttribute("qtableId", Integer.toString(tableID));
      localIIOMetadataNode.setUserObject(new JPEGQTable(data));
      return localIIOMetadataNode;
    }
    
    void print()
    {
      System.out.println("Table id: " + Integer.toString(tableID));
      System.out.println("Element precision: " + Integer.toString(elementPrecision));
      new JPEGQTable(data).toString();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\jpeg\DQTMarkerSegment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */