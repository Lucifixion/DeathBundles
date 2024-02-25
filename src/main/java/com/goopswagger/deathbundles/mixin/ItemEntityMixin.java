package com.goopswagger.deathbundles.mixin;

import com.goopswagger.deathbundles.DeathBundles;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Shadow public abstract ItemStack getStack();
    @Shadow public abstract void setNeverDespawn();

    @Inject(at = @At("TAIL"), method = "setStack")
    private void setStack(ItemStack stack, CallbackInfo ci) {
        if (stack.getItem() == DeathBundles.DEATH_BUNDLE)
            setNeverDespawn();
    }

    @Inject(at = @At("HEAD"), method = "damage", cancellable = true)
    private void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!this.getStack().isEmpty() && this.getStack().isOf(DeathBundles.DEATH_BUNDLE))
            cir.setReturnValue(false);
    }
}
