package com.etrium.backup;

// Based on algorithm http://donjon.bin.sh/fantasy/dungeon/about/

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.etrium.backup.character.Character;
import com.etrium.backup.character.EnemyController;
import com.etrium.backup.character.EnemySpecGenerator;
import com.etrium.backup.character.EnemyUI;
import com.etrium.backup.item.EnemyInventory;
import com.etrium.backup.item.Item;
import com.etrium.backup.system.EtriumEvent;
import com.etrium.backup.system.EventListener;
import com.etrium.backup.system.EventManager;
import com.etrium.backup.system.EventType;

public class Map implements EventListener
{
  class Coordinates
  {
    int x;
    int y;
  };
  
  public static MapState[] mapStates = null;
  public final int EMPTY = 1;
  public final int WALL = 2;
  public final int STAIRSUP = 3;
  public final int STAIRSDOWN  = 4;
  public final int MAXMONSTERS = 20;
  public final int MAXITEMS = 10;
  public final int MAXROOMS = 20;  
  public final int MAPWIDTH = 41;
  public final int MAPHEIGHT = 41;  
  public final int TILEWIDTH = 32;
  public final int TILEHEIGHT = 32;
  private final float LIGHTRADIUS = 4.0f;
  private final float LIGHTFALLOFF = 4.0f;
  private final float SEEN = 0.4f;
  private final float UNSEEN = 0.0f;
    
  Random randomGenerator;  
  
  public int curLevel = -1;
  public MapState curState = null;
  private EventManager evtMgr = new EventManager();
  private boolean listening = true;
  public int width = 1;
  public int height = 1;
  public MapLocation stairsUp = new MapLocation(0,0);
  public MapLocation stairsDown = new MapLocation(0,0);
  public Sprite sprites[][] = new Sprite[MAPHEIGHT][MAPWIDTH];
  private TextureRegion textureRegions[] = new TextureRegion[5];
  
  public int map[][];
  public List<Room> rooms = new ArrayList<Room>();
  private ProtoMap protoMap;
  
  public Character player;
  
  public MapLocation mapWindow = new MapLocation();
  
  public Map(long pSeed, int pLevel, TextureRegion[] regions)
  {    
    curLevel = pLevel;
    width = MAPWIDTH;
    height = MAPHEIGHT;
    textureRegions = regions;
    
    if (mapStates == null)
    {
        mapStates = new MapState[36];
        for (int i = 0; i < 36; i++)
        {
            mapStates[i] = null;
        }
    }

    /* Seed the random number generator */
    randomGenerator = new Random( pSeed + pLevel);
    
    map = new int[height][width];
   
    // Prepare proto-map by filling with zeros
    protoMap = new ProtoMap( randomGenerator, width, height);
    
    // Fill level in with walls
    for (int y = 0; y < height; y++)
    {
      for (int x = 0; x < width; x++)
      {
          map[y][x] = WALL;
      }
    }
    
    protoMap.AddStairs(pLevel, rooms);
    stairsUp = protoMap.stairsUp;
    stairsDown = protoMap.stairsDown;
      
    boolean moreRooms = true;
    
    // Create rooms
    for (int r = 1; r <= MAXROOMS; r++)
    {     
      if (moreRooms)
      {
        Room room = new Room(protoMap, randomGenerator);
        if (room.Generate(this))
        {
            rooms.add(room);
        } else
        {
            moreRooms = false;
        }
      }      
    }
    
    /* Now allocate corridors */
    protoMap.addCorridors( pLevel);

    /* Convert proto map into usable map */
    for (int y = 0; y < height; y++)
    {
      for (int x = 0; x < width; x++)
      {
        int value = WALL;
        
        if ((protoMap.cells[y][x] == protoMap.ROOM) ||
            (protoMap.cells[y][x] == protoMap.ENTRANCE) ||
            (protoMap.cells[y][x] == protoMap.CORRIDOR))
        {
          value = EMPTY;
        }
        
        if ((protoMap.cells[y][x] == protoMap.STAIRSUP))
        {
          value = STAIRSUP;
        }
        
        if ((protoMap.cells[y][x] == protoMap.STAIRSDOWN))
        {
          value = STAIRSDOWN;
        }                
        
        map[y][x] = value;
      }        
    }
    
    /* Fix any rooms \ stairs that are closed off by creating a door */
    for (int i = 0; i < rooms.size(); i++)
    {
      Room room = rooms.get(i);      
      boolean doorFound = false;
      
      for (int yLoop = -1; yLoop < room.height + 1; yLoop++)
      {
        for (int xLoop = -1; xLoop < room.width + 1; xLoop++)
        {          
          if (xLoop == -1 || yLoop == -1 || yLoop == room.height || xLoop == room.width)
          {
            if (map[room.y + yLoop][room.x + xLoop] == EMPTY)
            {
              doorFound = true;
            }            
          }          
        }        
      }
      
      if (!doorFound)
      {
        map[room.y][room.x+room.width] = EMPTY;
      }
    }    
    
    for (int y = 0; y < MAPHEIGHT; y++)
    {
      for (int x = 0; x < MAPWIDTH; x++)
      {
        int xx = (1024-(MAPWIDTH*TILEWIDTH) - 10) + (x * TILEWIDTH);
        int yy = (768-(MAPHEIGHT*TILEHEIGHT) - 10) + (y * TILEHEIGHT);
        
        xx -= 512;
        yy -= 384;
        
        sprites[y][x] = new Sprite(textureRegions[map[y][x]]);
        sprites[y][x].setSize(TILEWIDTH, TILEHEIGHT);
        sprites[y][x].setOrigin(0, 0);
        sprites[y][x].setPosition(xx, yy);
      }
    }        
    
    if (mapStates[curLevel] == null)
    {
        mapStates[curLevel] = new MapState();
        
        mapStates[curLevel].seen = new boolean[height][width];
        mapStates[curLevel].items = new Item[height][width];
        
        for (int y = 0; y < height; y++)
        {
          for (int x = 0; x < width; x++)
          {
              mapStates[curLevel].seen[y][x] = false;
              mapStates[curLevel].items[y][x] = null;
          }
        }

        GenerateMonsters();        
        GenerateItems();
    }
    else
    {
      for (int i = 0; i < mapStates[curLevel].enemies.size(); i++)
      {
        mapStates[curLevel].enemies.get(i).SetMap(this);
        mapStates[curLevel].enemies.get(i).StartListening();
      } 
    }
    
    curState = mapStates[curLevel];    
    
    evtMgr.RegisterListener(this, EventType.evtCharDead);
  }
  
  public Room FindRoom(int p_x, int p_y)
  {
      Room result = null;
      
      for (int i = 0; i < rooms.size(); i++)
      {
          Room r = rooms.get(i);
          if ((p_x >= r.x) &&
              (p_x < r.x+r.width) &&
              (p_y >= r.y) &&
               (p_y < r.y+r.height))
          {
              result = r;
          }
      }
      
      return result;
  }

    public void update()
    {
        for (int i = 0; i < mapStates[curLevel].enemies.size(); i++)
        {
            mapStates[curLevel].enemies.get(i).DoControl();
        }
    }

    public void render(SpriteBatch batch)
    {
        for (int y = 0; y < MAPHEIGHT; y++)
        {
            for (int x = 0; x < MAPWIDTH; x++)
            {
                sprites[y][x].setColor(GetTileShade(x, y));
                sprites[y][x].draw(batch);
                if (mapStates[curLevel].items[y][x] != null)
                {
                    mapStates[curLevel].items[y][x].setColor(GetTileShade(x, y));
                    mapStates[curLevel].items[y][x].render(batch);
                }
            }
        }
        
        for (int i = 0; i < mapStates[curLevel].enemies.size(); i++)
        {
            Color col = GetTileShade((int)mapStates[curLevel].enemies.get(i).location.x, (int)mapStates[curLevel].enemies.get(i).location.y);
            if (!((col.r == SEEN) &&
                  (col.g == SEEN) &&
                  (col.b == SEEN)))
            {
                mapStates[curLevel].enemies.get(i).SetColor(col);
                mapStates[curLevel].enemies.get(i).render(batch);
            }
        }
    }
    
    private Color GetTileShade(int x, int y)
    {
        Color colour = null;
        
        if (Dungeon.revealMap)
        {
            colour = new Color(1.0f, 1.0f, 1.0f, 1.0f);
            return colour;
        }
        
        float xx = x - player.location.x;
        float yy = y - player.location.y;
        float dist = (float)Math.sqrt((xx*xx)+(yy*yy));
        
        if (dist <= 0.1f)
        {
            colour = new Color(1.0f, 1.0f, 1.0f, 1.0f);
            mapStates[curLevel].seen[y][x] = true;
            
            return colour;
        }
        
        if ((dist > LIGHTRADIUS + LIGHTFALLOFF) ||
            (!LineOfSight(this, new MapLocation(player.location), new MapLocation(x, y))))
        {
            if (mapStates[curLevel].seen[y][x])
            {
                colour = new Color(SEEN, SEEN, SEEN, 1.0f);
            } else
            {
                colour = new Color(UNSEEN, UNSEEN, UNSEEN, 1.0f);
            }
            return colour;
        }
         
        if (dist < LIGHTRADIUS)
        {
            colour = new Color(1.0f, 1.0f, 1.0f, 1.0f);
            mapStates[curLevel].seen[y][x] = true;
        } else
        {
            float shade = dist - LIGHTRADIUS;
            shade /= LIGHTFALLOFF;
            shade = 1.0f-shade;
            shade *= (1.0f - SEEN);
            shade += SEEN;
            colour = new Color(shade, shade, shade, 1.0f);
            mapStates[curLevel].seen[y][x] = true;
        }
        
        return colour;
    }

    public static boolean LineOfSight(Map p_level, MapLocation p_start, MapLocation p_target)
    {
        int x = p_target.x;
        int y = p_target.y;
        
        int deltaX1 = p_start.x - x;
        int deltaY1 = p_start.y - y;
     
        int deltaX = Math.abs(deltaX1)<<1;
        int deltaY = Math.abs(deltaY1)<<1;
     
        int sx = 1;
        int sy = 1;
        if (deltaX1 < 0)
        {
            sx = -1;
        }
        if (deltaY1 < 0)
        {
            sy = -1;
        }
     
        if(deltaX > deltaY)
        {
            int t = deltaY - (deltaX >> 1);
            do
            {
                if(t >= 0)
                {
                    y += sy;
                    t -= deltaX;
                }
     
               x += sx;
               t += deltaY;
     
               if ((x == p_start.x) && (y == p_start.y))
               {
                   return true;
               }
           }
           while(!SightBlocked(p_level, x,y));
     
           return false;
        }
       else
       {
          int t = deltaX - (deltaY >> 1);
          do
          {
             if(t >= 0)
             {
                x += sx;
                t -= deltaY;
             }
             y += sy;
             t += deltaX;
             if((x == p_start.x) && (y == p_start.y))
             {
                return true;
             }
          }
          while(!SightBlocked(p_level, x,y));
          return false;
       }
    }
    
    private static boolean SightBlocked(Map level, int x, int y)
    {
        if ((level.map[y][x] != level.EMPTY) &&
            (level.map[y][x] != level.STAIRSDOWN))
        {
            return true;
        }
        
        return false;
    }

    @Override
    public boolean ReceiveEvent(EtriumEvent p_event)
    {
        if (listening)
        {
            switch (p_event.type)
            {
                case evtCharDead:
                {
                    Character died = (Character)p_event.data;
                    
                    for (int i = 0; i < mapStates[curLevel].enemies.size(); i++)
                    {
                        if (died == mapStates[curLevel].enemies.get(i))
                        {
                            mapStates[curLevel].enemies.remove(died);
                            return true;
                        }
                    }
                    
                    return false;
                }
            }
        }
        
        return false;
    }

    @Override
    public void StartListening()
    {
        listening = true;
    }

    @Override
    public void StopListening()
    {
        listening = false;
    }

    public void RIP()
    {
      evtMgr.UnregisterListener(this, EventType.evtCharDead);
      evtMgr = null;
        
      stairsUp = null;
      stairsDown = null;
      for (int y = 0; y < MAPHEIGHT; y++)
      {
        for (int x = 0; x < MAPWIDTH; x++)
        {
          sprites[y][x] = null;
        }
      }
      rooms.clear();
        
      player = null;
        
      for (int i = 0; i < mapStates[curLevel].enemies.size(); i++)
      {
        mapStates[curLevel].enemies.get(i).StopListening();
      } 
    }
    
    public MapState GetState()
    {
      return mapStates[curLevel];
    }
    
    public Coordinates findRandomSpot()
    {
      Coordinates position = new Coordinates();

      boolean positionGood = false;
      while (!positionGood)
      {      
        position.x = randomGenerator.nextInt(width - 1);
        position.y = randomGenerator.nextInt(height - 1);
        
        int inMap = map[position.y][position.x];
        Item inItems = mapStates[curLevel].items[position.y][position.x];
        if ((inMap == EMPTY) &&
            (inItems == null))
        {
            boolean found = false;
            for (int i = 0; i < mapStates[curLevel].enemies.size(); i++)
            {
                Character inEnemies = mapStates[curLevel].enemies.get(i);
                
                if ((inEnemies.location.x == position.x) &&
                    (inEnemies.location.y == position.y))
                {
                    found = true;
                }
            }
            
            if (!found)
            {
                positionGood = true;
            }
        }
      }
      return position;
    }
    
    public void GenerateMonsters()
    {
      String[][] monsters = {{"explorer", "Explorer"},
                             {"dwarf"   , "Dwarf"},                             
                             {"medusa"  , "Gorgon"},
                             {"paladin" , "Paladin"},
                             {"warrior" , "Warrior"},
                             {"wizard"  , "Wizard"},
                             {"zombie"  , "Zombie"}};                             
          
      for (int i = 0; i < MAXMONSTERS; i++)
      {      
        Coordinates monsterPosition = findRandomSpot();
        int monsterType = randomGenerator.nextInt(monsters.length - 1);                
        
        Character character = new Character(
           new EnemyController(),
           new SimpleSpriteRenderer("characters/" + monsters[monsterType][0]),
           new EnemyInventory(),
           new EnemySpecGenerator(),
           new EnemyUI(),
           this,
           curLevel,
           monsterPosition.x,
           monsterPosition.y);
        character.SetName( monsters[monsterType][1]);
        
        mapStates[curLevel].enemies.add( character);                              
      }
    }
    
    public void GenerateItems()
    {      
      for (int i = 0; i < MAXITEMS; i++)
      {
        Coordinates itemPosition = findRandomSpot();
                
        Item item = new Item(this, curLevel);
        item.SetLocation(itemPosition.x, itemPosition.y);
        mapStates[curLevel].items[itemPosition.y][itemPosition.x] = item;        
      }      
    }

    public void SetPlayer(Character p_player)
    {
        player = p_player;
        CenterMapWindowOnPlayer();
    }
    
    public void CenterMapWindowOnPlayer()
    {
        mapWindow.x = (int) (player.location.x - 10);
        mapWindow.y = (int) (player.location.y - 10);
    }
}
