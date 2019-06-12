package paysys.check;

import org.junit.After;
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
    public void check_ID1_Exists() {
        AccountExistsCheck check = new AccountExistsCheck(accountRepository);
        String actual = check.check(1L);
        Assert.assertEquals("", actual);
    }

    @Test
    public void check_ID2_NotExists() {
        AccountExistsCheck check = new AccountExistsCheck(accountRepository);
        String actual = check.check(2L);
        Assert.assertNotEquals("", actual);
    }


}