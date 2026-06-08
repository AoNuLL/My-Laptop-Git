package com.ai.voicechanger.data.local;

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
import com.ai.voicechanger.data.model.VoiceModel;
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
public final class VoiceModelDao_Impl implements VoiceModelDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<VoiceModel> __insertionAdapterOfVoiceModel;

  private final EntityDeletionOrUpdateAdapter<VoiceModel> __deletionAdapterOfVoiceModel;

  private final EntityDeletionOrUpdateAdapter<VoiceModel> __updateAdapterOfVoiceModel;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  public VoiceModelDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfVoiceModel = new EntityInsertionAdapter<VoiceModel>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `voice_models` (`id`,`name`,`modelPath`,`indexPath`,`description`,`isDownloaded`,`createdAt`,`fileSize`,`isLoaded`,`useGPU`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VoiceModel entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        if (entity.getModelPath() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getModelPath());
        }
        if (entity.getIndexPath() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getIndexPath());
        }
        if (entity.getDescription() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getDescription());
        }
        final int _tmp = entity.isDownloaded() ? 1 : 0;
        statement.bindLong(6, _tmp);
        statement.bindLong(7, entity.getCreatedAt());
        statement.bindLong(8, entity.getFileSize());
        final int _tmp_1 = entity.isLoaded() ? 1 : 0;
        statement.bindLong(9, _tmp_1);
        final int _tmp_2 = entity.getUseGPU() ? 1 : 0;
        statement.bindLong(10, _tmp_2);
      }
    };
    this.__deletionAdapterOfVoiceModel = new EntityDeletionOrUpdateAdapter<VoiceModel>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `voice_models` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VoiceModel entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfVoiceModel = new EntityDeletionOrUpdateAdapter<VoiceModel>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `voice_models` SET `id` = ?,`name` = ?,`modelPath` = ?,`indexPath` = ?,`description` = ?,`isDownloaded` = ?,`createdAt` = ?,`fileSize` = ?,`isLoaded` = ?,`useGPU` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VoiceModel entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        if (entity.getModelPath() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getModelPath());
        }
        if (entity.getIndexPath() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getIndexPath());
        }
        if (entity.getDescription() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getDescription());
        }
        final int _tmp = entity.isDownloaded() ? 1 : 0;
        statement.bindLong(6, _tmp);
        statement.bindLong(7, entity.getCreatedAt());
        statement.bindLong(8, entity.getFileSize());
        final int _tmp_1 = entity.isLoaded() ? 1 : 0;
        statement.bindLong(9, _tmp_1);
        final int _tmp_2 = entity.getUseGPU() ? 1 : 0;
        statement.bindLong(10, _tmp_2);
        statement.bindLong(11, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM voice_models WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final VoiceModel model, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfVoiceModel.insertAndReturnId(model);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final VoiceModel model, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfVoiceModel.handle(model);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final VoiceModel model, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfVoiceModel.handle(model);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
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
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<VoiceModel>> getAll() {
    final String _sql = "SELECT * FROM voice_models ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"voice_models"}, new Callable<List<VoiceModel>>() {
      @Override
      @NonNull
      public List<VoiceModel> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfModelPath = CursorUtil.getColumnIndexOrThrow(_cursor, "modelPath");
          final int _cursorIndexOfIndexPath = CursorUtil.getColumnIndexOrThrow(_cursor, "indexPath");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfIsDownloaded = CursorUtil.getColumnIndexOrThrow(_cursor, "isDownloaded");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "fileSize");
          final int _cursorIndexOfIsLoaded = CursorUtil.getColumnIndexOrThrow(_cursor, "isLoaded");
          final int _cursorIndexOfUseGPU = CursorUtil.getColumnIndexOrThrow(_cursor, "useGPU");
          final List<VoiceModel> _result = new ArrayList<VoiceModel>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VoiceModel _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpModelPath;
            if (_cursor.isNull(_cursorIndexOfModelPath)) {
              _tmpModelPath = null;
            } else {
              _tmpModelPath = _cursor.getString(_cursorIndexOfModelPath);
            }
            final String _tmpIndexPath;
            if (_cursor.isNull(_cursorIndexOfIndexPath)) {
              _tmpIndexPath = null;
            } else {
              _tmpIndexPath = _cursor.getString(_cursorIndexOfIndexPath);
            }
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final boolean _tmpIsDownloaded;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDownloaded);
            _tmpIsDownloaded = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpFileSize;
            _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            final boolean _tmpIsLoaded;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsLoaded);
            _tmpIsLoaded = _tmp_1 != 0;
            final boolean _tmpUseGPU;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfUseGPU);
            _tmpUseGPU = _tmp_2 != 0;
            _item = new VoiceModel(_tmpId,_tmpName,_tmpModelPath,_tmpIndexPath,_tmpDescription,_tmpIsDownloaded,_tmpCreatedAt,_tmpFileSize,_tmpIsLoaded,_tmpUseGPU);
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
  public Object getById(final long id, final Continuation<? super VoiceModel> $completion) {
    final String _sql = "SELECT * FROM voice_models WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<VoiceModel>() {
      @Override
      @Nullable
      public VoiceModel call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfModelPath = CursorUtil.getColumnIndexOrThrow(_cursor, "modelPath");
          final int _cursorIndexOfIndexPath = CursorUtil.getColumnIndexOrThrow(_cursor, "indexPath");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfIsDownloaded = CursorUtil.getColumnIndexOrThrow(_cursor, "isDownloaded");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "fileSize");
          final int _cursorIndexOfIsLoaded = CursorUtil.getColumnIndexOrThrow(_cursor, "isLoaded");
          final int _cursorIndexOfUseGPU = CursorUtil.getColumnIndexOrThrow(_cursor, "useGPU");
          final VoiceModel _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpModelPath;
            if (_cursor.isNull(_cursorIndexOfModelPath)) {
              _tmpModelPath = null;
            } else {
              _tmpModelPath = _cursor.getString(_cursorIndexOfModelPath);
            }
            final String _tmpIndexPath;
            if (_cursor.isNull(_cursorIndexOfIndexPath)) {
              _tmpIndexPath = null;
            } else {
              _tmpIndexPath = _cursor.getString(_cursorIndexOfIndexPath);
            }
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final boolean _tmpIsDownloaded;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDownloaded);
            _tmpIsDownloaded = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpFileSize;
            _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            final boolean _tmpIsLoaded;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsLoaded);
            _tmpIsLoaded = _tmp_1 != 0;
            final boolean _tmpUseGPU;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfUseGPU);
            _tmpUseGPU = _tmp_2 != 0;
            _result = new VoiceModel(_tmpId,_tmpName,_tmpModelPath,_tmpIndexPath,_tmpDescription,_tmpIsDownloaded,_tmpCreatedAt,_tmpFileSize,_tmpIsLoaded,_tmpUseGPU);
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
