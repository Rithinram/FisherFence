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
public final class GetAlertsUseCase_Factory implements Factory<GetAlertsUseCase> {
  private final Provider<FisherRepository> repositoryProvider;

  public GetAlertsUseCase_Factory(Provider<FisherRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetAlertsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetAlertsUseCase_Factory create(Provider<FisherRepository> repositoryProvider) {
    return new GetAlertsUseCase_Factory(repositoryProvider);
  }

  public static GetAlertsUseCase newInstance(FisherRepository repository) {
    return new GetAlertsUseCase(repository);
  }
}
