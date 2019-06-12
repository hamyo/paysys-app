package paysys.check;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import paysys.domain.Account;
import paysys.repository.AccountRepository;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BalanceMoreOrEqualCheckTest {
    private AccountRepository accountRepository;

    @Before
    public void setUp() throws Exception {
        this.accountRepository = mock(AccountRepository.class);
        when(accountRepository.getById(1L)).thenReturn(new Account(1L, BigDecimal.TEN, "address@gmail.com"));
    }

    @Test
    public void check_Balance10_MoreOrEqual10() {
        BalanceMoreOrEqualCheck check = new BalanceMoreOrEqualCheck(accountRepository);
        String actual = check.check(1L, BigDecimal.TEN);
        Assert.assertEquals("", actual);
    }

    @Test
    public void checkBalance10_Smaller100() {
        BalanceMoreOrEqualCheck check = new BalanceMoreOrEqualCheck(accountRepository);
        String actual = check.check(1L, BigDecimal.valueOf(100));
        Assert.assertNotEquals("", actual);
    }
}