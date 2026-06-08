package com.ai.voicechanger.data.local;

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
public final class AppDatabase_Impl extends AppDatabase {
  private volatile AudioFileDao _audioFileDao;

  private volatile VoicePackDao _voicePackDao;

  private volatile VoiceModelDao _voiceModelDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `audio_files` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `filePath` TEXT NOT NULL, `duration` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `isProcessed` INTEGER NOT NULL, `modelPath` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `voice_packs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `modelPath` TEXT NOT NULL, `index` TEXT NOT NULL, `description` TEXT NOT NULL, `isDownloaded` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `voice_models` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `modelPath` TEXT NOT NULL, `indexPath` TEXT, `description` TEXT NOT NULL, `isDownloaded` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `fileSize` INTEGER NOT NULL, `isLoaded` INTEGER NOT NULL, `useGPU` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '3cd9ad80b720166fc95f754670e9e2a0')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `audio_files`");
        db.execSQL("DROP TABLE IF EXISTS `voice_packs`");
        db.execSQL("DROP TABLE IF EXISTS `voice_models`");
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
        final HashMap<String, TableInfo.Column> _columnsAudioFiles = new HashMap<String, TableInfo.Column>(7);
        _columnsAudioFiles.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAudioFiles.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAudioFiles.put("filePath", new TableInfo.Column("filePath", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAudioFiles.put("duration", new TableInfo.Column("duration", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAudioFiles.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAudioFiles.put("isProcessed", new TableInfo.Column("isProcessed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAudioFiles.put("modelPath", new TableInfo.Column("modelPath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAudioFiles = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAudioFiles = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAudioFiles = new TableInfo("audio_files", _columnsAudioFiles, _foreignKeysAudioFiles, _indicesAudioFiles);
        final TableInfo _existingAudioFiles = TableInfo.read(db, "audio_files");
        if (!_infoAudioFiles.equals(_existingAudioFiles)) {
          return new RoomOpenHelper.ValidationResult(false, "audio_files(com.ai.voicechanger.data.local.AudioFile).\n"
                  + " Expected:\n" + _infoAudioFiles + "\n"
                  + " Found:\n" + _existingAudioFiles);
        }
        final HashMap<String, TableInfo.Column> _columnsVoicePacks = new HashMap<String, TableInfo.Column>(6);
        _columnsVoicePacks.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVoicePacks.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVoicePacks.put("modelPath", new TableInfo.Column("modelPath", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVoicePacks.put("index", new TableInfo.Column("index", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVoicePacks.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVoicePacks.put("isDownloaded", new TableInfo.Column("isDownloaded", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysVoicePacks = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesVoicePacks = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoVoicePacks = new TableInfo("voice_packs", _columnsVoicePacks, _foreignKeysVoicePacks, _indicesVoicePacks);
        final TableInfo _existingVoicePacks = TableInfo.read(db, "voice_packs");
        if (!_infoVoicePacks.equals(_existingVoicePacks)) {
          return new RoomOpenHelper.ValidationResult(false, "voice_packs(com.ai.voicechanger.data.local.VoicePack).\n"
                  + " Expected:\n" + _infoVoicePacks + "\n"
                  + " Found:\n" + _existingVoicePacks);
        }
        final HashMap<String, TableInfo.Column> _columnsVoiceModels = new HashMap<String, TableInfo.Column>(10);
        _columnsVoiceModels.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVoiceModels.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVoiceModels.put("modelPath", new TableInfo.Column("modelPath", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVoiceModels.put("indexPath", new TableInfo.Column("indexPath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVoiceModels.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVoiceModels.put("isDownloaded", new TableInfo.Column("isDownloaded", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVoiceModels.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVoiceModels.put("fileSize", new TableInfo.Column("fileSize", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVoiceModels.put("isLoaded", new TableInfo.Column("isLoaded", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVoiceModels.put("useGPU", new TableInfo.Column("useGPU", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysVoiceModels = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesVoiceModels = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoVoiceModels = new TableInfo("voice_models", _columnsVoiceModels, _foreignKeysVoiceModels, _indicesVoiceModels);
        final TableInfo _existingVoiceModels = TableInfo.read(db, "voice_models");
        if (!_infoVoiceModels.equals(_existingVoiceModels)) {
          return new RoomOpenHelper.ValidationResult(false, "voice_models(com.ai.voicechanger.data.model.VoiceModel).\n"
                  + " Expected:\n" + _infoVoiceModels + "\n"
                  + " Found:\n" + _existingVoiceModels);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "3cd9ad80b720166fc95f754670e9e2a0", "1587e1758d778dd80f564db9e689d71c");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "audio_files","voice_packs","voice_models");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `audio_files`");
      _db.execSQL("DELETE FROM `voice_packs`");
      _db.execSQL("DELETE FROM `voice_models`");
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
    _typeConvertersMap.put(AudioFileDao.class, AudioFileDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(VoicePackDao.class, VoicePackDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(VoiceModelDao.class, VoiceModelDao_Impl.getRequiredConverters());
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
  public AudioFileDao audioFileDao() {
    if (_audioFileDao != null) {
      return _audioFileDao;
    } else {
      synchronized(this) {
        if(_audioFileDao == null) {
          _audioFileDao = new AudioFileDao_Impl(this);
        }
        return _audioFileDao;
      }
    }
  }

  @Override
  public VoicePackDao voicePackDao() {
    if (_voicePackDao != null) {
      return _voicePackDao;
    } else {
      synchronized(this) {
        if(_voicePackDao == null) {
          _voicePackDao = new VoicePackDao_Impl(this);
        }
        return _voicePackDao;
      }
    }
  }

  @Override
  public VoiceModelDao voiceModelDao() {
    if (_voiceModelDao != null) {
      return _voiceModelDao;
    } else {
      synchronized(this) {
        if(_voiceModelDao == null) {
          _voiceModelDao = new VoiceModelDao_Impl(this);
        }
        return _voiceModelDao;
      }
    }
  }
}
