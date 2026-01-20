import { fetcher } from './fetcher';
import { CardSet } from '@/types';

export const SetService = {
	async getAll(): Promise<CardSet[]> {
		const response = await fetcher('/sets', 'GET');
		if (!response.ok) {
			throw new Error('Failed to fetch sets');
		}
		return await response.json();
	},

	async getOne(id: string): Promise<CardSet> {
		const response = await fetcher(`/sets/${id}`, 'GET');
		if (!response.ok) {
			throw new Error('Failed to fetch set');
		}
		return await response.json();
	},

	async create(data: { name: string; description?: string }): Promise<CardSet> {
		const response = await fetcher('/sets', 'POST', {
			body: JSON.stringify(data),
		});
		if (!response.ok) {
			throw new Error('Failed to create set');
		}
		return await response.json();
	},

	async update(id: string, data: { name?: string; description?: string }): Promise<any> {
		const response = await fetcher(`/sets/${id}`, 'PATCH', {
			body: JSON.stringify(data),
		});
		if (!response.ok) {
			throw new Error('Failed to update set');
		}
		return await response.json();
	},

	async delete(id: string): Promise<void> {
		const response = await fetcher(`/sets/${id}`, 'DELETE');
		if (!response.ok) {
			throw new Error('Failed to delete set');
		}
	},
};

