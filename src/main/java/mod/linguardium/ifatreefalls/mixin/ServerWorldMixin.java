package mod.linguardium.ifatreefalls.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameRules;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@Environment(EnvType.SERVER)
@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {

    protected ServerWorldMixin(MutableWorldProperties mutableWorldProperties, RegistryKey<World> registryKey, RegistryKey<DimensionType> registryKey2, DimensionType dimensionType, Supplier<Profiler> profiler, boolean bl, boolean bl2, long l) {
        super(mutableWorldProperties, registryKey, registryKey2, dimensionType, profiler, bl, bl2, l);
    }

    @Shadow public abstract MinecraftServer getServer();

    @Redirect(method="tick",at=@At(value="INVOKE",target="Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
    private boolean doWeatherPlayerCheck(GameRules gameRules, GameRules.Key<GameRules.BooleanRule> rule) {
        return this.getServer().getPlayerManager().getPlayerList().size() > 0 && this.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE);
    }
    @Inject(method="tickTime",at=@At("HEAD"), cancellable = true)
    private void tickTimeIfPlayers(CallbackInfo info) {
        if (this.getServer().getPlayerManager().getPlayerList().size() < 1) {
            info.cancel();
        }
    }
}
