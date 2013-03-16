package com.etrium.backup;

import java.util.ArrayList;

import com.etrium.backup.character.Character;
import com.etrium.backup.item.Item;

public class MapState
{
    public boolean seen[][];
    public Item items[][];
    public java.util.List<Character> enemies = new ArrayList<Character>();
}
