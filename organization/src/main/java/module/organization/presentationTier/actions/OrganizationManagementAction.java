/*
 * @(#)OrganizationManagementAction.java
 *
 * Copyright 2009 Instituto Superior Tecnico
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
package module.organization.presentationTier.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import module.organization.domain.Accountability;
import module.organization.domain.AccountabilityType;
import module.organization.domain.AccountabilityType.AccountabilityTypeBean;
import module.organization.domain.ConnectionRule;
import module.organization.domain.ConnectionRule.ConnectionRuleBean;
import module.organization.domain.Party;
import module.organization.domain.PartyBean;
import module.organization.domain.PartyType;
import module.organization.domain.PartyType.PartyTypeBean;
import module.organization.domain.Person;
import module.organization.domain.Person.PersonBean;
import module.organization.domain.Unit;
import module.organization.domain.UnitBean;
import module.organization.presentationTier.renderers.OrganizationViewConfiguration;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.bennu.core.domain.MyOrg;
import pt.ist.bennu.core.domain.exceptions.DomainException;
import pt.ist.bennu.core.presentationTier.DefaultContext;
import pt.ist.bennu.core.presentationTier.actions.ContextBaseAction;
import pt.ist.bennu.core.presentationTier.forms.BaseForm;
import pt.ist.bennu.portal.Application;
import pt.ist.bennu.portal.Functionality;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;
import pt.utl.ist.fenix.tools.util.StringNormalizer;

/**
 * 
 * @author Pedro Santos
 * @author João Figueiredo
 * @author Paulo Abrantes
 * @author Luis Cruz
 * 
 */

@Mapping(path = "/organization", formBeanClass = OrganizationManagementAction.OrganizationForm.class)
@Application(path = "organization", bundle = OrganizationManagementAction.BUNDLE, title = "label.module.organization",
        description = "label.module.organization", group = "#managers")
public class OrganizationManagementAction extends ContextBaseAction {

    private static MyOrg getMyOrg() {
        return MyOrg.getInstance();
    }

    public final static String BUNDLE = "resources.OrganizationResources";

    static public class OrganizationForm extends BaseForm {
        private static final long serialVersionUID = 4469811183847905665L;

        private String name;
        private String connectionRuleClassName;
        private String[] oids;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getConnectionRuleClassName() {
            return connectionRuleClassName;
        }

        public void setConnectionRuleClassName(String connectionRuleClassName) {
            this.connectionRuleClassName = connectionRuleClassName;
        }

        boolean hasConnectionRuleClassName() {
            return connectionRuleClassName != null && !connectionRuleClassName.isEmpty();
        }

        public String[] getOids() {
            return oids;
        }

        public void setOids(String[] oids) {
            this.oids = oids;
        }

        public boolean hasOids() {
            return getOids() != null;
        }

        public boolean hasName() {
            return getName() != null && !getName().isEmpty();
        }
    }

    @Override
    public ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        final ActionForward forward = super.execute(mapping, form, request, response);
        final DefaultContext layoutContext = (DefaultContext) getContext(request);
        layoutContext.setHead("/organization/layoutContext/head.jsp");
        return forward;
    }

    @Functionality(app = OrganizationManagementAction.class, path = "intro", bundle = BUNDLE,
            title = "label.manage.organization", description = "label.manage.organization", group = "#managers")
    public ActionForward intro(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        return forward(request, "/organization/intro.jsp");
    }

    public ActionForward app(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        return intro(mapping, form, request, response);
    }

    public ActionForward viewPartyTypes(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        request.setAttribute("partyTypes", getMyOrg().getPartyTypesSet());
        return forward(request, "/organization/partyTypes/viewPartyTypes.jsp");
    }

    public ActionForward prepareCreatePartyType(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        request.setAttribute("partyTypeBean", new PartyTypeBean());
        return forward(request, "/organization/partyTypes/createPartyType.jsp");
    }

    public ActionForward createPartyType(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        final PartyTypeBean bean = getRenderedObject("partyTypeBean");
        try {
            PartyType.create(bean);
        } catch (final DomainException e) {
            addMessage(request, e.getMessage(), e.getArgs());
            request.setAttribute("partyTypeBean", bean);
            return forward(request, "/organization/partyTypes/createPartyType.jsp");
        }
        return viewPartyTypes(mapping, form, request, response);
    }

    public ActionForward prepareEditPartyType(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        request.setAttribute("partyTypeBean", new PartyTypeBean((PartyType) getDomainObject(request, "partyTypeOid")));
        return forward(request, "/organization/partyTypes/editPartyType.jsp");
    }

    public ActionForward editPartyType(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        final PartyTypeBean bean = getRenderedObject("partyTypeBean");
        try {
            bean.edit();
        } catch (final DomainException e) {
            addMessage(request, e.getMessage(), e.getArgs());
            request.setAttribute("partyTypeBean", bean);
            return forward(request, "/organization/partyTypes/editPartyType.jsp");
        }

        return viewPartyTypes(mapping, form, request, response);
    }

    public ActionForward deletePartyType(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        ((PartyType) getDomainObject(request, "partyTypeOid")).delete();
        return viewPartyTypes(mapping, form, request, response);
    }

    public ActionForward viewAccountabilityTypes(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        request.setAttribute("accountabilityTypes", getMyOrg().getAccountabilityTypesSet());
        return forward(request, "/organization/accountabilityTypes/viewAccountabilityTypes.jsp");
    }

    public ActionForward prepareCreateAccountabilityType(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        request.setAttribute("accountabilityTypeBean", new AccountabilityTypeBean());
        return forward(request, "/organization/accountabilityTypes/createAccountabilityType.jsp");
    }

    public ActionForward createAccountabilityType(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final AccountabilityTypeBean bean = getRenderedObject("accountabilityTypeBean");
        try {
            bean.create();
        } catch (final DomainException e) {
            addMessage(request, e.getMessage(), e.getArgs());
            request.setAttribute("accountabilityTypeBean", bean);
            return forward(request, "/organization/accountabilityTypes/createAccountabilityType.jsp");
        }
        return viewAccountabilityTypes(mapping, form, request, response);
    }

    public ActionForward prepareEditAccountabilityType(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final AccountabilityType type = getDomainObject(request, "accountabilityTypeOid");
        request.setAttribute("accountabilityTypeBean", new AccountabilityTypeBean(type));
        return forward(request, "/organization/accountabilityTypes/editAccountabilityType.jsp");
    }

    public ActionForward editAccountabilityType(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final AccountabilityTypeBean bean = getRenderedObject("accountabilityTypeBean");
        try {
            bean.edit();
        } catch (final DomainException e) {
            addMessage(request, e.getMessage(), e.getArgs());
            request.setAttribute("accountabilityTypeBean", bean);
            return forward(request, "/organization/accountabilityTypes/editAccountabilityType.jsp");
        }
        return viewAccountabilityTypes(mapping, form, request, response);
    }

    public ActionForward deleteAccountabilityType(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        try {
            ((AccountabilityType) getDomainObject(request, "accountabilityTypeOid")).delete();
        } catch (final DomainException e) {
            addMessage(request, e.getMessage(), e.getArgs());
        }
        return viewAccountabilityTypes(mapping, form, request, response);
    }

    public ActionForward prepareAssociateConnectionRules(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {

        final AccountabilityType type = getDomainObject(request, "accountabilityTypeOid");
        buildConnectionRuleOids(type, (OrganizationForm) form);

        request.setAttribute("accountabilityType", type);
        request.setAttribute("connectionRules", getMyOrg().getConnectionRulesSet());

        return forward(request, "/organization/connectionRules/associateConnectionRules.jsp");
    }

    private void buildConnectionRuleOids(final AccountabilityType type, final OrganizationForm organizationForm) {
        int index = 0;
        final String[] oids = new String[type.getConnectionRulesSet().size()];
        for (final ConnectionRule connectionRule : type.getConnectionRulesSet()) {
            oids[index++] = connectionRule.getExternalId();
        }
        organizationForm.setOids(oids);
    }

    public ActionForward associateConnectionRules(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {

        final AccountabilityType type = getDomainObject(request, "accountabilityTypeOid");

        try {
            type.associateConnectionRules(buildConnectionRules((OrganizationForm) form));
        } catch (final DomainException e) {
            addMessage(request, e.getKey(), e.getArgs());
            request.setAttribute("accountabilityType", type);
            request.setAttribute("connectionRules", getMyOrg().getConnectionRulesSet());
            return forward(request, "/organization/connectionRules/associateConnectionRules.jsp");
        }

        return viewAccountabilityTypes(mapping, form, request, response);
    }

    private List<ConnectionRule> buildConnectionRules(final OrganizationForm form) {
        final List<ConnectionRule> result = new ArrayList<ConnectionRule>();
        if (form.hasOids()) {
            for (final String oid : form.getOids()) {
                result.add((ConnectionRule) getDomainObject(oid));
            }
        }
        return result;
    }

    public ActionForward viewConnectionRules(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        request.setAttribute("connectionRules", getMyOrg().getConnectionRulesSet());
        return forward(request, "/organization/connectionRules/viewConnectionRules.jsp");
    }

    public ActionForward prepareCreateConnectionRule(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {

        final OrganizationForm organizationForm = (OrganizationForm) form;
        if (organizationForm.hasConnectionRuleClassName()) {
            final Class<?> clazz = Class.forName(organizationForm.getConnectionRuleClassName());
            request.setAttribute("connectionRuleBean", clazz.newInstance());
        } else {
            request.removeAttribute("connectionRuleBean");
        }
        RenderUtils.invalidateViewState();

        return forward(request, "/organization/connectionRules/createConnectionRule.jsp");
    }

    public ActionForward createConnectionRuleInvalid(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        request.setAttribute("connectionRuleBean", getRenderedObject("connectionRuleBean"));
        return forward(request, "/organization/connectionRules/createConnectionRule.jsp");
    }

    public ActionForward createConnectionRule(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {

        final ConnectionRuleBean bean = getRenderedObject("connectionRuleBean");
        if (bean == null) {
            addMessage(request, "label.connection.rule.select.type");
            request.setAttribute("connectionRuleBean", bean);
            return forward(request, "/organization/connectionRules/createConnectionRule.jsp");
        }

        try {
            bean.create();
        } catch (final DomainException e) {
            addMessage(request, e.getMessage(), e.getArgs());
            request.setAttribute("connectionRuleBean", bean);
            return forward(request, "/organization/connectionRules/createConnectionRule.jsp");
        }

        return viewConnectionRules(mapping, form, request, response);
    }

    public ActionForward deleteConnectionRule(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {

        final ConnectionRule connectionRule = getDomainObject(request, "connectionRuleOid");
        try {
            connectionRule.delete();
        } catch (final DomainException e) {
            addMessage(request, e.getMessage(), e.getArgs());
        }

        return viewConnectionRules(mapping, form, request, response);
    }

    public ActionForward viewOrganization(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        request.setAttribute("myorg", getMyOrg());
        request.setAttribute("config", OrganizationViewConfiguration.defaultConfiguration());
        return forward(request, "/organization/viewOrganization.jsp");
    }

    public ActionForward viewParty(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        final Party party = getDomainObject(request, "partyOid");
        if (party.isUnit()) {
            return viewUnit(mapping, form, request, response, (Unit) party);
        } else if (party.isPerson()) {
            return viewPerson(mapping, form, request, response, (Person) party);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private ActionForward viewUnit(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response, final Unit unit) throws Exception {
        request.setAttribute("unit", unit);
        return forward(request, "/organization/unit/viewUnit.jsp");
    }

    public ActionForward prepareCreateUnit(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        final UnitBean bean = new UnitBean();
        bean.setParent((Unit) getDomainObject(request, "partyOid"));
        request.setAttribute("unitBean", bean);
        return forward(request, "/organization/unit/createUnit.jsp");
    }

    public ActionForward createUnit(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        final UnitBean bean = getRenderedObject("unitBean");
        Unit result = null;
        try {
            result = bean.createUnit();
        } catch (final DomainException e) {
            addMessage(request, e.getKey(), e.getArgs());
            request.setAttribute("unitBean", bean);
            return forward(request, "/organization/unit/createUnit.jsp");
        }

        if (result.isTop()) {
            return viewOrganization(mapping, form, request, response);
        } else {
            request.setAttribute("unit", bean.getParent());
            return forward(request, "/organization/unit/viewUnit.jsp");
        }
    }

    public ActionForward prepareEditUnit(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        final Unit unit = getDomainObject(request, "partyOid");
        request.setAttribute("unitBean", new UnitBean(unit));
        return forward(request, "/organization/unit/editUnit.jsp");
    }

    public ActionForward editUnit(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        final UnitBean bean = getRenderedObject("unitBean");
        try {
            bean.editUnit();
        } catch (final DomainException e) {
            addMessage(request, e.getKey(), e.getArgs());
            request.setAttribute("unitBean", bean);
            return forward(request, "/organization/unit/editUnit.jsp");
        }

        request.setAttribute("unit", bean.getUnit());
        return forward(request, "/organization/unit/viewUnit.jsp");
    }

    public ActionForward deleteUnit(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        final Unit unit = getDomainObject(request, "partyOid");
        try {
            unit.delete();
        } catch (final DomainException e) {
            addMessage(request, e.getKey(), e.getArgs());
            request.setAttribute("unit", unit);
            return forward(request, "/organization/unit/viewUnit.jsp");
        }
        return viewOrganization(mapping, form, request, response);
    }

    public ActionForward prepareAddParent(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        request.setAttribute("partyBean", createPartyBean(request));
        return forward(request, "/organization/addParent.jsp");
    }

    private PartyBean createPartyBean(final HttpServletRequest request) {
        final Party party = getDomainObject(request, "partyOid");
        if (party.isUnit()) {
            return new UnitBean((Unit) party);
        } else if (party.isPerson()) {
            return new PersonBean((Person) party);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public ActionForward addParent(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {

        final PartyBean bean = getRenderedObject("partyBean");
        try {
            bean.addParent();
        } catch (final DomainException e) {
            addMessage(request, e.getKey(), e.getArgs());
            request.setAttribute("partyBean", bean);
            return forward(request, "/organization/addParent.jsp");
        }

        request.setAttribute("partyOid", bean.getParty().getExternalId());
        return viewParty(mapping, form, request, response);
    }

    public ActionForward prepareAddChild(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        final UnitBean bean = new UnitBean();
        bean.setParent((Unit) getDomainObject(request, "partyOid"));
        request.setAttribute("unitBean", bean);
        return forward(request, "/organization/unit/addChild.jsp");
    }

    public ActionForward addChild(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        final UnitBean bean = getRenderedObject("unitBean");
        try {
            bean.addChild();
        } catch (final DomainException e) {
            addMessage(request, e.getKey(), e.getArgs());
            request.setAttribute("unitBean", bean);
            return forward(request, "/organization/unit/addChild.jsp");
        }

        request.setAttribute("unit", bean.getParent());
        return forward(request, "/organization/unit/viewUnit.jsp");
    }

    public ActionForward removeParent(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        final Accountability accountability = getDomainObject(request, "accOid");
        final Party child = accountability.getChild();
        try {
            child.removeParent(accountability);
        } catch (final DomainException e) {
            addMessage(request, e.getKey(), e.getArgs());
        }

        request.setAttribute("partyOid", child.getExternalId());
        return viewParty(mapping, form, request, response);
    }

    public ActionForward removeChild(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        final Accountability accountability = getDomainObject(request, "accOid");
        final Party parent = accountability.getParent();
        final Party child = accountability.getChild();
        try {
            child.removeParent(accountability);
        } catch (final DomainException e) {
            addMessage(request, e.getKey(), e.getArgs());
        }
        request.setAttribute("unit", parent);
        return forward(request, "/organization/unit/viewUnit.jsp");
    }

    public ActionForward prepareEditPartyPartyTypes(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {

        final Party party = getDomainObject(request, "partyOid");
        buildPartyTypeOids(party, (OrganizationForm) form);

        request.setAttribute("party", party);
        request.setAttribute("partyTypes", getMyOrg().getPartyTypesSet());
        return forward(request, "/organization/partyTypes/editPartyPartyTypes.jsp");
    }

    private void buildPartyTypeOids(final Party party, final OrganizationForm organizationForm) {
        int index = 0;
        final String[] oids = new String[party.getPartyTypesSet().size()];
        for (final PartyType partyType : party.getPartyTypesSet()) {
            oids[index++] = partyType.getExternalId();
        }
        organizationForm.setOids(oids);
    }

    public ActionForward editPartyPartyTypes(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {

        final Party party = getDomainObject(request, "partyOid");
        try {
            party.editPartyTypes(buildPartyTypes((OrganizationForm) form));
        } catch (final DomainException e) {
            addMessage(request, e.getKey(), e.getArgs());
            request.setAttribute("party", party);
            request.setAttribute("partyTypes", getMyOrg().getPartyTypesSet());
            return forward(request, "/organization/partyTypes/editPartyPartyTypes.jsp");
        }

        return viewParty(mapping, form, request, response);
    }

    private List<PartyType> buildPartyTypes(final OrganizationForm form) {
        final List<PartyType> result = new ArrayList<PartyType>();
        if (form.hasOids()) {
            for (final String oid : form.getOids()) {
                result.add((PartyType) getDomainObject(oid));
            }
        }
        return result;
    }

    public ActionForward managePersons(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        return forward(request, "/organization/person/managePersons.jsp");
    }

    public ActionForward searchPerson(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        searchPerson(request, (OrganizationForm) form);
        return forward(request, "/organization/person/managePersons.jsp");
    }

    // TODO: refactor this code?
    private void searchPerson(final HttpServletRequest request, final OrganizationForm form) {
        if (!form.hasName()) {
            addMessage(request, "label.must.introduce.name");
            return;
        }

        final List<Person> persons = new ArrayList<Person>();

        final String trimmedValue = form.getName().trim();
        final String[] input = trimmedValue.split(" ");
        StringNormalizer.normalize(input);

        for (final Party party : getMyOrg().getPersonsSet()) {
            if (party.isPerson()) {
                final Person person = (Person) party;
                final String unitName = StringNormalizer.normalize(person.getPartyName().getContent());
                if (hasMatch(input, unitName)) {
                    persons.add(person);
                }
            }
        }

        Collections.sort(persons, Party.COMPARATOR_BY_NAME);
        request.setAttribute("persons", persons);
    }

    private boolean hasMatch(final String[] input, final String unitNameParts) {
        for (final String namePart : input) {
            if (unitNameParts.indexOf(namePart) == -1) {
                return false;
            }
        }
        return true;
    }

    public ActionForward prepareCreatePerson(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        request.setAttribute("personBean", new PersonBean());
        return forward(request, "/organization/person/createPerson.jsp");
    }

    public ActionForward createPerson(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {

        final PersonBean bean = getRenderedObject("personBean");
        Person person = null;
        try {
            person = Person.create(bean);
        } catch (final DomainException e) {
            addMessage(request, e.getKey(), e.getArgs());
            request.setAttribute("personBean", new PersonBean());
            return forward(request, "/organization/person/createPerson.jsp");
        }

        return viewPerson(mapping, form, request, response, person);
    }

    private ActionForward viewPerson(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response, Person person) {
        request.setAttribute("person", person);
        return forward(request, "/organization/person/viewPerson.jsp");
    }

    public ActionForward prepareEditPerson(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        request.setAttribute("personBean", new PersonBean((Person) getDomainObject(request, "partyOid")));
        return forward(request, "/organization/person/editPerson.jsp");
    }

    public ActionForward editPerson(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {

        final PersonBean bean = getRenderedObject("personBean");
        try {
            bean.edit();
        } catch (final DomainException e) {
            addMessage(request, e.getKey(), e.getArgs());
            request.setAttribute("personBean", bean);
            return forward(request, "/organization/person/editPerson.jsp");
        }

        return viewPerson(mapping, form, request, response, bean.getPerson());
    }

    public ActionForward deletePerson(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        final Person person = getDomainObject(request, "partyOid");
        try {
            person.delete();
        } catch (final DomainException e) {
            addMessage(request, e.getKey(), e.getArgs());
            request.setAttribute("person", person);
            return forward(request, "/organization/person/viewPerson.jsp");
        }
        return managePersons(mapping, form, request, response);
    }

}