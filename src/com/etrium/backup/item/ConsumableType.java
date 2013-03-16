package com.etrium.backup.item;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.etrium.backup.Map;
import com.etrium.backup.MapLocation;
import com.etrium.backup.SimpleSpriteRenderer;
import com.etrium.backup.TileRenderer;
import com.etrium.backup.character.BaseStats;
import com.etrium.backup.item.Item.Class;
import com.etrium.backup.item.Item.Subclass;

public class ConsumableType implements ItemTypeInterface
{
    private Item.Class itemClass = Class.CONSUMABLE;
    private Item.Subclass itemSubClass = Subclass.NONE;
    private TileRenderer tile = null;
    private MapLocation location;
    private Map level;
    private String iconName = "";
    private BaseStats stats = null;
    private String name;

    @Override
    public void Create(int level)
    {
        String[][] items = {
            {"pots/potionred", "Health potion"},
            {"misc/food01"   , "Bread"},                             
            {"misc/food02"   , "Grapes"},
            {"misc/food03"   , "Carrot"},
            {"misc/food04"   , "Egg"},            
            {"misc/bandage01", "Bandage"}};

        Random random = new Random();                        
        int itemType = random.nextInt(items.length - 1);                
        
        itemSubClass = Subclass.HEALTHPOTION;
        iconName = items[itemType][0];
        tile = new SimpleSpriteRenderer(iconName);
        name = items[itemType][1];
        
        tile.LoadResources();
        
        stats = new BaseStats();
        stats.HP = 25;
    }
    
    @Override
    public Class GetType()
    {
        return itemClass;
    }

    @Override
    public String GetName()
    {
        return name;
    }
    
    @Override
    public void render(SpriteBatch batch)
    {
        tile.Render(batch, location);
    }

    @Override
    public void SetLocation(MapLocation p_location)
    {
        location = p_location;
    }

    @Override
    public void SetLevel(Map p_level)
    {
        level = p_level;
        tile.SetLevel(level);
    }

    @Override
    public String GetIconName()
    {
        return iconName;
    }
    
    @Override
    public void SetColor(Color p_colour)
    {
        tile.SetColor(p_colour);
    }

    @Override
    public BaseStats GetStats()
    {
        return stats;
    }

    
    @Override
    public Subclass GetSubClass()
    {
        return itemSubClass;
    }
}
