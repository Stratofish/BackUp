package com.etrium.backup.item;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.etrium.backup.Map;
import com.etrium.backup.MapLocation;
import com.etrium.backup.character.BaseStats;

public class Item
{
    public enum Class
    {
        WEAPON,
        ARMOUR,
        CONSUMABLE
    }

    public enum Subclass
    {
        NONE,
        HELMET,
        CHEST,
        LEGS,
        BOOTS,
        GLOVES,
        HEALTHPOTION,
        MANAPOTION,
        DAGGER,
        SWORD,
        AXE
    }
    
    private ItemTypeInterface type;
    public MapLocation location;
    private Map map = null;
    public int itemLevel = 0;
    public BaseStats stats = null;
    
    public Item(Map p_level, int p_itemLevel)
    {
        map = p_level;
        itemLevel = p_itemLevel;
        
        Random randomGenerator = new Random();
        float chance = randomGenerator.nextFloat();
        
        if (chance < 0.5f)
        {
            type = new ConsumableType();
        } else
        {
            chance -= 0.5f;
            if (chance < 0.25f)
            {
                type = new WeaponType();
            } else
            {
                type = new ArmourType();
            }
        }
        
        location = new MapLocation();
        location.x = 0;
        location.y = 0;
        
        type.Create(itemLevel);
        type.SetLocation(location);
        type.SetLevel(map);
        
        stats = type.GetStats();
    }
    
    public Item(Map p_level, int p_itemLevel, Class p_type, Subclass p_subType)
    {
        map = p_level;
        itemLevel = p_itemLevel;
        
        switch (p_type)
        {
            case WEAPON:
            {
                type = new WeaponType();
                break;
            }
            case ARMOUR:
            {
                type = new ArmourType(p_subType);
                break;
            }
            case CONSUMABLE:
            {
                type = new ConsumableType();
                break;
            }
        }
        
        location = new MapLocation();
        location.x = 0;
        location.y = 0;
        
        type.Create(itemLevel);
        type.SetLocation(location);
        type.SetLevel(map);
        
        stats = type.GetStats();
    }
    
    public void render(SpriteBatch batch)
    {
        type.render(batch);
    }
    
    public void SetLocation(int p_x, int p_y)
    {
        location.x = p_x;
        location.y = p_y;
    }
    
    public void SetMap(Map p_map)
    {
        map = p_map;
    }

    public String GetName()
    {
        return type.GetName();
    }
    
    public String GetIconName()
    {
        return type.GetIconName();
    }

    public Class GetType()
    {
        return type.GetType();
    }
    
    public Subclass GetSubClass()
    {
        return type.GetSubClass();
    }
    
    public void setColor(Color p_colour)
    {
        type.SetColor(p_colour);
    }
}
