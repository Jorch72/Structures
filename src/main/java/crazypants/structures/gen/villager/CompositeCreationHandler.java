package crazypants.structures.gen.villager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.registry.VillagerRegistry.IVillageCreationHandler;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;

public class CompositeCreationHandler implements IVillageCreationHandler {

  private List<CreationHandler> handlers = new ArrayList<CreationHandler>();

  //TODO: This is dodgy as it assumes structure creation is single threaded and non-interleaved
  private int currentStartPieceId = -1;
  private List<WeightedCreationHandler> pieceWeights;

  @Override
  public PieceWeight getVillagePieceWeight(Random random, int i) {

    int maxToSpawn = 0;
    int totalWeight = 0;
    for (CreationHandler handler : handlers) {
      PieceWeight pw = handler.getVillagePieceWeight(random, i);
      maxToSpawn += pw.villagePiecesLimit;
      totalWeight += pw.villagePieceWeight;
    }
    return new PieceWeight(VillageHouse.class, totalWeight, maxToSpawn);
  }

  public void addCreationHandler(CreationHandler handler) {
    handlers.add(handler);
  }

  @Override
  public Class<?> getComponentClass() {
    return VillageHouse.class;
  }


  @SuppressWarnings("rawtypes")
  @Override
  public Object buildComponent(PieceWeight villagePiece, Start startPiece, List pieces, Random random, int x, int y, int z, int coordBaseMode, int p5) {

    int startId = System.identityHashCode(startPiece);
    if(startId != currentStartPieceId) {
//      System.out.println("CompositeCreationHandler.buildComponent: New village");
      currentStartPieceId = startId;
      pieceWeights = getWeightedSpawnList(random, 0);
    }

    int totalWeight = getTotalWieght(pieceWeights);

    if(totalWeight <= 0) {
      return null;
    }

    for(int i=0; i < 5; ++i) {
          
      int randomWeight = random.nextInt(totalWeight);
      Iterator<WeightedCreationHandler> iterator = pieceWeights.iterator();

      while (iterator.hasNext()) {
        WeightedCreationHandler el = iterator.next();
        PieceWeight pieceweight = el.weight;
        randomWeight -= pieceweight.villagePieceWeight;

        if(randomWeight < 0) {
          int notUsed = 0;
          if(!pieceweight.canSpawnMoreVillagePiecesOfType(notUsed)
              || pieceweight == startPiece.structVillagePieceWeight && pieceWeights.size() > 1) {
            break;
          }
          StructureVillagePieces.Village village = (Village) el.handler.buildComponent(pieceweight, startPiece, pieces, random, x, y, z, coordBaseMode, p5);

          if(village != null) {
            ++pieceweight.villagePiecesSpawned;
            startPiece.structVillagePieceWeight = pieceweight;
            if(!pieceweight.canSpawnMoreVillagePieces()) {
              pieceWeights.remove(el);
            }
            return village;
          }
        }
      }
    }
    return null;
  }

  private int getTotalWieght(List<WeightedCreationHandler> handlers) {
    boolean result = false;
    int totalWeight = 0;
    StructureVillagePieces.PieceWeight pieceweight;

    for (Iterator<WeightedCreationHandler> iterator = handlers.iterator(); iterator.hasNext(); totalWeight += pieceweight.villagePieceWeight) {
      pieceweight = iterator.next().weight;

      if(pieceweight.villagePiecesLimit > 0 && pieceweight.villagePiecesSpawned < pieceweight.villagePiecesLimit) {
        result = true;
      }
    }

    return result ? totalWeight : -1;
  }

  private List<WeightedCreationHandler> getWeightedSpawnList(Random random, int i) {
    List<WeightedCreationHandler> res = new ArrayList<WeightedCreationHandler>();
    for (CreationHandler ch : handlers) {
      res.add(new WeightedCreationHandler(ch.getVillagePieceWeight(random, i), ch));
    }
    return res;
  }

  private static class WeightedCreationHandler {

    final PieceWeight weight;
    final CreationHandler handler;

    public WeightedCreationHandler(PieceWeight weight, CreationHandler handler) {
      this.weight = weight;
      this.handler = handler;
    }

  }

}
