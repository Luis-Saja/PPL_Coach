package com.ukdw.pplaicoach.data.local;

import android.database.Cursor;
import androidx.annotation.NonNull;
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
public final class SetRecordDao_Impl implements SetRecordDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SetRecordEntity> __insertionAdapterOfSetRecordEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteSetRecord;

  public SetRecordDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSetRecordEntity = new EntityInsertionAdapter<SetRecordEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `set_records` (`id`,`exerciseId`,`sessionId`,`weightInKg`,`reps`,`intensityType`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SetRecordEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getExerciseId());
        statement.bindLong(3, entity.getSessionId());
        statement.bindDouble(4, entity.getWeightInKg());
        statement.bindLong(5, entity.getReps());
        statement.bindString(6, entity.getIntensityType());
      }
    };
    this.__preparedStmtOfDeleteSetRecord = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM set_records WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertSetRecord(final SetRecordEntity record,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSetRecordEntity.insert(record);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteSetRecord(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteSetRecord.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfDeleteSetRecord.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SetRecordEntity>> getRecordsBySession(final long sessionId) {
    final String _sql = "SELECT * FROM set_records WHERE sessionId = ? ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, sessionId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"set_records"}, new Callable<List<SetRecordEntity>>() {
      @Override
      @NonNull
      public List<SetRecordEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfExerciseId = CursorUtil.getColumnIndexOrThrow(_cursor, "exerciseId");
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
          final int _cursorIndexOfWeightInKg = CursorUtil.getColumnIndexOrThrow(_cursor, "weightInKg");
          final int _cursorIndexOfReps = CursorUtil.getColumnIndexOrThrow(_cursor, "reps");
          final int _cursorIndexOfIntensityType = CursorUtil.getColumnIndexOrThrow(_cursor, "intensityType");
          final List<SetRecordEntity> _result = new ArrayList<SetRecordEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SetRecordEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpExerciseId;
            _tmpExerciseId = _cursor.getInt(_cursorIndexOfExerciseId);
            final long _tmpSessionId;
            _tmpSessionId = _cursor.getLong(_cursorIndexOfSessionId);
            final double _tmpWeightInKg;
            _tmpWeightInKg = _cursor.getDouble(_cursorIndexOfWeightInKg);
            final int _tmpReps;
            _tmpReps = _cursor.getInt(_cursorIndexOfReps);
            final String _tmpIntensityType;
            _tmpIntensityType = _cursor.getString(_cursorIndexOfIntensityType);
            _item = new SetRecordEntity(_tmpId,_tmpExerciseId,_tmpSessionId,_tmpWeightInKg,_tmpReps,_tmpIntensityType);
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
  public Flow<List<ProgressPoint>> getProgressForExercise(final int exerciseId) {
    final String _sql = "\n"
            + "        SELECT sr.id, sr.exerciseId, sr.sessionId, \n"
            + "               MAX(sr.weightInKg) as weightInKg, \n"
            + "               MAX(sr.reps) as reps,\n"
            + "               sr.intensityType,\n"
            + "               ts.date\n"
            + "        FROM set_records sr\n"
            + "        INNER JOIN tracker_sessions ts ON sr.sessionId = ts.id\n"
            + "        WHERE sr.exerciseId = ?\n"
            + "        GROUP BY sr.sessionId\n"
            + "        ORDER BY ts.date ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, exerciseId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"set_records",
        "tracker_sessions"}, new Callable<List<ProgressPoint>>() {
      @Override
      @NonNull
      public List<ProgressPoint> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfExerciseId = 1;
          final int _cursorIndexOfSessionId = 2;
          final int _cursorIndexOfWeightInKg = 3;
          final int _cursorIndexOfReps = 4;
          final int _cursorIndexOfIntensityType = 5;
          final int _cursorIndexOfDate = 6;
          final List<ProgressPoint> _result = new ArrayList<ProgressPoint>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ProgressPoint _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpExerciseId;
            _tmpExerciseId = _cursor.getInt(_cursorIndexOfExerciseId);
            final long _tmpSessionId;
            _tmpSessionId = _cursor.getLong(_cursorIndexOfSessionId);
            final double _tmpWeightInKg;
            _tmpWeightInKg = _cursor.getDouble(_cursorIndexOfWeightInKg);
            final int _tmpReps;
            _tmpReps = _cursor.getInt(_cursorIndexOfReps);
            final String _tmpIntensityType;
            _tmpIntensityType = _cursor.getString(_cursorIndexOfIntensityType);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            _item = new ProgressPoint(_tmpId,_tmpExerciseId,_tmpSessionId,_tmpWeightInKg,_tmpReps,_tmpIntensityType,_tmpDate);
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
