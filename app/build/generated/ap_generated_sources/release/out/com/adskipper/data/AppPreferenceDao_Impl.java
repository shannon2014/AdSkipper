package com.adskipper.data;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
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
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppPreferenceDao_Impl implements AppPreferenceDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AppPreference> __insertionAdapterOfAppPreference;

  private final EntityDeletionOrUpdateAdapter<AppPreference> __updateAdapterOfAppPreference;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public AppPreferenceDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAppPreference = new EntityInsertionAdapter<AppPreference>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `app_preferences` (`id`,`packageName`,`appName`,`isMonitored`,`lastUsedTimestamp`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final AppPreference entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getPackageName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getPackageName());
        }
        if (entity.getAppName() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getAppName());
        }
        final int _tmp = entity.isMonitored() ? 1 : 0;
        statement.bindLong(4, _tmp);
        statement.bindLong(5, entity.getLastUsedTimestamp());
      }
    };
    this.__updateAdapterOfAppPreference = new EntityDeletionOrUpdateAdapter<AppPreference>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `app_preferences` SET `id` = ?,`packageName` = ?,`appName` = ?,`isMonitored` = ?,`lastUsedTimestamp` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final AppPreference entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getPackageName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getPackageName());
        }
        if (entity.getAppName() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getAppName());
        }
        final int _tmp = entity.isMonitored() ? 1 : 0;
        statement.bindLong(4, _tmp);
        statement.bindLong(5, entity.getLastUsedTimestamp());
        statement.bindLong(6, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM app_preferences";
        return _query;
      }
    };
  }

  @Override
  public void insert(final AppPreference appPreference) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfAppPreference.insert(appPreference);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final AppPreference appPreference) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfAppPreference.handle(appPreference);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteAll() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDeleteAll.release(_stmt);
    }
  }

  @Override
  public LiveData<List<AppPreference>> getAllApps() {
    final String _sql = "SELECT * FROM app_preferences ORDER BY appName ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"app_preferences"}, false, new Callable<List<AppPreference>>() {
      @Override
      @Nullable
      public List<AppPreference> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfAppName = CursorUtil.getColumnIndexOrThrow(_cursor, "appName");
          final int _cursorIndexOfIsMonitored = CursorUtil.getColumnIndexOrThrow(_cursor, "isMonitored");
          final int _cursorIndexOfLastUsedTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUsedTimestamp");
          final List<AppPreference> _result = new ArrayList<AppPreference>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AppPreference _item;
            _item = new AppPreference();
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            _item.setId(_tmpId);
            final String _tmpPackageName;
            if (_cursor.isNull(_cursorIndexOfPackageName)) {
              _tmpPackageName = null;
            } else {
              _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            }
            _item.setPackageName(_tmpPackageName);
            final String _tmpAppName;
            if (_cursor.isNull(_cursorIndexOfAppName)) {
              _tmpAppName = null;
            } else {
              _tmpAppName = _cursor.getString(_cursorIndexOfAppName);
            }
            _item.setAppName(_tmpAppName);
            final boolean _tmpIsMonitored;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsMonitored);
            _tmpIsMonitored = _tmp != 0;
            _item.setMonitored(_tmpIsMonitored);
            final long _tmpLastUsedTimestamp;
            _tmpLastUsedTimestamp = _cursor.getLong(_cursorIndexOfLastUsedTimestamp);
            _item.setLastUsedTimestamp(_tmpLastUsedTimestamp);
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
  public LiveData<List<AppPreference>> getMonitoredApps() {
    final String _sql = "SELECT * FROM app_preferences WHERE isMonitored = 1 ORDER BY appName ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"app_preferences"}, false, new Callable<List<AppPreference>>() {
      @Override
      @Nullable
      public List<AppPreference> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfAppName = CursorUtil.getColumnIndexOrThrow(_cursor, "appName");
          final int _cursorIndexOfIsMonitored = CursorUtil.getColumnIndexOrThrow(_cursor, "isMonitored");
          final int _cursorIndexOfLastUsedTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUsedTimestamp");
          final List<AppPreference> _result = new ArrayList<AppPreference>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AppPreference _item;
            _item = new AppPreference();
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            _item.setId(_tmpId);
            final String _tmpPackageName;
            if (_cursor.isNull(_cursorIndexOfPackageName)) {
              _tmpPackageName = null;
            } else {
              _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            }
            _item.setPackageName(_tmpPackageName);
            final String _tmpAppName;
            if (_cursor.isNull(_cursorIndexOfAppName)) {
              _tmpAppName = null;
            } else {
              _tmpAppName = _cursor.getString(_cursorIndexOfAppName);
            }
            _item.setAppName(_tmpAppName);
            final boolean _tmpIsMonitored;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsMonitored);
            _tmpIsMonitored = _tmp != 0;
            _item.setMonitored(_tmpIsMonitored);
            final long _tmpLastUsedTimestamp;
            _tmpLastUsedTimestamp = _cursor.getLong(_cursorIndexOfLastUsedTimestamp);
            _item.setLastUsedTimestamp(_tmpLastUsedTimestamp);
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
  public LiveData<List<AppPreference>> getRecentlyUsedApps(final int limit) {
    final String _sql = "SELECT * FROM app_preferences ORDER BY lastUsedTimestamp DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return __db.getInvalidationTracker().createLiveData(new String[] {"app_preferences"}, false, new Callable<List<AppPreference>>() {
      @Override
      @Nullable
      public List<AppPreference> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfAppName = CursorUtil.getColumnIndexOrThrow(_cursor, "appName");
          final int _cursorIndexOfIsMonitored = CursorUtil.getColumnIndexOrThrow(_cursor, "isMonitored");
          final int _cursorIndexOfLastUsedTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUsedTimestamp");
          final List<AppPreference> _result = new ArrayList<AppPreference>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AppPreference _item;
            _item = new AppPreference();
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            _item.setId(_tmpId);
            final String _tmpPackageName;
            if (_cursor.isNull(_cursorIndexOfPackageName)) {
              _tmpPackageName = null;
            } else {
              _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            }
            _item.setPackageName(_tmpPackageName);
            final String _tmpAppName;
            if (_cursor.isNull(_cursorIndexOfAppName)) {
              _tmpAppName = null;
            } else {
              _tmpAppName = _cursor.getString(_cursorIndexOfAppName);
            }
            _item.setAppName(_tmpAppName);
            final boolean _tmpIsMonitored;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsMonitored);
            _tmpIsMonitored = _tmp != 0;
            _item.setMonitored(_tmpIsMonitored);
            final long _tmpLastUsedTimestamp;
            _tmpLastUsedTimestamp = _cursor.getLong(_cursorIndexOfLastUsedTimestamp);
            _item.setLastUsedTimestamp(_tmpLastUsedTimestamp);
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
  public AppPreference getAppByPackage(final String packageName) {
    final String _sql = "SELECT * FROM app_preferences WHERE packageName = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (packageName == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, packageName);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
      final int _cursorIndexOfAppName = CursorUtil.getColumnIndexOrThrow(_cursor, "appName");
      final int _cursorIndexOfIsMonitored = CursorUtil.getColumnIndexOrThrow(_cursor, "isMonitored");
      final int _cursorIndexOfLastUsedTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUsedTimestamp");
      final AppPreference _result;
      if (_cursor.moveToFirst()) {
        _result = new AppPreference();
        final long _tmpId;
        _tmpId = _cursor.getLong(_cursorIndexOfId);
        _result.setId(_tmpId);
        final String _tmpPackageName;
        if (_cursor.isNull(_cursorIndexOfPackageName)) {
          _tmpPackageName = null;
        } else {
          _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
        }
        _result.setPackageName(_tmpPackageName);
        final String _tmpAppName;
        if (_cursor.isNull(_cursorIndexOfAppName)) {
          _tmpAppName = null;
        } else {
          _tmpAppName = _cursor.getString(_cursorIndexOfAppName);
        }
        _result.setAppName(_tmpAppName);
        final boolean _tmpIsMonitored;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsMonitored);
        _tmpIsMonitored = _tmp != 0;
        _result.setMonitored(_tmpIsMonitored);
        final long _tmpLastUsedTimestamp;
        _tmpLastUsedTimestamp = _cursor.getLong(_cursorIndexOfLastUsedTimestamp);
        _result.setLastUsedTimestamp(_tmpLastUsedTimestamp);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public boolean isAppMonitored(final String packageName) {
    final String _sql = "SELECT isMonitored FROM app_preferences WHERE packageName = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (packageName == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, packageName);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final boolean _result;
      if (_cursor.moveToFirst()) {
        final int _tmp;
        _tmp = _cursor.getInt(0);
        _result = _tmp != 0;
      } else {
        _result = false;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public LiveData<Integer> getMonitoredAppsCount() {
    final String _sql = "SELECT COUNT(*) FROM app_preferences WHERE isMonitored = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"app_preferences"}, false, new Callable<Integer>() {
      @Override
      @Nullable
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
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

  @Override
  public List<AppPreference> getAllAppsList() {
    final String _sql = "SELECT * FROM app_preferences ORDER BY appName ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
      final int _cursorIndexOfAppName = CursorUtil.getColumnIndexOrThrow(_cursor, "appName");
      final int _cursorIndexOfIsMonitored = CursorUtil.getColumnIndexOrThrow(_cursor, "isMonitored");
      final int _cursorIndexOfLastUsedTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUsedTimestamp");
      final List<AppPreference> _result = new ArrayList<AppPreference>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final AppPreference _item;
        _item = new AppPreference();
        final long _tmpId;
        _tmpId = _cursor.getLong(_cursorIndexOfId);
        _item.setId(_tmpId);
        final String _tmpPackageName;
        if (_cursor.isNull(_cursorIndexOfPackageName)) {
          _tmpPackageName = null;
        } else {
          _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
        }
        _item.setPackageName(_tmpPackageName);
        final String _tmpAppName;
        if (_cursor.isNull(_cursorIndexOfAppName)) {
          _tmpAppName = null;
        } else {
          _tmpAppName = _cursor.getString(_cursorIndexOfAppName);
        }
        _item.setAppName(_tmpAppName);
        final boolean _tmpIsMonitored;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsMonitored);
        _tmpIsMonitored = _tmp != 0;
        _item.setMonitored(_tmpIsMonitored);
        final long _tmpLastUsedTimestamp;
        _tmpLastUsedTimestamp = _cursor.getLong(_cursorIndexOfLastUsedTimestamp);
        _item.setLastUsedTimestamp(_tmpLastUsedTimestamp);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public LiveData<List<AppPreference>> searchApps(final String query) {
    final String _sql = "SELECT * FROM app_preferences WHERE appName LIKE '%' || ? || '%' ORDER BY appName ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (query == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, query);
    }
    return __db.getInvalidationTracker().createLiveData(new String[] {"app_preferences"}, false, new Callable<List<AppPreference>>() {
      @Override
      @Nullable
      public List<AppPreference> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfAppName = CursorUtil.getColumnIndexOrThrow(_cursor, "appName");
          final int _cursorIndexOfIsMonitored = CursorUtil.getColumnIndexOrThrow(_cursor, "isMonitored");
          final int _cursorIndexOfLastUsedTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUsedTimestamp");
          final List<AppPreference> _result = new ArrayList<AppPreference>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AppPreference _item;
            _item = new AppPreference();
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            _item.setId(_tmpId);
            final String _tmpPackageName;
            if (_cursor.isNull(_cursorIndexOfPackageName)) {
              _tmpPackageName = null;
            } else {
              _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            }
            _item.setPackageName(_tmpPackageName);
            final String _tmpAppName;
            if (_cursor.isNull(_cursorIndexOfAppName)) {
              _tmpAppName = null;
            } else {
              _tmpAppName = _cursor.getString(_cursorIndexOfAppName);
            }
            _item.setAppName(_tmpAppName);
            final boolean _tmpIsMonitored;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsMonitored);
            _tmpIsMonitored = _tmp != 0;
            _item.setMonitored(_tmpIsMonitored);
            final long _tmpLastUsedTimestamp;
            _tmpLastUsedTimestamp = _cursor.getLong(_cursorIndexOfLastUsedTimestamp);
            _item.setLastUsedTimestamp(_tmpLastUsedTimestamp);
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
