package org.better.visualizer.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.better.visualizer.EnchantmentBadgeUtil;
import org.better.visualizer.Visualizer;

public class ProtectionArmorLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private static final int MAX_PROTECTION_LEVEL = 4;

    private final HumanoidModel<AbstractClientPlayer> innerModel;
    private final HumanoidModel<AbstractClientPlayer> outerModel;

    public ProtectionArmorLayer(
            RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent,
            EntityModelSet modelSet
    ) {
        super(parent);
        this.innerModel = new HumanoidModel<>(modelSet.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));
        this.outerModel = new HumanoidModel<>(modelSet.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR));
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            AbstractClientPlayer player,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;

        renderPiece(poseStack, buffer, packedLight, player, EquipmentSlot.HEAD, level);
        renderPiece(poseStack, buffer, packedLight, player, EquipmentSlot.CHEST, level);
        renderPiece(poseStack, buffer, packedLight, player, EquipmentSlot.LEGS, level);
        renderPiece(poseStack, buffer, packedLight, player, EquipmentSlot.FEET, level);
    }

    private void renderPiece(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            AbstractClientPlayer player,
            EquipmentSlot slot,
            ClientLevel level
    ) {
        ItemStack stack = player.getItemBySlot(slot);
        if (stack.isEmpty()) return;

        String material = getSupportedMaterial(stack);
        if (material == null) return;

        int protection = EnchantmentBadgeUtil.getProtectionArmorLevel(stack, level);
        if (protection <= 0) return;
        protection = Math.min(protection, MAX_PROTECTION_LEVEL);

        HumanoidModel<AbstractClientPlayer> armorModel = usesInnerModel(slot) ? innerModel : outerModel;
        ResourceLocation texture = getTexture(material, slot, protection);

        this.getParentModel().copyPropertiesTo(armorModel);
        setPartVisibility(armorModel, slot);

        VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(
                buffer,
                RenderType.armorCutoutNoCull(texture),
                stack.hasFoil()
        );

        armorModel.renderToBuffer(
                poseStack,
                vertexConsumer,
                packedLight,
                OverlayTexture.NO_OVERLAY
        );
    }

    private static boolean usesInnerModel(EquipmentSlot slot) {
        return slot == EquipmentSlot.LEGS;
    }

    private static String getSupportedMaterial(ItemStack stack) {
        if (stack.is(Items.IRON_HELMET)
                || stack.is(Items.IRON_CHESTPLATE)
                || stack.is(Items.IRON_LEGGINGS)
                || stack.is(Items.IRON_BOOTS)) {
            return "iron";
        }

        if (stack.is(Items.DIAMOND_HELMET)
                || stack.is(Items.DIAMOND_CHESTPLATE)
                || stack.is(Items.DIAMOND_LEGGINGS)
                || stack.is(Items.DIAMOND_BOOTS)) {
            return "diamond";
        }

        return null;
    }

    private static ResourceLocation getTexture(String material, EquipmentSlot slot, int protectionLevel) {
        int layer = (slot == EquipmentSlot.LEGS) ? 2 : 1;
        return ResourceLocation.fromNamespaceAndPath(
                Visualizer.MODID,
                "textures/models/armor/" + material + "_protection_" + protectionLevel + "_layer_" + layer + ".png"
        );
    }

    private static void setPartVisibility(HumanoidModel<?> model, EquipmentSlot slot) {
        model.setAllVisible(false);

        switch (slot) {
            case HEAD -> {
                model.head.visible = true;
                model.hat.visible = true;
            }
            case CHEST -> {
                model.body.visible = true;
                model.rightArm.visible = true;
                model.leftArm.visible = true;
            }
            case LEGS -> {
                model.body.visible = true;
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
            }
            case FEET -> {
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
            }
            default -> {
            }
        }
    }
}