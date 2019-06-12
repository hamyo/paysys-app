package paysys.repository;

import paysys.domain.Account;

/**
 * Implementing this interface allows an object to be the repository for {@code Account} object
 */
public interface AccountRepository {
    /**
     * Save account in repository
     *
     * @param account Account for saving
     * @return Saved account
     */
    Account save(Account account);

    /**
     * Get account by Id
     * @param id Id of needing account
     * @return {@code Account} object
     */
    Account getById(Long id);

    /**
     * Update account's data in repository
     *
     * @param account Account for updating
     */
    void update(Account account);

    /**
     * Lock needing account
     * <p>It is recommended practice to <em>always</em> immediately
     * follow a call to {@code lock} with a {@code try} block, such as:
     *
     * <pre> {@code
     *     accountRepository.lock();  // block until condition holds
     *     try {
     *       // ... method body
     *     } finally {
     *       accountRepository.unlock()
     *     }
     *   }
     * }}</pre>
     *
     * @param id Account's id for lock
     */
    void lock(Long id);

    /**
     * Attempts to release lock for needing account.
     *
     * @param id Account's id for unlock
     */
    void unlock(Long id);
}
