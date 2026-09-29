package com.fisherfence.maritime.domain.usecase;

import com.fisherfence.maritime.domain.repository.FisherRepository;
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
public final class ManageTripUseCase_Factory implements Factory<ManageTripUseCase> {
  private final Provider<FisherRepository> repositoryProvider;

  public ManageTripUseCase_Factory(Provider<FisherRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ManageTripUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static ManageTripUseCase_Factory create(Provider<FisherRepository> repositoryProvider) {
    return new ManageTripUseCase_Factory(repositoryProvider);
  }

  public static ManageTripUseCase newInstance(FisherRepository repository) {
    return new ManageTripUseCase(repository);
  }
}
