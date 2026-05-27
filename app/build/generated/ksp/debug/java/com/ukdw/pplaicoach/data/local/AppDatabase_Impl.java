package com.ukdw.pplaicoach.data.local;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile WorkoutSessionDao _workoutSessionDao;

  private volatile ExerciseDao _exerciseDao;

  private volatile TrackerSessionDao _trackerSessionDao;

  private volatile SetRecordDao _setRecordDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `workout_sessions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `timestamp` INTEGER NOT NULL, `sleepDuration` REAL NOT NULL, `muscleSoreness` TEXT NOT NULL, `userGoal` TEXT NOT NULL, `prevPerformance` TEXT NOT NULL, `loadAdjustment` INTEGER NOT NULL, `volumeAdjustment` INTEGER NOT NULL, `recommendation` TEXT NOT NULL, `activeRules` TEXT NOT NULL, `explanationText` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `exercises` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `category` TEXT NOT NULL, `instructions` TEXT NOT NULL, `youtubeUrl` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `tracker_sessions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` INTEGER NOT NULL, `category` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `set_records` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `exerciseId` INTEGER NOT NULL, `sessionId` INTEGER NOT NULL, `weightInKg` REAL NOT NULL, `reps` INTEGER NOT NULL, `intensityType` TEXT NOT NULL, FOREIGN KEY(`exerciseId`) REFERENCES `exercises`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`sessionId`) REFERENCES `tracker_sessions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_set_records_exerciseId` ON `set_records` (`exerciseId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_set_records_sessionId` ON `set_records` (`sessionId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '9339bebacbc5fe8289b945b81d4ff3a5')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `workout_sessions`");
        db.execSQL("DROP TABLE IF EXISTS `exercises`");
        db.execSQL("DROP TABLE IF EXISTS `tracker_sessions`");
        db.execSQL("DROP TABLE IF EXISTS `set_records`");
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
        db.execSQL("PRAGMA foreign_keys = ON");
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
        final HashMap<String, TableInfo.Column> _columnsWorkoutSessions = new HashMap<String, TableInfo.Column>(11);
        _columnsWorkoutSessions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutSessions.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutSessions.put("sleepDuration", new TableInfo.Column("sleepDuration", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutSessions.put("muscleSoreness", new TableInfo.Column("muscleSoreness", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutSessions.put("userGoal", new TableInfo.Column("userGoal", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutSessions.put("prevPerformance", new TableInfo.Column("prevPerformance", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutSessions.put("loadAdjustment", new TableInfo.Column("loadAdjustment", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutSessions.put("volumeAdjustment", new TableInfo.Column("volumeAdjustment", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutSessions.put("recommendation", new TableInfo.Column("recommendation", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutSessions.put("activeRules", new TableInfo.Column("activeRules", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutSessions.put("explanationText", new TableInfo.Column("explanationText", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWorkoutSessions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesWorkoutSessions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoWorkoutSessions = new TableInfo("workout_sessions", _columnsWorkoutSessions, _foreignKeysWorkoutSessions, _indicesWorkoutSessions);
        final TableInfo _existingWorkoutSessions = TableInfo.read(db, "workout_sessions");
        if (!_infoWorkoutSessions.equals(_existingWorkoutSessions)) {
          return new RoomOpenHelper.ValidationResult(false, "workout_sessions(com.ukdw.pplaicoach.data.local.WorkoutSessionEntity).\n"
                  + " Expected:\n" + _infoWorkoutSessions + "\n"
                  + " Found:\n" + _existingWorkoutSessions);
        }
        final HashMap<String, TableInfo.Column> _columnsExercises = new HashMap<String, TableInfo.Column>(5);
        _columnsExercises.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExercises.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExercises.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExercises.put("instructions", new TableInfo.Column("instructions", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExercises.put("youtubeUrl", new TableInfo.Column("youtubeUrl", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysExercises = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesExercises = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoExercises = new TableInfo("exercises", _columnsExercises, _foreignKeysExercises, _indicesExercises);
        final TableInfo _existingExercises = TableInfo.read(db, "exercises");
        if (!_infoExercises.equals(_existingExercises)) {
          return new RoomOpenHelper.ValidationResult(false, "exercises(com.ukdw.pplaicoach.data.local.ExerciseEntity).\n"
                  + " Expected:\n" + _infoExercises + "\n"
                  + " Found:\n" + _existingExercises);
        }
        final HashMap<String, TableInfo.Column> _columnsTrackerSessions = new HashMap<String, TableInfo.Column>(3);
        _columnsTrackerSessions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTrackerSessions.put("date", new TableInfo.Column("date", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTrackerSessions.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTrackerSessions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTrackerSessions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTrackerSessions = new TableInfo("tracker_sessions", _columnsTrackerSessions, _foreignKeysTrackerSessions, _indicesTrackerSessions);
        final TableInfo _existingTrackerSessions = TableInfo.read(db, "tracker_sessions");
        if (!_infoTrackerSessions.equals(_existingTrackerSessions)) {
          return new RoomOpenHelper.ValidationResult(false, "tracker_sessions(com.ukdw.pplaicoach.data.local.TrackerSessionEntity).\n"
                  + " Expected:\n" + _infoTrackerSessions + "\n"
                  + " Found:\n" + _existingTrackerSessions);
        }
        final HashMap<String, TableInfo.Column> _columnsSetRecords = new HashMap<String, TableInfo.Column>(6);
        _columnsSetRecords.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSetRecords.put("exerciseId", new TableInfo.Column("exerciseId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSetRecords.put("sessionId", new TableInfo.Column("sessionId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSetRecords.put("weightInKg", new TableInfo.Column("weightInKg", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSetRecords.put("reps", new TableInfo.Column("reps", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSetRecords.put("intensityType", new TableInfo.Column("intensityType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSetRecords = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysSetRecords.add(new TableInfo.ForeignKey("exercises", "CASCADE", "NO ACTION", Arrays.asList("exerciseId"), Arrays.asList("id")));
        _foreignKeysSetRecords.add(new TableInfo.ForeignKey("tracker_sessions", "CASCADE", "NO ACTION", Arrays.asList("sessionId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesSetRecords = new HashSet<TableInfo.Index>(2);
        _indicesSetRecords.add(new TableInfo.Index("index_set_records_exerciseId", false, Arrays.asList("exerciseId"), Arrays.asList("ASC")));
        _indicesSetRecords.add(new TableInfo.Index("index_set_records_sessionId", false, Arrays.asList("sessionId"), Arrays.asList("ASC")));
        final TableInfo _infoSetRecords = new TableInfo("set_records", _columnsSetRecords, _foreignKeysSetRecords, _indicesSetRecords);
        final TableInfo _existingSetRecords = TableInfo.read(db, "set_records");
        if (!_infoSetRecords.equals(_existingSetRecords)) {
          return new RoomOpenHelper.ValidationResult(false, "set_records(com.ukdw.pplaicoach.data.local.SetRecordEntity).\n"
                  + " Expected:\n" + _infoSetRecords + "\n"
                  + " Found:\n" + _existingSetRecords);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "9339bebacbc5fe8289b945b81d4ff3a5", "8d628dc6349d97754bf2af8b2ea6c953");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "workout_sessions","exercises","tracker_sessions","set_records");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `workout_sessions`");
      _db.execSQL("DELETE FROM `exercises`");
      _db.execSQL("DELETE FROM `tracker_sessions`");
      _db.execSQL("DELETE FROM `set_records`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
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
    _typeConvertersMap.put(WorkoutSessionDao.class, WorkoutSessionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ExerciseDao.class, ExerciseDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TrackerSessionDao.class, TrackerSessionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SetRecordDao.class, SetRecordDao_Impl.getRequiredConverters());
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
  public WorkoutSessionDao workoutSessionDao() {
    if (_workoutSessionDao != null) {
      return _workoutSessionDao;
    } else {
      synchronized(this) {
        if(_workoutSessionDao == null) {
          _workoutSessionDao = new WorkoutSessionDao_Impl(this);
        }
        return _workoutSessionDao;
      }
    }
  }

  @Override
  public ExerciseDao exerciseDao() {
    if (_exerciseDao != null) {
      return _exerciseDao;
    } else {
      synchronized(this) {
        if(_exerciseDao == null) {
          _exerciseDao = new ExerciseDao_Impl(this);
        }
        return _exerciseDao;
      }
    }
  }

  @Override
  public TrackerSessionDao trackerSessionDao() {
    if (_trackerSessionDao != null) {
      return _trackerSessionDao;
    } else {
      synchronized(this) {
        if(_trackerSessionDao == null) {
          _trackerSessionDao = new TrackerSessionDao_Impl(this);
        }
        return _trackerSessionDao;
      }
    }
  }

  @Override
  public SetRecordDao setRecordDao() {
    if (_setRecordDao != null) {
      return _setRecordDao;
    } else {
      synchronized(this) {
        if(_setRecordDao == null) {
          _setRecordDao = new SetRecordDao_Impl(this);
        }
        return _setRecordDao;
      }
    }
  }
}
