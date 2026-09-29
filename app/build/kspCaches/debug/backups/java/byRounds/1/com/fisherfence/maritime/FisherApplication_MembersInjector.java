package com.fisherfence.maritime;

import com.fisherfence.maritime.data.mock.DatabaseSeeder;
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
public final class FisherApplication_MembersInjector implements MembersInjector<FisherApplication> {
  private final Provider<DatabaseSeeder> databaseSeederProvider;

  public FisherApplication_MembersInjector(Provider<DatabaseSeeder> databaseSeederProvider) {
    this.databaseSeederProvider = databaseSeederProvider;
  }

  public static MembersInjector<FisherApplication> create(
      Provider<DatabaseSeeder> databaseSeederProvider) {
    return new FisherApplication_MembersInjector(databaseSeederProvider);
  }

  @Override
  public void injectMembers(FisherApplication instance) {
    injectDatabaseSeeder(instance, databaseSeederProvider.get());
  }

  @InjectedFieldSignature("com.fisherfence.maritime.FisherApplication.databaseSeeder")
  public static void injectDatabaseSeeder(FisherApplication instance,
      DatabaseSeeder databaseSeeder) {
    instance.databaseSeeder = databaseSeeder;
  }
}
