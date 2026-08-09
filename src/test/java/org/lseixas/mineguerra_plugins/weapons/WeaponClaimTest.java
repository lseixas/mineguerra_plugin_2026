package org.lseixas.mineguerra_plugins.weapons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WeaponClaimTest {

    @Test
    void hasOwnerRequiresNonBlankTeamId() {
        WeaponClaim claim = new WeaponClaim();
        assertFalse(claim.hasOwner());

        claim.setOwnerTeamId("   ");
        assertFalse(claim.hasOwner());

        claim.setOwnerTeamId("red");
        assertTrue(claim.hasOwner());

        claim.setOwnerTeamId(null);
        assertFalse(claim.hasOwner());
    }
}
