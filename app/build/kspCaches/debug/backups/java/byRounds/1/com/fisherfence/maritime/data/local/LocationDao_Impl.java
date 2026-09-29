package com.fisherfence.maritime.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
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
public final class LocationDao_Impl implements LocationDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<FishermanLocation> __insertionAdapterOfFishermanLocation;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public LocationDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfFishermanLocation = new EntityInsertionAdapter<FishermanLocation>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `fisherman_locations` (`id`,`fishermanId`,`latitude`,`longitude`,`speed`,`timestamp`,`sessionId`,`syncedToCloud`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FishermanLocation entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getFishermanId());
        statement.bindDouble(3, entity.getLatitude());
        statement.bindDouble(4, entity.getLongitude());
        statement.bindDouble(5, entity.getSpeed());
        statement.bindLong(6, entity.getTimestamp());
        statement.bindString(7, entity.getSessionId());
        final int _tmp = entity.getSyncedToCloud() ? 1 : 0;
        statement.bindLong(8, _tmp);
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM fisherman_locations";
        return _query;
      }
    };
  }

  @Override
  public Object insertLocation(final FishermanLocation location,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfFishermanLocation.insertAndReturnId(location);
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
  public Flow<List<FishermanLocation>> getLocationsForSession(final String sessionId) {
    final String _sql = "SELECT * FROM fisherman_locations WHERE sessionId = ? ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, sessionId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"fisherman_locations"}, new Callable<List<FishermanLocation>>() {
      @Override
      @NonNull
      public List<FishermanLocation> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFishermanId = CursorUtil.getColumnIndexOrThrow(_cursor, "fishermanId");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfSpeed = CursorUtil.getColumnIndexOrThrow(_cursor, "speed");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
          final int _cursorIndexOfSyncedToCloud = CursorUtil.getColumnIndexOrThrow(_cursor, "syncedToCloud");
          final List<FishermanLocation> _result = new ArrayList<FishermanLocation>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FishermanLocation _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpFishermanId;
            _tmpFishermanId = _cursor.getString(_cursorIndexOfFishermanId);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final double _tmpSpeed;
            _tmpSpeed = _cursor.getDouble(_cursorIndexOfSpeed);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpSessionId;
            _tmpSessionId = _cursor.getString(_cursorIndexOfSessionId);
            final boolean _tmpSyncedToCloud;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfSyncedToCloud);
            _tmpSyncedToCloud = _tmp != 0;
            _item = new FishermanLocation(_tmpId,_tmpFishermanId,_tmpLatitude,_tmpLongitude,_tmpSpeed,_tmpTimestamp,_tmpSessionId,_tmpSyncedToCloud);
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
  public Flow<FishermanLocation> getLatestLocation() {
    final String _sql = "SELECT * FROM fisherman_locations ORDER BY timestamp DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"fisherman_locations"}, new Callable<FishermanLocation>() {
      @Override
      @Nullable
      public FishermanLocation call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFishermanId = CursorUtil.getColumnIndexOrThrow(_cursor, "fishermanId");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfSpeed = CursorUtil.getColumnIndexOrThrow(_cursor, "speed");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
          final int _cursorIndexOfSyncedToCloud = CursorUtil.getColumnIndexOrThrow(_cursor, "syncedToCloud");
          final FishermanLocation _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpFishermanId;
            _tmpFishermanId = _cursor.getString(_cursorIndexOfFishermanId);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final double _tmpSpeed;
            _tmpSpeed = _cursor.getDouble(_cursorIndexOfSpeed);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpSessionId;
            _tmpSessionId = _cursor.getString(_cursorIndexOfSessionId);
            final boolean _tmpSyncedToCloud;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfSyncedToCloud);
            _tmpSyncedToCloud = _tmp != 0;
            _result = new FishermanLocation(_tmpId,_tmpFishermanId,_tmpLatitude,_tmpLongitude,_tmpSpeed,_tmpTimestamp,_tmpSessionId,_tmpSyncedToCloud);
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

  @Override
  public Object getLatestLocationSync(final Continuation<? super FishermanLocation> $completion) {
    final String _sql = "SELECT * FROM fisherman_locations ORDER BY timestamp DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<FishermanLocation>() {
      @Override
      @Nullable
      public FishermanLocation call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFishermanId = CursorUtil.getColumnIndexOrThrow(_cursor, "fishermanId");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfSpeed = CursorUtil.getColumnIndexOrThrow(_cursor, "speed");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
          final int _cursorIndexOfSyncedToCloud = CursorUtil.getColumnIndexOrThrow(_cursor, "syncedToCloud");
          final FishermanLocation _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpFishermanId;
            _tmpFishermanId = _cursor.getString(_cursorIndexOfFishermanId);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final double _tmpSpeed;
            _tmpSpeed = _cursor.getDouble(_cursorIndexOfSpeed);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpSessionId;
            _tmpSessionId = _cursor.getString(_cursorIndexOfSessionId);
            final boolean _tmpSyncedToCloud;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfSyncedToCloud);
            _tmpSyncedToCloud = _tmp != 0;
            _result = new FishermanLocation(_tmpId,_tmpFishermanId,_tmpLatitude,_tmpLongitude,_tmpSpeed,_tmpTimestamp,_tmpSessionId,_tmpSyncedToCloud);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
