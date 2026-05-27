package com.ukdw.pplaicoach.data.local;

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
import java.lang.Double;
import java.lang.Exception;
import java.lang.Integer;
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
public final class WorkoutSessionDao_Impl implements WorkoutSessionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<WorkoutSessionEntity> __insertionAdapterOfWorkoutSessionEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteSession;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllSessions;

  public WorkoutSessionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWorkoutSessionEntity = new EntityInsertionAdapter<WorkoutSessionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `workout_sessions` (`id`,`timestamp`,`sleepDuration`,`muscleSoreness`,`userGoal`,`prevPerformance`,`loadAdjustment`,`volumeAdjustment`,`recommendation`,`activeRules`,`explanationText`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WorkoutSessionEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getTimestamp());
        statement.bindDouble(3, entity.getSleepDuration());
        statement.bindString(4, entity.getMuscleSoreness());
        statement.bindString(5, entity.getUserGoal());
        statement.bindString(6, entity.getPrevPerformance());
        statement.bindLong(7, entity.getLoadAdjustment());
        statement.bindLong(8, entity.getVolumeAdjustment());
        statement.bindString(9, entity.getRecommendation());
        statement.bindString(10, entity.getActiveRules());
        statement.bindString(11, entity.getExplanationText());
      }
    };
    this.__preparedStmtOfDeleteSession = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM workout_sessions WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllSessions = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM workout_sessions";
        return _query;
      }
    };
  }

  @Override
  public Object insertSession(final WorkoutSessionEntity session,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfWorkoutSessionEntity.insert(session);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteSession(final int sessionId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteSession.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, sessionId);
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
          __preparedStmtOfDeleteSession.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllSessions(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllSessions.acquire();
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
          __preparedStmtOfDeleteAllSessions.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<WorkoutSessionEntity>> getAllSessions() {
    final String _sql = "SELECT * FROM workout_sessions ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"workout_sessions"}, new Callable<List<WorkoutSessionEntity>>() {
      @Override
      @NonNull
      public List<WorkoutSessionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfSleepDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepDuration");
          final int _cursorIndexOfMuscleSoreness = CursorUtil.getColumnIndexOrThrow(_cursor, "muscleSoreness");
          final int _cursorIndexOfUserGoal = CursorUtil.getColumnIndexOrThrow(_cursor, "userGoal");
          final int _cursorIndexOfPrevPerformance = CursorUtil.getColumnIndexOrThrow(_cursor, "prevPerformance");
          final int _cursorIndexOfLoadAdjustment = CursorUtil.getColumnIndexOrThrow(_cursor, "loadAdjustment");
          final int _cursorIndexOfVolumeAdjustment = CursorUtil.getColumnIndexOrThrow(_cursor, "volumeAdjustment");
          final int _cursorIndexOfRecommendation = CursorUtil.getColumnIndexOrThrow(_cursor, "recommendation");
          final int _cursorIndexOfActiveRules = CursorUtil.getColumnIndexOrThrow(_cursor, "activeRules");
          final int _cursorIndexOfExplanationText = CursorUtil.getColumnIndexOrThrow(_cursor, "explanationText");
          final List<WorkoutSessionEntity> _result = new ArrayList<WorkoutSessionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WorkoutSessionEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final double _tmpSleepDuration;
            _tmpSleepDuration = _cursor.getDouble(_cursorIndexOfSleepDuration);
            final String _tmpMuscleSoreness;
            _tmpMuscleSoreness = _cursor.getString(_cursorIndexOfMuscleSoreness);
            final String _tmpUserGoal;
            _tmpUserGoal = _cursor.getString(_cursorIndexOfUserGoal);
            final String _tmpPrevPerformance;
            _tmpPrevPerformance = _cursor.getString(_cursorIndexOfPrevPerformance);
            final int _tmpLoadAdjustment;
            _tmpLoadAdjustment = _cursor.getInt(_cursorIndexOfLoadAdjustment);
            final int _tmpVolumeAdjustment;
            _tmpVolumeAdjustment = _cursor.getInt(_cursorIndexOfVolumeAdjustment);
            final String _tmpRecommendation;
            _tmpRecommendation = _cursor.getString(_cursorIndexOfRecommendation);
            final String _tmpActiveRules;
            _tmpActiveRules = _cursor.getString(_cursorIndexOfActiveRules);
            final String _tmpExplanationText;
            _tmpExplanationText = _cursor.getString(_cursorIndexOfExplanationText);
            _item = new WorkoutSessionEntity(_tmpId,_tmpTimestamp,_tmpSleepDuration,_tmpMuscleSoreness,_tmpUserGoal,_tmpPrevPerformance,_tmpLoadAdjustment,_tmpVolumeAdjustment,_tmpRecommendation,_tmpActiveRules,_tmpExplanationText);
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
  public Flow<List<WorkoutSessionEntity>> getRecentSessions(final int limit) {
    final String _sql = "SELECT * FROM workout_sessions ORDER BY timestamp DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"workout_sessions"}, new Callable<List<WorkoutSessionEntity>>() {
      @Override
      @NonNull
      public List<WorkoutSessionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfSleepDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepDuration");
          final int _cursorIndexOfMuscleSoreness = CursorUtil.getColumnIndexOrThrow(_cursor, "muscleSoreness");
          final int _cursorIndexOfUserGoal = CursorUtil.getColumnIndexOrThrow(_cursor, "userGoal");
          final int _cursorIndexOfPrevPerformance = CursorUtil.getColumnIndexOrThrow(_cursor, "prevPerformance");
          final int _cursorIndexOfLoadAdjustment = CursorUtil.getColumnIndexOrThrow(_cursor, "loadAdjustment");
          final int _cursorIndexOfVolumeAdjustment = CursorUtil.getColumnIndexOrThrow(_cursor, "volumeAdjustment");
          final int _cursorIndexOfRecommendation = CursorUtil.getColumnIndexOrThrow(_cursor, "recommendation");
          final int _cursorIndexOfActiveRules = CursorUtil.getColumnIndexOrThrow(_cursor, "activeRules");
          final int _cursorIndexOfExplanationText = CursorUtil.getColumnIndexOrThrow(_cursor, "explanationText");
          final List<WorkoutSessionEntity> _result = new ArrayList<WorkoutSessionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WorkoutSessionEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final double _tmpSleepDuration;
            _tmpSleepDuration = _cursor.getDouble(_cursorIndexOfSleepDuration);
            final String _tmpMuscleSoreness;
            _tmpMuscleSoreness = _cursor.getString(_cursorIndexOfMuscleSoreness);
            final String _tmpUserGoal;
            _tmpUserGoal = _cursor.getString(_cursorIndexOfUserGoal);
            final String _tmpPrevPerformance;
            _tmpPrevPerformance = _cursor.getString(_cursorIndexOfPrevPerformance);
            final int _tmpLoadAdjustment;
            _tmpLoadAdjustment = _cursor.getInt(_cursorIndexOfLoadAdjustment);
            final int _tmpVolumeAdjustment;
            _tmpVolumeAdjustment = _cursor.getInt(_cursorIndexOfVolumeAdjustment);
            final String _tmpRecommendation;
            _tmpRecommendation = _cursor.getString(_cursorIndexOfRecommendation);
            final String _tmpActiveRules;
            _tmpActiveRules = _cursor.getString(_cursorIndexOfActiveRules);
            final String _tmpExplanationText;
            _tmpExplanationText = _cursor.getString(_cursorIndexOfExplanationText);
            _item = new WorkoutSessionEntity(_tmpId,_tmpTimestamp,_tmpSleepDuration,_tmpMuscleSoreness,_tmpUserGoal,_tmpPrevPerformance,_tmpLoadAdjustment,_tmpVolumeAdjustment,_tmpRecommendation,_tmpActiveRules,_tmpExplanationText);
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
  public Flow<Integer> getSessionCount() {
    final String _sql = "SELECT COUNT(*) FROM workout_sessions";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"workout_sessions"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
  public Flow<List<WorkoutSessionEntity>> getSessionsByRecommendation(final String type) {
    final String _sql = "SELECT * FROM workout_sessions WHERE recommendation = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, type);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"workout_sessions"}, new Callable<List<WorkoutSessionEntity>>() {
      @Override
      @NonNull
      public List<WorkoutSessionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfSleepDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepDuration");
          final int _cursorIndexOfMuscleSoreness = CursorUtil.getColumnIndexOrThrow(_cursor, "muscleSoreness");
          final int _cursorIndexOfUserGoal = CursorUtil.getColumnIndexOrThrow(_cursor, "userGoal");
          final int _cursorIndexOfPrevPerformance = CursorUtil.getColumnIndexOrThrow(_cursor, "prevPerformance");
          final int _cursorIndexOfLoadAdjustment = CursorUtil.getColumnIndexOrThrow(_cursor, "loadAdjustment");
          final int _cursorIndexOfVolumeAdjustment = CursorUtil.getColumnIndexOrThrow(_cursor, "volumeAdjustment");
          final int _cursorIndexOfRecommendation = CursorUtil.getColumnIndexOrThrow(_cursor, "recommendation");
          final int _cursorIndexOfActiveRules = CursorUtil.getColumnIndexOrThrow(_cursor, "activeRules");
          final int _cursorIndexOfExplanationText = CursorUtil.getColumnIndexOrThrow(_cursor, "explanationText");
          final List<WorkoutSessionEntity> _result = new ArrayList<WorkoutSessionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WorkoutSessionEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final double _tmpSleepDuration;
            _tmpSleepDuration = _cursor.getDouble(_cursorIndexOfSleepDuration);
            final String _tmpMuscleSoreness;
            _tmpMuscleSoreness = _cursor.getString(_cursorIndexOfMuscleSoreness);
            final String _tmpUserGoal;
            _tmpUserGoal = _cursor.getString(_cursorIndexOfUserGoal);
            final String _tmpPrevPerformance;
            _tmpPrevPerformance = _cursor.getString(_cursorIndexOfPrevPerformance);
            final int _tmpLoadAdjustment;
            _tmpLoadAdjustment = _cursor.getInt(_cursorIndexOfLoadAdjustment);
            final int _tmpVolumeAdjustment;
            _tmpVolumeAdjustment = _cursor.getInt(_cursorIndexOfVolumeAdjustment);
            final String _tmpRecommendation;
            _tmpRecommendation = _cursor.getString(_cursorIndexOfRecommendation);
            final String _tmpActiveRules;
            _tmpActiveRules = _cursor.getString(_cursorIndexOfActiveRules);
            final String _tmpExplanationText;
            _tmpExplanationText = _cursor.getString(_cursorIndexOfExplanationText);
            _item = new WorkoutSessionEntity(_tmpId,_tmpTimestamp,_tmpSleepDuration,_tmpMuscleSoreness,_tmpUserGoal,_tmpPrevPerformance,_tmpLoadAdjustment,_tmpVolumeAdjustment,_tmpRecommendation,_tmpActiveRules,_tmpExplanationText);
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
  public Flow<Double> getAverageLoadAdjustmentSince(final long since) {
    final String _sql = "\n"
            + "        SELECT AVG(loadAdjustment) FROM workout_sessions \n"
            + "        WHERE timestamp >= ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, since);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"workout_sessions"}, new Callable<Double>() {
      @Override
      @Nullable
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final Double _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getDouble(0);
            }
            _result = _tmp;
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
