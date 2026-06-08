package com.ai.voicechanger.data.local;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\'\u0018\u0000 \t2\u00020\u0001:\u0001\tB\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H&J\b\u0010\u0005\u001a\u00020\u0006H&J\b\u0010\u0007\u001a\u00020\bH&\u00a8\u0006\n"}, d2 = {"Lcom/ai/voicechanger/data/local/AppDatabase;", "Landroidx/room/RoomDatabase;", "()V", "audioFileDao", "Lcom/ai/voicechanger/data/local/AudioFileDao;", "voiceModelDao", "Lcom/ai/voicechanger/data/local/VoiceModelDao;", "voicePackDao", "Lcom/ai/voicechanger/data/local/VoicePackDao;", "Companion", "app_release"})
@androidx.room.Database(entities = {com.ai.voicechanger.data.local.AudioFile.class, com.ai.voicechanger.data.local.VoicePack.class, com.ai.voicechanger.data.model.VoiceModel.class}, version = 2)
public abstract class AppDatabase extends androidx.room.RoomDatabase {
    @kotlin.jvm.Volatile
    @org.jetbrains.annotations.Nullable
    private static volatile com.ai.voicechanger.data.local.AppDatabase instance;
    @org.jetbrains.annotations.NotNull
    public static final com.ai.voicechanger.data.local.AppDatabase.Companion Companion = null;
    
    public AppDatabase() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public abstract com.ai.voicechanger.data.local.AudioFileDao audioFileDao();
    
    @org.jetbrains.annotations.NotNull
    public abstract com.ai.voicechanger.data.local.VoicePackDao voicePackDao();
    
    @org.jetbrains.annotations.NotNull
    public abstract com.ai.voicechanger.data.local.VoiceModelDao voiceModelDao();
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0005\u001a\u00020\u0004R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2 = {"Lcom/ai/voicechanger/data/local/AppDatabase$Companion;", "", "()V", "instance", "Lcom/ai/voicechanger/data/local/AppDatabase;", "get", "app_release"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final com.ai.voicechanger.data.local.AppDatabase get() {
            return null;
        }
    }
}