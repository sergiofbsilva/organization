/*
 * @(#)OrganizationRenderer.java
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
package module.organization.presentationTier.renderers;

import java.util.Comparator;

import module.organization.domain.Party;
import module.organization.domain.predicates.PartyPredicate;
import module.organization.presentationTier.renderers.decorators.PartyDecorator;
import pt.ist.fenixWebFramework.renderers.OutputRenderer;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;

/**
 * 
 * @author João Figueiredo
 * 
 */
public class OrganizationRenderer extends OutputRenderer implements OrganizationView {

    private String rootClasses = "tree";
    private String childListStyle = "display:none";

    private String blankImage = "/organization/images/blank.gif";
    private String minusImage = "/organization/images/minus.gif";
    private String plusImage = "/organization/images/plus.gif";

    private String viewPartyUrl = "/organization.do?method=viewParty&amp;partyOid=%s";

    @Override
    public String getRootClasses() {
        return rootClasses;
    }

    public void setRootClasses(String rootClasses) {
        this.rootClasses = rootClasses;
    }

    @Override
    public String getChildListStyle() {
        return childListStyle;
    }

    public void setChildListStyle(String childListStyle) {
        this.childListStyle = childListStyle;
    }

    @Override
    public String getBlankImage() {
        return blankImage;
    }

    public void setBlankImage(String blankImage) {
        this.blankImage = blankImage;
    }

    @Override
    public String getMinusImage() {
        return minusImage;
    }

    public void setMinusImage(String minusImage) {
        this.minusImage = minusImage;
    }

    @Override
    public String getPlusImage() {
        return plusImage;
    }

    public void setPlusImage(String plusImage) {
        this.plusImage = plusImage;
    }

    @Override
    public String getViewPartyUrl() {
        return viewPartyUrl;
    }

    public void setViewPartyUrl(String viewPartyUrl) {
        this.viewPartyUrl = viewPartyUrl;
    }

    protected OrganizationViewConfiguration getConfig() {
        return OrganizationViewConfiguration.defaultConfiguration();
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return (Layout) getConfig().getLayout().saveView(this);
    }

    @Override
    public Comparator<Party> getSortBy() {
        return getConfig().getSortBy();
    }

    @Override
    public PartyDecorator getDecorator() {
        return getConfig().getDecorator();
    }

    @Override
    public PartyPredicate getPredicate() {
        return getConfig().getPredicate();
    }

    @Override
    public void setProperty(String name, Object value) {
        throw new RuntimeException("do.not.use.this.method");
    }
}
