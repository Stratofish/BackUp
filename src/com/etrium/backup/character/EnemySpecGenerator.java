package com.etrium.backup.character;

import java.util.Random;

public class EnemySpecGenerator implements SpecGenerator
{
    private String name = "Monster";

    @Override
    public void SetName( String pName)
    {
      name = pName;
    }
    
    @Override
    public String GetName()
    {
        return name;
    }

    @Override
    public BaseStats GetStats(int p_level)
    {
        BaseStats bs = new BaseStats();
        
        bs.HP = 3 * p_level;
        bs.MP = 5 * p_level;
        bs.armour = 8 * p_level;
        bs.dodge = 0.1f * p_level;
        bs.critChance = 0.0f;
        bs.critAmount = 0;
        bs.str = 0;
        
        return bs;
    }

    @Override
    public BaseStats GetItemStats(int level, float scaler)
    {
        Random rg = new Random();
        BaseStats bs = new BaseStats();
        
        float[] stats = new float[8];
        for (int i = 0; i < 8; i++)
        {
            stats[i] = 2.0f;
        }
        
        for (int i = 0; i < 100; i++)
        {
            int num1 = rg.nextInt(8);
            int num2 = -1;
            do
            {
                num2 = rg.nextInt(8);
            } while (num2 == num1);
            
            float diff = rg.nextFloat() * Math.min(stats[num1], stats[num2]);
            
            stats[num1] += diff;
            stats[num2] -= diff;
        }
        
        int newLev = level;
        
        bs.MP = (int) (stats[1] * newLev);
        bs.absorb = (int) (stats[4] * newLev);
        

        bs.HP = (newLev*8) + (int)(stats[0] * (newLev / 10.0f));
        bs.armour = (newLev*2) + (int) (stats[2] * (newLev/5));
        bs.str = (int) ((newLev / 2) + stats[7]);
        
        bs.dodge = (stats[3] * newLev) / 100.0f;
        bs.critChance = stats[5] / 100.0f;
        bs.critAmount = (int) (stats[6] * (newLev / 3.5f));

        return bs;
    }
}
