package net.threetag.palladium.client.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.threetag.palladium.power.ability.AbilityInstance;
import net.threetag.palladium.power.ability.PlaySoundAbility;
import net.threetag.palladium.sound.AbilitySound;

public final class AbilitySoundHandler {

    private AbilitySoundHandler() {
    }

    public static void startSound(LivingEntity entity, AbilityInstance entry) {
        boolean play;

        if (entry.getProperty(PlaySoundAbility.PLAY_SELF)) {
            play = entity == Minecraft.getInstance().player;
        } else if (entry.getProperty(PlaySoundAbility.PLAY_OTHERS)) {
            play = entity != Minecraft.getInstance().player;
        } else {
            play = true;
        }

        if (play) {
            Minecraft.getInstance().getSoundManager().play(new AbilitySound(entry.getReference(), entity, entry.getProperty(PlaySoundAbility.SOUND), entity.getSoundSource(), entry.getProperty(PlaySoundAbility.VOLUME), entry.getProperty(PlaySoundAbility.PITCH)));
        }
    }
}
