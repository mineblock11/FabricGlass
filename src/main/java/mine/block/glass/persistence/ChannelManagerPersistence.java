package mine.block.glass.persistence;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @deprecated DO NOT USE IN CLIENT SITUATIONS WITH CLIENTWORLD, ALWAYS USE SERVERWORLD
 */
public class ChannelManagerPersistence extends PersistentState implements Collection<Channel> {

    public static final HashMap<World, ChannelManagerPersistence> MANAGERS = new HashMap<>();
    private static final Logger LOGGER = LogManager.getLogger("ChannelManagerPersistence");

    private final ArrayList<Channel> CHANNELS = new ArrayList<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {

        NbtList channels = new NbtList();

        for (Channel channel : CHANNELS) {
            NbtCompound item = new NbtCompound();
            item.putString("name", channel.name());
            if(channel.linkedBlock() != null)
                item.putLong("linked_pos", channel.linkedBlock().asLong());

        }

        nbt.put("channels", channels);

        return nbt;
    }

    private static PersistentState gather(NbtCompound compound) {

        ChannelManagerPersistence persistence = new ChannelManagerPersistence();

        NbtList channels = compound.getList("channels", NbtElement.COMPOUND_TYPE);

        for (int i = 0; i < channels.size(); i++) {
            NbtCompound channel = channels.getCompound(i);
            @Nullable BlockPos bpos = (channel.contains("linked_pos")) ? null : BlockPos.fromLong(channel.getLong("linked_pos"));
            Channel channel1 = new Channel(channel.getString("name"), bpos);
            persistence.CHANNELS.add(channel1);
        }

        return persistence;
    }

    public static void init() {
        ServerWorldEvents.LOAD.register((server, world) -> {
            PersistentState state = world.getPersistentStateManager().getOrCreate(ChannelManagerPersistence::gather, ChannelManagerPersistence::new, "glass_channels");
            MANAGERS.put(world, (ChannelManagerPersistence) state);
            LOGGER.info("Loaded ChannelManagerPersistence for: " + world.getDimensionKey().getValue() + " at " + world);
        });
    }

    @Override
    public int size() {
        return CHANNELS.size();
    }

    @Override
    public boolean isEmpty() {
        return CHANNELS.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return CHANNELS.contains(o);
    }

    @NotNull
    @Override
    public Iterator<Channel> iterator() {
        return CHANNELS.iterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return CHANNELS.toArray();
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] ts) {
        return CHANNELS.toArray(ts);
    }

    @Override
    public boolean add(Channel channel) {
        boolean e = CHANNELS.add(channel);
        this.markDirty();
        return e;
    }

    @Override
    public boolean remove(Object o) {
        boolean e = CHANNELS.remove(o);
        this.markDirty();
        return e;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> collection) {
        return CHANNELS.containsAll(collection);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends Channel> collection) {
        boolean e = CHANNELS.addAll(collection);
        this.markDirty();
        return e;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> collection) {
        boolean e = CHANNELS.removeAll(collection);
        this.markDirty();
        return e;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> collection) {
        boolean e = CHANNELS.retainAll(collection);
        this.markDirty();
        return e;
    }

    @Override
    public <T> T[] toArray(IntFunction<T[]> generator) {
        return CHANNELS.toArray(generator);
    }

    @Override
    public boolean removeIf(Predicate<? super Channel> filter) {
        boolean e = CHANNELS.removeIf(filter);
        this.markDirty();
        return e;
    }

    @Override
    public Spliterator<Channel> spliterator() {
        return CHANNELS.spliterator();
    }

    @Override
    public Stream<Channel> stream() {
        return CHANNELS.stream();
    }

    @Override
    public Stream<Channel> parallelStream() {
        return CHANNELS.parallelStream();
    }

    /**
     * @Deprecated Do not use.
     */
    @Override
    public void clear() {
        CHANNELS.clear();
        this.markDirty();
    }
}
