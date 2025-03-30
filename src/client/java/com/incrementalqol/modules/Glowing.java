package com.incrementalqol.modules;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.ConcurrentHashMap;


public class Glowing implements ClientModInitializer {
    private static final double PROXIMITY_THRESHOLD = 0.1f;
    private static final double TOLERANCE = 0.001f; // Allow for slight floating point errors

    private static KeyBinding glowKey;
    private static KeyBinding disableGlowKey;
    private static EntityType<?> glowingEntityType = null;
    private static Block blockType = null;

    private static final ConcurrentHashMap<BlockPos, HighlightEntity> attachedEntities = new ConcurrentHashMap<>();

    // Create the RegistryKey for the entity type
    RegistryKey<EntityType<?>> entityKey = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of("incrementalqol", "highlight_entity"));

    @Override
    public void onInitializeClient() {
        Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of("incrementalqol", "highlight_entity"),
                EntityType.Builder.create(HighlightEntity::new, SpawnGroup.MISC)
                        .dimensions(2.0F, 2.0F) // Use the size defined in getDimensions
                        .maxTrackingRange(10)
                        .trackingTickInterval(20)
                        .build(entityKey)
        );
        EntityRendererRegistry.register((EntityType<HighlightEntity>) Registries.ENTITY_TYPE.get(Identifier.of("incrementalqol", "highlight_entity")), HighlightEntityRenderer::new);

        glowKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Select Target to Glow",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G, // Default key for enabling glow
                "Incremental QOL"
        ));

        disableGlowKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Disable Glow Selection",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H, // Default key for disabling glow
                "Incremental QOL"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (glowKey.wasPressed()) {
                updateGlowingTarget(client);
            }
            if (disableGlowKey.wasPressed()) {
                reset();
            }
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            reset();
        });
        ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register((client, world) -> {
            reset();
        });
    }

    private void reset() {
        glowingEntityType = null;
        blockType = null;
        attachedEntities.clear();
    }

    private void updateGlowingTarget(MinecraftClient client) {
        if (client.player == null) return;
        if (client.world == null) return;

        HitResult hit = client.crosshairTarget;
        if (hit instanceof EntityHitResult entityHit) {
            Entity target = entityHit.getEntity();
            blockType = null;
            attachedEntities.clear();
            glowingEntityType = target.getType();
        }
        if (hit instanceof BlockHitResult blockHit) {
            glowingEntityType = null;
            attachedEntities.clear();
            blockType = client.world.getBlockState(blockHit.getBlockPos()).getBlock();
        }
    }

    public static boolean shouldGlow(Entity entity) {
        return glowingEntityType != null && entity.getType() == glowingEntityType;
    }

    public static boolean shouldGlow(Block block) {
        if (blockType != null) {
            return block.getTranslationKey().equals(blockType.getTranslationKey());
        }
        return false;
    }

    public static void onAddParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfoReturnable<Particle> cir) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.world != null) {
            var particlePos = new BlockPos((int) x, (int) y, (int) z);
            var particleType = parameters.getType();
            var particle = Registries.PARTICLE_TYPE.getId(particleType);

            if (particle != null && (particle.equals(Identifier.ofVanilla("electric_spark")) || particle.equals(Identifier.ofVanilla("scrape")))) {
                var xDistance = x - Math.floor(x);
                var yDistance = y - Math.floor(y);
                var zDistance = z - Math.floor(z);
                var sparkSurfaceBlock = Math.abs(xDistance - PROXIMITY_THRESHOLD) < TOLERANCE ? particlePos.west()
                        : Math.abs(xDistance - (1.0f -PROXIMITY_THRESHOLD)) < TOLERANCE ? particlePos.east()
                        : Math.abs(yDistance - PROXIMITY_THRESHOLD) < TOLERANCE ? particlePos.down()
                        : Math.abs(yDistance - (1.0f -PROXIMITY_THRESHOLD)) < TOLERANCE ? particlePos.up()
                        : Math.abs(zDistance - PROXIMITY_THRESHOLD) < TOLERANCE ? particlePos.north()
                        : Math.abs(zDistance - (1.0f -PROXIMITY_THRESHOLD)) < TOLERANCE ? particlePos.south()
                        : null;

                //sparkSurfaceBlock = particlePos;
                if (sparkSurfaceBlock != null) {
                    if (x < 0) {
                        sparkSurfaceBlock = sparkSurfaceBlock.west();
                    }
                    if (y < 0) {
                        sparkSurfaceBlock = sparkSurfaceBlock.down();
                    }
                    if (z < 0) {
                        sparkSurfaceBlock = sparkSurfaceBlock.north();
                    }

                    BlockState neighborBlockState = client.world.getBlockState(sparkSurfaceBlock);
                    if (Glowing.shouldGlow(neighborBlockState.getBlock())) {
                        if (parameters instanceof SimpleParticleType) {
                            var current = attachedEntities.get(sparkSurfaceBlock);
                            if (current == null) {
                                var entity = Registries.ENTITY_TYPE.get(Identifier.of("incrementalqol", "highlight_entity")).create(client.world, SpawnReason.TRIGGERED);
                                if (entity instanceof HighlightEntity highlightEntity) {
                                    entity.setSneaking(!particle.equals(Identifier.ofVanilla("electric_spark")));
                                    // Set the position of the entity to the target block position
                                    highlightEntity.refreshPositionAndAngles(sparkSurfaceBlock.getX(), sparkSurfaceBlock.getY(), sparkSurfaceBlock.getZ(), highlightEntity.getYaw(), highlightEntity.getPitch());
                                    client.world.addEntity(highlightEntity);
                                    if (client.world.getEntityById(highlightEntity.getId()) != null) {
                                        attachedEntities.put(sparkSurfaceBlock, highlightEntity);
                                    }
                                }
                            } else {
                                current.setSneaking(!particle.equals(Identifier.ofVanilla("electric_spark")));
                            }
                        }
                    }
                }
            }
        }
    }

    public static void onParticle(ParticleS2CPacket packet, CallbackInfo ci) {
        onAddParticle(packet.getParameters(), packet.getX(), packet.getY(), packet.getZ(), 0, 0, 0, null);
    }

    public static class HighlightEntity extends Entity {
        public HighlightEntity(EntityType<? extends Entity> type, World world) {
            super(type, world);
        }

        @Override
        protected void initDataTracker(DataTracker.Builder builder) {

        }

        @Override
        public void tick() {
            super.tick();
            var world = MinecraftClient.getInstance().world;
            if (world != null) {
                if (this.getEntityWorld() != world || !Glowing.shouldGlow(world.getBlockState(this.getBlockPos()).getBlock()) || attachedEntities.getOrDefault(this.getBlockPos(), null) != this) {
                    world.removeEntity(this.getId(), RemovalReason.DISCARDED);
                    attachedEntities.remove(this.getBlockPos());
                }
            }
        }

        @Override
        public boolean damage(ServerWorld world, DamageSource source, float amount) {
            return false;
        }

        @Override
        protected void readCustomDataFromNbt(NbtCompound nbt) {

        }

        @Override
        protected void writeCustomDataToNbt(NbtCompound nbt) {

        }

        @Override
        public EntityDimensions getDimensions(EntityPose pose) {
            return EntityDimensions.fixed(1.0F, 1.0F); // 1x1 size for a block
        }
    }

    public static class HighlightEntityRenderer extends EntityRenderer<HighlightEntity, HighlightEntityRenderer.HighlightEntityRenderState> {

        private final CubeEntityModel model;

        public HighlightEntityRenderer(EntityRendererFactory.Context context) {
            super(context);
            this.model = new CubeEntityModel();
        }

        @Override
        public HighlightEntityRenderState createRenderState() {
            // This method is used to configure the entity renderer's state
            return new HighlightEntityRenderState();
        }

        public static class HighlightEntityRenderState extends EntityRenderState {
            public HighlightEntityRenderState() {
            }
        }

        public static class CubeEntityModel extends EntityModel<HighlightEntityRenderState> {

            public CubeEntityModel() {
                super(getTexturedModelData().createModel());
            }

            public static TexturedModelData getTexturedModelData() {
                ModelData modelData = new ModelData();
                ModelPartData root = modelData.getRoot();

                // Make sure the cube is large enough
                root.addChild("cube",
                        ModelPartBuilder.create().cuboid(-0.0F, -0.0F, -0.0F, 16, 16, 16),
                        ModelTransform.NONE
                );

                return TexturedModelData.of(modelData, 16, 16);
            }
        }

        @Override
        public boolean shouldRender(HighlightEntity entity, Frustum frustum, double x, double y, double z) {
            return true;
        }

        @Override
        public void render(HighlightEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {

            matrices.push();
            VertexConsumer glowingConsumers = vertexConsumers.getBuffer(RenderLayer.getOutline(Identifier.ofVanilla("textures/block/glass.png")));

            var color = state.sneaking ? 0xFF00FF00 : 0xFFFFFFFF;
            this.model.render(matrices, glowingConsumers, light, OverlayTexture.DEFAULT_UV, color);
            matrices.pop();
        }
    }
}

