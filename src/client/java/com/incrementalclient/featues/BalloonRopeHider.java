package com.incrementalclient.featues;

import com.google.common.base.Suppliers;
import com.incrementalclient.interfaces.Configurable;
import com.incrementalclient.interfaces.Observer;
import com.incrementalclient.internals.EntityRendererObservable;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Supplier;

public class BalloonRopeHider implements Configurable<BalloonRopeHider.Configuration>, Observer<EntityRendererObservable.EntityRender> {

    private final Configuration configuration = new Configuration();

    private final Supplier<List<OptionPiece>> options;

    public BalloonRopeHider(
            EntityRendererObservable entityRendererObservable
    ) {
        entityRendererObservable.subscribe(this);
        options = Suppliers.memoize(() -> List.of(Categories.Misc.General.createConfig(0,
                        Option.<Boolean>createBuilder()
                                .name(Text.of("Toggle balloon ropes for self."))
                                .description(OptionDescription.of(Text.of("Turn on and off the balloon rope attached to the player.")))
                                .binding(Configuration.defaultIsHidden, () -> configuration.isHidden, newVal -> configuration.isHidden = newVal)
                                .controller(BooleanControllerBuilder::create)
                                .build())));
    }

    @Override
    public String getJsonSection() {
        return "balloonRopeHider";
    }

    @Override
    public BalloonRopeHider.Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public List<OptionPiece> getOption() {
        return options.get();
    }

    @Override
    public void optionChanged() {

    }

    @Override
    public void onEvent(EntityRendererObservable.EntityRender result) {
        if (configuration.isHidden){
            if (result.entity() instanceof SilverfishEntity silverfishEntity) {
                if (silverfishEntity.getLeashHolder() instanceof PlayerEntity player) {
                    if (player == MinecraftClient.getInstance().player) {
                        silverfishEntity.detachLeash();
                    }
                }
            }
        }
    }

    static public class Configuration {

        private static final boolean defaultIsHidden = true;

        @SerialEntry
        public boolean isHidden = defaultIsHidden;

    }
}
