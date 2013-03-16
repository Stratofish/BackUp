package com.etrium.backup.character;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.etrium.backup.Map;

public interface CharUI
{
    public Table GetStatsUI();
    public void SetMap(Map p_map);
    public void SetChar(Character p_char);
}
