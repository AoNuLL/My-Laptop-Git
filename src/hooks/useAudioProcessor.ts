import { useRef, useCallback, useState, useEffect } from 'react';
import { AudioEngine } from '../audio/AudioEngine';
import { modelManager } from '../audio/ModelManager';
import { AudioState, RVCConfig, WaveformData, RVCModelMeta, ProcessingMode } from '../types';

const defaultConfig: RVCConfig = {
  pitchShift: 0,
  formantShift: 0,
  gain: 1.0,
  noiseGate: 0.02,
  reverb: 0,
};

export function useAudioProcessor() {
  const engineRef = useRef<AudioEngine | null>(null);
  const [config, setConfig] = useState<RVCConfig>(defaultConfig);
  const [audioState, setAudioState] = useState<AudioState>({
    isRecording: false,
    isProcessing: false,
    inputLevel: 0,
    outputLevel: 0,
    latency: 0,
  });
  const [waveform, setWaveform] = useState<WaveformData | null>(null);
  const [models, setModels] = useState<RVCModelMeta[]>([]);
  const [selectedModelId, setSelectedModelId] = useState<string | null>(null);
  const [processingMode, setProcessingMode] = useState<ProcessingMode>('dsp');
  const [importing, setImporting] = useState(false);
  const [importProgress, setImportProgress] = useState(0);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    modelManager.init().then(() => {
      modelManager.listModels().then(setModels);
    });
  }, []);

  const startAudio = useCallback(async () => {
    const engine = new AudioEngine();
    engineRef.current = engine;

    if (selectedModelId && processingMode === 'onnx') {
      try {
        await engine.setMode('onnx', selectedModelId);
      } catch {
        engine.setMode('dsp');
        setProcessingMode('dsp');
      }
    }

    try {
      await engine.start(
        (state) => setAudioState(state),
        (data) => setWaveform(data),
      );
      setError(null);
    } catch (err: any) {
      const msg = err?.message || String(err);
      const friendly = msg.includes('NotAllowedError') || msg.includes('Permission')
        ? '麦克风权限被拒绝，请在浏览器设置中允许麦克风访问'
        : msg.includes('NotFoundError')
        ? '未检测到麦克风设备'
        : `启动失败: ${msg}`;
      setError(friendly);
      engineRef.current?.stop();
      engineRef.current = null;
    }
  }, [selectedModelId, processingMode]);

  const stopAudio = useCallback(() => {
    engineRef.current?.stop();
    engineRef.current = null;
    setAudioState({
      isRecording: false,
      isProcessing: false,
      inputLevel: 0,
      outputLevel: 0,
      latency: 0,
    });
    setWaveform(null);
    setError(null);
  }, []);

  const updateConfig = useCallback((partial: Partial<RVCConfig>) => {
    setConfig((prev) => {
      const next = { ...prev, ...partial };
      engineRef.current?.updateConfig(next);
      return next;
    });
  }, []);

  const importModel = useCallback(async (file: File) => {
    setImporting(true);
    setImportProgress(0);
    try {
      const meta = await modelManager.importModel(file, (pct) => {
        setImportProgress(pct);
      });
      const updated = await modelManager.listModels();
      setModels(updated);
      setSelectedModelId(meta.id);
      setProcessingMode('onnx');
    } catch (err) {
      console.error('Import failed:', err);
    } finally {
      setImporting(false);
      setImportProgress(0);
    }
  }, []);

  const selectModel = useCallback((modelId: string) => {
    setSelectedModelId(modelId);
    setProcessingMode('onnx');
  }, []);

  const deleteModel = useCallback(async (modelId: string) => {
    await modelManager.deleteModel(modelId);
    const updated = await modelManager.listModels();
    setModels(updated);
    if (selectedModelId === modelId) {
      setSelectedModelId(null);
      setProcessingMode('dsp');
    }
  }, [selectedModelId]);

  return {
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
    clearError: () => setError(null),
    isActive: audioState.isRecording,
  };
}
