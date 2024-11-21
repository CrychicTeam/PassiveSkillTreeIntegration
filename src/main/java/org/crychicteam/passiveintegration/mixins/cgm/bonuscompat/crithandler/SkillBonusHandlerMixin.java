package org.crychicteam.passiveintegration.mixins.cgm.bonuscompat.crithandler;

import com.mrcrayfish.guns.entity.ProjectileEntity;
import daripher.skilltree.skill.bonus.SkillBonusHandler;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkillBonusHandler.class)
public class SkillBonusHandlerMixin {
    @Inject(method = "applyCritBonuses(Lnet/minecraftforge/event/entity/living/LivingHurtEvent;)V", at = @At(value = "HEAD"), remap = false, cancellable = true)
    private static void passiveIntegration$applyCritBonuses(LivingHurtEvent event, CallbackInfo ci) {
        if (event.getSource().getDirectEntity() instanceof ProjectileEntity) ci.cancel();
    }
}
