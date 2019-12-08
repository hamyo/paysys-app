package paysys.check;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import paysys.domain.Account;
import paysys.repository.AccountRepository;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AccountExistsCheckTest {
    private AccountRepository accountRepository;

    @Before
    public void setUp() throws Exception {
        this.accountRepository = mock(AccountRepository.class);
        when(accountRepository.getById(1L)).thenReturn(new Account(1L, BigDecimal.TEN, "address@gmail.com"));
    }

    @Test
    public void checkID1Exists() {
        AccountExistsCheck checker = new AccountExistsCheck(accountRepository);
        String actual = checker.check(1L);
        Assert.assertEquals("", actual);
    }

    @Test
    public void checkID2NotExists() {
        AccountExistsCheck checker = new AccountExistsCheck(accountRepository);
        String actual = checker.check(2L);
        Assert.assertNotEquals("", actual);
    }


}