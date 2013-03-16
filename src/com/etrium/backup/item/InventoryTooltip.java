package com.etrium.backup.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.etrium.backup.character.Character;
import com.etrium.backup.system.EtriumEvent;
import com.etrium.backup.system.EventManager;
import com.etrium.backup.system.EventType;

public class InventoryTooltip
{
    private Vector2 mouse = new Vector2();
    private Vector2 pointer = new Vector2();
    private Character player = null;
    private PlayerInventory.ItemData hoveredItemData = null;
    private boolean newHover = false;
    private Stage stage = null;
    boolean leftClickHeld = false;
    boolean rightClickHeld = false;
    private EventManager evtMgr = new EventManager();
    
    public InventoryTooltip(Character p_player)
    {
        player = p_player;
    }
    
    public void SetStage(Stage p_stage)
    {
        stage = p_stage;
    }
    
    public void Update()
    {
        mouse.x = Gdx.input.getX();
        mouse.y = Gdx.input.getY();
        Vector2 mouseWindow = mouse.cpy();
        
        pointer = ((PlayerInventory)player.inventory).window.screenToLocalCoordinates(mouseWindow);
        
        PlayerInventory inv = (PlayerInventory)player.inventory;
        Actor actor = inv.window.hit(pointer.x, pointer.y, true);
        
        boolean newLeftClick = false;
        if (Gdx.input.isButtonPressed(Buttons.LEFT))
        {
            if (!leftClickHeld)
            {
                newLeftClick = true;
                leftClickHeld = true;
            }
        } else
        {
            leftClickHeld = false;
        }
        
        boolean newRightClick = false;
        if (Gdx.input.isButtonPressed(Buttons.RIGHT))
        {
            if (!rightClickHeld)
            {
                newRightClick = true;
                rightClickHeld = true;
            }
        } else
        {
            rightClickHeld = false;
        }
        
        if (actor != null)
        {
            boolean found = false;
            
            for (int i = 0; i < inv.itemData.size(); i++)
            {
                Vector2 mouseIcon = mouse.cpy();
                Vector2 local = inv.itemData.get(i).image.screenToLocalCoordinates(mouseIcon);
                if (inv.itemData.get(i).image.hit(local.x, local.y, true) == inv.itemData.get(i).image)
                {
                    if (hoveredItemData != inv.itemData.get(i))
                    {
                        newHover = true;
                        
                        if (hoveredItemData != null)
                        {
                            hoveredItemData.tooltip.remove();
                            hoveredItemData = null;
                        }
                    }
                    
                    hoveredItemData = inv.itemData.get(i);
                    
                    found = true;
                }
            }
            
            if (found)
            {
                if (newHover)
                {
                    stage.addActor(hoveredItemData.tooltip);
                    newHover = false;
                }
                Vector2 tooltipPos = mouse.cpy();
                stage.screenToStageCoordinates(tooltipPos);
                hoveredItemData.tooltip.setPosition(tooltipPos.x, tooltipPos.y);
                
                // Check for mouse clicks
                if (newLeftClick)
                {
                    EtriumEvent event = new EtriumEvent();
                    event.type = EventType.evtItemUsed;
                    event.data = hoveredItemData.item;
                    evtMgr.SendEvent(event,  true);
                    
                    hoveredItemData.tooltip.remove();
                    hoveredItemData = null;
                }
                if (newRightClick)
                {
                    // Discard item
                    inv.RemoveItem(hoveredItemData.item);
                }
            } else
            {
                if (hoveredItemData != null)
                {
                    hoveredItemData.tooltip.remove();
                }
                hoveredItemData = null;
            }
        }
    }
    
    public void UpdateGear()
    {
        mouse.x = Gdx.input.getX();
        mouse.y = Gdx.input.getY();
        Vector2 mouseWindow = mouse.cpy();
        
        pointer = player.gearWindow.screenToLocalCoordinates(mouseWindow);
        Actor actor = player.gearWindow.hit(pointer.x, pointer.y, true);

        if (actor != null)
        {
            boolean found = false;
            
            PlayerInventory inv = (PlayerInventory)player.inventory;
            
            for (int i = 0; i < player.gear.length; i++)
            {
                Vector2 mouseIcon = mouse.cpy();
                Vector2 local = inv.equippedItemData[i].image.screenToLocalCoordinates(mouseIcon);
                
                if (inv.equippedItemData[i].image.hit(local.x, local.y, true) == inv.equippedItemData[i].image)
                {
                    if (hoveredItemData != inv.equippedItemData[i])
                    {
                        newHover = true;

                        if (hoveredItemData != null)
                        {
                            hoveredItemData.tooltip.remove();
                            hoveredItemData = null;
                        }
                    }
                    
                    hoveredItemData = inv.equippedItemData[i];
                    
                    found = true;
                }
            }
            
            if (found)
            {
                if (newHover)
                {
                    stage.addActor(hoveredItemData.tooltip);
                    newHover = false;
                }
                Vector2 tooltipPos = mouse.cpy();
                stage.screenToStageCoordinates(tooltipPos);
                hoveredItemData.tooltip.setPosition(tooltipPos.x, tooltipPos.y);
            } else
            {
                if (hoveredItemData != null)
                {
                    hoveredItemData.tooltip.remove();
                }
                hoveredItemData = null;
            }
        }
    }
}