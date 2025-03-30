package com.incrementalqol.mixinWrappers;

import com.incrementalqol.modules.TaskTracker.TaskTrackerModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ClientBossBar;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class BossBarHud {

    private static final AtomicBoolean ongoing = new AtomicBoolean();

    public static void render(Map<UUID, ClientBossBar> bossBars,  DrawContext ctx, CallbackInfo ci) {
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
