import React from 'react';
import './FloatingToggle.css';

interface Props {
  isActive: boolean;
  onToggle: () => void;
  processingMode: string;
}

export const FloatingToggle: React.FC<Props> = ({ isActive, onToggle, processingMode }) => {
  return (
    <div className={`floating-toggle ${isActive ? 'active' : ''}`}>
      <button
        className="float-btn"
        onClick={onToggle}
        aria-label={isActive ? '停止变声' : '开始变声'}
      >
        <div className="float-btn-ring" />
        <div className="float-btn-inner">
          {isActive ? (
            <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
              <rect x="6" y="6" width="12" height="12" rx="2" />
            </svg>
          ) : (
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M12 1a3 3 0 0 0-3 3v8a3 3 0 0 0 6 0V4a3 3 0 0 0-3-3z" />
              <path d="M19 10v2a7 7 0 0 1-14 0v-2" />
              <line x1="12" y1="19" x2="12" y2="23" />
              <line x1="8" y1="23" x2="16" y2="23" />
            </svg>
          )}
        </div>
      </button>
      <span className="float-label">{isActive ? '运行中' : '已停止'}</span>
      {isActive && <span className="float-mode">{processingMode}</span>}
    </div>
  );
};
