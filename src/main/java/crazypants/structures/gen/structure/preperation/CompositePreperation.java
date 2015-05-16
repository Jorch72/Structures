package crazypants.structures.gen.structure.preperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import crazypants.structures.gen.WorldStructures;
import crazypants.structures.gen.structure.Structure;

public class CompositePreperation implements ISitePreperation {

  private final List<ISitePreperation> preps = new ArrayList<ISitePreperation>();

  public CompositePreperation() {
  }

  public void add(ISitePreperation prep) {
    preps.add(prep);
  }

  @Override
  public boolean prepareLocation(Structure structure, WorldStructures structures, World world, Random random, int chunkX, int chunkZ) {
    for (ISitePreperation rule : preps) {
      if(!rule.prepareLocation(structure, structures, world, random, chunkX, chunkZ)) {
        return false;
      }
    }
    return true;
  }

}
