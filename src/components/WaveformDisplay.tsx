import React, { useEffect, useRef } from 'react';
import { WaveformData } from '../types';
import './WaveformDisplay.css';

interface Props {
  waveform: WaveformData | null;
  isActive: boolean;
}

export const WaveformDisplay: React.FC<Props> = ({ waveform, isActive }) => {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const animRef = useRef<number>(0);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    const draw = () => {
      const { width, height } = canvas;
      ctx.clearRect(0, 0, width, height);

      const gradient = ctx.createLinearGradient(0, 0, width, 0);
      gradient.addColorStop(0, '#6c5ce7');
      gradient.addColorStop(0.5, '#a29bfe');
      gradient.addColorStop(1, '#fd79a8');

      ctx.strokeStyle = gradient;
      ctx.lineWidth = 2;
      ctx.lineJoin = 'round';

      if (waveform && isActive) {
        const { timeData } = waveform;
        ctx.beginPath();
        const sliceWidth = width / timeData.length;

        let x = 0;
        for (let i = 0; i < timeData.length; i++) {
          const v = timeData[i] * 0.8;
          const y = (v * height) / 2 + height / 2;

          if (i === 0) {
            ctx.moveTo(x, y);
          } else {
            ctx.lineTo(x, y);
          }
          x += sliceWidth;
        }
        ctx.stroke();

        ctx.strokeStyle = 'rgba(253, 121, 168, 0.3)';
        ctx.lineWidth = 1;
        ctx.beginPath();
        for (let i = 0; i < timeData.length; i++) {
          const v = -timeData[i] * 0.8;
          const y = (v * height) / 2 + height / 2;
          if (i === 0) ctx.moveTo(i * sliceWidth, y);
          else ctx.lineTo(i * sliceWidth, y);
        }
        ctx.stroke();
      } else {
        ctx.strokeStyle = 'rgba(255, 255, 255, 0.1)';
        ctx.lineWidth = 1;
        ctx.beginPath();
        ctx.moveTo(0, height / 2);
        ctx.lineTo(width, height / 2);
        ctx.stroke();
      }

      animRef.current = requestAnimationFrame(draw);
    };

    draw();
    return () => cancelAnimationFrame(animRef.current);
  }, [waveform, isActive]);

  return (
    <div className="waveform-container">
      <canvas ref={canvasRef} className="waveform-canvas" />
    </div>
  );
};
