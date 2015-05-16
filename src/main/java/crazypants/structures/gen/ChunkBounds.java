package crazypants.structures.gen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.world.ChunkCoordIntPair;


public class ChunkBounds {

  private final int minChunkX;
  private final int maxChunkX;
  private final int minChunkZ;
  private final int maxChunkZ;

  public ChunkBounds(int minChunkX, int minChunkZ, int maxChunkX, int maxChunkZ) {
    this.minChunkX = minChunkX;
    this.maxChunkX = maxChunkX;
    this.minChunkZ = minChunkZ;
    this.maxChunkZ = maxChunkZ;
  }

  public ChunkBounds(int chunkX, int chunkZ) {
    this(chunkX, chunkZ, chunkX, chunkZ);
  }

  public boolean isChunkInBounds(int chunkX, int chunkZ) {
    return chunkX >= minChunkX && chunkX <= maxChunkX && chunkZ >= minChunkZ && chunkZ <= maxChunkZ;
  }

  public boolean isBlockInBounds(int x, int z) {
    return isChunkInBounds(x >> 4, z >> 4);
  }

  public Collection<ChunkCoordIntPair> getChunks() {
    List<ChunkCoordIntPair> res = new ArrayList<ChunkCoordIntPair>();
    for (int x = minChunkX; x <= maxChunkX; x++) {
      for (int z = minChunkZ; z <= maxChunkZ; z++) {
        res.add(new ChunkCoordIntPair(x, z));
      }
    }
    return res;
  }

  public int getNumChunks() {
    int res = 0;
    for (int x = minChunkX; x <= maxChunkX; x++) {
      for (int z = minChunkZ; z <= maxChunkZ; z++) {
        res++;
      }
    }
    return res;
  }

}
