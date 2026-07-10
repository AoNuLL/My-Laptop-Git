import React, { useState, useCallback } from 'react';
import { useAudioProcessor } from './hooks/useAudioProcessor';
import { WaveformDisplay } from './components/WaveformDisplay';
import { VoiceSelector } from './components/VoiceSelector';
import { Controls } from './components/Controls';
import { RecordButton } from './components/RecordButton';
import { ModelImporter } from './components/ModelImporter';
import { FloatingToggle } from './components/FloatingToggle';
import { ModelDownload } from './components/ModelDownload';
import { VOICE_PRESETS } from './types/voicePresets';
import { VoicePreset } from './types';
import './App.css';

const App: React.FC = () => {
  const {
    audioState,
    waveform,
    config,
    models,
    selectedModelId,
    processingMode,
    importing,
    importProgress,
    error,
    startAudio,
    stopAudio,
    updateConfig,
    importModel,
    selectModel,
    deleteModel,
    clearError,
    isActive,
  } = useAudioProcessor();

  const [selectedPreset, setSelectedPreset] = useState<string>('original');

  const handleToggle = useCallback(async () => {
    if (isActive) {
      stopAudio();
    } else {
      await startAudio();
    }
  }, [isActive, startAudio, stopAudio]);

  const handlePresetSelect = useCallback((preset: VoicePreset) => {
    setSelectedPreset(preset.id);
    updateConfig({
      pitchShift: preset.pitchShift,
      formantShift: preset.formantShift,
    });
  }, [updateConfig]);

  const modeDisplay = processingMode === 'onnx' ? 'ONNX' : 'DSP';

  return (
    <div className="app">
      <header className="app-header">
        <h1 className="app-title">RVC 变声器</h1>
        <p className="app-subtitle">实时语音变换 · ONNX模型推理 · 多音色支持</p>
      </header>

      <main className="app-main">
        <section className="section waveform-section">
          <WaveformDisplay waveform={waveform} isActive={isActive} />
        </section>

        {error && (
          <div className="error-toast" onClick={clearError}>
            <span className="error-toast-text">{error}</span>
            <span className="error-toast-close">x</span>
          </div>
        )}

        <section className="section record-section-wrapper">
          <RecordButton
            isActive={isActive}
            onToggle={handleToggle}
            inputLevel={audioState.inputLevel}
            outputLevel={audioState.outputLevel}
          />
        </section>

        <section className="section">
          <VoiceSelector
            presets={VOICE_PRESETS}
            selectedId={selectedPreset}
            onSelect={handlePresetSelect}
          />
        </section>

        <section className="section">
          <ModelImporter
            models={models}
            selectedModelId={selectedModelId}
            isImporting={importing}
            importProgress={importProgress}
            onImport={importModel}
            onSelect={selectModel}
            onDelete={deleteModel}
          />
          <ModelDownload />
        </section>

        <section className="section">
          <Controls config={config} onChange={updateConfig} />
        </section>
      </main>

      <footer className="app-footer">
        <span className="latency-indicator">
          {isActive
            ? `${processingMode === 'onnx' ? 'ONNX推理' : 'DSP模拟'} · 延迟: ${audioState.latency.toFixed(1)}ms`
            : '点击按钮开始变声'}
        </span>
      </footer>

      <FloatingToggle
        isActive={isActive}
        onToggle={handleToggle}
        processingMode={modeDisplay}
      />
    </div>
  );
};

export default App;
