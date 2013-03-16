package com.etrium.backup.item;

import java.util.ArrayList;
import java.util.List;

import com.etrium.backup.character.Character;


public class EnemyInventory implements InventoryInterface
{
    private List<Item> items = new ArrayList<Item>();
    
    public EnemyInventory()
    {
        
    }
    
    @Override
    public void AddItem(Item p_item)
    {
        items.add(p_item);
    }


    @Override
    public void Update()
    {
    }

    @Override
    public void RemoveItem(Item item)
    {
    }


    @Override
    public void SetCharacter(Character p_char)
    {
    }
}
