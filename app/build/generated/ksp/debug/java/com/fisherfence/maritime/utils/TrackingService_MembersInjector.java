package com.fisherfence.maritime.utils;

import com.fisherfence.maritime.domain.repository.FisherRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class TrackingService_MembersInjector implements MembersInjector<TrackingService> {
  private final Provider<FisherRepository> repositoryProvider;

  public TrackingService_MembersInjector(Provider<FisherRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  public static MembersInjector<TrackingService> create(
      Provider<FisherRepository> repositoryProvider) {
    return new TrackingService_MembersInjector(repositoryProvider);
  }

  @Override
  public void injectMembers(TrackingService instance) {
    injectRepository(instance, repositoryProvider.get());
  }

  @InjectedFieldSignature("com.fisherfence.maritime.utils.TrackingService.repository")
  public static void injectRepository(TrackingService instance, FisherRepository repository) {
    instance.repository = repository;
  }
}
