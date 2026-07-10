import React, { useRef } from 'react';
import { RVCModelMeta } from '../types';
import './ModelImporter.css';

interface Props {
  models: RVCModelMeta[];
  selectedModelId: string | null;
  isImporting: boolean;
  importProgress: number;
  onImport: (file: File) => void;
  onSelect: (modelId: string) => void;
  onDelete: (modelId: string) => void;
}

export const ModelImporter: React.FC<Props> = ({
  models,
  selectedModelId,
  isImporting,
  importProgress,
  onImport,
  onSelect,
  onDelete,
}) => {
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file && (file.name.endsWith('.onnx') || file.name.endsWith('.pth'))) {
      onImport(file);
    }
    if (fileInputRef.current) fileInputRef.current.value = '';
  };

  const formatSize = (bytes: number): string => {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
  };

  return (
    <div className="model-importer">
      <div className="model-section-header">
        <span className="model-section-title">RVC 模型</span>
        <span className="processing-mode-badge">
          {selectedModelId ? 'ONNX 推理' : 'DSP 模拟'}
        </span>
      </div>

      {models.length > 0 && (
        <div className="model-list">
          {models.map((model) => (
            <div
              key={model.id}
              className={`model-item ${selectedModelId === model.id ? 'active' : ''}`}
              onClick={() => onSelect(model.id)}
            >
              <div className="model-item-info">
                <span className="model-item-name">{model.name}</span>
                <span className="model-item-meta">
                  {formatSize(model.fileSize)} · {model.sampleRate}Hz · hop={model.hopSize}
                </span>
                {model.status === 'error' && (
                  <span className="model-error">{model.errorMessage}</span>
                )}
              </div>
              <button
                className="model-delete-btn"
                onClick={(e) => {
                  e.stopPropagation();
                  onDelete(model.id);
                }}
              >
                x
              </button>
            </div>
          ))}
        </div>
      )}

      <div className="model-upload-area">
        {isImporting ? (
          <div className="import-progress">
            <div className="progress-bar-bg">
              <div className="progress-bar-fill" style={{ width: `${importProgress}%` }} />
            </div>
            <span className="progress-text">导入中... {Math.round(importProgress)}%</span>
          </div>
        ) : (
          <button
            className="upload-btn"
            onClick={() => fileInputRef.current?.click()}
          >
            + 导入 ONNX 模型
          </button>
        )}
        <input
          ref={fileInputRef}
          type="file"
          accept=".onnx,.pth"
          onChange={handleFileChange}
          style={{ display: 'none' }}
        />
      </div>

      <p className="model-hint">
        支持RVC v1/v2导出的ONNX格式模型文件。导入后可使用真实RVC推理替代DSP模拟。
      </p>
    </div>
  );
};
