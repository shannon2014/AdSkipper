package com.adskipper.data;

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
public final class SkipDatabase_Impl extends SkipDatabase {
  private volatile SkipRecordDao _skipRecordDao;

  private volatile AppPreferenceDao _appPreferenceDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `skip_records` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `packageName` TEXT, `appName` TEXT, `timestamp` INTEGER NOT NULL, `adType` TEXT, `timeSavedSeconds` INTEGER NOT NULL, `skipMethod` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `app_preferences` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `packageName` TEXT, `appName` TEXT, `isMonitored` INTEGER NOT NULL, `lastUsedTimestamp` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '674ae273eb1f523a0526578b5bf208ef')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `skip_records`");
        db.execSQL("DROP TABLE IF EXISTS `app_preferences`");
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
        final HashMap<String, TableInfo.Column> _columnsSkipRecords = new HashMap<String, TableInfo.Column>(7);
        _columnsSkipRecords.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSkipRecords.put("packageName", new TableInfo.Column("packageName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSkipRecords.put("appName", new TableInfo.Column("appName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSkipRecords.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSkipRecords.put("adType", new TableInfo.Column("adType", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSkipRecords.put("timeSavedSeconds", new TableInfo.Column("timeSavedSeconds", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSkipRecords.put("skipMethod", new TableInfo.Column("skipMethod", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSkipRecords = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSkipRecords = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSkipRecords = new TableInfo("skip_records", _columnsSkipRecords, _foreignKeysSkipRecords, _indicesSkipRecords);
        final TableInfo _existingSkipRecords = TableInfo.read(db, "skip_records");
        if (!_infoSkipRecords.equals(_existingSkipRecords)) {
          return new RoomOpenHelper.ValidationResult(false, "skip_records(com.adskipper.data.SkipRecord).\n"
                  + " Expected:\n" + _infoSkipRecords + "\n"
                  + " Found:\n" + _existingSkipRecords);
        }
        final HashMap<String, TableInfo.Column> _columnsAppPreferences = new HashMap<String, TableInfo.Column>(5);
        _columnsAppPreferences.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppPreferences.put("packageName", new TableInfo.Column("packageName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppPreferences.put("appName", new TableInfo.Column("appName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppPreferences.put("isMonitored", new TableInfo.Column("isMonitored", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppPreferences.put("lastUsedTimestamp", new TableInfo.Column("lastUsedTimestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAppPreferences = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAppPreferences = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAppPreferences = new TableInfo("app_preferences", _columnsAppPreferences, _foreignKeysAppPreferences, _indicesAppPreferences);
        final TableInfo _existingAppPreferences = TableInfo.read(db, "app_preferences");
        if (!_infoAppPreferences.equals(_existingAppPreferences)) {
          return new RoomOpenHelper.ValidationResult(false, "app_preferences(com.adskipper.data.AppPreference).\n"
                  + " Expected:\n" + _infoAppPreferences + "\n"
                  + " Found:\n" + _existingAppPreferences);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "674ae273eb1f523a0526578b5bf208ef", "90449ff424e9bacb79ef3163e5117a47");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "skip_records","app_preferences");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `skip_records`");
      _db.execSQL("DELETE FROM `app_preferences`");
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
    _typeConvertersMap.put(SkipRecordDao.class, SkipRecordDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AppPreferenceDao.class, AppPreferenceDao_Impl.getRequiredConverters());
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
  public SkipRecordDao skipRecordDao() {
    if (_skipRecordDao != null) {
      return _skipRecordDao;
    } else {
      synchronized(this) {
        if(_skipRecordDao == null) {
          _skipRecordDao = new SkipRecordDao_Impl(this);
        }
        return _skipRecordDao;
      }
    }
  }

  @Override
  public AppPreferenceDao appPreferenceDao() {
    if (_appPreferenceDao != null) {
      return _appPreferenceDao;
    } else {
      synchronized(this) {
        if(_appPreferenceDao == null) {
          _appPreferenceDao = new AppPreferenceDao_Impl(this);
        }
        return _appPreferenceDao;
      }
    }
  }
}
