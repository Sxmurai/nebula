package wtf.nebula.asm.transform.impl.client

import jdk.internal.org.objectweb.asm.Type
import org.objectweb.asm.Opcodes.ALOAD
import org.objectweb.asm.Opcodes.INVOKESTATIC
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.VarInsnNode
import wtf.nebula.asm.hooks.MinecraftHook
import wtf.nebula.asm.transform.api.ClassInjection
import wtf.nebula.asm.transform.api.ClassTransformer
import wtf.nebula.asm.transform.api.Injection
import wtf.nebula.util.animation.Delta

@ClassInjection("net.minecraft.client.Minecraft")
class MinecraftTransformer : ClassTransformer() {
    @Injection(name = "runTick")
    fun runTick(node: MethodNode) {
        val instructions = InsnList()

        // invoke MinecraftHook#runTick()V
        instructions.add(MethodInsnNode(
            INVOKESTATIC,
            Type.getInternalName(MinecraftHook::class.java),
            "runTick",
            "()V",
            false
        ))

        // add instructions to the method node
        node.instructions.insert(instructions)
    }

    @Injection(name = "runGameLoop")
    fun runGameLoop(node: MethodNode) {
        val instructions = InsnList()

        // invoke MinecraftHook#runTick()V
        instructions.add(MethodInsnNode(
            INVOKESTATIC,
            Type.getInternalName(Delta::class.java),
            "updateDelta",
            "()V",
            false
        ))

        // add instructions to the method node
        node.instructions.insert(instructions)
    }

    @Injection(name = "displayGuiScreen", descriptor = "(Lnet/minecraft/client/gui/GuiScreen;)V")
    fun displayGuiScreen(node: MethodNode) {
        val instructions = InsnList()

        instructions.add(VarInsnNode(ALOAD, 1))
        instructions.add(invokeStatic(MinecraftHook::class.java, "guiOpened", "(Lnet/minecraft/client/gui/GuiScreen;)V"))

        node.instructions.insert(instructions)
    }
}