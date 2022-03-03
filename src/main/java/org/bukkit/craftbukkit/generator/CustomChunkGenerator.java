package org.bukkit.craftbukkit.generator;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.server.level.RegionLimitedWorldAccess;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.level.BlockColumn;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSettingsMobs;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.block.ITileEntity;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkSection;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.WorldGenStage;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.CraftHeightMap;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.ChunkGenerator.BiomeGrid;
import org.bukkit.generator.ChunkGenerator.ChunkData;

public class CustomChunkGenerator extends InternalChunkGenerator {

    private final net.minecraft.world.level.chunk.ChunkGenerator delegate;
    private final ChunkGenerator generator;
    private final WorldServer world;
    private final Random random = new Random();
    private boolean newApi;
    private boolean implementBaseHeight = true;

    @Deprecated
    private class CustomBiomeGrid implements BiomeGrid {

        private final IChunkAccess biome;

        public CustomBiomeGrid(IChunkAccess biome) {
            this.biome = biome;
        }

        @Override
        public Biome getBiome(int x, int z) {
            return getBiome(x, 0, z);
        }

        @Override
        public void setBiome(int x, int z, Biome bio) {
            for (int y = world.getWorld().getMinHeight(); y < world.getWorld().getMaxHeight(); y += 4) {
                setBiome(x, y, z, bio);
            }
        }

        @Override
        public Biome getBiome(int x, int y, int z) {
            return CraftBlock.biomeBaseToBiome(biome.biomeRegistry, biome.getNoiseBiome(x >> 2, y >> 2, z >> 2));
        }

        @Override
        public void setBiome(int x, int y, int z, Biome bio) {
            Preconditions.checkArgument(bio != Biome.CUSTOM, "Cannot set the biome to %s", bio);
            biome.setBiome(x >> 2, y >> 2, z >> 2, CraftBlock.biomeToBiomeBase(biome.biomeRegistry, bio));
        }
    }

    public CustomChunkGenerator(WorldServer world, net.minecraft.world.level.chunk.ChunkGenerator delegate, ChunkGenerator generator) {
        super(delegate.structureSets, delegate.structureOverrides, delegate.getBiomeSource());

        this.world = world;
        this.delegate = delegate;
        this.generator = generator;
    }

    private static SeededRandom getSeededRandom() {
        return new SeededRandom(new LegacyRandomSource(0));
    }

    @Override
    public net.minecraft.world.level.chunk.ChunkGenerator withSeed(long i) {
        return new CustomChunkGenerator(this.world, delegate.withSeed(i), this.generator);
    }

    @Override
    public WorldChunkManager getBiomeSource() {
        return delegate.getBiomeSource();
    }

    @Override
    public int getMinY() {
        return delegate.getMinY();
    }

    @Override
    public int getSeaLevel() {
        return delegate.getSeaLevel();
    }

    @Override
    public void createStructures(IRegistryCustom iregistrycustom, StructureManager structuremanager, IChunkAccess ichunkaccess, DefinedStructureManager definedstructuremanager, long i) {
        if (generator.shouldGenerateStructures()) {
            super.createStructures(iregistrycustom, structuremanager, ichunkaccess, definedstructuremanager, i);
        }
    }

    @Override
    public void buildSurface(RegionLimitedWorldAccess regionlimitedworldaccess, StructureManager structuremanager, IChunkAccess ichunkaccess) {
        if (generator.shouldGenerateSurface()) {
            delegate.buildSurface(regionlimitedworldaccess, structuremanager, ichunkaccess);
        }

        CraftChunkData chunkData = new CraftChunkData(this.world.getWorld(), ichunkaccess);
        SeededRandom random = getSeededRandom();
        int x = ichunkaccess.getPos().x;
        int z = ichunkaccess.getPos().z;
        random.setSeed((long) x * 341873128712L + (long) z * 132897987541L);
        generator.generateSurface(this.world.getWorld(), random, x, z, chunkData);

        if (generator.shouldGenerateBedrock()) {
            random = getSeededRandom();
            random.setSeed((long) x * 341873128712L + (long) z * 132897987541L);
            // delegate.buildBedrock(ichunkaccess, random);
        }

        random = getSeededRandom();
        random.setSeed((long) x * 341873128712L + (long) z * 132897987541L);
        generator.generateBedrock(this.world.getWorld(), random, x, z, chunkData);
        chunkData.breakLink();

        // return if new api is used
        if (newApi) {
            return;
        }

        // old ChunkGenerator logic, for backwards compatibility
        // Call the bukkit ChunkGenerator before structure generation so correct biome information is available.
        this.random.setSeed((long) x * 341873128712L + (long) z * 132897987541L);

        // Get default biome data for chunk
        CustomBiomeGrid biomegrid = new CustomBiomeGrid(ichunkaccess);

        ChunkData data;
        try {
            if (generator.isParallelCapable()) {
                data = generator.generateChunkData(this.world.getWorld(), this.random, x, z, biomegrid);
            } else {
                synchronized (this) {
                    data = generator.generateChunkData(this.world.getWorld(), this.random, x, z, biomegrid);
                }
            }
        } catch (UnsupportedOperationException exception) {
            newApi = true;
            return;
        }

        Preconditions.checkArgument(data instanceof OldCraftChunkData, "Plugins must use createChunkData(World) rather than implementing ChunkData: %s", data);
        OldCraftChunkData craftData = (OldCraftChunkData) data;
        ChunkSection[] sections = craftData.getRawChunkData();

        ChunkSection[] csect = ichunkaccess.getSections();
        int scnt = Math.min(csect.length, sections.length);

        // Loop through returned sections
        for (int sec = 0; sec < scnt; sec++) {
            if (sections[sec] == null) {
                continue;
            }
            ChunkSection section = sections[sec];

            // SPIGOT-6843: Copy biomes over to new section.
            // Not the most performant way, but has a small footprint and developer should move to the new api anyway
            ChunkSection oldSection = csect[sec];
            for (int biomeX = 0; biomeX < 4; biomeX++) {
                for (int biomeY = 0; biomeY < 4; biomeY++) {
                    for (int biomeZ = 0; biomeZ < 4; biomeZ++) {
                        section.setBiome(biomeX, biomeY, biomeZ, oldSection.getNoiseBiome(biomeX, biomeY, biomeZ));
                    }
                }
            }

            csect[sec] = section;
        }

        if (craftData.getTiles() != null) {
            for (BlockPosition pos : craftData.getTiles()) {
                int tx = pos.getX();
                int ty = pos.getY();
                int tz = pos.getZ();
                IBlockData block = craftData.getTypeId(tx, ty, tz);

                if (block.hasBlockEntity()) {
                    TileEntity tile = ((ITileEntity) block.getBlock()).newBlockEntity(new BlockPosition((x << 4) + tx, ty, (z << 4) + tz), block);
                    ichunkaccess.setBlockEntity(tile);
                }
            }
        }

        // Apply captured light blocks
        for (BlockPosition lightPosition : craftData.getLights()) {
            ((ProtoChunk) ichunkaccess).addLight(new BlockPosition((x << 4) + lightPosition.getX(), lightPosition.getY(), (z << 4) + lightPosition.getZ()));
        }
    }

    @Override
    public void applyCarvers(RegionLimitedWorldAccess regionlimitedworldaccess, long seed, BiomeManager biomemanager, StructureManager structuremanager, IChunkAccess ichunkaccess, WorldGenStage.Features worldgenstage_features) {
        if (generator.shouldGenerateCaves()) {
            delegate.applyCarvers(regionlimitedworldaccess, seed, biomemanager, structuremanager, ichunkaccess, worldgenstage_features);
        }

        if (worldgenstage_features == WorldGenStage.Features.LIQUID) { // stage check ensures that the method is only called once
            CraftChunkData chunkData = new CraftChunkData(this.world.getWorld(), ichunkaccess);
            SeededRandom random = getSeededRandom();
            int x = ichunkaccess.getPos().x;
            int z = ichunkaccess.getPos().z;
            random.setDecorationSeed(seed, 0, 0);

            generator.generateCaves(this.world.getWorld(), random, x, z, chunkData);
            chunkData.breakLink();
        }
    }

    @Override
    public CompletableFuture<IChunkAccess> fillFromNoise(Executor executor, Blender blender, StructureManager structuremanager, IChunkAccess ichunkaccess) {
        CompletableFuture<IChunkAccess> future = null;
        if (generator.shouldGenerateNoise()) {
            future = delegate.fillFromNoise(executor, blender, structuremanager, ichunkaccess);
        }

        java.util.function.Function<IChunkAccess, IChunkAccess> function = (ichunkaccess1) -> {
            CraftChunkData chunkData = new CraftChunkData(this.world.getWorld(), ichunkaccess1);
            SeededRandom random = getSeededRandom();
            int x = ichunkaccess1.getPos().x;
            int z = ichunkaccess1.getPos().z;
            random.setSeed((long) x * 341873128712L + (long) z * 132897987541L);

            generator.generateNoise(this.world.getWorld(), random, x, z, chunkData);
            chunkData.breakLink();
            return ichunkaccess1;
        };

        return future == null ? CompletableFuture.supplyAsync(() -> function.apply(ichunkaccess), net.minecraft.SystemUtils.backgroundExecutor()) : future.thenApply(function);
    }

    @Override
    public int getBaseHeight(int i, int j, HeightMap.Type heightmap_type, LevelHeightAccessor levelheightaccessor) {
        if (implementBaseHeight) {
            try {
                SeededRandom random = getSeededRandom();
                int xChunk = i >> 4;
                int zChunk = j >> 4;
                random.setSeed((long) xChunk * 341873128712L + (long) zChunk * 132897987541L);

                return generator.getBaseHeight(this.world.getWorld(), random, i, j, CraftHeightMap.fromNMS(heightmap_type));
            } catch (UnsupportedOperationException exception) {
                implementBaseHeight = false;
            }
        }

        return delegate.getBaseHeight(i, j, heightmap_type, levelheightaccessor);
    }

    @Override
    public WeightedRandomList<BiomeSettingsMobs.c> getMobsAt(Holder<BiomeBase> holder, StructureManager structuremanager, EnumCreatureType enumcreaturetype, BlockPosition blockposition) {
        return delegate.getMobsAt(holder, structuremanager, enumcreaturetype, blockposition);
    }

    @Override
    public void applyBiomeDecoration(GeneratorAccessSeed generatoraccessseed, IChunkAccess ichunkaccess, StructureManager structuremanager) {
        super.applyBiomeDecoration(generatoraccessseed, ichunkaccess, structuremanager, generator.shouldGenerateDecorations());
    }

    @Override
    public void addDebugScreenInfo(List<String> list, BlockPosition blockposition) {
        delegate.addDebugScreenInfo(list, blockposition);
    }

    @Override
    public void spawnOriginalMobs(RegionLimitedWorldAccess regionlimitedworldaccess) {
        if (generator.shouldGenerateMobs()) {
            delegate.spawnOriginalMobs(regionlimitedworldaccess);
        }
    }

    @Override
    public int getSpawnHeight(LevelHeightAccessor levelheightaccessor) {
        return delegate.getSpawnHeight(levelheightaccessor);
    }

    @Override
    public int getGenDepth() {
        return delegate.getGenDepth();
    }

    @Override
    public BlockColumn getBaseColumn(int i, int j, LevelHeightAccessor levelheightaccessor) {
        return delegate.getBaseColumn(i, j, levelheightaccessor);
    }

    @Override
    public Climate.Sampler climateSampler() {
        return delegate.climateSampler();
    }

    @Override
    protected Codec<? extends net.minecraft.world.level.chunk.ChunkGenerator> codec() {
        return Codec.unit(null);
    }
}
