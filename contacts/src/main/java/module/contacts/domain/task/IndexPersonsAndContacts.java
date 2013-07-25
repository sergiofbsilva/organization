/*
 * @(#)IndexPersonsAndContacts.java
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
package module.contacts.domain.task;

import module.contacts.domain.ContactsConfigurator;
import module.contacts.domain.PartyContact;
import module.organization.domain.Person;
import pt.ist.bennu.core.domain.MyOrg;
import pt.ist.bennu.core.i18n.BundleUtil;
import pt.ist.bennu.scheduler.CronTask;
import pt.ist.bennu.scheduler.annotation.Task;

/**
 * 
 * @author João Antunes
 * 
 */
@Task(englishTitle = "Index persons and contacts")
public class IndexPersonsAndContacts extends CronTask {

    private static int personsTouched = 0;

    private static int contactsTouched = 0;

    /* (non-Javadoc)
     * @see pt.ist.bennu.core.domain.scheduler.Task#getLocalizedName()
     */
    @Override
    public String getLocalizedName() {
        return BundleUtil.getString("resources/ContactsResources", "label.task.index.persons.and.contacts");
    }

    /* (non-Javadoc)
     * @see pt.ist.bennu.core.domain.scheduler.Task#executeTask()
     */
    @Override
    public void runTask() {
        for (Person person : MyOrg.getInstance().getPersonsSet()) {
            person.setPartyName(person.getPartyName());
            personsTouched++;
        }

        for (PartyContact contact : ContactsConfigurator.getInstance().getPartyContact()) {
            contact.setContactValue(contact.getValue());
            contactsTouched++;
        }

        taskLog("Touched " + personsTouched + " persons");
        taskLog("Touched " + contactsTouched + " contacts");

    }

}
