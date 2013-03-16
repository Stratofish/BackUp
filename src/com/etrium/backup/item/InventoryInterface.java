package com.etrium.backup.item;

import com.etrium.backup.item.Item;
import com.etrium.backup.character.Character;

public interface InventoryInterface
{
    public void SetCharacter(Character p_char);
    public void AddItem(Item item);
    public void RemoveItem(Item item);
    public void Update();
}
