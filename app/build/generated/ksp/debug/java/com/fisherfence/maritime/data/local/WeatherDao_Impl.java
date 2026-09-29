package com.fisherfence.maritime.data.local;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class WeatherDao_Impl implements WeatherDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<WeatherData> __insertionAdapterOfWeatherData;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public WeatherDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWeatherData = new EntityInsertionAdapter<WeatherData>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `weather_data` (`id`,`latitude`,`longitude`,`windSpeed`,`waveHeight`,`zone`,`timestamp`,`advisory`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WeatherData entity) {
        statement.bindLong(1, entity.getId());
        statement.bindDouble(2, entity.getLatitude());
        statement.bindDouble(3, entity.getLongitude());
        statement.bindDouble(4, entity.getWindSpeed());
        statement.bindDouble(5, entity.getWaveHeight());
        statement.bindString(6, entity.getZone());
        statement.bindLong(7, entity.getTimestamp());
        statement.bindString(8, entity.getAdvisory());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM weather_data";
        return _query;
      }
    };
  }

  @Override
  public Object insertWeatherData(final WeatherData weather,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfWeatherData.insertAndReturnId(weather);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<WeatherData>> getForecast() {
    final String _sql = "SELECT * FROM weather_data ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"weather_data"}, new Callable<List<WeatherData>>() {
      @Override
      @NonNull
      public List<WeatherData> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfWindSpeed = CursorUtil.getColumnIndexOrThrow(_cursor, "windSpeed");
          final int _cursorIndexOfWaveHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "waveHeight");
          final int _cursorIndexOfZone = CursorUtil.getColumnIndexOrThrow(_cursor, "zone");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfAdvisory = CursorUtil.getColumnIndexOrThrow(_cursor, "advisory");
          final List<WeatherData> _result = new ArrayList<WeatherData>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WeatherData _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final double _tmpWindSpeed;
            _tmpWindSpeed = _cursor.getDouble(_cursorIndexOfWindSpeed);
            final double _tmpWaveHeight;
            _tmpWaveHeight = _cursor.getDouble(_cursorIndexOfWaveHeight);
            final String _tmpZone;
            _tmpZone = _cursor.getString(_cursorIndexOfZone);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpAdvisory;
            _tmpAdvisory = _cursor.getString(_cursorIndexOfAdvisory);
            _item = new WeatherData(_tmpId,_tmpLatitude,_tmpLongitude,_tmpWindSpeed,_tmpWaveHeight,_tmpZone,_tmpTimestamp,_tmpAdvisory);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<WeatherData> getLatestWeather() {
    final String _sql = "SELECT * FROM weather_data ORDER BY timestamp DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"weather_data"}, new Callable<WeatherData>() {
      @Override
      @Nullable
      public WeatherData call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfWindSpeed = CursorUtil.getColumnIndexOrThrow(_cursor, "windSpeed");
          final int _cursorIndexOfWaveHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "waveHeight");
          final int _cursorIndexOfZone = CursorUtil.getColumnIndexOrThrow(_cursor, "zone");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfAdvisory = CursorUtil.getColumnIndexOrThrow(_cursor, "advisory");
          final WeatherData _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final double _tmpWindSpeed;
            _tmpWindSpeed = _cursor.getDouble(_cursorIndexOfWindSpeed);
            final double _tmpWaveHeight;
            _tmpWaveHeight = _cursor.getDouble(_cursorIndexOfWaveHeight);
            final String _tmpZone;
            _tmpZone = _cursor.getString(_cursorIndexOfZone);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpAdvisory;
            _tmpAdvisory = _cursor.getString(_cursorIndexOfAdvisory);
            _result = new WeatherData(_tmpId,_tmpLatitude,_tmpLongitude,_tmpWindSpeed,_tmpWaveHeight,_tmpZone,_tmpTimestamp,_tmpAdvisory);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
