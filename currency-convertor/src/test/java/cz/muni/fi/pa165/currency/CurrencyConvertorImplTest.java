package cz.muni.fi.pa165.currency;

import org.junit.Test;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CurrencyConvertorImplTest {

    private static final Currency EUR = Currency.getInstance("EUR");
    private static final Currency USD = Currency.getInstance("USD");

    @Mock
    private ExchangeRateTable exchangeRateTable;
    private CurrencyConvertor currencyConvertor;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        currencyConvertor = new CurrencyConvertorImpl(exchangeRateTable);
    }

    @Test
    public void testConvert() throws ExternalServiceFailureException {
        when(exchangeRateTable.getExchangeRate(EUR, USD)).thenReturn(new BigDecimal("1.19"));

        assertThat(currencyConvertor.convert(EUR, USD, new BigDecimal("1.00"))).isEqualTo(new BigDecimal("1.19"));
        assertThat(currencyConvertor.convert(EUR, USD, new BigDecimal("1.01"))).isEqualTo(new BigDecimal("1.20"));
        assertThat(currencyConvertor.convert(EUR, USD, new BigDecimal("1.02"))).isEqualTo(new BigDecimal("1.21"));
    }

    @Test
    public void testConvertWithNullSourceCurrency() {
        assertThatIllegalArgumentException().isThrownBy(() ->
                currencyConvertor.convert(null, USD, new BigDecimal("1.00")));
    }

    @Test
    public void testConvertWithNullTargetCurrency() {
        assertThatIllegalArgumentException().isThrownBy(() ->
                currencyConvertor.convert(EUR, null, new BigDecimal("1.00")));
    }

    @Test
    public void testConvertWithNullSourceAmount() {
        assertThatIllegalArgumentException().isThrownBy(() -> currencyConvertor.convert(EUR, USD, null));
    }

    @Test
    public void testConvertWithUnknownCurrency() throws ExternalServiceFailureException {
        when(exchangeRateTable.getExchangeRate(EUR, USD)).thenReturn(null);
        assertThatExceptionOfType(UnknownExchangeRateException.class).isThrownBy(() ->
                currencyConvertor.convert(EUR, USD, new BigDecimal("1.00")));
    }

    @Test
    public void testConvertWithExternalServiceFailure() throws ExternalServiceFailureException {
        when(exchangeRateTable.getExchangeRate(EUR, USD)).thenThrow(ExternalServiceFailureException.class);
        assertThatExceptionOfType(UnknownExchangeRateException.class).isThrownBy(() ->
                currencyConvertor.convert(EUR, USD, new BigDecimal("1.00")));
    }

}
