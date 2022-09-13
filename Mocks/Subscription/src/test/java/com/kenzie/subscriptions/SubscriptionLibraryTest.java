package com.kenzie.subscriptions;

import com.kenzie.subscriptions.exceptions.InvalidInputException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubscriptionLibraryTest {
    private String asin = "ABC123";
    private String customerId = "123456789";
    private String subscriptionId = String.format("%s-%s", customerId, asin);

    //  Something goes here
    @Mock
    private SubscriptionDao subscriptionDao;

    // Something goes here
    @Mock
    private EmailServiceClient emailServiceClient;

    // Something goes here
    @InjectMocks
    private SubscriptionLibrary subscriptionLibrary;

    @BeforeEach
    public void setup() {
    // Something goes here
        MockitoAnnotations.openMocks(this);

    }

    @Test
    public void addMonthlySubscription_newSubscription_sendsNewSubscriptionEmail() throws InvalidInputException {
        //  Add your test logic here
        //GIVEN
        MonthlySubscription monthlySubscription = new MonthlySubscription(subscriptionId, asin, customerId );
        when(subscriptionDao.createSubscription(monthlySubscription)).thenReturn(true);


        //WHEN
        assertEquals(subscriptionId,subscriptionLibrary.addMonthlySubscription(asin,customerId));

        //THEN
        verify(emailServiceClient).sendNewSubscriptionEmail(monthlySubscription);

    }

    @Test
    public void cancelSubscription_subscriptionDoesNotExist_throwsInvalidInputException() throws InvalidInputException {
        // Add your test logic here
        //GIVEN
        Subscription subscription = new MonthlySubscription(subscriptionId,asin,customerId);
        when(subscriptionDao.deleteSubscription(subscriptionId)).thenReturn(null);

        //WHEN

        //THEN
        assertThrows(InvalidInputException.class,
                () -> subscriptionLibrary.cancelSubscription(subscriptionId));

    }
}
