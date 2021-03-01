package ca.concordia.moloch.init;

import ca.concordia.moloch.Resources;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class ModSounds {
	//Found Public Domain Sound
	//2021-02-28
	//https://soundbible.com/1813-Slowed-Breathing.html
	public static final SoundEvent molochBreathe = createRegisteredSoundEvent("moloch_breathing");


	private static SoundEvent createRegisteredSoundEvent(String soundEvent) {
		ResourceLocation loc = new ResourceLocation(Resources.MOD_ID, soundEvent);
		return new SoundEvent(loc).setRegistryName(loc);
	}
}
