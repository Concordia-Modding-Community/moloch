package ca.concordia.moloch.common;



import ca.concordia.moloch.Resources;
import ca.concordia.moloch.init.ModSounds;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = Resources.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventSubscriber {

	@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> evt) {
		IForgeRegistry<SoundEvent> r = evt.getRegistry();
		r.register(ModSounds.molochBreathe);

	}
}
