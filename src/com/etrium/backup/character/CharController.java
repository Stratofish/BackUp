package com.etrium.backup.character;

import com.etrium.backup.Map;

public interface CharController
{
    public boolean DoControl();
    public void SetLevel(Map p_level); 
    public void SetCharacter(Character p_char);
    public boolean isPlayer();
}
