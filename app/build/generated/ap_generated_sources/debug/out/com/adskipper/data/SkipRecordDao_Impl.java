package com.adskipper.data;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
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
public final class SkipRecordDao_Impl implements SkipRecordDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SkipRecord> __insertionAdapterOfSkipRecord;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public SkipRecordDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSkipRecord = new EntityInsertionAdapter<SkipRecord>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `skip_records` (`id`,`packageName`,`appName`,`timestamp`,`adType`,`timeSavedSeconds`,`skipMethod`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final SkipRecord entity) {
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
        statement.bindLong(4, entity.getTimestamp());
        if (entity.getAdType() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getAdType());
        }
        statement.bindLong(6, entity.getTimeSavedSeconds());
        if (entity.getSkipMethod() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getSkipMethod());
        }
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM skip_records";
        return _query;
      }
    };
  }

  @Override
  public void insert(final SkipRecord record) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfSkipRecord.insert(record);
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
  public LiveData<List<SkipRecord>> getAllRecords() {
    final String _sql = "SELECT * FROM skip_records ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"skip_records"}, false, new Callable<List<SkipRecord>>() {
      @Override
      @Nullable
      public List<SkipRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfAppName = CursorUtil.getColumnIndexOrThrow(_cursor, "appName");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfAdType = CursorUtil.getColumnIndexOrThrow(_cursor, "adType");
          final int _cursorIndexOfTimeSavedSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "timeSavedSeconds");
          final int _cursorIndexOfSkipMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "skipMethod");
          final List<SkipRecord> _result = new ArrayList<SkipRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SkipRecord _item;
            _item = new SkipRecord();
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
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item.setTimestamp(_tmpTimestamp);
            final String _tmpAdType;
            if (_cursor.isNull(_cursorIndexOfAdType)) {
              _tmpAdType = null;
            } else {
              _tmpAdType = _cursor.getString(_cursorIndexOfAdType);
            }
            _item.setAdType(_tmpAdType);
            final int _tmpTimeSavedSeconds;
            _tmpTimeSavedSeconds = _cursor.getInt(_cursorIndexOfTimeSavedSeconds);
            _item.setTimeSavedSeconds(_tmpTimeSavedSeconds);
            final String _tmpSkipMethod;
            if (_cursor.isNull(_cursorIndexOfSkipMethod)) {
              _tmpSkipMethod = null;
            } else {
              _tmpSkipMethod = _cursor.getString(_cursorIndexOfSkipMethod);
            }
            _item.setSkipMethod(_tmpSkipMethod);
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
  public LiveData<List<SkipRecord>> getRecordsSince(final long startTime) {
    final String _sql = "SELECT * FROM skip_records WHERE timestamp >= ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    return __db.getInvalidationTracker().createLiveData(new String[] {"skip_records"}, false, new Callable<List<SkipRecord>>() {
      @Override
      @Nullable
      public List<SkipRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfAppName = CursorUtil.getColumnIndexOrThrow(_cursor, "appName");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfAdType = CursorUtil.getColumnIndexOrThrow(_cursor, "adType");
          final int _cursorIndexOfTimeSavedSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "timeSavedSeconds");
          final int _cursorIndexOfSkipMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "skipMethod");
          final List<SkipRecord> _result = new ArrayList<SkipRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SkipRecord _item;
            _item = new SkipRecord();
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
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item.setTimestamp(_tmpTimestamp);
            final String _tmpAdType;
            if (_cursor.isNull(_cursorIndexOfAdType)) {
              _tmpAdType = null;
            } else {
              _tmpAdType = _cursor.getString(_cursorIndexOfAdType);
            }
            _item.setAdType(_tmpAdType);
            final int _tmpTimeSavedSeconds;
            _tmpTimeSavedSeconds = _cursor.getInt(_cursorIndexOfTimeSavedSeconds);
            _item.setTimeSavedSeconds(_tmpTimeSavedSeconds);
            final String _tmpSkipMethod;
            if (_cursor.isNull(_cursorIndexOfSkipMethod)) {
              _tmpSkipMethod = null;
            } else {
              _tmpSkipMethod = _cursor.getString(_cursorIndexOfSkipMethod);
            }
            _item.setSkipMethod(_tmpSkipMethod);
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
  public LiveData<List<SkipRecord>> getRecordsByPackage(final String packageName) {
    final String _sql = "SELECT * FROM skip_records WHERE packageName = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (packageName == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, packageName);
    }
    return __db.getInvalidationTracker().createLiveData(new String[] {"skip_records"}, false, new Callable<List<SkipRecord>>() {
      @Override
      @Nullable
      public List<SkipRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfAppName = CursorUtil.getColumnIndexOrThrow(_cursor, "appName");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfAdType = CursorUtil.getColumnIndexOrThrow(_cursor, "adType");
          final int _cursorIndexOfTimeSavedSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "timeSavedSeconds");
          final int _cursorIndexOfSkipMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "skipMethod");
          final List<SkipRecord> _result = new ArrayList<SkipRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SkipRecord _item;
            _item = new SkipRecord();
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
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item.setTimestamp(_tmpTimestamp);
            final String _tmpAdType;
            if (_cursor.isNull(_cursorIndexOfAdType)) {
              _tmpAdType = null;
            } else {
              _tmpAdType = _cursor.getString(_cursorIndexOfAdType);
            }
            _item.setAdType(_tmpAdType);
            final int _tmpTimeSavedSeconds;
            _tmpTimeSavedSeconds = _cursor.getInt(_cursorIndexOfTimeSavedSeconds);
            _item.setTimeSavedSeconds(_tmpTimeSavedSeconds);
            final String _tmpSkipMethod;
            if (_cursor.isNull(_cursorIndexOfSkipMethod)) {
              _tmpSkipMethod = null;
            } else {
              _tmpSkipMethod = _cursor.getString(_cursorIndexOfSkipMethod);
            }
            _item.setSkipMethod(_tmpSkipMethod);
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
  public LiveData<Integer> getTotalSkipCount() {
    final String _sql = "SELECT COUNT(*) FROM skip_records";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"skip_records"}, false, new Callable<Integer>() {
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
  public LiveData<Integer> getTotalTimeSaved() {
    final String _sql = "SELECT SUM(timeSavedSeconds) FROM skip_records";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"skip_records"}, false, new Callable<Integer>() {
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
  public LiveData<Integer> getSkipCountSince(final long startTime) {
    final String _sql = "SELECT COUNT(*) FROM skip_records WHERE timestamp >= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    return __db.getInvalidationTracker().createLiveData(new String[] {"skip_records"}, false, new Callable<Integer>() {
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
  public LiveData<Integer> getTimeSavedSince(final long startTime) {
    final String _sql = "SELECT SUM(timeSavedSeconds) FROM skip_records WHERE timestamp >= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    return __db.getInvalidationTracker().createLiveData(new String[] {"skip_records"}, false, new Callable<Integer>() {
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
