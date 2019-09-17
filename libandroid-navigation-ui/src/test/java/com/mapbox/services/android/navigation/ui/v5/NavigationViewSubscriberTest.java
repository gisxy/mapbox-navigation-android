package com.mapbox.services.android.navigation.ui.v5;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NavigationViewSubscriberTest {

  @Mock
  private LifecycleOwner lifecycleOwner;
  @Mock
  private Lifecycle lifecycle;
  @Mock
  private NavigationPresenter navigationPresenter;
  @Mock
  private NavigationViewModel navigationViewModel;

  @Rule
  public MockitoRule mockitoRule = MockitoJUnit.rule();

  private NavigationViewSubscriber theNavigationViewSubscriber;

  @Before
  public void setup() {
    when(lifecycleOwner.getLifecycle()).thenReturn(lifecycle);

    theNavigationViewSubscriber = new NavigationViewSubscriber(lifecycleOwner,
      navigationViewModel, navigationPresenter);
  }

  @Test
  public void checkLifecycleObserverAddedWhenCreateSubscriber() {
    verify(lifecycleOwner.getLifecycle()).addObserver(theNavigationViewSubscriber);
  }

  @Test
  public void checkObserversAreRemovedWhenUnsubscribe() {
    when(navigationViewModel.retrieveRoute()).thenReturn(mock(MutableLiveData.class));
    when(navigationViewModel.retrieveNavigationLocation()).thenReturn(mock(MutableLiveData.class));
    when(navigationViewModel.retrieveDestination()).thenReturn(mock(MutableLiveData.class));
    when(navigationViewModel.retrieveShouldRecordScreenshot()).thenReturn(mock(MutableLiveData.class));

    theNavigationViewSubscriber.unsubscribe();

    verify(navigationViewModel.retrieveRoute()).removeObservers(eq(lifecycleOwner));
    verify(navigationViewModel.retrieveNavigationLocation()).removeObservers(eq(lifecycleOwner));
    verify(navigationViewModel.retrieveDestination()).removeObservers(eq(lifecycleOwner));
    verify(navigationViewModel.retrieveShouldRecordScreenshot()).removeObservers(eq(lifecycleOwner));
  }

  @Test
  public void checkObserversAreAddedWhenSubscribe() {
    when(navigationViewModel.retrieveRoute()).thenReturn(mock(MutableLiveData.class));
    when(navigationViewModel.retrieveNavigationLocation()).thenReturn(mock(MutableLiveData.class));
    when(navigationViewModel.retrieveDestination()).thenReturn(mock(MutableLiveData.class));
    when(navigationViewModel.retrieveShouldRecordScreenshot()).thenReturn(mock(MutableLiveData.class));

    theNavigationViewSubscriber.subscribe();

    verify(navigationViewModel.retrieveRoute()).observe(eq(lifecycleOwner), any(Observer.class));
    verify(navigationViewModel.retrieveNavigationLocation()).observe(eq(lifecycleOwner), any(Observer.class));
    verify(navigationViewModel.retrieveDestination()).observe(eq(lifecycleOwner), any(Observer.class));
    verify(navigationViewModel.retrieveShouldRecordScreenshot()).observe(eq(lifecycleOwner), any(Observer.class));
  }
}
