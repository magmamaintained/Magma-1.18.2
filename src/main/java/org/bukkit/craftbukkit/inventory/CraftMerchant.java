package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import net.minecraft.world.entity.player.net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffers;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantOffer;

public class CraftMerchant implements Merchant {

    protected final net.minecraft.world.item.trading.Merchant merchant;

    public CraftMerchant(net.minecraft.world.item.trading.Merchant merchant) {
        this.merchant = merchant;
    }

    public net.minecraft.world.item.trading.Merchant getMerchant() {
        return merchant;
    }

    @Override
    public List<MerchantOffer> getRecipes() {
        return Collections.unmodifiableList(Lists.transform(merchant.getOffers(), new Function<net.minecraft.world.item.trading.MerchantOffer, MerchantOffer>() {
            @Override
            public MerchantOffer apply(net.minecraft.world.item.trading.MerchantOffer recipe) {
                return recipe.asBukkit();
            }
        }));
    }

    @Override
    public void setRecipes(List<MerchantOffer> recipes) {
        MerchantOffers recipesList = merchant.getOffers();
        recipesList.clear();
        for (MerchantOffer recipe : recipes) {
            recipesList.add(CraftMerchantRecipe.fromBukkit(recipe).toMinecraft());
        }
    }

    @Override
    public MerchantOffer getRecipe(int i) {
        return merchant.getOffers().get(i).asBukkit();
    }

    @Override
    public void setRecipe(int i, MerchantOffer merchantRecipe) {
        merchant.getOffers().set(i, CraftMerchantRecipe.fromBukkit(merchantRecipe).toMinecraft());
    }

    @Override
    public int getRecipeCount() {
        return merchant.getOffers().size();
    }

    @Override
    public boolean isTrading() {
        return getTrader() != null;
    }

    @Override
    public HumanEntity getTrader() {
        net.minecraft.world.entity.player.Player eh = merchant.getTradingPlayer();
        return eh == null ? null : eh.getBukkitEntity();
    }

    @Override
    public int hashCode() {
        return merchant.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof CraftMerchant && ((CraftMerchant) obj).merchant.equals(this.merchant);
    }
}
