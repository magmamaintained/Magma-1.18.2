package org.magmafoundation.magma.helpers;

public final class AnvilHelper {

    private int maxRepairCost = 40;

    public int getMaxRepairCost() {
        return maxRepairCost;
    }

    public void setMaxRepairCost(int maxRepairCost) {
        this.maxRepairCost = maxRepairCost;
    }

    public boolean isDefaultValue() {
        return maxRepairCost == 40;
    }
}
