package com.etrium.backup.character;

public interface SpecGenerator
{
    public enum UserType
    {
        CASTER,
        MELEE,
        HYBRID
    }
    
    void SetName(String pName);
    String GetName();
    BaseStats GetStats(int p_level);
    BaseStats GetItemStats(int level, float scaler);
}
