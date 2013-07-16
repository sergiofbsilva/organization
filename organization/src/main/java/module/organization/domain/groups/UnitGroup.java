/*
 * @(#)UnitGroup.java
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
package module.organization.domain.groups;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import module.organization.domain.Accountability;
import module.organization.domain.AccountabilityType;
import module.organization.domain.Party;
import module.organization.domain.Person;
import module.organization.domain.Unit;
import module.organization.domain.exceptions.OrganizationDomainException;

import org.joda.time.DateTime;

import pt.ist.bennu.core.annotation.CustomGroupArgument;
import pt.ist.bennu.core.annotation.CustomGroupOperator;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.groups.Group;
import pt.ist.bennu.core.i18n.BundleUtil;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

/**
 * 
 * @author Luis Cruz
 * @author Susana Fernandes
 * 
 */
@CustomGroupOperator("unit")
public class UnitGroup extends UnitGroup_Base {

    private static abstract class AccountabilityTypeArgs implements SimpleArgument<AccountabilityType[], UnitGroup> {
        private static final long serialVersionUID = -2413683214414411444L;

        @Override
        public AccountabilityType[] parse(String argument) {
            return FluentIterable.from(Arrays.asList(argument.split("|"))).transform(new Function<String, AccountabilityType>() {

                @Override
                public AccountabilityType apply(String input) {
                    return AccountabilityType.readBy(input);
                }
            }).toArray(AccountabilityType.class);
        }

        @Override
        public String extract(UnitGroup group) {
            return Joiner.on("|").join(
                    FluentIterable.from(getAccountabilitySet(group)).transform(new Function<AccountabilityType, String>() {

                        @Override
                        public String apply(AccountabilityType input) {
                            return input.getType();
                        }
                    }));
        }

        abstract Iterable<AccountabilityType> getAccountabilitySet(UnitGroup group);

        @Override
        public Class<? extends AccountabilityType[]> getType() {
            return AccountabilityType[].class;
        }
    }

    @CustomGroupArgument(index = 1)
    public static Argument<Unit, UnitGroup> unitArg() {
        return new SimpleArgument<Unit, UnitGroup>() {
            private static final long serialVersionUID = -1849855694994920352L;

            @Override
            public Unit parse(String argument) {
                return FenixFramework.getDomainObject(argument);
            }

            @Override
            public String extract(UnitGroup group) {
                return group.getUnit().getExternalId();
            }

            @Override
            public Class<? extends Unit> getType() {
                return Unit.class;
            }
        };
    }

    @CustomGroupArgument(index = 2)
    public static Argument<AccountabilityType[], UnitGroup> memberTypesArg() {
        return new AccountabilityTypeArgs() {

            private static final long serialVersionUID = 1870253423753413002L;

            @Override
            Iterable<AccountabilityType> getAccountabilitySet(UnitGroup group) {
                return group.getMemberAccountabilityTypeSet();
            }
        };
    }

    @CustomGroupArgument(index = 3)
    public static Argument<AccountabilityType[], UnitGroup> childUnitTypesArg() {
        return new AccountabilityTypeArgs() {

            private static final long serialVersionUID = -4743936002636799176L;

            @Override
            Iterable<AccountabilityType> getAccountabilitySet(UnitGroup group) {
                return group.getChildUnitAccountabilityTypeSet();
            }

        };

    }

    private UnitGroup(final Unit unit, final AccountabilityType[] memberTypes, final AccountabilityType[] childUnitTypes) {
        super();
        if (memberTypes == null || memberTypes.length == 0) {
            throw new OrganizationDomainException("cannot.create.empty.unit.group");
        }
        setUnit(unit);
        addAccountabilityTypes(getMemberAccountabilityTypeSet(), memberTypes);
        addAccountabilityTypes(getChildUnitAccountabilityTypeSet(), childUnitTypes);
    }

    private void addAccountabilityTypes(final Set<AccountabilityType> accountabilityTypes, final AccountabilityType[] typeArray) {
        if (typeArray != null) {
            for (final AccountabilityType accountabilityType : typeArray) {
                accountabilityTypes.add(accountabilityType);
            }
        }
    }

    @Override
    public Set<User> getMembers() {
        final Unit unit = getUnit();
        return unit.getMembers(getAccountabilityTypes());
    }

    @Override
    public boolean isMember(final User user) {
        if (user != null && user.getPerson() != null) {
            final Person person = user.getPerson();
            final Unit unit = getUnit();
            return person.hasPartyAsAncestor(unit, getAccountabilityTypes());
        }
        return false;
    }

    private transient Set<AccountabilityType> accountabilityTypes = null;

    private Set<AccountabilityType> getAccountabilityTypes() {
        Set<AccountabilityType> result = accountabilityTypes;
        if (result == null) {
            result = new HashSet<AccountabilityType>();
            result.addAll(getMemberAccountabilityTypeSet());
            result.addAll(getChildUnitAccountabilityTypeSet());
            accountabilityTypes = result;
        }
        return result;
    }

    @Atomic
    public static UnitGroup getInstance(final Unit unit, final AccountabilityType[] memberTypes,
            final AccountabilityType[] childUnitTypes) {

        UnitGroup unitGroup = select(UnitGroup.class, new Predicate<UnitGroup>() {

            @Override
            public boolean apply(UnitGroup input) {
                return match(input.getMemberAccountabilityTypeSet(), memberTypes)
                        && match(input.getChildUnitAccountabilityTypeSet(), childUnitTypes);
            }
        });

        return unitGroup != null ? unitGroup : new UnitGroup(unit, memberTypes, childUnitTypes);
    }

    private static boolean match(final Set<AccountabilityType> types1, final AccountabilityType[] types2) {
        if (types2 == null) {
            return types1.isEmpty();
        }

        if (types1.size() == types2.length) {
            for (int i = 0; i < types2.length; i++) {
                final AccountabilityType accountabilityType = types2[i];
                if (!types1.contains(accountabilityType)) {
                    return false;
                }
                for (int j = i + 1; j < types2.length; j++) {
                    if (accountabilityType == types2[j]) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Deprecated
    public java.util.Set<module.organization.domain.AccountabilityType> getChildUnitAccountabilityType() {
        return getChildUnitAccountabilityTypeSet();
    }

    @Deprecated
    public java.util.Set<module.organization.domain.AccountabilityType> getMemberAccountabilityType() {
        return getMemberAccountabilityTypeSet();
    }

    @Override
    public String getPresentationName() {
        final Unit unit = getUnit();
        final StringBuilder builder = new StringBuilder();
        for (final AccountabilityType accountabilityType : getMemberAccountabilityTypeSet()) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(accountabilityType.getName().getContent());
        }
        final String unitName = unit.getPresentationName();
        final String unitIdentifier =
                !getChildUnitAccountabilityTypeSet().isEmpty() ? BundleUtil.getString("resources/OrganizationResources",
                        "label.persistent.group.unitGroup.includeing.subunits", unitName) : unitName;
        return BundleUtil.getString("resources/OrganizationResources", "label.persistent.group.unitGroup.name", unitIdentifier,
                builder.toString());
    }

    @Override
    public Set<User> getMembers(DateTime when) {
        return getMembers();
    }

    @Override
    public boolean isMember(User user, DateTime when) {
        return isMember(user);
    }

    public static Set<Group> groupsForUser(User user) {
        final Set<Group> groups = new HashSet<Group>();
        if (user.getPerson() != null) {
            for (Accountability accountability : user.getPerson().getParentAccountabilitiesSet()) {
                if (accountability.isActiveNow()) {
                    Party parent = accountability.getParent();
                    if (parent.isUnit()) {
                        Unit unit = (Unit) parent;
                        for (UnitGroup unitGroup : unit.getUnitGroupSet()) {
                            if (unitGroup.isMember(user)) {
                                groups.add(unitGroup);
                            }
                        }
                    }
                }
            }
        }
        return groups;
    }

}
