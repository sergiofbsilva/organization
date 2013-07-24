package module.organization.domain.exceptions;

import pt.ist.bennu.core.domain.exceptions.DomainException;

@SuppressWarnings("serial")
public class OrganizationDomainException extends DomainException {

    private static final String BUNDLE = "resources.OrganizationResources";

    public OrganizationDomainException(String key, String... args) {
        super(BUNDLE, key, args);
    }
}
