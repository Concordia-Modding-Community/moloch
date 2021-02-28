package ca.concordia.moloch.tileentity.moloch.desire;

import com.mojang.brigadier.StringReader;

import net.minecraft.command.arguments.ItemParser;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class Desire {
	private long id;
	private String item;
	private int amountTotal;
	private int amountRemaining;

	protected Desire(long id, String item, int amountTotal, int amountRemaining) {
		super();
		this.id = id;
		this.item = item;
		this.amountTotal = Math.max(0, amountTotal);
		this.amountRemaining = Math.max(0, amountRemaining);
	}

	public String getItemName() {
		return item;
	}
	
	public Item getItem() {
		ItemParser itemParser = new ItemParser(new StringReader(this.item), true);
		
        try {
            itemParser.readItem();
            return itemParser.getItem();
        } catch(Exception e) {
            return Items.BEDROCK;
        }
	}

	public void setItem(String item) {
		this.item = item;
	}

	public void setItem(Item item) {
		this.item = item.getName().toString();
	}

	public int getAmountTotal() {
		return amountTotal;
	}

	public void setAmountTotal(int amountTotal) {
		this.amountTotal = amountTotal;
	}

	public int getAmountRemaining() {
		return amountRemaining;
	}

	public void setAmountRemaining(int amountRemaining) {
		this.amountRemaining = amountRemaining;
	}

	public void decrementAmountRemaining() {
		this.setAmountRemaining(this.amountRemaining-1);
	}
	
	public long getId() {
		return id;
	}
}
