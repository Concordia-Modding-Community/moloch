package ca.concordia.moloch.item;

import ca.concordia.moloch.MolochMod;
import net.minecraft.item.Item;

public class OperatorKey extends Item {
    public OperatorKey() {
        super(new Properties().maxStackSize(1).group(MolochMod.ITEM_GROUP));
    }
}