package com.incrementalqol.mixins;

import com.incrementalqol.modules.TaskTracker.TaskTrackerModule;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;

import java.util.Map;
import java.util.UUID;


@Mixin(BossBarHud.class)
public class TaskProgressMixin {

    @Final
    @Shadow
    private Map<UUID, ClientBossBar> bossBars;

    @Inject(at=@At("HEAD"), method="render(Lnet/minecraft/client/gui/DrawContext;)V")
    private void onRender(DrawContext ctx, CallbackInfo ci) {
        if (bossBars != null && !bossBars.isEmpty()) {
            bossBars.forEach((uuid, bar) -> {
                TaskTrackerModule.taskList.forEach(task -> {
                    task.bossBarForTask(bar);
                });
            });
        }
    }
}
