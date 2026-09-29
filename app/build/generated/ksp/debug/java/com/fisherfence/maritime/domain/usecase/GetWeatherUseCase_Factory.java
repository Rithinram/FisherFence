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
public final class GetWeatherUseCase_Factory implements Factory<GetWeatherUseCase> {
  private final Provider<FisherRepository> repositoryProvider;

  public GetWeatherUseCase_Factory(Provider<FisherRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetWeatherUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetWeatherUseCase_Factory create(Provider<FisherRepository> repositoryProvider) {
    return new GetWeatherUseCase_Factory(repositoryProvider);
  }

  public static GetWeatherUseCase newInstance(FisherRepository repository) {
    return new GetWeatherUseCase(repository);
  }
}
