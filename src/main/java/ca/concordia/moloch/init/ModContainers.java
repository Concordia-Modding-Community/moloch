package ca.concordia.moloch.init;

import ca.concordia.moloch.Resources;
import ca.concordia.moloch.container.MolochContainer;
import ca.concordia.moloch.container.MolochOPContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainers {
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister
            .create(ForgeRegistries.CONTAINERS, Resources.MOD_ID);

    public static final RegistryObject<ContainerType<MolochContainer>> MOLOCH = CONTAINERS.register("moloch",
            () -> IForgeContainerType.create(MolochContainer::new));

    public static final RegistryObject<ContainerType<MolochOPContainer>> MOLOCH_OP = CONTAINERS.register("moloch_op",
            () -> IForgeContainerType.create(MolochOPContainer::new));   
}
