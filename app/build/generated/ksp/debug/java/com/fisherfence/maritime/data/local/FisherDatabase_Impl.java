package com.fisherfence.maritime.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class FisherDatabase_Impl extends FisherDatabase {
  private volatile LocationDao _locationDao;

  private volatile AlertDao _alertDao;

  private volatile TripDao _tripDao;

  private volatile WeatherDao _weatherDao;

  private volatile ChecklistDao _checklistDao;

  private volatile NotificationDao _notificationDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `fisherman_locations` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `fishermanId` TEXT NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `speed` REAL NOT NULL, `timestamp` INTEGER NOT NULL, `sessionId` TEXT NOT NULL, `syncedToCloud` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `alert_events` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `fishermanId` TEXT NOT NULL, `alertType` TEXT NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `timestamp` INTEGER NOT NULL, `status` TEXT NOT NULL, `message` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `trip_sessions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `fishermanId` TEXT NOT NULL, `startTime` INTEGER NOT NULL, `endTime` INTEGER NOT NULL, `distanceCovered` REAL NOT NULL, `alertCount` INTEGER NOT NULL, `status` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `weather_data` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `windSpeed` REAL NOT NULL, `waveHeight` REAL NOT NULL, `zone` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `advisory` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `checklist_items` (`id` INTEGER NOT NULL, `title` TEXT NOT NULL, `isChecked` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `notifications` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `message` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `isRead` INTEGER NOT NULL, `role` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'e9e4a93932daeca4a90828056a6acef5')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `fisherman_locations`");
        db.execSQL("DROP TABLE IF EXISTS `alert_events`");
        db.execSQL("DROP TABLE IF EXISTS `trip_sessions`");
        db.execSQL("DROP TABLE IF EXISTS `weather_data`");
        db.execSQL("DROP TABLE IF EXISTS `checklist_items`");
        db.execSQL("DROP TABLE IF EXISTS `notifications`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsFishermanLocations = new HashMap<String, TableInfo.Column>(8);
        _columnsFishermanLocations.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFishermanLocations.put("fishermanId", new TableInfo.Column("fishermanId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFishermanLocations.put("latitude", new TableInfo.Column("latitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFishermanLocations.put("longitude", new TableInfo.Column("longitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFishermanLocations.put("speed", new TableInfo.Column("speed", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFishermanLocations.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFishermanLocations.put("sessionId", new TableInfo.Column("sessionId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFishermanLocations.put("syncedToCloud", new TableInfo.Column("syncedToCloud", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFishermanLocations = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFishermanLocations = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoFishermanLocations = new TableInfo("fisherman_locations", _columnsFishermanLocations, _foreignKeysFishermanLocations, _indicesFishermanLocations);
        final TableInfo _existingFishermanLocations = TableInfo.read(db, "fisherman_locations");
        if (!_infoFishermanLocations.equals(_existingFishermanLocations)) {
          return new RoomOpenHelper.ValidationResult(false, "fisherman_locations(com.fisherfence.maritime.data.local.FishermanLocation).\n"
                  + " Expected:\n" + _infoFishermanLocations + "\n"
                  + " Found:\n" + _existingFishermanLocations);
        }
        final HashMap<String, TableInfo.Column> _columnsAlertEvents = new HashMap<String, TableInfo.Column>(8);
        _columnsAlertEvents.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertEvents.put("fishermanId", new TableInfo.Column("fishermanId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertEvents.put("alertType", new TableInfo.Column("alertType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertEvents.put("latitude", new TableInfo.Column("latitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertEvents.put("longitude", new TableInfo.Column("longitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertEvents.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertEvents.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertEvents.put("message", new TableInfo.Column("message", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAlertEvents = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAlertEvents = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAlertEvents = new TableInfo("alert_events", _columnsAlertEvents, _foreignKeysAlertEvents, _indicesAlertEvents);
        final TableInfo _existingAlertEvents = TableInfo.read(db, "alert_events");
        if (!_infoAlertEvents.equals(_existingAlertEvents)) {
          return new RoomOpenHelper.ValidationResult(false, "alert_events(com.fisherfence.maritime.data.local.AlertEvent).\n"
                  + " Expected:\n" + _infoAlertEvents + "\n"
                  + " Found:\n" + _existingAlertEvents);
        }
        final HashMap<String, TableInfo.Column> _columnsTripSessions = new HashMap<String, TableInfo.Column>(7);
        _columnsTripSessions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTripSessions.put("fishermanId", new TableInfo.Column("fishermanId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTripSessions.put("startTime", new TableInfo.Column("startTime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTripSessions.put("endTime", new TableInfo.Column("endTime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTripSessions.put("distanceCovered", new TableInfo.Column("distanceCovered", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTripSessions.put("alertCount", new TableInfo.Column("alertCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTripSessions.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTripSessions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTripSessions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTripSessions = new TableInfo("trip_sessions", _columnsTripSessions, _foreignKeysTripSessions, _indicesTripSessions);
        final TableInfo _existingTripSessions = TableInfo.read(db, "trip_sessions");
        if (!_infoTripSessions.equals(_existingTripSessions)) {
          return new RoomOpenHelper.ValidationResult(false, "trip_sessions(com.fisherfence.maritime.data.local.TripSession).\n"
                  + " Expected:\n" + _infoTripSessions + "\n"
                  + " Found:\n" + _existingTripSessions);
        }
        final HashMap<String, TableInfo.Column> _columnsWeatherData = new HashMap<String, TableInfo.Column>(8);
        _columnsWeatherData.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWeatherData.put("latitude", new TableInfo.Column("latitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWeatherData.put("longitude", new TableInfo.Column("longitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWeatherData.put("windSpeed", new TableInfo.Column("windSpeed", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWeatherData.put("waveHeight", new TableInfo.Column("waveHeight", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWeatherData.put("zone", new TableInfo.Column("zone", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWeatherData.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWeatherData.put("advisory", new TableInfo.Column("advisory", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWeatherData = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesWeatherData = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoWeatherData = new TableInfo("weather_data", _columnsWeatherData, _foreignKeysWeatherData, _indicesWeatherData);
        final TableInfo _existingWeatherData = TableInfo.read(db, "weather_data");
        if (!_infoWeatherData.equals(_existingWeatherData)) {
          return new RoomOpenHelper.ValidationResult(false, "weather_data(com.fisherfence.maritime.data.local.WeatherData).\n"
                  + " Expected:\n" + _infoWeatherData + "\n"
                  + " Found:\n" + _existingWeatherData);
        }
        final HashMap<String, TableInfo.Column> _columnsChecklistItems = new HashMap<String, TableInfo.Column>(3);
        _columnsChecklistItems.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChecklistItems.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChecklistItems.put("isChecked", new TableInfo.Column("isChecked", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysChecklistItems = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesChecklistItems = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoChecklistItems = new TableInfo("checklist_items", _columnsChecklistItems, _foreignKeysChecklistItems, _indicesChecklistItems);
        final TableInfo _existingChecklistItems = TableInfo.read(db, "checklist_items");
        if (!_infoChecklistItems.equals(_existingChecklistItems)) {
          return new RoomOpenHelper.ValidationResult(false, "checklist_items(com.fisherfence.maritime.data.local.ChecklistItem).\n"
                  + " Expected:\n" + _infoChecklistItems + "\n"
                  + " Found:\n" + _existingChecklistItems);
        }
        final HashMap<String, TableInfo.Column> _columnsNotifications = new HashMap<String, TableInfo.Column>(6);
        _columnsNotifications.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("message", new TableInfo.Column("message", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("isRead", new TableInfo.Column("isRead", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("role", new TableInfo.Column("role", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysNotifications = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesNotifications = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoNotifications = new TableInfo("notifications", _columnsNotifications, _foreignKeysNotifications, _indicesNotifications);
        final TableInfo _existingNotifications = TableInfo.read(db, "notifications");
        if (!_infoNotifications.equals(_existingNotifications)) {
          return new RoomOpenHelper.ValidationResult(false, "notifications(com.fisherfence.maritime.data.local.SystemNotification).\n"
                  + " Expected:\n" + _infoNotifications + "\n"
                  + " Found:\n" + _existingNotifications);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "e9e4a93932daeca4a90828056a6acef5", "958e656ae336e7807f5cc6b8e95583eb");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "fisherman_locations","alert_events","trip_sessions","weather_data","checklist_items","notifications");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `fisherman_locations`");
      _db.execSQL("DELETE FROM `alert_events`");
      _db.execSQL("DELETE FROM `trip_sessions`");
      _db.execSQL("DELETE FROM `weather_data`");
      _db.execSQL("DELETE FROM `checklist_items`");
      _db.execSQL("DELETE FROM `notifications`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(LocationDao.class, LocationDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AlertDao.class, AlertDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TripDao.class, TripDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(WeatherDao.class, WeatherDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ChecklistDao.class, ChecklistDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(NotificationDao.class, NotificationDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public LocationDao locationDao() {
    if (_locationDao != null) {
      return _locationDao;
    } else {
      synchronized(this) {
        if(_locationDao == null) {
          _locationDao = new LocationDao_Impl(this);
        }
        return _locationDao;
      }
    }
  }

  @Override
  public AlertDao alertDao() {
    if (_alertDao != null) {
      return _alertDao;
    } else {
      synchronized(this) {
        if(_alertDao == null) {
          _alertDao = new AlertDao_Impl(this);
        }
        return _alertDao;
      }
    }
  }

  @Override
  public TripDao tripDao() {
    if (_tripDao != null) {
      return _tripDao;
    } else {
      synchronized(this) {
        if(_tripDao == null) {
          _tripDao = new TripDao_Impl(this);
        }
        return _tripDao;
      }
    }
  }

  @Override
  public WeatherDao weatherDao() {
    if (_weatherDao != null) {
      return _weatherDao;
    } else {
      synchronized(this) {
        if(_weatherDao == null) {
          _weatherDao = new WeatherDao_Impl(this);
        }
        return _weatherDao;
      }
    }
  }

  @Override
  public ChecklistDao checklistDao() {
    if (_checklistDao != null) {
      return _checklistDao;
    } else {
      synchronized(this) {
        if(_checklistDao == null) {
          _checklistDao = new ChecklistDao_Impl(this);
        }
        return _checklistDao;
      }
    }
  }

  @Override
  public NotificationDao notificationDao() {
    if (_notificationDao != null) {
      return _notificationDao;
    } else {
      synchronized(this) {
        if(_notificationDao == null) {
          _notificationDao = new NotificationDao_Impl(this);
        }
        return _notificationDao;
      }
    }
  }
}
