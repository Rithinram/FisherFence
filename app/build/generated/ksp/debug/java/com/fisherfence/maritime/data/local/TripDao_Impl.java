package com.fisherfence.maritime.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
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
public final class TripDao_Impl implements TripDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TripSession> __insertionAdapterOfTripSession;

  private final EntityDeletionOrUpdateAdapter<TripSession> __updateAdapterOfTripSession;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public TripDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTripSession = new EntityInsertionAdapter<TripSession>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `trip_sessions` (`id`,`fishermanId`,`startTime`,`endTime`,`distanceCovered`,`alertCount`,`status`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TripSession entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getFishermanId());
        statement.bindLong(3, entity.getStartTime());
        statement.bindLong(4, entity.getEndTime());
        statement.bindDouble(5, entity.getDistanceCovered());
        statement.bindLong(6, entity.getAlertCount());
        statement.bindString(7, entity.getStatus());
      }
    };
    this.__updateAdapterOfTripSession = new EntityDeletionOrUpdateAdapter<TripSession>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `trip_sessions` SET `id` = ?,`fishermanId` = ?,`startTime` = ?,`endTime` = ?,`distanceCovered` = ?,`alertCount` = ?,`status` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TripSession entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getFishermanId());
        statement.bindLong(3, entity.getStartTime());
        statement.bindLong(4, entity.getEndTime());
        statement.bindDouble(5, entity.getDistanceCovered());
        statement.bindLong(6, entity.getAlertCount());
        statement.bindString(7, entity.getStatus());
        statement.bindLong(8, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM trip_sessions";
        return _query;
      }
    };
  }

  @Override
  public Object insertTrip(final TripSession trip, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfTripSession.insertAndReturnId(trip);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateTrip(final TripSession trip, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTripSession.handle(trip);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
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
  public Object getActiveTrip(final Continuation<? super TripSession> $completion) {
    final String _sql = "SELECT * FROM trip_sessions WHERE status = 'ACTIVE' LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TripSession>() {
      @Override
      @Nullable
      public TripSession call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFishermanId = CursorUtil.getColumnIndexOrThrow(_cursor, "fishermanId");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
          final int _cursorIndexOfDistanceCovered = CursorUtil.getColumnIndexOrThrow(_cursor, "distanceCovered");
          final int _cursorIndexOfAlertCount = CursorUtil.getColumnIndexOrThrow(_cursor, "alertCount");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final TripSession _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpFishermanId;
            _tmpFishermanId = _cursor.getString(_cursorIndexOfFishermanId);
            final long _tmpStartTime;
            _tmpStartTime = _cursor.getLong(_cursorIndexOfStartTime);
            final long _tmpEndTime;
            _tmpEndTime = _cursor.getLong(_cursorIndexOfEndTime);
            final double _tmpDistanceCovered;
            _tmpDistanceCovered = _cursor.getDouble(_cursorIndexOfDistanceCovered);
            final int _tmpAlertCount;
            _tmpAlertCount = _cursor.getInt(_cursorIndexOfAlertCount);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            _result = new TripSession(_tmpId,_tmpFishermanId,_tmpStartTime,_tmpEndTime,_tmpDistanceCovered,_tmpAlertCount,_tmpStatus);
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

  @Override
  public Flow<TripSession> getActiveTripFlow() {
    final String _sql = "SELECT * FROM trip_sessions WHERE status = 'ACTIVE' LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"trip_sessions"}, new Callable<TripSession>() {
      @Override
      @Nullable
      public TripSession call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFishermanId = CursorUtil.getColumnIndexOrThrow(_cursor, "fishermanId");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
          final int _cursorIndexOfDistanceCovered = CursorUtil.getColumnIndexOrThrow(_cursor, "distanceCovered");
          final int _cursorIndexOfAlertCount = CursorUtil.getColumnIndexOrThrow(_cursor, "alertCount");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final TripSession _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpFishermanId;
            _tmpFishermanId = _cursor.getString(_cursorIndexOfFishermanId);
            final long _tmpStartTime;
            _tmpStartTime = _cursor.getLong(_cursorIndexOfStartTime);
            final long _tmpEndTime;
            _tmpEndTime = _cursor.getLong(_cursorIndexOfEndTime);
            final double _tmpDistanceCovered;
            _tmpDistanceCovered = _cursor.getDouble(_cursorIndexOfDistanceCovered);
            final int _tmpAlertCount;
            _tmpAlertCount = _cursor.getInt(_cursorIndexOfAlertCount);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            _result = new TripSession(_tmpId,_tmpFishermanId,_tmpStartTime,_tmpEndTime,_tmpDistanceCovered,_tmpAlertCount,_tmpStatus);
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
  public Flow<List<TripSession>> getPastTrips() {
    final String _sql = "SELECT * FROM trip_sessions WHERE status = 'COMPLETED' ORDER BY startTime DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"trip_sessions"}, new Callable<List<TripSession>>() {
      @Override
      @NonNull
      public List<TripSession> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFishermanId = CursorUtil.getColumnIndexOrThrow(_cursor, "fishermanId");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
          final int _cursorIndexOfDistanceCovered = CursorUtil.getColumnIndexOrThrow(_cursor, "distanceCovered");
          final int _cursorIndexOfAlertCount = CursorUtil.getColumnIndexOrThrow(_cursor, "alertCount");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final List<TripSession> _result = new ArrayList<TripSession>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TripSession _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpFishermanId;
            _tmpFishermanId = _cursor.getString(_cursorIndexOfFishermanId);
            final long _tmpStartTime;
            _tmpStartTime = _cursor.getLong(_cursorIndexOfStartTime);
            final long _tmpEndTime;
            _tmpEndTime = _cursor.getLong(_cursorIndexOfEndTime);
            final double _tmpDistanceCovered;
            _tmpDistanceCovered = _cursor.getDouble(_cursorIndexOfDistanceCovered);
            final int _tmpAlertCount;
            _tmpAlertCount = _cursor.getInt(_cursorIndexOfAlertCount);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            _item = new TripSession(_tmpId,_tmpFishermanId,_tmpStartTime,_tmpEndTime,_tmpDistanceCovered,_tmpAlertCount,_tmpStatus);
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
  public Flow<List<TripSession>> getAllTrips() {
    final String _sql = "SELECT * FROM trip_sessions ORDER BY startTime DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"trip_sessions"}, new Callable<List<TripSession>>() {
      @Override
      @NonNull
      public List<TripSession> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFishermanId = CursorUtil.getColumnIndexOrThrow(_cursor, "fishermanId");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
          final int _cursorIndexOfDistanceCovered = CursorUtil.getColumnIndexOrThrow(_cursor, "distanceCovered");
          final int _cursorIndexOfAlertCount = CursorUtil.getColumnIndexOrThrow(_cursor, "alertCount");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final List<TripSession> _result = new ArrayList<TripSession>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TripSession _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpFishermanId;
            _tmpFishermanId = _cursor.getString(_cursorIndexOfFishermanId);
            final long _tmpStartTime;
            _tmpStartTime = _cursor.getLong(_cursorIndexOfStartTime);
            final long _tmpEndTime;
            _tmpEndTime = _cursor.getLong(_cursorIndexOfEndTime);
            final double _tmpDistanceCovered;
            _tmpDistanceCovered = _cursor.getDouble(_cursorIndexOfDistanceCovered);
            final int _tmpAlertCount;
            _tmpAlertCount = _cursor.getInt(_cursorIndexOfAlertCount);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            _item = new TripSession(_tmpId,_tmpFishermanId,_tmpStartTime,_tmpEndTime,_tmpDistanceCovered,_tmpAlertCount,_tmpStatus);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
