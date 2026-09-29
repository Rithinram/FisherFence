package com.fisherfence.maritime.presentation.coastguard;

import com.fisherfence.maritime.data.local.FisherDatabase;
import com.fisherfence.maritime.domain.usecase.GetAlertsUseCase;
import com.fisherfence.maritime.domain.usecase.ManageTripUseCase;
import com.fisherfence.maritime.utils.SettingsManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class CoastGuardViewModel_Factory implements Factory<CoastGuardViewModel> {
  private final Provider<GetAlertsUseCase> getAlertsUseCaseProvider;

  private final Provider<ManageTripUseCase> manageTripUseCaseProvider;

  private final Provider<FisherDatabase> dbProvider;

  private final Provider<SettingsManager> settingsManagerProvider;

  public CoastGuardViewModel_Factory(Provider<GetAlertsUseCase> getAlertsUseCaseProvider,
      Provider<ManageTripUseCase> manageTripUseCaseProvider, Provider<FisherDatabase> dbProvider,
      Provider<SettingsManager> settingsManagerProvider) {
    this.getAlertsUseCaseProvider = getAlertsUseCaseProvider;
    this.manageTripUseCaseProvider = manageTripUseCaseProvider;
    this.dbProvider = dbProvider;
    this.settingsManagerProvider = settingsManagerProvider;
  }

  @Override
  public CoastGuardViewModel get() {
    return newInstance(getAlertsUseCaseProvider.get(), manageTripUseCaseProvider.get(), dbProvider.get(), settingsManagerProvider.get());
  }

  public static CoastGuardViewModel_Factory create(
      Provider<GetAlertsUseCase> getAlertsUseCaseProvider,
      Provider<ManageTripUseCase> manageTripUseCaseProvider, Provider<FisherDatabase> dbProvider,
      Provider<SettingsManager> settingsManagerProvider) {
    return new CoastGuardViewModel_Factory(getAlertsUseCaseProvider, manageTripUseCaseProvider, dbProvider, settingsManagerProvider);
  }

  public static CoastGuardViewModel newInstance(GetAlertsUseCase getAlertsUseCase,
      ManageTripUseCase manageTripUseCase, FisherDatabase db, SettingsManager settingsManager) {
    return new CoastGuardViewModel(getAlertsUseCase, manageTripUseCase, db, settingsManager);
  }
}
