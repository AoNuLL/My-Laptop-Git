import React, { useState } from 'react';
import './ModelDownload.css';

interface ModelLink {
  name: string;
  url: string;
  size: string;
  description: string;
}

const MODEL_LINKS: ModelLink[] = [
  {
    name: 'RVC v2 - 通用女声',
    url: 'https://huggingface.co/lj1995/VoiceConversionWebUI/resolve/main/pretrained_v2/f0G40k.pth',
    size: '~380MB',
    description: 'RVC v2 预训练基模型，40kHz，含F0',
  },
  {
    name: 'RVC v2 - 通用男声',
    url: 'https://huggingface.co/lj1995/VoiceConversionWebUI/resolve/main/pretrained_v2/f0D40k.pth',
    size: '~380MB',
    description: 'RVC v2 预训练基模型，40kHz，含F0',
  },
  {
    name: 'RVC v1 - 32kHz基模型',
    url: 'https://huggingface.co/lj1995/VoiceConversionWebUI/resolve/main/pretrained/32k.pth',
    size: '~320MB',
    description: 'RVC v1 预训练基模型，32kHz',
  },
  {
    name: 'RVC v1 - 40kHz基模型',
    url: 'https://huggingface.co/lj1995/VoiceConversionWebUI/resolve/main/pretrained/40k.pth',
    size: '~330MB',
    description: 'RVC v1 预训练基模型，40kHz',
  },
  {
    name: 'RVC v1 - 48kHz基模型',
    url: 'https://huggingface.co/lj1995/VoiceConversionWebUI/resolve/main/pretrained/48k.pth',
    size: '~340MB',
    description: 'RVC v1 预训练基模型，48kHz',
  },
];

export const ModelDownload: React.FC = () => {
  const [expanded, setExpanded] = useState(false);

  return (
    <div className="model-download">
      <button
        className="download-toggle"
        onClick={() => setExpanded(!expanded)}
      >
        <span>{expanded ? '收起' : '展开'}</span>
        <span className="download-icon">{expanded ? '\u25B2' : '\u25BC'}</span>
      </button>

      {expanded && (
        <div className="download-list">
          {MODEL_LINKS.map((model) => (
            <a
              key={model.name}
              href={model.url}
              target="_blank"
              rel="noopener noreferrer"
              className="download-item"
            >
              <div className="download-item-info">
                <span className="download-item-name">{model.name}</span>
                <span className="download-item-desc">{model.description}</span>
                <span className="download-item-size">{model.size}</span>
              </div>
              <svg className="download-arrow" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M7 17l9.2-9.2M17 17V7H7" />
              </svg>
            </a>
          ))}
          <p className="download-note">
            ONNX格式转换：使用RVC官方项目中的 export_onnx.py 将.pth转为.onnx后再导入
          </p>
        </div>
      )}
    </div>
  );
};
