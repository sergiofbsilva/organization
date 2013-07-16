/*
 * @(#)PersonGroup.java
 *
 * Copyright 2010 Instituto Superior Tecnico
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
package module.organization.domain.groups;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;

import pt.ist.bennu.core.annotation.CustomGroupOperator;
import pt.ist.bennu.core.domain.Bennu;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.groups.Group;
import pt.ist.bennu.core.domain.groups.UserGroup;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

/**
 * 
 * @author Pedro Santos
 * @author Susana Fernandes
 * 
 * 
 *         This group is deprecated since in Bennu2 every person has an user. Use {@link UserGroup}
 */

@Deprecated
@CustomGroupOperator("persons")
public class PersonGroup extends PersonGroup_Base {

    private PersonGroup() {
        super();
    }

    @Override
    public String getPresentationName() {
        return getClass().getSimpleName();
    }

    @Override
    public Set<User> getMembers() {
        return Sets.filter(Bennu.getInstance().getUsersSet(), new Predicate<User>() {

            @Override
            public boolean apply(User input) {
                return input != null && input.getPerson() != null;
            }
        });
    }

    @Override
    public Set<User> getMembers(DateTime when) {
        return getMembers();
    }

    @Override
    public boolean isMember(User user) {
        return user != null && user.getPerson() != null;
    }

    @Override
    public boolean isMember(User user, DateTime when) {
        return isMember(user);
    }

    public static PersonGroup getInstance() {
        final PersonGroup personGroup = select(PersonGroup.class);
        return personGroup == null ? new PersonGroup() : personGroup;
    }

    public static Set<Group> groupsForUser(User user) {
        final Set<Group> groups = new HashSet<Group>();
        if (user != null && user.getPerson() != null) {
            groups.add(getInstance());
        }
        return groups;
    }

}