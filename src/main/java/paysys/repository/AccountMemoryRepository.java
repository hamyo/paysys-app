package paysys.repository;

import lombok.NonNull;
import paysys.domain.Account;
import paysys.repository.domain.StoredAccount;
import paysys.repository.exception.NotFoundException;
import paysys.repository.exception.RepositoryException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Repository for {@code Account} object. Accounts are kept in Map in memory
 */
public class AccountMemoryRepository implements AccountRepository {

    /**
     * Stored accounts. Key is account's id.
     */
    private Map<Long, StoredAccount> accounts = new ConcurrentHashMap<>();

    /**
     * Сounter for id
     */
    private AtomicLong seq = new AtomicLong(1);

    /**
     * Save account in repository
     *
     * @param account Account for saving
     * @return Copy of saved account with id
     */
    @Override
    public Account save(@NonNull Account account) {
        Account newAcc = new Account(seq.getAndIncrement(), account.getBalance(), account.getEmail());
        StoredAccount accountStore = StoredAccount.of(newAcc);
        accounts.put(accountStore.getId(), accountStore);
        return newAcc;
    }

    /**
     * Get account by Id
     *
     * @param id Id of needing account
     * @return Account object. if account was not found return {@code null}
     */
    @Override
    public Account getById(@NonNull Long id) {
        StoredAccount acc = accounts.get(id);
        return acc == null ? null : acc.copyToAccount();
    }

    /**
     * Update account's data in repository
     *
     * @param account Account for updating
     * @throws RepositoryException if account has no id
     */
    @Override
    public void update(@NonNull Account account) {
        if (account.getId() == null) {
            throw new RepositoryException("Cannot save changes - no identifier", Account.class.getName());
        }
        StoredAccount storedAccount = accounts.get(account.getId());
        if (storedAccount == null) {
            throw new NotFoundException(Account.class.getName(), String.valueOf(account.getId()));
        }
        synchronized (storedAccount) {
            storedAccount.setBalance(account.getBalance());
            storedAccount.setEmail(account.getEmail());
        }
    }

    /**
     * Lock needing account
     *
     * @param id Account's id for lock
     * @throws RepositoryException if account was not found
     */
    @Override
    public void lock(@NonNull Long id) {
        StoredAccount acc = accounts.get(id);
        if (acc == null) {
            throw new NotFoundException(Account.class.getName(), String.valueOf(id));
        }
        acc.getLocker().lock();
    }

    /**
     * Attempts to release lock for needing account.
     *
     * @param id Account's id for unlock
     */
    @Override
    public void unlock(@NonNull Long id) {
        StoredAccount acc = accounts.get(id);
        if (acc != null) {
            acc.getLocker().unlock();
        }
    }
}
