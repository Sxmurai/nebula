package wtf.nebula.client.event.translate

import me.bush.eventbuskotlin.EventListener
import me.bush.eventbuskotlin.listener
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.client.CPacketEntityAction
import net.minecraft.network.play.server.SPacketEntityMetadata
import net.minecraft.network.play.server.SPacketEntityStatus
import net.minecraft.network.play.server.SPacketPlayerPosLook
import net.minecraftforge.client.event.InputUpdateEvent
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent
import net.minecraftforge.fml.common.gameevent.InputEvent.MouseInputEvent
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import wtf.nebula.asm.hooks.EntityPlayerSPHook
import wtf.nebula.client.Nebula
import wtf.nebula.client.event.input.MiddleClickEvent
import wtf.nebula.client.event.input.MovementInputEvent
import wtf.nebula.client.event.packet.PacketReceiveEvent
import wtf.nebula.client.event.packet.PacketSendEvent
import wtf.nebula.client.event.player.DeathEvent
import wtf.nebula.client.event.player.SetbackEvent
import wtf.nebula.client.event.player.TotemPopEvent
import wtf.nebula.client.event.world.PushOutOfBlocksEvent
import wtf.nebula.util.Globals

class ForgeEventListener : Globals {
    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onKeyInput(event: KeyInputEvent) {
        Nebula.BUS.post(
            wtf.nebula.client.event.input.KeyInputEvent(
                Keyboard.getEventKey(), Keyboard.getEventKeyState()
            )
        )
    }

    @SubscribeEvent
    fun onMouseInput(event: MouseInputEvent) {
        val mouseButtonDown = Mouse.getEventButton()
        if (mouseButtonDown == -1) {
            return
        }

        if (mouseButtonDown == 2 && !Mouse.getEventButtonState()) {
            Nebula.BUS.post(MiddleClickEvent())
        }
    }

    @SubscribeEvent
    fun onMovementUpdate(event: InputUpdateEvent) {
        Nebula.BUS.post(MovementInputEvent(event.movementInput))
    }

    @EventListener
    private val packetSendListener = listener<PacketSendEvent> {

        if (nullCheck()) {
            return@listener
        }

        if (it.packet is CPacketEntityAction) {
            val packet = it.packet

            if (packet.action == CPacketEntityAction.Action.START_SPRINTING
                || packet.action == CPacketEntityAction.Action.STOP_SPRINTING) {

                EntityPlayerSPHook.playerSprintState = packet.action == CPacketEntityAction.Action.START_SPRINTING
            }

            if (packet.action == CPacketEntityAction.Action.START_SNEAKING
                || packet.action == CPacketEntityAction.Action.STOP_SNEAKING) {

                EntityPlayerSPHook.playerSneakState = packet.action == CPacketEntityAction.Action.START_SNEAKING
            }
        }

//        else if (it.packet is CPacketPlayer) {
//            val packet = it.packet
//
//            EntityPlayerSPHook.lastGround = packet.isOnGround;
//
//            val x = packet.getX(0.0)
//            val y = packet.getY(0.0)
//            val z = packet.getZ(0.0)
//
//            val yaw = packet.getYaw(0.0f)
//            val pitch = packet.getPitch(0.0f)
//
//            if (isFinite(yaw) && isFinite(pitch)) {
//                EntityPlayerSPHook.prevYaw = yaw
//                EntityPlayerSPHook.prevPitch = pitch;
//            }
//
//            if (isFinite(x)) {
//                EntityPlayerSPHook.prevX = x;
//            }
//
//            if (isFinite(y)) {
//                EntityPlayerSPHook.prevY = y;
//            }
//
//            if (isFinite(z)) {
//                EntityPlayerSPHook.prevZ = z;
//            }
//        }
    }

    @EventListener
    private val packetReceiveListener = listener<PacketReceiveEvent> {
        if (nullCheck()) {
            return@listener
        }

        if (it.packet is SPacketPlayerPosLook) {
            Nebula.BUS.post(SetbackEvent())
        }

        else if (it.packet is SPacketEntityStatus) {
            val packet = it.packet
            val entity = packet.getEntity(mc.world)

            if (packet.opCode == 35.toByte() && entity is EntityPlayer) {
                Nebula.BUS.post(TotemPopEvent(entity))
            }
        }

        else if (it.packet is SPacketEntityMetadata) {
            val packet = it.packet

            val entity = mc.world.getEntityByID(packet.entityId) ?: return@listener
            if (entity !is EntityPlayer) {
                return@listener
            }

            if (entity.isDead || entity.health <= 0.0f) {
                Nebula.BUS.post(DeathEvent(entity))
            }
        }
    }
}