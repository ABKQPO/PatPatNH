package com.hfstudio.patpat.common.network.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class PatS2CPacket implements IMessage {

    private int targetEntityId;
    private int sourceEntityId;

    public PatS2CPacket() {}

    public PatS2CPacket(int targetEntityId, int sourceEntityId) {
        this.targetEntityId = targetEntityId;
        this.sourceEntityId = sourceEntityId;
    }

    public int getTargetEntityId() {
        return targetEntityId;
    }

    public int getSourceEntityId() {
        return sourceEntityId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        targetEntityId = buf.readInt();
        sourceEntityId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(targetEntityId);
        buf.writeInt(sourceEntityId);
    }
}
