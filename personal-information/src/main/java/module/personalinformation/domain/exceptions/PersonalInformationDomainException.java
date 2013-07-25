package module.personalinformation.domain.exceptions;

import pt.ist.bennu.core.domain.exceptions.DomainException;

@SuppressWarnings("serial")
public class PersonalInformationDomainException extends DomainException {

    private static final String BUNDLE = "resources.PersonalInformationResources";

    public PersonalInformationDomainException(String key, String... args) {
        super(BUNDLE, key, args);
    }
}
