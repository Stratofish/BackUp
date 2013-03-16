package com.etrium.backup.item;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.etrium.backup.Map;
import com.etrium.backup.MapLocation;
import com.etrium.backup.character.BaseStats;
import com.etrium.backup.character.PlayerSpecGenerator;
import com.etrium.backup.character.SpecGenerator;
import com.etrium.backup.item.Item.Class;
import com.etrium.backup.item.Item.Subclass;

public class WeaponType implements ItemTypeInterface
{
    public enum WeaponTypeClass
    {
        SWORD,
        DAGGER,
        POLEARM,
        BOW,
        AXE,
        MACE
    }
    
    private Item.Class itemClass = Class.WEAPON;
    private Item.Subclass itemSubClass = Subclass.NONE;
    private SpecGenerator specGen = new PlayerSpecGenerator();
    private WeaponTypeInterface weapon = null;
    private BaseStats stats = null;
    
    @Override
    public Class GetType()
    {
        return itemClass;
    }
    
    @Override
    public String GetName()
    {
        return weapon.GetName();
    }
    
    @Override
    public void Create(int level)
    {
        Random randomGenerator = new Random();
        int chance = randomGenerator.nextInt(3);
        
        switch (chance)
        {
            case 0:
            {
                weapon = new SwordType();
                itemSubClass = Subclass.SWORD;
                break;
            }
            case 1:
            {
                weapon = new AxeType();
                itemSubClass = Subclass.AXE;
                break;
            }
            case 2:
            {
                weapon = new DaggerType();
                itemSubClass = Subclass.DAGGER;
                break;
            }
        }
        
        weapon.Create();
        
        stats = specGen.GetItemStats(level, 2.0f);
    }
    

    @Override
    public void render(SpriteBatch batch)
    {
        weapon.render(batch);
    }

    @Override
    public void SetLocation(MapLocation p_location)
    {
        weapon.SetLocation(p_location);
    }

    @Override
    public void SetLevel(Map p_level)
    {
        weapon.SetLevel(p_level);
    }

    @Override
    public String GetIconName()
    {
        return weapon.GetIconName();
    }

    
    @Override
    public void SetColor(Color p_colour)
    {
        weapon.SetColour(p_colour);
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
