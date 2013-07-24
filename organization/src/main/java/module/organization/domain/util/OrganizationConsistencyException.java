/*
 * @(#)OrganizationConsistencyException.java
 *
 * Copyright 2011 Instituto Superior Tecnico
 * Founding Authors: João Figueiredo, Luis Cruz
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Organization Module.
 *
 *   The Organization Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The Organization Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Organization Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package module.organization.domain.util;

import jvstm.cps.ConsistencyException;
import pt.ist.bennu.core.i18n.BundleUtil;

/**
 * 
 * @author João Neves
 * 
 */
public class OrganizationConsistencyException extends ConsistencyException {
    private static final long serialVersionUID = 1L;

    @Override
    public String getLocalizedMessage() {
        return BundleUtil.getString("resources/OrganizationResources", "error." + getMethodFullname());
    }
}
