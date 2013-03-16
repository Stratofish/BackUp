package com.etrium.backup.item;

import java.io.IOException;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.etrium.backup.Map;
import com.etrium.backup.MapLocation;
import com.etrium.backup.SimpleSpriteRenderer;
import com.etrium.backup.character.BaseStats;
import com.etrium.backup.item.WeaponType.WeaponTypeClass;
import com.etrium.backup.system.NameGenerator;

public class DaggerType implements WeaponTypeInterface
{
    private final int DAGGERTYPES = 9;
    private SimpleSpriteRenderer tile;
    private MapLocation location;
    private Map level;
    private String name;
    private Random randomGenerator = new Random();
    private String iconName = "";
    private BaseStats stats = null;
    
    @Override
    public void Create()
    {
        int spriteNum = randomGenerator.nextInt(DAGGERTYPES)+1;
        iconName = "weapons/dagger0"+spriteNum;
        tile = new SimpleSpriteRenderer(iconName);
        tile.LoadResources();
        
        String ngOut = "NameGenerator";
        try
        {
            NameGenerator ng = new NameGenerator("data/elvish.syl");
            ngOut = ng.compose(randomGenerator.nextInt(2)+2);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        name = "Dagger of "+ngOut;
    }

    @Override
    public WeaponTypeClass GetType()
    {
        return WeaponTypeClass.SWORD;
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
    public void SetColour(Color p_colour)
    {
        tile.SetColor(p_colour);
    }

    @Override
    public BaseStats GetStats()
    {
        return stats;
    }
}
