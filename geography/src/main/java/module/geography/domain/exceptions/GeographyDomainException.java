package module.geography.domain.exceptions;

import pt.ist.bennu.core.domain.exceptions.DomainException;

@SuppressWarnings("serial")
public class GeographyDomainException extends DomainException {

    private static final String BUNDLE = "resources.GeographyResources";

    public GeographyDomainException(String key, String... args) {
        super(BUNDLE, key, args);
    }
}
