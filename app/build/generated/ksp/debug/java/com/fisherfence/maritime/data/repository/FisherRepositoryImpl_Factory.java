package com.fisherfence.maritime.data.repository;

import android.content.Context;
import com.fisherfence.maritime.data.local.FisherDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class FisherRepositoryImpl_Factory implements Factory<FisherRepositoryImpl> {
  private final Provider<Context> contextProvider;

  private final Provider<FisherDatabase> dbProvider;

  public FisherRepositoryImpl_Factory(Provider<Context> contextProvider,
      Provider<FisherDatabase> dbProvider) {
    this.contextProvider = contextProvider;
    this.dbProvider = dbProvider;
  }

  @Override
  public FisherRepositoryImpl get() {
    return newInstance(contextProvider.get(), dbProvider.get());
  }

  public static FisherRepositoryImpl_Factory create(Provider<Context> contextProvider,
      Provider<FisherDatabase> dbProvider) {
    return new FisherRepositoryImpl_Factory(contextProvider, dbProvider);
  }

  public static FisherRepositoryImpl newInstance(Context context, FisherDatabase db) {
    return new FisherRepositoryImpl(context, db);
  }
}
