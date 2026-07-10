import React from 'react';
import { VoicePreset } from '../types';
import './VoiceSelector.css';

interface Props {
  presets: VoicePreset[];
  selectedId: string;
  onSelect: (preset: VoicePreset) => void;
}

const ICON_MAP: Record<string, string> = {
  mic: '\u{1F399}',
  male: '\u{1F468}',
  female: '\u{1F469}',
  child: '\u{1F476}',
  person: '\u{1F474}',
  robot: '\u{1F916}',
  pet: '\u{1F439}',
  monster: '\u{1F47E}',
};

export const VoiceSelector: React.FC<Props> = ({ presets, selectedId, onSelect }) => {
  return (
    <div className="voice-selector">
      <div className="voice-selector-title">选择音色</div>
      <div className="voice-grid">
        {presets.map((preset) => (
          <button
            key={preset.id}
            className={`voice-card ${selectedId === preset.id ? 'active' : ''}`}
            onClick={() => onSelect(preset)}
          >
            <span className="voice-icon">{ICON_MAP[preset.icon] || '\u{1F399}'}</span>
            <span className="voice-name">{preset.name}</span>
          </button>
        ))}
      </div>
    </div>
  );
};
