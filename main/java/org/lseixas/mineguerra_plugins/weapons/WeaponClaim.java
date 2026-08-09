package org.lseixas.mineguerra_plugins.weapons;

/**
 * Estado de posse de uma arma lendária no servidor.
 */
public class WeaponClaim {

    private String ownerTeamId;

    public WeaponClaim() {
    }

    public WeaponClaim(String ownerTeamId) {
        this.ownerTeamId = ownerTeamId;
    }

    public String getOwnerTeamId() {
        return ownerTeamId;
    }

    public void setOwnerTeamId(String ownerTeamId) {
        this.ownerTeamId = ownerTeamId;
    }

    public boolean hasOwner() {
        return ownerTeamId != null && !ownerTeamId.isBlank();
    }
}
