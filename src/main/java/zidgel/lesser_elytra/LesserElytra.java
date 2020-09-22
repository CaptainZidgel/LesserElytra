package zidgel.lesser_elytra;

import net.minecraft.block.DispenserBlock;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.enchantment.*;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.item.*;
import zidgel.lesser_elytra.init.ModItems;

import java.util.Map;

@Mod(LesserElytra.MODID) //mod entry point
public class LesserElytra {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "lesser_elytra";

    public LesserElytra() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModEventSubscriber::onClientSetup);
    }

    public static class LesserElytraItem extends Item {
        public LesserElytraItem(Item.Properties builder) {
            super(builder);
            DispenserBlock.registerDispenseBehavior(this, ArmorItem.DISPENSER_BEHAVIOR); //when popped out of a dispenser, use the same behavior as armor
            builder.setNoRepair();
        }

        /* standard elytra will only be "usable" if it has at least 1 health, meaning it can never go below 1 and be obliterated.
        It will instead take on the quality of "broken". This statement just returns true always. */
        public static boolean isUsable(ItemStack stack) {
            //return stack.getDamage() < stack.getMaxDamage() - 1;
            return true;
        }

        @Override
        public EquipmentSlotType getEquipmentSlot(ItemStack stack) {
            return EquipmentSlotType.CHEST;
        }

        @Override
        public boolean canElytraFly(ItemStack stack, net.minecraft.entity.LivingEntity entity) {
            return LesserElytraItem.isUsable(stack);
        }

        @Override
        public boolean elytraFlightTick(ItemStack stack, net.minecraft.entity.LivingEntity entity, int flightTicks) {
            if (!entity.world.isRemote && (flightTicks + 1) % 20 == 0) {
                stack.damageItem(1, entity, e -> e.sendBreakAnimation(net.minecraft.inventory.EquipmentSlotType.CHEST));
            }
            return true;
        }

        /* Unncessary - elytras cant be enchanted at tables
        @Override
        public boolean canApplyAtEnchantingTable(ItemStack stack, net.minecraft.enchantment.Enchantment enchantment) {
            if (enchantment == Enchantments.UNBREAKING) {
                return false;
            } else {
                return true;
            }
        }
        */

        //This funcy boy checks books on anvils for correct enchantments.
        @Override
        public boolean isBookEnchantable(ItemStack stack, ItemStack book)
        {
            //Integer y = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, book); //this SHOULD work but only returns 0!!! What tah cringe!!

            Map e = EnchantmentHelper.getEnchantments(book);
            Integer u = (Integer) e.get(Enchantments.UNBREAKING);
            Integer m = (Integer) e.get(Enchantments.MENDING);
            return (u != null && m == null);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class CustomElytraLayer extends ElytraLayer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>>
    {
        private static final ResourceLocation TEXTURE_ELYTRA = new ResourceLocation(MODID, "textures/entity/elytra.png");

        public CustomElytraLayer(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> rendererIn)
        {
            super(rendererIn);
        }

        @Override
        public boolean shouldRender(ItemStack stack, AbstractClientPlayerEntity entity)
        {
            return stack.getItem() == ModItems.LESSER_ELYTRA;
        }

        @Override
        public ResourceLocation getElytraTexture(ItemStack stack, AbstractClientPlayerEntity entity)
        {
            return TEXTURE_ELYTRA;
        }
    }
}
