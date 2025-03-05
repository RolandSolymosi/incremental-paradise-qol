package com.incrementalqol.mixins;

import com.incrementalqol.modules.TaskTracker.TaskTrackerModule;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;


@Mixin(BossBarHud.class)
public class TaskProgressMixin {

    @Final
    @Shadow
    private Map<UUID, ClientBossBar> bossBars;

    private static AtomicBoolean ongoing = new AtomicBoolean();

    @Inject(at=@At("HEAD"), method="render(Lnet/minecraft/client/gui/DrawContext;)V")
    private void onRender(DrawContext ctx, CallbackInfo ci) {
        if (ongoing.compareAndSet(false, true)){
            if (bossBars != null && !bossBars.isEmpty()) {
                var keys = new ArrayList<>(bossBars.keySet());
                for (var key : keys){
                    var bar = bossBars.getOrDefault(key, null);
                    if (bar != null){
                        TaskTrackerModule.taskList.forEach(task -> {
                            task.bossBarForTask(bar);
                        });
                    }
                }
            }
            ongoing.set(false);
        }
    }
}
