import React from 'react';
import { RVCConfig } from '../types';
import './Controls.css';

interface Props {
  config: RVCConfig;
  onChange: (config: Partial<RVCConfig>) => void;
}

export const Controls: React.FC<Props> = ({ config, onChange }) => {
  return (
    <div className="controls">
      <div className="control-group">
        <div className="control-header">
          <span className="control-label">音高偏移</span>
          <span className="control-value">{config.pitchShift > 0 ? '+' : ''}{config.pitchShift.toFixed(1)} 半音</span>
        </div>
        <input
          type="range"
          min="-12"
          max="12"
          step="0.5"
          value={config.pitchShift}
          onChange={(e) => onChange({ pitchShift: parseFloat(e.target.value) })}
          className="slider pitch-slider"
        />
      </div>

      <div className="control-group">
        <div className="control-header">
          <span className="control-label">共振峰</span>
          <span className="control-value">{config.formantShift > 0 ? '+' : ''}{config.formantShift.toFixed(1)}</span>
        </div>
        <input
          type="range"
          min="-10"
          max="10"
          step="0.5"
          value={config.formantShift}
          onChange={(e) => onChange({ formantShift: parseFloat(e.target.value) })}
          className="slider formant-slider"
        />
      </div>

      <div className="control-group">
        <div className="control-header">
          <span className="control-label">输出增益</span>
          <span className="control-value">{Math.round(config.gain * 100)}%</span>
        </div>
        <input
          type="range"
          min="0"
          max="2"
          step="0.05"
          value={config.gain}
          onChange={(e) => onChange({ gain: parseFloat(e.target.value) })}
          className="slider gain-slider"
        />
      </div>

      <div className="control-group">
        <div className="control-header">
          <span className="control-label">混响</span>
          <span className="control-value">{Math.round(config.reverb * 100)}%</span>
        </div>
        <input
          type="range"
          min="0"
          max="1"
          step="0.05"
          value={config.reverb}
          onChange={(e) => onChange({ reverb: parseFloat(e.target.value) })}
          className="slider reverb-slider"
        />
      </div>

      <div className="control-group">
        <div className="control-header">
          <span className="control-label">噪声门限</span>
          <span className="control-value">{Math.round(config.noiseGate * 1000)}</span>
        </div>
        <input
          type="range"
          min="0"
          max="0.1"
          step="0.005"
          value={config.noiseGate}
          onChange={(e) => onChange({ noiseGate: parseFloat(e.target.value) })}
          className="slider noise-slider"
        />
      </div>
    </div>
  );
};
