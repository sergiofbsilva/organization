<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr"%>

<h2><bean:write name="unitBean" property="unit.partyName"/>: <bean:message key="label.add.parent" bundle="ORGANIZATION_RESOURCES" /></h2>

<html:messages id="message" message="true" bundle="ORGANIZATION_RESOURCES">
	<span class="error0"> <bean:write name="message" /> </span>
	<br />
</html:messages>

<bean:define id="partyOid" name="unitBean" property="unit.OID" />
<fr:form action='<%= "/organization.do?partyOid=" + partyOid.toString() %>'>
	<html:hidden property="method" value="addParent"/>
	
	<fr:edit id="unitBean" name="unitBean" visible="false" />

	<fr:edit id="unitBean.add.parent" name="unitBean" schema="organization.UnitBean.add.parent">
		<fr:layout name="tabular">
			<fr:property name="classes" value="tstyle2" />
			<fr:property name="columnClasses" value=",,tderror" />
			<fr:property name="requiredMarkShown" value="true" />
		</fr:layout>
	</fr:edit>

	<html:submit><bean:message key="label.add" bundle="ORGANIZATION_RESOURCES" /></html:submit>
	<html:cancel onclick="this.form.method.value='viewParty';return true;" ><bean:message key="label.back" bundle="ORGANIZATION_RESOURCES" /></html:cancel>

</fr:form>