export interface DataSource {
    id: number;
    name: string;
    type: 'DATABASE' | 'HTTP' | 'FILE';
    config: string;
    createdAt: string;
    updatedAt: string;
}

export interface CompareTask {
    id: number;
    name: string;
    source: DataSource;
    target: DataSource;
    primaryKeys: string;
    compareFields: string;
    triggerType: 'MANUAL' | 'SCHEDULED';
    cronExpression?: string;
    createdAt: string;
    updatedAt: string;
}

export interface CompareResult {
    id: number;
    task: CompareTask;
    executionTime: string;
    status: 'SUCCESS' | 'FAILED' | 'IN_PROGRESS';
    diffCount: number;
    details: string;
    createdAt: string;
}

export interface CompareProgress {
    taskId: number;
    totalRecords: number;
    processedRecords: number;
    diffCount: number;
    currentStep: string;
    progressPercentage: number;
} 