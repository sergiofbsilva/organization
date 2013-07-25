/*
 * @(#)ContactsRoles.java
 *
 * Copyright 2010 Instituto Superior Tecnico
 * Founding Authors: João Antunes
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Contacts Module.
 *
 *   The Contacts Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The Contacts Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Contacts Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package module.contacts.domain;

import pt.ist.bennu.core.domain.groups.legacy.IRoleEnum;

/**
 * 
 * @author João Antunes
 * @author Susana Fernandes
 * 
 */
public enum ContactsRoles implements IRoleEnum {
    MODULE_CONTACTS_DOMAIN_CONTACTSEDITOR;

    @Override
    public String getRoleName() {
        return name();
    }

    @Override
    public String getPresentationName() {
        return getRoleName();
    }

}
