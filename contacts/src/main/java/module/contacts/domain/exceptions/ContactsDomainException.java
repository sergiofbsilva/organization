package module.contacts.domain.exceptions;

import pt.ist.bennu.core.domain.exceptions.DomainException;

@SuppressWarnings("serial")
public class ContactsDomainException extends DomainException {
    private static final String BUNDLE = "resources.ContactsResources";

    public ContactsDomainException(String key, String... args) {
        super(BUNDLE, key, args);
    }
}
