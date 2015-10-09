package crazypants.structures;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.gen.IWorldStructures;
import crazypants.structures.gen.WorldStructures;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;

public class StructureRuntime {

  public static StructureRuntime create() {
    StructureRuntime sm = new StructureRuntime();
    sm.init();
    return sm;
  }
  
  private final Map<Integer, WorldStructures> worldManagers = new HashMap<Integer, WorldStructures>();
 
  private final Set<IStructure> loadedStructures = new HashSet<IStructure>();
  
  
  public StructureRuntime() {    
  }
  
  private void init() {
    MinecraftForge.EVENT_BUS.register(new EventListener());   
  }  

  public IWorldStructures getStructuresForWorld(World world) {
    return getStructuresForWorldImpl(world);
  }
  
  private WorldStructures getStructuresForWorldImpl(World world) {
    if(world == null) {
      return null;
    }
    WorldStructures res =  worldManagers.get(world.provider.dimensionId);
    if(res == null) {
      WorldStructures s = new WorldStructures(world);
      s.load();
      worldManagers.put(world.provider.dimensionId, s);
      res = s;      
    }
    return res;
  }
  
  public void serverStopped(FMLServerStoppedEvent event) {
    worldManagers.clear();  
    loadedStructures.clear();
  }
  
  public class EventListener {
    
    private EventListener() {      
    }
    
    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load evt) {
      Collection<IStructure> structs = getStructuresForWorld(evt.world).getStructuresWithOriginInChunk(evt.getChunk().getChunkCoordIntPair());            
      if(!structs.isEmpty()) {
        loadedStructures.addAll(structs);
        
//        for(Structure struct : structs) {
//          struct.
//        }
        
//        getStructuresForWorld(evt.world);
//        int total = worldManagers.get(evt.world.provider.dimensionId).getStructureCount();        
//        System.out.println("StructureRuntime.EventListener.onChunkLoad: " + StringUtils.join(structs, " ; ") + " total structures: " + total + " loaded: " + loadedStructures.size());
      }      
    }
    
    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload evt) {
      Collection<IStructure> structs = getStructuresForWorld(evt.world).getStructuresWithOriginInChunk(evt.getChunk().getChunkCoordIntPair());      
      
      if(!structs.isEmpty()) {
        loadedStructures.removeAll(structs);
//        System.out.println("StructureRuntime.EventListener.onChunkUnload: " + StringUtils.join(structs, " ; "));
      }
    }
    
    @SubscribeEvent
    public void eventWorldSave(WorldEvent.Save evt) {
      WorldStructures wm = getStructuresForWorldImpl(evt.world);
      if(wm != null) {
        wm.save();
      }
    }
    
  }
  
}
