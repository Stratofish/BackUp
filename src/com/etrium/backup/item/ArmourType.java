package com.etrium.backup.item;

import java.io.IOException;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.etrium.backup.Map;
import com.etrium.backup.MapLocation;
import com.etrium.backup.SimpleSpriteRenderer;
import com.etrium.backup.TileRenderer;
import com.etrium.backup.character.BaseStats;
import com.etrium.backup.character.PlayerSpecGenerator;
import com.etrium.backup.character.SpecGenerator;
import com.etrium.backup.item.Item.Class;
import com.etrium.backup.item.Item.Subclass;
import com.etrium.backup.system.NameGenerator;

public class ArmourType implements ItemTypeInterface
{
    private Item.Class itemClass = Class.ARMOUR;
    private Subclass itemSubClass = Item.Subclass.NONE;
    private TileRenderer tile = null;
    private MapLocation location = null;
    private Map level;
    private String name;
    private String iconName = "";
    private SpecGenerator specGen = new PlayerSpecGenerator();
    private BaseStats stats = null;
    
    public ArmourType()
    {
        itemSubClass = Item.Subclass.NONE;
    }
    
    public ArmourType(Subclass p_subType)
    {
        itemSubClass = p_subType;
    }

    @Override
    public void Create(int level)
    {
        Random rnd = new Random();
        String armourName = "undefined";

        if (itemSubClass == Item.Subclass.NONE)
        {
            int r = rnd.nextInt(5);
            switch (r)
            {
                case 0:
                {
                    int rand = rnd.nextInt(5) + 1;
                    iconName = "armour/hat0"+rand;
                    itemSubClass = Item.Subclass.HELMET;
                    break;
                }
                case 1:
                {
                    int rand = rnd.nextInt(5) + 1;
                    iconName = "armour/armour0"+rand;
                    itemSubClass = Item.Subclass.CHEST;
                    break;
                }
                case 2:
                {
                    iconName = "armour/legs01";
                    itemSubClass = Item.Subclass.LEGS;
                    break;
                }
                case 3:
                {
                    int rand = rnd.nextInt(5) + 1;
                    iconName = "armour/boots0"+rand;
                    itemSubClass = Item.Subclass.BOOTS;
                    break;
                }
                case 4:
                {
                    iconName = "armour/gloves01";
                    itemSubClass = Item.Subclass.GLOVES;
                    break;
                }
            }
        }
        
        switch (itemSubClass)
        {
            case HELMET:
            {
                int rand = rnd.nextInt(5) + 1;
                iconName = "armour/hat0"+rand;
                stats = specGen.GetItemStats(level, 1.0f);
                armourName = "Helmet";
                break;
            }
            case CHEST:
            {
                int rand = rnd.nextInt(5) + 1;
                iconName = "armour/armour0"+rand;
                stats = specGen.GetItemStats(level, 2.5f);
                armourName = "Chest";
                break;
            }
            case LEGS:
            {
                iconName = "armour/legs01";
                stats = specGen.GetItemStats(level, 1.5f);
                armourName = "Leggings";
                break;
            }
            case BOOTS:
            {
                int rand = rnd.nextInt(5) + 1;
                iconName = "armour/boots0"+rand;
                stats = specGen.GetItemStats(level, 0.8f);
                armourName = "Boots";
               break;
            }
            case GLOVES:
            {
                iconName = "armour/gloves01";
                stats = specGen.GetItemStats(level, 0.8f);
                armourName = "Gloves";
                break;
            }
        }
        
        String ngOut="";
        
        try
        {
            NameGenerator ng = new NameGenerator("data/elvish.syl");
            ngOut = ng.compose(rnd.nextInt(2)+2);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        name = armourName + " of "+ngOut;
        
        tile = new SimpleSpriteRenderer(iconName);
        tile.LoadResources();
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