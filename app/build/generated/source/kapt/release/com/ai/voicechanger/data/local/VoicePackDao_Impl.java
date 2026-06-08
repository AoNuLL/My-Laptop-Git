package com.ai.voicechanger.data.local;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
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
public final class VoicePackDao_Impl implements VoicePackDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<VoicePack> __insertionAdapterOfVoicePack;

  private final EntityDeletionOrUpdateAdapter<VoicePack> __deletionAdapterOfVoicePack;

  private final EntityDeletionOrUpdateAdapter<VoicePack> __updateAdapterOfVoicePack;

  public VoicePackDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfVoicePack = new EntityInsertionAdapter<VoicePack>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `voice_packs` (`id`,`name`,`modelPath`,`index`,`description`,`isDownloaded`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VoicePack entity) {
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
        if (entity.getIndex() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getIndex());
        }
        if (entity.getDescription() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getDescription());
        }
        final int _tmp = entity.isDownloaded() ? 1 : 0;
        statement.bindLong(6, _tmp);
      }
    };
    this.__deletionAdapterOfVoicePack = new EntityDeletionOrUpdateAdapter<VoicePack>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `voice_packs` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VoicePack entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfVoicePack = new EntityDeletionOrUpdateAdapter<VoicePack>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `voice_packs` SET `id` = ?,`name` = ?,`modelPath` = ?,`index` = ?,`description` = ?,`isDownloaded` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VoicePack entity) {
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
        if (entity.getIndex() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getIndex());
        }
        if (entity.getDescription() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getDescription());
        }
        final int _tmp = entity.isDownloaded() ? 1 : 0;
        statement.bindLong(6, _tmp);
        statement.bindLong(7, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final VoicePack pack, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfVoicePack.insert(pack);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final VoicePack pack, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfVoicePack.handle(pack);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final VoicePack pack, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfVoicePack.handle(pack);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<VoicePack>> getAll() {
    final String _sql = "SELECT * FROM voice_packs";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"voice_packs"}, new Callable<List<VoicePack>>() {
      @Override
      @NonNull
      public List<VoicePack> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfModelPath = CursorUtil.getColumnIndexOrThrow(_cursor, "modelPath");
          final int _cursorIndexOfIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "index");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfIsDownloaded = CursorUtil.getColumnIndexOrThrow(_cursor, "isDownloaded");
          final List<VoicePack> _result = new ArrayList<VoicePack>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VoicePack _item;
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
            final String _tmpIndex;
            if (_cursor.isNull(_cursorIndexOfIndex)) {
              _tmpIndex = null;
            } else {
              _tmpIndex = _cursor.getString(_cursorIndexOfIndex);
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
            _item = new VoicePack(_tmpId,_tmpName,_tmpModelPath,_tmpIndex,_tmpDescription,_tmpIsDownloaded);
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
