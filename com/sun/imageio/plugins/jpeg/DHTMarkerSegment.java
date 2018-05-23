package com.sun.imageio.plugins.jpeg;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class DHTMarkerSegment
  extends MarkerSegment
{
  List tables = new ArrayList();
  
  DHTMarkerSegment(boolean paramBoolean)
  {
    super(196);
    tables.add(new Htable(JPEGHuffmanTable.StdDCLuminance, true, 0));
    if (paramBoolean) {
      tables.add(new Htable(JPEGHuffmanTable.StdDCChrominance, true, 1));
    }
    tables.add(new Htable(JPEGHuffmanTable.StdACLuminance, false, 0));
    if (paramBoolean) {
      tables.add(new Htable(JPEGHuffmanTable.StdACChrominance, false, 1));
    }
  }
  
  DHTMarkerSegment(JPEGBuffer paramJPEGBuffer)
    throws IOException
  {
    super(paramJPEGBuffer);
    int i = length;
    while (i > 0)
    {
      Htable localHtable = new Htable(paramJPEGBuffer);
      tables.add(localHtable);
      i -= 17 + values.length;
    }
    bufAvail -= length;
  }
  
  DHTMarkerSegment(JPEGHuffmanTable[] paramArrayOfJPEGHuffmanTable1, JPEGHuffmanTable[] paramArrayOfJPEGHuffmanTable2)
  {
    super(196);
    for (int i = 0; i < paramArrayOfJPEGHuffmanTable1.length; i++) {
      tables.add(new Htable(paramArrayOfJPEGHuffmanTable1[i], true, i));
    }
    for (i = 0; i < paramArrayOfJPEGHuffmanTable2.length; i++) {
      tables.add(new Htable(paramArrayOfJPEGHuffmanTable2[i], false, i));
    }
  }
  
  DHTMarkerSegment(Node paramNode)
    throws IIOInvalidTreeException
  {
    super(196);
    NodeList localNodeList = paramNode.getChildNodes();
    int i = localNodeList.getLength();
    if ((i < 1) || (i > 4)) {
      throw new IIOInvalidTreeException("Invalid DHT node", paramNode);
    }
    for (int j = 0; j < i; j++) {
      tables.add(new Htable(localNodeList.item(j)));
    }
  }
  
  protected Object clone()
  {
    DHTMarkerSegment localDHTMarkerSegment = (DHTMarkerSegment)super.clone();
    tables = new ArrayList(tables.size());
    Iterator localIterator = tables.iterator();
    while (localIterator.hasNext())
    {
      Htable localHtable = (Htable)localIterator.next();
      tables.add(localHtable.clone());
    }
    return localDHTMarkerSegment;
  }
  
  IIOMetadataNode getNativeNode()
  {
    IIOMetadataNode localIIOMetadataNode = new IIOMetadataNode("dht");
    for (int i = 0; i < tables.size(); i++)
    {
      Htable localHtable = (Htable)tables.get(i);
      localIIOMetadataNode.appendChild(localHtable.getNativeNode());
    }
    return localIIOMetadataNode;
  }
  
  void write(ImageOutputStream paramImageOutputStream)
    throws IOException
  {}
  
  void print()
  {
    printTag("DHT");
    System.out.println("Num tables: " + Integer.toString(tables.size()));
    for (int i = 0; i < tables.size(); i++)
    {
      Htable localHtable = (Htable)tables.get(i);
      localHtable.print();
    }
    System.out.println();
  }
  
  Htable getHtableFromNode(Node paramNode)
    throws IIOInvalidTreeException
  {
    return new Htable(paramNode);
  }
  
  void addHtable(JPEGHuffmanTable paramJPEGHuffmanTable, boolean paramBoolean, int paramInt)
  {
    tables.add(new Htable(paramJPEGHuffmanTable, paramBoolean, paramInt));
  }
  
  class Htable
    implements Cloneable
  {
    int tableClass;
    int tableID;
    private static final int NUM_LENGTHS = 16;
    short[] numCodes = new short[16];
    short[] values;
    
    Htable(JPEGBuffer paramJPEGBuffer)
    {
      tableClass = (buf[bufPtr] >>> 4);
      tableID = (buf[(bufPtr++)] & 0xF);
      for (int i = 0; i < 16; i++) {
        numCodes[i] = ((short)(buf[(bufPtr++)] & 0xFF));
      }
      i = 0;
      for (int j = 0; j < 16; j++) {
        i += numCodes[j];
      }
      values = new short[i];
      for (j = 0; j < i; j++) {
        values[j] = ((short)(buf[(bufPtr++)] & 0xFF));
      }
    }
    
    Htable(JPEGHuffmanTable paramJPEGHuffmanTable, boolean paramBoolean, int paramInt)
    {
      tableClass = (paramBoolean ? 0 : 1);
      tableID = paramInt;
      numCodes = paramJPEGHuffmanTable.getLengths();
      values = paramJPEGHuffmanTable.getValues();
    }
    
    Htable(Node paramNode)
      throws IIOInvalidTreeException
    {
      if (paramNode.getNodeName().equals("dhtable"))
      {
        NamedNodeMap localNamedNodeMap = paramNode.getAttributes();
        int i = localNamedNodeMap.getLength();
        if (i != 2) {
          throw new IIOInvalidTreeException("dhtable node must have 2 attributes", paramNode);
        }
        tableClass = MarkerSegment.getAttributeValue(paramNode, localNamedNodeMap, "class", 0, 1, true);
        tableID = MarkerSegment.getAttributeValue(paramNode, localNamedNodeMap, "htableId", 0, 3, true);
        if ((paramNode instanceof IIOMetadataNode))
        {
          IIOMetadataNode localIIOMetadataNode = (IIOMetadataNode)paramNode;
          JPEGHuffmanTable localJPEGHuffmanTable = (JPEGHuffmanTable)localIIOMetadataNode.getUserObject();
          if (localJPEGHuffmanTable == null) {
            throw new IIOInvalidTreeException("dhtable node must have user object", paramNode);
          }
          numCodes = localJPEGHuffmanTable.getLengths();
          values = localJPEGHuffmanTable.getValues();
        }
        else
        {
          throw new IIOInvalidTreeException("dhtable node must have user object", paramNode);
        }
      }
      else
      {
        throw new IIOInvalidTreeException("Invalid node, expected dqtable", paramNode);
      }
    }
    
    protected Object clone()
    {
      Htable localHtable = null;
      try
      {
        localHtable = (Htable)super.clone();
      }
      catch (CloneNotSupportedException localCloneNotSupportedException) {}
      if (numCodes != null) {
        numCodes = ((short[])numCodes.clone());
      }
      if (values != null) {
        values = ((short[])values.clone());
      }
      return localHtable;
    }
    
    IIOMetadataNode getNativeNode()
    {
      IIOMetadataNode localIIOMetadataNode = new IIOMetadataNode("dhtable");
      localIIOMetadataNode.setAttribute("class", Integer.toString(tableClass));
      localIIOMetadataNode.setAttribute("htableId", Integer.toString(tableID));
      localIIOMetadataNode.setUserObject(new JPEGHuffmanTable(numCodes, values));
      return localIIOMetadataNode;
    }
    
    void print()
    {
      System.out.println("Huffman Table");
      System.out.println("table class: " + (tableClass == 0 ? "DC" : "AC"));
      System.out.println("table id: " + Integer.toString(tableID));
      new JPEGHuffmanTable(numCodes, values).toString();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\jpeg\DHTMarkerSegment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */