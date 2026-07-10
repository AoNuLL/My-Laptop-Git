import { RVCModelMeta } from '../types';

const DB_NAME = 'rvc-models';
const DB_VERSION = 1;
const STORE_META = 'models_meta';
const STORE_DATA = 'models_data';

export class ModelManager {
  private db: IDBDatabase | null = null;

  async init(): Promise<void> {
    return new Promise((resolve, reject) => {
      const req = indexedDB.open(DB_NAME, DB_VERSION);
      req.onupgradeneeded = () => {
        const db = req.result;
        if (!db.objectStoreNames.contains(STORE_META)) {
          db.createObjectStore(STORE_META, { keyPath: 'id' });
        }
        if (!db.objectStoreNames.contains(STORE_DATA)) {
          db.createObjectStore(STORE_DATA, { keyPath: 'id' });
        }
      };
      req.onsuccess = () => {
        this.db = req.result;
        resolve();
      };
      req.onerror = () => reject(req.error);
    });
  }

  async importModel(file: File, onProgress: (pct: number) => void): Promise<RVCModelMeta> {
    if (!this.db) await this.init();

    const id = `model_${Date.now()}`;
    const arrayBuffer = await file.arrayBuffer();

    onProgress(30);

    const session = await this.loadOnnxForInspect(arrayBuffer);

    onProgress(60);

    const inputs = session.inputNames;
    const outputs = session.outputNames;

    const hasEmbedInput = inputs.some((n: string) =>
      n.toLowerCase().includes('embed') || n.toLowerCase().includes('hubert')
    );

    let f0InputKey = inputs.find((n: string) =>
      n.toLowerCase().includes('f0') || n.toLowerCase().includes('pitch')
    ) || inputs[0];

    const embedInputKey = inputs.find((n: string) =>
      n.toLowerCase().includes('embed') || n.toLowerCase().includes('hubert')
    ) || '';

    const audioOutputKey = outputs.find((n: string) =>
      n.toLowerCase().includes('audio') || n.toLowerCase().includes('output')
    ) || outputs[0];

    const hopSize = this.detectHopSize(session);

    const meta: RVCModelMeta = {
      id,
      name: file.name.replace(/\.(onnx|pth)$/i, ''),
      fileName: file.name,
      fileSize: file.size,
      importedAt: Date.now(),
      sampleRate: 40000,
      hopSize,
      f0InputKey,
      embedInputKey,
      audioOutputKey,
      hasEmbedInput,
      inputNames: [...inputs],
      outputNames: [...outputs],
      status: 'loading',
    };

    onProgress(80);

    await this.saveModel(id, arrayBuffer, meta);

    meta.status = 'ready';
    await this.updateMeta(meta);

    onProgress(100);
    return meta;
  }

  private async loadOnnxForInspect(buffer: ArrayBuffer): Promise<{
    inputNames: string[];
    outputNames: string[];
    getInputShape: (name: string) => number[];
  }> {
    const ort = await import('onnxruntime-web');
    const session = await ort.InferenceSession.create(buffer, {
      executionProviders: ['wasm'],
      graphOptimizationLevel: 'basic',
    });

    const inputNames = session.inputNames;
    const outputNames = session.outputNames;
    const inputMeta: Record<string, number[]> = {};

    for (const name of inputNames) {
      try {
        const meta = (session as any).inputMeta?.[name];
        if (meta?.dims) {
          inputMeta[name] = meta.dims;
        }
      } catch {
        inputMeta[name] = [1, 1, 256];
      }
    }

    return {
      inputNames: [...inputNames],
      outputNames: [...outputNames],
      getInputShape: (name: string) => inputMeta[name] || [1, 1, 256],
    };
  }

  private detectHopSize(session: any): number {
    try {
      const metaKeys = Object.keys(session?.inputMeta || {});
      for (const key of metaKeys) {
        const dims = session.inputMeta[key]?.dims || [];
        if (dims.length === 3 && dims[2] === 256) return 160;
        if (dims.length === 3 && dims[2] === 512) return 320;
      }
    } catch { /* fallback */ }
    return 160;
  }

  private async saveModel(id: string, data: ArrayBuffer, meta: RVCModelMeta): Promise<void> {
    return new Promise((resolve, reject) => {
      if (!this.db) { reject(new Error('DB not init')); return; }
      const tx = this.db.transaction([STORE_META, STORE_DATA], 'readwrite');
      tx.objectStore(STORE_META).put(meta);
      tx.objectStore(STORE_DATA).put({ id, data });
      tx.oncomplete = () => resolve();
      tx.onerror = () => reject(tx.error);
    });
  }

  async getModelData(id: string): Promise<ArrayBuffer | null> {
    return new Promise((resolve, reject) => {
      if (!this.db) { reject(new Error('DB not init')); return; }
      const tx = this.db.transaction(STORE_DATA, 'readonly');
      const req = tx.objectStore(STORE_DATA).get(id);
      req.onsuccess = () => resolve(req.result?.data || null);
      req.onerror = () => reject(req.error);
    });
  }

  async listModels(): Promise<RVCModelMeta[]> {
    return new Promise((resolve, reject) => {
      if (!this.db) { reject(new Error('DB not init')); return; }
      const tx = this.db.transaction(STORE_META, 'readonly');
      const req = tx.objectStore(STORE_META).getAll();
      req.onsuccess = () => resolve(req.result || []);
      req.onerror = () => reject(req.error);
    });
  }

  async getMeta(id: string): Promise<RVCModelMeta | null> {
    return new Promise((resolve, reject) => {
      if (!this.db) { reject(new Error('DB not init')); return; }
      const tx = this.db.transaction(STORE_META, 'readonly');
      const req = tx.objectStore(STORE_META).get(id);
      req.onsuccess = () => resolve(req.result || null);
      req.onerror = () => reject(req.error);
    });
  }

  private async updateMeta(meta: RVCModelMeta): Promise<void> {
    return new Promise((resolve, reject) => {
      if (!this.db) { reject(new Error('DB not init')); return; }
      const tx = this.db.transaction(STORE_META, 'readwrite');
      tx.objectStore(STORE_META).put(meta);
      tx.oncomplete = () => resolve();
      tx.onerror = () => reject(tx.error);
    });
  }

  async deleteModel(id: string): Promise<void> {
    return new Promise((resolve, reject) => {
      if (!this.db) { reject(new Error('DB not init')); return; }
      const tx = this.db.transaction([STORE_META, STORE_DATA], 'readwrite');
      tx.objectStore(STORE_META).delete(id);
      tx.objectStore(STORE_DATA).delete(id);
      tx.oncomplete = () => resolve();
      tx.onerror = () => reject(tx.error);
    });
  }
}

export const modelManager = new ModelManager();
