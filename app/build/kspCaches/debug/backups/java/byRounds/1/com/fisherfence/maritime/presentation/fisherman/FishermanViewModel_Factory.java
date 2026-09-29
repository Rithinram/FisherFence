package com.fisherfence.maritime.presentation.fisherman;

import com.fisherfence.maritime.data.local.FisherDatabase;
import com.fisherfence.maritime.domain.usecase.GetAlertsUseCase;
import com.fisherfence.maritime.domain.usecase.GetWeatherUseCase;
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
public final class FishermanViewModel_Factory implements Factory<FishermanViewModel> {
  private final Provider<GetAlertsUseCase> getAlertsUseCaseProvider;

  private final Provider<GetWeatherUseCase> getWeatherUseCaseProvider;

  private final Provider<ManageTripUseCase> manageTripUseCaseProvider;

  private final Provider<FisherDatabase> dbProvider;

  private final Provider<SettingsManager> settingsManagerProvider;

  public FishermanViewModel_Factory(Provider<GetAlertsUseCase> getAlertsUseCaseProvider,
      Provider<GetWeatherUseCase> getWeatherUseCaseProvider,
      Provider<ManageTripUseCase> manageTripUseCaseProvider, Provider<FisherDatabase> dbProvider,
      Provider<SettingsManager> settingsManagerProvider) {
    this.getAlertsUseCaseProvider = getAlertsUseCaseProvider;
    this.getWeatherUseCaseProvider = getWeatherUseCaseProvider;
    this.manageTripUseCaseProvider = manageTripUseCaseProvider;
    this.dbProvider = dbProvider;
    this.settingsManagerProvider = settingsManagerProvider;
  }

  @Override
  public FishermanViewModel get() {
    return newInstance(getAlertsUseCaseProvider.get(), getWeatherUseCaseProvider.get(), manageTripUseCaseProvider.get(), dbProvider.get(), settingsManagerProvider.get());
  }

  public static FishermanViewModel_Factory create(
      Provider<GetAlertsUseCase> getAlertsUseCaseProvider,
      Provider<GetWeatherUseCase> getWeatherUseCaseProvider,
      Provider<ManageTripUseCase> manageTripUseCaseProvider, Provider<FisherDatabase> dbProvider,
      Provider<SettingsManager> settingsManagerProvider) {
    return new FishermanViewModel_Factory(getAlertsUseCaseProvider, getWeatherUseCaseProvider, manageTripUseCaseProvider, dbProvider, settingsManagerProvider);
  }

  public static FishermanViewModel newInstance(GetAlertsUseCase getAlertsUseCase,
      GetWeatherUseCase getWeatherUseCase, ManageTripUseCase manageTripUseCase, FisherDatabase db,
      SettingsManager settingsManager) {
    return new FishermanViewModel(getAlertsUseCase, getWeatherUseCase, manageTripUseCase, db, settingsManager);
  }
}
