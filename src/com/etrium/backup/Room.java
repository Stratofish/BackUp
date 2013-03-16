package com.etrium.backup;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Room
{
  public final int DOORFREQUENCY = 10; // Number of perimeter units per door 
  
  ProtoMap protoMap;
  public int width;
  public int height;
  public int x;
  public int y;
  
  public List<int[]> doors = new ArrayList<int[]>();
  
  Random randomGenerator;  
  
  public Room(ProtoMap pProtoMap, Random pRandom)  
  {    
    randomGenerator = pRandom;
    protoMap = pProtoMap;
  }       
  
  private void addDoors()
  {    
    // Do left \ right doors
    double maxDoors = (float)height / DOORFREQUENCY;        
    int numDoors = (int)Math.floor(maxDoors);        
    double remainder = maxDoors - numDoors;
    
    if (remainder > 0.0f)
    {
      numDoors++;
    }
    
    if (numDoors > 0)
    {
      boolean oddStart = false;
      if ( y % 2 == 1)
      {
        oddStart = true;          
      }
      
      if (x > 1) 
      {
        /* Left doors */
        for (int i = 0; i < numDoors; i++)
        {                                               
          int span = DOORFREQUENCY / 2;
          
          int doorSlot = randomGenerator.nextInt(span);
          
          int doorLoc = doorSlot * 2;        
          if (!oddStart)
          {
            doorLoc++;
          }
         
          if (doorLoc + (DOORFREQUENCY * i) < height)
          {
            int[] door = new int[2];
            door[0] = x-1;
            door[1] = y + doorLoc + (DOORFREQUENCY * i);
            doors.add(door);
            protoMap.cells[y + doorLoc + (DOORFREQUENCY * i)][x - 1] |= protoMap.ENTRANCE;
          }
        }
      }

      if ((x + width) < (protoMap.width - 1))
      {
        /* Right doors */
        for (int i = 0; i < numDoors; i++)
        {
          int span = DOORFREQUENCY / 2;

          int doorSlot = randomGenerator.nextInt(span);          

          int doorLoc = doorSlot * 2;
          if (!oddStart)
          {
            doorLoc++;
          }

          if (doorLoc + (DOORFREQUENCY * i) < height)
          {
            int[] door = new int[2];
            door[0] = x + width;
            door[1] = y + doorLoc + (DOORFREQUENCY * i);
            doors.add(door);
            protoMap.cells[y + doorLoc + (DOORFREQUENCY * i)][x + width] = protoMap.ENTRANCE;
          }
        }
      }
    }

    // Do  top \ bottom doors
    maxDoors = (float)width / DOORFREQUENCY;
    numDoors = (int)Math.floor(maxDoors);        
    remainder = maxDoors - numDoors;
    
    if (remainder > 0.0f)
    {
      numDoors++;
    }
    
    if (numDoors > 0)
    {
      boolean oddStart = false;
      if ( x % 2 == 1)
      {
        oddStart = true;          
      }
              
      if ((y + height) < (protoMap.height - 1))
      {
        /* Top doors */
        for (int i = 0; i < numDoors; i++)
        {                                               
          int span = DOORFREQUENCY / 2;
          
          int doorSlot = randomGenerator.nextInt(span);
          
          int doorLoc = doorSlot * 2;        
          if (!oddStart)
          {
            doorLoc++;
          }                          
          
          if (doorLoc + (DOORFREQUENCY * i) < width)
          {
            int[] door = new int[2];
            door[0] = x + doorLoc + (DOORFREQUENCY * i);
            door[1] = y + height;
            doors.add(door);
            protoMap.cells[y + height][x + doorLoc + (DOORFREQUENCY * i)] = protoMap.ENTRANCE;
          }            
        }
      }
        
      if (y > 1)
      {
        /* Bottom doors */
        for (int i = 0; i < numDoors; i++)
        {                                               
          int span = DOORFREQUENCY / 2;
          
          int doorSlot = randomGenerator.nextInt(span);
          
          int doorLoc = doorSlot * 2;        
          if (!oddStart)
          {
            doorLoc++;
          }                          
          
          if (doorLoc + (DOORFREQUENCY * i) < width)
          {
            int[] door = new int[2];
            door[0] = x + doorLoc + (DOORFREQUENCY * i);
            door[1] = y - 1;
            doors.add(door);
            protoMap.cells[y - 1][x + doorLoc + (DOORFREQUENCY * i)] = protoMap.ENTRANCE;
          }            
        }
      }
      
      /* If no doors created then create a door */
      if (doors.size() == 0)
      {        
        if (y > 1)
        {
          int[] door = new int[2];
          door[0] = x;
          door[1] = y - 1;
          doors.add(door);
          protoMap.cells[y - 1][x] = protoMap.ENTRANCE;
          //System.out.println("DOORING - "+door[0]+","+door[1]);
        }
        else
        {
          int[] door = new int[2];
          door[0] = x;
          door[1] = y + height;
          doors.add(door);
          protoMap.cells[y + height][x] = protoMap.ENTRANCE;          
        }
      }      
    }
  }

  private boolean roomSound(Map p_level)
  {
    boolean result = true;    
    
    for (int i = 0; i < p_level.rooms.size(); i++)  
    {
      Room room = p_level.rooms.get(i);
      
      if (x <= room.x + room.width && x + width >= room.x &&
          y <= room.y + room.height && y + height >= room.y) 
      {
        result = false;
      }
      
      if ((x < room.x) && ((x + width) > (room.x + room.width)) &&
          (y < room.y) && ((y + height) > (room.y + room.height)))
      {
        result = false;
      }
      
      if ((room.x < x) && ((room.x + room.width) > (x + width)) &&
          (room.y < y) && ((room.y + room.height) > (y + height)))
      {
        result = false;
      }         
    }         
    return result;       
  }
  
  public boolean Generate(Map p_level)
  {
      int attemptCount = 0;
      do
      {
        width = 1 + randomGenerator.nextInt(3);
        height = 1 + randomGenerator.nextInt(3);
        width = (width * 2) + 1;
        height = (height * 2) + 1;
        x = 1 + (randomGenerator.nextInt((protoMap.width - width - 2) / 2) * 2);         
        y = 1 + (randomGenerator.nextInt((protoMap.height - height - 2) / 2) * 2);
        attemptCount++;
      } while ((!roomSound(p_level)) && (attemptCount < 30));
    
      if (attemptCount >= 30)
      {
        return false;
      }        
      else
      {        
        for (int yLoop = -1; yLoop < height + 1; yLoop++)
        {
          for (int xLoop = -1; xLoop < width + 1; xLoop++)
          {
            int fillType = protoMap.ROOM;
            if (xLoop == -1 || yLoop == -1 || yLoop == height || xLoop == width)
            {
              fillType = protoMap.PERIMETER;
            }
            protoMap.cells[y + yLoop][x + xLoop] = fillType;
          }
        }
        
        /* Now allocate doors for room */
        addDoors();
      }
      
      return true;
  }
}
