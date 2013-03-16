package com.etrium.backup.character;

public class BaseStats
{
    public int HP = 0;
    public int MP = 0;
    public int str = 0;
    public int armour = 0;
    public float dodge = 0;
    public int absorb = 0;
    public float critChance = 0;
    public int critAmount = 0;
    
    public static BaseStats Add(BaseStats bs1, BaseStats bs2)
    {
        BaseStats bs = new BaseStats();
        
        bs.HP = bs1.HP + bs2.HP;
        bs.MP = bs1.MP + bs2.MP;
        bs.str = bs1.str + bs2.str;
        bs.armour = bs1.armour + bs2.armour;
        bs.dodge = bs1.dodge + bs2.dodge;
        bs.absorb = bs1.absorb + bs2.absorb;
        bs.critChance = bs1.critChance + bs2.critChance;
        bs.critAmount = bs1.critAmount + bs2.critAmount;
        
        return bs;
    }

    
    public BaseStats Copy()
    {
        BaseStats copy = new BaseStats();
        
        copy.HP = HP;
        copy.MP = MP;
        copy.str = str;
        copy.armour = armour;
        copy.dodge = dodge;
        copy.absorb = absorb;
        copy.critChance = critChance;
        copy.critAmount = critAmount;
        
        return copy;
    }


    
    public void Reset()
    {
        HP = 0;
        MP = 0;
        str = 0;
        armour = 0;
        dodge = 0;
        absorb = 0;
        critChance = 0;
        critAmount = 0;
    }
}
