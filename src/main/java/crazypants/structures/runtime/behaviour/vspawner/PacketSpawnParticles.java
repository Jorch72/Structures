package crazypants.structures.runtime.behaviour.vspawner;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.structures.EnderStructures;
import crazypants.structures.api.util.Point3i;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;

public class PacketSpawnParticles implements IMessage, IMessageHandler<PacketSpawnParticles, IMessage> {

  private int x;
  private int y;
  private int z;

  public PacketSpawnParticles() {

  }

  public PacketSpawnParticles(Point3i loc) {
    x = loc.x;
    y = loc.y;
    z = loc.z;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(x);
    buf.writeInt(y);
    buf.writeInt(z);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    x = buf.readInt();
    y = buf.readInt();
    z = buf.readInt();

  }

  @Override
  public IMessage onMessage(PacketSpawnParticles message, MessageContext ctx) {
    World world = EnderStructures.proxy.getClientWorld();
    
    double d0 = message.x + world.rand.nextFloat();
    double d1 = message.y + world.rand.nextFloat();
    double d2 = message.z + world.rand.nextFloat();
    world.spawnParticle("smoke", d0, d1, d2, 0.0D, 0.0D, 0.0D);
    world.spawnParticle("flame", d0, d1, d2, 0.0D, 0.0D, 0.0D);
    
    return null;
  }

}
