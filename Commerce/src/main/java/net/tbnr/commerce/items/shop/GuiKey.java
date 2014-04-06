/*
 * Copyright (c) 2014.
 * Cogz Development LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.tbnr.commerce.items.shop;

public enum GuiKey {
    Shop("shop"),
    Tier("tier"),
    Main("main");

    private String key;

    GuiKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}
