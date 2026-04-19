package com.hfstudio.patpat.common.network.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class PatToServerC2SPacket implements IMessage {

    private int targetEntityId;

    public PatToServerC2SPacket() {}

    public PatToServerC2SPacket(int targetEntityId) {
        this.targetEntityId = targetEntityId;
    }

    public int getTargetEntityId() {
        return targetEntityId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        targetEntityId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(targetEntityId);
    }
}
