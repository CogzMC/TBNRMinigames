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

package net.tbnr.commerce.items;

/**
 * Dictates a "conflict"
 *
 * If an item cannot be used while another on is present, this can be used to note that.
 *
 * Action performed:
 *
 */
public @interface Conflicts {
    public Class<? extends CommerceItem>[] value();
}
