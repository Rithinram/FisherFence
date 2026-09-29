package com.fisherfence.maritime.data.mock;

import com.fisherfence.maritime.data.local.FisherDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class DatabaseSeeder_Factory implements Factory<DatabaseSeeder> {
  private final Provider<FisherDatabase> dbProvider;

  public DatabaseSeeder_Factory(Provider<FisherDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public DatabaseSeeder get() {
    return newInstance(dbProvider.get());
  }

  public static DatabaseSeeder_Factory create(Provider<FisherDatabase> dbProvider) {
    return new DatabaseSeeder_Factory(dbProvider);
  }

  public static DatabaseSeeder newInstance(FisherDatabase db) {
    return new DatabaseSeeder(db);
  }
}
