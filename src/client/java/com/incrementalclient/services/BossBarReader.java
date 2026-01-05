package com.incrementalclient.services;

import com.incrementalclient.abstractions.ObservableBase;
import com.incrementalclient.interfaces.Observer;
import net.minecraft.client.gui.hud.ClientBossBar;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class BossBarReader extends ObservableBase<Observer<BossBarReader.BossBar>, BossBarReader.BossBar> {

    private final AtomicBoolean ongoing = new AtomicBoolean();

    public void bossBarUpdate(Map<UUID, ClientBossBar> bossBars) {
        if (ongoing.compareAndSet(false, true)) {
            if (bossBars != null && !bossBars.isEmpty()) {
                var keys = new ArrayList<>(bossBars.keySet());
                for (var key : keys) {
                    var bar = bossBars.getOrDefault(key, null);
                    if (bar != null) {
                        notifyObservers(new BossBar(bar.getName().getString(), bar.getPercent()));
                    }
                }
            }
            ongoing.set(false);
        }
    }

    public record BossBar(String text, double percent) {
    }
}
