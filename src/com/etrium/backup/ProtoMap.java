package com.etrium.backup;

import java.util.List;
import java.util.Random;

public class ProtoMap
{
  public final int PERIMETER = 0;
  public final int WALL      = 1;
  public final int ROOM     = 2;
  public final int ENTRANCE  = 3;
  public final int CORRIDOR  = 4;
  public final int STAIRSUP = 5;
  public final int STAIRSDOWN  = 6;
  
  Random randomGenerator;
  public int width;
  public int height;
  public int cells[][];
  public MapLocation stairsUp = new MapLocation(0,0);
  public MapLocation stairsDown = new MapLocation(0,0);
  
  public ProtoMap(Random pRandom, int pWidth, int pHeight)  
  {
    randomGenerator = pRandom;
    
    width = pWidth;
    height = pHeight;
    
    cells = new int[height][width];
    
    for (int y = 0; y < pHeight; y++)
    {
      for (int x = 0; x < pWidth; x++)
      {
        cells[y][x] = WALL;
      }
    }
  }

  /* Corridor Generation */
  private enum CompassDir
  {
      NONE,
      NORTH,
      SOUTH,
      EAST,
      WEST
  }
  
  private CompassDir[] tunnelDirs( CompassDir pLastDir)
  {
    int p = 50;    
    CompassDir[] dirs = { CompassDir.NORTH, CompassDir.SOUTH, CompassDir.EAST, CompassDir.WEST };
    
    //Collections.shuffle(Arrays.asList(dirs));
    Shuffle(dirs);

    if ((pLastDir != CompassDir.NONE) && (p > 0)) 
    {
        
      if (randomGenerator.nextInt(100) < p)
      {
        CompassDir[] newDirs = new CompassDir[5];

        newDirs[0] = pLastDir;
        newDirs[1] = dirs[0];
        newDirs[2] = dirs[1];
        newDirs[3] = dirs[2];
        newDirs[4] = dirs[3];
        
        dirs = newDirs;
      }
    }
    return dirs;
  }
  
  public void addCorridors(int pLevel)
  {
    for (int y = 1; y < height; y+=2)
    {
      for (int x = 1; x < width; x+=2)
      {                   
        if (cells[y][x] != CORRIDOR)
        {                  
          tunnel(x, y, CompassDir.NONE); 
        }
      }
    }
    
    cells[stairsUp.y][stairsUp.x] = STAIRSUP;
    
    if (pLevel < 35)
    {
      cells[stairsDown.y][stairsDown.x] = STAIRSDOWN;
    }
  }
  
  /* Recursively tunnel */
  private void tunnel( int pX, int pY, CompassDir lastDir)
  {               
    CompassDir[] directions = tunnelDirs( lastDir);

    for(int i = 0; i < directions.length; i++)
    {
      if (directions[i] != CompassDir.NONE)
      {             
        if (openTunnel(pX, pY, directions[i])) 
        {
          int nextX = pX;
          int nextY = pY;
          
          switch (directions[i])
          {
            case NORTH:
            {
              nextY += 2;
              break;
            }
            case SOUTH:
            {
              nextY -= 2;
              break;
            }
            case EAST:
            {
              nextX += 2;
              break;
            }
            case WEST:
            {
              nextX -= 2;
              break;
            }            
          }
                    
          tunnel(nextX, nextY, directions[i]);
        }
      }       
    }
  }
  
  private boolean openTunnel( int pX, int pY, CompassDir pDirection)  
  {      
    int this_r = pY;
    int this_c = pX;   
    
    int r = 0;
    int c = 0;
    
    switch (pDirection)
    {
      case NORTH:
      {
        r = 2;
        break;
      }
      case SOUTH:
      {
        r = -2;
        break;
      }
      case EAST:
      {
        c = 2;
        break;
      }
      case WEST:
      {
        c = -2;
        break;
      }            
    }
    
    int next_r = pY+r;
    int next_c = pX+c;
        
    int mid_r = (this_r + next_r) / 2;
    int mid_c = (this_c + next_c) / 2;

    if (soundTunnel(mid_r, mid_c, next_r, next_c)) 
    {
      return delveTunnel(mid_r, mid_c, next_r, next_c);
    } else 
    {
      return false;
    }
  }
  
  private boolean soundTunnel( int pMid_r, int pMid_c, int pNext_r, int pNext_c)
  {    
    if (pNext_r < 0 || pNext_r >= height)
    {
      return false;
    }
    
    if (pNext_c < 0 || pNext_c >= width)
    {
      return false; 
    }        
         
    int r1 = pMid_r;
    int r2 = pNext_r;    
    if (r1 > r2) 
    {
      r1 = pNext_r;
      r2 = pMid_r;
    }
    
    int c1 = pMid_c;
    int c2 = pNext_c;
    if (c1 > c2) 
    {
      c1 = pNext_c;
      c2 = pMid_c;
    }

    for (int r = r1; r <= r2; r++) 
    {
      for (int c = c1; c <= c2; c++) 
      { 
        if ((cells[r][c] == PERIMETER) ||
            (cells[r][c] == CORRIDOR))
         {
           return false; 
         }
      }
    } 
    
    return true;
  }

  private boolean delveTunnel( int pMid_r, int pMid_c, int pNext_r, int pNext_c)
  {
    int r1 = pMid_r;
    int r2 = pNext_r;    
    if (r1 > r2) 
    {
      r1 = pNext_r;
      r2 = pMid_r;
    }
    
    int c1 = pMid_c;
    int c2 = pNext_c;
    if (c1 > c2) 
    {
      c1 = pNext_c;
      c2 = pMid_c;
    }
  
    for (int r = r1; r <= r2; r++) 
    {
      for (int c = c1; c <= c2; c++) 
      {   
        cells[r][c] = CORRIDOR;
      }
    }
    return true;
  }

    public void AddStairs(int pLevel, List<Room> pRooms)
    {
        CompassDir[] dirs = tunnelDirs(CompassDir.NONE);

        int x = randomGenerator.nextInt(5);
        x *= 2;
        x += 5;
        int y = randomGenerator.nextInt((height - 7) / 2);
        y *= 2;
        y += 5;
        stairsUp.x = x;
        stairsUp.y = y;        
        
        Room up = MakeStairPassage(stairsUp, dirs[0]);
        cells[stairsUp.y][stairsUp.x] = STAIRSUP;
        pRooms.add(up);
        
        if (pLevel < 35)
        {                   
          x = randomGenerator.nextInt(5);
          x *= 2;
          x += width - 12;
          y = randomGenerator.nextInt((height - 7) / 2);
          y *= 2;
          y += 5;
          stairsDown.x = x;
          stairsDown.y = y;          
          
          Room down = MakeStairPassage(stairsDown, dirs[1]);
                  
          cells[stairsDown.y][stairsDown.x] = STAIRSDOWN;               
          pRooms.add(down);
        }
        else
        {
          stairsDown = null;
        }
    }
    
    private Room MakeStairPassage(MapLocation p_loc, CompassDir p_dir)
    {
        Room room = new Room(this, randomGenerator);

        int x = p_loc.x;
        int y = p_loc.y;
        
        room.x = x;
        room.y = y;
        room.width = 1;
        room.height = 1;
        
        cells[y][x] = ROOM;
        
        cells[y-1][x-1] = PERIMETER;
        cells[y-1][x] = PERIMETER;
        cells[y-1][x+1] = PERIMETER;
        cells[y+1][x-1] = PERIMETER;
        cells[y+1][x] = PERIMETER;
        cells[y+1][x+1] = PERIMETER;
        cells[y][x-1] = PERIMETER;
        cells[y][x+1] = PERIMETER;
        
        switch (p_dir)
        {
            case NORTH:
            {
                cells[y+1][x] = ENTRANCE;
                break;
            }
            case SOUTH:
            {
                cells[y-1][x] = ENTRANCE;
                break;
            }
            case EAST:
            {
                cells[y][x+1] = ENTRANCE;
                break;
            }
            case WEST:
            {
                cells[y][x-1] = ENTRANCE;
                break;
            }
        }
        
        return room;
    }

    void Shuffle(CompassDir[] pDirArray)
    {   
      CompassDir[] temp = new CompassDir[pDirArray.length];         
          
      for (int i=0; i < temp.length; i++)
      {
        temp[i] = pDirArray[i];
      }
      
      int count = temp.length;
      
      for (int i=count - 1; i >= 0; i--)
      {
        int from = randomGenerator.nextInt(i + 1);
        int to = temp.length - i - 1;
        pDirArray[to] = temp[from];
        
        for (int j=from; j < i; j++)
        {
          temp[j] = temp[j + 1];
        }
      }
    }    
}
