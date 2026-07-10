import React from 'react';
import './RecordButton.css';

interface Props {
  isActive: boolean;
  onToggle: () => void;
  inputLevel: number;
  outputLevel: number;
}

export const RecordButton: React.FC<Props> = ({ isActive, onToggle, inputLevel, outputLevel }) => {
  return (
    <div className="record-section">
      <div className="level-meters">
        <div className="meter-row">
          <span className="meter-label">输入</span>
          <div className="meter-bar-bg">
            <div
              className="meter-bar-fill input-fill"
              style={{ width: `${Math.min(inputLevel * 100 * 5, 100)}%` }}
            />
          </div>
        </div>
        <div className="meter-row">
          <span className="meter-label">输出</span>
          <div className="meter-bar-bg">
            <div
              className="meter-bar-fill output-fill"
              style={{ width: `${Math.min(outputLevel * 100 * 5, 100)}%` }}
            />
          </div>
        </div>
      </div>

      <button
        className={`record-button ${isActive ? 'active' : ''}`}
        onClick={onToggle}
      >
        <div className="record-button-inner">
          {isActive ? (
            <div className="stop-icon" />
          ) : (
            <div className="mic-icon">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M12 1a3 3 0 0 0-3 3v8a3 3 0 0 0 6 0V4a3 3 0 0 0-3-3z" />
                <path d="M19 10v2a7 7 0 0 1-14 0v-2" />
                <line x1="12" y1="19" x2="12" y2="23" />
                <line x1="8" y1="23" x2="16" y2="23" />
              </svg>
            </div>
          )}
        </div>
        <span className="record-label">
          {isActive ? '停止变声' : '开始变声'}
        </span>
      </button>
    </div>
  );
};
